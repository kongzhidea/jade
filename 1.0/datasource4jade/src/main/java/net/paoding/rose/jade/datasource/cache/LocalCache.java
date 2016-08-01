package net.paoding.rose.jade.datasource.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 此类 适用于频繁使用，且数据量较少情况的场景
 * <p/>
 * 未开通守护线程来清理过期数据，只在Get的时候判断是否过期
 */
public class LocalCache {
    // 默认的缓存容量
    private static int DEFAULT_CAPACITY = 128;


    // 使用默认容量创建一个Map
    private ConcurrentHashMap<String, CacheEntity> cache = new ConcurrentHashMap<String, CacheEntity>(DEFAULT_CAPACITY);

    /**
     * 将key-value 保存到本地缓存并制定该缓存的过期时间
     *
     * @param key
     * @param value
     * @param expireTime 过期时间，精确到秒，如果是-1 则表示永不过期（不推荐设置为-1）
     * @return
     */
    public void putValue(String key, Object value, int expireTime) {
        CacheEntity entityClone = new CacheEntity(value, System.currentTimeMillis(), expireTime);
        cache.put(key, entityClone);
    }

    /**
     * 从本地缓存中获取key对应的值，如果该值不存则则返回null
     *
     * @param key
     * @return
     */
    public Object getValue(String key) {
        CacheEntity cacheEntity = cache.get(key);
        if (cacheEntity == null) {
            return null;
        }

        if (checkTimeExpire(key, cacheEntity)) {
            return null;
        }
        return cache.get(key).getValue();

    }

    /**
     * 清空所有
     */
    public void clear() {
        cache.clear();
    }

    /**
     * 过期缓存的具体处理方法， true 过期， false 未过期
     *
     * @throws Exception
     */
    private boolean checkTimeExpire(String key, CacheEntity cacheEntity) {
        int expire = cacheEntity.getExpire();
        if (expire == -1) {
            return false;
        }

        int live = (int) ((System.currentTimeMillis() - cacheEntity.getGmtModify()) / 1000);

        if (live > expire) {
            cache.remove(key);
            return true;
        } else {
            return false;
        }

    }



}