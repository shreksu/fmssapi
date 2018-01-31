package fmssapi.curtain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import fmssapi.manager.model.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-10 上午8:39
 */
@Data
public class Curtain {

    private Long id;

    private String comFlag;

    private String code;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date startMonth;

    private String state;
    /**
     * 帐套主管
     */
    private String userId;

    /*******展示数据******/
    /**
     * 帐套操作员集合
     */
    private List<User> userList;


    private String[] userIds;
}
