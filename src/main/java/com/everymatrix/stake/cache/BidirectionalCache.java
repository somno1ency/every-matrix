package com.everymatrix.stake.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mackay.zhou
 * created at 2024/12/12
 */
public class BidirectionalCache<K, V> {

    // quickly find sessionKey for customerId, we need a map, which time complex is O(1)
    private final LinkedHashMap<K, CacheEntry<V>> cache = new LinkedHashMap<>();

    private final Map<K, CacheEntry<V>> synchronizedMap = Collections.synchronizedMap(cache);

    // quickly find customerId for sessionKey, we need a reversed map
    private final ConcurrentHashMap<V, CacheEntry<K>> reverseCache = new ConcurrentHashMap<>();

    private final long expirationMillis;

    public BidirectionalCache(long expirationMillis) {
        this.expirationMillis = expirationMillis;
    }

    public void put(K key, V value) {
        CacheEntry<V> entry = new CacheEntry<>(value);
        synchronizedMap.put(key, entry);

        CacheEntry<K> reverseEntry = new CacheEntry<>(key);
        reverseCache.put(value, reverseEntry);
    }

    public V getValueByKey(K key) {
        CacheEntry<V> entry = synchronizedMap.get(key);
        if (entry == null) {
            return null;
        }

        if (System.currentTimeMillis() - entry.createdTime > expirationMillis) {
            synchronizedMap.remove(key);
            reverseCache.remove(entry.value);

            return null;
        }

        return entry.value;
    }

    public K getKeyByValue(V value) {
        CacheEntry<K> entry = reverseCache.get(value);
        if (entry == null) {
            return null;
        }

        if (System.currentTimeMillis() - entry.createdTime > expirationMillis) {
            synchronizedMap.remove(entry.value);
            reverseCache.remove(value);

            return null;
        }

        return entry.value;
    }

    public void cleanExpired() {
        long currentTimestamp = System.currentTimeMillis();
        List<V> expiredSessionList = new ArrayList<>();
        Iterator<Map.Entry<K, CacheEntry<V>>> iterator = synchronizedMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, CacheEntry<V>> current = iterator.next();
            if (currentTimestamp - current.getValue().createdTime >= expirationMillis) {
                expiredSessionList.add(current.getValue().value);
                iterator.remove();
            } else {
                break;
            }
        }
        if (!expiredSessionList.isEmpty()) {
            for (V session : expiredSessionList) {
                reverseCache.remove(session);
            }
        }
    }

    static class CacheEntry<T> {

        T value;

        long createdTime;

        public CacheEntry(T value) {
            this.value = value;
            this.createdTime = System.currentTimeMillis();
        }
    }
}
