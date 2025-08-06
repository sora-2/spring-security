package com.sora.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sora.entity.Role;
import com.sora.entity.User;

import java.util.List;

/**
* @author 轩宇
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-08-05 13:40:06
* @Entity generator.domain.User
*/
public interface UserMapper extends BaseMapper<User> {


    User loadUserByUsername(String username);

    List<Role> getRolesById(Integer id);
}
