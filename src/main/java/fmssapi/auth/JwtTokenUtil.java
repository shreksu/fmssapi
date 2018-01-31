package fmssapi.auth;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @author suyuanyang
 * @create 2017-12-06 下午3:44
 */
public class JwtTokenUtil {

    //密钥
    private static final String secret = "whoisyourdaddy";
    //超时时间
    private static final long ttlMillis = 1000 * 60 * 60 * 10;

    public static String generateToken(String name) {
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(name)
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 验证token
     * @param token
     * @return
     */
    public static Boolean validateToken(String token,UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        //签名是否正确
        String username;
        Claims claims = JwtTokenUtil.getClaimsFromToken(token);
        if(claims!=null) {
            username = claims.getSubject();
        }else{
            return false;
        }
        //是否过期
        boolean isTokenExpired = JwtTokenUtil.getClaimsFromToken(token).getExpiration().before(new Date());
        return (username.equals(user.getUsername()) && !isTokenExpired);
    }

    public static String generateToken(UserDetails userDetails) {
        JwtUser jwtUser = (JwtUser) userDetails;
        return generateToken(jwtUser.getUsername());
    }

    public static Boolean canTokenBeRefreshed(String token) {
        //是否过期
        boolean isTokenExpired = JwtTokenUtil.getClaimsFromToken(token).getExpiration().after(new Date());
        return !isTokenExpired;
    }

    public static String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            refreshedToken = generateToken(claims.getSubject());
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }


//    public static void main(String[] args){
//        String token = generateToken(203L,"黎明");
//        Claims claims = getClaimsFromToken((token));
//        System.out.println(token+"    "+claims.getId()+"---"+claims.getSubject());
//    }


}
