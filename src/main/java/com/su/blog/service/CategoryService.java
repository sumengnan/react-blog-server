package com.su.blog.service;

import com.su.blog.entity.vo.NameCountBo;

import java.util.List;

/**
 * @ClassName: CategoryService
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:49
 * @Version:
 */
public interface CategoryService {
    /**
     * 获取列表
     *
     * @return
     */
    List<NameCountBo> list();
}
