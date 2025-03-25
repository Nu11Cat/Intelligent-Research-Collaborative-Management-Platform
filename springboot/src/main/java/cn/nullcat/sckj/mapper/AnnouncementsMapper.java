package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.Announcements;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AnnouncementsMapper {
    /**
     * 发布公告
     * @param announcements
     */
    @Insert("insert into announcements(title, content, created_by, created_at) VALUES (#{title},#{content},#{createdBy},#{createdAt})")
    void add(Announcements announcements);

    /**
     * 删除公告
     * @param id
     */
    @Delete("delete from announcements where id = #{id}")
    void delete(Integer id);

    /**
     *
     * @param title
     * @param begin
     * @param end
     * @return
     */
    List<Announcements> getAll(String title, LocalDate begin, LocalDate end);

    /**
     *
     * @param userId
     * @param announcementId
     */
    @Insert("insert into announcement_user(user_id, announcement_id, read_status, read_time) VALUES (#{userId},#{announcementId},1,#{now})")
    void read(Integer userId, Integer announcementId, LocalDateTime now);

    /**
     * @param userId
     * @return
     */
    Announcements get(Integer userId);
}
