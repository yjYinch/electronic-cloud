package com.sedwt.common.entity.sys;

import lombok.Getter;
import lombok.Setter;

/**
 * 角色和权限关联 sys_role_privilege
 * 
 * @author sedwt
 */
@Setter
@Getter
public class SysRolePrivilege {

	private Long id;

	/** 角色ID */
	private Long roleId;

	/** 用户ID */
	private Long privilegeId;

}
