package com.su.blog.service.impl;

import com.su.blog.dao.CategoryDao;
import com.su.blog.entity.vo.NameCountBo;
import com.su.blog.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: CategoryServiceImpl
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:50
 * @Version:
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryDao categoryDao;

    @Override
    public List<NameCountBo> list() {
        return categoryDao.list();
    }
}
