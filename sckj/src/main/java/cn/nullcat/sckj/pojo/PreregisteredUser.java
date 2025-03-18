package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreregisteredUser {
    private Integer id;              // 记录ID，主键，自动递增
    private String username;         // 预登记用户名，唯一
    private Integer groupId;         // 所属小组ID
    private String status;           // 标记该用户名是否已被注册
    private LocalDateTime createdAt;          // 预登记时间
}
