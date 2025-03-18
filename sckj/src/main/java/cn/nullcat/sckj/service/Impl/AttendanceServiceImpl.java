package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.AttendanceMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.DTO.SignInDTO;
import cn.nullcat.sckj.pojo.DTO.SignOutDTO;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Users;
import cn.nullcat.sckj.service.AttendanceService;
import cn.nullcat.sckj.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;
    @Autowired
    private UserService userService;
    /**
     * 判断今日是否签到1
     * @return
     */
    @Override
    public boolean signedIn(Integer userIdNow) {
        Integer userId = userIdNow;
        Integer groupId = userService.getGroupIdByUserId(userIdNow);
        LocalDateTime time = LocalDateTime.now();
        Users users = attendanceMapper.getBySignIn(userId,groupId,time);
        return users != null;
    }

    /**
     * 签到
     * @param userIdNow
     */
    @Override
    public void signIn(Integer userIdNow) {
        Integer userId = userIdNow;
        Integer groupId = userService.getGroupIdByUserId(userIdNow);
        LocalDateTime time = LocalDateTime.now();
        String userName = userService.getUsernameById(userIdNow);
        String groupName = userService.getGroupnameById(userIdNow);
        attendanceMapper.add(userId,groupId,time,userName,groupName);
    }

    /**
     * 判断今日是否签到2
     * @param userIdNow
     * @return
     */
    @Override
    public boolean signedOut(Integer userIdNow) {
        Integer userId = userIdNow;
        LocalDateTime time = LocalDateTime.now();
        Users users = attendanceMapper.getBySignOut(userId,time);
        return users == null;
    }

    /**
     * 签退
     * @param userIdNow
     */
    @Override
    public void signOut(Integer userIdNow) {
        Integer userId = userIdNow;
        LocalDateTime time = LocalDateTime.now();
        attendanceMapper.add2(userId,time);
    }

    /**
     * 获取全部考勤记录 分页条件查询
     * @param page
     * @param pageSize
     * @param username
     * @param groupName
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean allRecords(Integer page, Integer pageSize, String username, String groupName, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<Attendance> list = attendanceMapper.allRecord(username, groupName,begin, end);
        Page<Attendance> p = (Page<Attendance>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 获取个人考勤记录 分页条件查询
     * @param page
     * @param pageSize
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean myRecords(Integer page, Integer pageSize ,Integer userIdNow,LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<Attendance> list = attendanceMapper.myRecord(userIdNow,begin, end);
        Page<Attendance> p = (Page<Attendance>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 获取小组考勤记录 分页条件查询
     * @param page
     * @param pageSize
     * @param groupIdNow
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean groupRecords(Integer page, Integer pageSize, Integer groupIdNow, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<Attendance> list = attendanceMapper.groupRecord(groupIdNow,begin, end);
        Page<Attendance> p = (Page<Attendance>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }
}
