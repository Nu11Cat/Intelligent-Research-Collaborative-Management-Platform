package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.DTO.SignInDTO;
import cn.nullcat.sckj.pojo.DTO.SignOutDTO;
import cn.nullcat.sckj.pojo.PageBean;

import java.time.LocalDate;

public interface AttendanceService {
    /**
     * 判断今日是否签到1
     * @param userIdNow
     * @return
     */
    boolean signedIn(Integer userIdNow);

    /**
     * 签到
     * @param userIdNow
     */
    void signIn(Integer userIdNow);

    /**
     * 判断今日是否签到2
     * @param userIdNow
     * @return
     */
    boolean signedOut(Integer userIdNow);

    /**
     * 签退
     * @param userIdNow
     */
    void signOut(Integer userIdNow);

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
    PageBean allRecords(Integer page, Integer pageSize, String username, String groupName, LocalDate begin, LocalDate end);

    /**
     * 获取个人考勤记录 分页条件查询
     * @param page
     * @param pageSize
     * @param userIdNow
     * @param begin
     * @param end
     * @return
     */
    PageBean myRecords(Integer page, Integer pageSize, Integer userIdNow,LocalDate begin, LocalDate end);

    /**
     * 获取小组考勤记录 分页条件查询
     * @param page
     * @param pageSize
     * @param groupIdNow
     * @param begin
     * @param end
     * @return
     */
    PageBean groupRecords(Integer page, Integer pageSize, Integer groupIdNow, LocalDate begin, LocalDate end);
}
