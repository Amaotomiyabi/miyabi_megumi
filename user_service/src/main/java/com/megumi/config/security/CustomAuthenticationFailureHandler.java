package com.megumi.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megumi.common.ResultBody;
import com.megumi.common.StateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Autowired
    public CustomAuthenticationFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        var out = httpServletResponse.getOutputStream();
        var result = new ResultBody<>(StateCode.ACCESS_ERR, "身份验证失败");
        out.write(objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }
}
