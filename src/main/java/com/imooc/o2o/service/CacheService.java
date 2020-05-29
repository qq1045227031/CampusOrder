package com.imooc.o2o.service;

public interface CacheService {
    /**
     * 依据key前缀匹配原则删除缓存数据  注意如果传入 shopcategory 还会删除 shopcategory_parentid1 以及shopcategory_allfirstlevel等所有有关shopcategory的key
     *
     * @param keyPrefix
     */
    void removeFromCache(String keyPrefix);
}
