package fmssapi.report.service;

import fmssapi.curtain.model.Curtain;
import fmssapi.curtain.model.CurtainBalance;
import fmssapi.curtain.service.CurtainBalanceService;
import fmssapi.curtain.service.CurtainService;
import fmssapi.report.mapper.ReportBMapper;
import fmssapi.report.model.ReportB;
import fmssapi.report.model.ReportBElement;
import fmssapi.report.model.ReportBTable;
import fmssapi.subject.model.Subject;
import fmssapi.subject.service.SubjectService;
import fmssapi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author suyuanyang
 * @create 2018-01-27 上午10:11
 */
@Service
@Transactional
public class ReportBServiceImpl implements ReportBService {

    @Autowired
    CurtainService curtainService;
    @Autowired
    ReportBElementService reportBElementService;
    @Autowired
    CurtainBalanceService curtainBalanceService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    ReportBMapper reportBMapper;

    @Override
    public void createReport(Long curtainId, String month) {
        //获得上年度的月末余额，即年初余额
        String lastYearmonth = StringUtil.getPreYearMonth(month);
        //先删除报表
        reportBMapper.deleteAll(curtainId,month);
        Curtain curtain = curtainService.findById(curtainId);
        List<ReportBElement> reportBElementList = reportBElementService.findAllByComFlag(curtain.getComFlag());
        //年初余额
        List<CurtainBalance> lastCurtainBalanceList = curtainBalanceService.getCurtainBalances(curtainId, lastYearmonth);
        //月末余额
        List<CurtainBalance> curtainBalanceList = curtainBalanceService.getCurtainBalances(curtainId, month);
        //帐套对应的所有科目
        List<Subject> subjects = subjectService.getSubjectsCode1List(curtainId);
        Map<String, Subject> subCodeMap = new HashMap<>();
        Map<Long, Subject> subIdMap = new HashMap<>();
        for (Subject sub : subjects) {
            subCodeMap.put(sub.getCode(), sub);
            subIdMap.put(sub.getId(), sub);
        }
        //加工年初余额的数据
        Map<Long, Double> lastMap = new HashMap<>();
        for (CurtainBalance bl : lastCurtainBalanceList) {
            Double amount = 0d;
            if (bl.getDebit() != null && bl.getDebit() != 0) {
                amount = bl.getDebit();
            } else if (bl.getCredit() != null && bl.getCredit() != 0) {
                amount = bl.getCredit();
            }
            lastMap.put(bl.getSubjectId(), amount);
        }
        //加工月末余额的数据
        Map<Long, Double> nowMap = new HashMap<>();
        for (CurtainBalance bl : curtainBalanceList) {
            Double amount = 0d;
            if (bl.getDebit() != null && bl.getDebit() != 0) {
                amount = bl.getDebit();
            } else if (bl.getCredit() != null && bl.getCredit() != 0) {
                amount = bl.getCredit();
            }
            nowMap.put(bl.getSubjectId(), amount);
        }


        //遍历计算规则
        Map<String, Double> abInitMap = new HashMap<>();
        Map<String, Double> abNowMap = new HashMap<>();
        for (ReportBElement element : reportBElementList) {
            Double initNum = 0d;//年初余额
            Double lastNum = 0d;//期末数
            String type = element.getType();
            if ("T".equals(type) || "B".equals(type)) {
                continue;
            }
            if ("N".equals(type)) {//汇总
                //查询需要汇总的科目
                String[] codes = reportBElementService.getSubjectCodes(element.getId());
                if (codes != null && codes.length > 0) {//有对应科目的，直接汇总
                    initNum = this.getAmount(lastMap, codes, subCodeMap);
                    lastNum = this.getAmount(nowMap, codes, subCodeMap);
                } else if (element.getCode().endsWith("SUM")) {//没有对应科目的可能是合计
                    initNum = abInitMap.get(element.getCode());
                    lastNum = abNowMap.get(element.getCode());
                }
            } else if ("C".equals(type)) {//计算
                String calFun = element.getCalFun();
                initNum = this.getCalAmount(calFun, abInitMap);
                lastNum = this.getCalAmount(calFun, abNowMap);
            }
            abInitMap.put(element.getCode(), initNum);
            abNowMap.put(element.getCode(), lastNum);
            //计算合计放入map
            this.calTotal(abInitMap,element.getCode(),initNum);
            this.calTotal(abNowMap,element.getCode(),lastNum);
            //保存数据
            if(initNum!=null) {
                initNum = 0 == initNum ? null : StringUtil.round2(initNum);
            }
            if(lastNum!=null) {
                lastNum = 0 == lastNum ? null : StringUtil.round2(lastNum);
            }
            if(initNum!=null || lastNum!=null) {
                ReportB reportB = new ReportB(curtainId, month, element.getId());
                reportB.setInitNum(initNum);
                reportB.setLastNum(lastNum);
                reportBMapper.insertByObject(reportB);
            }
        }
    }

    @Override
    public List<ReportB> findAll(Long curtainId, String month) {
        return reportBMapper.findAll(curtainId,month);
    }

    @Override
    public List<ReportBTable> getReportBTable(List<ReportBElement> reportBElementList, List<ReportB> reportBList) {
        Map<Long,ReportB> map = new HashMap<>();
        for(ReportB reportB : reportBList){
            map.put(reportB.getReportBElementId(),reportB);
        }
        List<ReportBTable> reportBTableList = new ArrayList<>();
        int i = 0;
        for(ReportBElement ele : reportBElementList){
            if(ele.getCode().startsWith("1")){
                ReportBTable reportBTable = new ReportBTable();
                reportBTable.setName1(ele.getName());
                if("T".equals(ele.getType())){
                    reportBTable.setIsTitle1("Y");
                }
                reportBTable.setRow1(ele.getRow());
                ReportB obj = map.get(ele.getId());
                if(obj!=null) {
                    reportBTable.setInitNum1(obj.getInitNum());
                    reportBTable.setLastNum1(obj.getLastNum());
                }
                reportBTableList.add(reportBTable);
            }else if(i<reportBTableList.size()){
                ReportBTable obj = reportBTableList.get(i);
                obj.setName2(ele.getName());
                if("T".equals(ele.getType())){
                    obj.setIsTitle2("Y");
                }
                obj.setRow2(ele.getRow());
                ReportB temp = map.get(ele.getId());
                if(temp!=null) {
                    obj.setInitNum2(temp.getInitNum());
                    obj.setLastNum2(temp.getLastNum());
                }
                i++;
            }
        }
        return reportBTableList;
    }

    @Override
    public void removeAll(Long curtainId, String month) {
        reportBMapper.deleteAll(curtainId,month);
    }

    /**
     * 计算合计放入合计项目
     * @param map 保存map
     * @param code 科目的编号
     * @param initNum 科目对应金额
     */
    private void calTotal(Map<String, Double> map, String code, Double initNum) {
        String parentCode;
        if(code.endsWith("SUM")){
            parentCode = code.substring(0,code.length()-4);
        }else{
            parentCode = code.substring(0,code.length()-1);
        }
        parentCode += "SUM";
        if(map.containsKey(parentCode)){
            map.put(parentCode,StringUtil.getSum(map.get(parentCode),initNum));
        }else{
            map.put(parentCode,initNum);
        }
    }

    /**
     * 计算类的科目，计算后返回金额
     * @param calFun
     * @param map
     * @return
     */
    private Double getCalAmount(String calFun, Map<String, Double> map) {
        String s = calFun;
        String reg = "\\$\\{\\w+\\}";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String matchWord = matcher.group(0);
            String property = matchWord.substring(2, matchWord.length() - 1);
            s = s.replace(matchWord, map.get(property) == null ? "0" : (map.get(property) + ""));
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine se = manager.getEngineByName("js");
        Double amount = null;
        try {
            amount = (Double) se.eval(s);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        //System.out.println(amount);
        return amount;
    }

    /**
     * 查到余额表后，根据
     *
     * @param map
     * @param codes
     * @return
     */
    private Double getAmount(Map<Long, Double> map, String[] codes, Map<String, Subject> subCodeMap) {
        Double amount = 0d;
        for (String code : codes) {
            Subject sub = subCodeMap.get(code);
            amount = StringUtil.getSum(amount, map.get(sub.getId()));
        }
        return amount;
    }
}
