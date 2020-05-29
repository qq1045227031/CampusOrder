package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.ProductCategoryDao;
import com.imooc.o2o.dao.ProductDao;
import com.imooc.o2o.dto.ProductCategoryExecution;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.enums.ProductCategoryStateEnum;
import com.imooc.o2o.exceptions.ProductCategoryOperationException;
import com.imooc.o2o.exceptions.ProductOperationException;
import com.imooc.o2o.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    @Autowired
    ProductCategoryDao productCategoryDao;
    @Autowired
    ProductDao productDao;
    @Override
    public List<ProductCategory> getProductCategoryList(Long shopId) {
        return  productCategoryDao.queryProductCategoryByShopId(shopId);
    }

    @Override
    @Transactional//开启事务
    public ProductCategoryExecution batchAddProductCategory(
            List<ProductCategory> productCategoryList) throws RuntimeException {
        if (productCategoryList != null && productCategoryList.size() > 0) {
            try {
                int effectedNum = productCategoryDao
                        .batchInsertProductCategory(productCategoryList);
                if (effectedNum <= 0) {
                    throw new RuntimeException("店铺类别失败");
                } else {

                    return new ProductCategoryExecution(
                            ProductCategoryStateEnum.SUCCESS);
                }

            } catch (Exception e) {
                throw new RuntimeException("batchAddProductCategory error: "
                        + e.getMessage());
            }
        } else {
            return new ProductCategoryExecution(
                    ProductCategoryStateEnum.INNER_ERROR);
        }

    }

    @Override
    @Transactional
    public ProductCategoryExecution deleteProductCategory(long productCategoryId, long shopId) throws ProductCategoryOperationException {
        //TODO将此类别下的商品类别id变为空,否则因为productcateogy是product的外键，数据库无法删除
        try{
            int effectedNum = productDao.updateProductCategoryToNull(productCategoryId);
            if (effectedNum<0){
                throw new ProductCategoryOperationException("商品类别更新失败，无法将product的category置为空");
            }
        }catch (Exception e){
            throw new ProductOperationException("deleProductCateogry error:法将product的category置为空"+e.getMessage());
        }
        try{
            int effectNum =productCategoryDao.deleteProductCategory(productCategoryId,shopId);
            if (effectNum<=0){
                throw new ProductCategoryOperationException("商品类别删除失败");
            }else {
                return new  ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
            }
        }catch (Exception e){
            throw new ProductCategoryOperationException("deleteProductCategory err:"+e.getMessage());
        }
    }
}
