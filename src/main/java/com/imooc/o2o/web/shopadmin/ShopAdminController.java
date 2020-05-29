package com.imooc.o2o.web.shopadmin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/shopadmin")
public class ShopAdminController {
    //视图转发器,专门处理跳转  .这里是跳转到web、web-inf/html/shop/shopoperation
    @RequestMapping(value = "/shopoperation")
    public String shopOperation(){
        return "shop/shopoperation";
    }
    @RequestMapping(value = "/shoplist")
    public String shopList(){
        return "shop/shoplist";
    }
    @RequestMapping(value = "/shopmanagement")
    public String shopManagement(){
        return "shop/shopmanagement";
    }
    @RequestMapping(value = "/productcategorymanage")
    public String productCategoryManage(){
        return "shop/productcategorymanage";
    }
    @RequestMapping(value = "/productoperation")
    public String productOperation(){
        return "shop/productoperation";
    }
    @RequestMapping(value = "/productmanagement")
    public String productManage(){
        return "shop/productmanagement";
    }
    @RequestMapping(value = "/shopauthmanage")
    public String shopAuthManage(){
        return "shop/shopauthmanage";
    }
    @RequestMapping(value = "/shopauthedit")
    public String shopAuthEdit(){
        return "shop/shopauthedit";
    }
    @RequestMapping(value = "/operationfail")
    public String operationFail(){
        return "shop/operationfail";
    }
    @RequestMapping(value = "/operationsuccess")
    public String operationSuccess(){
        return "shop/operationsuccess";
    }
    @RequestMapping(value = "/operationredo")
    public String operationRedo(){
        return "shop/ operationredo";
    }
    @RequestMapping(value = "/productbuycheck")
    public String productBuyCheck(){
        return "shop/productbuycheck";
    }
    @RequestMapping(value = "/usershopcheck")
    public String userShopCheck(){
        return "shop/usershopcheck";
    }
    @RequestMapping(value = "/awarddelivercheck")
    public String awardDeliverCheck(){
        return "shop/awarddelivercheck";
    }
    @RequestMapping(value = "/awardmanage")
    public String awardManage(){
        return "shop/awardmanage";
    }
    @RequestMapping(value = "/awardoperation")
    public String awardOperation(){
        return "shop/awardoperation";
    }
    @RequestMapping(value = "/productshow")
    public String productShow(){
        return "shop/productshow";
    }
    @RequestMapping(value = "/listorder")
    public String listOrder(){
        return "shop/listorder";
    }
}
