package fmssapi.voucher.service;

import fmssapi.voucher.model.VoucherDetail;

import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-22 下午3:29
 */
public interface VoucherDetailService {


    int insertByObject(VoucherDetail voucherDetail);

    void updateByObject(VoucherDetail voucherDetail);

    List<VoucherDetail> getOriVoucherDetails(Long voucherId);

    /**
     * 获得凭证明细
     * @param voucherId
     * @return
     */
    List<VoucherDetail> getVoucherDetails(Long voucherId);

    /**
     * 获得凭证明细的id几个
     * @param voucherId
     * @return
     */
    List<Long> getOldIds(Long voucherId);

    /**
     * 通过id删除凭证明细
     * @param id
     */
    void deleteById(Long id);

    /**
     * 分页查询
     * @param curtainId
     * @param startDate
     * @param endDate
     * @param subjectCode
     * @return
     */
    List<VoucherDetail> listPage(Long curtainId, String startDate, String endDate, String subjectCode, Integer page);

    /**
     * 获得分页总数
     * @param curtainId
     * @param startDate
     * @param endDate
     * @param subjectCode
     * @return
     */
    Long listPageTotal(Long curtainId, String startDate, String endDate, String subjectCode);

    /**
     * 生成当月发生额(单位：元)
     * @param curtainId
     * @param month
     * @return
     */
    Map<Long,Double[]> getMonthAt(Long curtainId, String month);

    /**
     * 生成当月损益发生额（单位：分）
     * @param curtainId
     * @param month
     * @return
     */
    Map<Long,Long[]> get5MonthAt(Long curtainId, String month);

    /**
     * 删除明细
     * @param id
     */
    void deleteByVoucherId(Long id);
}
