package fmssapi.manager.mapper;

import fmssapi.manager.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 *
 *
 * @author suyuanyang
 * @create 2017-12-08 上午9:22
 */
@Mapper
public interface UserMapper {



    @Insert("INSERT INTO USER(LOGINNAME, PASSWORD, NAME, BIRTH, SEX, CREATEDATE, ROLECODE, STATE, COMFLAG) VALUES(#{loginName}, #{password}, #{name}, #{birth}, #{sex}, #{createDate}, #{roleCode}, #{state}, #{comFlag})")
    int insertByObject(User user);

    @Update("UPDATE USER SET NAME=#{name},BIRTH=#{birth},SEX=#{sex},ROLECODE=#{roleCode} WHERE loginName=#{loginName}")
    void updateByObject(User user);

    @Update("UPDATE USER SET PASSWORD=#{password} WHERE loginName=#{loginName}")
    void updatePwd(@Param("loginName") String loginName,@Param("password") String password);

    @Delete("DELETE FROM USER WHERE LOGINNAME=#{loginName}")
    void deleteById(String loginName);

    @Select("SELECT * FROM USER WHERE LOGINNAME = #{loginName}")
    User findById(@Param("loginName") String loginName);

    @Select("SELECT * FROM USER WHERE STATE='Y' AND COMFLAG= #{comFlag}")
    List<User> findAll(String comFlag);

    @Select("select u.* from CURTAIN_USER m,USER u where m.USER_ID=u.LOGINNAME AND m.CURTAIN_ID=#{curtainId}")
    List<User> findByCurtainId(Long curtainId);

    // 通过@Results，绑定返回值
//    @Results({
//            @Result(property = "loginName", column = "LOGINNAME"),
//            @Result(property = "pwd", column = "PWD"),
//            @Result(property = "name", column = "NAME"),
//            @Result(property = "birth", column = "BIRTH"),
//            @Result(property = "sex", column = "SEX")
//    })
//    @Select("SELECT LOGINNAME, PWD, NAME, BIRTH, SEX FROM USER")
//    List<User> findAllExclude();

}
