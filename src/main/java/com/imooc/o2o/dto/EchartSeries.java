package com.imooc.o2o.dto;

import java.util.List;

//商品名 类型(bar树状图 默认) 销量
public class EchartSeries {
    private String name;
    private String type ="bar";
    private List<Integer> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }
}
