package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.service.AttendanceService;
import cn.nullcat.sckj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private UserService userService;

    /**
     * 签到
     * @param
     * @return
     */
    @PostMapping("/signIn")
    public Result signIn(HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        if(attendanceService.signedIn(userIdNow)){
            return Result.error("你今天已经签过到了喔");
        }
        attendanceService.signIn(userIdNow);
        return Result.success("签到成功");
    }

    /**
     * 签退
     * @param
     * @return
     */
    @PostMapping("/signOut")
    public Result signOut(HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        if(attendanceService.signedOut(userIdNow)){
            return Result.error("你还没签到喔，不能签退");
        }
        attendanceService.signOut(userIdNow);
        return Result.success("签退成功");
    }

    /**
     * 获取全部考勤记录 分页条件查询
     * @return
     */
    @GetMapping("/allRecords")
    public Result allRecords(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             String username,
                             String groupName,
                             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("全部分页条件查询:{},{},{},{},{},{}",page,pageSize,username,groupName,begin,end);
        PageBean pageBean = attendanceService.allRecords(page,pageSize,username,groupName,begin,end);
        return Result.success(pageBean);
    }

    /**
     * 获取个人考勤记录 分页条件查询
     * @param page
     * @param pageSize
     * @param begin
     * @param end
     * @param request
     * @return
     */
    @GetMapping("/myRecords")
    public Result myRecords(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                             HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        log.info("个人分页条件查询:{},{},{},{},{}", page, pageSize, userIdNow, begin, end);
        PageBean pageBean = attendanceService.myRecords(page, pageSize, userIdNow, begin, end);
        return Result.success(pageBean);
    }

    /**
     * 获取小组考勤记录 分页条件查询
     * @param page
     * @param pageSize
     * @param begin
     * @param end
     * @param request
     * @return
     */
    @GetMapping("/groupRecords")
    public Result groupRecords(@RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                            HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        Integer groupIdNow = userService.getGroupIdByUserId(userIdNow);
        log.info("小组分页条件查询:{},{},{},{},{}", page, pageSize, groupIdNow, begin, end);
        PageBean pageBean = attendanceService.groupRecords(page, pageSize, groupIdNow, begin, end);
        return Result.success(pageBean);
    }

}
