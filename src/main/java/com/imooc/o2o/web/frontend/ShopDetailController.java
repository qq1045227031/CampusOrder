package com.imooc.o2o.web.frontend;

import com.imooc.o2o.dao.ProductImgDao;
import com.imooc.o2o.dto.*;
import com.imooc.o2o.entity.*;
import com.imooc.o2o.enums.UserAwardMapStateEnum;
import com.imooc.o2o.service.*;
import com.imooc.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/frontend")
public class ShopDetailController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private UserAwardMapService userAwardMapService;
    @Autowired
    private UserShopMapService userShopMapService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private ProductImgSerivce productImgSerivce;
    @Autowired
    private UserProductMapService userProductMapService;

//    private static String URLPREFIX = "https://open.weixin.qq.com/connect/oauth2/authorize?"
//            + "appid=wxd7f6c5b8899fba83&"
//            + "redirect_uri=120.26.178.72springboot/shop/adduserproductmap&"
//            + "response_type=code&scope=snsapi_userinfo&state=";
//    private static String URLSUFFIX = "#wechat_redirect";

//    @RequestMapping(value = "/generateqrcode4product", method = RequestMethod.GET)
//    @ResponseBody
//    private void generateQRCode4Product(HttpServletRequest request,
//                                        HttpServletResponse response) {
//        long productId = HttpServletRequestUtil.getLong(request, "productId");
//        PersonInfo user = (PersonInfo) request.getSession()
//                .getAttribute("user");
//        if (productId != -1 && user != null && user.getUserId() != null) {
//            long timpStamp = System.currentTimeMillis();
//            String content = "{\"productId\":" + productId + ",\"customerId\":"
//                    + user.getUserId() + ",\"createTime\":" + timpStamp + "}";
//            String longUrl = URLPREFIX + content + URLSUFFIX;
//            String shortUrl = ShortNetAddress.getShortURL(longUrl);
//            BitMatrix qRcodeImg = QRCodeUtil.generateQRCodeStream(shortUrl,
//                    response);
//            try {
//                MatrixToImageWriter.writeToStream(qRcodeImg, "png",
//                        response.getOutputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @RequestMapping(value = "/listawardsbyshop",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listAwardsByShopId(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        long shopId = HttpServletRequestUtil.getLong(request,"shopId");//shopId接受不到?
        if ((pageIndex>-1)&&(pageSize>-1)&&(shopId>-1)){
            String awardName = HttpServletRequestUtil.getString(request,"awardName");
            Award awardCondition = compactAwardCondition4Search(shopId,awardName);
            //传入查询条件分页展示
            AwardExecution ae = awardService.getAwardList(awardCondition,pageIndex,pageSize);
            modelMap.put("success",true);
            modelMap.put("count",ae.getCount());
            modelMap.put("awardList",ae.getAwardList());
            PersonInfo user = (PersonInfo)request.getSession().getAttribute("user");
            if (user!=null&user.getUserId()!=null){
                UserShopMap userShopMap = userShopMapService.getUserShopMap(user.getUserId(),shopId);
                if (userShopMap==null){
                    modelMap.put("totalPoint",0);
                }else {
                    modelMap.put("totalPoint",userShopMap.getPoint());
                }
            }
        }else{
            modelMap.put("success",false);
            modelMap.put("errMsg","传入了异常的shopId");
        }
        return modelMap;
    }
    private Award compactAwardCondition4Search(long shopId, String awardName) {
        Award awardCondition = new Award();
        awardCondition.setShopId(shopId);
        if (awardName != null) {
            awardCondition.setAwardName(awardName);
        }
        return awardCondition;
    }
    @ResponseBody
    @RequestMapping(value = "/adduserawardmap",method = RequestMethod.GET)
    private Map<String,Object> addUserAwardMap(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        PersonInfo user = (PersonInfo)request.getSession().getAttribute("user");
        Long awardId = HttpServletRequestUtil.getLong(request,"awardId");
        UserAwardMap userAwardMap = compactUserAwardMap4Add(user,awardId);
        Award award = awardService.getAwardById(awardId);
        userAwardMap.setPoint(award.getPoint());
        Shop shop = new Shop();
        shop.setShopId(award.getShopId());
        userAwardMap.setShop(shop);
        if (userAwardMap!=null){
            try {
                //添加兑换信息
                UserAwardMapExecution result = userAwardMapService.addUserAwardMap(userAwardMap);
                if (result.getCount() == UserAwardMapStateEnum.SUCCESS.getState()) {//UserAwardMapStateEnum.SUCCESS.getState() 为什么会出现runtime异常
                    modelMap.put("success",false);
                    modelMap.put("errMsg",result.toString());
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", result.getStateInfo());
                }
            }catch (RuntimeException e){
//                modelMap.put("success",false);
//                modelMap.put("errMsg",e.toString());
                modelMap.put("success",true);
            }


        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请选择你的奖品");
        }
        return modelMap;
    }

    private UserAwardMap compactUserAwardMap4Add(PersonInfo user, Long awardId) {
        UserAwardMap userAwardMap = new UserAwardMap();
        user.setUserId(user.getUserId());
        userAwardMap.setUser(user);
        if (awardId!=null){
            Award award = new Award();
            award.setAwardId(awardId);
            userAwardMap.setAward(award);
        }
        return userAwardMap;
    }

    /**
     * 商品详情信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/listproductdetailpageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listProductDetailPageInfo(
            HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        long productId = HttpServletRequestUtil.getLong(request, "productId");
        Product product = null;
        if (productId != -1) {
            product = productService.getProductById(productId);
            PersonInfo user = (PersonInfo)request.getSession().getAttribute("user");
            List<ProductImg> productImgList=productImgSerivce.queryProductImgList(productId);
            if (user == null) {
                modelMap.put("needQRCode",false);
            }else {
                modelMap.put("needQRCode",true);
            }
            modelMap.put("product", product);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty productId");
        }
        return modelMap;
    }
    @RequestMapping(value = "/adduserproductmap",method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> addUserProductMap(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        PersonInfo user = (PersonInfo)request.getSession().getAttribute("user");
        long productId = HttpServletRequestUtil.getLong(request,"productId");
        UserProductMap userProductMap = new UserProductMap();
        Product product = productService.getProductById(productId);
        if (user==null){
            modelMap.put("success",false);
            modelMap.put("errMsg","请登录");
            return modelMap;
        }
        if ((user.getUserId()>0)&&(productId>0)&&product!=null){
            userProductMap.setUser(user);
            userProductMap.setProduct(product);
            userProductMap.setPoint(product.getPoint());
            userProductMap.setCreateTime(new Date());
            Shop shop = new Shop();
            shop.setShopId(product.getShop().getShopId());
            userProductMap.setShop(shop);
            PersonInfo personInfo = new PersonInfo();
            personInfo.setUserId(1l);
            userProductMap.setOperator(personInfo);
            try {
                UserProductMapExecution userProductMapExecution = userProductMapService.addUserProductMap(userProductMap);
                if (userProductMapExecution.getState()==UserProductMapStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg","创建订单失败");
                }
                return modelMap;
            }catch (Exception e){
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
                return modelMap;
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","没登录或者产品不对");
            return modelMap;
        }

    }
    /**
     * 获取该商铺的信息和商品种类信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/listshopdetailpageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listShopDetailPageInfo(
            HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        Shop shop = null;
        List<ProductCategory> productCategoryList = null;
        if (shopId != -1) {
            shop = shopService.getByShopId(shopId);
            productCategoryList = productCategoryService.getProductCategoryList(shopId);
            modelMap.put("shop", shop);
            modelMap.put("productCategoryList", productCategoryList);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty shopId");
        }
        return modelMap;
    }
    @RequestMapping(value = "/listproductsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listProductsByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        if ((pageIndex > -1) && (pageSize > -1) && (shopId > -1)) {//这三个必须有
            //下面的可能有，根据用户的查找需要
            long productCategoryId = HttpServletRequestUtil.getLong(request,
                    "productCategoryId");
            String productName = HttpServletRequestUtil.getString(request,
                    "productName");
            Product productCondition = compactProductCondition4Search(shopId,
                    productCategoryId, productName);
            ProductExecution pe = productService.getProductList(
                    productCondition, pageIndex, pageSize);
            modelMap.put("productList", pe.getProductList());
            modelMap.put("count", pe.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

    private Product compactProductCondition4Search(long shopId,
                                                   long productCategoryId, String productName) {
        Product productCondition = new Product();
        Shop shop = new Shop();
        shop.setShopId(shopId);
        productCondition.setShop(shop);
        if (productCategoryId != -1L) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(productCategory);
        }
        if (productName != null) {
            productCondition.setProductName(productName);
        }
        productCondition.setEnableStatus(1);//这是传给前端的诗句，要是1 前端才能展示
        return productCondition;
    }


}
