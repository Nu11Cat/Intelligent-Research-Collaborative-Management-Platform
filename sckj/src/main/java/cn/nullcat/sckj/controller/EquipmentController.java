package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.Equipment;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.service.EquipmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    @Autowired
    private EquipmentService equipmentService;

    /**
     * 添加器材
     * @param equipment
     * @return
     */
    @PutMapping("/add")
    private Result addEquipment(@RequestBody Equipment equipment) {
        equipmentService.add(equipment);
        return Result.success("添加成功");
    }

    /**
     * 删除
     * @param equipmentId
     * @return
     */
    @DeleteMapping("/delete")
    private Result deleteEquipment(@RequestParam Integer equipmentId) {
        equipmentService.delete(equipmentId);
        return Result.success("删除成功");
    }
    /**
     * 借用器材
     * @param equipmentId
     * @return
     */
    @PostMapping("/borrow")
    private Result borrowEquipment(@RequestParam Integer equipmentId, HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        equipmentService.borrow(userIdNow,equipmentId);
        return Result.success("借用成功");
    }

    /**
     * 归还器材
     * @param equipmentId
     * @param request
     * @return
     */
    @PostMapping("/return")
    private Result returnEquipment(@RequestParam Integer equipmentId, HttpServletRequest request) {
        Integer userIdNow = (Integer) request.getAttribute("userId");
        if(equipmentService.notYourBorrow(equipmentId,userIdNow)){
            return Result.error("不是你的借用，你无权归还");
        }
        equipmentService.returnEquipment(userIdNow,equipmentId);
        return Result.success("归还成功");
    }

    /**
     * 分页查询器材列表
     * @return
     */
    @GetMapping
    private Result getEquipment(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                String name,
                                String description,
                                @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate begin,
                                @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate end) {
        log.info("查询器材：{},{},{},{},{},{}",page,pageSize,name,description,begin,end);
        PageBean pageBean = equipmentService.allEquipment(page,pageSize,name,description,begin,end);
        return Result.success(pageBean);
    }

    /**
     * 查看借用记录
     * @param page
     * @param pageSize
     * @param equipmentId
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/borrowRecords")
    private Result getBorrowRecords(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam Integer equipmentId,
                                    @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate begin,
                                    @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate end){
        PageBean pageBean = equipmentService.getBorrowRecords(page,pageSize,equipmentId,begin,end);
        return Result.success(pageBean);
    }

    /**
     * 根据id查看信息：回显
     * @param equipmentId
     * @return
     */
    @GetMapping("/getById")
    private Result getEquipmentById(@RequestParam Integer equipmentId) {
        Equipment equipment =  equipmentService.getEquipmentById(equipmentId);
        return Result.success(equipment);
    }
    /**
     * 更新器材信息
     * @param equipment
     * @return
     */
    @PostMapping("/update")
    private Result updateEquipment(@RequestBody Equipment equipment) {
        equipmentService.update(equipment);
        return Result.success("修改成功！");
    }
}
