package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    private Integer id;            // 小组ID，主键，自动递增
    private String name;           // 小组名称
    private String description;    // 小组描述
    private Integer adminA;        // 负责人A（用户ID，外键）
    private Integer adminB;        // 负责人B（用户ID，外键）
    private Integer adminC;        // 负责人C（用户ID，外键）
    private LocalDateTime createdAt;        // 小组创建时间
}
