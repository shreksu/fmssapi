package fmssapi.manager.action;

import fmssapi.manager.model.Role;
import fmssapi.manager.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-08 上午11:58
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;

    //获得角色备选项
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<Role> findAll(){
        return roleService.findAll();
    }
}
