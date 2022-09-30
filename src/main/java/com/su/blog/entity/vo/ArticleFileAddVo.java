package com.su.blog.entity.vo;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: FileBo
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/16 11:32
 * @Version:
 */
@Data
public class ArticleFileAddVo {
    private List insertList = new LinkedList();
    private String message;
    private List updateList = new LinkedList();
}
