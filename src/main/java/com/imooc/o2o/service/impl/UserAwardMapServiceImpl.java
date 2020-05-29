package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.UserAwardMapDao;
import com.imooc.o2o.dao.UserShopMapDao;
import com.imooc.o2o.dto.UserAwardMapExecution;
import com.imooc.o2o.entity.UserAwardMap;
import com.imooc.o2o.entity.UserShopMap;
import com.imooc.o2o.enums.UserAwardMapStateEnum;
import com.imooc.o2o.exceptions.UserAwardMapOperationException;
import com.imooc.o2o.service.UserAwardMapService;
import com.imooc.o2o.util.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserAwardMapServiceImpl implements UserAwardMapService {
    @Autowired
    private UserAwardMapDao userAwardMapDao;
    @Autowired
    private UserShopMapDao userShopMapDao;

    @Override
    public UserAwardMapExecution listUserAwardMap(
            UserAwardMap userAwardCondition, Integer pageIndex, Integer pageSize) {
        if (userAwardCondition != null && pageIndex != null && pageSize != null) {
            int beginIndex = PageCalculator.calculateRowIndex(pageIndex,
                    pageSize);
            List<UserAwardMap> userAwardMapList = userAwardMapDao
                    .queryUserAwardMapList(userAwardCondition, beginIndex,
                            pageSize);
            int count = userAwardMapDao
                    .queryUserAwardMapCount(userAwardCondition);
            UserAwardMapExecution ue = new UserAwardMapExecution();
            ue.setUserAwardMapList(userAwardMapList);
            ue.setCount(count);
            return ue;
        } else {
            return null;
        }

    }
    @Override
    public UserAwardMap getUserAwardMapById(long userAwardMapId) {

        return userAwardMapDao.queryUserAwardMapById(userAwardMapId);
    }

    @Override
    @Transactional
    public UserAwardMapExecution addUserAwardMap(UserAwardMap userAwardMap)throws RuntimeException {
        if (userAwardMap!=null&&userAwardMap.getUser()!=null&&userAwardMap.getUser().getUserId()!=null) {
            userAwardMap.setCreateTime(new Date());
            userAwardMap.setUsedStatus(0);
            try {
                int effctedNum = 0;
                if (userAwardMap.getPoint() != null && userAwardMap.getPoint() > 0) {
                    //根据用户id和电批od获取该用户在店铺的积分
                    UserShopMap userShopMap = userShopMapDao.queryUserShopMap(userAwardMap.getUser().getUserId(), userAwardMap.getShop().getShopId());
                    if (userShopMap.getPoint() > userAwardMap.getPoint()) {
                        //积分抵扣
                        userShopMap.setPoint(userShopMap.getPoint() - userAwardMap.getPoint());
                        //更新积分信息
                        effctedNum = userShopMapDao.updateUserShopMapPoint(userShopMap);
                        if (effctedNum < 0) {
                            throw new UserAwardMapOperationException("更新积分信息失败");
                        }
                    } else {
                        throw new UserAwardMapOperationException("积分不足无法领取");
                    }
                } else {
                    //在商店没有积分
                    throw new UserAwardMapOperationException("在商铺没有积分");
                }
                effctedNum = userAwardMapDao.insertUserAwardMap(userAwardMap);
                if (effctedNum <= 0) {
                    throw new UserAwardMapOperationException("领取奖品失败");
                }
                return new UserAwardMapExecution(UserAwardMapStateEnum.SUCCESS);

            } catch (Exception e) {
                throw new UserAwardMapOperationException("领取奖品失败" + e.toString());
            }
        }else {
            return new UserAwardMapExecution(UserAwardMapStateEnum.NULL_USERAWARD_ID);

        }
    }

    @Override
    @Transactional
    public UserAwardMapExecution modifyUserAwardMap(UserAwardMap userAwardMap)
            throws RuntimeException {
        if (userAwardMap == null || userAwardMap.getUserAwardId() == null
                || userAwardMap.getUsedStatus() == null) {
            return new UserAwardMapExecution(
                    UserAwardMapStateEnum.NULL_USERAWARD_ID);
        } else {
            try {
                int effectedNum = userAwardMapDao
                        .updateUserAwardMap(userAwardMap);
                if (effectedNum <= 0) {
                    return new UserAwardMapExecution(
                            UserAwardMapStateEnum.INNER_ERROR);
                } else {
                    return new UserAwardMapExecution(
                            UserAwardMapStateEnum.SUCCESS, userAwardMap);
                }
            } catch (Exception e) {
                throw new RuntimeException("modifyUserAwardMap error: "
                        + e.getMessage());
            }
        }
    }

}
