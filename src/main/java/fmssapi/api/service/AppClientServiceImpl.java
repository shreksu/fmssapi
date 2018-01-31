package fmssapi.api.service;

import com.alibaba.fastjson.JSON;
import fmssapi.api.mapper.AppClientMapper;
import fmssapi.api.model.AppClient;
import fmssapi.util.StringEncypyt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-25 下午2:31
 */
@Service
@Transactional
public class AppClientServiceImpl implements  AppClientService{

    @Autowired
    AppClientMapper appClientMapper;

    @Override
    public boolean checkLegal(String data, String ak) {
        Map map = (Map) JSON.parse(data);
        if(map==null || map.size()==0 || map.get("appId")==null) {
            return false;
        }
        String appId = (String) map.get("appId");
        if(!StringUtils.isEmpty(appId)) {
            AppClient appClient = appClientMapper.findByAppId(appId);
            if (appClient != null) {
                String screctKey = appClient.getSecretKey();
                String str = StringEncypyt.sign(map, screctKey);//签名
                if (ak != null && ak.equals(str)) { // 签名验证通过
                    return true;
                }
            }
        }
        return false;
    }
}
