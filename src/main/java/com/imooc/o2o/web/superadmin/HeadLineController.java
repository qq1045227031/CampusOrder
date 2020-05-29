package com.imooc.o2o.web.superadmin;


import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.imooc.o2o.dao.HeadLineDao;
import com.imooc.o2o.dto.ImageHolder;
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
import com.imooc.o2o.dto.HeadLineExecution;
import com.imooc.o2o.entity.ConstantForSuperAdmin;
import com.imooc.o2o.entity.HeadLine;
import com.imooc.o2o.enums.HeadLineStateEnum;
import com.imooc.o2o.service.HeadLineService;
import com.imooc.o2o.util.HttpServletRequestUtil;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Controller
@RequestMapping("/superadmin")
public class HeadLineController {
    @Autowired
    private HeadLineService headLineService;
    @Autowired
    private HeadLineDao headLineDao;
    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "/listheadlines", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> listHeadLines(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        List<HeadLine> list = new ArrayList<HeadLine>();
        try {
            Integer enableStatus = HttpServletRequestUtil.getInt(request,
                    "enableStatus");
            HeadLine headLine = new HeadLine();
            if (enableStatus > -1) {
                headLine.setEnableStatus(enableStatus);
            }
            list = headLineService.getHeadLineList(headLine);
            modelMap.put(ConstantForSuperAdmin.PAGE_SIZE, list);
            modelMap.put(ConstantForSuperAdmin.TOTAL, list.size());
        } catch (Exception e) {
            e.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
        }
        return modelMap;
    }

    @RequestMapping(value = "/addheadline", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addHeadLine(HttpServletRequest request) throws IOException{
        Map<String, Object> modelMap = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        HeadLine headLine = null;
        String headLineStr = HttpServletRequestUtil.getString(request,
                "headLineStr");
        ImageHolder thumbnail = null;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest
                .getFile("headTitleManagementAdd_lineImg");
        thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        try {
            headLine = mapper.readValue(headLineStr, HeadLine.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        if (headLine != null && thumbnail != null) {
            try {
                // decode可能有中文的地方
                headLine.setLineName((headLine.getLineName() == null) ? null
                        : URLDecoder.decode(headLine.getLineName(), "UTF-8"));
                headLine.setLineLink(headLine.getLineLink()==null?null
                        :URLDecoder.decode(headLine.getLineLink(), "UTF-8"));
                HeadLineExecution ae = headLineService.addHeadLine(headLine,
                        thumbnail);
                if (ae.getState() == HeadLineStateEnum.SUCCESS.getState()) {
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
            modelMap.put("errMsg", "请输入头条信息");
        }
        return modelMap;
    }

    @RequestMapping(value = "/modifyheadline", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> modifyHeadLine(HttpServletRequest request) throws IOException {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        HeadLine headLine = null;
        String headLineStr = HttpServletRequestUtil.getString(request,
                "headLineStr");
        ImageHolder thumbnail = null;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest
//                .getFile("headTitleManagementEdit_lineImg");
//        thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());

        try {
            headLine = mapper.readValue(headLineStr, HeadLine.class);
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
        } catch (Exception e) {
            if (headLine != null && headLine.getLineId() != null) {
                try {
                    // decode可能有中文的地方
                    headLine.setLineName((headLine.getLineName() == null) ? null
                            : URLDecoder.decode(headLine.getLineName(), "UTF-8"));
                    headLine.setLineLink(headLine.getLineLink()==null?null
                            :URLDecoder.decode(headLine.getLineLink(), "UTF-8"));
                    int eff =headLineDao.updateHeadLine(headLine);
                    if (eff>0) {
                        modelMap.put("success", true);
                        cacheService.removeFromCache(headLineService.HLLISTKEY);
                        return  modelMap;
                    } else {
                        modelMap.put("success", false);
                        modelMap.put("errMsg", "插入失败");
                    }
                } catch (Exception e2) {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", e2.toString());
                    return modelMap;
                }
            }else {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }
        }
        if (headLine != null && headLine.getLineId() != null) {
            try {
                // decode可能有中文的地方
                headLine.setLineName((headLine.getLineName() == null) ? null
                        : URLDecoder.decode(headLine.getLineName(), "UTF-8"));
                headLine.setLineLink(headLine.getLineLink()==null?null
                        :URLDecoder.decode(headLine.getLineLink(), "UTF-8"));
                HeadLineExecution ae = headLineService.modifyHeadLine(headLine,
                        thumbnail);
                if (ae.getState() == HeadLineStateEnum.SUCCESS.getState()) {
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
            modelMap.put("errMsg", "请输入头条信息");
        }
        return modelMap;
    }
    private ImageHolder handlerImage(MultipartHttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest;
        ImageHolder thumbnail;
        multipartRequest = request;
        //去除缩略图并构建ImageHolder对象
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest
                .getFile("headTitleManagementEdit_lineImg");
        //这是缩略图的ImageHolder类
        thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        return thumbnail;
    }

    @RequestMapping(value = "/removeheadline", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> removeHeadLine(Long headLineId) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (headLineId != null && headLineId > 0) {
            try {
                HeadLineExecution ae = headLineService
                        .removeHeadLine(headLineId);
                if (ae.getState() == HeadLineStateEnum.SUCCESS.getState()) {
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
            modelMap.put("errMsg", "请输入头条信息");
        }
        return modelMap;
    }

    @RequestMapping(value = "/removeheadlines", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> removeHeadLines(String headLineIdListStr) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(
                ArrayList.class, Long.class);
        List<Long> headLineIdList = null;
        try {
            headLineIdList = mapper.readValue(headLineIdListStr, javaType);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
        }
        if (headLineIdList != null && headLineIdList.size() > 0) {
            try {
                HeadLineExecution ae = headLineService
                        .removeHeadLineList(headLineIdList);
                if (ae.getState() == HeadLineStateEnum.SUCCESS.getState()) {
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
            modelMap.put("errMsg", "请输入区域信息");
        }
        return modelMap;
    }

}
