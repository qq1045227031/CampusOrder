package com.imooc.o2o.web.frontend;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.imooc.o2o.dto.UserAwardMapExecution;
import com.imooc.o2o.entity.Award;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.UserAwardMap;
import com.imooc.o2o.service.AwardService;
import com.imooc.o2o.service.UserAwardMapService;
import com.imooc.o2o.service.UserShopMapService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/frontend")
public class MyAwardController {
@Autowired
private UserAwardMapService userAwardMapService;
@Autowired
private AwardService awardService;

@RequestMapping(value = "/generateqrcode4award",method = RequestMethod.GET)
@ResponseBody
private void generateQRCode4Product(HttpServletRequest request, HttpServletResponse response){
    long userAwarId = HttpServletRequestUtil.getLong(request,"userAwardId");
    UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwarId);
    PersonInfo user = (PersonInfo)request.getSession().getAttribute("user");
    if (userAwardMap!=null&&user!=null&&user.getUserId()!=null){
        //获取当前时间截
        long timeStamp =System.currentTimeMillis();
        String content = "{aaauserAwardIdaaa:"+userAwarId+",aaacustomerIdaaa:"+user.getUserId()
                +",aaacreteTimeaaa:"+timeStamp+"}";
        try {
            String longUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1b110190ee6ca8ca&redirect_uri=http://120.26.178.72/springboot/shopadmin/exchangeaward&role_type=1&response_type=code&scope=snsapi_userinfo&state="+ URLEncoder.encode(content,"UTF-8")+"#wechat_redirect";
            //二维码生成工具类  转入url生成二维码
            BitMatrix qRcodeImg = CodeUtil.generateQRCodeStream(longUrl,response);
            //将二维码以图片流形式传入到前端
            MatrixToImageWriter.writeToStream(qRcodeImg,"png",response.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
    /**
     * 获取某客户奖品兑换记录的详情
     * @param request
     * @return
     */
    @RequestMapping(value = "/getawardbyuserawardid",method = RequestMethod.GET)
@ResponseBody
private Map<String,Object> getAwardByUserAwardId(HttpServletRequest request){
    Map<String,Object> modelMap = new HashMap<String, Object>();
    long userAwardId = HttpServletRequestUtil.getLong(request,"userAwardId");
    if (userAwardId>-1){
        //获取userawardmao 里面有奖品信息
        UserAwardMap userAwardMap = new UserAwardMap();
        userAwardMap.setUserAwardId(userAwardId);
        UserAwardMapExecution userAwardMapExecution = userAwardMapService.listUserAwardMap(userAwardMap,0,999);
        userAwardMap=userAwardMapExecution.getUserAwardMapList().get(0);
//        UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId);//?
        Award award =  awardService.getAwardById(userAwardMap.getAward().getAwardId());
        modelMap.put("award",award);
        modelMap.put("success",true);
        modelMap.put("userAwardMap",userAwardMap);
        modelMap.put("usedStatus",userAwardMap.getUsedStatus());
    }else {
        modelMap.put("success",false);
        modelMap.put("errMsg","empty awardId");
    }
    return modelMap;
}

    /**
     * 获取某个客户的所有奖品兑换记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/listuserawardmapbycustomer",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listUserAwardMapByCustmoer(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        //从session中取值
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        if ((pageIndex>-1)&&(pageSize>-1)&&(user!=null)&&(user.getUserId()!=null)){
            UserAwardMap userAwardMapCondition = new UserAwardMap();
            userAwardMapCondition.setUser(user);
            long shopId = HttpServletRequestUtil.getLong(request,"shopId");
            if (shopId>-1){
                Shop shop = new Shop();
                shop.setShopId(shopId);
                userAwardMapCondition.setShop(shop);
            }
            String awardName = HttpServletRequestUtil.getString(request,"awardName");
            if (awardName!=null){
                Award award = new Award();
                award.setAwardName(awardName);
                userAwardMapCondition.setAward(award);
            }
            UserAwardMapExecution ue = userAwardMapService.listUserAwardMap(userAwardMapCondition,pageIndex,pageSize);//?
            modelMap.put("userAwardMapList",ue.getUserAwardMapList());
            modelMap.put("count",ue.getCount());
            modelMap.put("success",true);
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageInex or userId");

        }
        return modelMap;
    }
    //某客户的奖品信息的详情
//    @RequestMapping(value = "/getawardbyuserawardid",method = RequestMethod.GET)
//    @ResponseBody



}
