package net.ys.util;

import com.alibaba.fastjson.JSONObject;
import net.ys.bean.Person;

import java.lang.reflect.*;
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

    public static void main(String[] args) throws NoSuchMethodException {
        Object init = genObject(Person.class);
        System.out.println(JSONObject.toJSONString(init));

        Method method = InitObject.class.getDeclaredMethod("testMethod", Person.class, List.class);
        Object[] objects = genParameterObject(method);
        for (Object object : objects) {
            System.out.println(object);
        }
    }

    public static Object genObject(Class<?> clazz) {
        try {
            return initObj(clazz);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object[] genParameterObject(Method method) {
        try {
            int parameterCount = method.getParameterCount();
            Object[] objects = new Object[parameterCount];
            int i = 0;
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                objects[i] = InitObject.initParameterObj(parameter);
                i++;
            }
            return objects;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化对象
     *
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object initObj(Class clazz) throws IllegalAccessException, InstantiationException {

        int dType = getDataType(clazz);
        if (dType == 1) {
            return initBaseTypeObj(clazz);
        } else if (dType == 0) {
            return initArrayObj(clazz);
        } else if (dType == 2) {
            return null;
        }

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
                    value = initArrayObj(type);
                    break;
                case 1:
                    value = initBaseTypeObj(type);
                    break;
                case 2:
                    value = initCollectionObj(field);
                    break;
                default:
                    value = initBaseBean(type);
                    break;
            }
            field.set(o, value);
        }
        return o;
    }

    /**
     * 初始化参数对象
     *
     * @param parameter
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object initParameterObj(Parameter parameter) throws IllegalAccessException, InstantiationException {
        Class<?> type = parameter.getType();
        int dataType = InitObject.getDataType(type);
        Object value;
        switch (dataType) {
            case 0:
                value = initArrayObj(type);
                break;
            case 1:
                value = initBaseTypeObj(type);
                break;
            case 2:
                value = initCollectionObjParameter(parameter);
                break;
            default:
                value = initBaseBean(type);
                break;
        }

        return value;
    }

    private static Object initArrayObj(Class<?> type) throws InstantiationException, IllegalAccessException {
        Class<?> componentType = getComponentType(type);//内部类型
        int dataType = getDataType(componentType);
        Object value;
        switch (dataType) {
            case 1:
                value = initBaseTypeObj(componentType);
                break;
            case 3:
                value = initBaseBean(componentType);
                break;
            default:
                throw new IllegalStateException("Unexpected value: 1/3");
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

    private static Object initCollectionObjParameter(Parameter parameter) throws IllegalAccessException, InstantiationException {
        String name = parameter.getType().getName().toLowerCase();
        if (name.endsWith("list")) {
            ParameterizedType listType = (ParameterizedType) parameter.getParameterizedType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
            List list = new ArrayList<>();
            Object val = genCollectionValue(listClass);
            list.add(val);
            return list;
        } else if (name.endsWith("set")) {
            ParameterizedType setType = (ParameterizedType) parameter.getParameterizedType();
            Class<?> setClass = (Class<?>) setType.getActualTypeArguments()[0];
            Set set = new HashSet();
            Object val = genCollectionValue(setClass);
            set.add(val);
            return set;
        } else if (name.endsWith("map")) {
            ParameterizedType mapType = (ParameterizedType) parameter.getParameterizedType();
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
            case 1:
                value = initBaseTypeObj(clazz);
                break;
            case 3:
                value = initBaseBean(clazz);
                break;
            default:
                throw new IllegalStateException("Unexpected value: 1/3");
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
                case 1:
                    value = initBaseTypeObj(type);
                    break;
                case 3:
                    value = initBaseBean(type);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: 1/3");
            }
            field.set(obj, value);
        }

        return obj;
    }

    /**
     * @param clazz
     * @return 0：数组、1：基础类型、2：集合类型、3：对象类型
     */
    public static int getDataType(Class clazz) {
        String name = clazz.getName().toLowerCase();
        if (name.startsWith("[")) {
            return 0;
        } else if (name.matches("^(java\\.(lang|math)\\.)?(string|integer|double|float|long|char|short|int|byte|boolean|character|bigdecimal)")) {
            return 1;
        } else if (name.matches("^(java\\.util\\.).*(map|list|set)&")) {
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

    public void testMethod(Person person, List<String> list) {
        System.out.println(person);
        System.out.println(list);
    }
}
