package com.study.redis;

import com.study.constants.RedisContants;
import com.study.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.util.*;

/**
 * @author neibiaofei
 */
@Component
public class RedisClient {

    @Autowired
    private JedisPool jedisPool;

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.set(key, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.setex(key, seconds, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setNx(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.set(key, value, "NX", "EX", seconds);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String get(String key) {

        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.get(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.del(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void delAll(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            Set<String> set = jedis.keys(key + "*");
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String keyStr = it.next();
                jedis.del(keyStr);
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Set keys(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.keys(key + "*");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean lock(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String oldValue = jedis.get(key);
            if (oldValue != null && oldValue.equals(value)) {
                return false;
            }
            jedis.setex(key, seconds, value);
            return true;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean lock(String key) {
        return this.lock(key, "1", RedisContants.GOAL_FIVE_MIN_SECONDS);
    }


    public boolean unLock(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            Long result = jedis.del(key);
            if (result > 0) {
                return true;
            }
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 永久缓存任意对象
     * gaohaiming 20170809
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        this.set(key, JsonUtils.serialize(value));
    }

    /**
     * 缓存任意对象并设置过期时间
     * gaohaiming 20170809
     *
     * @param key
     * @param value
     * @param seconds
     */
    public void set(String key, Object value, int seconds) {
        this.set(key, JsonUtils.serialize(value), seconds);
    }

    /**
     * 根据key获取并转化对象类型
     * gaohaiming 20170809
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return 如果缓存为空则返回null
     */
    public <T> T get(String key, Class<T> clazz) {
        String value = this.get(key);

        return StringUtils.isEmpty(value) ? null : JsonUtils.deserialize(value, clazz);
    }

    /**
     * 缓存中是否包含key
     * gaohaiming 20170809
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.exists(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 尝试获取锁
     * <p>
     * gaohaiming 20170809
     *
     * @param key     锁的key
     * @param timeout 尝试时长,秒
     * @return
     */
    public boolean tryGetLock(String key, int timeout) {

        for (int i = 0; i < timeout; i++) {
            Jedis jedis = null;
            try {
                jedis = this.jedisPool.getResource();
                Long result = jedis.setnx(key, key);
                if (result == 1) {
                    return true;
                } else {
                    //等待
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return false;
    }

    /**
     * 防重复提交锁
     * <p>
     * gaohaiming 20170809
     *
     * @param object
     * @return
     */
    public boolean lockRepeat(Object object) {
        String key = this.EncoderByMd5(JsonUtils.serialize(object));
        return this.lock(RedisContants.REPEAT_LOCK + key);
    }

    /**
     * 防重复提交解锁
     * gaohaiming 20170809
     *
     * @param object
     */
    public void unlockRepeat(Object object) {
        String key = this.EncoderByMd5(JsonUtils.serialize(object));
        this.unLock(RedisContants.REPEAT_LOCK + key);
    }

    /**
     * md5
     * gaohaiming 20170809
     *
     * @param string
     * @return
     */
    private String EncoderByMd5(String string) {
        String encodeString = "";
        try {
            //确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            //加密后的字符串
            encodeString = base64en.encode(md5.digest(string.getBytes("utf-8")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return encodeString;
    }

    /**
     * 加入list
     * gaohaiming 20170810
     *
     * @param key
     * @param object
     */
    public void listAdd(String key, Object object) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.lpush(key, JsonUtils.serialize(object));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 加入list
     * gaohaiming 20170810
     *
     * @param key
     * @param objectList
     */
    public void listAddAll(String key, List objectList) {
        for (Object object : objectList) {
            listAdd(key, object);
        }
    }

    /**
     * 取出list中的头元素
     * gaohaiming 20170810
     *
     * @param key
     * @return
     */
    public <T> T listGetHead(String key, Class<T> T) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return JsonUtils.deserialize(jedis.lpop(key), T);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 取出list中的尾元素
     * gaohaiming 20170810
     *
     * @param key
     * @return
     */
    public <T> T listGetTail(String key, Class<T> T) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return JsonUtils.deserialize(jedis.rpop(key), T);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取list中的所有元素
     * gaohaiming 20170810
     *
     * @param key
     * @return
     */
    public <T> List<T> getList(String key, Class<T> T) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            List<String> list = jedis.lrange(key, 0, -1);
            List<T> resultList = new ArrayList<>();
            for (String value : list) {
                resultList.add(JsonUtils.deserialize(value, T));
            }
            return resultList;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 往map中加入元素键值对字符串
     * gaohaiming 20170811
     *
     * @param key
     * @param itemKey
     * @param itemValue
     */
    public void mapSetValue(String key, String itemKey, String itemValue) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.hset(key, itemKey, itemValue);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 往map中加入元素键值对对象
     *
     * @param key
     * @param itemKey
     * @param itemValue
     */
    public void mapSetValue(String key, String itemKey, Object itemValue) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.hset(key, itemKey, JsonUtils.serialize(itemValue));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 从map中根据元素key获取值
     * gaohaiming 20170811
     *
     * @param key
     * @param itemKey
     * @return
     */
    public String mapGetValue(String key, String itemKey) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.hget(key, itemKey);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 从map中根据元素key获取值,并直接转换未锁需要的对象类型
     * gaohaiming 20170811
     *
     * @param key
     * @param itemKey
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T mapGetValue(String key, String itemKey, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String value = jedis.hget(key, itemKey);
            return JsonUtils.deserialize(value, clazz);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 从map中所有的键值对
     * gaohaiming 20170811
     *
     * @param key
     * @return
     */
    public Map<String, String> mapGetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.hgetAll(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 从map中所有的键
     * gaohaiming 20170811
     *
     * @param key
     * @return
     */
    public Set<String> mapGetItemKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.hkeys(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 根据元素key删除一个或多个元素
     * gaohaiming 20170811
     *
     * @param key
     * @param itemKey
     */
    public void mapDelItems(String key, String... itemKey) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.hdel(key, itemKey);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取key的有效期(秒)
     *
     * @param key
     * @return
     */
    public long ttl(String key) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedis.ttl(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
