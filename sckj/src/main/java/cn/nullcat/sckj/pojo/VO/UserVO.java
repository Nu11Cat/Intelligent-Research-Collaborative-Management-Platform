package cn.nullcat.sckj.pojo.VO;

import cn.nullcat.sckj.pojo.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {

    private String username; // 用户名，唯一

    private Users.Role role; // 角色（组员、组长、超级管理员）

    private String groupName;

    private LocalDateTime createdAt; // 用户注册时间

    private LocalDateTime updatedAt; // 用户信息更新时间

    // 枚举类表示角色
    public enum Role {
        USER, LEADER, ADMIN
    }
}
