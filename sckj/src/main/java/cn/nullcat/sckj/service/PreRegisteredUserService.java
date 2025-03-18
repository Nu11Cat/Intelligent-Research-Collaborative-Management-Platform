package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.PageBean;

import java.time.LocalDate;

public interface PreRegisteredUserService {
    /**
     * 判断名字是否存在
     * @param username
     * @return
     */
    boolean hadname(String username);

    /**
     * 判断小组是否存在
     * @param groupId
     * @return
     */
    boolean hadGroupId(Integer groupId);

    /**
     * 预注册用户
     * @param username
     * @param groupId
     */
    void add(String username, Integer groupId);

    /**
     * 删除
     * @param username
     */
    void delete(String username);

    /**
     * 条件分页查询
     * @param page
     * @param pageSize
     * @param username
     * @param begin
     * @param end
     * @return
     */
    PageBean getAll(Integer page, Integer pageSize, String username, LocalDate begin, LocalDate end);
}
