package com.sedwt.system.controller;

import com.sedwt.common.entity.R;
import com.sedwt.common.entity.sys.SysUser;
import com.sedwt.system.form.LoginForm;
import com.sedwt.system.service.AccessTokenService;
import com.sedwt.system.service.SysLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xpan
 */
@RestController
public class LoginController {
	
	@Autowired
    private AccessTokenService tokenService;

	@Autowired
	private SysLoginService sysLoginService;

	@PostMapping("login")
	public R login(@RequestBody LoginForm form) {
		// 用户登录
		SysUser user = sysLoginService.login(form.getLoginName(), form.getPassword());
		// 获取登录token
		return R.ok(tokenService.createToken(user));
	}
}
