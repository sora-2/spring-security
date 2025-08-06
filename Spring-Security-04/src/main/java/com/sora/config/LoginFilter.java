package com.sora.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sora.exception.KaptchaNotMatchException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    public String getKaptchaParameter() {
        return kaptchaParameter;
    }

    public void setKaptchaParameter(String kaptchaParameter) {
        this.kaptchaParameter = kaptchaParameter;
    }

    private String kaptchaParameter = "kaptcha";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("method not support");
        }

        try {
            Map<String,String> userInfo = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            String kapt = userInfo.get(getKaptchaParameter());
            String kaptcha = (String) request.getSession().getAttribute("kaptcha");
            String username = userInfo.get(getUsernameParameter());
            String password = userInfo.get(getPasswordParameter());
            if (!ObjectUtils.isEmpty(kaptcha) && !ObjectUtils.isEmpty(kapt) && kapt.equals(kaptcha)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
                setDetails(request,usernamePasswordAuthenticationToken);
                return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
            }
            throw new KaptchaNotMatchException("验证码错误");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
