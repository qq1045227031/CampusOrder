$(function () {
    var loading = false;
    var maxItems = 999;
    var pageSize = 10
    var listUrl = '/springboot/frontend/listawardsbyshop';
    var exchangeUrl ='/springboot/frontend/adduserawardmap';
    var pageNum = 1;
    var shopId = getQueryString("shopId");
    var awardName='';
    var canProceed = false;
    var totalPoint = 0;
    //预先加载20
    addItems(pageSize,pageNum);
    function addItems(pagesize,pageIndex) {
        var url = listUrl +'?'+'pageIndex='+pageIndex+'&pageSize='+pagesize+'&shopId='+shopId+'&awardName='+awardName;
        loading=true;
        $.getJSON(url,function (data) {
            if (data.success){
                maxItems = data.count;
                var html = '';
                data.awardList.map(function (item,index) {
                    html+=''+'<div class="card" data-award-id="'+item.awardId+'"data-point="' +item.point+'">' +
                        ''+'<div class="card-header">'+item.awardName +'<span class="pull-right">需要积分'+item.point+'</span></div>'
                        + '<div class="card-content">'+'<div class="list-block media-list">'+'<ul>' +'<li class="item-content">'+'<div class="item-meadia">'+'<img src="' +item.awardImg+'"width="44">'+ '</div>'
                    +'<div class="item-inner">'
                     +'<div class="item-subtitle">'+item.awardDesc +'</div>'+'</div>' +'</li>'+'</ul>'
                    +'</div>'
                    +'</div>'
                     +'<div class="card-footer">'
                    +'<p class="color-gray">'+new Date(item.lastEditTime).Format("yyyy-MM-dd")
                    + '更新</p>';
                    if (data.totalPoint!=undefined){
                        html+='<span>点击领取</span></div></div>'
                    }else {
                        html+='</div></div>'
                    }
                });

                $('.list-div').append(html);
                //该用户在该电批的积分
                if (data.totalPoint!=undefined){
                    canProceed = true;
                    $('#title').text('当前积分'+data.totalPoint);
                    totalPoint = data.totalPoint;
                }
                var total = $('.list-div .card').length;
                if (total>maxItems){
                    //加载完毕，则注销无限加载时间，一方不必要的加载
                    $.detachInfiniteScroll($('.infinite-scroll'));
                    $('.infinite-scroll-preloader').remove();
                    return;
                }
                pageNum+=1;
                loading= false;
                $.refreshScroller();

            }

        });
    }
    function getContextPath() {
        var pathName = document.location.pathname;
        var index = pathName.substr(1).indexOf("/");
        var result = pathName.substr(0,index+1);
        return result;
    }
    $(document).on('infinite','infinite-scroll-bottom',function () {
        //无极滚动
        if (loading)
            return;
        addItems(pageSize,pageNum);
    });

    $('.award-list').on('click','.card',function (e) {
        //该用户在该商铺积分大于该商品需要消耗的积分
        if (canProceed&&(totalPoint>e.currentTarget.dataset.point)){
            $.confirm('需要消耗'+e.currentTarget.dataset.point+"积分，确定操作吗",function () {
                $.ajax({
                    url:exchangeUrl,
                    type: "get",
                    data:{
                        awardId:e.currentTarget.dataset.awardId
                    },
                    dataType: "json",
                    success:function (data) {
                        if (data.success){
                            $.toast("操作成功!");
                            totalPoint=totalPoint-e.currentTarget.dataset.point;
                            $('#title').text('当前积分'+totalPoint);
                        }else {
                            $.toast("操作失败!");
                        }

                    }

                });

            });
        }else {
            $.toast("积分不足或无权操作！");
        }

    });
    //需要查询的商品名字发生变化后，重置页码，清空原先的商品列表，按照新的名字去查询
    $('#search').on('change', function(e) {
        awardName = e.target.value;
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageSize, pageNum);
    });
//右侧栏
    $('#me').click(function() {
        $.openPanel('#panel-left-demo');
    });
    $.init();


});