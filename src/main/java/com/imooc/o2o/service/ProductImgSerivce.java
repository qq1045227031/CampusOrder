package com.imooc.o2o.service;

import com.imooc.o2o.entity.ProductImg;

import java.util.List;

public interface ProductImgSerivce {
    /**
     * 批量添加详情图片
     * @param productImgList
     * @return
     */
    int batchInsertProductImg(List<ProductImg> productImgList);
    /**
     * 删除指定商品下的所有详情图
     * @param productId
     * @return
     */
    int deleteProductImgByProductId(long productId);
    List<ProductImg> queryProductImgList(long productId);
}
