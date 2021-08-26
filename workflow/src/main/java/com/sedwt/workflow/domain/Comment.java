package com.sedwt.workflow.domain;

import com.sedwt.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Comment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long rowId;

    private Long appraisalId;

    private Long userId;

    private String userName;

    private String questionLocate;

    private String questionDesc;

    private Integer handleStatus;

}
