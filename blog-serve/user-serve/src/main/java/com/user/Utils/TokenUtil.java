package com.user.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


// 生成token
public class TokenUtil {

    private static final String token_secret = "D1igsYeJvrwUOulR38fOSL";

    private static final long expire_time = 4 * 60 * 60 * 1000;

    private static final String origin_Token = "123456"; // 原始token


    // 生成token，普通用户通过邮箱生成，管理员通过账号，密码，权限
    public  String creatToken(String id, String email,String status) {
        String token = origin_Token;
        Algorithm algorithm = Algorithm.HMAC256(token_secret);
        // 设置token的保留时间为4个小时，4个小时后必须重新登录
        Date date = new Date(System.currentTimeMillis() + expire_time);
        Map<String,Object> header = new HashMap<>();
        header.put("Type","JWT");
        header.put("alg","HS256");
        try {
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("id", id)
                    .withClaim("status",status)
                    .withClaim("email", email)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            MyInfoLog.LogOut("====   token生成失败   ====",true);
        }
        if(!token.equals(origin_Token)) {
            String tokenKey = getTokenKey(id + "", email, status + "");
            insertToken_ToRedis(tokenKey,token);
        }
        return token;
    }

    // 校验token
    public  boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(token_secret);
            JWTVerifier build = JWT.require(algorithm).build();
            DecodedJWT verify = build.verify(token);
            String email = verify.getClaim("email").asString();
            String id = verify.getClaim("id").asString();
            String status = verify.getClaim("status").asString();
           // 检验token是否还存在于redis中

            return existToken(getTokenKey(id, email, status));
        } catch (Exception e) {
            e.printStackTrace();
            MyInfoLog.LogOut("====   token解析失败   ====",true);
            return false;
        }
    }

    // 生成Redis中的token的key
    public String getTokenKey(String id, String email, String status) {
        String key = id + email +status;
        return key;
    }




    private static StringRedisTemplate stringRedisTemplate;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
    }

    // 将token插入到Redis中
    public  void insertToken_ToRedis(String tokenKey, String token) {
        stringRedisTemplate.opsForValue().set(tokenKey,token);
        // 设置30分钟的过期时间
        stringRedisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
    }

    // 从Redis中查询token是否存在
    public  boolean existToken(String tokenKey) {

        boolean exist = stringRedisTemplate.hasKey(tokenKey).booleanValue();

        // 不存在，返回false
        if(!exist) {
            return false;
        }
        // 如果存在，查看剩余过期时间
        int expire = stringRedisTemplate.getExpire(tokenKey, TimeUnit.MINUTES).intValue();
        // 如果剩余过期时间少于5分钟，更新过期时间
        if(expire <= 5) {
            stringRedisTemplate.expire(tokenKey,30,TimeUnit.MINUTES);
        }
        return true;
    }
}
