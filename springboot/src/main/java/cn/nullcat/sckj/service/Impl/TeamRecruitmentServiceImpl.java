package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.TeamRecruitmentMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.TeamRecruitment;
import cn.nullcat.sckj.service.TeamRecruitmentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeamRecruitmentServiceImpl implements TeamRecruitmentService {
    @Autowired
    private TeamRecruitmentMapper teamRecruitmentMapper;

    /**
     *
     * @param teamRecruitment
     */
    @Override
    public void add(TeamRecruitment teamRecruitment) {
        String title = teamRecruitment.getTitle();
        String description = teamRecruitment.getDescription();
        String contact = teamRecruitment.getContact();
        Integer createdBy = teamRecruitment.getCreatedBy();
        LocalDateTime now = LocalDateTime.now();
        teamRecruitmentMapper.add(title,description,contact,createdBy,now);
    }

    /**
     * 检查是否属于操作者
     * @param teamRecruitmentId
     * @param userIdNow
     * @return
     */
    @Override
    public boolean notYours(Integer teamRecruitmentId, Integer userIdNow) {
        TeamRecruitment teamRecruitment = teamRecruitmentMapper.getById(teamRecruitmentId);
        Integer createdBy = teamRecruitment.getCreatedBy();
        return createdBy != userIdNow;
    }

    /**
     * 删除信息
     * @param teamRecruitmentId
     */
    @Override
    public void delete(Integer teamRecruitmentId) {
        teamRecruitmentMapper.delete(teamRecruitmentId);
    }

    /**
     * 根据id获取信息
     * @param teamRecruitmentId
     * @return
     */
    @Override
    public TeamRecruitment getById(Integer teamRecruitmentId) {
        return teamRecruitmentMapper.getById(teamRecruitmentId);
    }

    /**
     * 修改
     * @param teamRecruitment
     */
    @Override
    public void update(TeamRecruitment teamRecruitment) {
        Integer id = teamRecruitment.getId();
        String title = teamRecruitment.getTitle();
        String description = teamRecruitment.getDescription();
        String contact = teamRecruitment.getContact();
        teamRecruitmentMapper.update(id,title,description,contact);
    }

    @Override
    public PageBean getAll(Integer page, Integer pageSize, String title, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<TeamRecruitment> list = teamRecruitmentMapper.getAll(title,begin, end);
        Page<TeamRecruitment> p = (Page<TeamRecruitment>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }
}
