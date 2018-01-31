package fmssapi.manager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import fmssapi.curtain.model.Curtain;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户类
 * @author suyuanyang
 * @create 2017-12-07 上午9:24
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private String loginName;

    private String password;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date birth;

    private Integer sex;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date createDate;

    private String roleCode;
    // 对应的管理员
    private String comFlag;

    //状态Y(在职)N(离职)
    private String state;

    private List<Curtain> curtainList;


}
