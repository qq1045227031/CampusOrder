package com.imooc.o2o.service;

import com.imooc.o2o.dto.AwardExecution;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.entity.Award;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public interface AwardService {

    /**
     *
     * @param awardCondition
     * @param pageIndex
     * @param pageSize
     * @return
     */
    AwardExecution getAwardList(Award awardCondition, int pageIndex,
                                int pageSize);

    /**
     *
     * @param awardId
     * @return
     */
    Award getAwardById(long awardId);

    /**
     *
     * @param award
     * @param thumbnail
     * @return
     */
    AwardExecution addAward(Award award, ImageHolder thumbnail);

    /**
     *
     * @param award
     * @param thumbnail
     * @return
     */
    AwardExecution modifyAward(Award award,ImageHolder thumbnail);
}
