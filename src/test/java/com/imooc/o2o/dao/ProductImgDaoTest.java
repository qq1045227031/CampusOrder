package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.ProductImg;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)//test按照名字执行
public class ProductImgDaoTest extends BaseTest {
    @Autowired
    ProductImgDao productImgDao;
    @Test
    public void testABatchInsertProductImg()throws Exception{
        ProductImg productImg1 = new ProductImg();
        productImg1.setImgAddr("图片1地址");
        productImg1.setCreateTime(new Date());
        productImg1.setImgDesc("图片1测试");
        productImg1.setPriority(50);
        productImg1.setProductId(1l);
        ProductImg productImg2 = new ProductImg();
        productImg2.setImgAddr("图片2地址");
        productImg2.setCreateTime(new Date());
        productImg2.setImgDesc("图片2测试");
        productImg2.setPriority(30);
        productImg2.setProductId(1l);
        List<ProductImg> productImgList = new ArrayList<ProductImg>();
        productImgList.add(productImg1);
        productImgList.add(productImg2);
        int effectNum = productImgDao.batchInsertProductImg(productImgList);
        System.out.println(effectNum);
    }
    @Test
    public void testCDeleteProductImgByProductId()throws Exception{
        //删除新增的两条商品详情图片记录
        long productId = 1;
        int effectNum  = productImgDao.deleteProductImgByProductId(productId);
        System.out.println(effectNum);
    }
    @Test
    public void testQueryProductImgList() {
        //
    }
}
