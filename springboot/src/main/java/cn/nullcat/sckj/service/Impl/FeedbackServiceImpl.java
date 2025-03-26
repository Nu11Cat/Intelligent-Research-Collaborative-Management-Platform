package cn.nullcat.sckj.service.Impl;

import cn.nullcat.sckj.mapper.FeedbackMapper;
import cn.nullcat.sckj.pojo.Attendance;
import cn.nullcat.sckj.pojo.Feedback;
import cn.nullcat.sckj.pojo.PageBean;
import cn.nullcat.sckj.service.FeedbackService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackMapper feedbackMapper;

    /**
     * 提交反馈
     * @param feedback
     */
    @Override
    public void put(Feedback feedback) {
        feedbackMapper.put(feedback);
    }

    /**
     * 查看我的反馈
     * @param page
     * @param pageSize
     * @param userId
     * @return
     */
    @Override
    public PageBean getMyFeedback(Integer page, Integer pageSize, Integer userId) {
        PageHelper.startPage(page, pageSize);
        List<Feedback> list = feedbackMapper.getMyFeedback(userId);
        Page<Feedback> p = (Page<Feedback>) list;
        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 处理反馈
     * @param feedback
     */
    @Override
    public void resolved(Feedback feedback) {
        feedbackMapper.resolved(feedback);
    }

    /**
     * 查看全部反馈
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageBean getAllFeedback(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Feedback> list = feedbackMapper.getAllFeedback();
        Page<Feedback> p = (Page<Feedback>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }
}
