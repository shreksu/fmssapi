package fmssapi.voucher.mapper;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-24 上午11:13
 */
public class VoucherDetailSqlProvider {

    //Long curtainId, String startDate, String endDate, Long subjectId, int pageIndex, int pageSize
    public String listPage(Map<String, Object> map)
    {
        String select_sql = "SELECT l.* FROM VOUCHERDETAIL l,VOUCHER v";
        String where_sql = " WHERE l.VOUCHER_ID=v.id";
        StringBuffer sb = new StringBuffer("");
        if(map.get("curtainId")!=null){
            sb.append(" AND v.CURTAINID=" + map.get("curtainId"));
        }
        if(!StringUtils.isEmpty(map.get("startDate"))){
            sb.append(" AND v.CREATEDATE>='" + map.get("startDate")+"'");
        }
        if(!StringUtils.isEmpty(map.get("endDate"))){
            sb.append(" AND v.CREATEDATE<='" + map.get("endDate")+"'");
        }
        if(!StringUtils.isEmpty(map.get("subjectCode"))){
            select_sql += ",SUBJECT s";
            where_sql += " and l.SUBJECT_ID=s.ID";
            sb.append(" AND s.CODE LIKE '" + map.get("subjectCode")+"%'" );
        }

        sb.append(" ORDER BY v.CODE");
        if(map.get("pageIndex")!=null){
            sb.append(" Limit " + map.get("pageIndex") + "," + map.get("pageSize"));
        }
        String sql = select_sql + where_sql + sb.toString();
        return sql;
    }

    public String listPageTotal(Map<String, Object> map)
    {
        String select_sql = "SELECT count(l.ID) FROM VOUCHERDETAIL l,VOUCHER v";
        String where_sql = " WHERE l.VOUCHER_ID=v.ID";
        StringBuffer sb = new StringBuffer("");
        if(map.get("curtainId")!=null){
            sb.append(" AND v.CURTAINID=" + map.get("curtainId"));
        }
        if(!StringUtils.isEmpty(map.get("startDate"))){
            sb.append(" AND v.CREATEDATE>='" + map.get("startDate")+"'");
        }
        if(!StringUtils.isEmpty(map.get("endDate"))){
            sb.append(" AND v.CREATEDATE<='" + map.get("endDate")+"'");
        }
        if(!StringUtils.isEmpty(map.get("subjectCode"))){
            select_sql += ",SUBJECT s";
            where_sql += " and l.SUBJECT_ID=s.ID";
            sb.append(" AND s.CODE LIKE '" + map.get("subjectCode")+"%'" );
        }

        String sql = select_sql + where_sql + sb.toString();
        return sql;
    }
}
