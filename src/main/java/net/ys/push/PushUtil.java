package net.ys.push;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.DefaultResult;
import cn.jpush.api.JPushClient;
import cn.jpush.api.device.TagAliasResult;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import net.ys.component.SysConfig;
import net.ys.utils.LogUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 极光推送工具
 */

public class PushUtil {

    private static JPushClient jPushClient = new JPushClient(SysConfig.pushMasterSecret, SysConfig.pushAppKey);

    /**
     * 推送给多个指定设备标识参数的用户
     *
     * @param registrationIds 设备标识
     * @param titleContent    通知内容标题
     * @param extraParam      扩展字段
     * @return
     */
    public static boolean sendToRegistrationId(String titleContent, String extraParam, String... registrationIds) throws APIConnectionException, APIRequestException {
        if (registrationIds == null || registrationIds.length == 0) {
            return true;
        }
        PushPayload pushPayload = PushUtil.buildPushObjectRegistrationIds(registrationIds, titleContent, extraParam);
        PushResult pushResult = jPushClient.sendPush(pushPayload);
        return pushResult.getResponseCode() == HttpStatus.OK.value();
    }

    /**
     * 推送给指定标签的用户
     *
     * @param tagName      设备标识
     * @param titleContent 标题内容
     * @param extraParam   扩展字段
     * @return
     */
    public static boolean sendToTag(String tagName, String titleContent, String extraParam) throws APIConnectionException, APIRequestException {
        if (StringUtils.isBlank(tagName)) {
            return true;
        }
        PushPayload pushPayload = PushUtil.buildPushObjectByTag(tagName, titleContent, extraParam);
        PushResult pushResult = jPushClient.sendPush(pushPayload);
        return pushResult.getResponseCode() == HttpStatus.OK.value();
    }

    /**
     * 发送给所有安卓用户
     *
     * @param titleContent 通知内容标题
     * @param extraParam   扩展字段
     * @return
     */
    public static boolean sendToAllAndroid(String titleContent, String extraParam) throws APIConnectionException, APIRequestException {
        PushPayload pushPayload = PushUtil.buildPushObjectAllAndroid(titleContent, extraParam);
        PushResult pushResult = jPushClient.sendPush(pushPayload);
        return pushResult.getResponseCode() == HttpStatus.OK.value();
    }

    /**
     * 发送给所有IOS用户
     *
     * @param titleContent 通知内容标题
     * @param extraParam   扩展字段
     * @return
     */
    public static boolean sendToAllIos(String titleContent, String extraParam) throws APIConnectionException, APIRequestException {
        PushPayload pushPayload = PushUtil.buildPushObjectAllIos(titleContent, extraParam);
        PushResult pushResult = jPushClient.sendPush(pushPayload);
        return pushResult.getResponseCode() == HttpStatus.OK.value();
    }

    /**
     * 发送给所有用户
     *
     * @param titleContent 通知内容标题
     * @param extraParam   扩展字段
     * @return 0推送失败，1推送成功
     */
    public static boolean sendToAll(String titleContent, String extraParam) throws APIConnectionException, APIRequestException {
        PushPayload pushPayload = PushUtil.buildPushObjectAll(titleContent, extraParam);
        PushResult pushResult = jPushClient.sendPush(pushPayload);
        return pushResult.getResponseCode() == HttpStatus.OK.value();
    }

    /**
     * 所有用户，包括Android和ios
     *
     * @param titleContent
     * @param extraParam
     * @return
     */
    public static PushPayload buildPushObjectAll(String titleContent, String extraParam) {
        return PushPayload.newBuilder().setPlatform(Platform.android_ios()).setAudience(Audience.all()).setNotification(Notification.newBuilder().setAlert(titleContent).addPlatformNotification(AndroidNotification.newBuilder().setAlert(titleContent).setTitle(titleContent).addExtra("extra_key", extraParam).build()).addPlatformNotification(IosNotification.newBuilder().setAlert(titleContent).incrBadge(1).setSound("sound.caf").addExtra("extra_key", extraParam).build()).build()).setMessage(Message.newBuilder().setMsgContent(titleContent).setTitle(titleContent).addExtra("extra_key", extraParam).build()).setOptions(Options.newBuilder().setApnsProduction(false).setSendno(1).setTimeToLive(86400).build()).build();
    }

    /**
     * 指定Tag的人
     *
     * @param tagName      标签名字
     * @param titleContent 通知标题
     * @param extraParam   附加信息
     * @return
     */
    public static PushPayload buildPushObjectByTag(String tagName, String titleContent, String extraParam) {

        PushPayload.Builder pBuilder = PushPayload.newBuilder();
        pBuilder.setPlatform(Platform.all());
        pBuilder.setAudience(Audience.tag(tagName));

        Notification.Builder nBuilder = Notification.newBuilder();
        nBuilder.setAlert(titleContent);
        nBuilder.addPlatformNotification(AndroidNotification.newBuilder().setAlert(titleContent).setTitle(titleContent).addExtra("extra_key", extraParam).build());
        nBuilder.addPlatformNotification(IosNotification.newBuilder().setAlert(titleContent).incrBadge(1).setSound("sound.caf").addExtra("extra_key", extraParam).build());

        pBuilder.setNotification(nBuilder.build());
        pBuilder.setMessage(Message.newBuilder().setMsgContent(titleContent).setTitle(titleContent).addExtra("extra_key", extraParam).build());
        pBuilder.setOptions(Options.newBuilder().setApnsProduction(false).setSendno(1).setTimeToLive(86400).build());
        PushPayload pushPayload = pBuilder.build();
        return pushPayload;
    }

    /**
     * 发送给多个指定的 registrationId
     *
     * @param registrationIds
     * @param titleContent
     * @param extraParam
     * @return
     */
    private static PushPayload buildPushObjectRegistrationIds(String[] registrationIds, String titleContent, String extraParam) {
        return PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.registrationId(registrationIds)).setNotification(Notification.newBuilder().addPlatformNotification(AndroidNotification.newBuilder().setAlert(titleContent).setTitle(titleContent).addExtra("extra_key", extraParam).build()).addPlatformNotification(IosNotification.newBuilder().setAlert(titleContent).incrBadge(1).setSound("sound.caf").addExtra("extra_key", extraParam).build()).build()).setMessage(Message.newBuilder().setMsgContent(titleContent).setTitle(titleContent).addExtra("extra_key", extraParam).build()).setOptions(Options.newBuilder().setApnsProduction(false).setSendno(1).setTimeToLive(86400).build()).build();
    }

    /**
     * 所有android用户
     *
     * @param titleContent
     * @param extraParam
     * @return
     */
    private static PushPayload buildPushObjectAllAndroid(String titleContent, String extraParam) {
        return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.all()).setNotification(Notification.newBuilder().addPlatformNotification(AndroidNotification.newBuilder().
                setAlert(titleContent).setTitle(titleContent).addExtra("extra_key", extraParam).build()).build()).setMessage(Message.newBuilder().setMsgContent(titleContent).setTitle(titleContent).addExtra("extra_key", extraParam).build()).setOptions(Options.newBuilder().setApnsProduction(false).setSendno(1).setTimeToLive(86400).build()).build();
    }

    /**
     * 所有苹果用户
     *
     * @param titleContent
     * @param extraParam
     * @return
     */
    private static PushPayload buildPushObjectAllIos(String titleContent, String extraParam) {
        return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.all()).setNotification(Notification.newBuilder().addPlatformNotification(IosNotification.newBuilder().setAlert(titleContent).incrBadge(1).setSound("sound.caf").addExtra("extra_key", extraParam).build()).build()).setMessage(Message.newBuilder().setMsgContent(titleContent).setTitle(titleContent).addExtra("extra_key", extraParam).build()).setOptions(Options.newBuilder().setApnsProduction(false).setSendno(1).setTimeToLive(86400).build()).build();
    }

    /**
     * 添加标签
     *
     * @param registrationId
     * @param tag
     */
    public static boolean addTag(String registrationId, String... tag) {
        try {
            if (StringUtils.isBlank(registrationId)) {
                return true;
            }
            Set<String> set = new HashSet<String>();
            for (int i = 0; i < tag.length; i++) {
                set.add(tag[i]);
            }
            DefaultResult result = jPushClient.updateDeviceTagAlias(registrationId, "", set, null);
            return HttpStatus.OK.value() == result.getResponseCode();
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return false;
    }

    /**
     * 移除标签
     *
     * @param registrationId
     * @param tag
     */
    public static boolean removeTag(String registrationId, String... tag) {
        try {
            if (StringUtils.isBlank(registrationId)) {
                return true;
            }
            Set<String> set = new HashSet<String>();
            for (int i = 0; i < tag.length; i++) {
                set.add(tag[i]);
            }
            DefaultResult result = jPushClient.updateDeviceTagAlias(registrationId, "", null, set);
            return HttpStatus.OK.value() == result.getResponseCode();
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return false;
    }

    /**
     * 获取标签
     *
     * @param registrationId
     */
    public static List<String> getTags(String registrationId) {
        try {
            TagAliasResult result = jPushClient.getDeviceTagAlias(registrationId);
            return result.tags;
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return new ArrayList<String>();
    }

    public static void main(String[] args) throws APIConnectionException, APIRequestException {
        sendToTag("tag_", "msg_content", "");
        sendToRegistrationId("titleContent", "{\"code\":\"1000\"}", "120c83f76003773eb00");
    }
}