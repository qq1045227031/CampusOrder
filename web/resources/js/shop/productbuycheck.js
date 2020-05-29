$(function() {

    var productName = '';
    var listUrl = '/springboot/shopadmin/listuserproductmapsbyshop?pageIndex=1&pageSize=9999&productName=' + productName;
    function getList() {
        $.getJSON(listUrl, function (data) {
            if (data.success) {
                var userProductMapList = data.userProductMapList;
                var tempHtml = '';
                userProductMapList.map(function (item, index) {
                    tempHtml += ''
                        +'<div class="row row-productbuycheck">'
                        +'<div class="col-10">'+ item.product.productName +'</div>'
                        +'<div class="col-40 productbuycheck-time">'+ new Date(item.createTime).Format("yyyy-MM-dd hh:mm:ss")+'</div>'
                        +'<div class="col-20">'+ item.user.name +'</div>'
                        +'<div class="col-10">'+ item.point+'</div>'
                        +'<div class="col-20">'+item.operator.name+'</div> '
                        + '</div>';
                });
                $('.productbuycheck-wrap').html(tempHtml);
            }
        });
    }
    getList();
    $('#search').on('change', function (e) {//input?
        productName = e.target.value;
        $('.productbuycheck-wrap').empty();
        getList();
    });



function getProductSellDailyList() {
    var listProductSellDailyUrl = '/springboot/shopadmin/listproductselldailyinfobyshop';
    $.getJSON(listProductSellDailyUrl,function (data) {
        if (data.success){
            var myChart = echarts.init(document.getElementById("chart"));
            //生成静态Echart信息部分
            var option = generateStaticEchartPart();
            //遍历销量系统列表 动态设定echarts的值
            option.legend.data = data.legendData;
            option.xAxis =  data.xAxis;//种类 和 周几
            option.series = data.series;//商品名 类型(bar树状图 默认) 销量
            myChart.setOption(option);

        }

    });

}
function generateStaticEchartPart() {
    var option = {
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
            legend: {
                data:['茉莉奶茶','绿茶拿铁','冰雪奇缘']
            },
        //直角坐标系内绘网格
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        //横轴数组
        xAxis : [
            {
            }
        ],
        //中轴数组 数组中每一项代表一条纵轴坐标轴
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : [{}]
    };
    return option;

}
    getProductSellDailyList();


    //
    // var myChart = echarts.init(document.getElementById('chart'));
    //
    // var option = {
    //     tooltip : {
    //         trigger: 'axis',
    //         axisPointer : {            // 坐标轴指示器，坐标轴触发有效
    //             type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
    //         }
    //     },
    //     //图例
    //     legend: {
    //         data:['茉莉奶茶','绿茶拿铁','冰雪奇缘']
    //     },
    //     //直角坐标系内绘网格
    //     grid: {
    //         left: '3%',
    //         right: '4%',
    //         bottom: '3%',
    //         containLabel: true
    //     },
    //     //横轴数组
    //     xAxis : [
    //         {
    //             type : 'category',
    //             data : ['周一','周二','周三','周四','周五','周六','周日']
    //         }
    //     ],
    //     //中轴数组 数组中每一项代表一条纵轴坐标轴
    //     yAxis : [
    //         {
    //             type : 'value'
    //         }
    //     ],
    //     series : [
    //         {
    //             name:'茉莉奶茶',
    //             type:'bar',
    //             data:[120, 132, 101, 134, 290, 230, 220]
    //         },
    //         {
    //             name:'绿茶拿铁',
    //             type:'bar',
    //             data:[60, 72, 71, 74, 190, 130, 110]
    //         },
    //         {
    //             name:'冰雪奇缘',
    //             type:'bar',
    //             data:[62, 82, 91, 84, 109, 110, 120]
    //         }
    //     ]
    // };
    //
    // myChart.setOption(option);
});