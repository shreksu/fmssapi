package fmssapi.manager.action;

import com.mysql.jdbc.StringUtils;
import fmssapi.auth.JwtAuthenticationRequest;
import fmssapi.auth.JwtAuthenticationResponse;
import fmssapi.curtain.model.Curtain;
import fmssapi.curtain.service.CurtainService;
import fmssapi.manager.model.User;
import fmssapi.manager.service.AuthService;
import fmssapi.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.header}")
    private String tokenHeader;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private CurtainService curtainService;

    //测试服务
    @RequestMapping(value = "helloWorld", method = RequestMethod.GET)
    public String helloWorld(){
        return "helloWorld!";
    }
    //测试数据库
    @RequestMapping(value = "testMysql", method = RequestMethod.GET)
    public String testMysql(){
        User user = userService.findById("hxgj");
        return user.getName();
    }

    @RequestMapping(value = "getRoleByLoginName", method = RequestMethod.GET)
    public Map<String,Object> getRoleByLoginName(String loginName){
        Map<String,Object> result = new HashMap<>();
        if(!StringUtils.isNullOrEmpty(loginName)) {
            User user = userService.findById(loginName);
            if(user!=null) {
                List<Curtain> curtainList = curtainService.getCurtainsByUser(user);
                result.put("role", user.getRoleCode());
                result.put("curtains",curtainList);
            }else {
                result.put("role", "ROLE_USER");
            }
        }
        return result;
    }

    @RequestMapping(value = "getToken", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException{
        final JwtAuthenticationResponse response = authService.login(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        // Return the token
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "refreshToken", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(
            HttpServletRequest request) throws AuthenticationException{
        String token = request.getHeader(tokenHeader);
        JwtAuthenticationResponse response = authService.refresh(token);
        if(response == null || response.getToken() == null) {
            return ResponseEntity.badRequest().body(null);
        } else {
            return ResponseEntity.ok(response);
        }
    }

//    @RequestMapping(value = "${jwt.route.authentication.register}", method = RequestMethod.POST)
//    public User register(@RequestBody User addedUser) throws AuthenticationException{
//        return authService.register(addedUser);
//    }
}
