package com.su.blog.service.impl;

import com.su.blog.dao.UserDao;
import com.su.blog.entity.User;
import com.su.blog.entity.vo.UserBo;
import com.su.blog.service.UserService;
import com.su.blog.util.MD5Util;
import com.su.blog.util.Result.ResultPage;
import com.su.blog.util.Result.Tips;
import com.su.blog.util.exception.AppRuntimeException;
import com.su.blog.util.redis.RedisKey;
import com.su.blog.util.redis.RedisOperator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: HomeServiceImpl
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 11:01
 * @Version:
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private RedisOperator redisOperator;

    @Override
    public boolean register(User user) {
        Integer id = userDao.getIdByEmail(user.getEmail());
        if (id != null) {
            throw new AppRuntimeException(Tips.USER_EMAIL_EXIST_ALREADY);
        }
        id = userDao.getIdByUsername(user.getUsername());
        if (id != null) {
            throw new AppRuntimeException(Tips.USER_NAME_EXIST_ALREADY);
        }
        //密码加密
        user.setPassword(MD5Util.MD5(user.getPassword()));
        int count = userDao.insert(user);
        if (count > 0) {
            return true;
        } else {
            throw new AppRuntimeException(Tips.REGISTER_FAIL);
        }
    }

    @Override
    public User login(UserBo user) {
        //从redis查看该用户是否允许登录
        String redisKey = RedisKey.LOGIN_ERROR_COUNT_UNAME.value() + user.getAccount();
        String count = redisOperator.get(redisKey);
        if (count != null && Integer.valueOf(count) >= 3) {
            //-1:没有过期时间  -2:没有这个keys
            long ttl = redisOperator.ttl(redisKey);
            if (ttl > 0) {//没有这个key
                throw new AppRuntimeException(Tips.LOGIN_ERR_TIME + ttl + "秒");
            }
        }

        //插叙
        User data = userDao.getInfoByName(user.getAccount());
        if (data == null) {
            throw new AppRuntimeException(Tips.USER_OR_PASSWORD_ERROR);
        }
        if (MD5Util.MD5(user.getPassword()).equals(data.getPassword())) {
            data.setPassword("");
            data.setUserId(data.getId());
            redisOperator.del(redisKey);
            return data;
        } else {
            //失败三次，锁定十分钟
            redisOperator.incr(redisKey, 1);
            redisOperator.expire(redisKey,10*60);
            throw new AppRuntimeException(Tips.USER_OR_PASSWORD_ERROR);
        }
    }

    @Override
    public ResultPage list(UserBo user) {
        int offset = (user.getPage() - 1) * user.getPageSize();
        user.setOffset(offset);
        int count = userDao.getListCount(user);
        List<User> list = new LinkedList<>();
        if (count > 0) {
            list = userDao.getList(user);
        }
        return new ResultPage(count, list);
    }

    @Override
    public void delete(String userId) {
        boolean res = userDao.delete(Integer.valueOf(userId));
        if (!res) {
            throw new AppRuntimeException(Tips.DELETE_ERROR);
        }
    }

    @Override
    @Transactional
    public void put(String userId, Boolean notice, Boolean disabledDiscuss) {
        if (null != notice) {
            boolean res = userDao.updateNotice(Integer.valueOf(userId), notice ? 1 : 0);
            if (!res) {
                throw new AppRuntimeException(Tips.DELETE_ERROR);
            }
        }
        if (null != disabledDiscuss) {
            boolean res = userDao.updateDisabledDiscuss(Integer.valueOf(userId), disabledDiscuss ? 1 : 0);
            if (!res) {
                throw new AppRuntimeException(Tips.DELETE_ERROR);
            }
        }


    }
}
