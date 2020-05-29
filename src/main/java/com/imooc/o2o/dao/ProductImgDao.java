package com.imooc.o2o.dao;

import com.imooc.o2o.entity.ProductImg;

import java.util.List;

public interface ProductImgDao {
    /**
     * 批量添加详情图片
     * @param productImgList
     * @return
     */
    int batchInsertProductImg(List<ProductImg> productImgList);
    /**
     * 删除指定商品下的所有详情图
     * @param ProductId
     * @return
     */
    int deleteProductImgByProductId(long ProductId);
    List<ProductImg> queryProductImgList(long ProductId);
}
