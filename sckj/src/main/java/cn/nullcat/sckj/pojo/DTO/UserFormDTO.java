package cn.nullcat.sckj.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFormDTO {

    private String username; // 用户名，唯一

    private String password; // 密码（加密存储）

    private Integer groupId; // 所属小组ID，外键，关联 groups 表
}
