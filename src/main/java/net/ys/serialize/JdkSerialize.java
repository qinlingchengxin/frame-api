package net.ys.serialize;

import java.io.*;

/**
 * User: NMY
 * Date: 19-4-23
 */
public class JdkSerialize implements ISerialize {

    private JdkSerialize() {
    }

    public static JdkSerialize instance = null;

    public static JdkSerialize getInstance() {
        if (instance == null) {
            synchronized (JdkSerialize.class) {
                if (instance == null) {
                    instance = new JdkSerialize();
                }
            }
        }
        return instance;
    }

    @Override
    public <T> byte[] serialize(T t) {
        ByteArrayOutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(t);
            return outputStream.toByteArray();
        } catch (IOException e) {
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (Exception e) {
            }
        }
        return new byte[0];
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInputStream.readObject();
        } catch (Exception e) {
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}
