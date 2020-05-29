package com.imooc.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.*;
import com.imooc.o2o.entity.*;
import com.imooc.o2o.enums.ProductCategoryStateEnum;
import com.imooc.o2o.enums.ProductStateEnum;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ProductOperationException;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.*;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductService productService;
//需要在session中取currentshop  和传入 页码，一页数量
    @RequestMapping(value = "/getproductlistbyshop", method = RequestMethod.GET)
    @ResponseBody  //    /getproductlistbyshop?pageIndex=0&&pageSize=3
    private Map<String, Object> getProductListByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Shop currentShop = (Shop) request.getSession().getAttribute(
                "currentShop");
        int pageSize =HttpServletRequestUtil.getInt(request,"pageSize");
        int pageIndex =HttpServletRequestUtil.getInt(request,"pageIndex");
        if ((pageIndex>-1)&&(pageSize>-1)&&(currentShop!= null) && (currentShop.getShopId() != null)) {
            long productCategoryId = HttpServletRequestUtil.getLong(request,"productCategoryId");
            String productName = HttpServletRequestUtil.getString(request,"productName");
            Product productCondition = compactProductCondition(currentShop.getShopId(),productCategoryId,productName);
            ProductExecution pe = productService.getProductList(productCondition,pageIndex,pageSize);
            modelMap.put("productList", pe.getProductList());
            modelMap.put("success", true);
            modelMap.put("count",pe.getCount());
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

    private Product compactProductCondition(Long shopId, long productCategoryId, String productName) {
    Product productCondition = new Product();
    Shop shop = new Shop();
        shop.setShopId(shopId);
        if (productCategoryId!=-1l){
            ProductCategory productCategory =new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(productCategory);
        }
        if (productName!=null){
            productCondition.setProductName(productName);
        }
        return productCondition;

    }

    //支持上传商品详情图片的最大数量
    private static final int IMAGEMAXCOUNT = 6;
    @RequestMapping(value = "/modifyproduct", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> ModifyProduct(HttpServletRequest request) {
        //是商品编辑时候   还是调用上下架操作的时候调用
        //弱谴责进行验证码吗判断，厚泽跳过验证码判断
        boolean statusChage = HttpServletRequestUtil.getBoolean(request,"statusChange");

        Map<String, Object> modelMap = new HashMap<String, Object>();
        //验证码效验
        if (!statusChage&&!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        //接受前端的变量初始化，包括商品，缩略图，详情商品列表实体类
        ObjectMapper mapper = new ObjectMapper();
        Product product = null;
        String productStr = HttpServletRequestUtil.getString(request,
                "productStr");
        MultipartHttpServletRequest multipartRequest = null;
        ImageHolder thumbnail = null;
        List<ImageHolder> productImgList = new ArrayList<ImageHolder>();
        //用于判断是否有文件流
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        try {
            //若请求中存在文件流，去除相关的文件包括缩略图和详情图片
            if (multipartResolver.isMultipart(request)) {//平判断请求中是否有文件流
                thumbnail = handlerImage((MultipartHttpServletRequest) request, productImgList);
            }
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        try {
            //尝试将前端传过来的表单string流并将其转化为String实体类O bjectMapper mapper = new ObjectMapper();
            product = mapper.readValue(productStr, Product.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        //？？serive里面详情图片可以为空，这里详情图片不能为空
        if (product != null) {
            try {
                //session获取shopid用与图片文件地址的穿件
                Shop currentShop = (Shop) request.getSession().getAttribute(
                        "currentShop");
                product.setShop(currentShop);
                //执行添加操作
                ProductExecution pe = productService.modifyProduct(product,
                        thumbnail, productImgList);
                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", pe.getStateInfo());
                }
            } catch (ProductOperationException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入商品信息");
        }
        return modelMap;
    }

    private ImageHolder handlerImage(MultipartHttpServletRequest request, List<ImageHolder> productImgList) throws IOException {
        MultipartHttpServletRequest multipartRequest;
        ImageHolder thumbnail;
        multipartRequest = request;
        //去除缩略图并构建ImageHolder对象
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest
                .getFile("thumbnail");
        //这是缩略图的ImageHolder类
        thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        //去除详情图片列表冰构建List<ImageHolder>列表对象，最多支持六张图片上传
        for (int i = 0; i < IMAGEMAXCOUNT; i++) {
            CommonsMultipartFile productImgFile = (CommonsMultipartFile) multipartRequest
                    .getFile("productImg" + i);
            if (productImgFile != null) {
                //取出低i个详情图片，如果不为空，构建其imageHoler加入详情图片列表
                ImageHolder imageHolder = new ImageHolder(productImgFile.getOriginalFilename(), productImgFile.getInputStream());
                productImgList.add(imageHolder);
            } else {
                //如果低i个详情图片文件流为空，则终止循环
                break;
            }
        }
        return thumbnail;
    }

    @RequestMapping(value = "/getproductbyid",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getProductById(@RequestParam Long productId){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if (productId>-1){
           Product product =  productService.getProductById(productId);
           //同时为了方便修改产品种类，还要查出所有的商品种类
            List<ProductCategory> productCategoryList = productCategoryService.getProductCategoryList(product.getShop().getShopId());
            modelMap.put("success",true);
            modelMap.put("product",product);
            modelMap.put("productCategoryList",productCategoryList);

        }else {
            modelMap.put("success",false);
            modelMap.put("errMgs","empty productId");

        }
        return modelMap;
    }
    @RequestMapping(value = "/addproduct", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addProduct(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //验证码效验
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        //接受前端的变量初始化，包括商品，缩略图，详情商品列表实体类
        ObjectMapper mapper = new ObjectMapper();
        Product product = null;
        String productStr = HttpServletRequestUtil.getString(request,
                "productStr");
        MultipartHttpServletRequest multipartRequest = null;
        ImageHolder thumbnail = null;
        List<ImageHolder> productImgList = new ArrayList<ImageHolder>();
        //用于判断是否有文件流
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        try {
            //若请求中存在文件流，去除相关的文件包括缩略图和详情图片
            if (multipartResolver.isMultipart(request)) {//平判断请求中是否有文件流
                thumbnail = handlerImage((MultipartHttpServletRequest) request, productImgList);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "上传图片不能为空");
                return modelMap;
            }
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        try {
            //尝试将前端传过来的表单string流并将其转化为String实体类O bjectMapper mapper = new ObjectMapper();
            product = mapper.readValue(productStr, Product.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        //？？serive里面详情图片可以为空，这里详情图片不能为空
        if (product != null && thumbnail != null && productImgList.size() > 0) {
            try {
                //session获取shopid用与图片文件地址的穿件
                Shop currentShop = (Shop) request.getSession().getAttribute(
                        "currentShop");
                product.setShop(currentShop);
                //执行添加操作
                ProductExecution pe = productService.addProduct(product,
                        thumbnail, productImgList);
                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", pe.getStateInfo());
                }
            } catch (ProductOperationException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入商品信息");
        }
        return modelMap;
    }
    @RequestMapping(value = "/removeproductcategory",method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> removeProductCategory(Long productCategoryId,HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
             if (productCategoryId!=null&&productCategoryId>0){//如果有shopid
            try {
                Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
                ProductCategoryExecution pe =productCategoryService.deleteProductCategory(productCategoryId,currentShop.getShopId());
                if (pe.getState()==ProductCategoryStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg",pe.getStateInfo());
                }
            }catch (RuntimeException e){
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
                return modelMap;
            }

        }else {//如果没有shopid
            modelMap.put("success",false);
            modelMap.put("errMsg","请至少删除一个商品类别");
        }
        return modelMap;
    }
    /**
     * 前台创建productcateoory
     * @param productCategoryList
     * @param request
     * @return success     errMsg
     */
    @RequestMapping(value = "/addproductcategorys",method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> addProductCategorys(@RequestBody List<ProductCategory> productCategoryList,HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();

        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        long id = currentShop.getShopId();
        for (ProductCategory productCategory:productCategoryList){
            productCategory.setShopId(currentShop.getShopId());
        }
        if(productCategoryList!=null&&productCategoryList.size()>0){
            try{
           ProductCategoryExecution pe =  productCategoryService.batchAddProductCategory(productCategoryList);
           if (pe.getState()==ProductCategoryStateEnum.SUCCESS.getState())
           {
               modelMap.put("success",true);
           }else {
               modelMap.put("success",false);
               modelMap.put("errMsg",pe.getStateInfo());
           }
        }catch (RuntimeException e){
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请至少输入一个商品了别");
        }
        return  modelMap;
    }

    //通过shopid查找这个店铺的商品分类
//    @ResponseBody
//    @RequestMapping(value = "/getproductcategory",method = RequestMethod.GET)
//    //用鱼判断是否有用户进行操作，有就能进行shoplist更改，没有则不行
//    private Map<String,Object> getproductcategory(HttpServletRequest request){
//        Shop shop = new Shop();
//        shop.setShopId(1l);//没有开发，先设定为1,
//        request.getSession().setAttribute("currentShop",shop);
//        //将其存在session，其实进入店铺的时候就应该把这个shop存在session中了，但是没有实现默认其为用户1
//
//        Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");//从session中获取shop，这个shop应该在manage的执行ajax请求时候，对应controller下保存了
//        Map<String,Object> modelMap = new HashMap<String, Object>();
//
////        long shopId = HttpServletRequestUtil.getLong(request,"shopId");//不是通过传入id而应该从session中获取shop来查
////        List<ProductCategory> productCategoryList = productCategoryService.getProductCategoryList(shopId);
//
//        List<ProductCategory> productCategoryList = null;
//        if (currentShop!=null&&currentShop.getShopId()>0)//判断session是否有shop并查其id是否合法
//        {
//            productCategoryList = productCategoryService.getProductCategoryList(currentShop.getShopId());
//            modelMap.put("productCategoryList",productCategoryList);
//            modelMap.put("success",true);
//        }else
//        {
//            ProductCategoryStateEnum ps = ProductCategoryStateEnum.INNER_ERROR;
//            modelMap.put("success",false);
//        }
////        modelMap.put("productCategoryList",productCategoryList);
//        return modelMap;
//    }

    //通过session获取shop查找这个店铺的商品分类
    @ResponseBody
    @RequestMapping(value = "/getproductcategory",method = RequestMethod.GET)
    //用鱼判断是否有用户进行操作，有就能进行shoplist更改，没有则不行
    private Result<List<ProductCategory>> getproductcategory(HttpServletRequest request){
//        Shop shop = new Shop();
//        shop.setShopId(1l);//没有开发，先设定为1,
//        request.getSession().setAttribute("currentShop",shop);
//        //将其存在session，其实进入店铺管理的manage发送ajax请求的controller就已经设置了所以可以删除
        Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");//从session中获取shop，这个shop应该在manage的执行ajax请求时候，对应controller下保存了
        Map<String,Object> modelMap = new HashMap<String, Object>();
       List<ProductCategory> list = null;
        if (currentShop!=null&&currentShop.getShopId()>0){//判断session是否有shop并查其id是否合法
            list=productCategoryService.getProductCategoryList(currentShop.getShopId());
            return new Result<List<ProductCategory>>(true,list);
        }else {
            ProductCategoryStateEnum ps = ProductCategoryStateEnum.INNER_ERROR;
            return  new Result<List<ProductCategory>>(false,ps.getState(),ps.getStateInfo());
        }
    }

    //能不能在这里session存入shopid?
    @ResponseBody
    @RequestMapping(value = "/getshopmanagementinfo",method = RequestMethod.GET)
    private Map<String,Object> getShopManagementInfo(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        long shopId = HttpServletRequestUtil.getLong(request,"shopId");
        if (shopId<=0){//先检测session是否有shopId
            Object currentShopObj = request.getSession().getAttribute("currentShop");
            if (currentShopObj==null){//在检测session是有有shopcurrent这个sho对象
                //没有就不允许操作，直接重定向到商铺列表
                modelMap.put("redirect",true);
                modelMap.put("url","/shopadmin/shoplist");//??这里是哪里啊啊啊
                modelMap.put("msg","没有shopid和shop");
            }else {//有currentShop就把shopId存进session去
                Shop currentShop = (Shop)currentShopObj;
                modelMap.put("redirect",false);
                modelMap.put("shopId",currentShop.getShopId());
                modelMap.put("msg", "有shop");
            }

        }else {
            Shop currentShop = new Shop();
            currentShop.setShopId(shopId);
            request.getSession().setAttribute("currentShop",currentShop);//将shop存在session中
            modelMap.put("redirect",false);//如果有id则不需要冲顶先
            modelMap.put("msg","有shopid");
        }
        return modelMap;
    }
//获取该用户的所有店铺信息
    @ResponseBody
    @RequestMapping(value = "/getshoplist",method = RequestMethod.GET)
    private Map<String,Object> getShopList(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
//        PersonInfo user = new PersonInfo();
//        user.setUserId(1L);
//        user.setName("测试");
//        request.getSession().setAttribute("user",user);
        PersonInfo user = (PersonInfo)request.getSession().getAttribute("user");
//        if (user!=null) {//不判断会因为没有查询条件查出所有list  这里已配置拦截器，所以可以忽略
            try {
                Shop shop = new Shop();
                shop.setOwner(user);
                ShopExecution se = shopService.getShopList(shop, 1, 100);
                modelMap.put("user", user);
                modelMap.put("shopList", se.getShopList());
                request.getSession().setAttribute("shopList",se.getShopList());
                modelMap.put("success", true);
            } catch (Exception e) {
                modelMap.put("success", false);
                modelMap.put("errorMsg", e.getMessage());
            }
//        } else {//
//            modelMap.put("success", false);//
//            modelMap.put("errorMsg", "没有登陆用户不可访问");//
//        }//
        return modelMap;
    }
//根据前台穿过来的id返回其用户信息，和所有区域信息（区域信息方便修改）
    @RequestMapping(value = "/getshopbyid",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopById(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        Long shopId = HttpServletRequestUtil.getLong(request,"shopId");//获取前台传过来的id
        if (shopId>0){//如果有shopid
            try {
                Shop shop = shopService.getByShopId(shopId);
                List<Area> areaList = areaService.getAreaList();//输出他是为了方便前台进行区域更改，而ownner和shopcategory是不希望用户随意更改的所以不返回给前台
                modelMap.put("shop",shop);
                modelMap.put("area",areaList);
                modelMap.put("success",true);
            }catch (Exception e){
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
            }

        }else {//如果没有shopid
            modelMap.put("success",false);
            modelMap.put("errMsg","empty shopId");
        }
        return modelMap;
    }
    @ResponseBody
    @RequestMapping(value = "/modifyshop",method = RequestMethod.POST)//
    private Map<String, Object> modifyShop(HttpServletRequest request){
        System.out.println("执行了registershopcontroller");
        Map<String,Object> modelMap = new HashMap<String, Object>();
//        modelMap.put("ceshi","进入了register方法");//为空，方法根本没进来啊！
        //首先验证码判断
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","验证码错误");
            return modelMap;
        }
        //1接收并转化相应的参数，包括店铺信息以及图片,这里要求前端穿过来一个名字为shopStr的json数组，因为是json所以用String接收其本质是一个shop对象
        String shopStr = HttpServletRequestUtil.getString(request,"shopStr");
        ObjectMapper mapper = new ObjectMapper();//这是jackson-databind包的方法，y用于将前端json数组转变化为对象
        Shop shop = null;
        try{
            shop = mapper.readValue(shopStr,Shop.class);
        }catch (Exception e){
            modelMap.put("success",false);//如果错误，返回前台一个错误信息
            modelMap.put("errMsg","转化shop对象失败"+e.getMessage());
            return modelMap;//类似request.setAttribute ModelMap对象主要用于传递控制方法处理数据到结果页面，也就是说我们把结果页面上需要的数据放到ModelMap对象中即可，他的作用类似于request对象的setAttribute方法的作用，用来在一个请求过程中传递处理的数据。
        }
        //处理图片
        // 创建一个通用的多部分解析器
        CommonsMultipartFile shopImg = null;
        MultipartHttpServletRequest multipartRequest = null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        // 设置编码
//        commonsMultipartResolver.setDefaultEncoding("utf-8");
        // 判断 request 是否有文件上传,即多部分请求...
        if (commonsMultipartResolver.isMultipart(request)){
            // 转换成多部分request
            multipartRequest=(MultipartHttpServletRequest) request;
            // shopImg 是指 文件上传标签的 name=值
            // 根据 name 获取上传的文件..
            shopImg = (CommonsMultipartFile)multipartRequest.getFile("shopImg");
        }//图片可上传也可不上传
        //2.修改店铺
        if (shop!=null&&shop.getShopId()!=null)
        {
            ShopExecution se;
            try {
                if (shopImg==null){
                    se = shopService.modifyShop(shop,null);
                }else {
                    ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(),shopImg.getInputStream());
                    se = shopService.modifyShop(shop,imageHolder);
                }

                if (se.getState() == ShopStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                }else{
                    modelMap.put("success",false);
                    modelMap.put("success","shopservice插入用户失败"+se.getStateInfo());
                }
            } catch (ShopOperationException e){
                modelMap.put("success",false);
                modelMap.put("errMsg","shopservice插入用户失败"+e.getMessage());
            }catch (IOException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg","shopservice插入用户失败"+e.getMessage());
            }
            //这里的shopImg是CommonsMultipartFile类型不是File类型，其核难转化为FIle类型，
            // 但是inputStream却很好转化为File类型,所以这里通过CommonsMultipartFile获取imputSteam转化为file来传入图片
            return modelMap;
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入店铺信息id");
            return modelMap;
        }
    }

    @RequestMapping(value = "/getshopinitinfo",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopInintInfo(){
        Map<String,Object> modelMap = new HashMap<>();
        List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
        List<Area> areaList = new ArrayList<Area>();
        try{
            shopCategoryList = shopCategoryService.getShopCategoryList(null);
            areaList = areaService.getAreaList();
            modelMap.put("shopCategoryList",shopCategoryList);
            modelMap.put("areaList",areaList);
            modelMap.put("success",true);
        }catch (Exception e){
             modelMap.put("success",false);
             modelMap.put("errMsg",e.getMessage());
        }
//        modelMap.put("ceshi","调用了init方法");//可行
        return modelMap;

    }
    //注册商铺
    @ResponseBody
    @RequestMapping(value = "/registershop",method = RequestMethod.POST)//
    private Map<String, Object> registerShop(HttpServletRequest request){
        System.out.println("执行了registershopcontroller");
        Map<String,Object> modelMap = new HashMap<String, Object>();
//        modelMap.put("ceshi","进入了register方法");//为空，方法根本没进来啊！
        //首先验证码判断
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","验证码错误");
            return modelMap;
        }
        //1接收并转化相应的参数，包括店铺信息以及图片,这里要求前端穿过来一个名字为shopStr的json数组，因为是json所以用String接收其本质是一个shop对象
        String shopStr = HttpServletRequestUtil.getString(request,"shopStr");
        ObjectMapper mapper = new ObjectMapper();//这是jackson-databind包的方法，y用于将前端json数组转变化为对象
        Shop shop = null;
        try{
            shop = mapper.readValue(shopStr,Shop.class);
        }catch (Exception e){
            modelMap.put("success",false);//如果错误，返回前台一个错误信息
            modelMap.put("errMsg","转化shop对象失败"+e.getMessage());
            return modelMap;//类似request.setAttribute ModelMap对象主要用于传递控制方法处理数据到结果页面，也就是说我们把结果页面上需要的数据放到ModelMap对象中即可，他的作用类似于request对象的setAttribute方法的作用，用来在一个请求过程中传递处理的数据。
        }
        //处理图片
        // 创建一个通用的多部分解析器
        CommonsMultipartFile shopImg = null;
        MultipartHttpServletRequest multipartRequest = null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        // 设置编码
//        commonsMultipartResolver.setDefaultEncoding("utf-8");
        // 判断 request 是否有文件上传,即多部分请求...
        if (commonsMultipartResolver.isMultipart(request)){
            // 转换成多部分request
            multipartRequest=(MultipartHttpServletRequest) request;
            // shopImg 是指 文件上传标签的 name=值
            // 根据 name 获取上传的文件..
            shopImg = (CommonsMultipartFile)multipartRequest.getFile("shopImg");
//            //可以通过 file.transferTo(imageFile) file是MultipartFile，imageFile是File类型,
//            // 上传后记录的文件...
//            File imgeFile = new File("fileName");
//            //上传
//            shopImg.transferTo(imgeFile);
        }else {//如果强制要求上传图片，则删除else这段
            modelMap.put("success",false);
            modelMap.put("errMsg","上传图片不能为空");
            return modelMap;
        }
        //2.注册店铺
        if (shop!=null&&shopImg!=null){
            PersonInfo owner = (PersonInfo) request.getSession().getAttribute("user");//从sesson中取得用户
//            PersonInfo owner = new PersonInfo();
            shop.setOwner(owner);
//            File shopImgFile = new File(PathUtil.getImgBasePath()+ ImageUtil.getRandomFileName());//随便写的
//            try {
//                shopImgFile.createNewFile();//创建这个文件用于File形式保存图片
//            } catch (IOException e) {
//                modelMap.put("success",false);//如果错误，返回前台一个错误信息
//                modelMap.put("errMsg",e.getMessage());
//                return modelMap;
//            }
//            try {
//                inputStreamToFile(shopImg.getInputStream(),shopImgFile);
//            } catch (IOException e) {
//                modelMap.put("success",false);//如果错误，返回前台一个错误信息
//                modelMap.put("errMsg",e.getMessage());
//                return modelMap;
//            }
            ShopExecution se = null;
            try {
                ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(),shopImg.getInputStream());
                se = shopService.addShop(shop,imageHolder);
                if (se.getState() == ShopStateEnum.CHECK.getState()){
                    modelMap.put("success",true);
                    //用户可以操作的店铺列表
                    @SuppressWarnings("unchecked")
                    List<Shop> shopList = (List<Shop>)request.getSession().getAttribute("shopList");
                    if (shopList==null||shopList.size()==0){//如果为空
                        shopList = new ArrayList<Shop>();
                    }
                    shopList.add(se.getShop());
                    request.getSession().setAttribute("shopList",shopList);
                }else{
                    modelMap.put("success",false);
                    modelMap.put("success","shopservice插入用户失败"+se.getStateInfo());
                }
            } catch (ShopOperationException e){
                modelMap.put("success",false);
                modelMap.put("errMsg","shopservice插入用户失败"+e.getMessage());
            }catch (IOException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg","shopservice插入用户失败"+e.getMessage());
            }
            //这里的shopImg是CommonsMultipartFile类型不是File类型，其核难转化为FIle类型，
            // 但是inputStream却很好转化为File类型,所以这里通过CommonsMultipartFile获取imputSteam转化为file来传入图片

            return modelMap;
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入店铺信息");
            return modelMap;
        }

        //3.返回结果

        //将inputSteam转化为file
    }
//    private static void inputStreamToFile(InputStream ins,File file){
//        FileOutputStream os = null ;//通过OutputSteam将其写入到file中
//        try {
//            os = new FileOutputStream(file);
//            int bytesRead = 0;
//            byte[] buffer = new byte[1024];
//            while ((bytesRead=ins.read(buffer))!=-1){
//                os.write(buffer,0,bytesRead);
//            }
//        }catch (Exception e){
//            throw new RuntimeException("调用inputSteamToFile产生异常"+e.getMessage());
//        }finally {
//            try {
//                if (ins != null) {
//                    ins.close();
//                }
//                if (os!=null){
//                    os.close();
//                }
//            }catch (IOException e){
//                throw new RuntimeException("调用inputSteamToFile关闭产生异常"+e.getMessage());
//            }
//
//        }
//    }
}

