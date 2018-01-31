package fmssapi.report.mapper;

import fmssapi.report.model.ReportBElement;
import fmssapi.subject.model.Subject;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2018-01-25 上午10:04
 */
@Mapper
public interface ReportBElementMapper {


    @Select("SELECT * FROM REPORTBELEMENT ORDER BY CODE ASC")
    List<ReportBElement> findAll();

    @Insert("INSERT INTO REPORTBELEMENT(COMFLAG, CODE, NAME, ROW, TYPE) VALUES(#{comFlag}, #{code}, #{name}, #{row}, #{type})")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty="id")
    int insertByObject(ReportBElement reportBElement);

    @Update("UPDATE REPORTBELEMENT SET NAME=#{name},CODE=#{code},ROW=#{row},TYPE=#{type},CALFUN=#{calFun} WHERE ID=#{id}")
    void updateByObject(ReportBElement reportBElement);

    @Select("SELECT * FROM REPORTBELEMENT WHERE ID = #{id}")
    Subject findById(@Param("id") Long id);

    @Delete("DELETE FROM REPORTBELEMENT WHERE ID=#{id}")
    void deleteById(@Param("id") Long id);

    @Select("SELECT * FROM REPORTBELEMENT WHERE COMFLAG=#{comFlag} ORDER BY CODE ASC")
    List<ReportBElement> findAllByComFlag(String comFlag);

    @Delete("DELETE FROM REPORTB_SUBJECT WHERE REPORTB_ID=#{id}")
    void deleteSubjectsById(Long id);
    //插入帐套的操作员
    @Insert("<script>"
            + "insert into REPORTB_SUBJECT(REPORTB_ID, CODE) "
            + "values "
            + "<foreach collection =\"subjectCodes\" item=\"code\" index= \"index\" separator =\",\"> "
            + "(#{id},#{code}) "
            + "</foreach > "
            + "</script>")
    void insertSubjectsById(@Param("id")Long id, @Param("subjectCodes")String[] subjectCodes);

    @Select("SELECT CODE FROM REPORTB_SUBJECT WHERE REPORTB_ID=#{id}")
    String[] getSubjectCodes(Long id);
}
