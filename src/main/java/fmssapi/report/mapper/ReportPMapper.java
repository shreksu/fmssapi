package fmssapi.report.mapper;

import fmssapi.report.model.ReportP;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2018-01-30 上午9:05
 */
@Mapper
public interface ReportPMapper {

    @Select("SELECT * FROM REPORTP WHERE CURTAINID=#{curtainId} AND MONTH=#{month}")
    List<ReportP> findAll(@Param("curtainId")Long curtainId,@Param("month")String month);

    @Insert("INSERT INTO REPORTP(CURTAINID, MONTH, REPORTPELEMENTID, MONTHNUM, YEARNUM) VALUES(#{curtainId}, #{month}, #{reportPElementId}, #{monthNum}, #{yearNum})")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty="id")
    int insertByObject(ReportP reportP);

    @Delete("DELETE FROM REPORTP WHERE CURTAINID=#{curtainId} AND MONTH=#{month}")
    void deleteAll(@Param("curtainId")Long curtainId,@Param("month")String month);
}
