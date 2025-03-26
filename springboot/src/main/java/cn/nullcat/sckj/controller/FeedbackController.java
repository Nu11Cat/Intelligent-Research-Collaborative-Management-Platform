package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.mapper.FeedbackMapper;
import cn.nullcat.sckj.pojo.Feedback;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    /**
     * 提交反馈
     * @param feedback
     * @return
     */
    @PostMapping("/put")
    public Result put(@RequestBody Feedback feedback,HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        feedback.setStatus("pending");
        feedback.setUserId(userId);
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackService.put(feedback);
        return Result.success("反馈成功，管理员查看后会回复你,感谢你做出的贡献！");
    }

    /**
     * 分页查看我的反馈
     * @param request
     * @return
     */
    @GetMapping("/getMyFeedback")
    public Result get(@RequestParam(defaultValue = "1") Integer page,
                      @RequestParam(defaultValue = "10") Integer pageSize,
                      HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        PageBean pageBean = feedbackService.getMyFeedback(page, pageSize, userId);
        return Result.success(pageBean);
    }

    /**
     * 处理反馈
     * @param feedback
     * @return
     */
    @PostMapping("/resolved")
    public Result resolved(@RequestBody Feedback feedback) {
        feedback.setResolvedAt(LocalDateTime.now());
        feedback.setStatus("resolved");
        feedbackService.resolved(feedback);
        return  Result.success("处理成功");
    }
    /**
     * 分页查看全部反馈
     * @return
     */
    @GetMapping("/getAllFeedback")
    public Result get(@RequestParam(defaultValue = "1") Integer page,
                      @RequestParam(defaultValue = "10") Integer pageSize) {
        PageBean pageBean = feedbackService.getAllFeedback(page, pageSize);
        return Result.success(pageBean);
    }
}
