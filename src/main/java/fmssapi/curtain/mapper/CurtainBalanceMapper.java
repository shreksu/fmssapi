package fmssapi.curtain.mapper;

import fmssapi.curtain.model.CurtainBalance;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 帐套余额
 * @author suyuanyang
 * @create 2018-01-20 下午12:13
 */
@Mapper
public interface CurtainBalanceMapper {

    @Insert("INSERT INTO CURTAINBALANCE(MONTH, CURTAINID, SUBJECTID, DEBIT, CREDIT, DEBITAT, CREDITAT, STATUS) VALUES(#{month}, #{curtainId}, #{subjectId}, #{debit}, #{credit},#{debitAt},#{creditAt},#{status})")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty="id")
    int insertByObject(CurtainBalance curtainBalance);

    @Update("UPDATE CURTAINBALANCE SET DEBIT=#{debit},CREDIT=#{credit} WHERE ID=#{id}")
    void updateByObject(CurtainBalance curtainBalance);

    //获得帐套起初余额
    @Select("select * from CURTAINBALANCE where CURTAINID=#{curtainId} AND MONTH=#{month}")
    @Results({
            //查询关联对象
            @Result(property = "subject",
                    column = "SUBJECT_ID",
                    one = @One(select = "fmssapi.subject.mapper.SubjectMapper.findById"))
    })
    List<CurtainBalance> getCurtainBalance(@Param("curtainId")Long curtainId,
                                           @Param("month")String month);

    @Select("select * from CURTAINBALANCE where CURTAINID=#{curtainId} AND MONTH=#{month} and SUBJECTID=#{subjectId}")
    CurtainBalance getBySubjectCode(@Param("curtainId")Long curtainId,
                                    @Param("month")String month,
                                    @Param("subjectId")Long subjectId);

    @Select("select STATUS from CURTAINBALANCE where CURTAINID=#{curtainId} AND MONTH=#{month} limit 0,1")
    String getStatus(@Param("curtainId")Long curtainId,
                     @Param("month")String month);

    @Delete("DELETE FROM CURTAINBALANCE WHERE CURTAINID=#{curtainId} AND MONTH=#{month}")
    void deleteBalances(@Param("curtainId")Long curtainId,
                        @Param("month")String month);

    @Delete("DELETE FROM CURTAINBALANCE WHERE ID=#{id}")
    void deleteById(Long id);

    @Select("select SUM(c.debit) as debit,SUM(c.credit) as credit from CURTAINBALANCE c,SUBJECT s where c.CURTAINID=#{curtainId} AND c.MONTH=#{month} AND c.SUBJECTID=s.ID AND LENGTH(s.code)=4 AND s.code like CONCAT(#{str},'%')")
    List<Map<String, Double>> getSubjectSum(@Param("curtainId")Long curtainId,
                               @Param("month")String month,
                               @Param("str")String str);

    @Update("UPDATE CURTAINBALANCE SET STATUS=#{status} WHERE CURTAINID=#{curtainId} AND MONTH=#{month}")
    void setStatus(@Param("curtainId")Long curtainId,
                   @Param("month")String month,
                   @Param("status")String status);
}
