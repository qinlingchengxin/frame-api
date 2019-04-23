package net.ys.serialize;

/**
 * User: NMY
 * Date: 19-4-23
 */
public interface ISerialize {

    <T> byte[] serialize(T t);

    <T> T deSerialize(byte[] bytes, Class<T> clazz);
}
