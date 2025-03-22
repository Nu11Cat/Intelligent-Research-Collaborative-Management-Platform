package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.Group;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GroupMapper {
    /**
     *
     * @param name
     * @return
     */
    @Select("select * from user_groups where name = #{name}")
    Group getByGruopName(String name);

    /**
     *
     * @param name
     * @param description
     * @param now
     */
    @Insert("INSERT INTO user_groups (name, description, created_at)" +
            "VALUES (#{name}, #{description}, #{now});")
    void add(String name, String description, LocalDateTime now);

    /**
     * 删除
     * @param name
     */
    @Delete("delete from user_groups where name = #{name}")
    void delete(String name);

    /**
     * 根据id查询小组
     * @param id
     * @return
     */
    @Select("select * from user_groups where id = #{id}")
    Group getById(String id);

    /**
     * 修改小组信息
     * @param name
     * @param description
     * @param now
     */
    @Update("UPDATE user_groups SET name = #{name}, description = #{description}, admin_a = #{leaderA}, admin_b = #{leaderB}, admin_c = #{leaderC}, updated_at = #{now} WHERE id = #{id}")
    void update(Integer id, String name, String description, Integer leaderA, Integer leaderB, Integer leaderC, LocalDateTime now);

    /**
     * 分页查询小组
     * @param name
     * @param begin
     * @param end
     * @return
     */
    List<Group> getAll(String name, LocalDate begin, LocalDate end);

}
