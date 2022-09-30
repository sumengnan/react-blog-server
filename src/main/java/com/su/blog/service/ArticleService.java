package com.su.blog.service;

import com.su.blog.entity.Article;
import com.su.blog.entity.vo.ArticleBo;
import com.su.blog.entity.vo.ArticleFileAddVo;
import com.su.blog.entity.vo.ArticleFileVo;
import com.su.blog.util.Result.ResultPage;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ClassName: ArticleService
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:25
 * @Version:
 */
public interface ArticleService {
    /**
     * 获取列表
     *
     * @param bo
     * @return
     */
    ResultPage list(ArticleBo bo);

    /**
     * 获取列表
     *
     * @param id
     * @return
     */
    Article getInfo(String id);

    /**
     * 添加文章
     *
     * @param bo
     * @return
     */
    Article create(ArticleBo bo);

    /**
     * 删除
     *
     * @param id
     */
    void delete(String id);

    /**
     * 批量删除
     *
     * @param id
     */
    void deleteList(String id);

    /**
     * 更新
     *
     * @param bo
     * @return
     */
    boolean update(ArticleBo bo);

    /**
     * 校验资源是否存在
     *
     * @return
     */
    List<ArticleFileVo> checkExist(List<String> fileNameList);

    /**
     * 上传
     */
    void upload(MultipartFile[] file);

    /**
     * 确定导入
     *
     * @return
     */
    ArticleFileAddVo confirm(Integer authorId, List<ArticleFileVo> uploadList);

    /**
     * 导出
     *
     * @param request
     * @param response
     */
    void outputAll(HttpServletRequest request, HttpServletResponse response);

    /**
     * 导出
     *
     * @param request
     * @param response
     * @param id
     */
    void output(HttpServletRequest request, HttpServletResponse response, String id);

}
