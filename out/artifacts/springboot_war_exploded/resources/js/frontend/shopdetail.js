$(function() {
    //加载符号，如果正在加载就推出
    var loading = false;
    //分页最大条数
    var maxItems = 20;
    //默认一页返回的商品数
    var pageSize = 10;
    //列出商品列表的url
    var listUrl = '/springboot/frontend/listproductsbyshop';
//默认页码
    var pageNum = 1;
    var shopId = getQueryString('shopId');
    var productCategoryId = '';
    var productName = '';
//获取本店信息以及商品类别信息的url
    var searchDivUrl = '/springboot/frontend/listshopdetailpageinfo?shopId='
        + shopId;
    //奖品兑换
    $('#exchangelist').attr('href','/springboot/frontend/awardlist?shopId='+shopId);

    function getSearchDivData() {
        var url = searchDivUrl;
        $
            .getJSON(
                url,
                function(data) {
                    if (data.success) {
                        var shop = data.shop;
                        $('#shop-cover-pic').attr('src', shop.shopImg);
                        $('#shop-update-time').html(
                            new Date(shop.lastEditTime)
                                .Format("yyyy-MM-dd"));
                        $('#shop-name').html("店名:"+shop.shopName);
                        $('#shop-desc').html("描述:"+shop.shopDesc);
                        $('#shop-addr').html("地址:"+shop.shopAddr);
                        $('#shop-phone').html("电话："+shop.phone);
                        $('#shopname').html(shop.shopName)
                        //展示商品类别列表
                        var productCategoryList = data.productCategoryList;
                        var html = '';
                        productCategoryList
                            .map(function(item, index) {
                                html += '<a href="#" class="button" data-product-search-id='
                                    + item.productCategoryId
                                    + '>'
                                    + item.productCategoryName
                                    + '</a>';
                            });
                        $('#shopdetail-button-div').html(html);
                    }
                });
    }
    getSearchDivData();

    function addItems(pageSize, pageIndex) {
        // 生成新条目的HTML  查询商品
        var url = listUrl + '?' + 'pageIndex=' + pageIndex + '&pageSize='
            + pageSize + '&productCategoryId=' + productCategoryId
            + '&productName=' + productName + '&shopId=' + shopId;
        loading = true;//设定加载符，若还在后台取数据则不能再次访问
        $.getJSON(url, function(data) {
            if (data.success) {
                maxItems = data.count;
                var html = '';
                data.productList.map(function(item, index) {
                    html += '' + '<div class="card" data-product-id='
                        + item.productId + '>'
                        + '<div class="card-header">' + item.productName
                        + '</div>' + '<div class="card-content">'
                        + '<div class="list-block media-list">' + '<ul>'
                        + '<li class="item-content">'
                        + '<div class="item-media">' + '<img src="'
                        + item.imgAddr + '" width="44">' + '</div>'
                        + '<div class="item-inner">'
                        + '<div class="item-subtitle">' + item.productDesc
                        + '</div>' + '</div>' + '</li>' + '</ul>'
                        + '</div>' + '</div>' + '<div class="card-footer">'
                        + '<p class="color-gray">'
                        + new Date(item.lastEditTime).Format("yyyy-MM-dd")
                        + '更新</p>' + '<span>点击查看</span>' + '</div>'
                        + '</div>';
                });
                $('.list-div').append(html);
                //获取没目前为止已显示的卡片总数，包含之前已经加载的
                var total = $('.list-div .card').length;
                //若总数达到此查询条件总数一直，则停驶后台继续加载
                if (total >= maxItems) {
                    // // 加载完毕，则注销无限加载事件，以防不必要的加载
                    // $.detachInfiniteScroll($('.infinite-scroll'));
                    // 删除加载提示符
                    $('.infinite-scroll-preloader').remove();
                }else {
                    $('.infinite-scroll-preloader').show();
                }
                //否则页码加以，继续load出新的店铺
                pageNum += 1;
                //加载结束，可以再次加载了
                loading = false;
                //刷新页面，显示新加载的店铺
                $.refreshScroller();
            }
        });
    }

    addItems(pageSize, pageNum);
//下滑屏幕自动进行分页搜索
    $(document).on('infinite', '.infinite-scroll-bottom', function() {
        if (loading)
            return;
        addItems(pageSize, pageNum);
    });
//选择新的商品类别后，重置页面，清空原先的商品列表，按照新的类别查询
    $('#shopdetail-button-div').on(
        'click',
        '.button',
        function(e) {
            productCategoryId = e.target.dataset.productSearchId;
            if (productCategoryId) {
                if ($(e.target).hasClass('button-fill')) {
                    $(e.target).removeClass('button-fill');
                    productCategoryId = '';
                } else {
                    $(e.target).addClass('button-fill').siblings()
                        .removeClass('button-fill');
                }
                $('.list-div').empty();
                pageNum = 1;
                addItems(pageSize, pageNum);
            }
        });
//转发到商品详情
    $('.list-div')
        .on(
            'click',
            '.card',
            function(e) {
                var productId = e.currentTarget.dataset.productId;
                window.location.href = '/springboot/frontend/productdetail?productId='
                    + productId;
            });
//需要查询的商品名字发生变化后，重置页码，清空原先的商品列表，按照新的名字去查询
    $('#search').on('change', function(e) {
        productName = e.target.value;
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
