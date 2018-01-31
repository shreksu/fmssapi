package fmssapi.curtain.action;

import fmssapi.curtain.model.Curtain;
import fmssapi.curtain.model.CurtainBalance;
import fmssapi.curtain.service.CurtainBalanceService;
import fmssapi.curtain.service.CurtainService;
import fmssapi.subject.model.Subject;
import fmssapi.subject.service.SubjectService;
import fmssapi.util.ExcelUtil;
import fmssapi.util.StringUtil;
import fmssapi.voucher.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
 * @create 2018-01-20 下午12:24
 */
@RestController
@RequestMapping("/curtainbalance")
public class CurtainBalanceController {

    @Autowired
    private CurtainService curtainService;

    @Autowired
    private CurtainBalanceService curtainBalanceService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private VoucherService voucherService;


    /**
     * 获得帐套的起初余额
     * @param curtainId
     * @return
     */
    @RequestMapping(value = "getInitCurtainBalance", method = RequestMethod.GET)
    public List<Subject> getInitCurtainBalance(Long curtainId){
        Curtain curtain = curtainService.findById(curtainId);
        String month = StringUtil.getMonth(curtain.getStartMonth());
        List<CurtainBalance> curtainBalanceList = curtainBalanceService.getCurtainBalances(curtainId, month);
        List<Subject> subjectList = subjectService.findSimpleAllByCurtainId(curtainId);
        curtainBalanceService.loadSubject(subjectList,curtainBalanceList);
        return subjectList;
    }

    /**
     * 保存修改起初余额
     * @param map
     * @return
     */
    @RequestMapping(value = "updateCurtainBalance", method = RequestMethod.POST)
    public Map<String,String> updateCurtainBalance(@RequestBody Map map){
        Long curtainId = Long.valueOf((String) map.get("curtainId"));
        Curtain curtain = curtainService.findById(curtainId);
        String month = StringUtil.getMonth(curtain.getStartMonth());
        List<Map<String,String>> list = (List<Map<String, String>>) map.get("subs");
        for(Map<String,String> subMap : list){
            String subjectCode = subMap.get("code");
            String amountStr = subMap.get("amount");
            if(StringUtils.isEmpty(amountStr) || Double.valueOf(amountStr)==0){
                curtainBalanceService.delete(curtainId, month, subjectCode);
            }else{
                Double amount =  Double.valueOf(subMap.get("amount"));
                curtainBalanceService.update(curtainId, month, subjectCode, amount);
            }
        }
        Map<String,String> result = new HashMap<>();
        result.put("errno", "0");
        return result;
    }

    /**
     * 试算期初余额是否相等
     * @param curtainId
     * @return
     */
    @RequestMapping(value = "checkSum", method = RequestMethod.GET)
    public Map<String,String> checkSum(Long curtainId,String month){
        Curtain curtain = curtainService.findById(curtainId);
        if(StringUtils.isEmpty(month)) {
            month = StringUtil.getMonth(curtain.getStartMonth());
        }
        return curtainBalanceService.checkSum(curtainId,month);
    }

    /**
     * 获得帐套的余额表
     * @param curtainId
     * @return
     */
    @RequestMapping(value = "getSubalances", method = RequestMethod.GET)
    public List<Subject> getSubalances(Long curtainId,String month){
        List<CurtainBalance> curtainBalanceList = curtainBalanceService.getCurtainBalances(curtainId, month);
        List<CurtainBalance> curtainBalancePreList = curtainBalanceService.getCurtainBalances(curtainId, StringUtil.getPreMonth(month));
        List<Subject> subjectList = subjectService.findSimpleAllByCurtainId(curtainId);
        curtainBalanceService.loadMonthSubject(subjectList, curtainBalancePreList, curtainBalanceList);
        //计算合计
        curtainBalanceService.calculateTotal(subjectList);
        return subjectList;
    }


    /**
     * 导出余额表
     * @return
     */
    @RequestMapping(value = "exportBalanceExcel", method = RequestMethod.POST)
    public void exportBalanceExcel(@RequestBody Map<String,String> map,HttpServletResponse response){
        Long curtainId = Long.valueOf(map.get("curtainId"));
        String month = map.get("month");
        List<CurtainBalance> curtainBalanceList = curtainBalanceService.getCurtainBalances(curtainId, month);
        List<CurtainBalance> curtainBalancePreList = curtainBalanceService.getCurtainBalances(curtainId, StringUtil.getPreMonth(month));
        List<Subject> subjectList = subjectService.findSimpleAllByCurtainId(curtainId);
        curtainBalanceService.loadMonthSubject(subjectList, curtainBalancePreList, curtainBalanceList);
        //计算合计
        curtainBalanceService.calculateTotal(subjectList);

        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Curtain curtain = curtainService.findById(curtainId);
        String fileName = month+"-"+curtain.getName()+"-"+"余额表.xlsx";
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename="
                    + URLEncoder.encode(fileName, "UTF-8"));
            String[] headers = {"科目编码", "科目名称", "借方(期初)", "贷方(期初)", "借方(本月)",
                    "借方(本月)", "借方(期末)", "借方(期末)"};
            List<List<String>> dataList = new ArrayList<>();
            for(Subject sub : subjectList){
                CurtainBalance curtainBalance = sub.getCurtainBalance();
                if(curtainBalance == null){
                    curtainBalance = new CurtainBalance();
                }
                List<String> list = new ArrayList<>();
                list.add(sub.getCode());
                list.add(sub.getName());
                list.add(String.valueOf(curtainBalance.getOriDebit() == null ? "" : curtainBalance.getOriDebit()));
                list.add(String.valueOf(curtainBalance.getOriCredit() == null ? "" : curtainBalance.getOriCredit()));

                list.add(String.valueOf(curtainBalance.getDebitAt()==null?"":curtainBalance.getDebitAt()));
                list.add(String.valueOf(curtainBalance.getCreditAt() == null ? "" : curtainBalance.getCreditAt()));

                list.add(String.valueOf(curtainBalance.getDebit()==null?"":curtainBalance.getDebit()));
                list.add(String.valueOf(curtainBalance.getCredit() == null ? "" : curtainBalance.getCredit()));

                dataList.add(list);
            }
            ExcelUtil.exportExcel("余额表", headers, dataList, outputStream);
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

    /**
     * 登账
     * 1.不能登账下个月份的账
     * 2.如果某月份已经记账，不能执行登账功能
     * @param curtainId
     * @return
     */
    @RequestMapping(value = "chargeUp", method = RequestMethod.GET)
    public Map<String,String> chargeUp(Long curtainId,String month,String type){
        Map<String,String> result = new HashMap<>();
        Curtain curtain = curtainService.findById(curtainId);
        //检验月份
        String thisMonth = StringUtil.getMonth(new Date());
        String startMonth = StringUtil.getMonth(curtain.getStartMonth());//帐套开启月份
        if(month.compareTo(thisMonth)>0){
            result.put("message","不可以对之后的月份登账");
            return result;
        }
        if(month.compareTo(startMonth)<=0){
            result.put("message","不可以对帐套开启之前的月份登账");
            return result;
        }
        //检验状态
        String status = curtainBalanceService.getStatus(curtainId, month);
        if("G".equals(status)){
            result.put("message","本月份已经结账，不需要再登账");
            return result;
        }
        // 月末结账
        if("month".equals(type)){
            //获得自动结转的状态，查询是否已结转
            boolean hasZhuan = curtainBalanceService.getHasZhuan(curtainId,month);
            if(!hasZhuan){
                result.put("message","还未进行自动结转，请先结转再结账！");
                return result;
            }
        }
        //删除原来的登账记录
        curtainBalanceService.deleteBalances(curtainId, month);
        curtainBalanceService.createBalances(curtainId, month, type);
        result.put("message", "1");
        return result;
    }


    /**
     * 转结账，将5类科目转为0
     * @return
     */
    @RequestMapping(value = "autoSettle", method = RequestMethod.GET)
    public Map<String,String> autoSettle(Long curtainId,String month){
        Map<String,String> result = new HashMap<>();
        Curtain curtain = curtainService.findById(curtainId);
        //检验月份
        String thisMonth = StringUtil.getMonth(new Date());
        String startMonth = StringUtil.getMonth(curtain.getStartMonth());//帐套开启月份
        if(month.compareTo(thisMonth)>0){
            result.put("message","不可以对之后的月份结转");
            return result;
        }
        if(month.compareTo(startMonth)<=0){
            result.put("message","不可以对帐套开启之前的月份结转");
            return result;
        }
        //检验状态
        String status = curtainBalanceService.getStatus(curtainId, month);
        if("G".equals(status)){
            result.put("message","本月份已经结账，不需要再结转");
            return result;
        }
        //获得自动结转的状态，查询是否已结转
        boolean hasZhuan = curtainBalanceService.getHasZhuan(curtainId, month);
        //查询未审核的凭证
        boolean hasAudit = !voucherService.getNotAudit(curtainId,month);
        if(hasZhuan){
            result.put("message","本月份已经自动结转，请先进行反结转");
            return result;
        }
        if(!hasAudit){
            result.put("message","本月份还有未审核凭证，请先审核再结转");
            return result;
        }
        voucherService.autoSettle(curtainId, month);
        result.put("message", "1");
        return result;
    }

    /**
     *
     * @param curtainId
     * @param month 反结转
     * @return
     */
    @RequestMapping(value = "autoSettleCancel", method = RequestMethod.GET)
    public Map<String,String> autoSettleCancel(Long curtainId,String month){
        //获得月末结账状态，查询时候月末结账
        boolean hasJie = curtainBalanceService.getHasJie(curtainId, month);
        //获得自动结转的状态，查询是否已结转
        boolean hasZhuan = curtainBalanceService.getHasZhuan(curtainId,month);
        Map<String,String> result = new HashMap<>();
        if(!hasJie && hasZhuan){
            curtainBalanceService.autoSettleCancel(curtainId,month);
        }else if(hasJie){
            result.put("message", "已经结账不可以反结转，请先进行反结账！");
            return result;
        }else if(!hasZhuan){
            result.put("message", "该帐套还未自动结转，不需要反结转！");
            return result;
        }
        result.put("message", "1");
        return result;
    }

    /**
     *
     * @param curtainId
     * @param month 反结转
     * @return
     */
    @RequestMapping(value = "chargeUpCancel", method = RequestMethod.GET)
    public Map<String,String> chargeUpCancel(Long curtainId,String month){
        //获得月末结账状态，查询时候月末结账
        boolean hasJie = curtainBalanceService.getHasJie(curtainId, month);
        //获得报表的状态，查询是否已经生成报表
        boolean hasReport = curtainBalanceService.hasReport(curtainId, month);
        Map<String,String> result = new HashMap<>();
        if(!hasReport && hasJie){
            curtainBalanceService.changeStatusA(curtainId,month);
        }else if(!hasJie){
            result.put("message", "该帐套还没有月末结账，不需要反结账！");
            return result;
        }else if(hasReport){
            result.put("message", "该帐套已生成报表，请先删除报表！");
            return result;
        }
        result.put("message", "1");
        return result;
    }

    /**
     * 获得步骤条
     * @param curtainId
     * @param month
     * @return
     */
    @RequestMapping(value = "getActive", method = RequestMethod.GET)
    public Map<String,Integer> getActive(Long curtainId,String month){
        int active = 0;
        //查询未审核的凭证
        boolean hasAudit = !voucherService.getNotAudit(curtainId,month);
        if(hasAudit) active++;
        //获得自动结转的状态，查询是否已结转
        boolean hasZhuan = curtainBalanceService.getHasZhuan(curtainId, month);
        if(hasZhuan) active++;
        //获得月末结账状态，查询时候月末结账
        boolean hasJie = curtainBalanceService.getHasJie(curtainId, month);
        if(hasJie) active++;
        //获得报表的状态，查询是否已经生成报表
        boolean hasReport = curtainBalanceService.hasReport(curtainId, month);
        if(hasReport) active++;

        Map<String,Integer> result = new HashMap<>();
        result.put("active", active);
        return result;
    }
}
