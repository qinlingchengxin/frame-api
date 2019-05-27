package net.ys.cache;

import net.ys.constant.CacheKey;
import net.ys.constant.X;
import net.ys.storage.RedsExecutor;
import net.ys.storage.RedsRunner;
import net.ys.storage.RedsServer;
import net.ys.utils.PropertyUtil;
import net.ys.utils.TimeUtil;
import net.ys.utils.Tools;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.PostConstruct;

/**
 * User: LiWenC
 * Date: 16-11-21
 */
@Repository
public class SmsCache {

    public int phoneMaxNumDay;//每天发送限制
    public int smsEffectiveTime;//短信有效时间

    @PostConstruct
    public void init() {
        phoneMaxNumDay = Integer.parseInt(PropertyUtil.get("sms_max_num_day"));
        smsEffectiveTime = Integer.parseInt(PropertyUtil.get("sms_effective_time")) * X.Time.MINUTE_SECOND;
    }

    /**
     * 发送短信
     *
     * @param phone   手机号
     * @param smsCode 验证码
     * @return
     */
    public boolean sendSms(final String phone, final String smsCode) {
        RedsRunner<Boolean> rr = new RedsRunner<Boolean>() {
            @Override
            public Boolean run(Jedis jedis) throws JedisConnectionException {
                Pipeline pipeline = jedis.pipelined();
                pipeline.set(CacheKey.PHONE_KEY + phone, smsCode);
                pipeline.expire(CacheKey.PHONE_KEY + phone, smsEffectiveTime);
                pipeline.incr(CacheKey.PHONE_DAY_KEY + phone);
                pipeline.expire(CacheKey.PHONE_DAY_KEY + phone, TimeUtil.todayRemainingSecond());
                pipeline.sync();
                return true;
            }
        };
        return new RedsExecutor<Boolean>().exe(rr, RedsServer.MASTER);
    }

    /**
     * 验证是否超出一天发送次数
     *
     * @param phone
     * @return
     */
    public boolean validateSmsDay(final String phone) {
        RedsRunner<Boolean> rr = new RedsRunner<Boolean>() {
            @Override
            public Boolean run(Jedis jedis) throws JedisConnectionException {
                return jedis.incr(CacheKey.PHONE_DAY_KEY + phone) <= phoneMaxNumDay;
            }
        };
        return new RedsExecutor<Boolean>().exe(rr, RedsServer.MASTER);
    }

    /**
     * 获取发送的验证码
     *
     * @param phone
     * @return
     */
    public String getSmsCode(final String phone) {
        RedsRunner<String> rr = new RedsRunner<String>() {
            @Override
            public String run(Jedis jedis) throws JedisConnectionException {
                return jedis.get(CacheKey.PHONE_KEY + phone);
            }
        };
        return new RedsExecutor<String>().exe(rr, RedsServer.MASTER);
    }

    /**
     * 创建4位验证码，若存在则返回
     *
     * @param phone
     * @return
     */
    public String newSmsCode(final String phone) {
        RedsRunner<String> rr = new RedsRunner<String>() {
            @Override
            public String run(Jedis jedis) throws JedisConnectionException {
                long expire = jedis.ttl(CacheKey.PHONE_KEY + phone);
                if (expire > X.Time.MINUTE_SECOND * 4) {//5分钟有效期，但是1分钟内不得重发
                    return null;
                }
                return Integer.toString(Tools.randomInt()).substring(0, 4);
            }
        };
        return new RedsExecutor<String>().exe(rr, RedsServer.MASTER);
    }
}
