package com.sedwt.system.controller;

import com.sedwt.common.auth.annotation.HasPermissions;
import com.sedwt.common.constant.ErrorCodeConstants;
import com.sedwt.common.core.controller.BaseController;
import com.sedwt.common.core.domain.R;
import com.sedwt.common.log.annotation.OperLog;
import com.sedwt.common.log.enums.BusinessType;
import com.sedwt.system.domain.SysUser;
import com.sedwt.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户 提供者
 *
 * @author zmr
 * @date 2019-05-20
 */
@RestController
@RequestMapping("user")
public class SysUserController extends BaseController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id
     * @return 当前id的用户信息
     */
    @GetMapping("queryById")
    public R queryById(Long userId) {
        SysUser sysUser = sysUserService.selectUserById(userId);
        return R.data(sysUser);
    }

    /**
     * 根据用户名查询用户信息 （用户登录时候使用）
     *
     * @param loginName 登录名
     * @return 根据登录名查询的用户信息
     */
    @GetMapping("find/{loginName}")
    public SysUser findByUsername(@PathVariable("loginName") String loginName) {
        return sysUserService.selectUserByLoginName(loginName);
    }

    /**
     * 查询用户列表
     *
     * @param sysUser 查询的对象
     * @param request 请求
     * @return 查询用户信息列表
     */
    @GetMapping("fetch")
    @HasPermissions("system:user:fetch")
    public R list(SysUser sysUser, HttpServletRequest request) {
        startPage();
        sysUser.setUserId(getCurrentUserId());
        return result(sysUserService.selectUserList(sysUser, request));
    }

    /**
     * 新增保存用户
     *
     * @param sysUser 新增的用户信息，密码 用户名 登录名 邮箱不为空且用户名 登录名 邮箱唯一
     * @return 添加的结果
     */
    @PostMapping("add")
    @HasPermissions("system:user:add")
    @OperLog(title = "用户管理", businessType = BusinessType.INSERT)
    public R addSave(@RequestBody SysUser sysUser) {
        sysUser.setCreateBy(getLoginName());
        return sysUserService.insertUser(sysUser);
    }

    /**
     * 修改保存用户
     *
     * @param sysUser 修改的信息  仅仅修改username 和 email
     * @return 修改的结果
     */

    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("update")
    @HasPermissions("system:user:update")
    public R editSave(@RequestBody SysUser sysUser) {
        return sysUserService.updateUser(sysUser);
    }

    /**
     * 修改用户个人信息
     *
     * @param sysUser 需要修改的用户信息 仅仅支持修改用户的username和email
     * @return 修改的结果
     */
    @PostMapping("update/info")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public R updateInfo(@RequestBody SysUser sysUser) {
        sysUser.setUserId(getCurrentUserId());
        if (SysUser.isAdmin(sysUser.getUserId())) {
            return R.error(ErrorCodeConstants.PERMISSION_ERROR, "不允许修改超级管理员用户");
        }
        return sysUserService.updateUserInfo(sysUser);
    }

    /**
     * 管理员重置用户密码
     *
     * @param user 需要被修改的用户信息
     * @return 修改的结果
     */
    @OperLog(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    @HasPermissions("system:user:updatePwd")
    public R resetPwdSave(@RequestBody SysUser user) {
        if (null != user.getUserId() && SysUser.isAdmin(user.getUserId())) {
            return R.error(ErrorCodeConstants.PERMISSION_ERROR, "不允许修改超级管理员用户");
        }
        return sysUserService.resetUserPwd(user);
    }

    /**
     * 用户中心 用户修改自己密码
     *
     * @param sysUser 需要修改的用户的信息
     * @return 修改后的结果
     */
    @OperLog(title = "修改密码", businessType = BusinessType.UPDATE)
    @PostMapping("/updatePwd")
    public R updatePwdSave(@RequestBody SysUser sysUser) {
        //获取到前端传来校验的旧密码
        String oldPassword = sysUser.getPassword();
        Long userId1 = getCurrentUserId();
        SysUser user = sysUserService.selectUserById(userId1);
        if (null != user.getUserId() && SysUser.isAdmin(user.getUserId())) {
            return R.error(ErrorCodeConstants.PERMISSION_ERROR, "不允许修改超级管理员用户");
        }
        user.setNewPassword(sysUser.getNewPassword());
        return sysUserService.updatePwd(user, oldPassword);
    }

    /**
     * 根据用户id删除用户 直接删除
     *
     * @param sysUser 包含需要删除的用户的id
     * @return 删除的结果
     * @throws Exception 可能发生的异常
     */
    @OperLog(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping("deleteByIds")
    @HasPermissions("system:user:remove")
    public R remove(@RequestBody SysUser sysUser) throws Exception {
        return sysUserService.deleteUserById(sysUser);
    }

}
