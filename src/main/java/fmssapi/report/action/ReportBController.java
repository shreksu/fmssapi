package fmssapi.report.action;

import fmssapi.curtain.model.Curtain;
import fmssapi.curtain.service.CurtainBalanceService;
import fmssapi.curtain.service.CurtainService;
import fmssapi.report.model.ReportB;
import fmssapi.report.model.ReportBElement;
import fmssapi.report.model.ReportBTable;
import fmssapi.report.service.ReportBElementService;
import fmssapi.report.service.ReportBService;
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
 * @create 2018-01-27 上午10:12
 */
@RestController
@RequestMapping("/reportb")
public class ReportBController {

    @Autowired
    private ReportBService reportBService;
    @Autowired
    private ReportBElementService reportBElementService;
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
        reportBService.createReport(curtainId,month);
        result.put("message", "1");
        return result;
    }


    @RequestMapping(value = "getReports", method = RequestMethod.GET)
    public List<ReportBTable> getReports(Long curtainId,String month){
        List<ReportB> reportBList = reportBService.findAll(curtainId,month);
        Curtain curtain = curtainService.findById(curtainId);
        List<ReportBElement> reportBElementList = reportBElementService.findAllByComFlag(curtain.getComFlag());
        return reportBService.getReportBTable(reportBElementList,reportBList);
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
        String fileName = month+"-"+curtain.getName()+"-"+"资产负债表.xlsx";
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename="
                    + URLEncoder.encode(fileName, "UTF-8"));
            String[] headers = {"资产", "行次", "年初余额", "期末余额", "负债和所有者权益",
                    "行次", "年初余额", "期末余额",};
            List<List<String>> dataList = new ArrayList<>();
            List<ReportB> reportBList = reportBService.findAll(curtainId, month);
            List<ReportBElement> reportBElementList = reportBElementService.findAllByComFlag(curtain.getComFlag());
            List<ReportBTable> reportBTableList = reportBService.getReportBTable(reportBElementList, reportBList);
            for(ReportBTable rbt : reportBTableList){
                List<String> list = new ArrayList<>();
                list.add(rbt.getName1()==null?"":rbt.getName1());
                list.add(rbt.getRow1()==null?"":String.valueOf(rbt.getRow1()));
                list.add(StringUtil.formatNumber(rbt.getInitNum1()));
                list.add(StringUtil.formatNumber(rbt.getLastNum1()));

                list.add(rbt.getName2()==null?"":rbt.getName2());
                list.add(rbt.getRow2()==null?"":String.valueOf(rbt.getRow2()));
                list.add(StringUtil.formatNumber(rbt.getInitNum2()));
                list.add(StringUtil.formatNumber(rbt.getLastNum2()));

                dataList.add(list);
            }
            ExcelUtil.exportExcel("资产负债表", headers, dataList, outputStream);
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
        reportBService.removeAll(curtainId,month);
        Map<String,String> result = new HashMap<>();
        result.put("errno","1");
        return result;
    }



}
