package com.imooc.o2o.util;

import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.util.PathUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ImageUtil {
    private static String basePath =Thread.currentThread().getContextClassLoader().getResource("").getPath();//格式
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final Random r = new Random();//生成随机数
    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
    //CommonsMultipartFile thumbnail,类似传入原图
//    public File commonsMultipartFileToFile(CommonsMultipartFile cfile) {//把commonsMultipartFile转化为File
//        File file = null;
//        try {
//            // 获取前端传递过来的文件名
//            file = new File(cfile.getOriginalFilename());
//            // 将cfile转换为file
//            cfile.transferTo(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("commonsMultipartFileToFile failed:{}", e.getMessage());
//        }
//        return file;
//    }

    /**
     * 将CommonsMultipartFile 转化为 java.io.File;
     * @param cFile
     * @return
     */
    public static File transferCommonsMultipartFileToFile(CommonsMultipartFile cFile){
        File newFile = new File(cFile.getOriginalFilename());//复制名字
        try {
            cFile.transferTo(newFile);//转化内容
        }catch (IllegalStateException e){
            logger.error(e.toString());
            e.printStackTrace();
        }catch (IOException e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
        return newFile;
    }
    //返回图片地址
    public static String generateNormalImg(ImageHolder thumbnail, String targetAddr){
        try {
            basePath = URLDecoder.decode(basePath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("创建微缩图失败,编码格式不能转化为utf-8"+e.toString());
        }
        String realFileName = getRandomFileName();//获取随机名字作为真正名字防止重复
        String extension = getFileExtension(thumbnail.getImageName());//扩展名后缀？,目标路径也包括后缀要通过代码动态生成格式
        makeDirPath(targetAddr);
        String relativeAddr = targetAddr + realFileName + extension;//相对路径:目标 + 随机真实文件名 +后缀
        logger.error("current relativeAddr is :"+relativeAddr);
        File dest = new File(PathUtil.getImgBasePath() + relativeAddr);//绝对路径?加上专门保存图片的地址
        logger.error("current Complete Addr is :"+dest);
        logger.error("地址" +new File(new File(basePath + "/watermark.jpg").getAbsolutePath()));
        try {
            Thumbnails.of(thumbnail.getImage()).size(337, 640)
                    .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/watermark.jpg")), 0.5f)
                    .toFile(dest);
//            Thumbnails.of("源文件路径")
//                    .scale(1f) //0-1 float 压缩大小
//                    .outputQuality(0.7f) //0-1 压缩质量
//                    .toFile("新文件路径");
        } catch (IOException e) {
            logger.error(e.toString());
            throw new RuntimeException("创建微缩图失败"+e.toString());
        }
        return relativeAddr;//返回相对路径,返回相对路径是为了在不同电脑上也能读到文件，使用PathUtil.getImgBasePath() + relativeAddr)的方式
    }
    /**
     * 处理缩略图，并返回新生成图片的相对值路径
     * @param thumbnail 将inputstream和name合二为一
     * @param targetAddr
     * @return
     * @throws IOException
     */

    //CommonsMultipartFile转化为file简单，但是file转化为CommonsMultipartFile 很困难
    //传进来来的是图片还有相对路径
    public static String generateThumbnail(ImageHolder thumbnail, String targetAddr){
        try {
            basePath = URLDecoder.decode(basePath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("创建微缩图失败,编码格式不能转化为utf-8"+e.toString());
        }
        String realFileName = getRandomFileName();//获取随机名字作为真正名字防止重复
        String extension = getFileExtension(thumbnail.getImageName());//扩展名后缀？,目标路径也包括后缀要通过代码动态生成格式
        makeDirPath(targetAddr);
        String relativeAddr = targetAddr + realFileName + extension;//相对路径:目标 + 随机真实文件名 +后缀
        logger.error("current relativeAddr is :"+relativeAddr);
        File dest = new File(PathUtil.getImgBasePath() + relativeAddr);//绝对路径?加上专门保存图片的地址
        logger.error("current Complete Addr is :"+dest);

        try {
            Thumbnails.of(thumbnail.getImage()).size(200, 200)
//                    .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/watermark.jpg")), 0.5f)
                    .toFile(dest);
        } catch (IOException e) {
            logger.error(e.toString());
            throw new RuntimeException("创建微缩图失败"+e.toString());
        }
        return relativeAddr;//返回相对路径,返回相对路径是为了在不同电脑上也能读到文件，使用PathUtil.getImgBasePath() + relativeAddr)的方式
    }
    public static String getRandomFileName(){//生成随机数
        int rannum = r.nextInt(89999)+10000;//确保是五位数
        String nowTimeStr = sDateFormat.format(new Date());//用到上面定义的日期格式
        String randomFileName = nowTimeStr + rannum;
        logger.debug("fileName: {}", randomFileName);
        return randomFileName;
    }
    private static String getFileExtension(String fileName){
//        String originalFileName = cFile.getName();//获取源文件名称cFile.getOriginalFilename()
        return fileName.substring(fileName.lastIndexOf("."));
        //lastIndexOf索引最后.的位置，然后通过substring从该位置切开取右边得到文件类型后缀比如 .jsp
    }
//    private static String getFileExtensionName(String fileName) {
//        String extension = fileName.substring(fileName.lastIndexOf("."));
//        logger.debug("extension: {}", extension);
//        return extension;
//    }

    //创建目标路径所涉及到的目录，即C:/Users/吴文兵/image/xxx.jpga那么User 吴文兵 img 这三个文件夹得创建出来
    //传进来的是相对路径
    private static void makeDirPath(String targetAddr){
        String realFileParentPath = PathUtil.getImgBasePath()+targetAddr;
        File dirPath = new File(realFileParentPath);
        if (!dirPath.exists()){
            dirPath.mkdirs();
        }
    }

    /**
     * storePath是文件的路径还是目录的路径
     * 如果storePath是文件路径则删除该路径
     * 如果storePath是目录路径则删除该目录下的所有文件
     * @param storePath
     * @throws IOException
     */
    public static void deleFileOrPath(String storePath){
        File fileOrPath = new File(PathUtil.getImgBasePath()+storePath);
        if (fileOrPath.exists()){//判断是否存在
            if (fileOrPath.isDirectory()){//判断是否为文件夹,是就删除问价下所有文件
                File file[] = fileOrPath.listFiles();//说明：返回某个目录下所有文件和目录的绝对路径，返回类型File[]
                for (int i=0;i<file.length;i++){
                    file[i].delete();
                }
                fileOrPath.delete();//如果是文件删除文件，如果是目录目录本身删除

            }
        }
    }

//    public static void deleteStorePath(String storePath) {
//        File fileOrMenu = new File(PathUtil.getImgBasePath() + storePath);
//        if (fileOrMenu != null) {
//            if (fileOrMenu.isDirectory()) {
//                File[] files = fileOrMenu.listFiles();
//                for (int i = 0; i < files.length; i++) {
//                    files[i].delete();
//                }
//            }
//            fileOrMenu.delete();
//        }
//    }

    public static void main(String[] args) throws IOException {
        //获取这个项目的resource路径，从而获取watermark.jap
//        String basePath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
        basePath= URLDecoder.decode(basePath,"utf-8");//如果不加这段会出现javax.imageio.IIOException: Can't read input file!
        //https://blog.csdn.net/lmhlmh_/article/details/82886419 ,主要是里面%20其实是个空格
        //.of(要修改的图片) .size图片大小 .watermark(水印位置，水印图片位置，透明度) .outputQuality(0.8f)图片质量越少越模糊1最大
        Thumbnails.of(new File("C:/Users/吴文兵/image/xuefeng.jpg"))
                .size(600,400)
                .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath+ "/watermark.jpg")),0.5f)
                .outputQuality(0.8f)
                .toFile("C:/Users/吴文兵/image/newxuefeng.jpg");

    }
}
