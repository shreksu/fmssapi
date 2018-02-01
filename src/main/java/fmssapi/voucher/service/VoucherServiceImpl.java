package fmssapi.voucher.service;

import fmssapi.subject.model.Subject;
import fmssapi.subject.service.SubjectService;
import fmssapi.util.StringUtil;
import fmssapi.voucher.mapper.VoucherMapper;
import fmssapi.voucher.model.Voucher;
import fmssapi.voucher.model.VoucherDetail;
import fmssapi.voucher.model.VoucherStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-21 上午10:54
 */
@Service
@Transactional
public class VoucherServiceImpl implements VoucherService{

    @Autowired
    VoucherMapper voucherMapper;
    @Autowired
    VoucherDetailService voucherDetailService;
    @Autowired
    OrderNumService orderNumService;
    @Autowired
    SubjectService subjectService;

    @Override
    public List<Voucher> list() {
        return voucherMapper.findAll();
    }




    @Override
    public void saveOrUpdate(Voucher voucher) {
        List<Long> old_ids = new ArrayList<>();//保存原来凭证明细的id
        if(voucher.getId()==null){
            //生成单号
            if(voucher.getCreateDate()==null) {
                voucher.setCreateDate(new Date());
            }
            String month = StringUtil.getMonth(voucher.getCreateDate());
            voucher.setCode(orderNumService.findNum(month,voucher.getCurtainId()));
            if(voucher.getVoucherStatus()==null) {
                voucher.setVoucherStatus(VoucherStatus.C);
            }
            voucherMapper.insertByObject(voucher);
            orderNumService.updateNum(month,voucher.getCurtainId(),voucher.getCode()+1);
        }else{
            voucherMapper.updateByObject(voucher);
            old_ids = voucherDetailService.getOldIds(voucher.getId());
        }
        List<Long> ids = new ArrayList<>();
        for(VoucherDetail detail : voucher.getVoucherDetailList()){
            detail.setVoucher(voucher);
            if(detail.getId()==null) {
                voucherDetailService.insertByObject(detail);
            }else{
                voucherDetailService.updateByObject(detail);
            }
            ids.add(detail.getId());
        }
        for(Long id : old_ids){
            if(!ids.contains(id)){
                voucherDetailService.deleteById(id);
            }
        }

    }

    @Override
    public List<Voucher> listPage(Long curtainId, String startDate, String endDate, Long startCode, Long endCode, String zhiName, String shenName, String status, String sysCode, Integer page) {
        int pageSize = 10;
        Integer startIndex = null;
        if(page!=null){
            startIndex = (page-1) * pageSize;
        }
        List<Voucher> voucherList = voucherMapper.listPage(curtainId,
                startDate,endDate,startCode,endCode,zhiName,shenName,
                status,sysCode,startIndex,pageSize);
        return voucherList;
    }
    @Override
    public Long listPageTotal(Long curtainId, String startDate, String endDate, Long startCode, Long endCode, String zhiName, String shenName, String status, String sysCode){
        Long totalPage = voucherMapper.listPageTotal(curtainId,
                startDate, endDate, startCode, endCode, zhiName, shenName,
                status, sysCode);
        return totalPage;
    }

    @Override
    public String auditVoucher(String userName, Voucher voucher) {
        String result = null;
        if(VoucherStatus.C  == voucher.getVoucherStatus()){
            //校验审核的凭证是否借贷方相等
            if(voucher.getCredit().longValue()!=voucher.getDebit().longValue()){
                result = "该凭证借贷方不相等，请修改后审核!";
                return result;
            }
            if(!voucher.getZhiName().equals(userName)){
                voucher.setShenName(userName);
                voucher.setVoucherStatus(VoucherStatus.G);
                voucherMapper.auditShen(voucher);
            }else {
                result = "不可以审核自己制单的凭证!";
            }
        }else{
            result = "只有状态为录入的凭证，才可以审核!";
        }
        return result;
    }

    @Override
    public Voucher findById(Long id) {
        return voucherMapper.findById(id);
    }

    @Override
    public Voucher getByCodeAndMonth(Long code,Long curtainId, Date[] dates) {
        return voucherMapper.getByCodeAndMonth(code,curtainId,dates[0],dates[1]);
    }

    @Override
    public List<Voucher> getListBySysCode(String sysCode, Long curtainId) {
        return voucherMapper.getListBySysCode(sysCode, curtainId);
    }

    @Override
    public void cancelAudit(Voucher voucher) {
        voucher.setShenName("");
        voucher.setVoucherStatus(VoucherStatus.C);
        voucherMapper.auditShen(voucher);
    }

    @Override
    public void autoSettle(Long curtainId, String month) {
        Map<Long,Long[]> map = voucherDetailService.get5MonthAt(curtainId,month);
        List<VoucherDetail> voucherDetailList = new ArrayList<>();
        Long debit = 0l;
        Long credit = 0l;
        for(Map.Entry<Long,Long[]> entry : map.entrySet()){
            Long subjectId = entry.getKey();
            Long[] arr = entry.getValue();
            Long amount = StringUtil.getCha(arr[1],arr[0]);//贷方余额
            if(amount==0) continue;//为0不用处理
            Subject subject = subjectService.findById(subjectId);
            VoucherDetail voucherDetail1 = new VoucherDetail();
            voucherDetail1.setDescription("结转");
            voucherDetail1.setSubject(subject);
            VoucherDetail voucherDetail2 = new VoucherDetail();
            voucherDetail2.setDescription("结转");
            voucherDetail2.setSubject(subjectService.getSubjectByCurtainAndCode(curtainId,"3131"));
            if("debit".equals(subject.getBalance())){//借方余额
                amount = -amount;
                voucherDetail1.setCredit(amount);
                voucherDetail2.setDebit(amount);
            }else{
                voucherDetail1.setDebit(amount);
                voucherDetail2.setCredit(amount);
            }
            voucherDetailList.add(voucherDetail1);
            voucherDetailList.add(voucherDetail2);
            debit+=amount;
            credit+=amount;
        }
        if(voucherDetailList.size()>0){
            Voucher voucher = new Voucher();
            voucher.setDebit(debit);
            voucher.setVoucherStatus(VoucherStatus.G);
            voucher.setCredit(credit);
            voucher.setDescription("结转");
            voucher.setCreateDate(new Date());
            voucher.setCurtainId(curtainId);
            voucher.setZhiName("zhuan");
            voucher.setVoucherDetailList(voucherDetailList);
            this.saveOrUpdate(voucher);
        }
    }

    @Override
    public List<Voucher> getByZhiName(Long curtainId, String month, String zhiName) {
        Date date1 = StringUtil.getFirstDayOfMonth(month);
        Date date2 = StringUtil.getLastDayOfMonth(month);
        return voucherMapper.getByZhiName(zhiName, curtainId, date1, date2);
    }

    @Override
    public boolean getNotAudit(Long curtainId, String month) {
        Date date1 = StringUtil.getFirstDayOfMonth(month);
        Date date2 = StringUtil.getLastDayOfMonth(month);
        List<Voucher> voucherList = voucherMapper.getByStatus("C", curtainId, date1, date2);
        return voucherList.size()>0;
    }

    @Override
    public void update(Voucher voucher) {
        voucherMapper.updateZhiName(voucher);
    }

    @Override
    public void cancelVoucher(Voucher voucher) {
        voucherDetailService.deleteByVoucherId(voucher.getId());
        voucher.setDebit(0l);
        voucher.setCredit(0l);
        voucher.setDescription("作废");
        voucherMapper.updateByObject(voucher);
    }
}
