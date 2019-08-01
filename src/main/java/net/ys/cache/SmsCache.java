package net.ys.cache;

import net.ys.component.SysConfig;
import net.ys.constant.CacheKey;
import net.ys.constant.X;
import net.ys.util.TimeUtil;
import net.ys.util.Tools;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import javax.annotation.Resource;

/**
 * User: NMY
 * Date: 16-11-21
 */
@Repository
public class SmsCache {

    @Resource
    private JedisPool redsPool;

    /**
     * 发送短信
     *
     * @param phone   手机号
     * @param smsCode 验证码
     * @return
     */
    public boolean sendSms(final String phone, final String smsCode) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            Pipeline pipeline = reds.pipelined();
            pipeline.set(CacheKey.PHONE_KEY + phone, smsCode);
            pipeline.expire(CacheKey.PHONE_KEY + phone, SysConfig.smsEffectiveTime);
            pipeline.incr(CacheKey.PHONE_DAY_KEY + phone);
            pipeline.expire(CacheKey.PHONE_DAY_KEY + phone, TimeUtil.todayRemainingSecond());
            pipeline.sync();
            return true;

        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return false;
    }

    /**
     * 验证是否超出一天发送次数
     *
     * @param phone
     * @return
     */
    public boolean validateSmsDay(final String phone) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            return reds.incr(CacheKey.PHONE_DAY_KEY + phone) <= SysConfig.smsMaxNumDay;
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return false;
    }

    /**
     * 获取发送的验证码
     *
     * @param phone
     * @return
     */
    public String getSmsCode(final String phone) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            return reds.get(CacheKey.PHONE_KEY + phone);
        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return null;
    }

    /**
     * 创建4位验证码，若存在则返回
     *
     * @param phone
     * @return
     */
    public String newSmsCode(final String phone) {
        Jedis reds = null;
        try {
            reds = redsPool.getResource();
            long expire = reds.ttl(CacheKey.PHONE_KEY + phone);
            if (expire > X.Time.MINUTE_SECOND * 4) {//5分钟有效期，但是1分钟内不得重发
                return null;
            }
            return Integer.toString(Tools.randomInt()).substring(0, 4);

        } catch (Exception e) {
        } finally {
            this.close(reds);
        }
        return null;
    }

    /**
     * 释放连接
     *
     * @param reds
     */
    public void close(Jedis reds) {
        if (reds != null) {
            reds.close();
            if (reds.isConnected()) {
                try {
                    reds.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
