package com.sedwt.system.controller;

import com.sedwt.common.annotation.LoginUser;
import com.sedwt.common.core.controller.BaseController;
import com.sedwt.common.core.domain.R;
import com.sedwt.common.log.annotation.OperLog;
import com.sedwt.common.log.enums.BusinessType;
import com.sedwt.system.domain.SysMenu;
import com.sedwt.system.domain.SysUser;
import com.sedwt.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 菜单权限
 * 
 * @author zmr
 * @date 2019-05-20
 */
@RestController
@RequestMapping("menu")
public class SysMenuController extends BaseController {
	@Autowired
	private ISysMenuService sysMenuService;

	/**
	 * 查询菜单权限
	 */
	@GetMapping("get/{menuId}")
	public SysMenu get(@PathVariable("menuId") Long menuId) {
		return sysMenuService.selectMenuById(menuId);
	}

	/**
	 * 查询用户拥有的权限
	 */
	@GetMapping("perms/{userId}")
	public Set<String> perms(@PathVariable("userId") Long userId) {
		return sysMenuService.selectPermsByUserId(userId);
	}

	/**
	 * 查询菜单权限
	 */
	@GetMapping("user")
	public R user(@LoginUser SysUser sysUser) {
		return result(sysMenuService.selectMenusByUser(sysUser));
	}

	/**
	 * 根据角色编号查询菜单编号（用于勾选）
	 * 
	 * @param roleId
	 * @return
	 * @author zmr
	 */
	@GetMapping("role/{roleId}")
	public R role(@PathVariable("roleId") Long roleId) {
		if (null == roleId || roleId <= 0) {
			return null;
		}
		return result(sysMenuService.selectMenuIdsByRoleId(roleId));
	}

	/**
	 * 查询菜单权限列表
	 */
	@GetMapping("list")
	public R list(SysMenu sysMenu) {
		return result(sysMenuService.selectMenuList(sysMenu));
	}

	/**
	 * 查询所有菜单权限列表
	 */
	@GetMapping("all")
	public R all(SysMenu sysMenu) {
		return result(sysMenuService.selectMenuList(sysMenu));
	}

	/**
	 * 新增保存菜单权限
	 */
	@PostMapping("save")
	@OperLog(title = "菜单管理", businessType = BusinessType.INSERT)
	public R addSave(@RequestBody SysMenu sysMenu) {
		return toAjax(sysMenuService.insertMenu(sysMenu));
	}

	/**
	 * 修改保存菜单权限
	 */
	@OperLog(title = "菜单管理", businessType = BusinessType.UPDATE)
	@PostMapping("update")
	public R editSave(@RequestBody SysMenu sysMenu) {
		return toAjax(sysMenuService.updateMenu(sysMenu));
	}

	/**
	 * 删除菜单权限
	 */
	@OperLog(title = "菜单管理", businessType = BusinessType.DELETE)
	@PostMapping("remove/{menuId}")
	public R remove(@PathVariable("menuId") Long menuId) {
		return toAjax(sysMenuService.deleteMenuById(menuId));
	}
}
