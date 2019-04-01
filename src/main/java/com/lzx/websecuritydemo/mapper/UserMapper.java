package com.lzx.websecuritydemo.mapper;

import com.lzx.websecuritydemo.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    UserPO getUserByName(@Param("name") String name);
    void unLockAccount(@Param("id") Long id);
    void lockAccount(@Param("id") Long id);
    UserPO getUserById(@Param("id") Long id);
    Long addUser(UserPO userPO);
}
