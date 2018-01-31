package fmssapi.api.mapper;

import fmssapi.api.model.AppClient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author suyuanyang
 * @create 2017-12-25 下午2:30
 */
@Mapper
public interface AppClientMapper {

    @Select("SELECT * FROM APPCLIENT WHERE APPID = #{appId}")
    AppClient findByAppId(@Param("appId") String appId);
}
