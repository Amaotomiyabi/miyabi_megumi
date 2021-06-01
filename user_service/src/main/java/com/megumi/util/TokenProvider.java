package com.megumi.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.HashMap;

import static com.megumi.common.Const.SECRET;

public class TokenProvider {
//
//    public String createToken(UserDetails userDetails) {
//        var map = new HashMap<String, Object>(1);
//        map.put("alg", "HS256");
//        var sb = new StringBuilder();
//        userDetails.getAuthorities().forEach(role -> sb.append(role.getAuthority()).append(','));
//        var roles = "";
//        if (!userDetails.getAuthorities().isEmpty()) {
//            roles = sb.deleteCharAt(sb.length() - 1).toString();
//        }
//        var token = JWT.create().withHeader(map)
//                .withClaim("id", userDetails.getUsername())
//                .withClaim("auth", roles)
//                .sign(Algorithm.HMAC256(SECRET));
//        var val = new HashMap<String, Object>();
//        val.put("id", userDetails.getUsername());
//        val.put("role", roles);
//            userRepo.updateUserLoginTime(LocalDateTime.now(), user.getId());
//        redisTemplate.opsForHash().putAll(token, val);
//        return token;
//    }
}
