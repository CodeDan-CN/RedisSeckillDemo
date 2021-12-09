package cn.wtu.zld.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ResourceBundle;

/**
 * 获取到单例的Jedis线程池对象
 * */
@Configuration
public class RedisClient {
    //单例Redis线程池对象
    private static JedisPool jedisPool;
    static {
        //我们要从配置文件中获取配置信息
        ResourceBundle redisConfig = ResourceBundle.getBundle("redisConfig");
        String host = redisConfig.getString("spring.redis.host");
        int port = Integer.parseInt(redisConfig.getString("spring.redis.port"));
        String pw = redisConfig.getString("spring.redis.password");
        int maxTotal = Integer.parseInt(redisConfig.getString("spring.jedisPool.maxActive"));
        int maxWait = Integer.parseInt(redisConfig.getString("spring.jedisPool.maxWait"));
        int maxIdle = Integer.parseInt(redisConfig.getString("spring.jedisPool.maxIdle"));
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        jedisPoolConfig.setMinIdle(maxIdle);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPool = new JedisPool(jedisPoolConfig,host,port,6000,pw);
    }

    @Bean
    public static JedisPool getJedisPool(){
        return jedisPool;
    }
}
