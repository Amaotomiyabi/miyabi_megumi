package com.megumi.util;

import com.auth0.jwt.JWT;
import com.megumi.common.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.message.AuthException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 2021/2/23
 *
 * @author miyabi
 * @since 1.0
 */
public class RequestHolderUtil {

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) (Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getRequest();
    }

    public static String getToken() throws AuthException {
        var request = getRequest();
        String token;
        if (StringUtils.isValid(token = request.getHeader("token"))) {
            return token;
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equalsIgnoreCase("token")) {
                    if (StringUtils.isValid(cookie.getValue())) {
                        return cookie.getValue();
                    }
                }
            }
        }
        throw new AuthException("身份令牌不存在");
    }

    public static Long getUserId(String token) {
        return JWT.decode(token).getClaim("id").asLong();
    }

    public static Long getUserId() throws AuthException {
        return JWT.decode(getToken()).getClaim("id").asLong();
    }
}
