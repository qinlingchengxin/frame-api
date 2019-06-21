package net.ys.constant;

/**
 * 缓存的key键
 * User: NMY
 * Date: 17-9-21
 */
public interface CacheKey {

    String SYS_ENUM_KEY = "SYS_ENUM";//系统枚举变量

    String USER_KEY = "USER:"; //用户数据

    String PHONE_DAY_KEY = "PHONE_DAY:";//每天发送短信限制

    String PHONE_KEY = "PHONE:";//存储手机号验证码
}
