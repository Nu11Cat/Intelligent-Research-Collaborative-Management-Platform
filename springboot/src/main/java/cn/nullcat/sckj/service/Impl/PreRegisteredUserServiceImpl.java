package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.PreRegisteredUserMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.Group;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.PreregisteredUser;
import cn.nullcat.sckj.service.PreRegisteredUserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PreRegisteredUserServiceImpl implements PreRegisteredUserService {

    @Autowired
    private PreRegisteredUserMapper preRegisteredUserMapper;
    /**
     * 判断名字是否存在
     * @param username
     * @return
     */
    @Override
    public boolean hadname(String username) {
        PreregisteredUser preregisteredUser = preRegisteredUserMapper.getByName(username);
        return preregisteredUser != null;
    }

    /**
     * 判断小组是否存在
     * @param groupId
     * @return
     */
    @Override
    public boolean hadGroupId(Integer groupId) {
        Group group = preRegisteredUserMapper.getByGroupId(groupId);
        return group != null;
    }

    /**
     * 预注册用户
     * @param username
     * @param groupId
     */
    @Override
    public void add(String username, Integer groupId) {
        String notadd = "未注册";
        LocalDateTime now = LocalDateTime.now();
        preRegisteredUserMapper.add(username,groupId,notadd,now);
    }

    /**
     * 删除
     * @param username
     */
    @Override
    public void delete(String username) {
        preRegisteredUserMapper.delete(username);
    }

    /**
     * 条件分页查询
     * @param page
     * @param pageSize
     * @param username
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean getAll(Integer page, Integer pageSize, String username, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<PreregisteredUser> list = preRegisteredUserMapper.getAll(username,begin, end);
        Page<PreregisteredUser> p = (Page<PreregisteredUser>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 预注册
     * @param username
     * @return
     */
    @Override
    public boolean exist(String username) {
        PreregisteredUser user = preRegisteredUserMapper.getByName(username);
        return user != null;
    }

    /**
     * 根据用户名字查询用户小组
     * @param username
     * @return
     */
    @Override
    public Integer getGroupIdByUserName(String username) {
        return preRegisteredUserMapper.getGroupIdByUserName(username);
    }

    /**
     * 修改用户注册状态
     * @param username
     */
    @Override
    public void chagneRegisteredStatus(String username) {
        preRegisteredUserMapper.chagneRegisteredStatus(username);
    }
}
