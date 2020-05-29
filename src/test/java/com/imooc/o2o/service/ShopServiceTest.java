package com.imooc.o2o.service;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class ShopServiceTest extends BaseTest {
    @Autowired
    ShopService shopService;

    @Test
    public void testGetShopList(){
        Shop shopCondition = new Shop();//查找条件
        ShopCategory shopCategory = new ShopCategory();
        shopCategory.setShopCategoryId(1l);
        shopCondition.setShopCategory(shopCategory);
        ShopExecution shopExecution =  shopService.getShopList(shopCondition,1,2);
        System.out.println(shopExecution.getShopList().size());//这是根据页码查询出的数量 shopDao.queryShopList(shopCondition,rowSize,pageSize);
        System.out.println(shopExecution.getCount());//这是符合条件的总数 shopDao.queryShopCount(shopCondition);
    }
    @Test
    @Ignore
    public void test1(){
        Shop shop = shopService.getByShopId(1l);
        System.out.println(shop.getArea().getAreaName());//结果能够获取
    }
    @Test
    @Ignore
    public void testModifyShop() throws ShopOperationException,FileNotFoundException{
        Shop shop = new Shop();
        shop.setShopId(1l);
        shop.setShopName("测试update的店铺");
        File shopImg = new File("C:/Users/吴文兵/image/jinkela.png");
        FileInputStream is = new FileInputStream(shopImg);
        ImageHolder imageHolder = new ImageHolder("jinkela.png",is);
        ShopExecution shopExecution = shopService.modifyShop(shop,imageHolder);
        System.out.println("新的图片地址"+shopExecution.getShop().getShopImg());
    }
    @Test
    @Ignore
    public void testAddShop() throws FileNotFoundException {
        Shop shop=new Shop();
        PersonInfo owner = new PersonInfo();
        Area area = new Area();
        ShopCategory shopCategory= new ShopCategory();
        owner.setUserId(1L);
        area.setAreaId(2l);
        shopCategory.setShopCategoryId(1L);
        shop.setOwner(owner);
        shop.setArea(area);
        shop.setShopCategory(shopCategory);
        shop.setShopName("测试的店铺图片4");
        shop.setShopDesc("描述图片4");
        shop.setShopAddr("地址图片3");
        shop.setPhone("13288214717-3");
        shop.setCreateTime(new Date());
        shop.setEnableStatus(1);
        shop.setAdvice("审核中");
        File shopImg = new File("C:/Program Files/study/test/1.jpg");
        InputStream is = new FileInputStream(shopImg);
        ImageHolder imageHolder = new ImageHolder(shopImg.getName(),is);
        ShopExecution se = shopService.addShop(shop,imageHolder);
        if (ShopStateEnum.CHECK.getState()==se.getState()){
            System.out.println("成功");
        }else{
            System.out.println("失败");
        }
    }

    //com.imooc.o2o.exceptions.ShopOperationException: addShop error addShopImg error创建微缩图失败javax.imageio.IIOException: Can't read inp
     //这个问题主要是test运行下如法读取到main下的resource下的水印文件watermark，所以要在Test下创建resource，然后创建水印文件
}
