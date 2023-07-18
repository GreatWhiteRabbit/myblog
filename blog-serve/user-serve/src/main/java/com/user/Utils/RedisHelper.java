package com.user.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisHelper {

    // Redis

    private static StringRedisTemplate stringRedisTemplate;

    //序列化和反序列化工具
    private static ObjectMapper objectMapper;



    public static void setApplicationContext(ApplicationContext applicationContext) {
        stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        objectMapper = applicationContext.getBean(ObjectMapper.class);
    }

    /**
     * 将object以string存入Redis并设置过期时间
     * @param key 传入的key值
     * @param object 传入的对象值
     * @param seconds 传入的过期时间，单位为秒,值为-1表示不过期
     * @return 插入成功返回true
     * @param <T> object的类型
     */
    public  <T> boolean setStringKey(String key, T object, int seconds) {
        // 将object序列化成json字符串
        String jsonString = ChangeToJson(object);
        if(jsonString.equals("")) {
            return false;
        }
        if(seconds == -1) {
            stringRedisTemplate.opsForValue().set(key,jsonString);
        } else {
            stringRedisTemplate.opsForValue().set(key,jsonString,seconds);
        }
        return true;
    }

    /**
     * 获取string类型的key所对应的对象的值
     * @param key 需要获取的对象的key
     * @param object 对象的类型
     * @return 返回一个对象
     * @param <T> 对象的类型
     */
    public <T> T getStringObject(String key, Class<T> object) {
        // 先获取json字符串
        String jsonValue = stringRedisTemplate.opsForValue().get(key);
        if(jsonValue == null) {
            return null;
        }
        // 将json字符串转成对应的对象
        T value = ChangeToObject(jsonValue, object);
        return value;
    }


    /**
     * 将List存入到Redis中
     * @param key key的值
     * @param objectList 对象的集合
     * @param seconds 过期时间，单位为秒，不过期为-1
     * @return 返回是否存储成功
     * @param <T> 集合的类型
     */
    public <T> boolean setLeftPushAllKey(String key, List<T> objectList, int seconds) {
        // 先将objectList转成json字符串集合
        List<String> stringList = GetListString(objectList);
        // 将json字符串集合LeftPush到Redis中
        Long aLong = stringRedisTemplate.opsForList().leftPushAll(key, stringList);
        if(aLong == null) {
            return false;
        }
        boolean success = false;
        // 设置过期时间
        if(seconds != -1) {
          success = setExpire(key,seconds);
        }
        return success;
    }

    /**
     * 将对象leftPush到list集合中
     * @param key list集合的key
     * @param object push的对象
     * @return 成功返回true
     * @param <T> object的类型
     */
    public <T> boolean setLeftPushKey(String key, T object) {
        // 先将object转换成json
        String jsonValue = ChangeToJson(object);
        // 将json存入到Redis中
        Long aLong = stringRedisTemplate.opsForList().leftPush(key, jsonValue);
        if(aLong == null) {
            return false;
        }
        return true;
    }

    /**
     * 将对象rightPush到list集合中
     * @param key list集合的key
     * @param object push的对象
     * @return 成功返回true
     * @param <T> object的类型
     */
    public <T> boolean setRightPushKey(String key, T object) {
        // 先将object转换成json
        String jsonValue = ChangeToJson(object);
        // 将json存入到Redis中
        Long aLong = stringRedisTemplate.opsForList().rightPush(key, jsonValue);
        if(aLong == null) {
            return false;
        }
        return true;
    }


    /**
     * 将List存入到Redis中
     * @param key key的值
     * @param objectList 对象的集合
     * @param seconds 过期时间，单位为秒，不过期为-1
     * @return 返回是否存储成功
     * @param <T> 集合的类型
     */
    public <T> boolean setRightPushAllKey(String key, List<T> objectList, int seconds) {
        // 先将objectList转成json字符串集合
        List<String> stringList = GetListString(objectList);
        // 将json字符串集合LeftPush到Redis中
        Long aLong = stringRedisTemplate.opsForList().rightPushAll(key, stringList);
        if(aLong == null) {
            return false;
        }
        boolean success = false;
        // 设置过期时间
        if(seconds != -1) {
            success = setExpire(key,seconds);
        }
        return success;
    }



    /**
     * 获取从start到end的对象的集合
     * @param key key
     * @param start 起始下标
     * @param end 结束下标
     * @param var 对象的类型
     * @return 返回对象的集合
     * @param <T> 对象的类型
     */
    public <T> List<T> getListRange(String key, int start, int end, Class<T> var) {
        // 获取从start到end的所有的stringList;
        List<String> stringList = stringRedisTemplate.opsForList().range(key, start, end);
        if(stringList == null) {
            return null;
        }
        // 将stringList转换成对应的objectList
        List<T> objectList = GetObjectList(stringList, var);
        return objectList;
    }


    /**
     *  获取leftPop的对象
     * @param key List的key
     * @param val 所要获取对象的类型
     * @return 返回获取到的对象
     * @param <T> 对象的类型
     */
    public <T> T getListLeftPop(String key, Class<T> val) {
        // 获取leftPop的json字符串
        String jsonValue = stringRedisTemplate.opsForList().leftPop(key);
        if(jsonValue == null) {
            return null;
        }
        // 将json字符串转换成var所对应的对象
        T object = ChangeToObject(jsonValue, val);
        return object;
    }


    /**
     *  获取rightPop的对象
     * @param key List的key
     * @param val 所要获取对象的类型
     * @return 返回获取到的对象
     * @param <T> 对象的类型
     */
    public <T> T getListRightPop(String key, Class<T> val) {
        // 获取leftPop的json字符串
        String jsonValue = stringRedisTemplate.opsForList().rightPop(key);
        if(jsonValue == null) {
            return null;
        }
        // 将json字符串转换成var所对应的对象
        T object = ChangeToObject(jsonValue, val);
        return object;
    }

    /**
     * 返回List的size
     * @param key List的key
     * @return List的size
     */
    public Long getListSize(String key) {
        Long size = (long) 0;
        if(exist(key)) {
          size = stringRedisTemplate.opsForList().size(key);
        }
        return size;
    }

    /**
     * 删除key
     * @param key key
     * @return 删除成功返回true
     */
    public boolean delete(String key) {
        if(exist(key)) {
            Boolean deleteKey = stringRedisTemplate.delete(key);
            return Boolean.TRUE.equals(deleteKey);
        }
        return false;

    }


    /**
     * 判断key是否存在
     * @param key key的
     * @return 存在返回true
     */
    public boolean exist(String key) {
        Boolean aBoolean = stringRedisTemplate.hasKey(key);
        boolean existKey = Boolean.TRUE.equals(aBoolean);
        return existKey;
    }


    /**
     * 为key设置过期时间
     * @param key 传入的key
     * @param seconds 过期时间，单位为秒，-1表示不过期
     * @return 设置成功返回true
     */
    public boolean setExpire(String key, int seconds) {
        boolean success = false;
        if(seconds != -1) {
           success = stringRedisTemplate.expire(key,seconds, TimeUnit.SECONDS);
        }
        return success;
    }

    /**
     * 获取key的过期时间，单位为秒
     * @param key key
     * @return -2表示key不存在，-1表示key不过期，非负数表示key的过期时间
     */
    public Long getExpireTime(String key) {
        Long expireTime = (long) -2;
        // 如果key不存在，返回-2
        if( !exist(key)) {
            return expireTime;
        }
         expireTime = stringRedisTemplate.getExpire(key);
        return expireTime;
    }






    /**
     * 获取objectList中从start到end的所有元素，获取所有元素start=0，end=-1
     * @param objectList 集合
     * @param start 起始下标
     * @param end 结束下标
     * @return 获取到的元素的集合
     * @param <T> 集合的类型
     */
    private <T> List<T> getFromStartToEnd(List<T> objectList, int start, int end) {
        // 获取objectList的长度
        int length = objectList.size();
        // 集合长度为0，返回空
        if(length == 0) {
            return null;
        }
        // 返回所有元素
        if(start == 0 && end == -1) {
            return objectList;
        }
        List<T> arrayList = new ArrayList<>();
        // 返回从start到length的所有元素
        if(end >= length) {
            for(int i = start; i < length; i++) {
                arrayList.add(objectList.get(i));
            }
            return arrayList;
        }
        // 返回从start到end的所有元素
        else {
            for(int i = start; i < end; i++) {
                arrayList.add(objectList.get(i));
            }
            return arrayList;
        }
    }


    /**
     * 将object集合转换成字符串集合
     * @param objectList 对象集合
     * @return 字符串集合
     * @param <T> 对象的类型
     */
    private <T> List<String> GetListString(List<T> objectList) {
        List<String> stringList = new ArrayList<>();
        for(int i = 0; i < objectList.size(); i++) {
         stringList.add(ChangeToJson(objectList.get(i)));
        }
        return stringList;
    }

    /**
     * 将json字符串集合转换成对象集合
     * @param stringList 字符串集合
     * @param val 对象的类型
     * @return 对象的集合
     * @param <T> 对象的类型
     */
    private <T> List<T> GetObjectList(List<String> stringList, Class<T> val) {
        List<T> objectList = new ArrayList<>();
        for(int i = 0; i < stringList.size(); i++) {
            objectList.add(ChangeToObject(stringList.get(i),val));
        }
        return objectList;
    }


    /**
     * 将对象序列化成json
     * @param object 需要序列化的对象
     * @return 序列化后的字符串，序列化失败返回""
     * @param <T> 序列化的对象的类型
     */
    private  <T> String ChangeToJson(T object) {
        // 序列化后的json字符串
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return jsonString;
    }

    /**
     * 反序列化json字符串
     * @param jsonString 需要反序列化的json串
     * @param var 反序列化的类型
     * @return 返回反序列化后的对象
     * @param <T> 类型
     */
    private  <T> T ChangeToObject(String jsonString, Class<T> var) {
        T object ;
        try {
            object = objectMapper.readValue(jsonString, var);
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
