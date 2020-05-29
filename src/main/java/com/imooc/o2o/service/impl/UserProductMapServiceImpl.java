package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.PersonInfoDao;
import com.imooc.o2o.dao.ShopDao;
import com.imooc.o2o.dao.UserProductMapDao;
import com.imooc.o2o.dao.UserShopMapDao;
import com.imooc.o2o.dto.Person;
import com.imooc.o2o.dto.UserProductMapExecution;
import com.imooc.o2o.entity.*;
import com.imooc.o2o.service.UserProductMapService;
import com.imooc.o2o.util.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserProductMapServiceImpl implements UserProductMapService {
    @Autowired
    private UserProductMapDao userProductMapDao;
    @Autowired
    private UserShopMapDao userShopMapDao;
    @Autowired
    private PersonInfoDao personInfoDao;
    @Autowired
    private ShopDao shopDao;

    @Override
    public UserProductMapExecution listUserProductMap(
            UserProductMap userProductCondition, Integer pageIndex,
            Integer pageSize) {
        if (userProductCondition != null && pageIndex != null
                && pageSize != null) {
            int beginIndex = PageCalculator.calculateRowIndex(pageIndex,
                    pageSize);
            List<UserProductMap> userProductMapList = userProductMapDao
                    .queryUserProductMapList(userProductCondition, beginIndex,
                            pageSize);
            int count = userProductMapDao
                    .queryUserProductMapCount(userProductCondition);
            UserProductMapExecution se = new UserProductMapExecution();
            se.setUserProductMapList(userProductMapList);
            se.setCount(count);
            return se;
        } else {
            return null;
        }

    }

//用户消费后添加积分
    @Override
    @Transactional
    public UserProductMapExecution addUserProductMap(
            UserProductMap userProductMap) throws RuntimeException {
        if (userProductMap != null && userProductMap.getUser().getUserId() != null
                && userProductMap.getShop().getShopId() != null&&userProductMap.getOperator()!=null) {
            userProductMap.setCreateTime(new Date());
            try {
                //添加巨龙
                int effectedNum = userProductMapDao
                        .insertUserProductMap(userProductMap);
                if (effectedNum <= 0) {
                    throw new RuntimeException("添加消费记录失败");
                }
//                if (userProductMap.getPoint() != null
//                        && userProductMap.getPoint() > 0) {
//                    //查询该顾客是否在该店铺消费国
//                    UserShopMap userShopMap = userShopMapDao.queryUserShopMap(
//                            userProductMap.getUser().getUserId(),
//                            userProductMap.getShop().getShopId());
//                    if (userShopMap != null) {
//                        if (userShopMap.getPoint() >= userProductMap.getPoint()) {
//                            userShopMap.setPoint(userShopMap.getPoint()
//                                    + userProductMap.getPoint());
//                            effectedNum = userShopMapDao
//                                    .updateUserShopMapPoint(userShopMap);
//                            if (effectedNum <= 0) {
//                                throw new RuntimeException("更新积分信息失败");
//                            }
//                        }
//
//                    } else {
//                        // 在店铺没有过消费记录，添加一条积分信息
//                        userShopMap = compactUserShopMap4Add(
//                                userProductMap.getUser().getUserId(),
//                                userProductMap.getShop().getShopId(),
//                                userProductMap.getPoint());
//                        effectedNum = userShopMapDao
//                                .insertUserShopMap(userShopMap);
//                        if (effectedNum <= 0) {
//                            throw new RuntimeException("积分信息创建失败");
//                        }
//                    }
//                }
                return new UserProductMapExecution(
                        UserProductMapStateEnum.SUCCESS, userProductMap);
            } catch (Exception e) {
                throw new RuntimeException("添加授权失败:" + e.toString());
            }
        } else {
            return new UserProductMapExecution(
                    UserProductMapStateEnum.NULL_USERPRODUCT_INFO);
        }
    }
//要求传入一个具有完成信息的订单
    @Override
    public UserProductMapExecution overUserProductMap(long userProductId,long shopId) {
        UserProductMap userProductMap = userProductMapDao.queryUserProductMapById(userProductId);
        if (userProductMap==null){
            return new UserProductMapExecution(UserProductMapStateEnum.NULL_ID);
        }
        if (userProductMap.getShop().getShopId()!=shopId){
            return new UserProductMapExecution(UserProductMapStateEnum.ERROR_SHOP);
        }
        if (userProductMap.getOperator().getUserId()==2){
            return new UserProductMapExecution(UserProductMapStateEnum.OVERED);
        }
        PersonInfo personInfo = new PersonInfo();
        personInfo.setUserId(2l);
        userProductMap.setOperator(personInfo);//完成订单
        int eff = userProductMapDao.updateUserProductMap(userProductMap);
        //添加积分

        if (eff>0){
            if (userProductMap.getPoint() != null
                    && userProductMap.getPoint() > 0) {
                //查询该顾客是否在该店铺消费国
                UserShopMap userShopMap = userShopMapDao.queryUserShopMap(
                        userProductMap.getUser().getUserId(),
                        userProductMap.getShop().getShopId());
                if (userShopMap != null) {
                    if (userShopMap.getPoint()>=0) {
                        userShopMap.setPoint(userShopMap.getPoint()
                                + userProductMap.getPoint());
                        int effectedNum = userShopMapDao
                                .updateUserShopMapPoint(userShopMap);
                        if (effectedNum <= 0) {
                            return new UserProductMapExecution(UserProductMapStateEnum.ADDPOINTFALSE);
                        }
                    }

                } else {
                    // 在店铺没有过消费记录，添加一条积分信息
                    userShopMap = compactUserShopMap4Add(
                            userProductMap.getUser().getUserId(),
                            userProductMap.getShop().getShopId(),
                            userProductMap.getPoint());
                     int effectedNum = userShopMapDao
                            .insertUserShopMap(userShopMap);
                    if (effectedNum <= 0) {
                        return new UserProductMapExecution(UserProductMapStateEnum.CREATEPORINTFALSE);
                    }
                }
            }
            return new UserProductMapExecution(UserProductMapStateEnum.SUCCESS);
        }
        return new UserProductMapExecution(UserProductMapStateEnum.INNER_ERROR);
    }

    private UserShopMap compactUserShopMap4Add(Long userId, Long shopId,
                Integer point) {
            UserShopMap userShopMap = null;
            if (userId != null && shopId != null) {
                userShopMap = new UserShopMap();
                PersonInfo personInfo = new PersonInfo();
                personInfo.setUserId(userId);
                Shop shop = new Shop();
                shop.setShopId(shopId);
                userShopMap.setUser(personInfo);
                userShopMap.setShop(shop);
                userShopMap.setCreateTime(new Date());
                userShopMap.setPoint(point);
            }
            return userShopMap;
        }
}
