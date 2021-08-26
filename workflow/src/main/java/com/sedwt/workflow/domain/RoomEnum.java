package com.sedwt.workflow.domain;

/**
 * @author : yj zhang
 * @since : 2021/8/19 14:03
 */

public enum RoomEnum {
    WIDE_ROOM(0,"大会议室"),
    SMALL_ROOM(1, "小会议室");

    private final int val;
    private final String desc;

     RoomEnum(int val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public int getVal() {
        return val;
    }

    public String getDesc(){
         return desc;
    }
}
