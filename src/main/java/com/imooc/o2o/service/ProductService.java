package com.imooc.o2o.service;

import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.exceptions.ProductCategoryOperationException;
import com.imooc.o2o.exceptions.ProductOperationException;

import java.util.List;

public interface ProductService {
    /**
     *
     * @param product  存产品信息
     * @param thumbnail   存产品的缩略图还有名字
     * @param productImgHolderList  存参评的详情图片和其名字 productImg
     * @return
     * @throws ProductCategoryOperationException
     */
    ProductExecution addProduct(Product product,ImageHolder thumbnail, List<ImageHolder> productImgHolderList)throws ProductCategoryOperationException;
    Product getProductById(long productId);
    ProductExecution modifyProduct(Product product,ImageHolder thumbnail ,List<ImageHolder> productImages)throws ProductOperationException;
    ProductExecution getProductList(Product productCondition,int pageIndex,int pageSize);

}

