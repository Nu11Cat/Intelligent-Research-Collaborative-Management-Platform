package cn.nullcat.sckj.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveApproveDTO {
    private Integer id;               // 请假记录ID
    private Integer status;           // 审核状态：1通过、2拒绝
    private String approverComment;   // 审核意见
}
