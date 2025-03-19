package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.UserMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.DTO.UserFormDTO;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Users;
import cn.nullcat.sckj.pojo.VO.UserVO;
import cn.nullcat.sckj.service.PreRegisteredUserService;
import cn.nullcat.sckj.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PreRegisteredUserService preRegisteredUserService;

    /**
     * 账号注册
     * @param userFormDTO
     */
    @Override
    public void register(UserFormDTO userFormDTO) {
        Users users = new Users();
        BeanUtils.copyProperties(userFormDTO, users);
        users.setRole(Users.Role.USER);
        Integer groupId = preRegisteredUserService.getGroupIdByUserName(userFormDTO.getUsername());
        users.setGroupId(groupId);
        users.setCreatedAt(LocalDateTime.now());
        users.setUpdatedAt(LocalDateTime.now());
        preRegisteredUserService.chagneRegisteredStatus(userFormDTO.getUsername());
        userMapper.add(users);
    }

    /**
     * 检验账号是否存在
     * @param username
     * @return
     */
    @Override
    public boolean exist(String username) {
        Users users = userMapper.findByUsername(username);
        return users != null;
    }

    /**
     * 校验密码是否正确
     * @param username
     * @param password
     * @return
     */
    @Override
    public boolean match(String username, String password) {
        Users users = userMapper.findByUsernameAndPassword(username,password);
        return users != null;
    }

    /**
     * 根据用户名获取用户 ID
     * @param username
     * @return
     */
    @Override
    public Integer getUserIdByUsername(String username) {
        Integer userId = userMapper.getUserIdByUsername(username);
        return userId;
    }

    /**
     * 根据用户id查询用户组id
     * @param userIdNow
     * @return
     */
    @Override
    public Integer getGroupIdByUserId(Integer userIdNow) {
        return userMapper.getGroupIdByUserId(userIdNow);
    }

    /**
     *
     * @param userIdNow
     * @return
     */
    @Override
    public String getUsernameById(Integer userIdNow) {
        return userMapper.getUsernameById(userIdNow);
    }

    /**
     *
     * @param userIdNow
     * @return
     */
    @Override
    public String getGroupnameById(Integer userIdNow) {
        return userMapper.getGroupnameById(userIdNow);
    }

    /**
     *
     * @return
     */
    @Override
    public String getPasswordById(Integer userIdNow) {
        return userMapper.getPassWordById(userIdNow);
    }

    /**
     * 修改密码
     * @param newPassword
     * @param userIdNow
     */
    @Override
    public void updatePassword(String newPassword, Integer userIdNow) {
        userMapper.updatePassword(newPassword,userIdNow);
    }

    /**
     * 获取个人信息
     * @param userIdNow
     * @return
     */
    @Override
    public UserVO getById(Integer userIdNow) {
        Users users = userMapper.getById(userIdNow);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(users,userVO);
        userVO.setGroupName(userMapper.getGroupnameById(userIdNow));
        return userVO;
    }

    @Override
    public void changeRole(Integer id) {
        userMapper.changeRole(id);
    }

    /**
     * 查询全部信息
     * @param page
     * @param pageSize
     * @param username
     * @param role
     * @param groupName
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean getAll(Integer page, Integer pageSize, String username, String role, String groupName, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<Users> list = userMapper.getAll(username,groupName,begin,end);
        Page<Users> p = (Page<Users>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }
}
