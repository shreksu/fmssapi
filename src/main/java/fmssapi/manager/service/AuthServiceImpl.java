package fmssapi.manager.service;

import fmssapi.auth.JwtAuthenticationResponse;
import fmssapi.auth.JwtTokenUtil;
import fmssapi.auth.JwtUser;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;


    @Autowired
    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

//    @Override
//    public User register(User userToAdd) {
//        final String username = userToAdd.getLoginName();
//        if(userRepository.findByUsername(username)!=null) {
//            return null;
//        }
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        final String rawPassword = userToAdd.getPassword();
//        userToAdd.setPassword(encoder.encode(rawPassword));
//        userToAdd.setLastPasswordResetDate(new Date());
//        userToAdd.setRoles(asList("ROLE_USER"));
//        return userRepository.insert(userToAdd);
//    }

    @Override
    public JwtAuthenticationResponse login(String username, String password) {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        final String token = JwtTokenUtil.generateToken(user);
        JwtAuthenticationResponse response = new JwtAuthenticationResponse(token,this.getUserMap(user));
        return response;
    }

    @Override
    public JwtAuthenticationResponse refresh(String oldToken) {
        //String username = JwtTokenUtil.getClaimsFromToken(oldToken).getSubject();
        String username = null;
        Claims claims = JwtTokenUtil.getClaimsFromToken(oldToken);
        if(claims!=null) username = claims.getSubject();
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        if (username!=null && JwtTokenUtil.canTokenBeRefreshed(oldToken)){
            String newtoken = JwtTokenUtil.refreshToken(oldToken);
            return new JwtAuthenticationResponse(newtoken,this.getUserMap(user));
        }
        return null;
    }


    private Map<String,String> getUserMap (JwtUser jwtUser) {
        Map<String,String> userMap = new HashMap<>();
        userMap.put("username",jwtUser.getUsername());
        userMap.put("name",jwtUser.getName());
        userMap.put("role",jwtUser.getRole());
        return userMap;
    }
}
