package com.sora.controller;

import com.sora.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@Slf4j
public class TestController {

    @GetMapping("/admin/hello")
    public String getAdmin(){
        return "/admin/hello";
    }



    @GetMapping("/user/hello")
    public String getUserById(){
        return "/user/hello";
    }



    @GetMapping("/guest/hello")
    public String getUser4(){
        return "/guest/hello";
    }

    @GetMapping("/hello")
    public String getTest(){
        return "/hello";
    }
}
