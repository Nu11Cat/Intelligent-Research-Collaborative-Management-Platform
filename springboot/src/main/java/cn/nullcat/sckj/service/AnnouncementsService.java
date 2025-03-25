package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.Announcements;
import cn.nullcat.sckj.pojo.PageBean;

import java.time.LocalDate;

public interface AnnouncementsService {
    /**
     *
     * @param announcements
     */
    void addAnnouncement(Announcements announcements);

    void delete(Integer id);

    /**
     *
     * @param page
     * @param pageSize
     * @param title
     * @param begin
     * @param end
     * @return
     */
    PageBean getAll(Integer page, Integer pageSize, String title, LocalDate begin, LocalDate end);

    /**
     *
     * @param userId
     * @param announcementId
     */
    void read(Integer userId, Integer announcementId);

    /**
     *
     * @param userId
     * @return
     */
    Announcements get(Integer userId);
}
