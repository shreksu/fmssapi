package fmssapi.curtain.mapper;

import fmssapi.curtain.model.Curtain;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-10 上午8:44
 */
@Mapper
public interface CurtainMapper {

    @Insert("INSERT INTO CURTAIN(COMFLAG, CODE, NAME, STARTMONTH, STATE) VALUES(#{comFlag}, #{code}, #{name}, #{startMonth}, #{state})")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty="id")
    int insertByObject(Curtain curtain);

    @Update("UPDATE CURTAIN SET NAME=#{name},STARTMONTH=#{startMonth} WHERE ID=#{id} AND STATE<>'N'")
    void updateByObject(Curtain curtain);

    @Select("SELECT MAX(CODE) FROM CURTAIN WHERE COMFLAG=#{comFlag}")
    String getNewCode(@Param("comFlag") String comFlag);

    @Select("SELECT * FROM CURTAIN WHERE ID = #{id}")
    Curtain findById(@Param("id") Long id);

    @Select("SELECT * FROM CURTAIN WHERE COMFLAG=#{comFlag} AND STATE<>'N'")
    List<Curtain> findAllByComFlag(String comFlag);

    //通过管理账号和帐套编号获得帐套
    @Select("SELECT * FROM CURTAIN WHERE COMFLAG = #{comFlag} AND CODE = #{code}")
    Curtain findByCode(@Param("comFlag") String comFlag,@Param("code") String code);

    //获得帐套的所有操作员
    @Select("select USER_ID from CURTAIN_USER where CURTAIN_ID=#{curtainId}")
    List<String> getUserByCurtainId(Long curtainId);

    //获得操作员可以操作的所有帐套
    @Select("select c.* from CURTAIN_USER m,CURTAIN c where m.CURTAIN_ID=c.id AND m.USER_ID=#{loginName}")
    List<Curtain> getCurtainsByUser(String loginName);

    //删除原来的帐套操作员
    @Delete("DELETE FROM CURTAIN_USER WHERE CURTAIN_ID=#{curtainId}")
    void deleteUserByCurtainId(Long curtainId);

    //插入帐套的操作员
    @Insert("<script>"
            + "insert into CURTAIN_USER(CURTAIN_ID, USER_ID) "
            + "values "
            + "<foreach collection =\"userIds\" item=\"userid\" index= \"index\" separator =\",\"> "
            + "(#{curtainId},#{userid}) "
            + "</foreach > "
            + "</script>")
    int insertUserByCurtainId(@Param("curtainId") Long curtainId,@Param("userIds") String[] userIds);

    //修改帐套主管
    @Update("UPDATE CURTAIN SET USERID=#{userId} WHERE ID=#{curtainId}")
    void updateCurtainUser(@Param("curtainId") Long curtainId,@Param("userId") String userId);

    @Update("UPDATE CURTAIN SET STATE=#{state} WHERE ID=#{curtainId}")
    void changeState(@Param("curtainId")Long curtainId,@Param("state") String state);
}
