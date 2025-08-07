package com.sora.service;

import com.sora.entity.Role;
import com.sora.entity.User;
import com.sora.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MyUserDetailsService implements UserDetailsService, UserDetailsPasswordService {


    private final UserMapper userMapper;

    public MyUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsername(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        List<Role> roles = userMapper.getRolesById(user.getId());
        user.setRoles(roles);

        return user;
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Integer i = userMapper.updatePassword(user.getUsername(), newPassword);
        if (i==1) {
            ((User)user).setPassword(newPassword);
        }
        return user;
    }
}
