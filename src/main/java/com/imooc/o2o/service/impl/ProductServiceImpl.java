package com.imooc.o2o.service.impl;


import com.imooc.o2o.dao.ProductDao;
import com.imooc.o2o.dao.ProductImgDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductImg;
import com.imooc.o2o.enums.ProductStateEnum;
import com.imooc.o2o.exceptions.ProductCategoryOperationException;
import com.imooc.o2o.exceptions.ProductOperationException;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PageCalculator;
import com.imooc.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductImgDao productImgDao;
    //1.处理缩略图，获取缩略图相对路径并赋值为product
    // 注意缩略图是存在product里面的只有一个为imgAddr，而详情图片是存在product_img里面的，一种产品对应多个缩略图
    //2.往tb——product写入商品信息，获取productId
    //3.解和productId批量处理商品详情图
    //4.将商品详情列表批量插入tb_product_img中
    @Override
    @Transactional  //事务支持  如果有缩略图就有thumbnail，如果没有则为空值
    // thumbnail.getImageName()主要是用于获取其后缀
    public ProductExecution addProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgHolderList) throws ProductCategoryOperationException {
        //空值判断,并且传入的店铺id也不能为空，不然不知道是那个店铺的
        if (product!=null&&product.getShop().getShopId()!=null){
            //由于是添加，其创建时间和目前的最后修改时间是西安贼
            product.setCreateTime(new Date());
            product.setLastEditTime(new Date());
            //默认商家时间的状态
            product.setEnableStatus(1);
            //弱商品缩略图不为空则田间
            if (thumbnail!=null){
                addThumbnail(product,thumbnail);//要获取product的id来识别缩略图，product的addr已经变成了图片存储的地址
            }
            try {
                //创建商品信息
                int effectedNum = productDao.insertProduct(product);
                if (effectedNum<=0){
                    throw new ProductOperationException("创建商品失败");
                }
            }catch (Exception e){
                throw new ProductOperationException("创建商品失败"+e.getMessage());
            }
            //商品详情图不为空则添加
            if (productImgHolderList!=null&&productImgHolderList.size()>0){//判断是否有详情图片
                addProductImgList(product,productImgHolderList);//传入product用于设定图片的productid
            }
            return new ProductExecution(ProductStateEnum.SUCCESS,product);

        }else {//product空值
            //传值为空则返回信息错误
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    @Override
    public Product getProductById(long productId) {
        return productDao.queryProductById(productId);
    }
//1.若缩略图参数有值，则处理缩略图
    //若先前存在缩略图则先删除再添加新图，之后获取缩略图相对路径冰赋值给product
    //2.若商品详情图列表的参数有值，对商品详情图片列表进行同样的操作
    //3.将tb_product_img 下面的该商品原先的商品详情图记录全部清除
    //4.更新tb_product和tb_product_img信息


    @Override
    public ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImages) throws ProductOperationException {
       if (product!=null&&product.getShop()!=null&&product.getShop().getShopId()!=null){
           product.setLastEditTime(new Date());
           //有缩略图就删除旧的 创建新的
           if (thumbnail!=null){
              Product tempProduct= productDao.queryProductById(product.getProductId());//查询本来数据库中的product
              if (tempProduct.getImgAddr()!=null){
                  ImageUtil.deleFileOrPath(tempProduct.getImgAddr());
              }
              addThumbnail(product,thumbnail);//创建图片并修改了图片地址到product中
           }
           if (productImages!=null&&productImages.size()>0){
               deleteProductList(product.getProductId());// 出错 删除存的图片和数据库里ProtuctId为改prodctid的
               addProductImgList(product,productImages);//穿件图片并添加到数据库中
           }
           try{
              int effectedNum =  productDao.updateProduct(product);
              if (effectedNum<=0){
                  throw new ProductOperationException("更新商品信息失败");
              }
              return new ProductExecution(ProductStateEnum.SUCCESS);
           }catch (Exception e){
               throw new ProductOperationException("更新商信息失败B"+e.toString());
           }

       }
        return new ProductExecution(ProductStateEnum.EMPTY);
    }

    @Override
    public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
        int rowSize = PageCalculator.calculateRowIndex(pageIndex,pageSize);
        List<Product> productList =productDao.queryProductList(productCondition,rowSize,pageSize);
        int count = productDao.queryProductCount(productCondition);
        ProductExecution pe = new ProductExecution();
        pe.setCount(count);
        pe.setProductList(productList);
        return pe;
    }

    //删除该Id商品下的所有存的详情图的图片和 数据库里面的Product_img信息
    //传入一个productId  删除该商品下所有的详情图
    private void deleteProductList(long productId){
        List<ProductImg> productImgList= productImgDao.queryProductImgList(productId);//?
        for (ProductImg productImg:productImgList){
            ImageUtil.deleFileOrPath(productImg.getImgAddr());
        }
        productImgDao.deleteProductImgByProductId(productId);
    }
//传入一个 product 和ImageHolder
    private void addThumbnail(Product product,ImageHolder thumbnail) {
        String dest = PathUtil.getShopImagePath(product.getShop().getShopId());//通过id获取相对卢克
        String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail,dest);//创建图片返回图片位置
        product.setImgAddr(thumbnailAddr);//将图片存在product.ImgAddr中

    }
    //创建商品的详情图
    private void addProductImgList(Product product,List<ImageHolder> productImgHolderList){
        //获取图片的存储路径，这里直接存放在相应店铺的文件夹地下
        String dest = PathUtil.getShopImagePath(product.getShop().getShopId());//这是存储图片的相对路径
        List<ProductImg> productImgList = new ArrayList<ProductImg>();
        //遍历一次就创建一个rpoductImg对象，并添加进productImg是立体类里
        for (ImageHolder productImgHolder:productImgHolderList){
            //依次创建图片 并且创建相应的productImg对象用于插入到数据库中
            String imgAddr = ImageUtil.generateNormalImg(productImgHolder,dest);//创建图片并且返回路径
            ProductImg productImg = new ProductImg();
            productImg.setImgAddr(imgAddr);
            productImg.setCreateTime(new Date());
            productImg.setProductId(product.getProductId());
            productImgList.add(productImg);
        }
        //有图片就添加
        if (productImgList.size()>0){
            try{
                int effectedNum = productImgDao.batchInsertProductImg(productImgList);
                if (effectedNum<=0){
                    throw new ProductOperationException("创建商铺详情图片失败");
                }
            }catch (Exception e){
                throw new ProductOperationException("创建商铺详情图失败"+e.getMessage());
            }
        }
    }

}
