package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    private Integer id; // 用户ID，自动递增

    private String username; // 用户名，唯一

    private String password; // 密码（加密存储）

    private Role role; // 角色（组员、组长、超级管理员）

    private Integer groupId; // 所属小组ID，外键，关联 groups 表

    private LocalDateTime createdAt; // 用户注册时间

    private LocalDateTime updatedAt; // 用户信息更新时间

    // 枚举类表示角色
    public enum Role {
        USER, LEADER, ADMIN
    }
}
