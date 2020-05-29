package com.imooc.o2o.dto;

import java.util.HashSet;
//种类 和 周几
public class EchartXAxis {
    private String type = "category";
    //是用hashset去重
    private HashSet<String> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashSet<String> getData() {
        return data;
    }

    public void setData(HashSet<String> data) {
        this.data = data;
    }
}
