package fmssapi.voucher.model;

/**
 * @author suyuanyang
 * @create 2017-12-24 上午9:35
 */
public enum VoucherStatus {


    C {public String getName(){
        return "未审核";
    }},
    G {public String getName(){
        return "已审核";
    }};
    public abstract String getName();

}
