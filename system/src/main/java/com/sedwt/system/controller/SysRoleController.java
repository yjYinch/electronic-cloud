package com.sedwt.system.controller;

import com.sedwt.common.auth.annotation.HasPermissions;
import com.sedwt.common.core.controller.BaseController;
import com.sedwt.common.core.domain.R;
import com.sedwt.common.log.annotation.OperLog;
import com.sedwt.common.log.enums.BusinessType;
import com.sedwt.system.domain.SysRole;
import com.sedwt.system.domain.vo.SysPrivilegeVo;
import com.sedwt.system.domain.vo.SysRoleVo;
import com.sedwt.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 角色 提供者
 * 
 * @author zmr
 * @date 2019-05-20
 */
@RestController
@RequestMapping("role")
public class SysRoleController extends BaseController {
	@Autowired
	private ISysRoleService sysRoleService;

	/**
	 * 查询角色
	 */
	@PostMapping("get")
	public R get(@RequestBody SysRoleVo role) {
		return R.data(sysRoleService.selectRoleById(role.getRoleId()));
	}

	/**
	 * 查询角色列表
	 */
	@GetMapping("fetch")
	@HasPermissions("system:role:fetch")
	public R list(SysRole sysRole) {
		startPage();
		return result(sysRoleService.selectRoleList(sysRole));
	}

	@GetMapping("all")
	public R all() {
		return R.ok().put("rows", sysRoleService.selectRoleAll());
	}

	/**
	 * 新增保存角色
	 */
	@OperLog(title = "角色管理", businessType = BusinessType.INSERT)
	@PostMapping("save")
	@HasPermissions("system:role:add")
	public R addSave(@RequestBody SysRole sysRole) {
		//(1)参数校验
		String roleName = sysRole.getRoleName();
		if(isBlank(roleName)){
			return R.error("角色名不能为空");
		}
		List<SysPrivilegeVo> privilegeList = sysRole.getPrivilegeList();
		if(privilegeList.isEmpty()){
			return R.error("角色需要存在至少一种权限");
		}
		return toAjax(sysRoleService.insertRole(sysRole));
	}

	/**
	 * 修改保存角色
	 */
	@OperLog(title = "角色管理", businessType = BusinessType.UPDATE)
	@PostMapping("update")
	@HasPermissions("system:role:update")
	public R editSave(@RequestBody SysRole sysRole) {
		//(1)参数校验
		if(sysRole.getRoleId()==null){
			return R.error("角色id不能为空");
		}
		String roleName = sysRole.getRoleName();
		if(isBlank(roleName)){
			return R.error("角色名不能为空");
		}
		List<SysPrivilegeVo> privilegeList = sysRole.getPrivilegeList();
		if(privilegeList.isEmpty()){
			return R.error("角色需要存在至少一种权限");
		}
		return toAjax(sysRoleService.updateRole(sysRole));
	}

	/**
	 * 修改保存角色
	 */
	@OperLog(title = "角色管理", businessType = BusinessType.UPDATE)
	@PostMapping("status")
	@HasPermissions("system:role:update")
	public R status(@RequestBody SysRole sysRole) {
		return toAjax(sysRoleService.changeStatus(sysRole));
	}

	/**
	 * 保存角色分配数据权限
	 */
	@OperLog(title = "角色管理", businessType = BusinessType.UPDATE)
	@PostMapping("/authDataScope")
	public R authDataScopeSave(@RequestBody SysRole role) {
		role.setUpdateBy(getLoginName());
		if (sysRoleService.authDataScope(role) > 0) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 删除角色
	 * 
	 * @throws Exception
	 */
	@OperLog(title = "角色管理", businessType = BusinessType.DELETE)
	@PostMapping("remove")
	@HasPermissions("system:role:remove")
	public R remove(@RequestBody SysRoleVo role) throws Exception {
		return toAjax(sysRoleService.deleteRoleById(role.getRoleId()));
	}
}
