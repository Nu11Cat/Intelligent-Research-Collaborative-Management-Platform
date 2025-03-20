package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 新增用户
     * @param users
     */
    void add(Users users);

    /**
     * 根据名字查询用户
     * @param username
     * @return
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    Users findByUsername(String username);

    /**
     * 根据名字和密码查询用户
     * @param username
     * @param password
     * @return
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND password = #{password}")
    Users findByUsernameAndPassword(String username, String password);

    /**
     * 根据用户名获取用户 ID
     * @param username
     * @return
     */
    @Select("SELECT id FROM users WHERE username = #{username};")
    Integer getUserIdByUsername(String username);

    /**
     * 根据用户id查询组id
     * @param userIdNow
     * @return
     */
    @Select("select users.group_id from users where id = #{userIdNow}")
    Integer getGroupIdByUserId(Integer userIdNow);
    /**
     * 根据用户id查询用户用户名
     * @param userIdNow
     * @return
     */
    @Select("SELECT username FROM users WHERE id = #{userIdNow}")
    String getUsernameById(Integer userIdNow);

    /**
     * 根据用户id查询组名
     * @param userIdNow
     * @return
     */
    @Select("SELECT g.name FROM nullcatsckj.user_groups g JOIN users u ON g.id = u.group_id WHERE u.id = #{userIdNow}")
    String getGroupnameById(Integer userIdNow);

    /**
     * 根据id查询密码
     * @param userIdNow
     * @return
     */
    @Select("SELECT password FROM users WHERE id = #{userIdNow}")
    String getPassWordById(Integer userIdNow);

    /**
     * 更改密码
     * @param newPassword
     * @param userIdNow
     */
    @Update("UPDATE users " +
            "    SET password = #{newPassword}" +
            "    WHERE id = #{userIdNow}")
    void updatePassword(String newPassword, Integer userIdNow);

    /**
     * 根据id获取信息
     * @param userIdNow
     * @return
     */
    @Select("select * from users where id = #{userIdNow}")
    Users getById(Integer userIdNow);

    /**
     * 修改身份
     * @param id
     */
    @Update("UPDATE users SET role = 'LEADER' WHERE id = #{id}")
    void changeRole(Integer id);

    /**
     * 查询全部信息
     * @param username
     * @param groupName
     * @param begin
     * @param end
     * @return
     */
    List<Users> getAll(String username, String groupName,String role ,LocalDate begin, LocalDate end);

    /**
     *
     * @param groupIdNow
     * @return
     */
    @Select("SELECT id, username FROM users WHERE group_id = #{groupId}")
    List<Users> getByGroupId(Integer groupIdNow);
}
