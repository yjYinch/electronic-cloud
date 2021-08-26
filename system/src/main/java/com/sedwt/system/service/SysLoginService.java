package com.sedwt.system.service;

import com.sedwt.common.constant.UserConstants;
import com.sedwt.common.entity.sys.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SysLoginService {

	@Autowired
	private RemoteUserService userService;

	/**
	 * 登录
	 */
	public SysUser login(String loginName, String password) {
		// 用户名或密码为空 错误
		if (StringUtils.isAnyBlank(loginName, password)) {
			throw new UserNotExistsException();
		}
		// 密码如果不在指定范围内 错误
		if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
				|| password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
			throw new UserPasswordNotMatchException();
		}
		// 用户名不在指定范围内 错误
		if (loginName.length() < UserConstants.USERNAME_MIN_LENGTH
				|| loginName.length() > UserConstants.USERNAME_MAX_LENGTH) {
			throw new UserPasswordNotMatchException();
		}
		// TODO
		// 查询用户信息
		SysUser user = userService.selectSysUserByUsername(loginName);
		if (user == null) {
			throw new UserNotExistsException();
		}
		if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
			throw new UserDeleteException();
		}
		if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
			throw new UserBlockedException();
		}
		if (!PasswordUtil.matches(user, password)) {
			throw new UserPasswordNotMatchException();
		}
		return user;
	}
}