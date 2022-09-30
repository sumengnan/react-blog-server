package com.su.blog.controller;

import com.su.blog.service.TagService;
import com.su.blog.util.Result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName: User
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 9:27
 * @Version:
 */
@RestController
@CrossOrigin
@RequestMapping("tag")
public class TagController {

    @Resource
    private TagService tagService;


    /**
     * 获取列表
     *
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity list() {
        return Result.Success(tagService.list());
    }

}
