package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.PageBean;

import java.time.LocalDate;

public interface ResourceLibraryService {

    boolean exit(Integer userIdNow);

    void add(Integer userIdNow,Integer groupId);


    void delete(Integer userIdNow);

    PageBean allResourceLibrary(Integer page, Integer pageSize, String username, String groupName, LocalDate begin, LocalDate end);


    Integer getLibraryIdByUserId(Integer userIdNow);
}
