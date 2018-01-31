package fmssapi.voucher.mapper;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-24 上午11:13
 */
public class VoucherSqlProvider {

    //Long curtainId, String startDate, String endDate, Long startCode, Long endCode, String zhiName, String shenName, String status, Integer page
    public String listPage(Map<String, Object> map)
    {
        StringBuffer sb = new StringBuffer("");
        if(map.get("curtainId")!=null){
            sb.append("CURTAINID=" + map.get("curtainId"));
        }
        if(!StringUtils.isEmpty(map.get("startDate"))){
            sb.append(" AND CREATEDATE>='" + map.get("startDate")+"'");
        }
        if(!StringUtils.isEmpty(map.get("endDate"))){
            sb.append(" AND CREATEDATE<='" + map.get("endDate")+"'");
        }
        if(map.get("startCode")!=null){
            sb.append(" AND CODE>=" + map.get("startCode"));
        }
        if(map.get("endCode")!=null){
            sb.append(" AND CODE<=" + map.get("endCode"));
        }
        if(!StringUtils.isEmpty(map.get("zhiName"))){
            sb.append(" AND ZHINAME='" + map.get("zhiName")+"'");
        }
        if(!StringUtils.isEmpty(map.get("shenName"))){
            sb.append(" AND SHENNAME='" + map.get("shenName")+"'");
        }
        if(!StringUtils.isEmpty(map.get("status"))){
            sb.append(" AND VOUCHERSTATUS='" + map.get("status")+"'");
        }
        if(!StringUtils.isEmpty(map.get("sysCode"))){
            sb.append(" AND SYSCODE='" + map.get("sysCode")+"'");
        }
        sb.append(" ORDER BY CODE");
        if(map.get("pageIndex") != null){
            sb.append(" Limit " + map.get("pageIndex") + "," + map.get("pageSize"));
        }
        return "SELECT * FROM VOUCHER WHERE " + sb.toString();
    }

    public String listPageTotal(Map<String, Object> map)
    {
        StringBuffer sb = new StringBuffer("");
        if(map.get("curtainId")!=null){
            sb.append("CURTAINID=" + map.get("curtainId"));
        }
        if(!StringUtils.isEmpty(map.get("startDate"))){
            sb.append(" AND CREATEDATE>='" + map.get("startDate")+"'");
        }
        if(!StringUtils.isEmpty(map.get("endDate"))){
            sb.append(" AND CREATEDATE<='" + map.get("endDate")+"'");
        }
        if(map.get("startCode")!=null){
            sb.append(" AND CODE>=" + map.get("startCode"));
        }
        if(map.get("endCode")!=null){
            sb.append(" AND CODE<=" + map.get("endCode"));
        }
        if(!StringUtils.isEmpty(map.get("zhiName"))){
            sb.append(" AND ZHINAME='" + map.get("zhiName")+"'");
        }
        if(!StringUtils.isEmpty(map.get("shenName"))){
            sb.append(" AND SHENNAME='" + map.get("shenName")+"'");
        }
        if(!StringUtils.isEmpty(map.get("status"))){
            sb.append(" AND VOUCHERSTATUS='" + map.get("status")+"'");
        }
        if(!StringUtils.isEmpty(map.get("sysCode"))){
            sb.append(" AND SYSCODE='" + map.get("sysCode")+"'");
        }
        return "SELECT count(*) FROM VOUCHER WHERE " + sb.toString();
    }
}
