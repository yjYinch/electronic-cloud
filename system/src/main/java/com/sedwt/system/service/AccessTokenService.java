package com.sedwt.system.service;

import com.sedwt.common.annotation.RedisEvict;
import com.sedwt.common.constant.Constants;

import com.sedwt.common.entity.sys.SysUser;
import com.sedwt.common.redis.RedisUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Administrator
 */
@Service
public class AccessTokenService {

    /**
     * 12小时后过期
     */
    private final static long EXPIRE = 12 * 60 * 60;

    private final static String ACCESS_TOKEN = Constants.ACCESS_TOKEN;

    private final static String ACCESS_USERID = Constants.ACCESS_USERID;

    public SysUser queryByToken(String token) {
        return RedisUtils.get(ACCESS_TOKEN + token, SysUser.class);
    }

    @RedisEvict(key = "user_perms", fieldKey = "#sysUser.userId")
    public Map<String, Object> createToken(SysUser sysUser) {
        // 生成token
        String token = UUID.randomUUID().toString();
        // 保存或更新用户token
        Map<String, Object> map = new HashMap<>(8);
        map.put("userId", sysUser.getUserId());
        map.put("loginName", sysUser.getLoginName());
        map.put("userName", sysUser.getUserName());
        map.put("token", token);
        if (SysUser.isAdmin(sysUser.getUserId())) {
            map.put("roleName", "admin");
        } else {
            map.put("roleName", "普通用户");
        }
        map.put("expire", EXPIRE);
        // expireToken(userId);
        RedisUtils.set(ACCESS_TOKEN + token, sysUser, EXPIRE);
        RedisUtils.set(ACCESS_USERID + sysUser.getUserId(), token, EXPIRE);
        return map;
    }

    /**
     * token删除
     * @param userId
     */
    public void expireToken(long userId) {
        String token = String.valueOf(RedisUtils.get(ACCESS_USERID + userId));
        if (StringUtils.isNotBlank(token)) {
            RedisUtils.del(ACCESS_USERID + userId);
            RedisUtils.del(ACCESS_TOKEN + token);
        }
    }
}