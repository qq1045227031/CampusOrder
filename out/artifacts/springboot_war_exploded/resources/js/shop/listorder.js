$(function () {
    var productName = '';
    var listUrl = '/springboot/shopadmin/listuserproductmapsbyshop?pageIndex=1&pageSize=9999&productName=' + productName;
    function getList() {
        $.getJSON(listUrl, function (data) {
            if (data.success) {
                var userProductMapList = data.userProductMapList;
                var tempHtml = '';
                userProductMapList.map(function (item, index) {
                    var ht = '';
                    if (item.operator.userId==1){
                        ht  = '<a href="#" class="status" data-id="'+item.userProductId+'">'+'完成订单' + '</a>';
                    }else {
                       ht = '配送完成';
                    }
                    tempHtml += ''
                        +'<div class="row row-productbuycheck">'
                        +'<div class="col-20">'+ item.product.productName +'</div>'
                        +'<div class="col-30 productbuycheck-time">'+ new Date(item.createTime).Format("MM-dd hh:mm")+'</div>'
                        +'<div class="col-20">'+ item.user.name +'</div>'
                        +'<div class="col-10">'+ item.product.promotionPrice+'</div>'
                        +'<div class="col-20">'+ht+'</div> '

                        +'<div class="col-50">地址:'+item.user.address+'</div>'
                        +'<div class="col-50">手机:'+item.user.phone+'</div>'       + '</div>';
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
    $('.productbuycheck-wrap').on('click','a',function (e) {
        var target = $(e.currentTarget);//触发事件的结点
        if (target.hasClass('status')) {
            //如果有calss status 则调用后台上架下架相关商品，并滴啊有productId参数
            //.toast()无法生效
            $.confirm('确定完成订单？',function () {
                changeItemStatus(e.currentTarget.dataset.id);
            })
            //   var panduan  = confirm('确定完成订单吗');
            //   if (panduan==true){
            //     changeItemStatus(e.currentTarget.dataset.id);
            //   }
        }
    });
    function changeItemStatus(id){
        var userProductId = id;
        $.ajax({
            url:'/springboot/shopadmin/overlist',
            data:{
                userProductId:userProductId
            },
            type:'post',
            dataType:'json',
            success:function (data) {
                if (data.success){
                    $.toast('完成订单成功');
                    getList();
                }else {
                    $.toast('完成订单失败');
                }

            }
        });
    }
    //因为有a标签和#无法生效?
    // $('.status').click(function (e) {
    //     var userProductId = e.currentTarget.dataset.id;
    //     $.confirm('确定完成订单？',function () {
    //         $.ajax({
    //             url:'/springboot/shopadmin/overlist',
    //             data:{
    //                 userProductId:userProductId
    //             },
    //             type:'post',
    //             dataType:'json',
    //             success:function (data) {
    //                 if (data.success){
    //                     $.toast('完成订单成功');
    //                     getList();
    //                 }else {
    //                     $.toast('完成订单失败');
    //                 }
    //
    //             }
    //         });
    //
    //     });
    //
    // })
})