package com.megumi.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.megumi.common.Const;
import com.megumi.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public AuthGlobalFilter(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var req = exchange.getRequest();
        var path = req.getURI().getPath();
        if (antPathMatcher.match("/login/**", path)) {
            return chain.filter(exchange);
        }
        if (antPathMatcher.match("/api/**", path)) {
            System.out.println("token : " + req.getHeaders().getFirst("token"));
            return chain.filter(exchange);
        }
        var token = req.getHeaders().getFirst("token");
        if (!StringUtils.isValid(token)) {
            var cookie = req.getCookies().getFirst("token");
            if (cookie != null) {
                token = cookie.getValue();
            }
        }
        if (!StringUtils.isValid(token)) {
            return out(exchange.getResponse(), "无身份认证信息");
        }


        try {
            var verifier = JWT.require(Algorithm.HMAC256(Const.SECRET)).build();
            verifier.verify(token);
            if (Objects.equals(redisTemplate.hasKey(token), Boolean.FALSE)) {
                return out(exchange.getResponse(), "身份信息已过期");
            }
        } catch (Exception e) {
            return out(exchange.getResponse(), "身份信息错误");
        }
        req.mutate().header("token", token);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> out(ServerHttpResponse response, String msg) {
        response.setStatusCode(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(msg.getBytes(StandardCharsets.UTF_8))));
    }
}
