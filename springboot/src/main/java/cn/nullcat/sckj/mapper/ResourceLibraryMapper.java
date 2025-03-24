package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.ResourceLibrary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ResourceLibraryMapper {
    /**
     * 根据id获取资料库
     * @param userIdNow
     * @return
     */
    @Select("select * from resource_library where user_id = #{userIdNpw}")
    ResourceLibrary getById(Integer userIdNow);

    /**
     * 新增
     * @param resourceLibrary
     */
    @Insert("INSERT INTO resource_library (user_id, created_at, updated_at,group_id) VALUES (#{userId}, #{createdAt}, #{updatedAt},#{groupId})")
    void add(ResourceLibrary resourceLibrary);

    /**
     * 删除
     * @param userIdNow
     */
    @Delete("delete from resource_library where user_id = #{userIdNow}")
    void delete(Integer userIdNow);

    /**
     * 分页条件查询
     * @param username
     * @param groupName
     * @param begin
     * @param end
     * @return
     */
    List<ResourceLibrary> getAll(String username, String groupName, LocalDate begin, LocalDate end);

    /**
     * 根据用户id查询资料库id
     * @param userIdNow
     * @return
     */
    @Select("select id from resource_library where user_id = #{userIdNow}")
    Integer getLibraryIdByUserId(Integer userIdNow);
}
