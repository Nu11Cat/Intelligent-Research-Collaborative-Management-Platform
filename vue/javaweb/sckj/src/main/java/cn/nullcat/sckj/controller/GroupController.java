package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.DTO.GroupDTO;
import cn.nullcat.sckj.pojo.Group;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.service.GroupService;
import cn.nullcat.sckj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;

    /**
     * 添加
     * @param group
     * @return
     */
    @PutMapping("/add")
    public Result add(@RequestBody Group group) {
        String name = group.getName();
        String description = group.getDescription();
        if(groupService.getByGruopName(name)){
            return Result.error("该组已经存在");
        }
        groupService.add(name,description);
        return Result.success("添加成功");

    }

    /**
     * 删除
     * @param group
     * @return
     */
    @DeleteMapping("/delete")
    public Result delete(@RequestBody Group group) {
        String name = group.getName();
        groupService.delete(name);
        return Result.success("删除成功");
    }

    /**
     * 根据id查询小组
     * @param id
     * @return
     */
    @GetMapping
    public Result getById(@RequestParam String id) {
        Group group = groupService.getById(id);
        return Result.success(group);
    }
    /**
     * 更新
     * @param groupDTO
     * @return
     */
    @PostMapping("/update")
    public Result edit(@RequestBody GroupDTO groupDTO) {
        String name = groupDTO.getName();
        Group existingGroup = groupService.getById(String.valueOf(groupDTO.getId()));
        if(!existingGroup.getName().equals(name) && groupService.getByGruopName(name)){
            return Result.error("该组已经存在");
        }
        String  leaderAName = groupDTO.getAdminAName();
        String leaderBName = groupDTO.getAdminBName();
        String leaderCName = groupDTO.getAdminCName();
        if(leaderAName != null && !leaderAName.trim().isEmpty() && !userService.exist(leaderAName)){
            return Result.error("用户不存在");
        }
        if(leaderBName != null && !leaderBName.trim().isEmpty() && !userService.exist(leaderBName)){
            return Result.error("用户不存在");
        }
        if(leaderCName != null && !leaderCName.trim().isEmpty() && !userService.exist(leaderCName)){
            return Result.error("用户不存在");
        }
        groupService.update(groupDTO);
        return Result.success("修改成功");
    }

    /**
     * 条件发分页查询
     * @param page
     * @param pageSize
     * @param name
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/getAll")
    public Result getAll(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         String name,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        PageBean pageBean = groupService.getAll(page,pageSize,name,begin,end);
        return Result.success(pageBean);
    }
}
