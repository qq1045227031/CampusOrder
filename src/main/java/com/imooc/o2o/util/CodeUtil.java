package com.imooc.o2o.util;

import com.google.code.kaptcha.Constants;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CodeUtil {
    //传入requset
    public static boolean checkVerifyCode(HttpServletRequest request){
        //获取正确的验证码
        String verifyCodeExpected = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        //HttpServletRequestUtil.getString(获取ajax中data的数据
        String verifyCodeActual = HttpServletRequestUtil.getString(request,"verifyCodeActual");
        if (verifyCodeActual==null||!verifyCodeActual.equalsIgnoreCase(verifyCodeExpected)){
            //错误返回false
            return false;
        }
        return true;
    }
    public static BitMatrix generateQRCodeStream(String content,
                                                 HttpServletResponse resp) {
        //设计头部信息  高数浏览器返回的是图片流 不要缓存
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);
        resp.setContentType("image/png");
        //设置图片文字编码一级内边框矿距
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);//内边框距离
        BitMatrix bitMatrix;
        try {
            //参数顺序分别为 :编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            bitMatrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, 300, 300, hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        return bitMatrix;
    }
}
