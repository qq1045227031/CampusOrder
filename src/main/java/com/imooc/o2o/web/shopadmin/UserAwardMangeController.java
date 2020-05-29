package com.imooc.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ShopAuthMapExecution;
import com.imooc.o2o.dto.UserAccessToken;
import com.imooc.o2o.dto.UserAwardMapExecution;
import com.imooc.o2o.dto.WechatInfo;
import com.imooc.o2o.entity.*;
import com.imooc.o2o.enums.UserAwardMapStateEnum;
import com.imooc.o2o.service.PersonInfoService;
import com.imooc.o2o.service.ShopAuthMapService;
import com.imooc.o2o.service.UserAwardMapService;
import com.imooc.o2o.service.WechatAuthService;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.wechat.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/shopadmin")
public class UserAwardMangeController {
    @Autowired
    private UserAwardMapService userAwardMapService;
    @Autowired
    private WechatAuthService wechatAuthService;
    @Autowired
    private PersonInfoService personInfoService;
    @Autowired
    private ShopAuthMapService shopAuthMapService;
    @RequestMapping(value = "/exchangeaward", method = RequestMethod.POST)
    @ResponseBody
    private String exchangeAward(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        WechatAuth auth = getOperatorInfo(request);
        if (auth!=null){//操作员不能为空
            PersonInfo operator = personInfoService.getPersonInfoById(auth.getPersonInfo().getUserId());
            request.getSession().setAttribute("user",operator);//根据uod查出信息
            String qrCodeinfo = new String(URLDecoder.decode(HttpServletRequestUtil.getString(request,"state"),"UTF-8"));
            ObjectMapper mapper = new ObjectMapper();
            WechatInfo wechatInfo = null;
            try {
                wechatInfo = mapper.readValue(qrCodeinfo.replace("aaa","\""),WechatInfo.class);

            }catch (Exception e){
            return "shop/operationfail";
        }
            if (!WechatUtil.checkQRCodeInfo(wechatInfo)){
                return "shop/operationfail";
            }
            Long userAwardID = wechatInfo.getUserAwardId();
            Long customerId = wechatInfo.getCustomerId();
            UserAwardMap userAwardMap = compactUserAwardMap4Exchange(customerId,userAwardID,operator.getUserId());
            if (userAwardMap!=null){
                try {
                    if (!checkShopAuth(operator.getUserId(),userAwardMap)){
                        return "shop/operationfail";
                    }
                    UserAwardMapExecution se = userAwardMapService.modifyUserAwardMap(userAwardMap);
                    if (se.getState()==UserAwardMapStateEnum.SUCCESS.getState()){
                        return "shop/operationsuccess";
                    }
                }catch (RuntimeException e){
                    return "shop/operationfail";
                }
            }
        }
        return "shop/operationfail";
    }
    //查询该操作员是否有操作全权限
    private boolean checkShopAuth(long userId,UserAwardMap userAwardMap){
        ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService.listShopAuthMapByShopId(userAwardMap.getShop().getShopId(),1,100);
        for (ShopAuthMap shopAuthMap:shopAuthMapExecution.getShopAuthMapList()){
            if (shopAuthMap.getEmployee().getUserId()==userId){
                return true;
            }
        }
        return false;

    }
//    private boolean checkShopAuth(long userId, UserProductMap userProductMap){
//        //获取改店铺的所有授权信息
//        ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService.listShopAuthMapByShopId(userProductMap.getShop().getShopId(),1,100);
//        for (ShopAuthMap shopAuthMap:shopAuthMapExecution.getShopAuthMapList()){
//            if (shopAuthMap.getEmployee().getUserId()==userId){
//                return true;
//            }
//        }
//        return false;
//    }

    private UserAwardMap compactUserAwardMap4Exchange(long customerId,long userAwardID,long operatorId){
        UserAwardMap userAwardMap = new UserAwardMap();
        if ((customerId>-1) &&(userAwardID>-1)&&(operatorId>-1)) {
            PersonInfo cust = new PersonInfo();
            cust.setUserId(customerId);
            PersonInfo operator = new PersonInfo();
            operator.setUserId(operatorId);
            Award award = new Award();
            award.setAwardId(userAwardID);
            userAwardMap.setUser(cust);
            userAwardMap.setAward(award);
            userAwardMap.setOperator(operator);
            userAwardMap.setCreateTime(new Date());
        }
        return userAwardMap;
    }
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
}
