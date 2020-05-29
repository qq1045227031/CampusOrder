$(function() {
    var url = '/springboot/frontend/listmainpageinfo';
    var pageSize = 10;//最大条数
    var pageNum = 1;
    var listUrl = '/springboot/frontend/listshops';
    $.getJSON(url, function (data) {
        if (data.success) {
            var headLineList = data.headLineList;
            var swiperHtml = '';
            headLineList.map(function (item, index) {
                swiperHtml += ''
                            + '<div class="swiper-slide img-wrap">'
                            +'<a href="'+item.lineLink+'"external>'
                            +      '<img class="banner-img" src="'+ item.lineImg +'" alt="'+ item.lineName +'">'
                            + '</div>';
            });
            $('.swiper-wrapper').html(swiperHtml);
            $(".swiper-container").swiper({
                autoplay: 1000,
                autoplayDisableOnInteraction: true    //false代表选中仍改变图片 true代表鼠标选中不改图片
            });
            var shopCategoryList = data.shopCategoryList;
            var categoryHtml = '';
            shopCategoryList.map(function (item, index) {
                categoryHtml += ''
                             +  '<div class="col-50 shop-classify" data-category='+ item.shopCategoryId +'>'
                             +      '<div class="word">'
                             +          '<p class="shop-title">'+ item.shopCategoryName +'</p>'
                             +          '<p class="shop-desc">'+ item.shopCategoryDesc +'</p>'
                             +      '</div>'
                             +      '<div class="shop-classify-img-warp">'
                             +          '<img class="shop-img" src="'+ item.shopCategoryImg +'">'
                             +      '</div>'
                             +  '</div>';
            });
            $('.row').html(categoryHtml);
        }
    });
    //侧边栏
    $('#me').click(function () {
        $.openPanel('#panel-left-demo');
    });
    //点击用户类别进入一级类别下的商店
    $('.row').on('click', '.shop-classify', function (e) {
        var shopCategoryId = e.currentTarget.dataset.category;
        var newUrl = '/springboot/frontend/shoplist?parentId=' + shopCategoryId;
        window.location.href = newUrl;
    });
    function addItems(pageSize, pageIndex) {
        //拼接出查询的url，赋空值默认去掉这个条件限制，有值则代表按照条件查找
        // 生成新条目的HTML listUrl='/frontend/listshops';
        var url = listUrl + '?' + 'pageIndex=' + pageIndex + '&pageSize='
            + pageSize;
        loading = true;
        //访问后台获取相应查询条件的商品列别
        $.getJSON(url, function(data) {
            if (data.success) {
                //总数
                maxItems = data.count;
                var html = '';
                //便利商铺列表，凭借出卡片集合
                data.shopList.map(function(item, index) {
                    html += '' + '<div class="card" data-shop-id="'
                        + item.shopId + '">' + '<div class="card-header">'
                        + item.shopName + '</div>'
                        + '<div class="card-content">'
                        + '<div class="list-block media-list">' + '<ul>'
                        + '<li class="item-content">'
                        + '<div class="item-media">' + '<img src="'
                        + item.shopImg + '" width="44">' + '</div>'
                        + '<div class="item-inner">'
                        + '<div class="item-subtitle">' + item.shopDesc
                        + '</div>' + '</div>' + '</li>' + '</ul>'
                        + '</div>' + '</div>' + '<div class="card-footer">'
                        + '<p class="color-gray">'
                        + new Date(item.lastEditTime).Format("yyyy-MM-dd")
                        + '更新</p>' + '<span>点击查看</span>' + '</div>'
                        + '</div>';
                });
                //添加到组件中
                $('.list-div').append(html);
                //到目前为止已显示的卡片总数，包含之前已经加载的
                var total = $('.list-div .card').length;
                //若总数大于查询条件，则停止加载
                if (total >= maxItems) {
                    // 加载完毕，则注销无限加载事件，以防不必要的加载
                    // $.detachInfiniteScroll($('.infinite-scroll'));有bug
                    // 删除加载提示符
                    // 加载完毕，则注销无限加载事件，以防不必要的加载
                    $.detachInfiniteScroll($('.infinite-scroll'));
                    // 删除加载提示符
                    $('.infinite-scroll-preloader').remove();
                }else {
                    $('.infinite-scroll-preloader').show();
                }
                //否则页码加以，继续load出新的商铺
                pageNum += 1;
                //加载结束，可以再次加载了
                loading = false;
                //刷新页面，显示新加载的店铺
                $.refreshScroller();
            }
        });
    }
    // 预先加载20条
    addItems(pageSize, pageNum);
    $(document).on('infinite', '.infinite-scroll-bottom', function() {
        if (loading)
            return;
        addItems(pageSize, pageNum);
    });
    $('.shop-list').on('click', '.card', function(e) {
        var shopId = e.currentTarget.dataset.shopId;
        window.location.href = '/springboot/frontend/shopdetail?shopId=' + shopId;//点击商铺跳转到改商铺
    });
});
