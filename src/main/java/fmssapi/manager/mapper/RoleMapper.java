package fmssapi.manager.mapper;

import fmssapi.manager.model.Role;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-08 上午11:14
 */
@Mapper
public interface RoleMapper {

    @Insert("INSERT INTO ROLE(CODE, NAME) VALUES(#{code}, #{name})")
    int insertByObject(Role role);

    @Select("SELECT * FROM ROLE WHERE CODE<>'ROLE_ADMIN' ")
    List<Role> findAll();

}
