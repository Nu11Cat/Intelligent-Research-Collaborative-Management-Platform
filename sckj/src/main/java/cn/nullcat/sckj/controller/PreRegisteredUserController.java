package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.PreregisteredUser;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.service.PreRegisteredUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/preRegisteredUser")
public class PreRegisteredUserController {
    @Autowired
    private PreRegisteredUserService preRegisteredUserService;

    /**
     * 预注册用户
     * @param preregisteredUser
     * @return
     */
    @PutMapping("/add")
    public Result add(@RequestBody PreregisteredUser preregisteredUser) {
        String username = preregisteredUser.getUsername();
        Integer groupId = preregisteredUser.getGroupId();
        if(preRegisteredUserService.hadname(username)){
            return Result.error("改用户已预注册");
        }
        if(!preRegisteredUserService.hadGroupId(groupId)){
            return Result.error("不存在该小组id");
        }
        preRegisteredUserService.add(username,groupId);
        return Result.success("预注册成功！");
    }

    /**
     * 删除预注册用户
     * @param preregisteredUser
     * @return
     */
    @DeleteMapping("/delete")
    public Result delete(@RequestBody PreregisteredUser preregisteredUser) {
        String username = preregisteredUser.getUsername();
        preRegisteredUserService.delete(username);
        return Result.success("删除成功！");
    }
    @GetMapping("/getAll")
    public Result getAll(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         String username,
                         @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate begin,
                         @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate end) {
        PageBean pageBean = preRegisteredUserService.getAll(page,pageSize,username,begin,end);
        return Result.success(pageBean);
    }
}
