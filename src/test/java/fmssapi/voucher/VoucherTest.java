package fmssapi.voucher;

import fmssapi.subject.mapper.SubjectMapper;
import fmssapi.voucher.mapper.VoucherDetailMapper;
import fmssapi.voucher.mapper.VoucherMapper;
import fmssapi.voucher.model.Voucher;
import fmssapi.voucher.model.VoucherDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author suyuanyang
 * @create 2017-12-21 上午10:21
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class VoucherTest {

    @Autowired
    private VoucherMapper voucherMapper;

    @Autowired
    private VoucherDetailMapper voucherDetailMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    @Test
    public void add() throws Exception {

        Voucher voucher = new Voucher();
        voucher.setCode(1L);
        voucher.setCreateDate(new Date());
        voucher.setBillNum(2);
        voucher.setZhiName("gai");
        voucher.setCredit(100l);
        voucher.setDebit(100l);
        voucherMapper.insertByObject(voucher);

        //
        VoucherDetail voucherDetail = new VoucherDetail();
        voucherDetail.setDescription("rent");
        voucherDetail.setDebit(100l);
        voucherDetail.setCredit(100l);
        voucherDetail.setSubject(subjectMapper.findById(1l));
        voucherDetail.setVoucher(voucher);
        voucherDetailMapper.insertByObject(voucherDetail);
    }
}
