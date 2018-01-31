package fmssapi.voucher.service;

/**
 * 凭证编号
 * @author suyuanyang
 * @create 2018-01-11 上午10:53
 */
public interface OrderNumService {
    /**
     * 查找当前凭证编号
     * @param month
     * @param curtainId
     * @return
     */
    Long findNum(String month,Long curtainId);

    /**
     * 更新当前凭证编号
     * @param month
     * @param curtainId
     * @param num
     */
    void updateNum(String month,Long curtainId,Long num);

}
