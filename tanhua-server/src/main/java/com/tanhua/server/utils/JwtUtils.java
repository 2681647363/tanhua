package com.tanhua.server.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${tanhua.secret}")
    private String secret;

    /**
     * 生成JWT
     *
     * @return
     */
    public String createJWT(String phone,Long userId) {
        // 存入token的信息，方便以后使用（用户操作时获取用户的信息）
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("mobile", phone);
        claims.put("id", userId);
        // 获取系统时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
                // 签发的时间
            .signWith(SignatureAlgorithm.HS256, secret);
        // 生成token
        return builder.compact();
    }
}