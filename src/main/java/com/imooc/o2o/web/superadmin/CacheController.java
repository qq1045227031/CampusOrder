package com.imooc.o2o.web.superadmin;

import com.imooc.o2o.entity.HeadLine;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.CacheService;
import com.imooc.o2o.service.HeadLineService;
import com.imooc.o2o.service.ShopCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
//@RequestMapping("/superadmin")
//清楚缓存
public class CacheController {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private HeadLineService headLineService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @RequestMapping(value = "/clearcache4area",method = RequestMethod.GET)
    private String clearCache4Area(){
        cacheService.removeFromCache(areaService.AREALISTKEY);
        return "shop/operationsuccess";
    }
    @RequestMapping(value = "/clearcache4headline",method = RequestMethod.GET)
    private String clearCache4HeadLine(){
        cacheService.removeFromCache(headLineService.HLLISTKEY);
        return "shop/operationsuccess";
    }
    @RequestMapping(value = "/clearcache4shopcategory",method = RequestMethod.GET)
    private String clearCache4shopCategory(){
        cacheService.removeFromCache(shopCategoryService.SCLISTKEY);
        return "shop/operationsuccess";
    }

}
