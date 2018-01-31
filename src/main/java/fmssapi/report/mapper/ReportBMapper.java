package fmssapi.report.mapper;

import fmssapi.report.model.ReportB;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2018-01-27 上午10:05
 */
@Mapper
public interface ReportBMapper {

    @Select("SELECT * FROM REPORTB WHERE CURTAINID=#{curtainId} AND MONTH=#{month}")
    List<ReportB> findAll(@Param("curtainId")Long curtainId,@Param("month")String month);

    @Insert("INSERT INTO REPORTB(CURTAINID, MONTH, REPORTBELEMENTID, INITNUM, LASTNUM) VALUES(#{curtainId}, #{month}, #{reportBElementId}, #{initNum}, #{lastNum})")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty="id")
    int insertByObject(ReportB reportB);

    @Delete("DELETE FROM REPORTB WHERE CURTAINID=#{curtainId} AND MONTH=#{month}")
    void deleteAll(@Param("curtainId")Long curtainId,@Param("month")String month);

//    @Update("UPDATE REPORTB SET NAME=#{name},CODE=#{code},ROW=#{row},TYPE=#{type},CALFUN=#{calFun} WHERE ID=#{id}")
//    void updateByObject(ReportB reportBElement);



}
