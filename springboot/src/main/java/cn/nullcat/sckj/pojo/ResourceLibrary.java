package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceLibrary {
    private Integer id;
    private Integer userId;
    private Integer groupId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String userName;
    private String groupName;
}
