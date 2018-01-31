package fmssapi.manager.action;

import fmssapi.manager.model.User;
import fmssapi.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-08 下午12:17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 获得用户列表
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> findAll(String comFlag, Long curtainId){
        return userService.findAll(comFlag,curtainId);
    }

    /**
     *
     * @param user
     * @return errno:0(成功)，1(用户已存在)
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Map<String,String> add(@RequestBody User user){
        Map<String,String> result = new HashMap<>();
        result.put("errno",userService.add(user));
        return result;
    }

    /**
     * 查找用户名是否存在
     * @param loginName
     * @return
     */
    @RequestMapping(value = "findById", method = RequestMethod.GET)
    public Map<String,String> findById(String loginName) {
        Map<String, String> result = new HashMap<>();
        result.put("errno", userService.findById(loginName)==null?"0":"1");
        return result;
    }

    /**
     * 删除用户
     * @param loginName
     * @return
     */
    @RequestMapping(value = "deleteById")
    public Map<String,String> deleteById(@RequestParam String loginName) {
        Map<String, String> result = new HashMap<>();
        result.put("errno", userService.deleteById(loginName));
        return result;
    }

    /**
     * 修改密码
     * @param map
     * @return
     */
    @RequestMapping(value = "updatePwd", method = RequestMethod.POST)
    public Map<String,String> updatePwd(@RequestBody Map<String,String> map) {
        Map<String, String> result = new HashMap<>();
        result.put("errno", userService.updatePwd(map.get("loginName"), map.get("pwd")));
        return result;
    }


}
