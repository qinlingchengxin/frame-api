package net.ys.service;

import net.ys.cache.SmsCache;
import net.ys.dao.SmsDao;
import net.ys.utils.SmsUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SmsService {

    @Resource
    private SmsCache smsCache;

    @Resource
    private SmsDao smsDao;

    /**
     * 检查是否超过当日短信发送限制
     *
     * @param phoneNum
     */
    public boolean validateSms(String phoneNum) {
        return smsCache.validateSmsDay(phoneNum);
    }

    /**
     * 发送短信验证码
     *
     * @param phoneNum
     */
    public int sendSms(String phoneNum) throws Exception {
        String smsCode = smsCache.newSmsCode(phoneNum);
        if (smsCode == null) {
            return -1;
        }
        boolean flag = SmsUtil.send(phoneNum, smsCode);  //短信服务接口调用
        if (flag) {
            flag = smsCache.sendSms(phoneNum, smsCode);
        }
        return flag ? 1 : 0;
    }

    /**
     * 获取短信验证码
     *
     * @param phoneNum
     */
    public String getSmsCode(String phoneNum) {
        return smsCache.getSmsCode(phoneNum);
    }

    /**
     * 添加短信发送记录
     *
     * @param phoneNumber
     */
    public void addSmsRecord(String phoneNumber) {
        smsDao.addSmsRecord(phoneNumber);
    }
}
