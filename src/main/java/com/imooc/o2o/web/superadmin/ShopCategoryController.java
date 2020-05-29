package com.imooc.o2o.web.superadmin;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.imooc.o2o.dao.ShopCategoryDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopCate;
import com.imooc.o2o.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.Result;
import com.imooc.o2o.dto.ShopCategoryExecution;
import com.imooc.o2o.entity.ConstantForSuperAdmin;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopCategoryStateEnum;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.util.HttpServletRequestUtil;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Controller
@RequestMapping("/superadmin")
public class ShopCategoryController {
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private ShopCategoryDao shopCategoryDao;
    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "/listshopcategorys", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> listShopCategorys() {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        List<ShopCategory> list = new ArrayList<ShopCategory>();
        try {
            ShopCategory shopCategory = new ShopCategory();
            ShopCategory parent = new ShopCategory();
            parent.setShopCategoryId(0l);//0代表所有 -1代表二级 null代表一级类别
            shopCategory.setParent(parent);
            list = shopCategoryService.getShopCategoryList(shopCategory);
            List<ShopCate> shopCates = new ArrayList<>();
            for (ShopCategory s : list){
                ShopCate shopCate = new ShopCate(s);
                shopCates.add(shopCate);
            }
            modelMap.put(ConstantForSuperAdmin.PAGE_SIZE, shopCates);
            modelMap.put(ConstantForSuperAdmin.TOTAL, shopCates.size());
        } catch (Exception e) {
            e.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
        }
        return modelMap;
    }

    @RequestMapping(value = "/list1stlevelshopcategorys", method = RequestMethod.POST)
    @ResponseBody
    private Result<List<ShopCategory>> list1stLevelShopCategorys() {
        List<ShopCategory> list = new ArrayList<ShopCategory>();
        try {
            list = shopCategoryService.getShopCategoryList(null);
        } catch (Exception e) {
            e.printStackTrace();
            ShopCategoryStateEnum a = ShopCategoryStateEnum.INNER_ERROR;
            return new Result<List<ShopCategory>>(false, a.getState(),
                    a.getStateInfo());
        }
        return new Result<List<ShopCategory>>(true, list);
    }

    @RequestMapping(value = "/addshopcategory", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addShopCategory(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        ShopCategory shopCategory = null;
        ImageHolder thumbnail = null;
        String shopCategoryStr = HttpServletRequestUtil.getString(request,
                "shopCategoryStr");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        try {
            ShopCate shopCate = mapper.readValue(shopCategoryStr, ShopCate.class);
            shopCategory =  new ShopCategory(shopCate);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        try {
            //若请求中存在文件流，去除相关的文件包括缩略图和详情图片
            if (multipartResolver.isMultipart(request)) {//平判断请求中是否有文件流
                thumbnail = handlerImage((MultipartHttpServletRequest) request);
            }
        }catch (Exception e) {
            //没有图片溜的方法
            if (shopCategory != null && shopCategory.getShopCategoryId()!=null) {
                try {
                    shopCategory.setShopCategoryName((shopCategory
                            .getShopCategoryName() == null) ? null : (URLDecoder
                            .decode(shopCategory.getShopCategoryName(), "UTF-8")));
                    shopCategory.setShopCategoryDesc((shopCategory
                            .getShopCategoryDesc() == null) ? null : (URLDecoder
                            .decode(shopCategory.getShopCategoryDesc(), "UTF-8")));
                    int eff =shopCategoryDao.insertShopCategory(shopCategory);
                    if (eff>0){
                        modelMap.put("success", true);
                        cacheService.removeFromCache(shopCategoryService.SCLISTKEY);//清楚缓存
                        return modelMap;
                    }else {
                        modelMap.put("success", false);
                        modelMap.put("errMsg", e.toString());
                        return modelMap;
                    }
                }catch (Exception e1) {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", e1.toString());
                    return modelMap;
                }

            }
            else {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }
        }
        if (shopCategory != null && thumbnail != null) {
            try {
                // decode可能有中文的地方
                shopCategory.setShopCategoryName((shopCategory
                        .getShopCategoryName() == null) ? null : (URLDecoder
                        .decode(shopCategory.getShopCategoryName(), "UTF-8")));
                shopCategory.setShopCategoryDesc((shopCategory
                        .getShopCategoryDesc() == null) ? null : (URLDecoder
                        .decode(shopCategory.getShopCategoryDesc(), "UTF-8")));
                ShopCategoryExecution ae = shopCategoryService.addShopCategory(
                        shopCategory, thumbnail);
                if (ae.getState() == ShopCategoryStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", ae.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入店铺类别信息");
        }
        return modelMap;
    }
    private ImageHolder handlerImage(MultipartHttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest;
        ImageHolder thumbnail;
        multipartRequest = request;
        //去除缩略图并构建ImageHolder对象
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest
                .getFile("shopCategoryManagementAdd_shopCategoryImg");//thumnail
        //这是缩略图的ImageHolder类
        thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        //去除详情图片列表冰构建List<ImageHolder>列表对象，最多支持六张图片上传
        return thumbnail;
    }

    @RequestMapping(value = "/modifyshopcategory", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> modifyShopCategory(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        ShopCategory shopCategory = null;
        String shopCategoryStr = HttpServletRequestUtil.getString(request,
                "shopCategoryStr");
        ImageHolder thumbnail = null;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        try {
            ShopCate shopCate = mapper.readValue(shopCategoryStr, ShopCate.class);//因为传入的是parentId不是一个对象，所以进行转换
            shopCategory = new ShopCategory(shopCate);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        try {
            //若请求中存在文件流，去除相关的文件包括缩略图和详情图片
            if (multipartResolver.isMultipart(request)) {//平判断请求中是否有文件流
//                thumbnail = handlerImage((MultipartHttpServletRequest) request);//读取到文件流名为thumail的文件
                multipartRequest=(MultipartHttpServletRequest)request;
                CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest
                        .getFile("shopCategoryManagementEdit_shopCategoryImg");
                //这是缩略图的ImageHolder类
                thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
            //1.变成mulrequest   2.getFile获得mulfile  3.mulfile.getname/geinputstream 变成ImageHolder
            }
        } catch (Exception e) {
            //没有图片溜的方法
            if (shopCategory != null && shopCategory.getShopCategoryId()!=null) {
                try {
                    shopCategory.setShopCategoryName((shopCategory
                            .getShopCategoryName() == null) ? null : (URLDecoder
                            .decode(shopCategory.getShopCategoryName(), "UTF-8")));
                    shopCategory.setShopCategoryDesc((shopCategory
                            .getShopCategoryDesc() == null) ? null : (URLDecoder
                            .decode(shopCategory.getShopCategoryDesc(), "UTF-8")));
                    int eff =shopCategoryDao.updateShopCategory(shopCategory);
                    if (eff>0){
                        modelMap.put("success", true);
                        cacheService.removeFromCache(shopCategoryService.SCLISTKEY);//清楚缓存
                        return modelMap;
                    }else {
                        modelMap.put("success", false);
                        modelMap.put("errMsg", e.toString());
                        return modelMap;
                    }
                }catch (Exception e1) {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", e1.toString());
                    return modelMap;
                }

            }
            else {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }
        }
        if (shopCategory != null && shopCategory.getShopCategoryId() != null) {
            try {
                boolean thumbnailChange = HttpServletRequestUtil.getBoolean(
                        request, "thumbnailChange");
                // decode可能有中文的地方
                shopCategory.setShopCategoryName((shopCategory
                        .getShopCategoryName() == null) ? null : (URLDecoder
                        .decode(shopCategory.getShopCategoryName(), "UTF-8")));
                shopCategory.setShopCategoryDesc((shopCategory
                        .getShopCategoryDesc() == null) ? null : (URLDecoder
                        .decode(shopCategory.getShopCategoryDesc(), "UTF-8")));
                ShopCategoryExecution ae = shopCategoryService
                        .modifyShopCategory(shopCategory, thumbnail,
                                thumbnailChange);
                if (ae.getState() == ShopCategoryStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", ae.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入店铺类别信息");
        }
        return modelMap;
    }

    @RequestMapping(value = "/removeshopcategory", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> removeShopCategory(Long shopCategoryId) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (shopCategoryId != null && shopCategoryId > 0) {
            try {
                ShopCategoryExecution ae = shopCategoryService
                        .removeShopCategory(shopCategoryId);
                if (ae.getState() == ShopCategoryStateEnum.SUCCESS.getState()) {
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
            modelMap.put("errMsg", "请输入店铺类别信息");
        }
        return modelMap;
    }

    @RequestMapping(value = "/removeshopcategories", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> removeShopCategories(
            String shopCategoryIdListStr) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(
                ArrayList.class, Long.class);
        List<Long> shopCategoryIdList = null;
        try {
            shopCategoryIdList = mapper.readValue(shopCategoryIdListStr,
                    javaType);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
        }
        if (shopCategoryIdList != null && shopCategoryIdList.size() > 0) {
            try {
                ShopCategoryExecution ae = shopCategoryService
                        .removeShopCategoryList(shopCategoryIdList);
                if (ae.getState() == ShopCategoryStateEnum.SUCCESS.getState()) {
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
            modelMap.put("errMsg", "请输入店铺类别信息");
        }
        return modelMap;
    }
}

