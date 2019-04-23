package net.ys.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * User: NMY
 * Date: 19-4-23
 */
public class KyRoSerialize implements ISerialize {

    Kryo kryo;

    private KyRoSerialize() {
        kryo = new Kryo();
    }

    public static KyRoSerialize instance = null;

    public static KyRoSerialize getInstance() {
        if (instance == null) {
            synchronized (KyRoSerialize.class) {
                if (instance == null) {
                    instance = new KyRoSerialize();
                }
            }
        }
        return instance;
    }

    @Override
    public <T> byte[] serialize(T t) {
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            Output output = new Output(outputStream);
            kryo.writeObject(output, t);
            output.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = null;

        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
            input.close();
            return kryo.readObject(input, clazz);
        } catch (Exception e) {
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}
