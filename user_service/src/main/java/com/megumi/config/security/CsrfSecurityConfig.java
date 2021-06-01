package com.megumi.config.security;

import com.megumi.common.StringUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Component
public class CsrfSecurityConfig implements RequestMatcher {

    private final Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

    @Override
    public boolean matches(HttpServletRequest request) {
        var token = request.getHeader("token");
        if (StringUtils.isValid(token)) {
            return false;
        }
        var excludeUrls = new ArrayList<String>();
        excludeUrls.add("/identity/login");//允许post请求的url路径，这只是简单测试，具体要怎么设计这个csrf处理，看个人爱好
        excludeUrls.add("/identity/register");
        excludeUrls.add("/msg/mail/register");
        excludeUrls.add("/msg/mail/pwd/change");
        excludeUrls.add("/identity/email/pwd/change");
        excludeUrls.add("/identity/email/login");

        if (!excludeUrls.isEmpty()) {
            var servletPath = request.getServletPath();
            for (String url : excludeUrls) {
                if (servletPath.contains(url)) {
                    return false;
                }
            }
        }
        return !allowedMethods.matcher(request.getMethod()).matches();
    }
}

