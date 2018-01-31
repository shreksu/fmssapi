package fmssapi.auth;

import fmssapi.manager.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-07 上午10:42
 */
public class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getLoginName(),
                user.getPassword(),
                user.getName(),
                user.getRoleCode(),
                mapToGrantedAuthorities(user.getRoleCode())
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(String roleCode) {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));
        list.add(new SimpleGrantedAuthority(roleCode));
        return list;
    }

}
