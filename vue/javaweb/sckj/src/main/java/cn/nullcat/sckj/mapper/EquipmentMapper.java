package cn.nullcat.sckj.mapper;

import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.Equipment;
import cn.nullcat.sckj.pojo.EquipmentBorrow;
import cn.nullcat.sckj.pojo.VO.EquipmentBorrowVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EquipmentMapper {
    /**
     * 新增
     * @param name
     * @param description
     * @param level
     * @param imageUrl
     * @param now
     */
    @Insert("INSERT INTO equipment (name, description, level, image_url, created_at) " +
            "VALUES (#{name}, #{description}, #{level}, #{imageUrl}, #{now})")
    void add(String name, String description, Integer level, String imageUrl, LocalDateTime now);

    /**
     * 借用
     * @param userIdNow
     * @param equipmentId
     * @param now
     */
    @Insert("INSERT INTO equipment_borrow (user_id, equipment_id, borrow_time) VALUES (#{userIdNow}, #{equipmentId}, #{now})")
    void borrow(Integer userIdNow, Integer equipmentId, LocalDateTime now);

    /**
     *
     * 归还
     * @param userIdNow
     * @param equipmentId
     * @param now
     */
    @Update("UPDATE equipment_borrow SET return_time = #{now} WHERE user_id = #{userIdNow} AND equipment_id = #{equipmentId}")
    void returnEquipment(Integer userIdNow, Integer equipmentId, LocalDateTime now);

    /**
     * 删除
     * @param equipmentId
     */
    @Delete("delete from equipment where id = #{equipmentId}")
    void delete(Integer equipmentId);

    /**
     * 分页获取全部器材列表
     * @param name
     * @param description
     * @param begin
     * @param end
     * @return
     */
    List<Equipment> allEquipment(String name, String description, LocalDate begin, LocalDate end);

    /**
     * 改为已借出
     * @param equipmentId
     */
    @Update("UPDATE equipment SET status = 1 WHERE id = #{equipmentId} AND status = 0 ")
    void toUnable(Integer equipmentId);

    /**
     * 改为可借用
     * @param equipmentId
     */
    @Update("UPDATE equipment SET status = 0 WHERE id = #{equipmentId}")
    void toAble(Integer equipmentId);

    /**
     * 根据id查询当前器材借用记录
     * @param equipmentId
     * @param begin
     * @param end
     * @return
     */
    List<EquipmentBorrowVO> getBorrowRecords(Integer equipmentId, LocalDate begin, LocalDate end);

    /**
     * 回显
     * @param equipmentId
     * @return
     */
    @Select("select * from equipment where id = #{equipmentId}")
    Equipment getEquipmentById(Integer equipmentId);

    /**
     * 更新
     * @param id
     * @param name
     * @param description
     * @param level
     * @param imageUrl
     */
    void update(Integer id, String name, String description, Integer level, String imageUrl);

    @Select("select * from equipment_borrow where user_id = #{userIdNow} and equipment_id = #{equipmentId} and return_time is null")
    EquipmentBorrow notYourBorrow(Integer equipmentId, Integer userIdNow);
}
