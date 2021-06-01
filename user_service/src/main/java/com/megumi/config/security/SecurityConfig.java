package com.megumi.config.security;

import com.megumi.service.impl.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CsrfSecurityConfig csrfSecurityConfig;
    private final JwtFilter jwtFilter;
    private final CustomUserDetailService userDetailService;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    public SecurityConfig(CsrfSecurityConfig csrfSecurityConfig, JwtFilter jwtFilter, CustomUserDetailService userDetailService, CustomAuthenticationFailureHandler customAuthenticationFailureHandler, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.csrfSecurityConfig = csrfSecurityConfig;
        this.jwtFilter = jwtFilter;
        this.userDetailService = userDetailService;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().requireCsrfProtectionMatcher(csrfSecurityConfig).and()
                .authorizeRequests()
                .antMatchers("/identity/login","/identity/email/login").permitAll()
                .antMatchers("/identity/register", "/msg/mail/register", "/msg/mail/pwd/change", "/identity/email/pwd/change").permitAll()
                .anyRequest()
                .authenticated();
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().authenticationEntryPoint(customAuthenticationFailureHandler).accessDeniedHandler(customAccessDeniedHandler);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers("/photo/map/**");
    }

}
