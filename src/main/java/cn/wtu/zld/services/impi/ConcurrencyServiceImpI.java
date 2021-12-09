package cn.wtu.zld.services.impi;

import cn.wtu.zld.services.ConcurrencyService;
import cn.wtu.zld.utils.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.LongAdder;

/**
 * 基于Watch实现的Redis乐观锁业务逻辑类
 * @author CodDean
 * */
@Service("ConcurrencyServiceImpI")
public class ConcurrencyServiceImpI implements ConcurrencyService {
    //计算并发请求失败次数
    private LongAdder longAdder = new LongAdder();

    //通过IOC获取到redis连接池对象，我们通过连接池对象获取与redis连接的线程
    @Autowired
    private JedisPool jedisPool;

    @Override
    public boolean bugCommotidy(String comId) {
        //首先我们来创建一个随机的用户ID
        String userId = new Random().nextInt(5000)+"";
        //拼接用户key，用于用户list类型的key
        String userKey = "Concurrency:user:"+comId;
        //拼接商品key，用于商品库存的key
        String comKey = "Concurrency:iphone:"+comId;
        //获取jedis连接线程
        Jedis jedis = jedisPool.getResource();
        //自旋解决乐观锁库存遗留问题
        while (true){
            //Redis乐观锁检测
            jedis.watch(comKey);
            //开始条件判断
            String s = jedis.get(comKey);
            //如果comKey返回null，说明后台还没有给库存设置key，代表秒杀还没开始
            if(s == null){
                System.out.println("秒杀还没开始");
                jedis.close();
                return false;
            }
            //如果在userKey的集合中检查到你用户id存在，说明你已经秒杀成功过了。
            if(jedis.sismember(userKey,userId)){
                System.out.println("您已购买成功，请勿重复购买秒杀商品");
                return false;
            }
            //当库存数量小于等于0，说明商品已经被秒杀空了，小于0是预防措施，哪怕并发失败了程序不能崩
            int number = Integer.parseInt(s);
            if(number <= 0 ){
                System.out.println("秒杀结束了");
                jedis.close();
                return false;
            }
            //执行到这里说明，检查到库存还有执行事务操作(库存减一，把用户加入到秒杀成功集合中)
            Transaction multi = jedis.multi();
                //减少库存
            multi.decr(comKey);
                //把用户加入到秒杀成功集合中
            multi.sadd(userKey,userId);
                //提交事务
            List<Object> exec = multi.exec();
            System.out.println("进入秒杀");
            //如果满足下述条件说明提交事务时，由于comKey被其他线程修改而且被watch检查到了数据版本不一致，取消事务提交。
            if(exec == null ||exec.size() == 0){
                longAdder.add(1);
                System.out.println("秒杀失败"+longAdder+"次");
            }else{
                //反之，watch检查到数据和获取时没有版本变化，说明没有线程修改，事务成功提交并执行
                System.out.println("秒杀成功");
                jedis.close();
                return true;
            }
        }
    }
}
