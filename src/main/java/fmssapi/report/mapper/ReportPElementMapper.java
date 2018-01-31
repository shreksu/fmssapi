package fmssapi.report.mapper;

import fmssapi.report.model.ReportPElement;
import fmssapi.subject.model.Subject;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 利润表
 * @author suyuanyang
 * @create 2018-01-25 下午2:11
 */
@Mapper
public interface ReportPElementMapper {
    
    @Select("SELECT * FROM REPORTPELEMENT ORDER BY CODE ASC")
    List<ReportPElement> findAll();

    @Insert("INSERT INTO REPORTPELEMENT(COMFLAG, CODE, NAME, ROW, TYPE) VALUES(#{comFlag}, #{code}, #{name}, #{row}, #{type})")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty="id")
    int insertByObject(ReportPElement reportPElement);

    @Update("UPDATE REPORTPELEMENT SET NAME=#{name},CODE=#{code},ROW=#{row},TYPE=#{type} WHERE ID=#{id}")
    void updateByObject(ReportPElement reportPElement);

    @Select("SELECT * FROM REPORTPELEMENT WHERE ID = #{id}")
    Subject findById(@Param("id") Long id);

    @Delete("DELETE FROM REPORTPELEMENT WHERE ID=#{id}")
    void deleteById(@Param("id") Long id);

    @Select("SELECT * FROM REPORTPELEMENT WHERE COMFLAG=#{comFlag} ORDER BY CODE ASC")
    List<ReportPElement> findAllByComFlag(String comFlag);

    @Delete("DELETE FROM REPORTP_SUBJECT WHERE REPORTP_ID=#{id}")
    void deleteSubjectsById(Long id);
    //插入帐套的操作员
    @Insert("<script>"
            + "insert into REPORTP_SUBJECT(REPORTP_ID, CODE) "
            + "values "
            + "<foreach collection =\"subjectCodes\" item=\"code\" index= \"index\" separator =\",\"> "
            + "(#{id},#{code}) "
            + "</foreach > "
            + "</script>")
    void insertSubjectsById(@Param("id")Long id, @Param("subjectCodes")String[] subjectCodes);

    @Select("SELECT CODE FROM REPORTP_SUBJECT WHERE REPORTP_ID=#{id}")
    String[] getSubjectCodes(Long id);
}
