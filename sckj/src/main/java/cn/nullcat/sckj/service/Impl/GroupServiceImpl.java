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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        Integer groupId = groupDTO.getId();  // 改名为 groupId
        String name = groupDTO.getName();
        String description = groupDTO.getDescription();

        // 获取修改前的小组信息
        Group oldGroup = getById(String.valueOf(groupId));

        // 获取修改前后的管理员ID列表
        List<Integer> oldAdmins = Arrays.asList(oldGroup.getAdminA(), oldGroup.getAdminB(), oldGroup.getAdminC());
        List<Integer> newAdmins = Arrays.asList(
                userService.getUserIdByUsername(groupDTO.getAdminAName()),
                userService.getUserIdByUsername(groupDTO.getAdminBName()),
                userService.getUserIdByUsername(groupDTO.getAdminCName())
        );

        // 找出新增的管理员（在新列表中但不在旧列表中）
        List<Integer> addedAdmins = newAdmins.stream()
                .filter(adminId -> adminId != null && !oldAdmins.contains(adminId))  // 改名为 adminId
                .collect(Collectors.toList());

        // 找出被删除的管理员（在旧列表中但不在新列表中）
        List<Integer> removedAdmins = oldAdmins.stream()
                .filter(adminId -> adminId != null && !newAdmins.contains(adminId))  // 改名为 adminId
                .collect(Collectors.toList());

        // 将新增的管理员角色改为 LEADER
        for (Integer adminId : addedAdmins) {
            userService.changeRole(adminId, "LEADER");
        }

        // 将被删除的管理员角色改回 USER
        for (Integer adminId : removedAdmins) {
            userService.changeRole(adminId, "USER");
        }

        // 更新小组信息
        LocalDateTime now = LocalDateTime.now();
        groupMapper.update(groupId, name, description,
                userService.getUserIdByUsername(groupDTO.getAdminAName()),
                userService.getUserIdByUsername(groupDTO.getAdminBName()),
                userService.getUserIdByUsername(groupDTO.getAdminCName()),
                now);
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
