package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.DTO.AdminAttendanceStatusDTO;
import cn.nullcat.sckj.pojo.DTO.GroupAttendanceStatusDTO;
import cn.nullcat.sckj.pojo.DTO.UserFormDTO;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.pojo.VO.UserVO;
import cn.nullcat.sckj.service.AttendanceService;
import cn.nullcat.sckj.service.PreRegisteredUserService;
import cn.nullcat.sckj.service.UserService;
import cn.nullcat.sckj.utils.JwtUtils;
import cn.nullcat.sckj.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserContorller {
    @Autowired
    private UserService userservice;
    @Autowired
    private PreRegisteredUserService preRegisteredUserService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private TokenUtils tokenUtils;

    /**
     * 账号登录
     * @param userFormDTO
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserFormDTO userFormDTO) {
        if(userFormDTO.getUsername() == null || userFormDTO.getPassword() == null){
            return Result.error("请输入密码和用户名");
        }
        String username = userFormDTO.getUsername();
        String password = userFormDTO.getPassword();
        if(!userservice.exist(username)){
            return Result.error("用户名不存在");
        }
        if(!userservice.match(username,password)){
            return Result.error("密码错误");
        }
        Integer userId = userservice.getUserIdByUsername(username); // 根据用户名获取用户 ID
        //生成令牌并下发
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",userId);
        claims.put("username",username);
        claims.put("password",password);
        String jwt = JwtUtils.generateJwt(claims);
        tokenUtils.saveToken(jwt, userId);
        return Result.success(jwt);
    }

    /**
     * 账号注册
     * @param userFormDTO
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserFormDTO userFormDTO) {
        if(userFormDTO.getUsername() == null || userFormDTO.getPassword() == null){
            return Result.error("不允许为空");
        }
        String username = userFormDTO.getUsername();
        if(!preRegisteredUserService.exist(username)){
            return Result.error("用户名未预注册,请联系管理员");
        }
        userservice.register(userFormDTO);
        return Result.success();
    }

    /**
     * 修改密码
     * @param request
     * @return
     */
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody Map<String, String> passwords,HttpServletRequest request) {
        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");
        if(oldPassword == null || newPassword == null){
            return Result.error("不能为空");
        }
        Integer userIdNow = (Integer) request.getAttribute("userId");
        if(!oldPassword.equals(userservice.getPasswordById(userIdNow))){
            return Result.error("旧密码错误");
        }
        userservice.updatePassword(newPassword,userIdNow);
        return Result.success("修改成功");
    }

    /**
     * 获取个人信息
     * @param request
     * @return
     */
    @GetMapping("/onself")
    public Result onself(HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        UserVO userVO = userservice.getById(userIdNow);
        return Result.success(userVO);
    }

    /**
     * 退出登录
     * @return
     */
    @PostMapping("/logOut")
    public Result logOut(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        tokenUtils.removeToken(userId);
        userservice.clearUserCache(userId);
        return Result.success("退出成功");
    }

    /**
     * 查看用户身份
     * @param request
     * @return
     */
    @GetMapping("/getRole")
    public Result getRole(HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        UserVO userVO = userservice.getById(userIdNow);
        String role = userVO.getRole().toString();
        return Result.success(role);
    }

    /**
     * 修改用户身份
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/changeRole")
    public Result changeRole(@RequestParam Integer id, @RequestParam String role, HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        UserVO userVO = userservice.getById(userIdNow);
        String currentRole = userVO.getRole().toString();
        String admin = "ADMIN";
        if(!currentRole.equals(admin)){
            return Result.error("你无权修改用户身份");
        }
        userservice.changeRole(id, role);
        return Result.success("身份修改成功");
    }

    /**
     * 查看全部用户
     * @param page
     * @param pageSize
     * @param username
     * @param role
     * @param groupName
     * @param begin
     * @param end
     * @param request
     * @return
     */
    @GetMapping("/getAll")
    public Result getAll(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         String username,
                         String role,
                         String groupName,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                         HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        Integer groupIdNow = userservice.getGroupIdByUserId(userIdNow);
        log.info("人员分页条件查询:{},{},{},{},{},{},{}", page, pageSize, username,role,groupName, begin, end);
        PageBean pageBean = userservice.getAll(page, pageSize, username,groupName, role,begin, end);
        return Result.success(pageBean);
    }

    /**
     *
     * 根据id查修信息
     * @param userId
     * @return
     */
    @GetMapping("/getById/{userId}")
    public Result getById(@PathVariable Integer userId) {
        UserVO userVO = userservice.getById(userId);
        return Result.success(userVO);
    }

    /**
     * 获取本组今日考勤统计
     * @param request
     * @return
     */
    @GetMapping("/groupTodayStatus")
    public Result groupTodayStatus(HttpServletRequest request) {
        // 1. 获取当前登录用户的ID
        Integer userIdNow = (Integer) request.getAttribute("userId");

        // 2. 获取用户所在的组ID
        Integer groupIdNow = userservice.getGroupIdByUserId(userIdNow);

        // 3. 调用service方法获取统计数据
        GroupAttendanceStatusDTO statusDTO = attendanceService.getTodayGroupAttendanceStatus(groupIdNow);

        return Result.success(statusDTO);
    }

    /**
     * 获取全部今日统计
     * @return
     */
    @GetMapping("/admin/todayStatus")
    public Result adminTodayStatus() {
        AdminAttendanceStatusDTO statusDTO = attendanceService.getAdminTodayStatus();
        return Result.success(statusDTO);
    }
}
