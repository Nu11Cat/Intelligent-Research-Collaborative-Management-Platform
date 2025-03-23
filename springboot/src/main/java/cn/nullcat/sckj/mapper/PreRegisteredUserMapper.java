package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.Group;
import cn.nullcat.sckj.pojo.PreregisteredUser;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PreRegisteredUserMapper {
    /**
     *
     * @param username
     * @return
     */
    @Select("select * from pre_registered_users where username = #{username}")
    PreregisteredUser getByName(String username);

    /**
     *
     * @param groupId
     * @return
     */
    @Select("select * from user_groups where id = #{groupId}")
    Group getByGroupId(Integer groupId);

    /**
     *
     * @param username
     * @param groupId
     * @param notadd
     * @param now
     */
    @Insert("INSERT INTO pre_registered_users (username, group_id, status, created_at)" +
            "VALUES (#{username}, #{groupId}, #{notadd}, #{now});")
    void add(String username, Integer groupId, String notadd,LocalDateTime now);

    /**
     * 删除预注册用户
     * @param username
     */
    @Delete("DELETE FROM pre_registered_users WHERE username = #{username}")
    void delete(String username);

    /**
     * 分页条件查询
     * @param username
     * @param begin
     * @param end
     * @return
     */
    List<PreregisteredUser> getAll(String username, LocalDate begin, LocalDate end);

    /**
     * 根据用户名字查询用户小组
     * @param username
     * @return
     */
    @Select("select group_id from pre_registered_users where username = #{username}")
    Integer getGroupIdByUserName(String username);

    /**
     * 修改用户注册状态
     * @param username
     */
    @Update("UPDATE pre_registered_users SET `status` = '已注册' WHERE username = #{username}")
    void chagneRegisteredStatus(String username);
}
