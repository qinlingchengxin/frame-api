package net.ys;

import com.alibaba.fastjson.JSONObject;
import net.ys.bean.Person;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.*;

/**
 * 初始化对象
 * User: NMY
 * Date: 2019-9-17
 * Time: 8:50
 * 说明
 * 1、父对象可以包含，int,long,bigDecimal,List,Set,Map,Object类型数据
 * 2、List、Set中可以包含int,long,bigDecimal,Object类型，但是Object类型只能包含基础类型
 * 3、Map中key为string类型，Value只能包含基础类型
 * 4、List、Set、Map必须制定泛型类型
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
            if (type == String.class) {
                field.set(o, field.getName() + "_val");
            } else if (type == int.class || type == long.class || type == Integer.class || type == Long.class) {
                field.set(o, 1);
            } else if (type == BigDecimal.class) {
                field.set(o, new BigDecimal(1));
            } else if (type == List.class) {
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                List list = new ArrayList<>();
                if (listClass == String.class) {
                    list.add("list_string_val");
                } else if (listClass == Integer.class || listClass == Long.class) {
                    list.add(1);
                } else if (listClass == BigDecimal.class) {
                    list.add(new BigDecimal(1));
                } else {
                    Object listObj = initBaseBean(listClass);
                    list.add(listObj);
                }
                field.set(o, list);
            } else if (type == Set.class) {
                ParameterizedType setType = (ParameterizedType) field.getGenericType();
                Class<?> setClass = (Class<?>) setType.getActualTypeArguments()[0];
                Set set = new HashSet();
                if (setClass == String.class) {
                    set.add("set_string_val");
                } else if (setClass == Integer.class || setClass == Long.class) {
                    set.add(1);
                } else if (setClass == BigDecimal.class) {
                    set.add(new BigDecimal(1));
                } else {
                    Object setObj = initBaseBean(setClass);
                    set.add(setObj);
                }
                field.set(o, set);
            } else if (type == Map.class) {
                ParameterizedType mapType = (ParameterizedType) field.getGenericType();
                Class<?> valueClass = (Class<?>) mapType.getActualTypeArguments()[1];
                Map map = new HashMap();
                if (valueClass == String.class) {
                    map.put("key", "map_string_val");
                } else if (valueClass == Integer.class || valueClass == Long.class) {
                    map.put("key", 1);
                } else if (valueClass == BigDecimal.class) {
                    map.put("key", new BigDecimal(1));
                } else {
                    Object valObj = initBaseBean(valueClass);
                    map.put("key", valObj);
                }
                field.set(o, map);
            } else {
                Object obj = initBaseBean(type);
                field.set(o, obj);
            }
        }
        return o;
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
            if (type == String.class) {
                field.set(obj, field.getName() + "_obj_val");
            } else if (type == int.class || type == long.class || type == Integer.class || type == Long.class) {
                field.set(obj, 1);
            } else if (type == BigDecimal.class) {
                field.set(obj, new BigDecimal(1));
            }
        }

        return obj;
    }
}
