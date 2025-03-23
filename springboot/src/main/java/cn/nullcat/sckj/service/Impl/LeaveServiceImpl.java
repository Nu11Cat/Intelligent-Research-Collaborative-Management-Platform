package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.AttendanceMapper;
import cn.nullcat.sckj.mapper.LeaveMapper;
import cn.nullcat.sckj.mapper.UserMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.DTO.LeaveApplyDTO;
import cn.nullcat.sckj.pojo.DTO.LeaveApproveDTO;
import cn.nullcat.sckj.pojo.LeaveRecord;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.service.LeaveService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveMapper leaveMapper;
    @Autowired
    private AttendanceMapper attendanceMapper;
    @Autowired
    private UserMapper userMapper;


    @Override
    public boolean exist(Integer userIdNow, LocalDate leaveDate) {
        LeaveRecord leaveRecord = leaveMapper.getByUserIdAndLocalDate(userIdNow,leaveDate);
        return leaveRecord != null;
    }

    @Override
    public void apply(LeaveRecord leaveRecord) {
        leaveRecord.setStatus(0);
        leaveRecord.setCreateTime(LocalDateTime.now());
        leaveMapper.apply(leaveRecord);
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param groupIdNow
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean getUnaudited(Integer page, Integer pageSize, Integer groupIdNow, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<LeaveRecord> list = leaveMapper.getUnaudited(groupIdNow,begin, end);
        Page<LeaveRecord> p = (Page<LeaveRecord>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param groupIdNow
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean getAll(Integer page, Integer pageSize, Integer groupIdNow, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<LeaveRecord> list = leaveMapper.getAll(groupIdNow,begin, end);
        Page<LeaveRecord> p = (Page<LeaveRecord>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 查看本人请求
     * @param page
     * @param pageSize
     * @param userIdNow
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean getMyLeave(Integer page, Integer pageSize, Integer userIdNow, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<LeaveRecord> list = leaveMapper.getMyLeave(userIdNow,begin, end);
        Page<LeaveRecord> p = (Page<LeaveRecord>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 处理请求
     * @param leaveApproveDTO
     */
    @Override
    public void approve(Integer userIdNow,LeaveApproveDTO leaveApproveDTO) {
        Integer id = leaveApproveDTO.getId();
        Integer status = leaveApproveDTO.getStatus();
        LocalDateTime approveTime = LocalDateTime.now();
        String approverComment = leaveApproveDTO.getApproverComment();
        Integer approverId = userIdNow;
        leaveMapper.approve(id,status,approveTime,approverId,approverComment);
        if (status == 1) {  // 1表示通过
            // 获取请假记录信息
            LeaveRecord leaveRecord = leaveMapper.getById(id);
            String userName = userMapper.getUsernameById(leaveRecord.getUserId());
            // 添加考勤记录
            attendanceMapper.addLeaveRecord(
                    leaveRecord.getUserId(),
                    leaveRecord.getGroupId(),
                    userName,
                    leaveRecord.getLeaveDate()
            );
        }
    }
}
