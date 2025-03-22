package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {
    private Integer id;
    private String name;
    private String description;
    private Integer level;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Integer status;
}