package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentBorrow {
    private Integer id;
    private Integer userId;
    private Integer equipmentId;
    private LocalDateTime borrowTime;
    private LocalDateTime returnTime;
}
