package cn.nullcat.sckj.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminAttendanceStatusDTO {
    // 1. 总体统计
    private Integer totalUsers;           // 系统总人数
    private Integer totalGroups;          // 小组总数
    private Integer totalCheckedIn;       // 今日总签到人数
    private Integer totalCheckedOut;      // 今日总签退人数
    private Integer totalLeave;           // 今日总请假人数

    // 2. 分组统计（复用现有的GroupAttendanceStatusDTO）
    private List<GroupAttendanceStatusDTO> groupStats;  // 各组统计数据
}