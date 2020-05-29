package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.PersonInfoDao;
import com.imooc.o2o.dao.WechatAuthDao;
import com.imooc.o2o.dto.WechatAuthExecution;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.WechatAuth;
import com.imooc.o2o.enums.WechatAuthStateEnum;
import com.imooc.o2o.exceptions.WechatOperationException;
import com.imooc.o2o.service.WechatAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WechatAuthServiceImpl implements WechatAuthService {
    @Autowired
    private WechatAuthDao wechatAuthDao;
    @Autowired
    private PersonInfoDao personInfoDao;
    @Override
    public WechatAuth getWechatAuthByOpenId(String openId) {
        return wechatAuthDao.queryWechatInfoByOpenId(openId);
    }

    @Override
    public WechatAuthExecution register(WechatAuth wechatAuth) throws WechatOperationException {
      //为空或者没有openId则无法创建
        if (wechatAuth == null || wechatAuth.getOpenId() == null) {
            return new WechatAuthExecution(WechatAuthStateEnum.NULL_AUTH_INFO);
        }
        try {
            wechatAuth.setCreateTime(new Date());
            //如果微信账号夹带这用户信息并且用户id为空，则认为改用户第一次使用平台(且通过微信登陆)
            //自动注册用户信息  （所以在注册的时候WechatAuth类中personInfo没有userId但是要有其他信息）
            if (wechatAuth.getPersonInfo() != null
                    && wechatAuth.getPersonInfo().getUserId() == null) {
//                if (profileImg != null) {
//                    try {
//                        addProfileImg(wechatAuth, profileImg);
//                    } catch (Exception e) {
//                        log.debug("addUserProfileImg error:" + e.toString());
//                        throw new RuntimeException("addUserProfileImg error: "
//                                + e.getMessage());
//                    }
//                }
                try {
                    wechatAuth.getPersonInfo().setCreateTime(new Date());
//                    wechatAuth.getPersonInfo().setLastEditTime(new Date());
//                    wechatAuth.getPersonInfo().setCustomerFlag(1);
//                    wechatAuth.getPersonInfo().setShopOwnerFlag(1);
//                    wechatAuth.getPersonInfo().setAdminFlag(0);
                    wechatAuth.getPersonInfo().setEnableStatus(1);
                    //取得该用户personinfo修改
                    PersonInfo personInfo = wechatAuth.getPersonInfo();
                    //插入用户详情
                    int effectedNum = personInfoDao
                            .insertPersonInfo(personInfo);//插入成功后会返回personInfo的userId并且插入到personInfo中
//                    wechatAuth.setUserId(personInfo.getUserId());
                    wechatAuth.setPersonInfo(personInfo);//更新其插入数据库后有的UserId
                    if (effectedNum <= 0) {
                        throw new RuntimeException("库添加用户详情信息失败");
                    }
                } catch (Exception e) {
//                    log.debug("insertPersonInfo error:" + e.toString());
                    throw new WechatOperationException("创建用户信息失败"+e.getMessage());
                }
            }
            int effectedNum = wechatAuthDao.insertWechatAuth(wechatAuth);//创建微信用户
            if (effectedNum <= 0) {
                throw new WechatOperationException("帐号创建失败");
            } else {
                return new WechatAuthExecution(WechatAuthStateEnum.SUCCESS,
                        wechatAuth);
            }
        } catch (Exception e) {
//            log.debug("insertWechatAuth error:" + e.toString());
            throw new WechatOperationException("插入微信账号失败"+e.getMessage());
        }
    }
}
