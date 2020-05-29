package com.imooc.o2o.web.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.Person;
import com.imooc.o2o.dto.PersonInfoExecution;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.*;
import com.imooc.o2o.enums.PersonInfoStateEnum;
import com.imooc.o2o.enums.ShopCategoryStateEnum;
import com.imooc.o2o.service.*;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Controller
@RequestMapping("/frontend")
public class MainPageController {
    @Autowired
    private HeadLineService headLineService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private PersonInfoService personInfoService;
    //查询第一级（传入null，取得parentid+null）  或者以传入的parentId以其为父类的字项目
    @RequestMapping(value = "/updatemessage",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> updateMessage(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String,Object> modelMap = new HashMap<>();
        String personInfoStr = HttpServletRequestUtil.getString(request,"root");
        ObjectMapper mapper = new ObjectMapper();
        PersonInfo personInfo = null;
//        String str ;

        ImageHolder thumbnail = null;
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        try {
            //若请求中存在文件流，去除相关的文件包括缩略图和详情图片
            if (multipartResolver.isMultipart(request)) {//平判断请求中是否有文件流
                thumbnail = handlerImage((MultipartHttpServletRequest) request);
            }
        } catch (Exception e) {
//            modelMap.put("success", false);
//            modelMap.put("errMsg", e.toString());
//            return modelMap;
        }
        try {
             personInfo = mapper.readValue(personInfoStr,PersonInfo.class);
//             str = new String(personInfo.getAddress().getBytes("ISO8859_1"), "UTF-8");
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMsg","类型转换失败");
            return modelMap;
        }
        if (thumbnail!=null){
            PersonInfo oldPerson = personInfoService.getPersonInfoById(personInfo.getUserId());
            if (oldPerson.getProfileImg()!=null){
                try {
                    ImageUtil.deleFileOrPath(oldPerson.getProfileImg());
                }catch (Exception e){}
                }
            addThumbnail(personInfo,thumbnail);
        }
//        personInfo.setAddress(str);
//        String  str2 = new String(personInfo.getAddress().getBytes("GBK"), "UTF-8");
//        String  str3 = new String(personInfo.getAddress().getBytes("UTF-8"), "GBK");
//        area.setAreaName((area.getAreaName() == null) ? null : URLDecoder
//                .decode(area.getAreaName(), "UTF-8"));
        personInfo.setLastEditTime(new Date());
        PersonInfoExecution personInfoExecution = personInfoService.modifyPersonInfo(personInfo);
        if (personInfoExecution.getState()== PersonInfoStateEnum.SUCCESS.getState()){
            modelMap.put("success",true);
        }else{
            modelMap.put("success",false);
            modelMap.put("errMsg","用户信息修改失败");
        }

        return modelMap;
    }

    private void addThumbnail(PersonInfo personInfo,ImageHolder thumbnail) {
        String dest = PathUtil.getPersonImagePath(personInfo.getUserId());//通过id获取相对卢克
        String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail,dest);//创建图片返回图片位置
        personInfo.setProfileImg(thumbnailAddr);//将图片存在product.ImgAddr中

    }
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
    private ImageHolder handlerImage(MultipartHttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest;
        ImageHolder thumbnail;
        multipartRequest = request;
        //去除缩略图并构建ImageHolder对象
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest
                .getFile("thumbnail");
        //这是缩略图的ImageHolder类
        thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        //去除详情图片列表冰构建List<ImageHolder>列表对象，最多支持六张图片上传
        return thumbnail;
    }
    @RequestMapping(value = "/getmymessage",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> getMyMessage(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        user = personInfoService.getPersonInfoById(user.getUserId());
        if (user!=null){
            modelMap.put("success",true);
            modelMap.put("user",user);
        }else{
            modelMap.put("success",false);
            modelMap.put("errMsg","请登录");
        }
        return modelMap;

    }
    @RequestMapping(value = "/listshopspageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> list1stShopCategory(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        long parentId = HttpServletRequestUtil.getLong(request,"parentId");
        List<ShopCategory> shopCategoryList = null;
        try {//传入一个商店类别，获取该类别下的所有商品类别
            ShopCategory shopCategoryCondition = new ShopCategory();
            ShopCategory parent = new ShopCategory();
            parent.setShopCategoryId(parentId);
            shopCategoryCondition.setParent(parent);
            shopCategoryList = shopCategoryService.getShopCategoryList(shopCategoryCondition);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
        }
        modelMap.put("shopCategoryList",shopCategoryList);
        List<Area> areaList = null;
        try {
            areaList = areaService.getAreaList();
            modelMap.put("success",true);
            modelMap.put("areaList",areaList);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
        }
        return modelMap;
}
    @RequestMapping(value = "/listshops", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listShops(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //获取页数和每页商铺数量
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        if ((pageIndex > -1) && (pageSize > -1)) {
            //获取类别id,查询所有其下的子类
            long parentId = HttpServletRequestUtil.getLong(request, "parentId");
            long shopCategoryId = HttpServletRequestUtil.getLong(request,
                    "shopCategoryId");
            long areaId = HttpServletRequestUtil.getLong(request, "areaId");
            String shopName = HttpServletRequestUtil.getString(request,
                    "shopName");
            //若有值将其设置进去
            Shop shopCondition = compactShopCondition4Search(parentId,
                    shopCategoryId, areaId, shopName);
            ShopExecution se = shopService.getShopList(shopCondition,
                    pageIndex, pageSize);
            modelMap.put("shopList", se.getShopList());
            modelMap.put("count", se.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex");
        }
        return modelMap;
    }
    private Shop compactShopCondition4Search(long parentId,
                                             long shopCategoryId, long areaId, String shopName) {
        Shop shopCondition = new Shop();
        if (parentId != -1L) {
            ShopCategory parentCategory = new ShopCategory();
            ShopCategory childCategory = new ShopCategory();
            parentCategory.setShopCategoryId(parentId);
            childCategory.setParent(parentCategory);
            shopCondition.setShopCategory(childCategory);
        }
        if (shopCategoryId != -1L) {
            ShopCategory shopCategory = new ShopCategory();
            shopCategory.setShopCategoryId(shopCategoryId);
            shopCondition.setShopCategory(shopCategory);
        }
        if (areaId != -1l) {
            Area area = new Area();
            area.setAreaId(areaId);
            shopCondition.setArea(area);
        }

        if (shopName != null) {
            shopCondition.setShopName(shopName);
        }
        shopCondition.setEnableStatus(1);
        return shopCondition;
    }

    @RequestMapping(value = "/listmainpageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> list1stShopCategory() {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
        try {
            //获取一级列表,也就是parent_id为null没有上级的列表
            shopCategoryList = shopCategoryService
                    .getShopCategoryList(null);
            modelMap.put("shopCategoryList", shopCategoryList);
        } catch (Exception e) {
            e.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", "获取一级列表失败"+e.getMessage());
            return modelMap;
        }
        List<HeadLine> headLineList = new ArrayList<HeadLine>();
        try {
            //获取状态为可用的头条列表
            HeadLine headLineCondition = new HeadLine();
            headLineCondition.setEnableStatus(1);
            headLineList = headLineService.getHeadLineList(headLineCondition);
            modelMap.put("headLineList", headLineList);
        } catch (Exception e) {
            e.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", "获取可用列表失败"+e.getMessage());
            return modelMap;
        }
        modelMap.put("success", true);
        return modelMap;
    }

}
