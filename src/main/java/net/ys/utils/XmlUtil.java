package net.ys.utils;

import net.ys.bean.Person;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

@SuppressWarnings("unchecked")
public class XmlUtil {

    static JAXBContext jaxbContext;
    static Marshaller marshaller;
    static Unmarshaller unmarshaller;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Person.class);
            unmarshaller = jaxbContext.createUnmarshaller();
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);//格式化输出
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "GBK");
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出XML字符串
     */
    public static String toString(Object object) throws JAXBException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(object, bos);
        return bos.toString();
    }

    /**
     * 输出到文件
     */
    public static void toFile(Object object, File file) throws JAXBException, IOException {
        FileOutputStream fos = new FileOutputStream(file);
        marshaller.marshal(object, fos);
        fos.close();
    }

    /**
     * 解析bean
     */
    public static <T> T toBean(String xml) throws JAXBException {
        return (T) unmarshaller.unmarshal(new StringReader(xml));
    }

    /**
     * 解析bean
     */
    public static <T> T toBean(File file) throws JAXBException, IOException {
        String xml = FileUtils.readFileToString(file);
        return (T) unmarshaller.unmarshal(new StringReader(xml));
    }
}