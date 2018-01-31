package fmssapi.curtain;

import fmssapi.curtain.mapper.CurtainMapper;
import fmssapi.curtain.model.Curtain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-10 上午8:58
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CurtainTest {


    @Autowired
    private CurtainMapper curtainMapper;

    @Test
    public void add() throws Exception {
        // insert by parameter

        Curtain curtain = new Curtain();
        curtain.setComFlag("hxgj");
        curtain.setCode("02");
        curtain.setName("华夏诚信");
        curtain.setStartMonth(new Date());
        int a = curtainMapper.insertByObject(curtain);
        System.out.println(a+"---"+curtain.getId());

    }

    @Test
    public void select() throws Exception {
        // insert by parameter


        List<String> userIds = curtainMapper.getUserByCurtainId(1L);
        System.out.println(userIds.get(0));

    }
}
