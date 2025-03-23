package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.DTO.LeaveApplyDTO;
import cn.nullcat.sckj.pojo.DTO.LeaveApproveDTO;
import cn.nullcat.sckj.pojo.LeaveRecord;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.service.LeaveService;
import cn.nullcat.sckj.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/leave")
public class LeaveController {
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private UserService userService;

    /**
     * 发送申请
     * @param leaveApplyDTO
     * @param request
     * @return
     */
    @PostMapping("/apply")
    public Result apply(@RequestBody LeaveApplyDTO leaveApplyDTO, HttpServletRequest request){
        if (leaveApplyDTO.getLeaveDate().isBefore(LocalDate.now())) {
            return Result.error("非本日或未来日期");
        }
        Integer userIdNow = (Integer) request.getAttribute("userId");
        if(leaveService.exist(userIdNow,leaveApplyDTO.getLeaveDate())){
            return Result.error("今日已提交请假，请等待审核，审核未通过可再次申请");
        }
        Integer groupId = userService.getGroupIdByUserId(userIdNow);
        LeaveRecord leaveRecord = new LeaveRecord();
        leaveRecord.setUserId(userIdNow);
        leaveRecord.setGroupId(groupId);
        leaveRecord.setLeaveDate(leaveApplyDTO.getLeaveDate());
        leaveRecord.setReason(leaveApplyDTO.getReason());
        leaveService.apply(leaveRecord);
        return Result.success("申请成功，等待审核");
    }

    /**
     * 分页查看未审核请求
     * @param request
     * @return
     */
    @GetMapping("/getUnaudited")
    public Result getUnaudited(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                               HttpServletRequest request){
        Integer userIdNow = (Integer) request.getAttribute("userId");
        Integer groupIdNow = userService.getGroupIdByUserId(userIdNow);
        log.info("未审核请求分页条件查询:{},{},{},{},{}", page, pageSize, groupIdNow, begin, end);
        PageBean pageBean = leaveService.getUnaudited(page, pageSize, groupIdNow, begin, end);
        return Result.success(pageBean);
    }

    /**
     * 查看全部请求
     * @param request
     * @return
     */
    @GetMapping("/getAll")
    public Result getAll(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                         HttpServletRequest request){
        Integer userIdNow = (Integer) request.getAttribute("userId");
        Integer groupIdNow = userService.getGroupIdByUserId(userIdNow);
        log.info("全部请求分页条件查询:{},{},{},{},{}", page, pageSize, groupIdNow, begin, end);
        PageBean pageBean = leaveService.getAll(page, pageSize, groupIdNow, begin, end);
        return Result.success(pageBean);
    }

    /**
     *  个人请假记录
     * @param page
     * @param pageSize
     * @param begin
     * @param end
     * @param request
     * @return
     */
    @GetMapping("/getMyLeave")
    public Result getMyLeave(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                             HttpServletRequest request){
        Integer userIdNow = (Integer) request.getAttribute("userId");
        log.info("个人请求分页条件查询:{},{},{},{},{}", page, pageSize, userIdNow, begin, end);
        PageBean pageBean = leaveService.getMyLeave(page, pageSize, userIdNow, begin, end);
        return Result.success(pageBean);
    }
    /**
     * 审核请求
     * @param request
     * @return
     */
    @PostMapping("/approve")
    public Result approve(@RequestBody LeaveApproveDTO LeaveApproveDTO, HttpServletRequest request){
        Integer userIdNow = (Integer) request.getAttribute("userId");
        leaveService.approve(userIdNow,LeaveApproveDTO);
        return Result.success("审核成功");
    }
}
