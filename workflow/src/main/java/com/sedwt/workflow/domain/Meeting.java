package com.sedwt.workflow.domain;

import com.sedwt.common.entity.BaseEntity;
import lombok.Data;

@Data
public class Meeting extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long meetingId;

    private Integer roomNumber;

    /**
     * 会议室状态
     * 0-待使用 1-使用中 2-已结束
     */
    private Integer status;

    /**
     * 会议对应的项目、产品、迭代、评审等
     */
    private Long projectId;

    private String remark;

    private String beginTime;

    private String endTime;
}
