package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.Articles;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.service.ArticlesService;
import cn.nullcat.sckj.service.ResourceLibraryService;
import cn.nullcat.sckj.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/articles")
public class ArticlesController {

    @Autowired
    private ArticlesService articlesService;
    @Autowired
    private UserService userService;
    @Autowired
    private ResourceLibraryService resourceLibraryService;

    /**
     * 添加文章
     * @param articles
     * @return
     */
    @PutMapping("/add")
    public Result add(@RequestBody Articles articles, HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        Integer groupIdNow = userService.getGroupIdByUserId(userIdNow);
        articles.setLibraryId(resourceLibraryService.getLibraryIdByUserId(userIdNow));
        articles.setAuthorId(userIdNow);
        articles.setGroupId(groupIdNow);
        articles.setCreatedAt(LocalDateTime.now());
        articles.setUpdatedAt(LocalDateTime.now());
        articlesService.add(articles);
        return Result.success("添加成功");
    }

    /**
     * 删除文章
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        articlesService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 更新文章
     * @param articles
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Articles articles) {
        articlesService.update(articles);
        return Result.success("修改成功");
    }
    /**
     * 根据用户id查询文章
     * @param userId
     * @return
     */
    @GetMapping("/getByUserId/{userId}")
    public Result getByUserId(@PathVariable Integer userId) {
        List<Articles> list = articlesService.getByUserId(userId);
        return Result.success(list);
    }
    /**
     * 根据资料库id查询文章
     * @param page
     * @param pageSize
     * @param libraryId
     * @param title
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/getBylibraryId")
    public Result getByLibraryId(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 Integer libraryId,
                                 String title,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("个人分页条件查询:{},{},{},{},{},{}", page, pageSize, libraryId,title, begin, end);
        PageBean pageBean = articlesService.getByLibraryId(page, pageSize, libraryId,title, begin, end);
        return Result.success(pageBean);
    }

}
