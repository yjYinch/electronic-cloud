package com.sedwt.system.controller;

import com.sedwt.common.core.controller.BaseController;
import com.sedwt.common.core.domain.R;
import com.sedwt.system.domain.SysUser;
import com.sedwt.system.service.ISysPrivilegeService;
import com.sedwt.system.service.ISysUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 权限
 *
 * @author zyh
 * @date 2021-8-23
 */
@RestController
@RequestMapping("privilege")
public class SysPrivilegeController extends BaseController {

    @Resource
    private ISysPrivilegeService privilegeService;

    @Resource
    private ISysUserService userService;

    @GetMapping("list")
    public R list() {
        return R.data(privilegeService.getPrivilegeList());
    }

    /**
     * 查询用户拥有的权限
     */
    @GetMapping("perms/{userId}")
    public Set<String> perms(@PathVariable("userId") Long userId) {
        return privilegeService.selectPermsByUserId(userId);
    }

    /**
     * 通过获取到当前登录用户的token 解析出前用户
     * 再查询当前用户所有的权限 和角色集合
     *
     * @return 返回所有的用户权限key集合 和角色集合
     */
    @GetMapping("getPrivilege")
    public R getPrivilegeByUser() {
        SysUser user = userService.selectUserById(getCurrentUserId());
        Map<String, HashSet> privilege = privilegeService.getPrivilegeByUser(user);
        return R.data(privilege);
    }

}
