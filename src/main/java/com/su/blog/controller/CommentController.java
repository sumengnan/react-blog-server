package com.su.blog.controller;

import com.su.blog.entity.vo.CommentBo;
import com.su.blog.service.CommentService;
import com.su.blog.util.ParamUtil;
import com.su.blog.util.Result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName: 评论
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/14 8:55
 * @Version:
 */
@RestController
@CrossOrigin
public class CommentController {
    @Resource
    private CommentService commentService;

    /**
     * 创建
     *
     * @return
     */
    @PostMapping("/discuss")
    public ResponseEntity create(@RequestBody CommentBo bo) {
        return Result.Success(commentService.create(bo));
    }

    /**
     * 删除
     *
     * @return
     */
    @DeleteMapping("/discuss/comment/{id}")
    public ResponseEntity delete(@PathVariable("id") String id) {
        if (ParamUtil.isOneEmpty(id)) {
            return Result.ParamError();
        }
        commentService.delete(id);
        return Result.Success();
    }

}

