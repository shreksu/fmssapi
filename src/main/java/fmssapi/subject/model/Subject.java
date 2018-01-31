package fmssapi.subject.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import fmssapi.curtain.model.CurtainBalance;
import lombok.Data;

/**
 * @author suyuanyang
 * @create 2017-12-11 下午1:10
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Subject {

    private Long id;

    private Long curtainId;

    private String name;

    private String code;

    private String balance;

    /***********展示属性***********/

    private Integer level;

    private String type;

    private Long toCurtainId;

    private Long[] subjectIds;

    private String fullName;

    private CurtainBalance curtainBalance;

    private Double amount;

    private String isLeaf;


    public Subject clone(Long curtainId){
        Subject subject = new Subject();
        subject.setCurtainId(curtainId);
        subject.setName(this.name);
        subject.setCode(this.code);
        subject.setBalance(this.balance);
        return subject;
    }


}
