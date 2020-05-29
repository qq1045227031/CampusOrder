package com.imooc.o2o.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.cache.JedisUtil;
import com.imooc.o2o.dao.AreaDao;
import com.imooc.o2o.dao.ShopDao;
import com.imooc.o2o.dto.AreaExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.AreaStateEnum;
import com.imooc.o2o.exceptions.AreaOperationException;
import com.imooc.o2o.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class AreaServiceImpl implements AreaService {
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private ShopDao shopDao;
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;
    private static Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);
    @Override
    @Transactional
    public List<Area> getAreaList() {
        // key
//       接口定义了  public static final String AREALISTKEY="arealist";
        String key = AREALISTKEY;
        List<Area> areaList = null;
        ObjectMapper mapper = new ObjectMapper();
        // 如果Redis中未存在key
        if (!jedisKeys.exists(key)) {
            // 数据库中获取区域列表
            areaList = areaDao.queryArea();
            String jsonString = null;
            // 将list转换为String
            try {
                jsonString = mapper.writeValueAsString(areaList);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.getMessage());
            }
            jedisStrings.set(key, jsonString);
        }
        // Redis中存在key，则取出
        else {
            // 将String转换为List
            String jsonString = jedisStrings.get(key);
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Area.class);
            try {
                areaList=mapper.readValue(jsonString, javaType);
            } catch (JsonParseException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.getMessage());
            } catch (JsonMappingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.getMessage());
            }
        }

        return areaList;
    }

    @Override
    @Transactional
    public AreaExecution addArea(Area area) {
        if (area.getAreaName() != null && !"".equals(area.getAreaName())) {
            area.setCreateTime(new Date());
            area.setLastEditTime(new Date());
            try {
                int effectedNum = areaDao.insertArea(area);
                if (effectedNum > 0) {
                    String key = AREALISTKEY;
                    if (jedisKeys.exists(key)) {
                        jedisKeys.del(key);
                    }
                    return new AreaExecution(AreaStateEnum.SUCCESS, area);
                } else {
                    return new AreaExecution(AreaStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("添加区域信息失败:" + e.toString());
            }
        } else {
            return new AreaExecution(AreaStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public AreaExecution modifyArea(Area area) {
        if (area.getAreaId() != null && area.getAreaId() > 0) {
            area.setLastEditTime(new Date());
            try {
                int effectedNum = areaDao.updateArea(area);
                if (effectedNum > 0) {
                    String key = AREALISTKEY;
                    if (jedisKeys.exists(key)) {
                        jedisKeys.del(key);
                    }
                    return new AreaExecution(AreaStateEnum.SUCCESS, area);
                } else {
                    return new AreaExecution(AreaStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("更新区域信息失败:" + e.toString());
            }
        } else {
            return new AreaExecution(AreaStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public AreaExecution removeArea(long areaId) {
        if (areaId > 0) {
            try {
//                Shop shop = new Shop();
//                Area area = new Area();
//                area.setAreaId(areaId);
//                shop.setArea(area);
//                List<Shop>shopList = shopDao.queryShopList(shop,0,1000);//查出所有以他为Id的shopDao
//                int effectNum = 1;
//                for (Shop s:shopList){
//                    area.setAreaId(-1l);
//                    s.setArea(area);
//                    s.setEnableStatus(0);//置空之后所以其他为区域的可用状态为不可用
//                    effectNum  = shopDao.updateShop(s);
//                    if (effectNum==0){
//                        return new AreaExecution(AreaStateEnum.SHOPERROR);
//                    }
//
//                }
                int effectedNum = areaDao.deleteArea(areaId);
                if (effectedNum > 0) {
                    String key = AREALISTKEY;
                    if (jedisKeys.exists(key)) {
                        jedisKeys.del(key);
                    }
                    return new AreaExecution(AreaStateEnum.SUCCESS);
                } else {
                    return new AreaExecution(AreaStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("删除区域信息失败:" + e.toString());
            }
        } else {
            return new AreaExecution(AreaStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public AreaExecution removeAreaList(List<Long> areaIdList) {
        if (areaIdList != null && areaIdList.size() > 0) {
            try {
                int effectedNum = areaDao.batchDeleteArea(areaIdList);
                if (effectedNum > 0) {
                    String key = AREALISTKEY;
                    if (jedisKeys.exists(key)) {
                        jedisKeys.del(key);
                    }
                    return new AreaExecution(AreaStateEnum.SUCCESS);
                } else {
                    return new AreaExecution(AreaStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("删除区域信息失败:" + e.toString());
            }
        } else {
            return new AreaExecution(AreaStateEnum.EMPTY);
        }
    }
}
