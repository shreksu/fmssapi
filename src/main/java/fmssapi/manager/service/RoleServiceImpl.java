package fmssapi.manager.service;

import fmssapi.manager.mapper.RoleMapper;
import fmssapi.manager.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-08 下午12:02
 */
@Service
@Transactional
public class RoleServiceImpl implements  RoleService{

    @Autowired
    RoleMapper roleMapper;

    @Override
    public List<Role> findAll() {
        return roleMapper.findAll();
    }
}
