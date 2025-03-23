package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamRecruitment {
    private Integer id;             // 招募信息ID
    private String title;           // 招募标题
    private String description;     // 招募详情
    private String contact;         // 联系方式（手机号/微信等）
    private Integer createdBy;      // 发布者ID
    private LocalDateTime createdAt; // 发布时间
}
