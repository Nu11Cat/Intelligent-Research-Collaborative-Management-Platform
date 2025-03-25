package cn.nullcat.sckj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcements {

    private Integer id;
    private String title;
    private String content;
    private Integer createdBy;
    private LocalDateTime createdAt;

    private String author;
}
