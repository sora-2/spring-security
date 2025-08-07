package com.sora.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sora.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.EventListener;
import java.util.HashMap;

@Configuration
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {


    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

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
        loginFilter.setAuthenticationManager(authenticationManager());
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
    private FindByIndexNameSessionRepository  sessionRegistry;


    @Bean
    public SpringSessionBackedSessionRegistry  registry() {
        return new SpringSessionBackedSessionRegistry(sessionRegistry);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
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
                })
                .sessionRegistry(registry()) //  session管理
                .maxSessionsPreventsLogin(true);//  限制同一账号只能登录一个地方,后来的不能登录
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

//    public RememberMeServices rememberMeServices() {
//        return new PersistentTokenBasedRememberMeServices(UUID.randomUUID().toString(), userDetailsService, new InMemoryTokenRepositoryImpl());
//    }


}
