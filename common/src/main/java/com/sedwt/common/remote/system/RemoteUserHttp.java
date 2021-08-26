package com.sedwt.common.remote.system;

import com.sedwt.common.constant.ServiceConstants;
import com.sedwt.common.entity.R;
import com.sedwt.common.entity.sys.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : yj zhang
 * @since : 2021/8/26 14:35
 */
@Component
public class RemoteUserHttp {
    private static final String prefixUrl = "http://" + ServiceConstants.SYSTEM_NAME +"/user/";
    @Autowired
    private RestTemplate restTemplate;

    public SysUser selectSysUserByUserId(@PathVariable("userId") long userId) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(userId));
        String realUrl = prefixUrl + "/get/{userId}";
        return restTemplate.getForObject(realUrl, SysUser.class, params);
    }

    public SysUser selectSysUserByUsername(@PathVariable("username") String username) {
        Map<String, String> params = new HashMap<>();
        params.put("username",username);
        String realUrl = prefixUrl + "/find/{username}";
        return restTemplate.getForObject(realUrl, SysUser.class, params);
    }

    @PostMapping("user/update/login")
    public R updateUserLoginRecord(@RequestBody SysUser user) {
        return null;
    }
}
