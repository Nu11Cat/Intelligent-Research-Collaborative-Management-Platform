package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.DTO.LeaveApplyDTO;
import cn.nullcat.sckj.pojo.DTO.LeaveApproveDTO;
import cn.nullcat.sckj.pojo.LeaveRecord;
import cn.nullcat.sckj.pojo.PageBean;

import java.time.LocalDate;

public interface LeaveService {
    /**
     * 查看本日是否有未审核请假
     * @param userIdNow
     * @param leaveDate
     * @return
     */
    boolean exist(Integer userIdNow, LocalDate leaveDate);

    /**
     *
     * @param leaveRecord
     */
    void apply(LeaveRecord leaveRecord);

    /**
     *
     * @param page
     * @param pageSize
     * @param groupIdNow
     * @param begin
     * @param end
     * @return
     */
    PageBean getUnaudited(Integer page, Integer pageSize, Integer groupIdNow, LocalDate begin, LocalDate end);

    /**
     *
     * @param page
     * @param pageSize
     * @param groupIdNow
     * @param begin
     * @param end
     * @return
     */
    PageBean getAll(Integer page, Integer pageSize, Integer groupIdNow, LocalDate begin, LocalDate end);

    /**
     *
     * @param page
     * @param pageSize
     * @param userIdNow
     * @param begin
     * @param end
     * @return
     */
    PageBean getMyLeave(Integer page, Integer pageSize, Integer userIdNow, LocalDate begin, LocalDate end);

    /**
     *
     * @param leaveApproveDTO
     */
    void approve(Integer userIdNow,LeaveApproveDTO leaveApproveDTO);
}
