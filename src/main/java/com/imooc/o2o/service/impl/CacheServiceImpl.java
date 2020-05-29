package com.imooc.o2o.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.o2o.cache.JedisUtil;
import com.imooc.o2o.service.CacheService;
@Service
public class CacheServiceImpl implements CacheService{
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Override
    public void removeFromCache(String keyPrefix) {
        //jedisKeys.keys根据传入信息取出相应key
        Set<String> keySet = jedisKeys.keys(keyPrefix + "*");//取出以keyPrefix打头的所有key
        for (String key : keySet) {
            jedisKeys.del(key);//删除所有有关这个实体类的key
        }
    }
}