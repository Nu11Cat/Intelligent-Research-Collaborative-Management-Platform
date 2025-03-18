package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.DTO.SignInDTO;
import cn.nullcat.sckj.pojo.Users;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AttendanceMapper {
    /**
     * 判断今日是否签到1
     * @param userId
     * @param groupId
     * @param time
     * @return
     */
    @Select("SELECT * FROM attendance " +
            "WHERE user_id = #{userId} " +
            "AND group_id = #{groupId} " +
            "AND DATE(check_in) = DATE(#{time})")
    Users getBySignIn(Integer userId, Integer groupId, LocalDateTime time);

    /**
     * 签到
     * @param userId
     * @param groupId
     * @param time
     */
    @Insert("INSERT INTO attendance (user_id, group_id, check_in,username,group_name) VALUES (#{userId}, #{groupId}, #{time},#{userName},#{groupName})")
    void add(Integer userId, Integer groupId, LocalDateTime time,String userName,String groupName);

    /**
     * 判断今日是否签到2
     * @param userId
     * @param time
     * @return
     */
    @Select("select * from attendance where user_id = #{userId} and DATE(check_in) = DATE(#{time})")
    Users getBySignOut(Integer userId, LocalDateTime time);

    /**
     * 签退
     * @param userId
     * @param time
     */
    @Update("UPDATE attendance SET check_out = #{time} WHERE user_id = #{userId} AND DATE(check_in) = CURRENT_DATE")
    void add2(Integer userId, LocalDateTime time);

    /**
     * 获取全部考勤记录 分页条件查询
     * @param username
     * @param groupName
     * @param begin
     * @param end
     * @return
     */

    List<Attendance> allRecord(String username, String groupName, LocalDate begin, LocalDate end);

    /**
     * 获取个人考勤记录 分页条件查询
     * @param begin
     * @param end
     * @return
     */
    List<Attendance> myRecord(Integer userIdNow,LocalDate begin, LocalDate end);

    /**
     * 获取小组考勤记录 分页条件查询
     * @param groupIdNow
     * @param begin
     * @param end
     * @return
     */
    List<Attendance> groupRecord(Integer groupIdNow, LocalDate begin, LocalDate end);
}
