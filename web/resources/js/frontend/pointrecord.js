$(function() {
    var loading = false;
    var maxItems = 20;
    var pageSize = 100;

    var listUrl = '/springboot/frontend/listuserawardmapbycustomer';

    var pageNum = 1;
    var awardName = '';

    function addItems(pageSize, pageIndex) {
        // 生成新条目的HTML
        var url = listUrl + '?pageIndex=' + pageIndex
            + '&pageSize=' + pageSize + '&awardName=' + awardName;
        loading = true;
        var test='';
        $.getJSON(url, function(data) {
            if (data.success) {
                maxItems = data.count;
                var html = '';
                data.userAwardMapList.map(function(item, index) {
                    var status ='';
                    if (item.usedStatus==0){
                        status = "未领取";
                    }else {
                        status = "已领取";
                    }
                    html += '' + '<div class="card" data-id="'
                        + item.userAwardId
                        +  '">'
                        + '<div class="card-header">' + item.shop.shopName
                        +'<span class="pull-right">'+status
                        + '</span></div>' + '<div class="card-content">'
                        + '<div class="list-block media-list">' + '<ul>'
                        + '<li class="item-content">'
                        + '<div class="item-inner">'
                        + '<div class="item-subtitle">' + item.award.awardName
                        + '</div>' + '</div>' + '</li>' + '</ul>'
                        + '</div>' + '</div>' + '<div class="card-footer">'
                        + '<p class="color-gray">'
                        + new Date(item.createTime).Format("yyyy-MM-dd")
                        + '</p>' + '<span>消费积分:' + item.point + '</span>'
                        + '</div>' + '</div>';
                });
                $('.list-div').append(html);
                var total = $('.list-div .card').length;
                if (total >= maxItems) {
                    // 加载完毕，则注销无限加载事件，以防不必要的加载
                    $.detachInfiniteScroll($('.infinite-scroll'));
                    // 删除加载提示符
                    $('.infinite-scroll-preloader').remove();
                    return;
                }
                pageNum += 1;
                loading = false;
                $.refreshScroller();
            }
        });
    }
$('.list-div').on('click','.card',function (e) {
    var userAwardId = e.currentTarget.dataset.id;
    window.location.href='/springboot/frontend/myawarddetail?userAwardId='+userAwardId;

})
    addItems(pageSize, pageNum);//

    $(document).on('infinite', '.infinite-scroll-bottom', function() {
        if (loading)
            return;
        addItems(pageSize, pageNum);
    });

    $('#search').on('input', function(e) {
        productName = e.target.value;
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageSize, pageNum);
    });

    $('#me').click(function() {
        $.openPanel('#panel-left-demo');
    });
    $.init();
});