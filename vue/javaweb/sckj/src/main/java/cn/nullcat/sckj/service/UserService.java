package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.DTO.UserFormDTO;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.VO.UserVO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

public interface UserService {
    /**
     * 账号注册
     * @param userFormDTO
     */
    void register(UserFormDTO userFormDTO);

    /**
     * 校验账号是否存在
     * @param username
     * @return
     */
    boolean exist(String username);

    /**
     * 密码正确
     * @param password
     * @return
     */
    boolean match(String username,String password);

    /**
     * 根据用户名获取用户 ID
     * @param username
     * @return
     */
    Integer getUserIdByUsername(String username);

    /**
     * 根据用户id查询用户组id
     * @param userIdNow
     * @return
     */
    Integer getGroupIdByUserId(Integer userIdNow);
    /**
     * 根据用户id查询用户用户名
     * @param userIdNow
     * @return
     */
    String getUsernameById(Integer userIdNow);

    /**
     * 根据用户id查询组名
     * @param userIdNow
     * @return
     */
    String getGroupnameById(Integer userIdNow);

    /**
     * 根据id查询密码
     * @return
     */
    String getPasswordById(Integer userIdNow);

    /**
     * 修改密码
     * @param newPassword
     * @param userIdNow
     */
    void updatePassword(String newPassword, Integer userIdNow);

    /**
     * 获取个人信息
     * @param userIdNow
     * @return
     */
    UserVO getById(Integer userIdNow);

    /**
     * 修改个人信息
     * @param id
     */
    void changeRole(Integer id);

    /**
     * 条件分页查询全部用户
     * @param page
     * @param pageSize
     * @param username
     * @param role
     * @param groupName
     * @param begin
     * @param end
     * @return
     */
    PageBean getAll(Integer page, Integer pageSize, String username, String groupName, String role, LocalDate begin, LocalDate end);
}
