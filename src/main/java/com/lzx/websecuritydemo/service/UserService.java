package com.lzx.websecuritydemo.service;

import com.lzx.websecuritydemo.exception.BusinessException;
import com.lzx.websecuritydemo.mapper.UserMapper;
import com.lzx.websecuritydemo.objectmapper.UserObjectMapper;
import com.lzx.websecuritydemo.po.UserPO;
import com.lzx.websecuritydemo.vo.UserVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserObjectMapper userObjectMapper;
    private final RedissonClient redissonClient;
    UserService(UserMapper userMapper,
                UserObjectMapper userObjectMapper,
                RedissonClient redissonClient){
        this.userMapper = userMapper;
        this.userObjectMapper = userObjectMapper;
        this.redissonClient = redissonClient;
    }

    @Transactional
    public void addUser(UserVO userVO){
        RLock lock = redissonClient.getLock("add-user");
        try{
            lock.lock(5,TimeUnit.MINUTES);
            UserPO userPO = userMapper.getUserByName(userVO.getName());
            if(userPO != null)throw new BusinessException("用户名已存在");
            userMapper.addUser(userObjectMapper.vo2po(userVO));
        }finally {
            lock.unlock();
        }
    }
}
