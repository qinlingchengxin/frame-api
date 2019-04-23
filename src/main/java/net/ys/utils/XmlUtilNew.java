package net.ys.utils;

import com.thoughtworks.xstream.XStream;

/**
 * xml转换
 * <dependency>
 * <groupId>com.thoughtworks.xstream</groupId>
 * <artifactId>xstream</artifactId>
 * <version>1.4.10</version>
 * </dependency>
 */
public class XmlUtilNew {

    public static void main(String[] args) {
        XStream xStream = new XStream();
        //xStream对象设置默认安全防护，同时设置允许的类
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypes(new Class[]{XmlBean.class});

        xStream.alias("xmlBean", XmlBean.class);//给类名起个别名  <net.ys.utils.XmlUtilNew_-XmlBean> 变成 <xmlBean>

        XmlBean xmlBean = new XmlBean(1, "hello world", System.currentTimeMillis());
        String s = xStream.toXML(xmlBean);
        System.out.println(s);
        System.out.println();
        XmlBean o = (XmlBean) xStream.fromXML(s);
        System.out.println(o);
        System.out.println(o.toJson());
    }

    static class XmlBean {

        private int id;

        private String name;

        private long createTime;

        XmlBean(int id, String name, long createTime) {
            this.id = id;
            this.name = name;
            this.createTime = createTime;
        }

        @Override
        public String toString() {
            return "XmlBean{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", createTime=" + createTime +
                    '}';
        }

        public String toJson() {
            return "{" +
                    "\"id\":" + id +
                    ",\"name\":\"" + name + '\"' +
                    ",\"createTime\":" + createTime +
                    '}';
        }
    }
}

