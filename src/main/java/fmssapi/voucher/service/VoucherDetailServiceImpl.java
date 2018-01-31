package fmssapi.voucher.service;

import fmssapi.subject.model.Subject;
import fmssapi.subject.service.SubjectService;
import fmssapi.util.StringUtil;
import fmssapi.voucher.mapper.VoucherDetailMapper;
import fmssapi.voucher.model.VoucherDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-22 下午3:30
 */
@Service
@Transactional
public class VoucherDetailServiceImpl implements VoucherDetailService {

    @Autowired
    VoucherDetailMapper voucherDetailMapper;
    @Autowired
    SubjectService subjectService;

    @Override
    public int insertByObject(VoucherDetail voucherDetail) {
        return voucherDetailMapper.insertByObject(voucherDetail);
    }

    @Override
    public void updateByObject(VoucherDetail voucherDetail) {
        voucherDetailMapper.updateByObject(voucherDetail);
    }

    @Override
    public List<VoucherDetail> getOriVoucherDetails(Long voucherId){
        return voucherDetailMapper.getVoucherDetails(voucherId);
    }

    @Override
    public List<VoucherDetail> getVoucherDetails(Long voucherId) {
        List<VoucherDetail> voucherDetailList = voucherDetailMapper.getVoucherDetails(voucherId);
        for (VoucherDetail detail : voucherDetailList) {
            Subject subject = subjectService.findById(detail.getSubject().getId());
            subject.setFullName(subjectService.getFullName(subject));
            detail.setSubject(subject);
            if (detail.getDebit()!=null && detail.getDebit() < 0) {
                detail.setDebitMinus(true);
                detail.setDebit(Math.abs(detail.getDebit()));
            } else {
                detail.setDebitMinus(false);
            }
            if (detail.getCredit()!=null && detail.getCredit() < 0) {
                detail.setCreditMinus(true);
                detail.setCredit(Math.abs(detail.getCredit()));
            } else {
                detail.setCreditMinus(false);
            }
        }
        return voucherDetailList;
    }

    @Override
    public List<Long> getOldIds(Long voucherId) {
        return voucherDetailMapper.getIdsByVoucherId(voucherId);
    }

    @Override
    public void deleteById(Long id) {
        voucherDetailMapper.deleteById(id);
    }

    @Override
    public List<VoucherDetail> listPage(Long curtainId, String startDate, String endDate, String subjectCode, Integer page) {
        int pageSize = 10;
        Integer startIndex = null;
        if(page!=null) {
            startIndex = (page - 1) * pageSize;
        }
        List<VoucherDetail> voucherDetailList = voucherDetailMapper.listPage(curtainId,
                startDate, endDate, subjectCode, startIndex, pageSize);
        return voucherDetailList;
    }

    @Override
    public Long listPageTotal(Long curtainId, String startDate, String endDate, String subjectCode) {
        Long totalPage = voucherDetailMapper.listPageTotal(curtainId,
                startDate, endDate, subjectCode);
        return totalPage;
    }

    @Override
    public Map<Long, Double[]> getMonthAt(Long curtainId, String month) {
        Date date1 = StringUtil.getFirstDayOfMonth(month);
        Date date2 = StringUtil.getLastDayOfMonth(month);
        List<VoucherDetail> voucherDetailList = voucherDetailMapper.getMonthDetails(curtainId, date1, date2);
        Map<Long, Double[]> map = new HashMap<>();
        for(VoucherDetail detail : voucherDetailList){
            if(map.containsKey(detail.getSubject().getId())){
                Double[] arr = map.get(detail.getSubject().getId());
                arr[0] = StringUtil.getSum(arr[0],StringUtil.getDouble(detail.getDebit()));
                arr[1] = StringUtil.getSum(arr[1],StringUtil.getDouble(detail.getCredit()));
                map.put(detail.getSubject().getId(),arr);
            }else{
                map.put(detail.getSubject().getId(),new Double[]{StringUtil.getDouble(detail.getDebit()),
                        StringUtil.getDouble(detail.getCredit())});
            }
        }
        return map;
    }

    @Override
    public Map<Long, Long[]> get5MonthAt(Long curtainId, String month) {
        Date date1 = StringUtil.getFirstDayOfMonth(month);
        Date date2 = StringUtil.getLastDayOfMonth(month);
        List<VoucherDetail> voucherDetailList = voucherDetailMapper.get5MonthDetails(curtainId, date1, date2);
        Map<Long, Long[]> map = new HashMap<>();
        for(VoucherDetail detail : voucherDetailList){
            if(map.containsKey(detail.getSubject().getId())){
                Long[] arr = map.get(detail.getSubject().getId());
                arr[0] = StringUtil.getSum(arr[0],detail.getDebit());
                arr[1] = StringUtil.getSum(arr[1],detail.getCredit());
                map.put(detail.getSubject().getId(),arr);
            }else{
                map.put(detail.getSubject().getId(),new Long[]{detail.getDebit(),
                        detail.getCredit()});
            }
        }
        return map;
    }

    @Override
    public void deleteByVoucherId(Long id) {
        voucherDetailMapper.deleteByVoucherId(id);
    }


}
