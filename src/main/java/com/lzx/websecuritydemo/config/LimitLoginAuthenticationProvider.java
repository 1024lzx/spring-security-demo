package com.lzx.websecuritydemo.config;

import com.lzx.websecuritydemo.mapper.UserMapper;
import com.lzx.websecuritydemo.po.UserPO;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.event.EntryExpiredListener;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class LimitLoginAuthenticationProvider extends DaoAuthenticationProvider {
    private final UserMapper userMapper;
    private final RMapCache<Long, Long> retryNum;
    private final SessionRegistry sessionRegistry;

    public LimitLoginAuthenticationProvider(UserDetailsService userDetailsService,
                                            PasswordEncoder passwordEncoder,
                                            UserMapper userMapper,
                                            RedissonClient redissonClient,
                                            SessionRegistry sessionRegistry) {
        super();
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
        this.userMapper = userMapper;
        this.retryNum = redissonClient.getMapCache("retry-num");
        this.retryNum.addListener((EntryExpiredListener<Long, Long>) event -> {
            Long userId = event.getKey();
            resetLoginStatus(userId);
            UserPO userPO = userMapper.getUserById(userId);
            if (userPO.getIsLock() != null && "Y".equals(userPO.getIsLock().toString())) {
                userMapper.unLockAccount(userId);
            }
        });
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String userName = authentication.getName();
        UserPO userPO = userMapper.getUserByName(userName);
        if (userPO == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 1.check locked
        if (userPO.getIsLock() != null && "Y".equals(userPO.getIsLock().toString())) {
            // 1.check time
            long remainTime = TimeUnit.SECONDS.convert(retryNum.remainTimeToLive(userPO.getId()), TimeUnit.MILLISECONDS);
            if(remainTime == 0){
                remainTime = 1;
            }
            throw new LockedException("帐号被锁," +
                    remainTime
                    + "秒后再试");
        }
        // check pwd
        Authentication auth;
        try {
            auth = super.authenticate(authentication);
        } catch (Exception e) {
            loginFail(userPO.getId());
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        cleanSession(userPO.getId());
        resetLoginStatus(userPO.getId());
        return auth;
    }

    private void loginFail(Long userId) {
        Long num = retryNum.get(userId);
        if (num == null) num = 0L;
        //登陆失败三次，锁定账号
        if (num >= 3) {
            userMapper.lockAccount(userId);
            retryNum.put(userId, ++num, 1, TimeUnit.MINUTES);
        } else {
            retryNum.put(userId, ++num, 1, TimeUnit.MINUTES);
        }
    }

    private void resetLoginStatus(Long userId) {
        retryNum.put(userId, 0L);
    }

    private void cleanSession(Long userId) {
        sessionRegistry.getAllPrincipals().forEach(userInfo -> {
            if (userId.equals(((UserPO) userInfo).getId())) {
                sessionRegistry.getAllSessions(userInfo, true).forEach(SessionInformation::expireNow);
            }
        });
    }

}
