package com.imooc.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.imooc.o2o.dao.ProductSellDailyDao;
import com.imooc.o2o.dto.ShopAuthMapExecution;
import com.imooc.o2o.dto.UserAccessToken;
import com.imooc.o2o.dto.WechatInfo;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopAuthMap;
import com.imooc.o2o.entity.WechatAuth;
import com.imooc.o2o.enums.ShopAuthMapStateEnum;
import com.imooc.o2o.service.PersonInfoService;
import com.imooc.o2o.service.ProductSellDailyService;
import com.imooc.o2o.service.ShopAuthMapService;
import com.imooc.o2o.service.WechatAuthService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.wechat.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shopadmin")
public class ShopAuthManagementController {
    @Autowired
    private ShopAuthMapService shopAuthMapService;
    @Autowired
    private WechatAuthService wechatAuthService;
    @Autowired
    private PersonInfoService personInfoService;
//    @Autowired
//    private ProductSellDailyService productSellDailyService;
    /**
     * 定时器
     */
    @Autowired
    private ProductSellDailyDao productSellDailyDao;//0 0 0 */1 * ?每天执行一次   0/5 * * * * ?每五秒执行一次
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 0 */1 * ?")
    private void sellCreate(){
        //插入有效率的product
        productSellDailyDao.insertProductSellDaily();
        //插入没有销量的product
        productSellDailyDao.insertDefaultProductSellDaily();
        System.out.println("执行生成列表");
    }


    @RequestMapping(value = "/listshopauthmapsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listShopAuthMapsByShop(
            HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //分页信息
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        Shop currentShop = (Shop) request.getSession().getAttribute(
                "currentShop");
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null)
                && (currentShop.getShopId() != null)) {
            ShopAuthMapExecution se = shopAuthMapService
                    .listShopAuthMapByShopId(currentShop.getShopId(),
                            pageIndex, pageSize);
            modelMap.put("shopAuthMapList", se.getShopAuthMapList());
            modelMap.put("count", se.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/modifyshopauthmap", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> modifyShopAuthMap(String shopAuthMapStr,
                                                  HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //编辑的时候不需要
        boolean statusChange = HttpServletRequestUtil.getBoolean(request,"statusChange");
        if (!statusChange&&!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        ObjectMapper mapper = new ObjectMapper();
        ShopAuthMap shopAuthMap = null;
        try {
            shopAuthMap = mapper.readValue(shopAuthMapStr, ShopAuthMap.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        if (shopAuthMap != null && shopAuthMap.getShopAuthId() != null) {
            try {
                if (!checkPermission(shopAuthMap.getShopAuthId())){
                    modelMap.put("success",false);
                    modelMap.put("errMsg","无法对店家本身权限操作");
                    return modelMap;
                }
//                Shop currentShop = (Shop) request.getSession().getAttribute(
//                        "currentShop");
//                PersonInfo user = (PersonInfo) request.getSession()
//                        .getAttribute("user");
//                shopAuthMap.(currentShop.getShopId());
//                shopAuthMap.setEmployeeId(user.getUserId());
                ShopAuthMapExecution se = shopAuthMapService.modifyShopAuthMap(shopAuthMap);
                if (se.getState() == ShopAuthMapStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", se.getStateInfo());
                }
            } catch (RuntimeException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入要修改的授权信息");
        }
        return modelMap;
    }
private boolean checkPermission(Long shopAuthId){
        ShopAuthMap grantedPerson = shopAuthMapService.getShopAuthMapById(shopAuthId);
        if (grantedPerson.getTitleFlag()==0){
            return false;
        }else {
            return true;
        }
}
    @RequestMapping(value = "/getshopauthmapbyid", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getShopAuthMapById(@RequestParam Long shopAuthId) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (shopAuthId != null && shopAuthId > -1) {
            ShopAuthMap shopAuthMap = shopAuthMapService
                    .getShopAuthMapById(shopAuthId);
            modelMap.put("shopAuthMap", shopAuthMap);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty shopAuthId");
        }
        return modelMap;
    }

    /**
     * 生成二维码
     * @param request
     * @param response
     */
    @RequestMapping(value = "/generateqrcode4shopauth")
    @ResponseBody
    private void generateQRCode4ShopAuth(HttpServletRequest request, HttpServletResponse response){
        Shop shop = (Shop)request.getSession().getAttribute("currentShop");
        if (shop!=null&&shop.getShopId()!=null){
            long timeStamp  = System.currentTimeMillis();
            String content  = "{aaashopIdaaa:"+shop.getShopId()+",aaacreateTimeaaa:"+timeStamp+"}";//,  .
            try {

                String longUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1b110190ee6ca8ca&redirect_uri=http://120.26.178.72/springboot/wechatlogin/logincheck&role_type=1&response_type=code&scope=snsapi_userinfo&state="+URLEncoder.encode(content,"UTF-8")+"#wechat_redirect";
                //生成二维码
                BitMatrix qRcodeImg = CodeUtil.generateQRCodeStream(longUrl,response);
                //以图片流的形式输出到前台
                MatrixToImageWriter.writeToStream(qRcodeImg,"png",response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //授权
    @RequestMapping(value = "/addshopauthmap", method = RequestMethod.GET)
    @ResponseBody
    private String addShopAuthMap(HttpServletRequest request,
                                               HttpServletResponse response) throws IOException {
        //获取到用户信息
        WechatAuth auth = getEmployeeInfo(request);
        if (auth!=null){
            //根据userId获取改用户信息
            PersonInfo user = personInfoService.getPersonInfoById(auth.getPersonInfo().getUserId());
            request.getSession().setAttribute("user",user);
            String qrCodeinfo = new String(URLDecoder.decode(HttpServletRequestUtil.getString(request,"state"),"UTF-8"));
            ObjectMapper mapper = new ObjectMapper();
            WechatInfo wechatInfo = null;
            try {
                wechatInfo = mapper.readValue(qrCodeinfo.replace("aaa","\""),WechatInfo.class);

            }catch (Exception e){
                return "shopadmin/operationfail";
            }
            //检测二维码是否过期
            if (!checkQRCodeInfo(wechatInfo)){
                return "shopadmin/operationfail";
            }
            ShopAuthMapExecution allMapList = shopAuthMapService.listShopAuthMapByShopId(wechatInfo.getShopId(),0,999);
            List<ShopAuthMap> shopAuthMapList = allMapList.getShopAuthMapList();
            for (ShopAuthMap sm:shopAuthMapList){
                if (sm.getEmployee().getUserId()==user.getUserId()){
                    return "shopadmin/operationredo";
                }
            }
            try {
                //根据获取的内容添加授权信息
                ShopAuthMap shopAuthMap = new ShopAuthMap();
                Shop shop = new Shop();
                shop.setShopId(wechatInfo.getShopId());
                shopAuthMap.setShop(shop);
                PersonInfo employee = new PersonInfo();
                shopAuthMap.setEmployee(user);
                shopAuthMap.setTitle("员工");
                shopAuthMap.setTitleFlag(1);
                ShopAuthMapExecution se = shopAuthMapService.addShopAuthMap(shopAuthMap);
                if (se.getState()==ShopAuthMapStateEnum.SUCCESS.getState()){
                    return "shopadmin/operationsuccess";
                }
                else {
                    return "shopadmin/operationfail";
                }
            }catch (RuntimeException e){
                return "shopadmin/operationfail";
            }

        }
        return "shopadmin/operationfail";


    }
    private WechatAuth getEmployeeInfo(HttpServletRequest request){
        String code = request.getParameter("code");
        WechatAuth auth = null;
        if (null!=code){
            UserAccessToken token;
            try {
                token = WechatUtil.getUserAccessToken(code);
                String openId = token.getOpenId();
                request.getSession().setAttribute("openId",openId);
                auth = wechatAuthService.getWechatAuthByOpenId(openId);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return auth;
    }
    private boolean checkQRCodeInfo(WechatInfo wechatInfo) {
        if (wechatInfo != null && wechatInfo.getShopId() != null && wechatInfo.getCreateTime() != null) {
            long nowTime = System.currentTimeMillis();
            if ((nowTime - wechatInfo.getCreateTime())<=600000){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

}
