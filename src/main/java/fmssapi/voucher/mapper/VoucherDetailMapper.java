package fmssapi.voucher.mapper;

import fmssapi.subject.model.Subject;
import fmssapi.voucher.model.VoucherDetail;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-21 上午9:51
 */
@Mapper
public interface VoucherDetailMapper {
    @Select("SELECT * FROM VOUCHERDETAIL")
    List<Subject> findAll();

    @Insert("INSERT INTO VOUCHERDETAIL(SUBJECT_ID, VOUCHER_ID, DEBIT, CREDIT, DESCRIPTION) VALUES(#{subject.id}, #{voucher.id}, #{debit}, #{credit}, #{description})")
    @Options(useGeneratedKeys = true,keyColumn = "ID",keyProperty="id")
    int insertByObject(VoucherDetail voucherDetail);

    @Update("UPDATE VOUCHERDETAIL SET CREDIT=#{credit},DEBIT=#{debit},SUBJECT_ID=#{subject.id},DESCRIPTION=#{description} WHERE ID=#{id}")
    void updateByObject(VoucherDetail voucherDetail);

    @Select("SELECT * FROM VOUCHERDETAIL WHERE ID = #{id}")
    Subject findById(@Param("id") Long id);


    @Delete("DELETE FROM VOUCHERDETAIL WHERE ID=#{id}")
    void deleteById(@Param("id") Long id);

    @Select("SELECT * FROM VOUCHERDETAIL WHERE VOUCHER_ID = #{voucherId}")
    @Results({
            //查询关联对象
            @Result(property = "subject",
                    column = "SUBJECT_ID",
                    one = @One(select = "fmssapi.subject.mapper.SubjectMapper.findById"))
    })
    List<VoucherDetail> getVoucherDetails(Long voucherId);

    @Select("SELECT ID FROM VOUCHERDETAIL WHERE VOUCHER_ID = #{voucherId}")
    List<Long> getIdsByVoucherId(Long voucherId);

    @SelectProvider(type=VoucherDetailSqlProvider.class,
            method="listPage")
    @Results({
            //查询关联对象
            @Result(property = "voucher",
                    column = "VOUCHER_ID",
                    one = @One(select = "fmssapi.voucher.mapper.VoucherMapper.findById")),
            @Result(property = "subject",
                    column = "SUBJECT_ID",
                    one = @One(select = "fmssapi.subject.mapper.SubjectMapper.findById"))
    })
    List<VoucherDetail> listPage(@Param("curtainId")Long curtainId,
                                 @Param("startDate")String startDate,
                                 @Param("endDate")String endDate,
                                 @Param("subjectCode")String subjectCode,
                                 @Param("pageIndex")Integer pageIndex,
                                 @Param("pageSize")Integer pageSize);

    @SelectProvider(type=VoucherDetailSqlProvider.class,
            method="listPageTotal")
    Long listPageTotal(@Param("curtainId")Long curtainId,
                       @Param("startDate")String startDate,
                       @Param("endDate")String endDate,
                       @Param("subjectCode")String subjectCode);


    @Select("SELECT d.* FROM VOUCHERDETAIL d,VOUCHER v WHERE d.VOUCHER_ID = v.ID AND v.CURTAINID=#{curtainId} AND v.CREATEDATE>=#{date1} AND v.CREATEDATE<=#{date2}")
    @Results({
            @Result(property = "subject.id",
                    column = "SUBJECT_ID")
    })
    List<VoucherDetail> getMonthDetails(@Param("curtainId")Long curtainId,
                                        @Param("date1")Date date1,
                                        @Param("date2")Date date2);

    @Select("SELECT d.* FROM VOUCHERDETAIL d,VOUCHER v,Subject b WHERE d.VOUCHER_ID = v.ID AND v.CURTAINID=#{curtainId} AND d.SUBJECT_ID=b.ID AND b.CODE like '5%' AND v.CREATEDATE>=#{date1} AND v.CREATEDATE<=#{date2}")
    @Results({
            @Result(property = "subject.id",
                    column = "SUBJECT_ID")
    })
    List<VoucherDetail> get5MonthDetails(@Param("curtainId")Long curtainId,
                                         @Param("date1")Date date1,
                                         @Param("date2")Date date2);

    @Delete("DELETE FROM VOUCHERDETAIL WHERE VOUCHER_ID=#{id}")
    void deleteByVoucherId(Long id);
}
