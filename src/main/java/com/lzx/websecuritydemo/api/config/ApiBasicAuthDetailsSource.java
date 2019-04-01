package com.lzx.websecuritydemo.api.config;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class ApiBasicAuthDetailsSource extends WebAuthenticationDetailsSource {
    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context){
        return new ApiBasicAuthDetails(context);
    }
    public static class ApiBasicAuthDetails extends WebAuthenticationDetails{
        private static final long serialVersionUID = -4527763456126240806L;

        ApiBasicAuthDetails(HttpServletRequest request){
            super(request);
        }
    }
}
