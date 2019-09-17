package net.ys.util;

import com.alibaba.fastjson.JSONObject;
import net.ys.bean.Person;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.*;

/**
 * 初始化对象，还算牛逼
 * User: NMY
 * Date: 2019-9-17
 * Time: 8:50
 * 说明：
 * 1、父对象可以包含，基本类型、集合类型、数组类型、普通对象，不过List、Set中只能包含基本类型、普通对象，
 * 2、Map中key为string类型，Value只能包含基本类型、普通对象
 * 3、普通对象中只能包含基本类型
 * 4、List、Set、Map必须强制指定泛型类型
 */
public class InitObject {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Object init = init(Person.class);
        String string = JSONObject.toJSONString(init);
        System.out.println(string);
    }

    public static Object init(Class clazz) throws IllegalAccessException, InstantiationException {
        Field[] fields = clazz.getDeclaredFields();
        Object o = clazz.newInstance();

        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            field.setAccessible(true);
            Class<?> type = field.getType();
            int dataType = getDataType(type);
            Object value;
            switch (dataType) {
                case 0:
                    value = initBaseTypeObj(type);
                    break;
                case 1:
                    value = initCollectionObj(field);
                    break;
                case 2:
                    value = initArrayObj(type);
                    break;
                default:
                    value = initBaseBean(type);
                    break;
            }
            field.set(o, value);
        }
        return o;
    }

    private static Object initArrayObj(Class<?> type) throws InstantiationException, IllegalAccessException {
        Class<?> componentType = getComponentType(type);//内部类型
        int dataType = getDataType(componentType);
        Object value;
        switch (dataType) {
            case 0:
                value = initBaseTypeObj(componentType);
                break;
            case 3:
                value = initBaseBean(componentType);
                break;
            default:
                throw new IllegalStateException("Unexpected value: 0/3");
        }
        Object[] o = (Object[]) Array.newInstance(componentType, 1);//根据给定类型初始化数组
        o[0] = value;
        return o;
    }

    private static Object initCollectionObj(Field field) throws IllegalAccessException, InstantiationException {
        String name = field.getType().getName().toLowerCase();
        if (name.endsWith("list")) {
            ParameterizedType listType = (ParameterizedType) field.getGenericType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
            List list = new ArrayList<>();
            Object val = genCollectionValue(listClass);
            list.add(val);
            return list;
        } else if (name.endsWith("set")) {
            ParameterizedType setType = (ParameterizedType) field.getGenericType();
            Class<?> setClass = (Class<?>) setType.getActualTypeArguments()[0];
            Set set = new HashSet();
            Object val = genCollectionValue(setClass);
            set.add(val);
            return set;
        } else if (name.endsWith("map")) {
            ParameterizedType mapType = (ParameterizedType) field.getGenericType();
            Class<?> valueClass = (Class<?>) mapType.getActualTypeArguments()[1];
            Map map = new HashMap();
            Object val = genCollectionValue(valueClass);
            map.put("key", val);
            return map;
        }
        return null;
    }

    private static Object initBaseTypeObj(Class<?> type) {
        if (type == String.class) {
            return "str_val";
        } else if (type == Boolean.class || type == boolean.class) {
            return false;
        } else if (type == Integer.class || type == int.class) {
            return 1;
        } else if (type == Long.class || type == long.class) {
            return 1L;
        } else if (type == Double.class || type == double.class) {
            return 1.0;
        } else if (type == Float.class || type == float.class) {
            return 1.0f;
        } else if (type == Short.class || type == short.class) {
            return Short.parseShort("1");
        } else if (type == Byte.class || type == byte.class) {
            return (byte) 1;
        } else if (type == Character.class || type == char.class) {
            return 'a';
        } else if (type == BigDecimal.class) {
            return new BigDecimal(1);
        }
        return null;
    }

    /**
     * 生成集合值
     *
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Object genCollectionValue(Class clazz) throws InstantiationException, IllegalAccessException {
        int dataType = getDataType(clazz);
        Object value;
        switch (dataType) {
            case 0:
                value = initBaseTypeObj(clazz);
                break;
            case 3:
                value = initBaseBean(clazz);
                break;
            default:
                throw new IllegalStateException("Unexpected value: 0/3");
        }

        return value;
    }

    /**
     * 初始化基础bean
     *
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object initBaseBean(Class clazz) throws IllegalAccessException, InstantiationException {
        Object obj = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            field.setAccessible(true);
            Class<?> type = field.getType();
            int dataType = getDataType(type);
            Object value;
            switch (dataType) {
                case 0:
                    value = initBaseTypeObj(type);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: 0");
            }
            field.set(obj, value);
        }

        return obj;
    }

    /**
     * @param clazz
     * @return 0：基础类型、1：集合类型、2：数组、3：对象类型
     */
    public static int getDataType(Class clazz) {
        String name = clazz.getName().toLowerCase();
        if (name.matches(".*(string|double|float|long|char|short|int|byte|boolean|decimal).*")) {
            return 0;
        } else if (name.matches(".*(map|list|set)")) {
            return 1;
        } else if (name.startsWith("[")) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * 获取数组类元素的类型
     *
     * @param arrayClass
     * @return
     */
    public static Class<?> getComponentType(Class<?> arrayClass) {
        return arrayClass.getComponentType();
    }

}
