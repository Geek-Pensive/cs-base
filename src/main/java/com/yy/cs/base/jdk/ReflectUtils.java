package com.yy.cs.base.jdk;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectUtils {

    /**
     * 获取类里面的某个字段，在递归获取的情况下，子类定义的字段优先返回
     * 
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

    /**
     * 获取类定义的所有字段
     * 
     * @param clz
     * @param recursion
     * @return
     * @throws Exception
     */
    public static List<Field> getClassFields(Class<?> clz, boolean recursion) throws Exception {
        List<Field> fields = new ArrayList<Field>();
        getClassFields(clz, recursion, fields);
        return fields;
    }

    private static void getClassFields(Class<?> clz, boolean recursion, List<Field> fields) throws Exception {
        Field[] declaredFields = clz.getDeclaredFields();
        for (Field f : declaredFields) {
            fields.add(f);
        }
        if (recursion) {
            Class<?> superclass = clz.getSuperclass();
            if (superclass != null) {
                getClassFields(superclass, recursion, fields);
            }
        }
    }

    /**
     * 获取对象的字段的值，返回map，key为对象字段的名称
     * 
     * @param obj
     * @param recursion
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getFieldsValueMap(Object obj, boolean recursion) throws Exception {
        Map<String, Object> ret = new HashMap<String, Object>();
        List<Field> fields = getClassFields(obj.getClass(), recursion);
        if (fields.size() > 0) {
            for (Field f : fields) {
                f.setAccessible(true);
                Object res = f.get(obj);
                ret.put(f.getName(), res);
            }
        }
        return ret;
    }

}
