package com.su.blog.dao;

import com.su.blog.entity.Ip;

public interface IpDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Ip record);

    int insertSelective(Ip record);

    Ip selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Ip record);

    int updateByPrimaryKeyWithBLOBs(Ip record);

    int updateByPrimaryKey(Ip record);
}