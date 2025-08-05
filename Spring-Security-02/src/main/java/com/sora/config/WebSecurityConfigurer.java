package com.sora.config;

import com.sora.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final MyUserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfigurer(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
//        userDetailsManager.createUser(User.withUsername("root").password("{noop}123456").roles("admin").build());
//        return userDetailsManager;
//    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/index/*").permitAll()
                .mvcMatchers("/login.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
                .successHandler(new MyAuthenticationSuccessHandler())
                .failureHandler(new MyAuthenticationFailureHandler())
                .and()
                .logout()   //get请求  /logout
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/fuckoff","GET")
                ))
//                .invalidateHttpSession(true)  //default
//                .clearAuthentication(true)  //default
//                .logoutSuccessUrl("/login.html")  //default
                .and().csrf().disable();
    }



}
