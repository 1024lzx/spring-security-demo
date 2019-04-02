package com.lzx.websecuritydemo.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzx.websecuritydemo.config.LimitLoginAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@EnableWebSecurity
@Order(1)
public class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private ApiBasicAuthDetailsSource apiBasicAuthDetailsSource;
    @Autowired
    private LimitLoginAuthenticationProvider limitLoginAuthenticationProvider;
    private final ObjectMapper mapper;

    ApiWebSecurityConfig(){
        mapper = new ObjectMapper();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception { //配置策略
        http.csrf().disable();
        http.antMatcher("/open-api/**").authorizeRequests().
                antMatchers("/**").authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().httpBasic().authenticationDetailsSource(apiBasicAuthDetailsSource);
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(limitLoginAuthenticationProvider);
        auth.eraseCredentials(false);
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletRequest.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("application/json; charset=utf-8");
                try (PrintWriter writer = httpServletResponse.getWriter()) {
                    mapper.writeValue(writer, "未授权的访问");
                }
            }
        };
    }
}

