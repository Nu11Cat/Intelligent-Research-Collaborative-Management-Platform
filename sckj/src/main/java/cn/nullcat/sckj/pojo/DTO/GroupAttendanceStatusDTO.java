package cn.nullcat.sckj.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupAttendanceStatusDTO {
    // 组内总人数
    private Integer totalMembers;

    // 今日已签到人数
    private Integer checkedInCount;

    // 今日已签退人数
    private Integer checkedOutCount;

    // 未签到人员名单
    private List<String> notCheckedInUsers;

    // 已签到但未签退人员名单
    private List<String> notCheckedOutUsers;

    // 可选：小组名称
    private String groupName;

    // 可选：统计日期
    private String date;
}
