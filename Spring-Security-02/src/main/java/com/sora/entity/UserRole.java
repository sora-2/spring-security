package com.sora.entity;


import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @TableName user_role
 */
@TableName(value ="user_role")
@Data
public class UserRole implements Serializable {
    private Integer id;

    private Integer uid;

    private Integer rid;

    private static final long serialVersionUID = 1L;
}