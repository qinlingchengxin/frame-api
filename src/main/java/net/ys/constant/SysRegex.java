package net.ys.constant;

/**
 * 系统正则
 * User: NMY
 * Date: 17-6-20
 */
public enum SysRegex {

    PASSWORD("[0-9a-zA-Z]{6,12}", "密码"),
    PHONE_NUMBER("^1(3|4|5|7|8|9)\\d{9}$", "手机号码"),
    COMMA_LIST("(\\d+,)*\\d+", "逗号隔开的数字字符串"),;

    public String regex;
    public String desc;

    private SysRegex(String regex, String desc) {
        this.regex = regex;
        this.desc = desc;
    }
}
