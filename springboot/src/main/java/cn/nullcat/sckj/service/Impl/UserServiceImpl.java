package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.UserMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.DTO.UserFormDTO;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Users;
import cn.nullcat.sckj.pojo.VO.UserVO;
import cn.nullcat.sckj.service.PreRegisteredUserService;
import cn.nullcat.sckj.service.UserService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PreRegisteredUserService preRegisteredUserService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
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
        UserVO userVO = getById(userIdNow);
        return userVO.getGroupId();
    }

    /**
     *
     * @param userIdNow
     * @return
     */
    @Override
    public String getUsernameById(Integer userIdNow) {
        UserVO userVO = getById(userIdNow);
        return userVO.getUsername();
    }

    /**
     *
     * @param userIdNow
     * @return
     */
    @Override
    public String getGroupnameById(Integer userIdNow) {
        UserVO userVO = getById(userIdNow);
        return userVO.getGroupName();
    }

    /**
     *
     * @return
     */
    @Override
    public String getPasswordById(Integer userIdNow) {
        UserVO userVO = getById(userIdNow);
        return userVO.getPassword();
    }

    /**
     * 修改密码
     * @param newPassword
     * @param userIdNow
     */
    @Override
    public void updatePassword(String newPassword, Integer userIdNow) {
        userMapper.updatePassword(newPassword,userIdNow);
        clearUserCache(userIdNow);
    }

    /**
     * 获取个人信息
     * @param userIdNow
     * @return
     */
    @Override
    public UserVO getById(Integer userIdNow) {
        // 先从Redis获取
        String key = "user:" + userIdNow;
        String userJson = redisTemplate.opsForValue().get(key);
        if (userJson != null) {
            return JSON.parseObject(userJson, UserVO.class);
        }

        // Redis没有，从数据库获取
        Users users = userMapper.getById(userIdNow);
        if (users == null) {
            return null;
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(users, userVO);

        // 存入Redis，设置30分钟过期
        redisTemplate.opsForValue().set(key, JSON.toJSONString(userVO), 30, TimeUnit.MINUTES);

        return userVO;
    }

    @Override
    public void changeRole(Integer id, String role) {
        userMapper.changeRole(id, role);
        clearUserCache(id);
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
    public PageBean getAll(Integer page, Integer pageSize, String username, String groupName,String role ,LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<Users> list = userMapper.getAll(username, groupName, role , begin, end);
        Page<Users> p = (Page<Users>) list;

        // 将 Users 转换为 UserVO
        List<UserVO> voList = p.getResult().stream()
                .map(user -> {
                    UserVO vo = new UserVO();
                    vo.setUsername(user.getUsername());
                    vo.setRole(user.getRole());
                    vo.setCreatedAt(user.getCreatedAt());
                    vo.setUpdatedAt(user.getUpdatedAt());
                    vo.setGroupName(userMapper.getGroupnameById(user.getId()));
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageBean(p.getTotal(), voList);
    }

    /**
     * 清除redis的用户信息
     * @param userId
     */
    @Override
    public void clearUserCache(Integer userId) {
        String key = "user:" + userId;
        redisTemplate.delete(key);
    }
}
