package com.sedwt.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sedwt.common.annotation.Excel;
import com.sedwt.common.annotation.Excel.Type;
import com.sedwt.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户对象 sys_user
 *
 * @author ruoyi
 */
public class SysUser extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/** 用户ID */
	@Excel(name = "用户序号", prompt = "用户编号")
	private Long userId;

	/** 登录名称 */
	@Excel(name = "登录名称")
	private String loginName;

	/** 用户名称 */
	@Excel(name = "用户名称")
	private String userName;

	/** 用户邮箱 */
	@Excel(name = "用户邮箱")
	private String email;

	/** 手机号码 */
	@Excel(name = "手机号码")
	private String phonenumber;

	/** 用户性别 */
	@Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
	private String sex;

	/** 用户头像 */
	private String avatar;

	/** 密码 */
	private String password;

	/** 新密码 */
	private String newPassword;

	/** 盐加密 */
	private String salt;

	/** 帐号状态（0正常 1停用） */
	@Excel(name = "帐号状态", readConverterExp = "0=正常,1=停用")
	private String status;

	/** 删除标志（0代表存在 2代表删除） */
	private String delFlag;

	/** 最后登陆IP */
	@Excel(name = "最后登陆IP", type = Type.EXPORT)
	private String loginIp;

	/** 最后登陆时间 */
	@Excel(name = "最后登陆时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date loginDate;

	private List<com.sedwt.system.domain.SysRole> roles;

	/** 角色组 */
	private List<Long> roleIds;

	private Set<String> buttons;

	private Integer isOwner;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean isAdmin() {
		return isAdmin(this.userId);
	}

	public static boolean isAdmin(Long userId) {
		return userId != null && 1L == userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public List<com.sedwt.system.domain.SysRole> getRoles() {
		return roles;
	}

	public void setRoles(List<com.sedwt.system.domain.SysRole> roles) {
		this.roles = roles;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	public Set<String> getButtons() {
		return buttons;
	}

	public void setButtons(Set<String> buttons) {
		this.buttons = buttons;
	}

	public Integer getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(Integer isOwner) {
		this.isOwner = isOwner;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("userId", getUserId())
				.append("loginName", getLoginName()).append("userName", getUserName())
				.append("email", getEmail()).append("phonenumber", getPhonenumber()).append("sex", getSex())
				.append("avatar", getAvatar()).append("password", getPassword()).append("salt", getSalt())
				.append("status", getStatus()).append("delFlag", getDelFlag()).append("loginIp", getLoginIp())
				.append("loginDate", getLoginDate()).append("createBy", getCreateBy())
				.append("createTime", getCreateTime()).append("updateBy", getUpdateBy())
				.append("updateTime", getUpdateTime()).append("remark", getRemark())
				.toString();
	}
}