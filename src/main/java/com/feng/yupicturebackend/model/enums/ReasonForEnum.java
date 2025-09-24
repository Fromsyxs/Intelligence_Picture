package com.feng.yupicturebackend.model.enums;

import lombok.Getter;

/**
 * @author : 缘在风里
 * @createDate : 2025/7/21 0:42
 */
@Getter
public enum ReasonForEnum implements CodeEnum{
    PENDING(1, "待处理"),
    PROCESSING(2, "处理中"),
    COMPLETED(3, "已完成");

    private final int code;
    private final String desc;

    ReasonForEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
