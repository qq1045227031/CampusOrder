package com.imooc.o2o.dao;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.FShop;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.util.ShortNetAddressUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//extends BaseTest继承为了加载spring的配置文件，因为继承了他，类似用了@component，可以使用@Autowired
public class AreaDaoTest extends BaseTest {
   @Autowired
    private AreaDao areaDao;
   @Autowired
   private ShopDao shopDao;
   @Test
    public void testQueryArea(){
       List<Area> areaList = areaDao.queryArea();
       for (Area area:areaList){
           System.out.println(area);
       }
   }
   @Test
   public void testDate(){
       Date date = new Date();
       SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String time = formatter.format(date);
       System.out.println(time);
   }
   @Test
   public void testFshop(){
       Shop shop= shopDao.queryByShopId(1l);
       System.out.println(shop.getShopAddr());
       FShop fShop= new FShop(shop);
       System.out.println(fShop.getShopAddr());
   }
    @Test
    public void testFshop2(){
        Shop shop= shopDao.queryByShopId(1l);
        System.out.println(shop.getShopAddr());
        FShop fShop= new FShop(shop);
        System.out.println(fShop.getShopAddr());
        Shop shopt = new Shop(fShop);
        System.out.println(shopt.getShopName()+"++++");
    }
   @Test
    public void test(){
//       String s = ShortNetAddressUtil.getShortURL("https://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login");
//       System.out.println(s);
       String str = "\"1\"";
       System.out.println(str);
       str= str.replace("\"", "");
       System.out.println(str);
   }
    @Test
    public void testC(){
       Area area = new Area();
       area.setAreaName("测试");
       area.setPriority(50);
       area.setLastEditTime(new Date());
        int  eff = areaDao.insertArea(area);
        System.out.println(eff);
    }
    @Test
    public void testUpdate(){
        Area area = new Area();
        area.setAreaId(4l);
        area.setAreaName("测试修改");
        area.setPriority(50);
        area.setLastEditTime(new Date());
        int  eff = areaDao.updateArea(area);
        System.out.println(eff);
    }
    @Test
    public void testDel(){
//        int  eff = areaDao.deleteArea(4l);
//        System.out.println(eff);
        List<Long> list = new ArrayList<>();
        list.add(5l);
        int eff = areaDao.batchDeleteArea(list);
        System.out.println(eff);
    }

}
