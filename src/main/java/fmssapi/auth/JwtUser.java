package fmssapi.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author suyuanyang
 * @create 2017-12-07 上午10:35
 */
public class JwtUser implements UserDetails {

    private final String username;
    private final String password;
    private final String name;
    private final String role;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUser(
            String username,
            String password,
            String name,
            String role,
            Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.authorities = authorities;
    }
    //返回分配给用户的角色列表
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }
    public String getRole() {
        return role;
    }
    // 账户是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    // 账户是否未锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    // 密码是否未过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    // 账户是否激活
    @Override
    public boolean isEnabled() {
        return true;
    }

}
