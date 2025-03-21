package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.DTO.LeaveApproveDTO;
import cn.nullcat.sckj.pojo.LeaveRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LeaveMapper {
    /**
     *
     * @param userIdNow
     * @param leaveDate
     * @return
     */
    @Select("select * from leave_record where user_id = #{userIdNow} and leave_date = #{leaveDate} and status != 2")
    LeaveRecord getByUserIdAndLocalDate(Integer userIdNow, LocalDate leaveDate);

    /**
     *
     * @param leaveRecord
     */
    @Insert("INSERT INTO leave_record (user_id,group_id,leave_date,reason,status,create_time) VALUES ( #{userId},#{groupId},#{leaveDate},#{reason},#{status},#{createTime})")
    void apply(LeaveRecord leaveRecord);

    /**
     *
     * @param groupIdNow
     * @param begin
     * @param end
     * @return
     */
    List<LeaveRecord> getUnaudited(Integer groupIdNow, LocalDate begin, LocalDate end);

    /**
     *
     * @param groupIdNow
     * @param begin
     * @param end
     * @return
     */
    List<LeaveRecord> getAll(Integer groupIdNow, LocalDate begin, LocalDate end);

    /**
     *
     * @param userIdNow
     * @param begin
     * @param end
     * @return
     */
    List<LeaveRecord> getMyLeave(Integer userIdNow, LocalDate begin, LocalDate end);

    /**
     * 审核请求
     * @param id
     * @param status
     * @param approveTime
     * @param approverId
     * @param approverComment
     */
    @Update("UPDATE leave_record SET status = #{status},approve_time = #{approveTime},approver_id = #{approverId},approver_comment = #{approverComment} WHERE id = #{id} and status = 0")
    void approve(Integer id, Integer status, LocalDateTime approveTime, Integer approverId, String approverComment);

    @Select("select * from leave_record where id = #{id}")
    LeaveRecord getById(Integer id);
}
