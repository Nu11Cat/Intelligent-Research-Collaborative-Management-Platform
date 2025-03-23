package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.TeamRecruitment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TeamRecruitmentMapper {
    /**
     * 新增
     * @param title
     * @param description
     * @param contact
     * @param createdBy
     * @param now
     */
    @Insert("INSERT INTO team_recruitment (title, description, contact, created_by, created_at) " +
            "VALUES (#{title}, #{description}, #{contact}, #{createdBy}, #{now})")
    void add(String title, String description, String contact, Integer createdBy, LocalDateTime now);

    /**
     * 删除
     * @param teamRecruitmentId
     */
    @Delete("delete from team_recruitment where id = #{teamRecruitmentId}")
    void delete(Integer teamRecruitmentId);

    /**
     * 根据id获得全部信息
     * @param teamRecruitmentId
     * @return
     */
    @Select("select * from team_recruitment where id = #{teamRecruitmentId}")
    TeamRecruitment getById(Integer teamRecruitmentId);

    /**
     * 修改
     * @param id
     * @param title
     * @param description
     * @param contact
     */
    void update(Integer id, String title, String description, String contact);

    /**
     * 分页查询
     * @param title
     * @param begin
     * @param end
     * @return
     */
    List<TeamRecruitment> getAll(String title, LocalDate begin, LocalDate end);
}
