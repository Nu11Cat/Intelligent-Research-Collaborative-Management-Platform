package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.Articles;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ArticlesMapper {
    /**
     * 添加文章
     * @param articles
     */
    @Insert("insert into articles(library_id, title, content, author_id, group_id, created_at, updated_at) VALUES (#{libraryId},#{title},#{content},#{authorId},#{groupId},#{createdAt},#{updatedAt})")
    void add(Articles articles);

    /**
     * 删除指定文章
     * @param id
     */
    @Delete("delete from articles where id = #{id}")
    void delete(Integer id);

    /**
     * 更新文章
     * @param articles
     */
    @Update("update articles set title = #{title},content = #{content} ,updated_at = #{updatedAt}")
    void update(Articles articles);

    /**
     * 查看指定人的文章
     * @param userId
     * @return
     */
    @Select("select * from articles where author_id = #{userId}")
    List<Articles> getByUserId(Integer userId);

    /**
     * 获取指定资源库的文章
     * @param libraryId
     * @param title
     * @param begin
     * @param end
     * @return
     */
    List<Articles> getByLibraryId(Integer libraryId, String title, LocalDate begin, LocalDate end);
}
