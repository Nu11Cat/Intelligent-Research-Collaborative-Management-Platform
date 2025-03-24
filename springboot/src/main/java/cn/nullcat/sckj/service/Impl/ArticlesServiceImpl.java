package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.ArticlesMapper;
import cn.nullcat.sckj.pojo.Articles;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.service.ArticlesService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticlesServiceImpl implements ArticlesService {
    @Autowired
    private ArticlesMapper articlesMapper;

    @Override
    public void add(Articles articles) {
        articlesMapper.add(articles);
    }

    @Override
    public void delete(Integer id) {
        articlesMapper.delete(id);
    }

    @Override
    public void update(Articles articles) {
        articles.setUpdatedAt(LocalDateTime.now());
        articlesMapper.update(articles);
    }

    @Override
    public List<Articles> getByUserId(Integer userId) {
        List<Articles> list = articlesMapper.getByUserId(userId);
        return list;
    }

    @Override
    public PageBean getByLibraryId(Integer page, Integer pageSize, Integer libraryId, String title, LocalDate begin, LocalDate end) {
        PageHelper.startPage(page, pageSize);
        List<Articles> list = articlesMapper.getByLibraryId(libraryId,title,begin, end);
        Page<Articles> p = (Page<Articles>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }
}
