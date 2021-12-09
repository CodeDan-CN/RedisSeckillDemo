package cn.wtu.zld.controller;


import cn.wtu.zld.services.ConcurrencyService;
import cn.wtu.zld.services.impi.ConcurrencyLUAServiceImpI;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class ConcurrencyController {

    //这里负责切换不同的秒杀实现，默认是Redis乐观锁实现，如果想要悲观锁实现，那么就把注释去掉，并下把下面的注释即可
//    @Resource(name = "ConcurrencyLUAServiceImpI")
    @Resource(name = "ConcurrencyServiceImpI")
    private ConcurrencyService concurrencyService;

    //首页请求
    @RequestMapping("/")
    public String getMainPage(){
        return "index";
    }

     //统计并发请求次数使用，由于并发量大，所以采用原子性操作类来统计
//   private LongAdder longAdder =  new LongAdder();

    //测试秒杀请求
    @RequestMapping("/bug")
    @ResponseBody
    public String bugCommodify(String commodityId){
        //统计代码，调试的时候可以把注解去掉
//        longAdder.add(1);
//        System.out.println("这是第"+longAdder+"次请求");
        boolean commotidy = concurrencyService.bugCommotidy(commodityId);
        return commotidy+"";
    }
}
