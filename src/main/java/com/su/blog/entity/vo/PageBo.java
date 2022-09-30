package com.su.blog.entity.vo;

import lombok.Data;

/**
 * @ClassName: PageBo
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:31
 * @Version:
 */
@Data
public class PageBo {
    /**
     * 当前页
     */
    private int page;
    /**
     * 每页条数
     */
    private int pageSize;
    /**
     * 起始位置
     */
    private int offset;
}
