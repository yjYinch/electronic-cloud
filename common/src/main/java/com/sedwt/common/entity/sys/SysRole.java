package com.sedwt.common.entity.sys;

import com.sedwt.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

/**
 * 角色表 sys_role
 *
 * @Excel 注解暂时删除，等导入导出功能需要时再恢复，
 * TODO EasyExcel 和 EasyPoi
 *
 * @author sedwt
 */
@Getter
@Setter
public class SysRole extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/** 角色ID */
	//@Excel(name = "角色序号")
	private Long roleId;

	/** 角色名称 */
	//@Excel(name = "角色名称")
	private String roleName;

	/** 角色权限 */
	//@Excel(name = "角色权限")
	private String roleKey;

	/** 角色排序 */
	//@Excel(name = "角色排序")
	private String roleSort;

	/** 数据范围（1：所有数据权限；2：自定数据权限） */
	//@Excel(name = "数据范围", readConverterExp = "1=所有数据权限,2=自定义数据权限")
	private String dataScope;

	/** 角色状态（0正常 1停用） */
	//@Excel(name = "角色状态", readConverterExp = "0=正常,1=停用")
	private String status;

	/** 删除标志（0代表存在 2代表删除） */
	private String delFlag;

	/** 用户是否存在此角色标识 默认不存在 */
	private boolean flag = false;

	/** 菜单组 */
	private List<Long> menuIds;

	/** 角色包含的用户列表 */
	private List<SysUser> userList;

	/** 角色包含的权限列表 */
	private List<SysRolePrivilege> privilegeList;

	private String users;

	private String prvgs;
}
