package com.ldm.rabbitmq;

import com.ldm.pojo.FollowOrNot;
import com.ldm.request.PublishDynamic;
import com.ldm.util.CacheHelper;
import com.ldm.util.JsonUtil;
import com.ldm.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.Set;

/**
 * 处理feed流
 */
@Slf4j
@Service
public class MQReceiver {

    @Autowired
    private JedisPool jedisPool;

    /**
     * 动态发布处理队列发现新消息时，取队首消息出队列。
     * 根据消息中的发布者UID，遍历其粉丝列表(当以后全站粉丝量较大时，可扩展为选择性推送)。
     * 给每个粉丝推送一条动态，将动态ID和时间戳写入粉丝的收Feed有序集中。
     * 消息处理完成，检查队列是否还有消息，无则阻塞。
     * @param message
     */
    @RabbitListener(queues = MQConfig.Feed_Dynamic_Publish_QUEUE)
    public void feedDynamicPublish(String message){
        Jedis jedis=jedisPool.getResource();
        PublishDynamic request=JsonUtil.stringToBean(message,PublishDynamic.class);
        log.info("RabbitMQ消费了一条动态ID为: {} 的消息",request.getDynamicId());
        int userId=request.getUserId();
        Set<String> set=jedis.smembers(RedisKeys.followMe(userId));
        // 每个用户都有一个发feed收件箱
        jedis.sadd(RedisKeys.dynamicFeedSend(request.getUserId()),String.valueOf(request.getDynamicId()));
        // 如果发布者是大V,则结束
        if (jedis.sismember(RedisKeys.bigV(),""+request.getUserId())){
            CacheHelper.returnToPool(jedis);
            return;
        }
        // 给每个粉丝推送一条动态
        // 使用管道进行批量添加，减少网络耗时
        Pipeline pipeline=jedis.pipelined();
        for (String string:set){
            jedis.sadd(RedisKeys.dynamicFeedReceive(Integer.valueOf(string)),String.valueOf(request.getDynamicId()));
        }
        pipeline.sync();
        CacheHelper.returnToPool(jedis);
    }

    /**
     * 关注取关处理队列发现新消息时，取队首消息出队列。
     * 根据动作标识判断是关注还是取关操作。
     * 如果是关注，拉取关注者的发Feed有序集中的动态，将最近的动态ID写入用户自己的收Feed中。
     * 如果是取关，遍历用户自己的收Feed，剔除其中取关UID的动态记录。
     * 消息处理完成，检查队列是否还有消息，无则阻塞。
     * @param message
     */
    @RabbitListener(queues = MQConfig.Feed_Follow_QUEUE)
    public void feedFollow(String message) {
        Jedis jedis=jedisPool.getResource();
        FollowOrNot followOrNot = JsonUtil.stringToBean(message, FollowOrNot.class);
        // 关注
        if (followOrNot.isFlag()) {
            log.info("RabbitMQ消费了一条用户 {} 关注用户 {} 的消息", followOrNot.getUserId(), followOrNot.getToUserId());
            // 将被关注者的发feed存到关注者的收feed
            Set<String> set = jedis.smembers(RedisKeys.dynamicFeedSend(followOrNot.getToUserId()));
            // 使用管道进行批量添加
            Pipeline pipeline=jedis.pipelined();
            for (String str:set){
                jedis.sadd(RedisKeys.dynamicFeedReceive(followOrNot.getUserId()),str);
            }
            pipeline.sync();

        } else {// 取消关注
            log.info("RabbitMQ消费了一条用户 {} 取消关注用户 {} 的消息", followOrNot.getUserId(), followOrNot.getToUserId());
            // 将被关注者的发feed从关注者的收feed中删除
            Set<String> set = jedis.smembers(RedisKeys.dynamicFeedSend(followOrNot.getToUserId()));
            // 使用管道进行批量删除
            Pipeline pipeline=jedis.pipelined();
            for (String str:set){
                jedis.srem(RedisKeys.dynamicFeedReceive(followOrNot.getUserId()),str);
            }
            pipeline.sync();
        }
        CacheHelper.returnToPool(jedis);
    }
}
