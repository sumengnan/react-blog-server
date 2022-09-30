package com.su.blog.service.impl;

import com.su.blog.dao.TagDao;
import com.su.blog.entity.vo.NameCountBo;
import com.su.blog.service.TagService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: TagServiceImpl
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:57
 * @Version:
 */
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagDao tagDao;
    @Override
    public List<NameCountBo> list() {
        return tagDao.list();
    }
}
