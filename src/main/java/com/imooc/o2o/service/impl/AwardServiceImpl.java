package com.imooc.o2o.service.impl;

import ch.qos.logback.core.util.FileUtil;
import com.imooc.o2o.dao.AwardDao;
import com.imooc.o2o.dto.AwardExecution;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.entity.Award;
import com.imooc.o2o.enums.AwardStateEnum;
import com.imooc.o2o.service.AwardService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PageCalculator;
import com.imooc.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class AwardServiceImpl implements AwardService {

    @Autowired
    private AwardDao awardDao;

    @Override
    public AwardExecution getAwardList(Award awardCondition, int pageIndex,
                                       int pageSize) {
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<Award> awardList = awardDao.queryAwardList(awardCondition,
                rowIndex, pageSize);
        int count = awardDao.queryAwardCount(awardCondition);
        AwardExecution ae = new AwardExecution();
        ae.setAwardList(awardList);
        ae.setCount(count);
        return ae;
    }

    @Override
    public Award getAwardById(long awardId) {
        return awardDao.queryAwardByAwardId(awardId);
    }

    @Override
    @Transactional
    public  AwardExecution addAward(Award award, ImageHolder thumbnail) {
        if (award != null && award.getShopId() != null) {
            award.setCreateTime(new Date());
            award.setLastEditTime(new Date());
            award.setEnableStatus(1);
            if (thumbnail != null) {
                addThumbnail(award, thumbnail);
            }
            try {
                int effectedNum = awardDao.insertAward(award);
                if (effectedNum <= 0) {
                    throw new RuntimeException("创建商品失败");
                }
            } catch (Exception e) {
                throw new RuntimeException("创建商品失败:" + e.toString());
            }
            return new AwardExecution(AwardStateEnum.SUCCESS, award);
        } else {
            return new AwardExecution(AwardStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public AwardExecution modifyAward(Award award, ImageHolder thumbnail) {
        if (award != null && award.getAwardId()!= null) {
            award.setLastEditTime(new Date());
            if (thumbnail != null) {
                Award tempAward = awardDao.queryAwardByAwardId(award
                        .getAwardId());
                if (tempAward.getAwardImg() != null) {
                    ImageUtil.deleFileOrPath(tempAward.getAwardImg());
                }
                //储存图片溜冰讲相对路径这只在里面
                addThumbnail(award, thumbnail);
            }
            try {
                int effectedNum = awardDao.updateAward(award);
                if (effectedNum <= 0) {
                    throw new RuntimeException("更新商品信息失败");
                }
                return new AwardExecution(AwardStateEnum.SUCCESS, award);
            } catch (Exception e) {
                throw new RuntimeException("更新商品信息失败:" + e.toString());
            }
        } else {
            return new AwardExecution(AwardStateEnum.EMPTY);
        }
    }
    private void addThumbnail(Award award,ImageHolder thumbnail) {
        //根据shop 传入相对的路径
        String dest = PathUtil.getShopImagePath(award.getShopId());
        //传入图片溜 和相对路径 创建图片 并且返回图片路径
        String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail,dest);//创建图片返回图片位置
        award.setAwardImg(thumbnailAddr);//将图片存在product.ImgAddr中
    }

}
