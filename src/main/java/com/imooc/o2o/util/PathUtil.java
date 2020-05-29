package com.imooc.o2o.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

public class PathUtil {
    //获取系统文件间隔符
    private static final Logger logger = LoggerFactory.getLogger(PathUtil.class);
    private static String seperator = System.getProperty("file.separator");
    //这里指定的是图片库的地址
    public static String getImgBasePath(){
        //获取计算机名字，因为间隔符在windows和ios的表现不一样
        String os = System.getProperty("os.name");
        String basePath="";
        if(os.toLowerCase().startsWith("win")){//如果皂搓系统是windows
            basePath="C:/Users/吴文兵/image/";//保存图片的地址
        }else {
            basePath="/Users/baidu/work/image";
        }
        //防止ios和windows间隔符冲突替换成系统的
        basePath = basePath.replace("/",seperator);
        return basePath;
    }
    //相对路径，后面还要加文件名  主要用于目标地址使用  真正的地址是  C:/Users/吴文兵/image/upload/item/shop/随机文件名.后缀"
    public static String getShopImagePath(long shopId){
        String imagePath = "/upload/item/shop/" + shopId + "/";//床架你文件路径的时候和上面两个斜杠不影响
        return imagePath.replace("/",seperator);
    }
    public static String getPersonImagePath(long userId){
        String imagePath = "/upload/item/person/" + userId + "/";//床架你文件路径的时候和上面两个斜杠不影响
        return imagePath.replace("/",seperator);
    }
    public static String getHeadLineImagePath(){
        String imagePath = "/upload/item/headtitle/";//床架你文件路径的时候和上面两个斜杠不影响
        return imagePath.replace("/",seperator);
    }
    public static String getShopCategoryPath(){
        String imagePath = "/upload/item/shopcategory/";//床架你文件路径的时候和上面两个斜杠不影响
        return imagePath.replace("/",seperator);
    }
    public static File getWaterMarkFile() {
        String basePath = PathUtil.getImgBasePath();
        String waterMarkImg = basePath + "/watermark/watermark.png";
        waterMarkImg = waterMarkImg.replace("/", seperator);
        logger.debug("waterMarkImg path: {}", waterMarkImg);
        return new File(waterMarkImg);
    }
}
