package fmssapi.manager;

import fmssapi.manager.mapper.UserMapper;
import fmssapi.manager.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-08 上午9:41
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

    @Autowired
    private UserMapper userMapper;



    @Test
    public void add() throws Exception {
        // insert by parameter

        User user = new User();
        user.setLoginName("jettynew");
        user.setPassword("12345678");
        user.setName("LiSitest");
        user.setComFlag("hxgj");
        user.setCreateDate(new Date());
        userMapper.insertByObject(user);

        //
        User user1 = new User();
        user1.setLoginName("suyuanyang");
        user1.setPassword("7788");
        user1.setName("LiSi5566");
        user.setComFlag("hxgj");
        userMapper.updateByObject(user1);

        //
        User aa = userMapper.findById("suyuanyang");
        System.out.print(aa.getName());

        //
        List<User> userList = userMapper.findAll("hxgj");
        System.out.print(userList.size()+"---"+userList.get(2).getCreateDate());

        //
        //List<User> userList1 = userMapper.findAllExclude();
        //System.out.print(userList1.size()+"---"+userList1.get(2).getName());
    }
}
