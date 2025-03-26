package cn.nullcat.sckj.service;

import cn.nullcat.sckj.pojo.Feedback;
import cn.nullcat.sckj.pojo.PageBean;

public interface FeedbackService {
    void put(Feedback feedback);

    PageBean getMyFeedback(Integer page, Integer pageSize, Integer userId);

    void resolved(Feedback feedback);

    PageBean getAllFeedback(Integer page, Integer pageSize);
}
