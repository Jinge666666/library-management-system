package com.jing.librarymanagementsystem.shiroAndRedis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

//自定义shiro缓存管理器
public class RedisCacheMananger implements CacheManager {

    /**
     * name参数是我们设置的AuthenticationCache与AuthorizationCache 即认证或者是授权缓存的统一名称
     *
     * 不设置默认默认名称
     * cn.liuhao.springboot_shiro01.realm.CustomerRealm.authenticationCache
     * cn.liuhao.springboot_shiro01.realm.CustomerRealm.authorizationCache
     */
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException {

        System.out.println(cacheName);
        return new RedisCache<K, V>(cacheName);
    }

}

