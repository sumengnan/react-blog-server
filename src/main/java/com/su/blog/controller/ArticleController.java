package com.su.blog.controller;

import com.su.blog.dao.ArticleDao;
import com.su.blog.entity.Article;
import com.su.blog.entity.vo.ArticleBo;
import com.su.blog.entity.vo.ArticleFileBo;
import com.su.blog.service.ArticleService;
import com.su.blog.util.ParamUtil;
import com.su.blog.util.Result.Result;
import com.su.blog.util.Result.ResultPage;
import com.su.blog.util.exception.AppRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: User
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 9:27
 * @Version:
 */
@RestController
@CrossOrigin
public class ArticleController {

    @Resource
    private ArticleService articleService;
    @Resource
    private ArticleDao articleDao;


    /**
     * 获取列表
     *
     * @return
     */
    @GetMapping("/article/list")
    public ResponseEntity list(@ModelAttribute ArticleBo bo) {
        bo.setPage(bo.getPage() == 0 ? 1 : bo.getPage());
        bo.setPageSize(bo.getPageSize() == 0 ? 10 : bo.getPageSize());
        ResultPage list = articleService.list(bo);
        return Result.Success(list);
    }

    /**
     * 获取列表
     *
     * @return
     */
    @GetMapping("/article/{id}")
    public ResponseEntity getInfo(@PathVariable String id) {
        if (ParamUtil.isEmpty(id)) {
            return Result.ParamError();
        }
        //观看次数加1
        articleDao.addViewCount(id);
        Article article = articleService.getInfo(id);
        return Result.Success(article);
    }

    /**
     * 创建
     *
     * @return
     */
    @PostMapping("/article")
    public ResponseEntity create(@RequestBody ArticleBo bo) {
        return Result.Success(articleService.create(bo));
    }


    /**
     * 删除
     *
     * @return
     */
    @DeleteMapping("/article/{id}")
    public ResponseEntity delete(@PathVariable("id") String id) {
        if (ParamUtil.isOneEmpty(id)) {
            return Result.ParamError();
        }
        articleService.delete(id);
        return Result.Success();
    }

    /**
     * 删除
     *
     * @return
     */
    @DeleteMapping("/article/list/{id}")
    public ResponseEntity deleteList(@PathVariable("id") String id) {
        if (ParamUtil.isOneEmpty(id)) {
            return Result.ParamError();
        }
        articleService.deleteList(id);
        return Result.Success();
    }

    /**
     * 更新
     *
     * @return
     */
    @PostMapping("/article/{id}")
    public ResponseEntity update(@PathVariable("id") String id, @RequestBody ArticleBo bo) {
        if (ParamUtil.isEmpty(id)) {
            return Result.ParamError();
        }
        bo.setId(Integer.valueOf(id));
        return Result.Success(articleService.update(bo));
    }

    /**
     * 校验资源是否存在
     *
     * @return
     */
    @PostMapping("/article/checkExist")
    public ResponseEntity checkExist(@RequestBody ArticleFileBo bo) {
        if (bo.getFileNameList() == null || bo.getFileNameList().size() == 0) {
            return Result.ParamError();
        }
        return Result.Success(articleService.checkExist(bo.getFileNameList()));
    }

    /**
     * 上传资源
     *
     * @return
     */
    @PostMapping("/article/upload")
    public void upload(@RequestParam("file") MultipartFile[] file) {
        articleService.upload(file);
    }

    /**
     * 导入资源
     *
     * @return
     */
    @PostMapping("/article/upload/confirm")
    public ResponseEntity confirm(@RequestBody ArticleFileBo bo) {
        if (bo.getUploadList() == null || bo.getUploadList().size() == 0) {
            return Result.ParamError();
        }
        return Result.Success(articleService.confirm(bo.getAuthorId(),bo.getUploadList()));
    }


    /**
     * 导出
     *
     * @return
     */
    @GetMapping("/article/output/all")
    public void outputAll(HttpServletRequest request, HttpServletResponse response) {
        articleService.outputAll(request, response);
    }

    /**
     * 导出
     *
     * @return
     */
    @GetMapping("/article/output/{id}")
    public void output(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        if (ParamUtil.isEmpty(id)) {
            throw new AppRuntimeException("参数错误");
        }
        articleService.output(request, response, id);
    }

}
