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


    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin') and authentication.name = 'root'" )
//    @PreAuthorize("hasAuthority('read')" )
    public String getAdmin(){
        return "admin";
    }

    @GetMapping("/admin")
    @PreAuthorize("authentication.name ==#name" )
    public String getAdmin(String name){
        return "admin";
    }

    @GetMapping("/users")
    @PreFilter(value = "filterObject.id%2!=1",filterTarget = "userList")
    public String addUsers(@RequestBody List<User> userList){
        return "user";
    }

    @GetMapping("/user")
    @PreAuthorize("returnObject.id==1")
    public String getUserById(Integer id){
        return "user";
    }

    @GetMapping("/getUsers")
    @PostFilter(value = "filterObject.id%2==1")
    public String getUsers(){
        return "user";
    }

    @GetMapping("/user1")
    @Secured({"admin","user"})
    public String getUser1(){
        return "user";
    }

    @GetMapping("/user2")
    @DenyAll
    public String getUser2(){
        return "user";
    }

    @GetMapping("/user3")
    @PermitAll
    public String getUser3(){
        return "user";
    }

    @GetMapping("/user4")
    @RolesAllowed("ROLE_ADMIN")
    public String getUser4(){
        return "user";
    }

    @GetMapping("/test")
    public String getTest(){
        return "test";
    }
}
