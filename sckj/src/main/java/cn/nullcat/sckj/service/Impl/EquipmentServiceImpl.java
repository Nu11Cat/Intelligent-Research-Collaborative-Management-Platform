package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.EquipmentMapper;
import cn.nullcat.sckj.mapper.GroupMapper;
import cn.nullcat.sckj.mapper.UserMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.Equipment;
import cn.nullcat.sckj.pojo.EquipmentBorrow;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.VO.EquipmentBorrowVO;
import cn.nullcat.sckj.service.EquipmentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GroupMapper groupMapper;
    /**
     *
     * @param equipment
     */
    @Override
    public void add(Equipment equipment) {
        String name = equipment.getName();
        String description = equipment.getDescription();
        Integer level = equipment.getLevel();
        String imageUrl = equipment.getImageUrl();
        LocalDateTime now = LocalDateTime.now();
        equipmentMapper.add(name,description,level,imageUrl,now);
    }

    /**
     * 借用器材
     * @param userIdNow
     * @param equipmentId
     */
    @Override
    public void borrow(Integer userIdNow, Integer equipmentId) {
        LocalDateTime now = LocalDateTime.now();
        equipmentMapper.borrow(userIdNow,equipmentId,now);
        equipmentMapper.toUnable(equipmentId);
    }

    /**
     * 归还器材
     * @param userIdNow
     * @param equipmentId
     */
    @Override
    public void returnEquipment(Integer userIdNow, Integer equipmentId) {
       LocalDateTime now = LocalDateTime.now();
       equipmentMapper.returnEquipment(userIdNow,equipmentId,now);
       equipmentMapper.toAble(equipmentId);
    }

    /**
     * 删除器材
     * @param equipmentId
     */
    @Override
    public void delete(Integer equipmentId) {
        LocalDateTime now = LocalDateTime.now();
        equipmentMapper.delete(equipmentId);
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param name
     * @param description
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean allEquipment(Integer page, Integer pageSize, String name, String description, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<Equipment> list = equipmentMapper.allEquipment(name, description,begin, end);
        Page<Equipment> p = (Page<Equipment>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param equipmentId
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean getBorrowRecords(Integer page, Integer pageSize, Integer equipmentId, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<EquipmentBorrowVO> list = equipmentMapper.getBorrowRecords(equipmentId,begin, end);
        Page<EquipmentBorrowVO> p = (Page<EquipmentBorrowVO>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 回显，根据id查询信息
     * @param equipmentId
     * @return
     */
    @Override
    public Equipment getEquipmentById(Integer equipmentId) {
        Equipment equipment = equipmentMapper.getEquipmentById(equipmentId);
        return equipment;
    }

    /**
     * 更新
     * @param equipment
     */
    @Override
    public void update(Equipment equipment) {
        Integer id = equipment.getId();
        String name = equipment.getName();
        String description = equipment.getDescription();
        Integer level = equipment.getLevel();
        String imageUrl = equipment.getImageUrl();
        equipmentMapper.update(id,name,description,level,imageUrl);
    }

}
