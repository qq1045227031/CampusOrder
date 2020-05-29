package com.imooc.o2o.web.frontend;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/frontend")
public class ProductDetailController {
    @RequestMapping(value = "/generateqrcode4shopauth")
    @ResponseBody
    private void generateQRCode4ShopAuth(HttpServletRequest request, HttpServletResponse response){
        //获取前端传入的商品id
        long productId = HttpServletRequestUtil.getLong(request,"productId");
        PersonInfo user = (PersonInfo)request.getSession().getAttribute("user");
        if (productId!=-1&&user!=null&&user.getUserId()!=null){
            //获取当前时间截以保证二维码的有效性精确到秒
            long timeStamp  = System.currentTimeMillis();
            String content  = "{aaaproductIdaaa:"+productId+",aaacustomerIdaaa"+user.getUserId()+",aaacreateTimeaaa:"+timeStamp+"}";
            try {
                                                                                                                     //这里是测试                       正常情况下这里是adduser
                String longUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1b110190ee6ca8ca&redirect_uri=http://120.26.178.72/springboot/shopadmin/aduserproductmap&role_type=1&response_type=code&scope=snsapi_userinfo&state="+URLEncoder.encode(content,"UTF-8")+"#wechat_redirect";

                //生成二维码
                BitMatrix qRcodeImg = CodeUtil.generateQRCodeStream(longUrl,response);
                //以图片流的形式输出到前台
                MatrixToImageWriter.writeToStream(qRcodeImg,"png",response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    @RequestMapping(value = "/generateqrcode4shopauth")
//    @ResponseBody
//    private void generateQRCode4ShopAuth(HttpServletRequest request, HttpServletResponse response){
//        Shop shop = (Shop)request.getSession().getAttribute("currentShop");
//        if (shop!=null&&shop.getShopId()!=null){
//            long timeStamp  = System.currentTimeMillis();
//            String content  = "{aaashopIdaaa:"+shop.getShopId()+",aaacreateTimeaaa:"+timeStamp+"}";//,  .
//            try {
//
//                String longUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1b110190ee6ca8ca&redirect_uri=http://120.26.178.72/springboot/wechatlogin/logincheck&role_type=1&response_type=code&scope=snsapi_userinfo&state="+URLEncoder.encode(content,"UTF-8")+"#wechat_redirect";
//                //生成二维码
//                BitMatrix qRcodeImg = CodeUtil.generateQRCodeStream(longUrl,response);
//                //以图片流的形式输出到前台
//                MatrixToImageWriter.writeToStream(qRcodeImg,"png",response.getOutputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    //授

}
