package com.yy.cs.base.jdk;

import java.lang.reflect.Field;

public class ReflectUtils {

    /**
     * 获取类里面的某个字段，在递归获取的情况下，子类定义的字段优先返回
     * @param clz 类
     * @param fieldName 字段名
     * @param recursion 是否递归获取父类的字段
     * @return
     * @throws Exception
     */
    public static Field getClassField(Class<?> clz, String fieldName, boolean recursion) throws Exception {
        Field[] declaredFields = clz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        if (recursion) {
            Class<?> superclass = clz.getSuperclass();
            if (superclass != null) {
                return getClassField(superclass, fieldName, recursion);
            }
        }
        return null;
    }

}
