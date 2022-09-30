package com.su.blog.service.impl;

import com.su.blog.dao.CommentDao;
import com.su.blog.dao.ReplyDao;
import com.su.blog.dao.UserDao;
import com.su.blog.entity.Comment;
import com.su.blog.entity.Reply;
import com.su.blog.entity.vo.CommentBo;
import com.su.blog.service.CommentService;
import com.su.blog.util.ParamUtil;
import com.su.blog.util.Result.ResultPage;
import com.su.blog.util.Result.Tips;
import com.su.blog.util.exception.AppRuntimeException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: CommentServiceImpl
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/14 8:58
 * @Version:
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentDao commentDao;
    @Resource
    private UserDao userDao;
    @Resource
    private ReplyDao replyDao;

    @Override
    public ResultPage create(CommentBo bo) {
        //用户是否有评论的权限
        Integer auth = userDao.getUserDiscuss(bo.getUserId());
        if (auth == null || auth != 1) {
            throw new AppRuntimeException(Tips.DISCUSS_AUTH_ERROR);
        }
        //是否为评论回复
        if (bo.getCommentId() == null) {
            Comment comment = new Comment(bo.getArticleId(), bo.getUserId(), bo.getContent());
            if (commentDao.insert(comment) == 0) {
                throw new AppRuntimeException(Tips.DISCUSS_ERROR);
            }
        } else {
            Reply reply = new Reply(bo.getArticleId(), bo.getCommentId(), bo.getUserId(), bo.getContent());
            if (replyDao.insert(reply) == 0) {
                throw new AppRuntimeException(Tips.DISCUSS_ERROR);
            }
        }

        //返回值
        Integer count = commentDao.getCount(bo.getArticleId());
        List<CommentBo> list = new LinkedList<>();
        if (count != null && count > 0) {
            list = commentDao.getInfoByArticleId(bo.getArticleId());

        }
        return new ResultPage(count, list);
    }

    @Override
    public void delete(String id) {
        int res = commentDao.deleteByPrimaryKey(Integer.valueOf(id));
        if (res <= 0) {
            throw new AppRuntimeException(Tips.DELETE_ERROR);
        }
    }

}
