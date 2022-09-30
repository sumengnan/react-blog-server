package com.su.blog.entity.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @ClassName: FileBo
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/16 11:32
 * @Version:
 */
@Data
public class ArticleFileBo {
    //文件名列表
    private List<String> fileNameList;
    //上传文件接收
    private MultipartFile[] file;

    //确认上传参数
    private Integer authorId;
    private List<ArticleFileVo> uploadList;
}
