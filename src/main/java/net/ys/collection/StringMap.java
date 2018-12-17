package net.ys.collection;

import java.util.HashMap;

/**
 * User: NMY
 * Date: 18-12-17
 */
public class StringMap<K, V> extends HashMap<K, V> {
    @Override
    public V put(K key, V value) {
        return super.put(key, value == null ? (V) "" : value);
    }
}
