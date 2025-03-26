package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.Feedback;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FeedbackMapper {
    /**
     * 提交反馈
     * @param feedback
     */
    @Insert("insert into feedback(user_id, content, status, created_at) VALUES(#{userId},#{content},#{status},#{createdAt})")
    void put(Feedback feedback);

    /**
     * 查看我的反馈
     * @param userId
     * @return
     */
    @Select("select * from feedback where user_id = #{userId}")
    List<Feedback> getMyFeedback(Integer userId);

    /**
     * 处理反馈
     * @param feedback
     */
    @Update("update feedback set status = #{status},resolved_at = #{resolvedAt},admin_response = #{adminResponse} where id = #{id}")
    void resolved(Feedback feedback);

    /**
     * 查看全部反馈
     * @return
     */
    List<Feedback> getAllFeedback();
}
