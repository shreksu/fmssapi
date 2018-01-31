package fmssapi.report.service;

import fmssapi.curtain.model.Curtain;
import fmssapi.curtain.model.CurtainBalance;
import fmssapi.curtain.service.CurtainBalanceService;
import fmssapi.curtain.service.CurtainService;
import fmssapi.report.mapper.ReportPMapper;
import fmssapi.report.model.ReportP;
import fmssapi.report.model.ReportPElement;
import fmssapi.subject.model.Subject;
import fmssapi.subject.service.SubjectService;
import fmssapi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2018-01-30 上午9:08
 */
@Service
@Transactional
public class ReportPServiceImpl implements ReportPService{

    @Autowired
    ReportPMapper reportPMapper;
    @Autowired
    CurtainBalanceService curtainBalanceService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    ReportPElementService reportPElementService;
    @Autowired
    CurtainService curtainService;

    @Override
    public void createReport(Long curtainId, String month) {
        //获得上年度最后一个月
        String lastYearmonth = StringUtil.getPreYearMonth(month);
        String preMonth = StringUtil.getPreMonth(month);//上个月
        //先删除报表
        reportPMapper.deleteAll(curtainId,month);
        //月末余额
        List<CurtainBalance> curtainBalanceList = curtainBalanceService.getCurtainBalances(curtainId, month);
        List<ReportP> preReportPList = new ArrayList<>();
        if(!preMonth.equals(lastYearmonth)){//如果不是第一个月
            preReportPList = reportPMapper.findAll(curtainId,preMonth);
        }
        Map<Long,Double> lastMap = new HashMap<>();//上个月的年度累计
        for(ReportP reportp : preReportPList){
            lastMap.put(reportp.getId(),reportp.getYearNum());
        }
        //帐套对应的所有科目
        List<Subject> subjects = subjectService.getSubjectsCode1List(curtainId);
        Map<Long, Subject> subIdMap = new HashMap<>();
        Map<String, Subject> subCodeMap = new HashMap<>();
        for (Subject sub : subjects) {
            if(sub.getCode().startsWith("5")) {//只统计5类科目
                subIdMap.put(sub.getId(), sub);
                subCodeMap.put(sub.getCode(),sub);
            }
        }
        //加工本月发生额
        Map<String, Double> nowMap = new HashMap<>();
        for (CurtainBalance bl : curtainBalanceList) {
            Double amount = 0d;
            Subject sub = subIdMap.get(bl.getSubjectId());
            if(sub!=null) {
                if ("debit".equals(sub.getBalance())) {
                    amount = -bl.getDebitAt();
                } else if ("credit".equals(sub.getBalance())) {
                    amount = bl.getCreditAt();
                }
                nowMap.put(sub.getCode(), amount);
            }
        }
        //获得利润表计算元素
        Curtain curtain = curtainService.findById(curtainId);
        List<ReportPElement> reportPElementList = reportPElementService.findAllByComFlag(curtain.getComFlag());
        Map<String,Double> abMap = new HashMap<>();
        for(ReportPElement reportPElement : reportPElementList){
            Double amount = 0d;
            int flag = 1;
            if("H".equals(reportPElement.getType())){//汇总类
                String[] codes = reportPElementService.getSubjectCodes(reportPElement.getId());
                amount = this.getAmountH(codes, nowMap);
                flag = this.getFlag(codes,subCodeMap);
            }else if("J".equals(reportPElement.getType())){//计算类
                amount = abMap.get(reportPElement.getCode());
            }
            String code = reportPElement.getCode();
            String nextCode = String.valueOf(Integer.valueOf(code.substring(0, 1)) + 1);//需要计算的下一步的code
            if(abMap.containsKey(nextCode)){
                abMap.put(nextCode,StringUtil.getSum(abMap.get(nextCode),amount));
            }else{
                abMap.put(nextCode,amount);
            }
            ReportP reportP = new ReportP(curtainId,month,reportPElement.getId());
            if(amount!=null && amount!=0) {
                reportP.setMonthNum(StringUtil.round2(amount * flag));
            }
            //年度累计
            Double yearAmount = StringUtil.getSum(reportP.getMonthNum(),lastMap.get(reportPElement.getId()));
            if(yearAmount!=null && yearAmount!=0) {
                reportP.setYearNum(StringUtil.round2(yearAmount));
            }
            reportPMapper.insertByObject(reportP);
        }

        //处理本年度累计利润

    }

    @Override
    public List<ReportP> findAll(Long curtainId, String month) {
        return reportPMapper.findAll(curtainId,month);
    }

    @Override
    public void removeAll(Long curtainId, String month) {
        reportPMapper.deleteAll(curtainId,month);
    }

    /**
     * 获得该科目在利润表中是加还是减
     * @param codes
     * @param subCodeMap
     * @return
     */
    private int getFlag(String[] codes, Map<String, Subject> subCodeMap) {
        int flag = 1;
        for(String code : codes){
            Subject sub = subCodeMap.get(code);
            if("debit".equals(sub.getBalance())){// 如果是成本则是减
                flag = -1;
            }else{
                return 1; // 如果是收入或者收入-成本（如其他业务利润），则是加
            }
        }
        return flag;
    }

    /**
     * 获得科目对应的金额
     * @param codes
     * @param nowMap
     * @return
     */
    private Double getAmountH(String[] codes, Map<String, Double> nowMap) {
        Double amount = 0d;
        for(String code : codes){
            amount = StringUtil.getSum(amount, nowMap.get(code));
        }
        return amount;
    }


}
