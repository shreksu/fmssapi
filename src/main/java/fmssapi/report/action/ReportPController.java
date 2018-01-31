package fmssapi.report.action;

import fmssapi.curtain.model.Curtain;
import fmssapi.curtain.service.CurtainBalanceService;
import fmssapi.curtain.service.CurtainService;
import fmssapi.report.model.ReportP;
import fmssapi.report.model.ReportPElement;
import fmssapi.report.service.ReportPElementService;
import fmssapi.report.service.ReportPService;
import fmssapi.util.ExcelUtil;
import fmssapi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author suyuanyang
 * @create 2018-01-30 上午9:10
 */
@RestController
@RequestMapping("/reportp")
public class ReportPController {

    @Autowired
    private ReportPService reportPService;
    @Autowired
    private ReportPElementService reportPElementService;
    @Autowired
    private CurtainService curtainService;
    @Autowired
    private CurtainBalanceService curtainBalanceService;
    /**
     * 转结账，将5类科目转为0
     * @return
     */
    @RequestMapping(value = "createReport", method = RequestMethod.GET)
    public Map<String,String> createReport(Long curtainId,String month){
        Map<String,String> result = new HashMap<>();
        Curtain curtain = curtainService.findById(curtainId);
        //检验月份
        String thisMonth = StringUtil.getMonth(new Date());
        String startMonth = StringUtil.getMonth(curtain.getStartMonth());//帐套开启月份
        if(month.compareTo(thisMonth)>0){
            result.put("message","不可以对之后的月份生成报表");
            return result;
        }
        if(month.compareTo(startMonth)<=0){
            result.put("message","不可以对帐套开启之前的月份生成报表");
            return result;
        }
        //检验状态
        String status = curtainBalanceService.getStatus(curtainId, month);
        if(!"G".equals(status)){
            result.put("message","该月份没有月末结账，不可以生成报表");
            return result;
        }
        reportPService.createReport(curtainId,month);
        result.put("message", "1");
        return result;
    }


    @RequestMapping(value = "getReports", method = RequestMethod.GET)
    public List<ReportPElement> getReports(Long curtainId,String month){
        List<ReportP> reportPList = reportPService.findAll(curtainId,month);
        Curtain curtain = curtainService.findById(curtainId);
        List<ReportPElement> reportPElementList = reportPElementService.findAllByComFlag(curtain.getComFlag());
        reportPElementService.assemblingList(reportPList, reportPElementList);
        return reportPElementList;
    }

    @RequestMapping(value = "exportExcel", method = RequestMethod.POST)
    public void exportExcel(@RequestBody Map<String,String> map,HttpServletResponse response){
        String month = map.get("month");
        //String curtainIdStr = request.getParameter("curtainId");
        Long curtainId = Long.valueOf(map.get("curtainId"));
        Curtain curtain = curtainService.findById(curtainId);
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = month+"-"+curtain.getName()+"-"+"利润表.xlsx";
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename="
                    + URLEncoder.encode(fileName, "UTF-8"));
            String[] headers = {"项目", "行次", "本月数", "本年累计"};
            List<List<String>> dataList = new ArrayList<>();
            List<ReportP> reportPList = reportPService.findAll(curtainId, month);
            List<ReportPElement> reportPElementList = reportPElementService.findAllByComFlag(curtain.getComFlag());
            reportPElementService.assemblingList(reportPList,reportPElementList);
            for(ReportPElement ele : reportPElementList){
                List<String> list = new ArrayList<>();
                list.add(ele.getName()==null?"":ele.getName());
                list.add(ele.getRow()==null?"":String.valueOf(ele.getRow()));
                list.add(StringUtil.formatNumber(ele.getMonthNum()));
                list.add(StringUtil.formatNumber(ele.getYearNum()));

                dataList.add(list);
            }
            ExcelUtil.exportExcel("利润表", headers, dataList, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream!=null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //删除报表
    @RequestMapping(value = "removeReports", method = RequestMethod.GET)
    public Map<String,String> removeReports(Long curtainId,String month){
        reportPService.removeAll(curtainId,month);
        Map<String,String> result = new HashMap<>();
        result.put("errno","1");
        return result;
    }
}
