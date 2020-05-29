package com.imooc.o2o.service;

import com.imooc.o2o.dto.HeadLineExecution;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.entity.HeadLine;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.List;

public interface HeadLineService {
    public static final  String HLLISTKEY="headlinelist";
    /**
     * 根据条件查询头条列表
     *
     * @param headLineCondition
     * @return
     * @throws IOException
     */
    List<HeadLine> getHeadLineList(HeadLine headLineCondition)throws IOException;
    /**
     *
     * @param headLine
     * @param thumbnail
     * @return
     */
    HeadLineExecution addHeadLine(HeadLine headLine, ImageHolder thumbnail);


    HeadLineExecution modifyHeadLine(HeadLine headLine, ImageHolder thumbnail);

    /**
     *
     * @param headLineId
     * @return
     */
    HeadLineExecution removeHeadLine(long headLineId);

    /**
     *
     * @param headLineIdList
     * @return
     */
    HeadLineExecution removeHeadLineList(List<Long> headLineIdList);
}
