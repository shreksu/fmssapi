package fmssapi.voucher.mapper;

import fmssapi.voucher.model.OrderNum;
import org.apache.ibatis.annotations.*;

/**
 * 财务单号
 * @author suyuanyang
 * @create 2017-12-22 下午2:41
 */
@Mapper
public interface OrderNumMapper {

    @Select("SELECT NUM FROM ORDERNUM WHERE MONTH = #{month} AND CURTAINID = #{curtainId} FOR UPDATE")
    Long findNum(@Param("month") String month,@Param("curtainId")Long curtainId);

    @Update("UPDATE ORDERNUM SET NUM=#{num} WHERE MONTH = #{month} AND CURTAINID = #{curtainId}")
    void updateNum(@Param("month") String month,@Param("curtainId") Long curtainId,@Param("num") Long num);

    @Insert("INSERT INTO ORDERNUM(CURTAINID, MONTH, NUM) VALUES(#{curtainId}, #{month}, #{num})")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty="id")
    int insertByObject(OrderNum orderNum);
}
