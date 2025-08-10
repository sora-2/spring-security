package com.sora.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sora.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.UrlAuthorizationConfigurer;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true,  securedEnabled = true,jsr250Enabled = true)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {


    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setFilterProcessesUrl("/doLogin"); // 自定义登录接口路径
        loginFilter.setUsernameParameter("username");
        loginFilter.setPasswordParameter("password");
        loginFilter.setAuthenticationSuccessHandler((request,response,authentication)->{
            HashMap<String,Object> result = new HashMap<>();
            result.put("code",200);
            result.put("msg","login successfully");
            result.put("authentication",authentication);
            response.setContentType("application/json;charset=UTF-8");
            String s = new ObjectMapper().writeValueAsString(result);
            response.getWriter().println(s);
        });
        loginFilter.setAuthenticationFailureHandler((request,response,exception)->{
            HashMap<String,Object> result = new HashMap<>();
            result.put("code",500);
            result.put("msg","login failure");
            result.put("exception",exception);
            response.setContentType("application/json;charset=UTF-8");
            String s = new ObjectMapper().writeValueAsString(result);
            response.getWriter().println(s);
        });
        loginFilter.setAuthenticationManager(super.authenticationManagerBean());
        return loginFilter;
    }

    private final MyUserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfigurer(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService);
    }


    @Resource
    private CustomSecurityMetaSource customSecurityMetaSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ApplicationContext sharedObject = http.getSharedObject(ApplicationContext.class);
        http.apply( new UrlAuthorizationConfigurer<>(sharedObject))
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setSecurityMetadataSource(customSecurityMetaSource);
                        object.setRejectPublicInvocations(false);
                        return object;
                    }
                });

        http.authorizeRequests()
                .mvcMatchers("/vc.jpg").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .rememberMe()
//                .alwaysRemember(true)
//                .rememberMeServices(rememberMeServices())
                .tokenRepository(persistentTokenRepository())
                .and()
                .logout()
                .logoutSuccessHandler((request, response, authentication) ->{
                    HashMap<String,Object> result = new HashMap<>();
                    result.put("code",200);
                    result.put("msg","logout successfully");
                    result.put("authentication",authentication);
                    response.setContentType("application/json;charset=UTF-8");
                    String s = new ObjectMapper().writeValueAsString(result);
                    response.getWriter().println(s);
                })
                .and().csrf().disable()
                .sessionManagement()
                .maximumSessions(1)
                .expiredSessionStrategy(session ->{   //挤下线处理方式
                    HttpServletResponse response = session.getResponse();
                    HashMap<String,Object> result = new HashMap<>();
                    result.put("code",501);
                    result.put("msg","another user login");
                    response.setContentType("application/json;charset=UTF-8");
                    String s = new ObjectMapper().writeValueAsString(result);
                    response.getWriter().println(s);
                });
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Resource
    private DataSource dateSource;

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dateSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }



}
