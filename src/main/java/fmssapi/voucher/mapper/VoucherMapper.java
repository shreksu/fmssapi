package fmssapi.voucher.mapper;

import fmssapi.voucher.model.Voucher;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-21 上午9:52
 */
@Mapper
public interface VoucherMapper {

    @Select("SELECT * FROM VOUCHER")
    List<Voucher> findAll();

    @Insert("INSERT INTO VOUCHER(CURTAINID, CODE, CREATEDATE, BILLNUM, JINAME, SHENNAME, CHUNAME, ZHINAME, DEBIT, CREDIT, DESCRIPTION, VOUCHERSTATUS, SYSCODE) VALUES(#{curtainId}, #{code}, #{createDate}, #{billNum}, #{jiName}, #{shenName}, #{chuName}, #{zhiName}, #{debit}, #{credit}, #{description}, #{voucherStatus}, #{sysCode} )")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty="id")
    int insertByObject(Voucher voucher);

    @Update("UPDATE VOUCHER SET CREDIT=#{credit},DEBIT=#{debit},CREATEDATE=#{createDate} WHERE ID=#{id}")
    void updateByObject(Voucher voucher);

    @Update("UPDATE VOUCHER SET CREDIT=#{credit},DEBIT=#{debit},ZHINAME=#{zhiName},DESCRIPTION=#{description} WHERE ID=#{id}")
    void updateZhiName(Voucher voucher);

    //审核凭证
    @Update("UPDATE VOUCHER SET VOUCHERSTATUS=#{voucherStatus},SHENNAME=#{shenName} WHERE ID=#{id}")
    void auditShen(Voucher voucher);

    @Select("SELECT * FROM VOUCHER WHERE ID = #{id}")
    Voucher findById(@Param("id") Long id);


    @Delete("DELETE FROM VOUCHER WHERE ID=#{id}")
    void deleteById(@Param("id") Long id);

    @SelectProvider(type=VoucherSqlProvider.class,
            method="listPage")
    List<Voucher> listPage(@Param("curtainId") Long curtainId,
                           @Param("startDate") String startDate,
                           @Param("endDate") String endDate,
                           @Param("startCode") Long startCode,
                           @Param("endCode") Long endCode,
                           @Param("zhiName") String zhiName,
                           @Param("shenName") String shenName,
                           @Param("status") String status,
                           @Param("sysCode") String sysCode,
                           @Param("pageIndex") Integer pageIndex,
                           @Param("pageSize") Integer pageSize);

    @SelectProvider(type=VoucherSqlProvider.class,
            method="listPageTotal")
    Long listPageTotal(@Param("curtainId") Long curtainId,
                       @Param("startDate") String startDate,
                       @Param("endDate") String endDate,
                       @Param("startCode") Long startCode,
                       @Param("endCode") Long endCode,
                       @Param("zhiName") String zhiName,
                       @Param("shenName") String shenName,
                       @Param("status") String status,
                       @Param("sysCode") String sysCode);

    @Select("SELECT * FROM VOUCHER WHERE CODE = #{code} AND CURTAINID = #{curtainId} AND CREATEDATE >= #{startDate} AND CREATEDATE<= #{endDate} ")
    Voucher getByCodeAndMonth(@Param("code")Long code,
                              @Param("curtainId")Long curtainId,
                              @Param("startDate")Date startDate,
                              @Param("endDate") Date endDate);

    @Select("SELECT * FROM VOUCHER WHERE CURTAINID = #{curtainId} AND SYSCODE = #{sysCode} ")
    List<Voucher> getListBySysCode(@Param("sysCode")String sysCode, @Param("curtainId")Long curtainId);

    @Select("SELECT * FROM VOUCHER WHERE ZHINAME = #{zhidan} AND CURTAINID = #{curtainId} AND CREATEDATE >= #{startDate} AND CREATEDATE<= #{endDate} ")
    List<Voucher> getByZhiName(@Param("zhidan")String zhidan,
                               @Param("curtainId")Long curtainId,
                               @Param("startDate")Date startDate,
                               @Param("endDate") Date endDate);

    @Select("SELECT * FROM VOUCHER WHERE VOUCHERSTATUS = #{status} AND CURTAINID = #{curtainId} AND CREATEDATE >= #{startDate} AND CREATEDATE<= #{endDate} ")
    List<Voucher> getByStatus(@Param("status")String status,
                              @Param("curtainId")Long curtainId,
                              @Param("startDate")Date startDate,
                              @Param("endDate") Date endDate);
}
