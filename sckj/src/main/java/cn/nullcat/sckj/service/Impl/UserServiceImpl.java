package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.UserMapper;
import cn.nullcat.sckj.pojo.DTO.UserFormDTO;
import cn.nullcat.sckj.pojo.Users;
import cn.nullcat.sckj.pojo.VO.UserVO;
import cn.nullcat.sckj.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 账号注册
     * @param userFormDTO
     */
    @Override
    public void register(UserFormDTO userFormDTO) {
        Users users = new Users();
        BeanUtils.copyProperties(userFormDTO, users);
        users.setRole(Users.Role.USER);
        users.setGroupId(1);
        users.setCreatedAt(LocalDateTime.now());
        users.setUpdatedAt(LocalDateTime.now());
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
}
