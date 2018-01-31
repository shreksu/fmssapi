package fmssapi.auth;

import java.io.Serializable;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-07 下午9:55
 */
public class JwtAuthenticationResponse implements Serializable{
    private static final long serialVersionUID = 1250166508152483573L;

    private final String token;

    private final Map<String,String> user;

    public JwtAuthenticationResponse(String token,Map<String,String> user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return this.token;
    }

    public Map<String,String> getUser() { return this.user; }
}
