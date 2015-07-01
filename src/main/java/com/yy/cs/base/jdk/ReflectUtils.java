package com.yy.cs.base.jdk;

import java.lang.reflect.Field;

public class ReflectUtils {

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
