package com.su.blog.entity.vo;

import lombok.Data;

/**
 * @ClassName: FileBo
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/16 11:32
 * @Version:
 */
@Data
public class ArticleFileVo {
    private String fileName;
    private String title;
    private Boolean exist;
    private Integer articleId;

    public ArticleFileVo() {
    }

    public ArticleFileVo(String fileName, String title) {
        this.fileName = fileName;
        this.title = title;
    }
}
