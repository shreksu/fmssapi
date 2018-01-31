package fmssapi.api.service;

/**
 * @author suyuanyang
 * @create 2017-12-25 下午2:31
 */
public interface AppClientService {
    /**
     * 检查签名是否正确
     * @param data 需要签名的数据
     * @param ak 签名
     * @return
     */
    boolean checkLegal(String data,String ak);
}
