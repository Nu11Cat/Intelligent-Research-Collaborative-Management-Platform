package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {
    private Integer id;         // 考勤记录ID，主键，自动递增
    private Integer userId;     // 用户ID，外键，关联 users 表
    private Integer groupId;    // 所属小组ID，外键，关联 groups 表
    private String userName;
    private String groupName;
    private LocalDateTime checkIn;  // 上班打卡时间
    private LocalDateTime checkOut; // 下班打卡时间（可为空）
    private Integer isLeave;    // 是否请假：0否，1是
}
