package com.imooc.o2o.service;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.AreaExecution;
import com.imooc.o2o.entity.Area;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AreaServiceTest extends BaseTest {
    @Autowired
    private AreaService areaService;
    @Autowired
    private CacheService cacheService;
    @Test
    public void testDel(){
        AreaExecution areaExecution = areaService.removeArea(4l);
        System.out.println(areaExecution.getState()+"++++++++"+areaExecution.getStateInfo());
    }
    @Test
    public void testGetAreaList(){
        List<Area> areaList =areaService.getAreaList();
        for (Area area:areaList){
            System.out.println(area);
        }
        //根据接口之前定义的key进行删除
        cacheService.removeFromCache(areaService.AREALISTKEY);
//        areaList = areaService.getAreaList();
    }

}
