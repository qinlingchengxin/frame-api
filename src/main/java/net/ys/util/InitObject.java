package net.ys.util;

import com.alibaba.fastjson.JSONObject;
import net.ys.bean.Person;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始化对象
 * User: NMY
 * Date: 2019-9-17
 * Time: 8:50 说明，父对象可以包含，int,long,bigDecimal,List,Object类型数据，List中可以包含int,long,bigDecimal,Object类型，但是Object类型只能包含基础类型
 */
public class InitObject {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object init = init(Person.class);
        String string = JSONObject.toJSONString(init);
        System.out.println(string);
    }

    public static Object init(Class clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Field[] fields = clazz.getDeclaredFields();

        Object o = clazz.newInstance();

        for (Field field : fields) {
            Class<?> type = field.getType();
            String name = field.getName();

            if ("serialVersionUID".equals(name)) {
                continue;
            }
            String setMethod = "set" + String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
            Method method = clazz.getDeclaredMethod(setMethod, type);

            if (type == String.class) {
                method.invoke(o, field.getName() + "_val");
            } else if (type == int.class || type == long.class || type == Integer.class || type == Long.class) {
                method.invoke(o, 1);
            } else if (type == BigDecimal.class) {
                method.invoke(o, new BigDecimal(1));
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
                    Object listObj = listClass.newInstance();
                    Field[] listFields = listClass.getDeclaredFields();

                    for (Field listField : listFields) {
                        Class<?> t = listField.getType();
                        String n = listField.getName();
                        String sm = "set" + String.valueOf(n.charAt(0)).toUpperCase() + n.substring(1);
                        Method mm = listClass.getDeclaredMethod(sm, t);
                        if (t == String.class) {
                            mm.invoke(listObj, listField.getName() + "_list_val");
                        } else if (t == int.class || t == long.class || t == Integer.class || t == Long.class) {
                            mm.invoke(listObj, 1);
                        } else if (t == BigDecimal.class) {
                            mm.invoke(listObj, new BigDecimal(1));
                        }
                    }
                    list.add(listObj);
                }
                method.invoke(o, list);
            } else {
                Object obj = type.newInstance();
                Field[] objFields = type.getDeclaredFields();
                for (Field objField : objFields) {
                    Class<?> t = objField.getType();
                    String n = objField.getName();
                    String sm = "set" + String.valueOf(n.charAt(0)).toUpperCase() + n.substring(1);
                    Method mm = type.getDeclaredMethod(sm, t);
                    if (t == String.class) {
                        mm.invoke(obj, objField.getName() + "_obj_val");
                    } else if (t == int.class || t == long.class || t == Integer.class || t == Long.class) {
                        mm.invoke(obj, 1);
                    } else if (t == BigDecimal.class) {
                        mm.invoke(obj, new BigDecimal(1));
                    }
                }
                method.invoke(o, obj);
            }
        }
        return o;
    }
}
