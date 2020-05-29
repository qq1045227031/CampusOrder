package com.imooc.o2o.util;

public class PageCalculator {
    //sql语句中limit 5,6的意思是从第五个也就是索引为4的开始取取留个数而我们一般传入的是页数，而不是起始数，所以传入的页数要转换为起始数
    public static int calculateRowIndex(int pageIndex,int pageSize){
        return (pageIndex>0)?(pageIndex-1)*pageSize:0;
    }
}
