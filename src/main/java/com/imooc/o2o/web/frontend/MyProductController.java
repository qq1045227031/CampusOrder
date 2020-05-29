package com.imooc.o2o.web.frontend;

import com.imooc.o2o.dto.UserProductMapExecution;
import com.imooc.o2o.entity.*;
import com.imooc.o2o.service.UserAwardMapService;
import com.imooc.o2o.service.UserProductMapService;
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
public class MyProductController {
@Autowired
private UserProductMapService userProductMapService;

    /**
     * 用户查询自己额消费记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/listuserproductmapsbycustomer",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listUserProductMapByCustomer(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String,Object>();
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        PersonInfo user = (PersonInfo)request.getSession().getAttribute("user");
        if ((pageIndex>-1)&&(pageSize>-1)&&(user!=null)){
            UserProductMap userProductMapCondition = new UserProductMap();
            userProductMapCondition.setUser(user);
            long shopId = HttpServletRequestUtil.getLong(request,"shopId");
            if (shopId>-1){
                Shop shop = new Shop();
                shop.setShopId(shopId);
                userProductMapCondition.setShop(shop);
            }
            String productName = HttpServletRequestUtil.getString(request,"productName");
            if (productName!=null){
                Product product = new Product();
                product.setProductName(productName);
                userProductMapCondition.setProduct(product);
            }
            UserProductMapExecution ue = userProductMapService.listUserProductMap(userProductMapCondition,pageIndex,pageSize);
            modelMap.put("success",true);
            modelMap.put("count",ue.getCount());
            modelMap.put("userProductMapList",ue.getUserProductMapList());
        }else {
            modelMap.put("success",false);
            modelMap.put("erMsg","空页数或者空shopId");
        }
return modelMap;

    }
}
