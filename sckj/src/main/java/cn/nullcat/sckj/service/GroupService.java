package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.Group;
import cn.nullcat.sckj.pojo.PageBean;

import java.time.LocalDate;

public interface GroupService {
    /**
     *
     * @param name
     * @return
     */
    boolean getByGruopName(String name);

    /**
     * 添加小组
     * @param name
     * @param description
     */
    void add(String name, String description);

    /**
     * 删除
     * @param name
     */
    void delete(String name);

    /**
     * 根据id查询小组
     * @param id
     * @return
     */
    Group getById(String id);

    /**
     * 修改小组信息
     * @param group
     */
    void update(Group group);

    /**
     * 条件分夜查询
     * @param page
     * @param pageSize
     * @param name
     * @param begin
     * @param end
     * @return
     */
    PageBean getAll(Integer page, Integer pageSize, String name, LocalDate begin, LocalDate end);
}
