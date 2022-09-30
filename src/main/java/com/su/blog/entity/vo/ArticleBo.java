package com.su.blog.entity.vo;

import com.su.blog.entity.Article;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ArticleBo
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:21
 * @Version:
 */
@Data
public class ArticleBo extends Article {
    /**
     * 参数，排序字段+排序方式：viewCount+DESC
     */
    private String order;
    /**
     * 排序字段
     */
    private String orderKey;
    /**
     * 搜索条件
     */
    private String keyword;
    private String tag;
    private String category;

    /**
     * 添加文章使用的参数
     */
    private List<String> tagList;
    private List<String> categoryList;
    private String authorId;

    public ArticleBo() {
    }

    public ArticleBo(String title, String content) {
        super(title, content);
    }
}
