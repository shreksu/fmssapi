package fmssapi.voucher.service;

import fmssapi.voucher.model.Voucher;

import java.util.Date;
import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-21 上午10:53
 */
public interface VoucherService {
    /**
     * 凭证列表
     * @return
     */
    List<Voucher> list();

    /**
     * 保存
     * @param voucher
     */
    void saveOrUpdate(Voucher voucher);

    /**
     * 分页查询凭证
     * @param curtainId
     * @param startDate
     * @param endDate
     * @param startCode
     * @param endCode
     * @param zhiName
     * @param shenName
     * @param status
     * @param page
     * @return
     */
    List<Voucher> listPage(Long curtainId, String startDate, String endDate, Long startCode, Long endCode, String zhiName, String shenName, String status,String sysCode, Integer page);

    Long listPageTotal(Long curtainId, String startDate, String endDate, Long startCode, Long endCode, String zhiName, String shenName, String status, String sysCode);

    /**
     * 审核凭证
     * @param userName 审核人
     * @param voucher 凭证ID
     * @return
     */
    String auditVoucher(String userName, Voucher voucher);

    /**
     * 查凭证
     * @param id
     * @return
     */
    Voucher findById(Long id);

    /**
     * 获得日期区间内的凭证
     * @param code 凭证编号
     * @param curtainId 帐套ID
     * @param dates 日期区间
     * @return
     */
    Voucher getByCodeAndMonth(Long code,Long curtainId, Date[] dates);

    /**
     * 获得凭证列表
     * @param sysCode 业务系统编号
     * @param curtainId 帐套ID
     * @return
     */
    List<Voucher> getListBySysCode(String sysCode, Long curtainId);

    /**
     * 取消审核
     * @param voucher
     */
    void cancelAudit(Voucher voucher);

    /**
     * 月末结转
     * @param curtainId
     * @param month
     */
    void autoSettle(Long curtainId, String month);

    /**
     * 通过制单人获得数据
     * @param curtainId
     * @param month
     * @param zhuan
     * @return
     */
    List<Voucher> getByZhiName(Long curtainId, String month, String zhuan);

    /**
     * 查看是否有未审核的凭证
     * @param curtainId
     * @param month
     * @return
     */
    boolean getNotAudit(Long curtainId, String month);

    /**
     * 更新
     * @param voucher
     */
    void update(Voucher voucher);
}
