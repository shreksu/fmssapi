package fmssapi.curtain.service;

import fmssapi.curtain.mapper.CurtainBalanceMapper;
import fmssapi.curtain.model.CurtainBalance;
import fmssapi.report.model.ReportB;
import fmssapi.report.model.ReportP;
import fmssapi.report.service.ReportBService;
import fmssapi.report.service.ReportPService;
import fmssapi.subject.model.Subject;
import fmssapi.subject.service.SubjectService;
import fmssapi.util.StringUtil;
import fmssapi.voucher.model.Voucher;
import fmssapi.voucher.service.VoucherDetailService;
import fmssapi.voucher.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2018-01-20 下午12:14
 */
@Service
@Transactional
public class CurtainBalanceServiceImpl implements CurtainBalanceService {

    @Autowired
    CurtainBalanceMapper curtainBalanceMapper;
    @Autowired
    SubjectService subjectService;
    @Autowired
    VoucherDetailService voucherDetailService;
    @Autowired
    VoucherService voucherService;
    @Autowired
    ReportBService reportBService;
    @Autowired
    ReportPService reportPService;

    @Override
    public List<CurtainBalance> getCurtainBalances(Long curtainId, String month) {
        return curtainBalanceMapper.getCurtainBalance(curtainId,month);
    }

    @Override
    public void loadSubject(List<Subject> subjectList, List<CurtainBalance> curtainBalanceList) {
        Map<Long,CurtainBalance> map = new HashMap<>();
        for(CurtainBalance b : curtainBalanceList){
            map.put(b.getSubjectId(),b);
        }
        Map<String,Subject> subMap = new HashMap<>();
        for(Subject s : subjectList){
            if(map.get(s.getId())!=null) {
                if ("debit".equals(s.getBalance())) {
                    s.setAmount(map.get(s.getId()).getDebit());
                } else {
                    s.setAmount(map.get(s.getId()).getCredit());
                }
            }
            subMap.put(s.getCode(),s);
        }
        for(Subject s : subjectList){
            String parentCode = null;
            if(s.getCode().length()==7){
                parentCode = s.getCode().substring(0,4);
            }
            if(s.getCode().length()==10){
                parentCode = s.getCode().substring(0,7);
            }
            if(parentCode!=null){
                subMap.get(parentCode).setIsLeaf("N");
            }
        }
    }

    @Override
    public void loadMonthSubject(List<Subject> subjectList,List<CurtainBalance> curtainBalancePreList, List<CurtainBalance> curtainBalanceList) {
        Map<Long,CurtainBalance> preMap = new HashMap<>();
        for(CurtainBalance b : curtainBalancePreList){
            preMap.put(b.getSubjectId(),b);
        }
        Map<Long,CurtainBalance> map = new HashMap<>();
        for(CurtainBalance b : curtainBalanceList){
            CurtainBalance oriCurtainBalance = preMap.get(b.getSubjectId());
            if(oriCurtainBalance!=null){
                b.setOriDebit(oriCurtainBalance.getDebit());
                b.setOriCredit(oriCurtainBalance.getCredit());
            }
            map.put(b.getSubjectId(),b);
        }
        Map<String,Subject> subMap = new HashMap<>();
        for(Subject s : subjectList){
            s.setCurtainBalance(map.get(s.getId()));
            subMap.put(s.getCode(),s);
        }
        for(Subject s : subjectList){
            String parentCode = null;
            if(s.getCode().length()==7){
                parentCode = s.getCode().substring(0,4);
            }
            if(s.getCode().length()==10){
                parentCode = s.getCode().substring(0,7);
            }
            if(parentCode!=null){
                subMap.get(parentCode).setIsLeaf("N");
            }
        }
    }

    @Override
    public void update(Long curtainId, String month, String subjectCode, Double amount) {
        Subject subject = subjectService.getSubjectByCurtainAndCode(curtainId, subjectCode);
        CurtainBalance curtainBalance = curtainBalanceMapper.getBySubjectCode(curtainId, month, subject.getId());
        if(curtainBalance==null){
            curtainBalance = new CurtainBalance();
            curtainBalance.setMonth(month);
            curtainBalance.setCurtainId(curtainId);
            curtainBalance.setSubjectId(subject.getId());
            curtainBalance.setStatus("A");
        }
        if("debit".equals(subject.getBalance())){//借方
            curtainBalance.setDebit(amount);
        }else{
            curtainBalance.setCredit(amount);
        }
        if(curtainBalance.getId()==null){
            curtainBalanceMapper.insertByObject(curtainBalance);
        }else{
            curtainBalanceMapper.updateByObject(curtainBalance);
        }
    }

    @Override
    public void delete(Long curtainId, String month, String subjectCode) {
        Subject subject = subjectService.getSubjectByCurtainAndCode(curtainId, subjectCode);
        CurtainBalance curtainBalance = curtainBalanceMapper.getBySubjectCode(curtainId, month, subject.getId());
        if(curtainBalance!=null){
            curtainBalanceMapper.deleteById(curtainBalance.getId());
        }
    }

    @Override
    public Map<String, String> checkSum(Long curtainId, String month) {
        List<Map<String, Double>> list1 = curtainBalanceMapper.getSubjectSum(curtainId, month, "1");
        List<Map<String, Double>> list2 = curtainBalanceMapper.getSubjectSum(curtainId, month, "2");
        List<Map<String, Double>> list3 = curtainBalanceMapper.getSubjectSum(curtainId, month, "3");
        List<Map<String, Double>> list5 = curtainBalanceMapper.getSubjectSum(curtainId, month, "5");
        Double a1 = list1.get(0)==null?0d:StringUtil.round2(StringUtil.getCha(list1.get(0).get("debit"), list1.get(0).get("credit")));
        Double a2 = list2.get(0)==null?0d:StringUtil.round2(StringUtil.getCha(list2.get(0).get("credit"), list2.get(0).get("debit")));
        Double a3 = list3.get(0)==null?0d:StringUtil.round2(StringUtil.getCha(list3.get(0).get("credit"), list3.get(0).get("debit")));
        Double a5 = list5.get(0)==null?0d:StringUtil.round2(StringUtil.getCha(list5.get(0).get("credit"), list5.get(0).get("debit")));
        Map<String, String> result = new HashMap<>();
        if(a1 == StringUtil.round2(a2 + a3 + a5).doubleValue()){
            result.put("errno","1");
        }else{
            result.put("errno","0");
        }
        result.put("resultData", a1+"="+a2+"+"+a3+"+"+a5);
        return result;
    }

    @Override
    public void calculateTotal(List<Subject> subjectList) {
        Subject[] subs = new Subject[4];
        Integer[] indexs = new Integer[4];//合计需要插入的位置
        for(int i=0;i<4;i++){
            subs[i] = new Subject();
            CurtainBalance curtainBalance = new CurtainBalance();
            subs[i].setCurtainBalance(curtainBalance);
            subs[i].setIsLeaf("G");
            subs[i].setCode(i+1+"000");
        }
        subs[0].setName("资产合计");
        subs[1].setName("负债合计");
        subs[2].setName("权益合计");
        subs[3].setName("损益合计");
        int index = 0;
        for(Subject sub : subjectList){
            int i = Integer.valueOf(sub.getCode().substring(0, 1));
            if(i==5) i=4;
            if(sub.getCode().length()==4 && sub.getCurtainBalance()!=null){
                CurtainBalance obj = subs[i-1].getCurtainBalance();
                obj.setOriDebit(StringUtil.round2(StringUtil.getSum(obj.getOriDebit(), sub.getCurtainBalance().getOriDebit())));
                obj.setOriCredit(StringUtil.round2(StringUtil.getSum(obj.getOriCredit(), sub.getCurtainBalance().getOriCredit())));
                obj.setDebit(StringUtil.round2(StringUtil.getSum(obj.getDebit(), sub.getCurtainBalance().getDebit())));
                obj.setCredit(StringUtil.round2(StringUtil.getSum(obj.getCredit(), sub.getCurtainBalance().getCredit())));
                obj.setDebitAt(StringUtil.round2(StringUtil.getSum(obj.getDebitAt(), sub.getCurtainBalance().getDebitAt())));
                obj.setCreditAt(StringUtil.round2(StringUtil.getSum(obj.getCreditAt(), sub.getCurtainBalance().getCreditAt())));
            }
            indexs[i-1] = index+1;
            index++;
        }
        for(int i= 3;i>=0;i--){
            subjectList.add(indexs[i],subs[i]);
        }
    }

    @Override
    public boolean getHasJie(Long curtainId, String month) {
        String status = curtainBalanceMapper.getStatus(curtainId, month);
        return "G".equals(status);
    }

    @Override
    public boolean getHasZhuan(Long curtainId, String month) {
        List<Voucher> voucherList = voucherService.getByZhiName(curtainId, month, "zhuan");
        return voucherList!=null && voucherList.size()>0;
    }

    @Override
    public void autoSettleCancel(Long curtainId, String month) {
        List<Voucher> voucherList = voucherService.getByZhiName(curtainId, month, "zhuan");
        for(Voucher voucher : voucherList){
            voucherDetailService.deleteByVoucherId(voucher.getId());
            voucher.setZhiName("void");
            voucher.setDescription("结转凭证作废");
            voucher.setDebit(0l);
            voucher.setCredit(0l);
            voucherService.update(voucher);
        }
    }

    @Override
    public boolean hasReport(Long curtainId, String month) {
        List<ReportB> reportBList = reportBService.findAll(curtainId,month);
        List<ReportP> reportPList = reportPService.findAll(curtainId,month);
        return reportBList.size()>0 || reportPList.size()>0;
    }

    @Override
    public void changeStatusA(Long curtainId, String month) {
        curtainBalanceMapper.setStatus(curtainId,month,"A");
    }

    @Override
    public String getStatus(Long curtainId, String month) {
        return curtainBalanceMapper.getStatus(curtainId, month);
    }

    @Override
    public void deleteBalances(Long curtainId, String month) {
        curtainBalanceMapper.deleteBalances(curtainId,month);
    }

    @Override
    public void createBalances(Long curtainId, String month, String type) {
        Map<Long,Double[]> mapInit = new HashMap<>();//上月期末余额
        //Map<Long,Double[]> mapThis = new HashMap<>();//本月发生额
        //上一个月
        String preMonth = StringUtil.getPreMonth(month);
        //先获得上一期的期末余额
        List<CurtainBalance> curtainBalanceList = this.getCurtainBalances(curtainId, preMonth);
        for(CurtainBalance cb : curtainBalanceList){
            mapInit.put(cb.getSubjectId(),new Double[]{cb.getDebit(),cb.getCredit(),0d,0d});
        }
        // 获得默认余额方向
        List<Subject> subjectList = subjectService.findSimpleAllByCurtainId(curtainId);
        //获得本月发生额
        Map<Long,Double[]> mapAt = voucherDetailService.getMonthAt(curtainId,month);
        //将本月发生额自动往上汇总
        this.collectParentSubject(subjectList,mapAt);
        //将期初余额与发生额合二为一
        for(Map.Entry<Long,Double[]> entry : mapAt.entrySet()){
            Long id = entry.getKey();
            Double[] arr = entry.getValue();
            Double[] arrInit = mapInit.get(id);
            if(arrInit!=null){
                arrInit[0] = StringUtil.getSum(arrInit[0],arr[0]);
                arrInit[1] = StringUtil.getSum(arrInit[1], arr[1]);
                arrInit[2] = arr[0];
                arrInit[3] = arr[1];
            }else{//没有起初余额
                arrInit = new Double[]{arr[0],arr[1],arr[0],arr[1]};
            }
            mapInit.put(id,arrInit);
        }
        Map<Long,String> subMap = new HashMap<>();
        for(Subject sub : subjectList){
            subMap.put(sub.getId(),sub.getBalance());
        }
        // 生成余额表
        for(Map.Entry<Long,Double[]> entry : mapInit.entrySet()){
            Long id = entry.getKey();
            Double[] arr = entry.getValue();
            CurtainBalance curtainBalance = new CurtainBalance();
            curtainBalance.setMonth(month);
            curtainBalance.setCurtainId(curtainId);
            curtainBalance.setSubjectId(id);
            if("month".equals(type)){
                curtainBalance.setStatus("G");
            }else {
                curtainBalance.setStatus("A");
            }
            String balance = subMap.get(id);
            if("debit".equals(balance)) {
                curtainBalance.setDebit(StringUtil.round2(StringUtil.getCha(arr[0], arr[1])));
            }else {
                curtainBalance.setCredit(StringUtil.round2(StringUtil.getCha(arr[1],arr[0])));
            }
            curtainBalance.setDebitAt(arr[2] != null ? StringUtil.round2(arr[2]) : null);
            curtainBalance.setCreditAt(arr[3] != null ? StringUtil.round2(arr[3]) : null);
            curtainBalanceMapper.insertByObject(curtainBalance);
        }
    }

    /**
     * 自动网父级科目汇总发生额
     * @param subjectList
     * @param mapAt
     */
    private void collectParentSubject(List<Subject> subjectList,Map<Long, Double[]> mapAt) {
        Map<String,Subject> subMap = new HashMap<>();
        for(Subject s : subjectList){
            subMap.put(s.getCode(),s);
        }
        for(Subject s : subjectList){
            Double[] childArr = mapAt.get(s.getId());
            if(childArr==null) continue;
            String code = s.getCode();
            while(code.length()>4){
                code = code.substring(0,code.length()-3);
                Long parentId = subMap.get(code).getId();
                if(mapAt.containsKey(parentId)){
                    Double[] arr = mapAt.get(parentId);
                    arr[0] = StringUtil.getSum(arr[0],childArr[0]);
                    arr[1] = StringUtil.getSum(arr[1],childArr[1]);
                }else{
                    Double[] arr = new Double[]{childArr[0],childArr[1]};
                    mapAt.put(parentId,arr);
                }
            }
        }
    }


}
