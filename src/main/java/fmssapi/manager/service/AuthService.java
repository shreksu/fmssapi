package fmssapi.manager.service;

import fmssapi.auth.JwtAuthenticationResponse;

/**
 * @author suyuanyang
 * @create 2017-12-07 下午9:51
 */

public interface AuthService {
    JwtAuthenticationResponse login(String username, String password);
    JwtAuthenticationResponse refresh(String oldToken);
}
