package cn.wtu.zld.services.impi;

import cn.wtu.zld.services.ConcurrencyService;
import cn.wtu.zld.utils.LUAConfig;
import cn.wtu.zld.utils.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Random;

/**
 * 基于LUA实现的Redis悲观锁业务逻辑类
 * @author CodDean
 * */
@Service("ConcurrencyLUAServiceImpI")
public class ConcurrencyLUAServiceImpI implements ConcurrencyService {
    //通过IOC获取到redis连接池对象，我们通过连接池对象获取与redis连接的线程
    @Autowired
    private JedisPool jedisPool;

    @Override
    public boolean bugCommotidy(String comId) {
        //获取连接线程
        Jedis jedis = jedisPool.getResource();
        //使用jedis提供的scripLoad方法可以执行String类型的LUA脚本语句
        String scriptLoad = jedis.scriptLoad(LUAConfig.getLua());
        //随机获取一个用户ID
        String userId = new Random().nextInt(5000)+"";
        //执行LUA脚本，并获取结果
        Object evalsha = jedis.evalsha(scriptLoad, 2, userId, comId);
        //转化结果格式为Stirng类型
        String result = String.valueOf(evalsha);

        if("0".equals(result)){
            System.out.println("秒杀结束了，很遗憾，您没有抢到");
            return false;
        }else if("2".equals(result)){
            System.out.println("您已经成功秒杀到此商品，请勿重复秒杀");
            return false;
        }else if("1".equals(result)){
            System.out.println("恭喜您，成功秒杀到此商品");
            return true;
        }else{
            System.out.println("抢购异常");
            return false;
        }
    }
}
