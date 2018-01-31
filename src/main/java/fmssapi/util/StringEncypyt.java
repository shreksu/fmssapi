package fmssapi.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * sha签名
 * @author suyuanyang
 * @create 2017-12-25 下午2:07
 */
public class StringEncypyt {

    /**
     * 签名
     * @param map 带签名数据
     * @param secretKey 签名密文
     * @return
     */
    public static String sign(Map map,String secretKey){
        //对body里参数按照键名正排序(TreeMap)
        map =sortMap(map);
        StringBuffer paramsStr = new StringBuffer();
        // 1.根据排序后的key，拼接value字符串
        for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Object> entry = it.next();
            Object value = entry.getValue();
            if (value != null) {
                paramsStr.append(value.toString());
            }
        }
        paramsStr.append(secretKey);
        return StringEncypyt.encrypt(paramsStr.toString(),null);
    }

    /**
     * 把数组所有元素排序
     * @param map 原map
     * @return 排序后的map
     * @author suyuanyang
     */
    private static Map<String, Object> sortMap(Map<String, Object> map){
        Map<String, Object> sortMap = new TreeMap<>();
        sortMap.putAll(map);
        return sortMap;
    }

    public static String encrypt(String strSrc, String encName) {
        MessageDigest md;
        String strDes;
        try {
            byte[] bt = strSrc.getBytes("UTF-8");
            if (encName == null || encName.equals("")) {
                encName = "SHA-256";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
            //System.out.println(strDes);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return strDes;
    }

    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }
}
