package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.pojo.Announcements;
import cn.nullcat.sckj.service.AnnouncementsService;
import cn.nullcat.sckj.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/announcements")
public class AnnouncementsController {
    @Autowired
    private AnnouncementsService announcementsService;

    /**
     * 发布公告
     * @return
     */
    @PutMapping("/add")
    public Result addAnnouncement(@RequestBody Announcements announcements , HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        announcements.setCreatedBy(userIdNow);
        announcements.setCreatedAt(LocalDateTime.now());
        announcementsService.addAnnouncement(announcements);
        return Result.success("发布成功");
    }

    /**
     * 删除公告
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteAnnouncement(@PathVariable Integer id) {
        announcementsService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 查看公告历史
     * @return
     */
    @GetMapping("/getAll")
    public Result getAllAnnouncements(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      String title,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("个人分页条件查询:{},{},{},{},{}", page, pageSize, title, begin, end);
        PageBean pageBean = announcementsService.getAll(page, pageSize, title, begin, end);
        return Result.success(pageBean);
    }

    /**
     * 已读
     * @param announcementId
     * @return
     */
    @PostMapping("/read")
    public Result readAnnouncements(@RequestParam Integer announcementId,HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        announcementsService.read(userId,announcementId);
        return Result.success("此后该公告不会再出现，可以到历史公告查看");
    }

    /**
     * 获取公告
     * @param request
     * @return
     */
    @GetMapping("/get")
    public Result getAnnouncements(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Announcements announcements = announcementsService.get(userId);
        return Result.success(announcements);
    }
}
