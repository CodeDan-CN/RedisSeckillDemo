package cn.wtu.zld.services;

public interface ConcurrencyService {
    /**
     * 秒杀业务逻辑
     * @param comId 商品ID
     * @return boolean
     * */
    public boolean bugCommotidy(String comId);
}
