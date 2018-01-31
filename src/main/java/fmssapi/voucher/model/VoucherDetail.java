package fmssapi.voucher.model;

import fmssapi.subject.model.Subject;
import lombok.Data;

/**
 * 凭证明细
 * @author suyuanyang
 * @create 2017-12-21 上午9:49
 */
@Data
public class VoucherDetail {

    // ID
    private Long id;
    // 凭证
    private Voucher voucher;
    // 摘要
    private String description;
    // 科目
    private Subject subject;
    // 借方金额
    private Long debit;
    // 贷方金额
    private Long credit;

    /***********展示属性***********/
    // 借方金额是否负数
    private boolean debitMinus;
    // 贷方金额是否负数
    private boolean creditMinus;
}
