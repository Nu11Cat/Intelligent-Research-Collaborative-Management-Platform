package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRecord {
    private Integer id;                // 主键ID
    private Integer userId;            // 申请人ID
    private Integer groupId;           // 所属组ID
    private LocalDate leaveDate;       // 请假日期
    private String reason;             // 请假原因
    private Integer status;            // 状态：0待审核、1已通过、2已拒绝
    private LocalDateTime createTime;  // 申请时间
    private LocalDateTime approveTime; // 审核时间
    private Integer approverId;        // 审核人ID
    private String approverComment;    // 审核意见

    // 额外字段（用于展示）
    private String userName;           // 申请人姓名
    private String groupName;          // 小组名称
    private String approverName;       // 审核人姓名
}