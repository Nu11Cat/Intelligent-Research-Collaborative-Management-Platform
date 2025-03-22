package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.TeamRecruitment;

import java.time.LocalDate;

public interface TeamRecruitmentService {
    /**
     *
     * @param teamRecruitment
     */
    void add(TeamRecruitment teamRecruitment);

    /**
     * 检查是否属于操作者
     * @param teamRecruitmentId
     * @param userIdNow
     * @return
     */
    boolean notYours(Integer teamRecruitmentId, Integer userIdNow);

    /**
     * 删除信息
     * @param teamRecruitmentId
     */
    void delete(Integer teamRecruitmentId);

    /**
     * 获取根据id
     * @param teamRecruitmentId
     * @return
     */
    TeamRecruitment getById(Integer teamRecruitmentId);

    /**
     * 修改
     * @param teamRecruitment
     */
    void update(TeamRecruitment teamRecruitment);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param title
     * @param begin
     * @param end
     * @return
     */
    PageBean getAll(Integer page, Integer pageSize, String title, LocalDate begin, LocalDate end);
}
