package fmssapi.manager.service;

import com.mysql.jdbc.StringUtils;
import fmssapi.manager.mapper.UserMapper;
import fmssapi.manager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-08 下午12:18
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> findAll(String comFlag,Long curtainId) {
        if(!StringUtils.isNullOrEmpty(comFlag)) {
            return userMapper.findAll(comFlag);
        }else if(curtainId!=null){
            return userMapper.findByCurtainId(curtainId);
        }
        return null;
    }

    @Override
    public String add(User user) {
        User obj = userMapper.findById(user.getLoginName());
        if(obj == null){//新增
            user.setCreateDate(new Date());
            user.setState("Y");
            //加密密码
            BCryptPasswordEncoder encoder =new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
            userMapper.insertByObject(user);
        }else{
            userMapper.updateByObject(user);
        }
        return "0";
    }

    @Override
    public User findById(String loginName) {
        return userMapper.findById(loginName);
    }

    @Override
    public String deleteById(String loginName) {
        userMapper.deleteById(loginName);
        return "0";
    }

    @Override
    public String updatePwd(String loginName, String pwd) {
        //加密密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        pwd = encoder.encode(pwd);
        userMapper.updatePwd(loginName,pwd);
        return "0";
    }


}
