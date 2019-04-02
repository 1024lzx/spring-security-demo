package com.lzx.websecuritydemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzx.websecuritydemo.objectmapper.UserObjectMapper;
import com.lzx.websecuritydemo.po.UserPO;
import com.lzx.websecuritydemo.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserObjectMapper userObjectMapper;
    private final ObjectMapper objectMapper;
    private final SessionRegistry sessionRegistry;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LimitLoginAuthenticationProvider limitLoginAuthenticationProvider;
    WebSecurityConfig(UserObjectMapper userObjectMapper,
                      SessionRegistry sessionRegistry,
                      UserDetailsService userDetailsService,
                      PasswordEncoder passwordEncoder,
                      LimitLoginAuthenticationProvider limitLoginAuthenticationProvider){
        this.userObjectMapper = userObjectMapper;
        objectMapper = new ObjectMapper();
        this.sessionRegistry = sessionRegistry;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.limitLoginAuthenticationProvider = limitLoginAuthenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception { //配置策略
        http.csrf().disable();
        http.antMatcher("/**").authorizeRequests()
                .antMatchers("/pagelogin").permitAll()
                .anyRequest().authenticated().
                and().formLogin().permitAll().successHandler(loginSuccessHandler()).failureHandler(simpleUrlAuthenticationFailureHandler()).
                and().logout().permitAll().invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessHandler(logoutSuccessHandler()).
                and().sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry);
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(limitLoginAuthenticationProvider);
        auth.eraseCredentials(false);
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler loginSuccessHandler() { //登入处理

        return new SavedRequestAwareAuthenticationSuccessHandler() {

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                /*UserPO userDetails = (UserPO) authentication.getPrincipal();
                UserVO userVO = userObjectMapper.po2vo(userDetails);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try (PrintWriter writer = response.getWriter()) {
                    objectMapper.writeValue(writer, userVO);
                }
                response.getWriter().flush();*/
                response.sendRedirect("/");
                clearAuthenticationAttributes(request);
            }
        };
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException exception) throws IOException {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"result\":\"" + exception.getMessage() + "\"}");
                response.getWriter().flush();
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() { //登出处理
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                try {
                    httpServletResponse.getWriter().write("logout success");
                } catch (Exception e) {
                    httpServletResponse.getWriter().write("logout failure");
                }
            }
        };
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                /*httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletRequest.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("application/json; charset=utf-8");
                try (PrintWriter writer = httpServletResponse.getWriter()) {
                    objectMapper.writeValue(writer, "未授权的访问");
                }*/
                httpServletResponse.sendRedirect("/pagelogin");
            }
        };
    }


}
