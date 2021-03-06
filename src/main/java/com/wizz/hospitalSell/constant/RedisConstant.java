package com.wizz.hospitalSell.constant;

/**
 * redis有关常量
 * Created By Cx On 2018/7/30 10:15
 */
public interface RedisConstant {

    /**
     * 生成token的前缀，即生成token的格式应该是token_xxxxxxx
     */
    String TOKEN_PREFIX = "token_%s";

    /**
     * redis过期时间，2小时，单位:秒
     */
    Integer EXPIRE = 7200;

    /**
     * 记住密码选项时，redis过期时间，10天，单位:秒
     */
    Integer REMEMBER = 864000;
}
