package cn.nullcat.sckj.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentBorrowVO {
    private String name;
    private String groupName;
    private LocalDateTime borrowTime;
    private LocalDateTime returnTime;
}
