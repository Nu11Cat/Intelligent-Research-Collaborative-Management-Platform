package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.AnnouncementsMapper;
import cn.nullcat.sckj.pojo.Announcements;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.service.AnnouncementsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementsServiceImpl implements AnnouncementsService {

    @Autowired
    private AnnouncementsMapper announcementsMapper;
    /**
     * 发布公告
     * @param announcements
     */
    @Override
    public void addAnnouncement(Announcements announcements) {
        announcementsMapper.add(announcements);
    }

    /**
     * 删除公告
     * @param id
     */
    @Override
    public void delete(Integer id) {
        announcementsMapper.delete(id);
    }

    /**
     * 查看全部公告
     * @param page
     * @param pageSize
     * @param title
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean getAll(Integer page, Integer pageSize, String title, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<Announcements> list = announcementsMapper.getAll(title,begin, end);
        Page<Announcements> p = (Page<Announcements>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 已读
     * @param userId
     * @param announcementId
     */
    @Override
    public void read(Integer userId, Integer announcementId) {
        LocalDateTime now = LocalDateTime.now();
        announcementsMapper.read(userId,announcementId,now);
    }

    /**
     * 获取公告
     * @param userId
     * @return
     */
    @Override
    public Announcements get(Integer userId) {
        Announcements announcements = announcementsMapper.get(userId);
        return announcements;
    }
}
