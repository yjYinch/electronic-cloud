package com.sedwt.workflow.domain;

import java.util.stream.Stream;

/**
 * 评审状态枚举类
 * @author : yj zhang
 * @since : 2021/8/19 14:22
 */
public enum AppraisalStatusEnum {
    /**
     * 字段注释如desc描述所示
     */
    UN_COMMITTED(0, "未提交"),
    PRE_APPRAISAL(1,"待评审"),
    DURING_PRE_APPRAISAL(2, "评审中"),
    COMPLETED(3, "已完成"),
    CANCELED(4, "已撤销"),
    CLOSED(5, "已关闭");

    private final int val;
    private final String desc;

    AppraisalStatusEnum(int val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public int getVal() {
        return val;
    }

    public String getDesc(){
        return desc;
    }

    /**
     * 根据val获取对应的枚举类描述
     * @param val
     * @return
     */
    public static String getDescByVal(int val) {
        return Stream.of(values())
                .filter(m -> m.val == val)
                .findFirst()
                .map(AppraisalStatusEnum::getDesc)
                .orElse("未知操作");
    }
}
