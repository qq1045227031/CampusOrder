package com.imooc.o2o.web.frontend;

import com.imooc.o2o.dto.UserProductMapExecution;
import com.imooc.o2o.dto.UserShopMapExecution;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.UserShopMap;
import com.imooc.o2o.service.UserShopMapService;
import com.imooc.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/frontend")
public class MyShopPointController {
    @Autowired
    private UserShopMapService userShopMapService;

    @RequestMapping(value = "/listusershopmapsbycustomer",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listUserShopByCustomer(HttpServletRequest request){
        Map<String,Object> modelMap  = new HashMap<>();
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        PersonInfo user = (PersonInfo)request.getSession().getAttribute("user");
        if ((pageIndex>-1)&&(pageSize>-1)&&(user!=null)&&(user.getUserId()!=null)){
            UserShopMap userShopMapCondition  = new UserShopMap();
            userShopMapCondition.setUser(user);
            long shopId = HttpServletRequestUtil.getLong(request,"shopId");
            if (shopId>-1){
                Shop shop = new Shop();
                shop.setShopId(shopId);
                userShopMapCondition.setShop(shop);
            }
            UserShopMapExecution ue = userShopMapService.listUserShopMap(userShopMapCondition,pageIndex,pageSize);
            modelMap.put("userShopMapList",ue.getUserShopMapList());
            modelMap.put("count",ue.getCount());
            modelMap.put("success",true);


        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","空页数或者空UserId");
        }
        return modelMap;

    }

}
