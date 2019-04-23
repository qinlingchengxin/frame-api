package net.ys.serialize;

import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * User: NMY
 * Date: 19-4-23
 */
public class MsgPackSerialize implements ISerialize {

    MessagePack msgPack;

    private MsgPackSerialize() {
        msgPack = new MessagePack();
    }

    static MsgPackSerialize instance = null;

    public static MsgPackSerialize getInstance() {
        if (instance == null) {
            synchronized (MsgPackSerialize.class) {
                if (instance == null) {
                    instance = new MsgPackSerialize();
                }
            }
        }
        return instance;
    }

    @Override
    public <T> byte[] serialize(T t) {
        try {
            return msgPack.write(t);
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) {
        try {
            return msgPack.read(bytes, clazz);
        } catch (IOException e) {
        }
        return null;
    }
}
