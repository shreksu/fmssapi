package fmssapi.voucher.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 凭证
 * @author suyuanyang
 * @create 2017-12-21 上午9:43
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Voucher {

    // ID
    private Long id;
    // 帐套ID
    private Long curtainId;
    // 记账号
    private Long code;
    // 制单日期
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date createDate;
    // 账单数
    private Integer billNum;
    // 记账人
    private String jiName;
    // 审核人
    private String shenName;
    // 出纳人
    private String chuName;
    // 制单人
    private String zhiName;
    // 借方金额
    private Long debit;
    // 贷方金额
    private Long credit;
    // 凭证明细
    List<VoucherDetail> voucherDetailList;
    // 摘要
    private String description;
    // 凭证状态
    private VoucherStatus voucherStatus;
    // 业务系统推送过来时，业务系统的系统编号
    private String sysCode;

    /***********展示属性***********/
    // 借方金额是否负数
    private boolean debitMinus;
    // 贷方金额是否负数
    private boolean creditMinus;

}
