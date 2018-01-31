package fmssapi.manager;

import fmssapi.manager.mapper.RoleMapper;
import fmssapi.manager.model.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author suyuanyang
 * @create 2017-12-08 上午11:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ROLETest {
    @Autowired
    private RoleMapper roleMapper;


    @Test
    private void add() throws Exception {
        // insert by parameter

        Role role = new Role();
        role.setCode("ROLE_ADMIN");
        role.setName("管理员");
        roleMapper.insertByObject(role);
        Role role2 = new Role();
        role2.setCode("ROLE_MA");
        role2.setName("财务主管");
        roleMapper.insertByObject(role2);
        Role role3 = new Role();
        role3.setCode("ROLE_EM");
        role3.setName("会计");
        roleMapper.insertByObject(role3);

    }
}
