package com.su.blog.entity.vo;

import lombok.Data;

/**
 * @ClassName: CategoryBo
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:48
 * @Version:
 */
@Data
public class NameCountBo {
    /**
     * 名称
     */
    private String name;
    /**
     * 数量
     */
    private Integer count;
}
