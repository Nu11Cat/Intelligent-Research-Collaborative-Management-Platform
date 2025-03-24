package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.Articles;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.PageBean;

import java.time.LocalDate;
import java.util.List;

public interface ArticlesService {

    void add(Articles articles);

    void delete(Integer id);

    void update(Articles articles);

    List<Articles> getByUserId(Integer userId);

    PageBean getByLibraryId(Integer page, Integer pageSize, Integer libraryId, String title, LocalDate begin, LocalDate end);
}
