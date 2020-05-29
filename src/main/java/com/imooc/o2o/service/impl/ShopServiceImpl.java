package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.ShopDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PageCalculator;
import com.imooc.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {
    private static final Logger logger = LoggerFactory.getLogger(ShopServiceImpl.class);
    @Autowired
    private ShopDao shopDao;

    @Override
    public ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize) {
        int rowSize = PageCalculator.calculateRowIndex(pageIndex,pageSize);
        List<Shop> shopList = shopDao.queryShopList(shopCondition,rowSize,pageSize);
        int count = shopDao.queryShopCount(shopCondition);
        ShopExecution se = new ShopExecution();
        if (shopList!=null){
            se.setShopList(shopList);
            se.setCount(count);
        }else {
            se.setState(ShopStateEnum.INNER_ERROR.getState());
        }
        return se;
    }

    @Override
    public Shop getByShopId(long shopId) {
        return shopDao.queryByShopId(shopId);
    }

    @Override
    public ShopExecution modifyShop(Shop shop, ImageHolder thumbnail) throws ShopOperationException {
//        1.判断是否需要处理图片
        //2.更新店铺信息
        if (shop==null||shop.getShopId()==null){//要根据id找出数据库的shop来找到本身img图片的位置并更改
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }else {
            if (thumbnail==null){
                shop.setLastEditTime(new Date());
                int effectedNum = shopDao.updateShop(shop);
                if (effectedNum <= 0) {
                    return new ShopExecution(ShopStateEnum.INNER_ERROR);
                } else {
                    shop = shopDao.queryByShopId(shop.getShopId());
                    return new ShopExecution(ShopStateEnum.SUCCESS, shop);
                }
            }
            try{
            //1.判断是否需要处理图片
            if (thumbnail.getImage()!=null&&thumbnail.getImageName()!=null&&!"".equals(thumbnail.getImageName())) {//图片输入不为空，名字不为空且不是空指端
                Shop temShop = shopDao.queryByShopId(shop.getShopId());
                if (temShop.getShopImg() != null) {
                    ImageUtil.deleFileOrPath(temShop.getShopImg());//删除本身的文件
                }
                addShopImg(shop,thumbnail);//shop此时会被更改
            }
                //2.更新店铺信息
                shop.setLastEditTime(new Date());
                int effectedNum = shopDao.updateShop(shop);
                if (effectedNum <= 0) {
                    return new ShopExecution(ShopStateEnum.INNER_ERROR);
                } else {
                    shop = shopDao.queryByShopId(shop.getShopId());
                    return new ShopExecution(ShopStateEnum.SUCCESS, shop);
                }
            }catch (Exception e){
                throw new ShopOperationException("modifyShop error:"+e.getMessage());
            }
        }
    }

    @Override
    @Transactional //事务支持
    //传入的是一个shop对象和图片地址
    public ShopExecution addShop(Shop shop, ImageHolder thumbnail) {
        //空置判断
        if (shop==null){
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }
        try {
            //给店铺信息赋初值
            shop.setEnableStatus(0);
            shop.setCreateTime(new Date());
            shop.setLastEditTime(new Date());
            //添加店铺信息
            int effectedNum = shopDao.insertShop(shop);
            if (effectedNum<= 0){
                throw new ShopOperationException("店铺创建失败");//RuntimeException才会终止事务
            }else {
                if (thumbnail.getImage()!=null){
                    //存储图片
                    try{
                        addShopImg(shop,thumbnail);//存储图片后，由于是对象其shop里面的地址也已经发生改变
                    }catch (Exception e){
                        throw new ShopOperationException("addShopImg error"+e.getMessage());
                    }
                    //更改店铺的图片地址到数据库中,指向我们在文件夹中图片的地址
                    effectedNum = shopDao.updateShop(shop);
                    if (effectedNum<=0){
                        throw new ShopOperationException("更新图片地址失败");
                    }
                }
            }

        }catch (Exception e){
            throw new ShopOperationException("addShop error "+e.getMessage());
        }
        return new ShopExecution(ShopStateEnum.CHECK,shop);
    }
//此方法用于存储图片到文件夹中,并修改shop对象里面的img地址
    private void addShopImg(Shop shop,ImageHolder thumbnail) {
        //获取相对路径,要传入id
        String dest = PathUtil.getShopImagePath(shop.getShopId());//获取图片相对路径地址
        String shopImgAddr  = null;
        try {
            shopImgAddr = ImageUtil.generateThumbnail(thumbnail,dest);//将图片保存到本地图库冰返回一个地址
        } catch (Exception e) {
            e.printStackTrace();
        }

        shop.setShopImg(shopImgAddr);//讲地址赋给shop

    }
}
