package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.Equipment;
import cn.nullcat.sckj.pojo.PageBean;

import java.time.LocalDate;

public interface EquipmentService {
    /**
     *
     * @param equipment
     */
    void add(Equipment equipment);

    /**
     *
     * @param userIdNow
     * @param equipmentId
     */
    void borrow(Integer userIdNow, Integer equipmentId);

    /**
     *
     * @param userIdNow
     * @param equipmentId
     */
    void returnEquipment(Integer userIdNow, Integer equipmentId);

    /**
     *
     * @param equipmentId
     */
    void delete(Integer equipmentId);

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
    PageBean allEquipment(Integer page, Integer pageSize, String name, String description, LocalDate begin, LocalDate end);

    /**
     *
     * @param page
     * @param pageSize
     * @param equipmentId
     * @param begin
     * @param end
     * @return
     */
    PageBean getBorrowRecords(Integer page, Integer pageSize, Integer equipmentId, LocalDate begin, LocalDate end);

    /**
     * 回显，根据id查询
     * @param equipmentId
     * @return
     */
    Equipment getEquipmentById(Integer equipmentId);

    /**
     * 更新
     * @param equipment
     */
    void update(Equipment equipment);
}
