package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.AttendanceMapper;
import cn.nullcat.sckj.mapper.EquipmentMapper;
import cn.nullcat.sckj.mapper.GroupMapper;
import cn.nullcat.sckj.mapper.UserMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.DTO.AdminAttendanceStatusDTO;
import cn.nullcat.sckj.pojo.DTO.GroupAttendanceStatusDTO;
import cn.nullcat.sckj.pojo.DTO.SignInDTO;
import cn.nullcat.sckj.pojo.DTO.SignOutDTO;
import cn.nullcat.sckj.pojo.Group;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Users;
import cn.nullcat.sckj.service.AttendanceService;
import cn.nullcat.sckj.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GroupMapper groupMapper;
    /**
     * 判断今日是否签到1
     * @return
     */
    @Override
    public boolean signedIn(Integer userIdNow) {
        Integer userId = userIdNow;
        Integer groupId = userService.getGroupIdByUserId(userIdNow);
        LocalDateTime time = LocalDateTime.now();
        Attendance users = attendanceMapper.getBySignIn(userId,groupId,time);
        return users != null;
    }

    /**
     * 签到
     * @param userIdNow
     */
    @Override
    public void signIn(Integer userIdNow) {
        Integer groupId = userService.getGroupIdByUserId(userIdNow);
        LocalDateTime time = LocalDateTime.now();
        String userName = userService.getUsernameById(userIdNow);

        // 先查询今天是否有记录
        Attendance todayRecord = attendanceMapper.getTodayRecord(userIdNow, groupId, time);

        if (todayRecord == null) {
            // 没有记录，新增
            attendanceMapper.add(userIdNow, groupId, time, userName);
        } else {
            // 有记录，更新签到时间
            attendanceMapper.updateCheckIn(userIdNow, time);
        }
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
        Attendance users = attendanceMapper.getBySignOut(userId,time);
        return users == null;
    }

    /**
     * 签退
     * @param userIdNow
     */
    @Override
    public void signOut(Integer userIdNow) {
        LocalDateTime time = LocalDateTime.now();
        attendanceMapper.add2(userIdNow, time);
    }

    /**
     * 获取全部考勤记录 分页条件查询
     * @param page
     * @param pageSize
     * @param username
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean allRecords(Integer page, Integer pageSize, String username, String groupName,LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<Attendance> list = attendanceMapper.allRecord(username,groupName,begin, end);
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

    /**
     *
     * @param groupIdNow
     * @return
     */
    @Override
    public GroupAttendanceStatusDTO getTodayGroupAttendanceStatus(Integer groupIdNow) {
        // 1. 创建返回对象
        GroupAttendanceStatusDTO statusDTO = new GroupAttendanceStatusDTO();

        // 2. 获取本组所有成员
        List<Users> allMembers = userMapper.getByGroupId(groupIdNow);
        statusDTO.setTotalMembers(allMembers.size());

        // 3. 获取今日考勤记录
        LocalDate today = LocalDate.now();
        List<Attendance> todayRecords = attendanceMapper.getTodayGroupAttendance(groupIdNow, today);

        // 4. 统计已签到人数（减去请假人数）
        long checkedInCount = todayRecords.stream()
                .filter(record -> record.getIsLeave() == 0)
                .count();
        statusDTO.setCheckedInCount((int) checkedInCount);

        // 5. 统计已签退人数（减去请假人数）
        long checkedOutCount = todayRecords.stream()
                .filter(record -> record.getCheckOut() != null && record.getIsLeave() == 0)
                .count();
        statusDTO.setCheckedOutCount((int) checkedOutCount);

        // 6. 获取未签到人员名单
        Set<Integer> checkedInUserIds = todayRecords.stream()
                .filter(record -> record.getIsLeave() == 0)
                .map(Attendance::getUserId)
                .collect(Collectors.toSet());

        List<String> notCheckedInUsers = allMembers.stream()
                .filter(member -> !checkedInUserIds.contains(member.getId()))
                .map(Users::getUsername)
                .collect(Collectors.toList());
        statusDTO.setNotCheckedInUsers(notCheckedInUsers);

        // 7. 获取未签退人员名单
        List<String> notCheckedOutUsers = todayRecords.stream()
                .filter(record -> record.getCheckOut() == null && record.getIsLeave() == 0)
                .map(Attendance::getUserName)
                .collect(Collectors.toList());
        statusDTO.setNotCheckedOutUsers(notCheckedOutUsers);

        // 8. 获取今日请假记录
        List<Attendance> leaveRecords = todayRecords.stream()
                .filter(record -> record.getIsLeave() == 1)
                .collect(Collectors.toList());

        // 9. 统计请假人数
        statusDTO.setLeaveCount(leaveRecords.size());

        // 10. 获取请假人员名单
        List<String> leaveUsers = leaveRecords.stream()
                .map(Attendance::getUserName)
                .collect(Collectors.toList());
        statusDTO.setLeaveUsers(leaveUsers);
        return statusDTO;
    }

    /**
     *
     * @return
     */
    @Override
    public AdminAttendanceStatusDTO getAdminTodayStatus() {
        AdminAttendanceStatusDTO adminDTO = new AdminAttendanceStatusDTO();

        // 1. 获取所有组
        List<Group> allGroups = groupMapper.getAll(null,null,null);
        adminDTO.setTotalGroups(allGroups.size());

        // 2. 获取系统总人数
        Integer totalUsers = userMapper.getTotalUsers();
        adminDTO.setTotalUsers(totalUsers);

        // 3. 获取各组统计
        List<GroupAttendanceStatusDTO> groupStats = new ArrayList<>();
        int totalCheckedIn = 0;
        int totalCheckedOut = 0;
        int totalLeave = 0;

        // 复用现有方法获取各组统计
        for (Group group : allGroups) {
            GroupAttendanceStatusDTO groupStat = getTodayGroupAttendanceStatus(group.getId());
            // 设置小组名称
            groupStat.setGroupName(group.getName());
            groupStats.add(groupStat);

            // 累加总数（不包含请假人数，因为 getTodayGroupAttendanceStatus 已经减去了请假人数）
            totalCheckedIn += groupStat.getCheckedInCount();
            totalCheckedOut += groupStat.getCheckedOutCount();
            totalLeave += groupStat.getLeaveCount();
        }

        // 4. 设置总体统计数据
        adminDTO.setTotalCheckedIn(totalCheckedIn);
        adminDTO.setTotalCheckedOut(totalCheckedOut);
        adminDTO.setTotalLeave(totalLeave);
        adminDTO.setGroupStats(groupStats);

        return adminDTO;
    }
}
