package fmssapi.voucher.model;

import lombok.Data;

/**
 * 财务单号
 * @author suyuanyang
 * @create 2017-12-22 下午2:39
 */
@Data
public class OrderNum {

    private Long id;

    private Long curtainId;

    private String month;

    private Long num;
}
