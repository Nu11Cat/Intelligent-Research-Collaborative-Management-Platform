package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.Group;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.service.GroupService;
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
     * @param group
     * @return
     */
    @PostMapping("/update")
    public Result edit(@RequestBody Group group) {
        String name = group.getName();
        if(groupService.getByGruopName(name)){
            return Result.error("该组已经存在");
        }
        groupService.update(group);
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
                         @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate begin,
                         @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate end) {
        PageBean pageBean = groupService.getAll(page,pageSize,name,begin,end);
        return Result.success(pageBean);
    }
}
