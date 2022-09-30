package com.su.blog.service;

import com.su.blog.entity.vo.CommentBo;
import com.su.blog.util.Result.ResultPage;

/**
 * @ClassName: CommentService
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/14 8:58
 * @Version:
 */
public interface CommentService {
    /**
     * 创建
     *
     * @param bo
     * @return
     */
    ResultPage create(CommentBo bo);

    void delete(String id);
}
