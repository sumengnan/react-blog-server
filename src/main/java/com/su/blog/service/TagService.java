package com.su.blog.service;

import com.su.blog.entity.vo.NameCountBo;

import java.util.List;

/**
 * @ClassName: TagService
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:55
 * @Version:
 */
public interface TagService {
    /**
     * 获取列表
     *
     * @return
     */
    List<NameCountBo> list();
}
