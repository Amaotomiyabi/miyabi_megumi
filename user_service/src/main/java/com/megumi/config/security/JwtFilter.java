package com.megumi.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.megumi.common.Const;
import com.megumi.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class JwtFilter extends OncePerRequestFilter {


    private final RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public JwtFilter(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @SuppressWarnings({"NullableProblems", "unchecked"})
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        var token = httpServletRequest.getHeader("token");
        if (!StringUtils.isValid(token)) {
            if (httpServletRequest.getCookies() != null) {
                for (Cookie cookie : httpServletRequest.getCookies()) {
                    if (cookie.getName().equalsIgnoreCase("token")) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }
        if (StringUtils.isValid(token)) {
            var jwtVerifier = JWT.require(Algorithm.HMAC256(Const.SECRET)).build();
            var decodeJWT = jwtVerifier.verify(token);
            var id = decodeJWT.getClaim("id").asLong();
            if (Objects.equals(redisTemplate.hasKey(id), Boolean.TRUE)) {
                var tokens = redisTemplate.opsForHash().get(id, "tokens");
                if (tokens != null) {
                    for (String s : ((List<String>) tokens)) {
                        if (s.equals(token)) {
                            var t=(Collection<? extends GrantedAuthority>) redisTemplate.opsForHash().get(id, "role");
                            var securityToken = new UsernamePasswordAuthenticationToken(id, null, (Collection<? extends GrantedAuthority>) redisTemplate.opsForHash().get(id, "role"));
                            SecurityContextHolder.getContext().setAuthentication(securityToken);
                        }
                    }
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
