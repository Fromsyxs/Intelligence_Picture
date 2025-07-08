package com.feng.yupicturebackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : 缘在风里
 * @createDate : 2025/7/9 0:29
 *
 * 通用的删除请求类
 */
@Data
public class DeleteResponse implements Serializable {
    /**
     * id
     */
    private int id;

    private static final long serialVersionUID = 1L;
}
