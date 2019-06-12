package net.ys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.ys.constant.GenResult;
import net.ys.service.SmsService;
import net.ys.util.LogUtil;
import net.ys.util.Tools;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping(value = "api/sms", produces = {"application/json;charset=utf-8"})
@Api(value = "sms", description = "短信接口")
public class SmsController {

    @Resource
    private SmsService smsService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    @ApiOperation(httpMethod = "POST", response = Map.class, responseContainer = "Map", value = "发送短信")
    public Map<String, Object> sendSms(@ApiParam(value = "手机号码", required = true) @RequestParam(value = "phone_num", required = true) String phoneNum) {
        try {
            if (!Tools.validatePhoneNumber(phoneNum)) {
                return GenResult.INVALID_PHONE.genResult();
            }

            if (!smsService.validateSms(phoneNum)) {
                return GenResult.SMS_EXCEEDS_LIMIT.genResult();
            }

            int code = smsService.sendSms(phoneNum);

            if (code == -1) {
                return GenResult.SMS_SEND_FREQUENTLY.genResult();
            }

            if (code == 0) {
                return GenResult.SMS_SEND_ERROR.genResult();
            }

            smsService.addSmsRecord(phoneNum);
            String smsCode = smsService.getSmsCode(phoneNum);//测试用
            return GenResult.SUCCESS_SEND.genResult(smsCode);

        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }

    @ResponseBody
    @RequestMapping(value = "check", method = RequestMethod.GET, headers = "Accept=application/json")
    @ApiOperation(httpMethod = "GET", response = Map.class, responseContainer = "Map", value = "校验验证码")
    public Map<String, Object> checkSmsCode(@ApiParam(value = "手机号码", required = true) @RequestParam(value = "phone_num", required = true) String phoneNum,
                                            @ApiParam(value = "手机验证码", required = true) @RequestParam(value = "sms_code", defaultValue = "", required = true) String smsCode) {
        try {
            String code = smsService.getSmsCode(phoneNum);
            if (StringUtils.equals(code, smsCode)) {
                return GenResult.SUCCESS.genResult();
            }
            return GenResult.FAILED.genResult();
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }
}
