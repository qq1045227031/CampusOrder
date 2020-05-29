package com.imooc.o2o.web.shopadmin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.*;
import com.imooc.o2o.entity.*;
import com.imooc.o2o.enums.AwardStateEnum;
import com.imooc.o2o.service.*;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.wechat.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


@Controller
@RequestMapping("/shopadmin")
public class UserProductManagementController {
    @Autowired
    private PersonInfoService personInfoService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ShopAuthMapService shopAuthMapService;
    @Autowired
    private UserProductMapService userProductMapService;
    @Autowired
    private ProductSellDailyService productSellDailyService;
    @Autowired
    private UserShopMapService userShopMapService;
    @Autowired
    private UserAwardMapService userAwardMapService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private WechatAuthService wechatAuthService;
    private static final int IMAGEMAXCOUNT = 6;

    /**
     * 预览商铺的详情
     * @return
     * @throws UnsupportedEncodingException
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

    @RequestMapping(value = "aduserproductmap",method = RequestMethod.GET)
    private String adUserProductMap(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

            //获取二维码里的state携带的content并解码
            String qrCodeinfo = new String(URLDecoder.decode(HttpServletRequestUtil.getString(request,"state"),"UTF-8"));
            ObjectMapper mapper=  new ObjectMapper();
            WechatInfo wechatInfo = null;
            try {
                //转化为对象
                wechatInfo = mapper.readValue(qrCodeinfo.replace("aaa","\""),WechatInfo.class);
            }catch (Exception e){
                return "shop/operationfail";
            }
            if (!WechatUtil.checkQRCodeInfo(wechatInfo)){//检验二维码是否过期
                return "shop/operationfail";
            }
            Long productId =wechatInfo.getProductId();
            Long customerId = wechatInfo.getCustomerId();
            UserProductMap userProductMap =new UserProductMap();
            if (productId!=null&&customerId!=null&&(productId>0)&&(customerId>0)){
                PersonInfo customer = new PersonInfo();
                customer.setUserId(customerId);
                userProductMap.setUser(customer);
                Product product= productService.getProductById(productId);
                userProductMap.setProduct(product);
                userProductMap.setShop(product.getShop());
                userProductMap.setPoint(product.getPoint());
                PersonInfo personInfo = new PersonInfo();
                personInfo.setUserId(6l);
                userProductMap.setOperator(personInfo);
                userProductMap.setCreateTime(new Date());
            }else {
                return "shop/operationfail";
            }
            if (userProductMap!=null&&customerId!=-1){
                try {
                    //增加消费记录
                    UserProductMapExecution se = userProductMapService.addUserProductMap(userProductMap);
                    if (se.getState()==UserProductMapStateEnum.SUCCESS.getState()){
                        return "shop/operationsuccess";
                    }
                }catch (RuntimeException e){
                    return "shop/operationfail";
                }
        }
        return "shop/operationfail";
    }
//店员扫描二维码后 客户支付成功增加积分
    //创建订单的controller
@RequestMapping(value = "adduserproductmap",method = RequestMethod.GET)
private String addUserProductMap(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
    WechatAuth auth = getOperatorInfo(request);//操作员信息
    if (auth!=null){
        PersonInfo operator = auth.getPersonInfo();
        request.getSession().setAttribute("user",operator);
        //获取二维码里的state携带的content并解码
        String qrCodeinfo = new String(URLDecoder.decode(HttpServletRequestUtil.getString(request,"state"),"UTF-8"));
        ObjectMapper mapper=  new ObjectMapper();
        WechatInfo wechatInfo = null;
        try {
            //转化为对象
            wechatInfo = mapper.readValue(qrCodeinfo.replace("aaa","\""),WechatInfo.class);
        }catch (Exception e){
            return "shop/operationfail";
        }
        if (!WechatUtil.checkQRCodeInfo(wechatInfo)){//检验二维码是否过期
            return "shop/operationfail";
        }
        Long productId =wechatInfo.getProductId();
        Long customerId = wechatInfo.getCustomerId();
        UserProductMap userProductMap =new UserProductMap();
        if (productId!=null&&customerId!=null&&(productId>0)&&(customerId>0)){
            PersonInfo customer = new PersonInfo();
            customer.setUserId(customerId);
            userProductMap.setUser(customer);
            Product product= productService.getProductById(productId);
            userProductMap.setProduct(product);
            userProductMap.setShop(product.getShop());
            userProductMap.setPoint(product.getPoint());
            userProductMap.setOperator(auth.getPersonInfo());
            userProductMap.setCreateTime(new Date());
        }else {
            return "shop/operationfail";
        }
        if (userProductMap!=null&&customerId!=-1){
            try {
                //监测操作员是否有权限
                if (!checkShopAuth(operator.getUserId(),userProductMap)){
                    return "shop/operationfail";
                }
                //增加消费记录
                UserProductMapExecution se = userProductMapService.addUserProductMap(userProductMap);
                if (se.getState()==UserProductMapStateEnum.SUCCESS.getState()){
                    return "shop/operationsuccess";
                }
            }catch (RuntimeException e){
                return "shop/operationfail";
            }

        }else {
            return "shop/operationfail";
        }
    }
    return "shop/operationfail";
}
private boolean checkShopAuth(long userId,UserProductMap userProductMap){
    //获取改店铺的所有授权信息
    ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService.listShopAuthMapByShopId(userProductMap.getShop().getShopId(),1,100);
    for (ShopAuthMap shopAuthMap:shopAuthMapExecution.getShopAuthMapList()){
        if (shopAuthMap.getEmployee().getUserId()==userId){
            return true;
        }
    }
    return false;
}
//    private boolean checkQRCodeInfo(WechatInfo wechatInfo) {
//        if (wechatInfo != null && wechatInfo.getShopId() != null && wechatInfo.getCreateTime() != null) {
//            long nowTime = System.currentTimeMillis();
//            if ((nowTime - wechatInfo.getCreateTime())<=600000){
//                return true;
//            }else {
//                return false;
//            }
//        }else {
//            return false;
//        }
//    }
private WechatAuth getOperatorInfo(HttpServletRequest request){
    String code = request.getParameter("code");
    WechatAuth auth = null;
    if (null!=code){
        UserAccessToken token;
        try{
            token = WechatUtil.getUserAccessToken(code);
            String openId = token.getOpenId();
            request.getSession().setAttribute("openId",openId);
            auth = wechatAuthService.getWechatAuthByOpenId(openId);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    return auth;
}
    @RequestMapping(value = "/getawardbyid", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getAwardbyId(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        long awardId = HttpServletRequestUtil.getLong(request, "awardId");
        if (awardId > -1) {
            Award award = awardService.getAwardById(awardId);
            modelMap.put("award", award);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty awardId");
        }
        return modelMap;
    }
    @RequestMapping(value = "/addaward", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addAward(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        ObjectMapper mapper = new ObjectMapper();
        Award award = null;
        String awardStr = HttpServletRequestUtil.getString(request, "awardStr");
        ImageHolder thumbnail = null;
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        try {
            if (multipartResolver.isMultipart(request)) {
            thumbnail = handlerImage(request,thumbnail);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "上传图片不能为空");
            return modelMap;
            }
        }catch (Exception e){
            modelMap.put("success", false);
            modelMap.put("errMsg", "thumbnail获取失败"+e.toString());
            return modelMap;
        }
        try {
            award = mapper.readValue(awardStr, Award.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        if (award != null && thumbnail != null) {
            try {
                Shop currentShop = (Shop) request.getSession().getAttribute(
                        "currentShop");
                award.setShopId(currentShop.getShopId());
                AwardExecution ae = awardService.addAward(award, thumbnail);
                if (ae.getState() == AwardStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", ae.getStateInfo());
                }
            } catch (RuntimeException e) {
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
    /**
     * 查询该店铺下所有的奖品信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/listawardsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listAwardsByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        Shop currentShop = (Shop) request.getSession().getAttribute(
                "currentShop");
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null)
                && (currentShop.getShopId() != null)) {
            String awardName = HttpServletRequestUtil.getString(request,
                    "awardName");
            Award awardCondition = compactAwardCondition4Search(currentShop.getShopId(), awardName);
            AwardExecution ae = awardService.getAwardList(awardCondition,
                    pageIndex, pageSize);
            modelMap.put("awardList", ae.getAwardList());
            modelMap.put("count", ae.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
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

    /**
     * 店铺修改奖品信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/modifyaward", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> modifyAward(HttpServletRequest request) {
        boolean statusChange = HttpServletRequestUtil.getBoolean(request,
                "statusChange");
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        ObjectMapper mapper = new ObjectMapper();
        Award award = null;
//        MultipartHttpServletRequest multipartRequest = null;
        ImageHolder thumbnail = null;
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
//        if (multipartResolver.isMultipart(request)) {
//            multipartRequest = (MultipartHttpServletRequest) request;
//            thumbnail = (CommonsMultipartFile) multipartRequest
//                    .getFile("thumbnail");
//        }
        try {
            if (multipartResolver.isMultipart(request)){
                thumbnail = handlerImage(request,thumbnail);
            }
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("cerMsg","请上传图片");
            return modelMap;
        }
        try {
            String awardStr = HttpServletRequestUtil.getString(request, "awardStr");
            award = mapper.readValue(awardStr, Award.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        if (award != null) {
            try {
                Shop currentShop = (Shop) request.getSession().getAttribute(
                        "currentShop");
                award.setShopId(currentShop.getShopId());
                AwardExecution pe = awardService.modifyAward(award, thumbnail);
                if (pe.getState() == AwardStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", pe.getStateInfo());
                }
            } catch (RuntimeException e) {
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
    private ImageHolder handlerImage(HttpServletRequest request, ImageHolder thumbnail) throws IOException {
        MultipartHttpServletRequest multipartRequest;
        multipartRequest = (MultipartHttpServletRequest)request;
        //取出图片输入流
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest
                .getFile("thumbnail");
        //将其设置给传入的thumbnail并返回
        thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        //去除详情图片列表冰构建List<ImageHolder>列表对象，最多支持六张图片上传
        return thumbnail;
    }

    //查询某个店铺下领取奖品记录
    @RequestMapping(value = "/listuserawardmapsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listUserAwardMapsByShop(
            HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Shop currentShop = (Shop) request.getSession().getAttribute(
                "currentShop");
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null)
                && (currentShop.getShopId() != null)) {
            UserAwardMap userAwardMap = new UserAwardMap();
            userAwardMap.setShop(currentShop);
            String awardName = HttpServletRequestUtil.getString(request,
                    "awardName");
            if (awardName != null) {
                Award award = new Award();
                award.setAwardName(awardName);
                userAwardMap.setAward(award);
            }
            UserAwardMapExecution ue = userAwardMapService.listUserAwardMap(
                    userAwardMap, pageIndex, pageSize);
            modelMap.put("userAwardMapList", ue.getUserAwardMapList());
            modelMap.put("count", ue.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }
    @ResponseBody
    @RequestMapping(value = "overawardlist",method = RequestMethod.POST)
    private Map<String,Object> overAwardList(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        long userAwardId = HttpServletRequestUtil.getLong(request,"userAwardId");
        Shop shop = (Shop) request.getSession().getAttribute("currentShop");
        UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId);
        if (userAwardMap.getShop().getShopId() ==shop.getShopId()){
            PersonInfo personInfo = new PersonInfo();
            personInfo.setUserId(2l);
            userAwardMap.setUsedStatus(1);
            userAwardMap.setOperator(personInfo);
            UserAwardMapExecution userAwardMapExecution = userAwardMapService.modifyUserAwardMap(userAwardMap);
            if (userAwardMapExecution.getState() ==UserProductMapStateEnum.SUCCESS.getState()){
                modelMap.put("success",true);
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("erMsg","不是本店铺的奖品");

        }
        return modelMap;

    }
    //查询某个店铺下所有改店铺客户的总积分
    @RequestMapping(value = "/listusershopmapsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> liseUserShopMapByShop(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        Shop currentShop =(Shop) request.getSession().getAttribute("currentShop");
        if ((pageIndex>-1)&&(pageSize>-1)&&(currentShop!=null)&&(currentShop.getShopId()!=null)){
            UserShopMap userShopMapCondition = new UserShopMap();
            userShopMapCondition.setShop(currentShop);
            String userName = HttpServletRequestUtil.getString(request,"userName");
            if (userName!=null){
                PersonInfo customer = new PersonInfo();
                customer.setName(userName);
                userShopMapCondition.setUser(customer);
            }
            UserShopMapExecution ue = userShopMapService.listUserShopMap(userShopMapCondition,pageIndex,pageSize);
            modelMap.put("userShopMapList",ue.getUserShopMapList());
            modelMap.put("success",true);
            modelMap.put("count",ue.getCount());
        }else {
            modelMap.put("success",false);
            modelMap.put("erMsg","空页数或者空Shop");
        }
        return modelMap;

    }
//查出某个商家 所有购买的商品
    @RequestMapping(value = "/listproductselldailyinfobyshop",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listProductSellDailyInfoByShop(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
        if (currentShop!=null && currentShop.getShopId()!=null){
            ProductSellDaily productSellDailyCondition = new ProductSellDaily();
            productSellDailyCondition.setShop(currentShop);
            Calendar calendar = Calendar.getInstance();
            //获取昨天的日期
            calendar.add(Calendar.DATE,-1);
            Date endTime  = calendar.getTime();
            //获取七天前的日期  再次-6    -1-6=-7
            calendar.add(Calendar.DATE,-6);
            Date beginTime = calendar.getTime();
            //????
            List<ProductSellDaily> productSellDailyList = productSellDailyService.listProductSellDaily(productSellDailyCondition,beginTime,endTime);
            //指定日期格式
            SimpleDateFormat sdf  = new SimpleDateFormat("yyy-MM-dd");
            //商品列表，保证唯一性
            HashSet<String> legendDate = new HashSet<String>();
            //x轴数据
            HashSet<String> xData = new HashSet<String>();
            //定义Series
            List<EchartSeries> series = new ArrayList<EchartSeries>();
            //日销量列表
            List<Integer> totalList =  new ArrayList<Integer>();
            //当前商品名 默认为空
            String cuccrentProductName = "";
            //没遍历一次 代表该店铺 一种商品 一天的销量
            for (int i=0;i<productSellDailyList.size();i++){
                ProductSellDaily productSellDaily = productSellDailyList.get(i);
                //自动去重复 只存商品名字
                legendDate.add(productSellDaily.getProduct().getProductName());
                xData.add(sdf.format(productSellDaily.getCreateTime()));
                //如果是不同商品
                if (!cuccrentProductName.equals(productSellDaily.getProduct().getProductName())&&!cuccrentProductName.isEmpty()){
                    //如果currentProductName不等于获取的商品名 ，或者已遍历到列表末尾
                    //则遍历到下一个商品的日销量统计 将前一轮的遍历信息放入到series中
                    //包括商品名称一级商品对应的统计日期和当前日销量
                    EchartSeries es  = new EchartSeries();
                    es.setName(cuccrentProductName);
                    es.setData(totalList.subList(0,totalList.size()));
                    //执行如下就把一种商品七天数据添加进去了
                    series.add(es);//执行到这就代表某件商品执行完毕了
                    //重置totalList
                    totalList = new ArrayList<Integer>();
                    //变化下currentProductId为当前productId  前台只接受名字 所以应该是名字
                    cuccrentProductName = productSellDaily.getProduct().getProductName();
                    //继续添加总数
                    totalList.add(productSellDaily.getTotal());
                }else {
                    //如果还是当前同种产品的信息则添加新值 也就是另外一天的值
                    totalList.add(productSellDaily.getTotal());
                    cuccrentProductName = productSellDaily.getProduct().getProductName();
                }
                //队列之末 需要将最后一个商品销量也添加上
                if (i==productSellDailyList.size()-1){
                    EchartSeries es = new EchartSeries();
                    es.setName(cuccrentProductName);
                    es.setData(totalList.subList(0,totalList.size()));
                    series.add(es);
                }
            }
            modelMap.put("series",series);
            modelMap.put("legendData",legendDate);
            List<EchartXAxis> xAxis = new ArrayList<EchartXAxis>();
            EchartXAxis exa = new EchartXAxis();
            exa.setData(xData);
            xAxis.add(exa);
            modelMap.put("xAxis",xAxis);
            modelMap.put("success",true);

        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty shopId");
        }
        return modelMap;
    }
    @ResponseBody
    @RequestMapping(value = "overlist",method = RequestMethod.POST)
    private Map<String,Object> overList(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        Long  userProductId = HttpServletRequestUtil.getLong(request,"userProductId");
        UserProductMap userProductMap = new UserProductMap();
        Shop currentShop = (Shop) request.getSession().getAttribute(
                "currentShop");
        UserProductMapExecution userProductMapExecution = userProductMapService.overUserProductMap(userProductId,currentShop.getShopId());
        if (userProductMapExecution.getState()==1){
            //要添加积分
            modelMap.put("success",true);
            return modelMap;
        }else {
            modelMap.put("success",false);
            modelMap.put("erMsg",userProductMapExecution.getStateInfo());
            return modelMap;
        }
    }
    @RequestMapping(value = "/listuserproductmapsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listUserProductMapsByShop(
            HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        Shop currentShop = (Shop) request.getSession().getAttribute(
                "currentShop");
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null)
                && (currentShop.getShopId() != null)) {
            UserProductMap userProductMapCondition = new UserProductMap();
            userProductMapCondition.setShop(currentShop);
            String productName = HttpServletRequestUtil.getString(request,
                    "productName");
            if (productName != null) {
                Product product = new Product();
                product.setProductName(productName);
                userProductMapCondition.setProduct(product);//插入到iaojian中
            }
            UserProductMapExecution ue = userProductMapService
                    .listUserProductMap(userProductMapCondition, pageIndex,
                            pageSize);
            modelMap.put("userProductMapList", ue.getUserProductMapList());
            modelMap.put("count", ue.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

}
