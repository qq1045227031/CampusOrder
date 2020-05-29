package com.imooc.o2o.service.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.cache.JedisUtil;
import com.imooc.o2o.dao.ShopCategoryDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopCategoryExecution;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopCategoryStateEnum;
import com.imooc.o2o.exceptions.ShopCategoryOperationException;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class ShopCategoryServiceImpl implements ShopCategoryService {
    @Autowired
    ShopCategoryDao shopCategoryDao;
    @Autowired
    private JedisUtil.Strings jedisStrings;
    @Autowired
    private JedisUtil.Keys jedisKeys;
    private static Logger logger = LoggerFactory.getLogger(ShopCategoryServiceImpl.class);

    @Override
    public List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition) {
        //若传入空则选出所有，若不为空则 查出所有同父类同级商铺种类

        // 定义Redis的key前缀
        String key = SCLISTKEY;
        // 定义接收对象
        List<ShopCategory> shopCategories = null;
        // 定义jackson数据转换操作类
        ObjectMapper mapper = new ObjectMapper();
        // 拼接出redis的key
        //  接口已定义key为 SCLISTKEY="shopcategorylist";
        if (shopCategoryCondition == null) {
            // 若查询条件为空，则列出所有首页大类，即parentId为空的店铺类型
            key = key + "_allfirstlevel";
        } else if (shopCategoryCondition != null && shopCategoryCondition.getParent() != null
                && shopCategoryCondition.getParent().getShopCategoryId() != null&& (shopCategoryCondition.getParent().getShopCategoryId()!=-1l&& (shopCategoryCondition.getParent().getShopCategoryId()!= 0L))) {
            // 若parentId不为空，则列出该parentId下的所有子类别 如果parentId为1 那么key就是 shopcategorylist_parent1   2就是shopcategorylist_parent2 来区分
            key = key + "_parent" + shopCategoryCondition.getParent().getShopCategoryId();
        } else if ((shopCategoryCondition != null) && (shopCategoryCondition.getParent().getShopCategoryId()==-1l)){
            // 列出所有子类别，不管其属于哪个类都列出
            key = key + "_allsecondlevel";
        }else if ((shopCategoryCondition != null) && (shopCategoryCondition.getParent().getShopCategoryId()== 0L)){
            key = key + "_all";
        }

        // 判断key是否存在
        if (!jedisKeys.exists(key)) {
            // 若不存在，则从数据库中取出数据
            shopCategories = shopCategoryDao.queryShopCategory(shopCategoryCondition);
            // 将实体类集合转换为string，存入redis
            String jsonString = null;
            try {
                jsonString = mapper.writeValueAsString(shopCategories);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new ShopCategoryOperationException(e.getMessage());
            }
            jedisStrings.set(key, jsonString);//设置到redis中
        } else {
            // 若存在，则直接从redis中取出数据
            String jsonString = jedisStrings.get(key);
            // 将String转换为集合类型
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, ShopCategory.class);
            try {
                shopCategories = mapper.readValue(jsonString, javaType);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new ShopCategoryOperationException(e.getMessage());
            }
        }

        return shopCategories;
    }

    @Override
    public ShopCategory getShopCategoryById(long shopCategoryId) {
        return shopCategoryDao.getShopCategoryById(shopCategoryId);
    }

    @Override
    public List<ShopCategory> getFirstLevelShopCategoryList() throws IOException {
        String key = SCLISTKEY;
        key = key + "_allfirstlevel";
        List<ShopCategory> shopCategories;
        ObjectMapper mapper = new ObjectMapper();
        if (!jedisKeys.exists(key)) {
            // 若不存在，则从数据库中取出数据
//            ShopCategory shopCategoryCondition = new ShopCategory();
//            shopCategoryCondition.setParent(null);
            shopCategories = shopCategoryDao.queryShopCategory(null);
            // 将实体类集合转换为string，存入redis
            String jsonString = null;
            try {
                jsonString = mapper.writeValueAsString(shopCategories);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new ShopCategoryOperationException(e.getMessage());
            }
            jedisStrings.set(key, jsonString);//设置到redis中
        } else {
            // 若存在，则直接从redis中取出数据
            String jsonString = jedisStrings.get(key);
            // 将String转换为集合类型
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, ShopCategory.class);
            try {
                shopCategories = mapper.readValue(jsonString, javaType);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new ShopCategoryOperationException(e.getMessage());
            }
        }
        return  shopCategories;
    }


    @Override
    public List<ShopCategory> getAllSecondLevelShopCategory() throws IOException {
        //如果parent的shopcategoryId为-1则代表查所有二级类名
        String key = SCLISTKEY + "ALLSECOND";
        List<ShopCategory> shopCategoryList = null;
        ObjectMapper mapper = new ObjectMapper();
        if (!jedisKeys.exists(key)) {
            ShopCategory shopCategoryCondition = new ShopCategory();
            // 当shopCategoryDesc不为空的时候，查询的条件会变为 where parent_id is not null
//            shopCategoryCondition.setShopCategoryDesc("ALLSECOND");
            ShopCategory parent = new ShopCategory();
            parent.setShopCategoryId(-1l);
            shopCategoryCondition.setParent(parent);
            shopCategoryList = shopCategoryDao
                    .queryShopCategory(shopCategoryCondition);
            String jsonString = mapper.writeValueAsString(shopCategoryList);
            jedisStrings.set(key, jsonString);
        } else {
            String jsonString = jedisStrings.get(key);
            JavaType javaType = mapper.getTypeFactory()
                    .constructParametricType(ArrayList.class,
                            ShopCategory.class);
            shopCategoryList = mapper.readValue(jsonString, javaType);
        }
        return shopCategoryList;
    }

    @Override
    public ShopCategory getShopCategoryById(Long shopCategoryId) {
        return shopCategoryDao.getShopCategoryById(shopCategoryId);
    }

    @Override
    public ShopCategoryExecution addShopCategory(ShopCategory shopCategory, ImageHolder thumbnail) {
        if (shopCategory != null) {
            shopCategory.setCreateTime(new Date());
            shopCategory.setLastEditTime(new Date());
            if (thumbnail.getImage() != null) {
                addThumbnail(shopCategory, thumbnail);
            }
            try {
                int effectedNum = shopCategoryDao
                        .insertShopCategory(shopCategory);
                if (effectedNum > 0) {
                    String prefix = SCLISTKEY;
                    Set<String> keySet = jedisKeys.keys(prefix + "*");
                    for (String key : keySet) {
                        jedisKeys.del(key);
                    }
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.SUCCESS, shopCategory);
                } else {
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("添加店铺类别信息失败:" + e.toString());
            }
        } else {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
    }



    @Override
    public ShopCategoryExecution modifyShopCategory(ShopCategory shopCategory, ImageHolder thumbnail, boolean thumbnailChange) {
        if (shopCategory.getShopCategoryId() != null
                && shopCategory.getShopCategoryId() > 0) {
            shopCategory.setLastEditTime(new Date());
            if (thumbnail != null) {
                ShopCategory tempShopCategory = shopCategoryDao
                        .getShopCategoryById(shopCategory.getShopCategoryId());
                if (tempShopCategory.getShopCategoryImg() != null) {
                    ImageUtil.deleFileOrPath(tempShopCategory.getShopCategoryImg());
                }
                addThumbnail(shopCategory, thumbnail);
            }
            try {
                int effectedNum = shopCategoryDao
                        .updateShopCategory(shopCategory);
                if (effectedNum > 0) {
                    String prefix = SCLISTKEY;
                    Set<String> keySet = jedisKeys.keys(prefix + "*");
                    for (String key : keySet) {
                        jedisKeys.del(key);
                    }
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.SUCCESS, shopCategory);
                } else {
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("更新店铺类别信息失败:" + e.toString());
            }
        } else {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
    }

    @Override
    public ShopCategoryExecution removeShopCategory(long shopCategoryId) {
        if (shopCategoryId > 0) {
            try {
                ShopCategory tempShopCategory = shopCategoryDao
                        .getShopCategoryById(shopCategoryId);
                if (tempShopCategory.getShopCategoryImg() != null) {
                    ImageUtil.deleFileOrPath(tempShopCategory.getShopCategoryImg());
                }
                int effectedNum = shopCategoryDao
                        .deleteShopCategory(shopCategoryId);
                if (effectedNum > 0) {
                    String prefix = SCLISTKEY;
                    Set<String> keySet = jedisKeys.keys(prefix + "*");
                    for (String key : keySet) {
                        jedisKeys.del(key);
                    }
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.SUCCESS);
                } else {
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("删除店铺类别信息失败:" + e.toString());
            }
        } else {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
    }

    @Override
    public ShopCategoryExecution removeShopCategoryList(List<Long> shopCategoryIdList) {
        if (shopCategoryIdList != null && shopCategoryIdList.size() > 0) {
            try {
                List<ShopCategory> shopCategoryList = shopCategoryDao
                        .queryShopCategoryByIds(shopCategoryIdList);
                for (ShopCategory shopCategory : shopCategoryList) {
                    if (shopCategory.getShopCategoryImg() != null) {
                        ImageUtil.deleFileOrPath(shopCategory.getShopCategoryImg());
                    }
                }
                int effectedNum = shopCategoryDao
                        .batchDeleteShopCategory(shopCategoryIdList);
                if (effectedNum > 0) {
                    String prefix = SCLISTKEY;
                    Set<String> keySet = jedisKeys.keys(prefix + "*");
                    for (String key : keySet) {
                        jedisKeys.del(key);
                    }
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.SUCCESS);
                } else {
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("删除店铺类别信息失败:" + e.toString());
            }
        } else {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
    }
    private void addThumbnail(ShopCategory shopCategory,
                              ImageHolder thumbnail) {
        String dest = PathUtil.getShopCategoryPath();
        String shopCategoryAddr = null;
     try {
         shopCategoryAddr= ImageUtil.generateThumbnail(thumbnail,dest);
     }catch (Exception e) {
         e.printStackTrace();
     }
        shopCategory.setShopCategoryImg(shopCategoryAddr);
    }
}
