package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {
    private Integer id;            // 反馈ID
    private Integer userId;        // 反馈用户ID
    private String content;        // 反馈内容
    private String status;         // 状态（pending, resolved）
    private LocalDateTime createdAt;   // 反馈提交时间
    private LocalDateTime resolvedAt;  // 反馈处理时间
    private String adminResponse;  // 管理员回复

    private String userName;
}
