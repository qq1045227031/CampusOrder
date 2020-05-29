$(function() {
    var awardName = '';

    function getList() {
        var listUrl = '/springboot/shopadmin/listuserawardmapsbyshop?pageIndex=1&pageSize=9999&awardName=' + awardName;
        $.getJSON(listUrl, function(data) {
            if (data.success) {
                var userAwardMapList = data.userAwardMapList;
                var tempHtml = '';
                userAwardMapList.map(function(item, index) {
                    var ht = '';
                    if (item.usedStatus==0){
                        ht  = '<a href="#" class="status" data-id="'+item.userAwardId+'">'+'完成' + '</a>';
                    }else {
                        ht = '已领取';
                    }
                    tempHtml += '' + '<div class="row row-awarddeliver">'
                        + '<div class="col-10">' + item.award.awardName
                        + '</div>'
                        + '<div class="col-40 awarddeliver-time">'
                        + new Date(item.createTime).Format("MM-dd hh:mm") + '</div>'
                        + '<div class="col-20">' + item.user.name + '</div>'
                        + '<div class="col-10">' + item.point + '</div>'
                        + '<div class="col-20">' + ht+ '</div>'
                        + '</div>';
                });
                $('.awarddeliver-wrap').html(tempHtml);
            }
        });
    }

    $('#search').on('input', function(e) {
        awardName = e.target.value;
        $('.awarddeliver-wrap').empty();
        getList();
    });
    getList();
    $('.awarddeliver-wrap').on('click','a',function (e) {
        var target = $(e.currentTarget);//触发事件的结点
        if (target.hasClass('status')) {
            //如果有calss status 则调用后台上架下架相关商品，并滴啊有productId参数
            //.toast()无法生效
            $.confirm('确定完成奖品订单？',function () {
                changeItemStatus(e.currentTarget.dataset.id);
            })
            // var panduan  = confirm('确定完成奖品领取吗');
            // if (panduan==true){
            //     changeItemStatus(e.currentTarget.dataset.id);
            // }
        }
    });
    function changeItemStatus(id){
        var userAwardId= id;
        $.ajax({
            url:'/springboot/shopadmin/overawardlist',
            data:{
                userAwardId:userAwardId
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

});