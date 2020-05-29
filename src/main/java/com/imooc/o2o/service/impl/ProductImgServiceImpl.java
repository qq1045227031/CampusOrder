package com.imooc.o2o.service.impl;

import com.imooc.o2o.dao.ProductImgDao;
import com.imooc.o2o.entity.ProductImg;
import com.imooc.o2o.service.ProductImgSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImgServiceImpl implements ProductImgSerivce {
    @Autowired
    private ProductImgDao productImgDao;

    @Override
    public int batchInsertProductImg(List<ProductImg> productImgList) {
        return productImgDao.batchInsertProductImg(productImgList);
    }

    @Override
    public int deleteProductImgByProductId(long productId) {
        return productImgDao.deleteProductImgByProductId(productId);
    }

    @Override
    public List<ProductImg> queryProductImgList(long productId) {
        return productImgDao.queryProductImgList(productId);
    }
}
