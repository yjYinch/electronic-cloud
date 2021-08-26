package com.sedwt.workflow.domain;


import com.sedwt.common.entity.BaseEntity;
import com.sedwt.common.entity.file.AttachedFile;
import com.sedwt.common.entity.sys.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class Appraisal extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

    /**
     * 评审id
     */
    private Long appraisalId;

    /**
     * 评审标题
     */
    private String appraisalTitle;

    /**
     * 评审地点
     * 0 : 大会议室
     * 1 : 小会议室
     */
    private Integer roomNumber;

    /**
     * 评审概述
     */
    private String appraisalDesc;

    /**
     * 附件链接
     * 多个附件以逗号 “,” 隔开
     */
    private String attachmentUrls;

    /**
     * 评审状态
     *      0-未提交
     *      1-待评审
     *      2-评审中
     *      3-已完成
     *      4-已撤销
     *      5-已关闭
     */
    private Integer status;

    /**
     * 预审
     */
    private boolean preAppraisal;
    /**
     * 评审中
     */
    private boolean duringAppraisal;

    /**
     * 已完成
     */
    private boolean completed;

    /**
     * 已撤销
     */
    private boolean canceled;

    /**
     * 评审人数
     */
    private Integer appraisalPersons;

    private Integer handledPersons;

    /**
     * 评审参与人员
     */
    private List<SysUser> participants;

    private List<Comment> comments;

    private List<AttachedFile> attachments;

    private Long version;

    /**
     * 评审创建人用户名
     */
    private String creatorName;

    private String handleTime;

    private Integer isOwner;

    /**
     * 评审人员邮箱地址
     */
    private String emailAddress;

    /**
     * 评审/会议开始时间
     */
    private String beginTime;

    /**
     * 评审/会议结束时间
     */
    private String endTime;
    
}
