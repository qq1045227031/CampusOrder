package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.HeadLine;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeadLineDaoTest extends BaseTest {
    @Autowired
    HeadLineDao headLineDao;
    @Test
    public void TestQuery(){
        List<HeadLine> headLineList=headLineDao.queryHeadLine(new HeadLine());
     for (HeadLine headLine:headLineList){
         System.out.println(headLine.getLineName());
     }
    }
    @Test
    public void TestB(){
        HeadLine headLine = new HeadLine();
        headLine.setCreateTime(new Date());
        headLine.setLastEditTime(new Date());
        headLine.setEnableStatus(1);
        headLine.setLineImg("不知道");
        headLine.setLineName("测试");
        headLine.setLineLink("www.bilibili.com");
        headLine.setPriority(100);
         int eff = headLineDao.insertHeadLine(headLine);
         System.out.println(eff);
    }
    @Test
    public void TestC(){
        HeadLine headLine = new HeadLine();
        headLine.setCreateTime(new Date());
        headLine.setLastEditTime(new Date());
        headLine.setEnableStatus(1);
        headLine.setLineImg("不知道修改");
        headLine.setLineName("测试不知道");
        headLine.setLineLink("www.bilibili.com");
        headLine.setPriority(100);
        int eff = headLineDao.updateHeadLine(headLine);
        System.out.println(eff);
    }
    @Test
    public void TestD(){
        List<Long> list = new ArrayList<>();
        list.add(1l);
        list.add(2l);
        List<HeadLine> headLines = headLineDao.queryHeadLineByIds(list);
        for (HeadLine headLine:headLines){
            System.out.println(headLine.getLineName());
        }
    }
    @Test
    public void TestE(){

        int eff = headLineDao.deleteHeadLine(16l);
        System.out.println(eff);
    }
    @Test
    public void TestF(){

        HeadLine headLine  = headLineDao.queryHeadLineById(17l);
        System.out.println(headLine.getLineName());
        List<Long> list =  new ArrayList<>();
        list.add(17l);
        list.add(18l);
        headLineDao.batchDeleteHeadLine(list);
    }
}
