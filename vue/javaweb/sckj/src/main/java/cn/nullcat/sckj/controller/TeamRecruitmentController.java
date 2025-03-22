package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.pojo.TeamRecruitment;
import cn.nullcat.sckj.service.TeamRecruitmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/teamRecruitment")
public class TeamRecruitmentController {
    @Autowired
    private TeamRecruitmentService teamRecruitmentService;

    /**
     * 发布招募信息
     * @param teamRecruitment
     * @param request
     * @return
     */
    @PutMapping("/add")
    private Result add(@RequestBody TeamRecruitment teamRecruitment, HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        teamRecruitment.setCreatedBy(userIdNow);
        teamRecruitmentService.add(teamRecruitment);
        return Result.success("发布成功");
    }

    /**
     * 删除自己发布的招募信息
     * @param teamRecruitmentId
     * @param request
     * @return
     */
    @DeleteMapping("/delete")
    private Result delete(@RequestParam Integer teamRecruitmentId, HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        if(teamRecruitmentService.notYours(teamRecruitmentId,userIdNow)){
            return Result.error("这条招募信息不属于你，你无权删除");
        }
        teamRecruitmentService.delete(teamRecruitmentId);
        return Result.success("删除成功");
    }

    /**
     * 根据id查询，回显
     * @param teamRecruitmentId
     * @return
     */
    @GetMapping("/getById")
    private Result getById(@RequestParam Integer teamRecruitmentId) {
        TeamRecruitment teamRecruitment = teamRecruitmentService.getById(teamRecruitmentId);
        return Result.success(teamRecruitment);
    }

    /**
     * 修改
     * @param teamRecruitment
     * @param request
     * @return
     */
    @PostMapping("/update")
    private Result update(@RequestBody TeamRecruitment teamRecruitment, HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        Integer teamRecruitmentId = teamRecruitment.getId();
        if(teamRecruitmentService.notYours(teamRecruitmentId,userIdNow)){
            return Result.error("这条招募信息不属于你，你无权修改");
        }
        teamRecruitmentService.update(teamRecruitment);
        return Result.success("修改成功");
    }
    /**
     * 获取全部 分页条件查询
     * @return
     */
    @GetMapping("/getAll")
    private Result getAll(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String title,
                          @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate begin,
                          @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate end) {
        PageBean pageBean = teamRecruitmentService.getAll(page,pageSize,title,begin,end);
        return Result.success(pageBean);
    }
}
