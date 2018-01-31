package fmssapi.subject.mapper;

import fmssapi.subject.model.Subject;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-11 下午1:11
 */
@Mapper
public interface SubjectMapper {


    /**
     * 1.用script标签包围，然后像xml语法一样书写
     * 2.用Provider去实现SQL拼接
     * @param curtainId
     * @return
     */
    @Select("SELECT * FROM SUBJECT WHERE CURTAINID = #{curtainId} ORDER BY CODE ASC")
    List<Subject> findAllByCurtainId(@Param("curtainId") Long curtainId);

    @Insert("INSERT INTO SUBJECT(CURTAINID, CODE, NAME, BALANCE) VALUES(#{curtainId}, #{code}, #{name}, #{balance})")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty="id")
    int insertByObject(Subject subject);

    @Update("UPDATE SUBJECT SET NAME=#{name},CODE=#{code},BALANCE=#{balance} WHERE ID=#{id}")
    void updateByObject(Subject subject);

    @Select("SELECT * FROM SUBJECT WHERE ID = #{id}")
    Subject findById(@Param("id") Long id);

    @Select("SELECT * FROM SUBJECT WHERE CURTAINID = #{curtainId} AND CODE=#{code} limit 0,1")
    Subject getSubjectByCurtainAndCode(@Param("curtainId") Long curtainId,@Param("code") String code);

    @Delete("DELETE FROM SUBJECT WHERE ID=#{id}")
    void deleteById(@Param("id") Long id);

    @Select("SELECT * FROM SUBJECT WHERE CURTAINID = #{curtainId} AND LENGTH(CODE)=4 ORDER BY CODE ASC")
    List<Subject> getCode1ByCurtain(Long curtainId);
}
