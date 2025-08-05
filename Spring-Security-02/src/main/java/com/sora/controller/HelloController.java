package com.sora.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/test")
    public String getTest(){
        SecurityContext context = SecurityContextHolder.getContext();
        User user = (User) context.getAuthentication().getPrincipal();
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        System.out.println(user.getAuthorities());

        new Thread(() -> {  //默认MODE 无法访问  改为->InheritableThreadLocal
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
        });
        return "test";
    }
}
