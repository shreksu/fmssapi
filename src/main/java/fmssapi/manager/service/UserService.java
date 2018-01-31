package fmssapi.manager.service;

import fmssapi.manager.model.User;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-08 下午12:18
 */
public interface UserService {

    List<User> findAll(String comFlag,Long curtainId);

    String add(User user);

    User findById(String loginName);

    String deleteById(String loginName);

    String updatePwd(String loginName,String pwd);

}
