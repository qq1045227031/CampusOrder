package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Award;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class AwardDaoTest extends BaseTest {
    @Autowired
    private AwardDao awardDao;
    @Test
    public void TestInsertAward(){
        Award award = new Award();
        award.setAwardName("奖品2");
        award.setPoint(5);
        award.setAwardDesc("测试奖品2");
        award.setAwardImg("图片2");
        award.setCreateTime(new Date());
        award.setEnableStatus(1);
        award.setShopId(1l);
        award.setLastEditTime(new Date());
        int eff = awardDao.insertAward(award);
        System.out.println(eff);
        Award award2 = new Award();
        award2.setAwardName("奖品3");
        award2.setPoint(5);
        award2.setAwardDesc("测试奖品3");
        award2.setAwardImg("图片3");
        award2.setCreateTime(new Date());
        award2.setEnableStatus(1);
        award2.setShopId(1l);
        award2.setLastEditTime(new Date());
        int eff2 = awardDao.insertAward(award2);
        System.out.println(eff2);
    }

    @Test
    public void testQueryAward() throws Exception{
        Award award = new Award();
        award.setShopId(1l);
        List<Award> awards = awardDao.queryAwardList(award,0,3);
        for (Award aw:awards){
            System.out.println(aw.getAwardName());
        }
//        System.out.println("---------"+awards.size());
//        int count = awardDao.queryAwardCount(award);//查询对应条件的总数
//        System.out.println("---------"+count);
//        award.setAwardName("测试");
//        awards = awardDao.queryAwardList(award,0,3);
//        System.out.println("---------"+awards.size());
//        count = awardDao.queryAwardCount(award);
//        System.out.println("---------"+count);
    }
    @Test
    public void testQueryById(){
        Award award = awardDao.queryAwardByAwardId(1l);
        System.out.println("---------"+award.getAwardName());
    }
    @Test
    public void testUpdate(){
        Award award = awardDao.queryAwardByAwardId(1l);
        award.setAwardName("update之后的奖品1");
        int eff = awardDao.updateAward(award);
        System.out.println("----------"+eff);
    }

    @Test
    public void TestDel(){
        Award award = awardDao.queryAwardByAwardId(1l);
        int eff = awardDao.deleteAward(award.getAwardId(),award.getShopId());
        System.out.println("------"+eff);
    }
}
