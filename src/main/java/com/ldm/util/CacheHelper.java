package com.ldm.util;

import com.ldm.entity.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author lidongming
 * @ClassName CacheHelper.java
 * @Description TODO
 * @createTime 2020年07月19日 18:11:00
 */
@Service
public class CacheHelper {

    @Autowired
    private WxProxy wxProxy;

    @Autowired
    private JedisPool jedisPool;
    public boolean updateAccessToken(AccessToken accessToken) {
        if (accessToken==null) {
            return false;
        }
        Jedis jedis=jedisPool.getResource();
        jedis.set(RedisKeys.accessToken,accessToken.getAccess_token());
        jedis.expire(RedisKeys.accessToken,accessToken.getExpires_in());
        returnToPool(jedis);
        return true;
    }

    public String getAccessToken() throws Exception {
        Jedis jedis=jedisPool.getResource();
        String accessToken="";
        if (!jedis.exists(RedisKeys.accessToken)){// 兜底逻辑处理
            AccessToken accessTokenObject=wxProxy.getAccessToken();
            if (updateAccessToken(accessTokenObject)){
                returnToPool(jedis);
                return accessTokenObject.getAccess_token();
            }
        }else {
            accessToken=jedis.get(RedisKeys.accessToken);
        }
        returnToPool(jedis);
        return accessToken;
    }
    /**
     * @title 将redis连接对象归还到redis连接池
     * @description
     * @author lidongming
     * @updateTime 2020/4/4 16:14
     */
    public static void returnToPool(Jedis jedis) {
        if (jedis != null){
            jedis.close();
        }
    }
}