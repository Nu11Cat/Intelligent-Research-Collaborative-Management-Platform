package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.GroupMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.DTO.GroupDTO;
import cn.nullcat.sckj.pojo.Group;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.service.GroupService;
import cn.nullcat.sckj.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private UserService userService;

    /**
     *
     * @param name
     * @return
     */
    @Override
    public boolean getByGruopName(String name) {
        Group group = groupMapper.getByGruopName(name);
        return group != null;
    }

    /**
     * 添加小组
     * @param name
     * @param description
     */
    @Override
    public void add(String name, String description) {
        LocalDateTime now = LocalDateTime.now();
        groupMapper.add(name,description,now);
    }

    /**
     * 删除
     * @param name
     */
    @Override
    public void delete(String name) {
        groupMapper.delete(name);
    }

    /**
     * 根据id查询小组
     * @param id
     * @return
     */
    @Override
    public Group getById(String id) {
        return groupMapper.getById(id);
    }

    /**
     * 编辑小组
     * @param groupDTO
     */
    @Override
    public void update(GroupDTO groupDTO) {
        Integer id = groupDTO.getId();
        String name = groupDTO.getName();
        String description = groupDTO.getDescription();
        String  leaderAName = groupDTO.getAdminAName();
        Integer leaderA = userService.getUserIdByUsername(leaderAName);
        userService.changeRole(leaderA);
        String leaderBName = groupDTO.getAdminBName();
        Integer leaderB = userService.getUserIdByUsername(leaderBName);
        userService.changeRole(leaderB);
        String leaderCName = groupDTO.getAdminCName();
        Integer leaderC = userService.getUserIdByUsername(leaderCName);
        userService.changeRole(leaderC);
        LocalDateTime now = LocalDateTime.now();
        groupMapper.update(id,name,description,leaderA,leaderB,leaderC,now);
    }

    @Override
    public PageBean getAll(Integer page, Integer pageSize, String name, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<Group> list = groupMapper.getAll(name,begin, end);
        Page<Group> p = (Page<Group>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }
}
