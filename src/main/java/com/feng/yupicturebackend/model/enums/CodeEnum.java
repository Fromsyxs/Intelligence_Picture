package com.feng.yupicturebackend.model.enums;

import lombok.Data;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 缘在风里
 * @createDate : 2025/7/21 0:44
 */
public interface CodeEnum {
    int getCode();

    String getDesc();

    static List<TypeObject> getAll(CodeEnum[] codeEnums) {
//        List<TypeObject> list = new ArrayList<>();
//        for (CodeEnum codeEnum : codeEnums) {
//            TypeObject typeObject = new TypeObject();
//            typeObject.setCode(codeEnum.getCode());
//            typeObject.setDesc(codeEnum.getDesc());
//            list.add(typeObject);
//        }
//        return list;
        return Arrays.stream(codeEnums)
                .map(e -> new TypeObject(e.getCode(), e.getDesc()))
                .collect(Collectors.toList());
    }

    @Data
    class TypeObject {
        int code;
        String desc;

        public TypeObject(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
