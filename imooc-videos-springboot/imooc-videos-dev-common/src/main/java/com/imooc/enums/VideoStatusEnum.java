package com.imooc.enums;

/**
 * @Author: mate_J
 * @Date: 2018/12/17 14:08
 * @Version 1.0
 */
public enum VideoStatusEnum {
    SUCCESS(1,"发布成功"),
    FORBIT(2,"禁止播放");
    private final int value;
    private final String msg;

    VideoStatusEnum(int value,String msg) {
        this.value = value;
        this.msg = msg;
    }

    public int getValue() {
        return value;
    }
}
