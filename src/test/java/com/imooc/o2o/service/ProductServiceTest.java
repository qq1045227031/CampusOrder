package com.imooc.o2o.service;


import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.ProductStateEnum;
import com.imooc.o2o.exceptions.ProductOperationException;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductServiceTest extends BaseTest {//要记得继承啊
    @Autowired
    ProductService productService;

    @Test
    public void testModifyProduct() throws ProductOperationException, FileNotFoundException {
        Product product= new Product();
        Shop shop = new Shop();
        shop.setShopId(1l);
        ProductCategory pc = new ProductCategory();
        product.setProductId(1l);
        pc.setProductCategoryId(1l);
        product.setShop(shop);
        product.setProductCategory(pc);
        product.setEnableStatus(1);
        product.setCreateTime(new Date());
        product.setProductName("测试修改1");
        product.setProductDesc("测试修改");
        File thumbnailFile = new File("C:/Users/吴文兵/image/xiugai.jpg");
        InputStream is = new FileInputStream(thumbnailFile);
        ImageHolder thumbnail = new ImageHolder(thumbnailFile.getName(),is);//文件命通过本身的file获得
        File thumbnailFile1 = new File("C:/Users/吴文兵/image/xiugai.jpg");
        InputStream is1 = new FileInputStream(thumbnailFile);
        File thumbnailFile2 = new File("C:/Users/吴文兵/image/xiugai.jpg");
        InputStream is2 = new FileInputStream(thumbnailFile);
        List<ImageHolder> productImgList = new ArrayList<ImageHolder>();
        productImgList.add(new ImageHolder(thumbnailFile1.getName(),is1));
        productImgList.add(new ImageHolder(thumbnailFile2.getName(),is2));
        ProductExecution  pe  = productService.modifyProduct(product,thumbnail,productImgList);
    }
    @Test
    public void testAddProduct() throws ShopOperationException, FileNotFoundException {
        //创建shopId为1且ProductCategory为1的商品实例并赋值
        Product product= new Product();
        Shop shop = new Shop();
        shop.setShopId(1l);
        ProductCategory pc = new ProductCategory();
        pc.setProductCategoryId(1l);
        product.setProductId(1l);
        product.setShop(shop);
        product.setProductCategory(pc);
        product.setEnableStatus(1);
        product.setCreateTime(new Date());
        product.setProductName("测试斜杠问题");
        product.setProductDesc("测试斜杠问题");
        product.setPriority(11);
        product.setEnableStatus(ProductStateEnum.SUCCESS.getState());
        //创建猥琐图文件流
        File thumbnailFile = new File("C:/Users/吴文兵/image/kewei.jpg");
        InputStream is = new FileInputStream(thumbnailFile);
        ImageHolder thumbnail = new ImageHolder(thumbnailFile.getName(),is);//文件命通过本身的file获得

        //创建两个商品详情图片文件流并将它们添加到详情图片表中
        File thumbnailFile1 = new File("C:/Users/吴文兵/image/xuefeng.jpg");
        InputStream is1 = new FileInputStream(thumbnailFile);
        File thumbnailFile2 = new File("C:/Users/吴文兵/image/kewei1.jpg");
        InputStream is2 = new FileInputStream(thumbnailFile);
        List<ImageHolder> productImgList = new ArrayList<ImageHolder>();
        productImgList.add(new ImageHolder(thumbnailFile1.getName(),is1));
        productImgList.add(new ImageHolder(thumbnailFile2.getName(),is2));

        ProductExecution pe = productService.addProduct(product,thumbnail,productImgList);
        if (pe.getState()==ProductStateEnum.SUCCESS.getState()&&pe!=null){
            System.out.println(pe.getState());
        }else {
            System.out.println("pe为空");
        }
    }
    @Test
    public void testaddThumbnail() throws FileNotFoundException {
        String dest = PathUtil.getShopImagePath(100l);
        File thumbnailFile = new File("C:/Users/吴文兵/image/kewei.jpg");
        InputStream is = new FileInputStream(thumbnailFile);
        ImageHolder thumbnail = new ImageHolder(thumbnailFile.getName(),is);
        String thumbnailAddr = ImageUtil.generateNormalImg(thumbnail,dest);
        //之前由于删除了basePath = URLDecoder.decode(basePath, "utf-8"); 导致找不到水印文件，
        // 因为我的路径C:\Users\吴文兵\IdeaProjects\springboot\target\test-classes是中文的，
        // 其解析为字符后没有将其再变成utf-8格式，所以要手动转化不然找不到，吴文兵会乱码
        System.out.println(thumbnailAddr);//返回的是一个相对路径
    }

}
