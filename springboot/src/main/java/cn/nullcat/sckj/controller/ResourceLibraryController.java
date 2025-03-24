package cn.nullcat.sckj.controller;


import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.ResourceLibrary;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.service.ResourceLibraryService;
import cn.nullcat.sckj.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/resourceLibrary")
public class ResourceLibraryController {
    @Autowired
    private ResourceLibraryService resourceLibraryService;
    @Autowired
    private UserService userService;
    /**
     * 添加自己的资料库
     * @param request
     * @return
     */
    @PutMapping("/add")
    public Result addResourceLibrary(HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        Integer groupIdNow = userService.getGroupIdByUserId(userIdNow);
        if(resourceLibraryService.exit(userIdNow)){
            return Result.error("你已经有资料库了");
        }
        resourceLibraryService.add(userIdNow,groupIdNow);
        return Result.success("添加成功！");
    }

    /**
     * 删除自己的资料库
     * @param request
     * @return
     */
    @DeleteMapping("/delete")
    public Result deleteResourceLibrary(HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        if(!resourceLibraryService.exit(userIdNow)){
            return Result.error("你还没有数据库");
        }
        resourceLibraryService.delete(userIdNow);
        return Result.success("删除成功");
    }

    /**
     * 分页查询全部资料库
     * @param page
     * @param pageSize
     * @param username
     * @param groupName
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/getAll")
    public Result getAllResourceLibrary(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        String username,
                                        String groupName,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("资料库分页条件查询:{},{},{},{},{},{}",page,pageSize,username,groupName,begin,end);
        PageBean pageBean = resourceLibraryService.allResourceLibrary(page,pageSize,username,groupName,begin,end);
        return Result.success(pageBean);
    }
}
