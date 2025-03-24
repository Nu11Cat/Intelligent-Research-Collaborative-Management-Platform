package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Articles {

    private Integer id;

    private Integer libraryId;

    private String title;
    private String content;

    private Integer authorId;
    private Integer groupId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
