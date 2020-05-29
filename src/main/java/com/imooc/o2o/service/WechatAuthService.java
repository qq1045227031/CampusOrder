package com.imooc.o2o.service;

import com.imooc.o2o.dto.WechatAuthExecution;
import com.imooc.o2o.entity.WechatAuth;
import com.imooc.o2o.exceptions.WechatOperationException;

public interface WechatAuthService {

    /**
     *
     * @param openId
     * @return
     */
    WechatAuth getWechatAuthByOpenId(String openId);

    WechatAuthExecution register(WechatAuth wechatAuth) throws WechatOperationException;

}
