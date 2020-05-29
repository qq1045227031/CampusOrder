package com.imooc.o2o.dao;

import com.imooc.o2o.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductDao {
    int insertProduct(Product product);

    /**
     * 通过productId查找唯一id
     * @param productId
     * @return
     */
    Product queryProductById(long productId);

    /**
     * 更新商品信息
     * @param product
     * @return
     */
    int updateProduct(Product product);

    List<Product> queryProductList(@Param("productCondition")Product productCondition,@Param("rowIndex")int rowIndex,@Param("pageSize")int pageSize);
    int queryProductCount(@Param("productCondition")Product productCondition);
   //删除商品种类后把改种类下的所有商品的 productCategory置为空
    int updateProductCategoryToNull(long productCategoryId);
}
