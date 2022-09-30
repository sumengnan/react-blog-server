package com.su.blog.dao;

import com.su.blog.entity.User;
import com.su.blog.entity.vo.UserBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {

    int insert(User record);

    Integer getIdByUsername(String username);

    Integer getIdByEmail(String email);

    User getInfoByName(String username);

    int getListCount(@Param("item") UserBo item);

    List<User> getList(@Param("item") UserBo item);

    boolean delete(Integer id);

    boolean updateNotice(Integer id, int notice);

    boolean updateDisabledDiscuss(Integer id, int disabledDiscuss);

    Integer getUserDiscuss(Integer id);
}