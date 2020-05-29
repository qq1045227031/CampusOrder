package com.imooc.o2o.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.cache.JedisUtil;
import com.imooc.o2o.dao.HeadLineDao;
import com.imooc.o2o.dto.HeadLineExecution;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.entity.HeadLine;
import com.imooc.o2o.enums.HeadLineStateEnum;
import com.imooc.o2o.exceptions.HeadLineOperationException;
import com.imooc.o2o.service.HeadLineService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class HeadLineServiceImpl implements HeadLineService {
    @Autowired
    private JedisUtil.Strings jedisStrings;
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private HeadLineDao headLineDao;
    private static Logger logger = LoggerFactory.getLogger(HeadLineServiceImpl.class);

    @Override
    @Transactional
    public List<HeadLine> getHeadLineList(HeadLine headLineCondition) {
        //接口已定义其key为 HLLISTKEY="headlinelist";
        //定义接收对象
        List<HeadLine> headLineList = null;
        //定义Jackson数据转换操作类
        ObjectMapper mapper = new ObjectMapper();
        //定义redis的key前缀
        String key = HLLISTKEY;//  public static final  String HLLISTKEY="headlinelist";
        //拼接出redis的key
        if (headLineCondition != null && headLineCondition.getEnableStatus() != null) {
            //拼接后，如果是headlinelist_0表示查出禁用的头条，如果是headlinelist_1表示查出显示的头条，如果值为null即是headlinelist表示查出所有的头条
            key = key + "_" + headLineCondition.getEnableStatus();
        }
        // redis中不存在key，则设值
        if (!jedisKeys.exists(key)) {
            //若不存在，则从数据库里面取出相应数据
            headLineList = headLineDao.queryHeadLine(headLineCondition);
            // 将相关的实体类集合转换成string，存入redis里面对应的key中
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(headLineList);
                jedisStrings.set(key, jsonString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new HeadLineOperationException(e.getMessage());
            }
        } else {
            String jsonString = jedisStrings.get(key);
            // 将jsonString转为list (redis存的都是string类型 ，数组要通过javaType mapperobject方法进行转换为实体类)
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, HeadLine.class);
            try {
                headLineList = mapper.readValue(jsonString, javaType);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new HeadLineOperationException(e.getMessage());
            }
        }
        return headLineList;
    }


    @Override
    @Transactional
    public HeadLineExecution addHeadLine(HeadLine headLine,ImageHolder thumbnail ) {
        if (headLine != null) {
            headLine.setCreateTime(new Date());
            headLine.setLastEditTime(new Date());
            if (thumbnail != null) {
                addThumbnail(headLine, thumbnail);
            }
            try {
                int effectedNum = headLineDao.insertHeadLine(headLine);
                if (effectedNum > 0) {
                    String prefix = HLLISTKEY;
                    Set<String> keySet = jedisKeys.keys(prefix + "*");
                    for (String key : keySet) {
                        jedisKeys.del(key);
                    }
                    return new HeadLineExecution(HeadLineStateEnum.SUCCESS,
                            headLine);
                } else {
                    return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("添加区域信息失败:" + e.toString());
            }
        } else {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public HeadLineExecution modifyHeadLine(HeadLine headLine,
                                            ImageHolder thumbnail ) {
        if (headLine.getLineId() != null && headLine.getLineId() > 0) {
            headLine.setLastEditTime(new Date());
            if (thumbnail != null) {
                HeadLine tempHeadLine = headLineDao.queryHeadLineById(headLine
                        .getLineId());
                if (tempHeadLine.getLineImg() != null) {
                    ImageUtil.deleFileOrPath(tempHeadLine.getLineImg());
                }
                addThumbnail(headLine, thumbnail);
            }
            try {
                int effectedNum = headLineDao.updateHeadLine(headLine);
                if (effectedNum > 0) {
                    String prefix = HLLISTKEY;
                    Set<String> keySet = jedisKeys.keys(prefix + "*");
                    for (String key : keySet) {
                        jedisKeys.del(key);
                    }
                    return new HeadLineExecution(HeadLineStateEnum.SUCCESS,
                            headLine);
                } else {
                    return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("更新头条信息失败:" + e.toString());
            }
        } else {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public HeadLineExecution removeHeadLine(long headLineId) {
        if (headLineId > 0) {
            try {
                HeadLine tempHeadLine = headLineDao
                        .queryHeadLineById(headLineId);
                if (tempHeadLine.getLineImg() != null) {
                    ImageUtil.deleFileOrPath(tempHeadLine.getLineImg());
                }
                int effectedNum = headLineDao.deleteHeadLine(headLineId);
                if (effectedNum > 0) {
                    String prefix = HLLISTKEY;
                    Set<String> keySet = jedisKeys.keys(prefix + "*");
                    for (String key : keySet) {
                        jedisKeys.del(key);
                    }
                    return new HeadLineExecution(HeadLineStateEnum.SUCCESS);
                } else {
                    return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("删除头条信息失败:" + e.toString());
            }
        } else {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public HeadLineExecution removeHeadLineList(List<Long> headLineIdList) {
        if (headLineIdList != null && headLineIdList.size() > 0) {
            try {
                List<HeadLine> headLineList = headLineDao
                        .queryHeadLineByIds(headLineIdList);
                for (HeadLine headLine : headLineList) {
                    if (headLine.getLineImg() != null) {
                        ImageUtil.deleFileOrPath(headLine.getLineImg());
                    }
                }
                int effectedNum = headLineDao
                        .batchDeleteHeadLine(headLineIdList);
                if (effectedNum > 0) {
                    String prefix = HLLISTKEY;
                    Set<String> keySet = jedisKeys.keys(prefix + "*");
                    for (String key : keySet) {
                        jedisKeys.del(key);
                    }
                    return new HeadLineExecution(HeadLineStateEnum.SUCCESS);
                } else {
                    return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("删除头条信息失败:" + e.toString());
            }
        } else {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
    }

    private void addThumbnail(HeadLine headLine, ImageHolder thumbnail) {
        String dest = PathUtil.getHeadLineImagePath();
        String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail,dest);//创建图片返回图片位置
        headLine.setLineImg(thumbnailAddr);
    }
}
