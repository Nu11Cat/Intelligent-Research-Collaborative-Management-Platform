package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.ResourceLibraryMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.pojo.ResourceLibrary;
import cn.nullcat.sckj.service.ResourceLibraryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResourceLibraryServiceImpl implements ResourceLibraryService {

    @Autowired
    private ResourceLibraryMapper resourceLibraryMapper;

    /**
     * 根据id获取资料库
     * @param userIdNow
     * @return
     */
    @Override
    public boolean exit(Integer userIdNow) {
        ResourceLibrary resourceLibrary = resourceLibraryMapper.getById(userIdNow);
        return resourceLibrary != null;
    }

    /**
     * 添加自己的资料库
     * @param userIdNow
     */
    @Override
    public void add(Integer userIdNow,Integer groupId) {
        ResourceLibrary resourceLibrary = new ResourceLibrary();
        resourceLibrary.setUserId(userIdNow);
        resourceLibrary.setGroupId(groupId);
        resourceLibrary.setCreatedAt(LocalDateTime.now());
        resourceLibrary.setUpdatedAt(LocalDateTime.now());
        resourceLibraryMapper.add(resourceLibrary);
    }

    /**
     * 删除自己的资料库
     * @param userIdNow
     */
    @Override
    public void delete(Integer userIdNow) {
        resourceLibraryMapper.delete(userIdNow);
    }

    /**
     * 分页条件查询全部资料库
     * @param page
     * @param pageSize
     * @param username
     * @param groupName
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean allResourceLibrary(Integer page, Integer pageSize, String username, String groupName, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<ResourceLibrary> list = resourceLibraryMapper.getAll(username,groupName,begin, end);
        Page<ResourceLibrary> p = (Page<ResourceLibrary>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    @Override
    public Integer getLibraryIdByUserId(Integer userIdNow) {
        return resourceLibraryMapper.getLibraryIdByUserId(userIdNow);
    }
}
