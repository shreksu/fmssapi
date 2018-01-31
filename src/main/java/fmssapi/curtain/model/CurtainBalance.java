package fmssapi.curtain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 帐套余额
 * @author suyuanyang
 * @create 2018-01-20 上午11:59
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurtainBalance {

    private Long id;
    // 月份
    private String month;
    // 帐套
    private Long curtainId;

    private Long subjectId;
    // 借方余额
    private Double debit;
    // 贷方余额
    private Double credit;
    // 当期发生
    private Double debitAt;
    // 当期发生
    private Double creditAt;
    // 状态：（A：登账，G：已结账）
    private String status;

    /**********展示辅助属性*********/
    //期初借方
    private Double oriDebit;
    //期初贷方
    private Double oriCredit;


}
