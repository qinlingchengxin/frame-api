package net.ys.util;

import net.sf.json.JSONObject;
import net.ys.constant.SysRegex;
import net.ys.constant.X;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;

/**
 * User: NMY
 * Date: 16-9-8
 */
public class Tools {

    /**
     * 判断多个字符串是否为空
     *
     * @param strings
     * @return
     */
    public static boolean isNotEmpty(String... strings) {
        if (strings == null || strings.length == 0) {
            return false;
        }
        for (String str : strings) {
            if (str == null || "".equals(str.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取随机数字
     *
     * @return
     */
    public static int randomInt() {
        Random rand = new Random();
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < 6; i++) {
            if (i == 0 && array[i] == 0) {
                array[i] = 1;
            }
            result = result * 10 + array[i];
        }
        return result;
    }

    /**
     * 获取指定长度随机字符串
     *
     * @param len
     * @return
     */
    public static String randomString(int len) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, len);
    }

    /**
     * 获取随机数字
     *
     * @return
     */
    public static int randomInt(int num) {
        Random rand = new Random();
        return rand.nextInt(num);
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String genMD5(String str) {
        try {
            if (str == null || "".equals(str.trim())) {
                return "";
            }
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest((str).getBytes(X.Code.U));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bs.length; i++) {
                sb.append(Character.forDigit((bs[i] >>> 4) & 0x0F, 16)).append(Character.forDigit(bs[i] & 0x0F, 16));
            }
            return sb.toString();
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 生成问号字符串
     *
     * @param size
     * @return
     */
    public static String genMark(int size) {
        StringBuffer sb = new StringBuffer("?");
        for (int i = 1; i < size; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }

    /**
     * 将下划线类型字符串转换为驼峰标识，eg: create_time -> createTime
     *
     * @param resource
     * @return
     */
    public static String camelFormat(String resource) {
        if (resource != null && resource.trim().length() > 0) {
            String[] strings = resource.split("_");
            if (strings.length > 1) {
                StringBuffer sb = new StringBuffer();
                sb.append(strings[0].toLowerCase());
                for (int i = 1; i < strings.length; i++) {
                    sb.append(firstToUpperCase(strings[i]));
                }
                return sb.toString();
            } else {
                return strings[0].toLowerCase();
            }
        }
        return "";
    }

    /**
     * 将驼峰标识转化成数据库存储格式，eg: createTime -> create_time
     */
    public static String camelToDb(String resource) {
        return resource.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    public static String firstToUpperCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    /**
     * 转换文件大小
     *
     * @param fileSize 字节
     * @return
     */
    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String result;
        if (fileSize <= 0) {
            result = "0B";
        } else if (fileSize < 1024) {
            result = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            result = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            result = df.format((double) fileSize / 1048576) + "MB";
        } else {
            result = df.format((double) fileSize / 1073741824) + "GB";
        }
        return result;
    }

    public static String genShortUrl(String url) throws IOException {
        HttpClient httpclient = new HttpClient();
        PostMethod postMethod = new PostMethod("https://dwz.cn/admin/create");
        RequestEntity entity = new StringRequestEntity("{\"url\":\"" + url + "\"}", "application/json", X.Code.U);
        postMethod.setRequestEntity(entity);
        postMethod.setRequestHeader("Content-Type", X.ContentType.JSON);

        int statusCode = httpclient.executeMethod(postMethod);
        if (statusCode == 200) {
            String result = postMethod.getResponseBodyAsString();
            JSONObject object = JSONObject.fromObject(result);
            int code = object.optInt("Code");
            if (code == 0) {
                return object.optString("ShortUrl");
            }
        }
        return null;
    }

    /**
     * 校验手机号合法性
     *
     * @param phoneNumber
     * @return
     */
    public static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !"".equals(phoneNumber.trim())) {
            return phoneNumber.matches(SysRegex.PHONE_NUMBER.regex);
        }
        return false;
    }

    /**
     * 获取当前进程号
     */
    public static final int getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0]).intValue();
    }

    /**
     * 佛祖保佑
     */
    public static void godBless() {
        System.out.println("" +
                "                    _ooOoo_\n" +
                "                   o8888888o\n" +
                "                   88\" . \"88\n" +
                "                   (| -_- |)\n" +
                "                   O\\  =  /O\n" +
                "                ____/`---'\\____\n" +
                "              .'  \\\\|     |//  `.\n" +
                "             /  \\\\|||  :  |||//  \\\n" +
                "            /  _||||| -:- |||||-  \\\n" +
                "            |   | \\\\\\  -  /// |   |\n" +
                "            | \\_|  ''\\---/''  |   |\n" +
                "            \\  .-\\__  `-`  ___/-. /\n" +
                "          ___`. .'  /--.--\\  `. . __\n" +
                "       .\"\" '<  `.___\\_<|>_/___.'  >'\"\".\n" +
                "      | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |\n" +
                "      \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /\n" +
                " ======`-.____`-.___\\_____/___.-`____.-'======\n" +
                "                    `=---='\n" +
                "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" +
                "              Buddha Bless, No Bug !");
    }

    /**
     * 生成toString，json格式，目前只支持int/long/String/BigDecimal
     *
     * @param clazz
     * @return
     */
    public static String genToString(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 0) {
            return "";
        }

        StringBuffer sb = new StringBuffer("\"{");
        for (Field field : fields) {
            sb.append("\\\"").append(field.getName()).append("\\\":");

            Class<?> type = field.getType();
            if (int.class == type || long.class == type || BigDecimal.class == type) {
                sb.append("\"+").append(field.getName()).append("+");
            } else if (String.class == type) {
                sb.append("\\\"\"+").append(field.getName()).append("+").append("\"\\");
            }
            sb.append("\",");
        }
        sb.deleteCharAt(sb.length() - 1).append("}\"");
        return sb.toString();
    }

    /**
     * @param clazz
     * @return 0：数组、1：基础类型、2：集合类型、3：对象类型
     */
    public static int getDataType(Class clazz) {
        String name = clazz.getName().toLowerCase();
        if (name.startsWith("[")) {
            return 0;
        } else if (name.matches(".*(string|double|float|long|char|short|int|byte|boolean|decimal).*")) {
            return 1;
        } else if (name.matches(".*(map|list|set)")) {
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

    /**
     * 判断是否是基本类型的包装类
     *
     * @param clz
     * @return
     */
    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(camelToDb("deleteCharAt"));
    }
}
