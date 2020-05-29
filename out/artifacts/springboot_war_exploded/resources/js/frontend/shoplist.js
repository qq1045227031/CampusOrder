$(function() {
	var loading = false;
	//分页循序最大条数，超过禁止访问后台
	var maxItems = 999;
	var pageSize = 10;//最大条数
	//获取商铺列表的url
	var listUrl = '/springboot/frontend/listshops';
	//获取商铺类别以及区域的url
	var searchDivUrl = '/springboot/frontend/listshopspageinfo';
	//页码
	var pageNum = 1;
	//从url链接获取parent shop category id
	var parentId = getQueryString('parentId');
	var areaId = '';
	var shopCategoryId = '';
	var shopName = '';
	//获取商铺列表以及区域信息
	function getSearchDivData() {
		//如果有parentId获取改类别的子类别  没有则为parentId为空的以及列表
		var url = searchDivUrl + '?' + 'parentId=' + parentId;
		$
				.getJSON(
						url,
						function(data) {
							//获取后台返回的商铺类别列表
							if (data.success) {
								var shopCategoryList = data.shopCategoryList;
								var html = '';
								html += '<a href="#" class="button" data-category-id=""> 全部类别  </a>';
								//遍历商铺列表，拼接出a标签
								shopCategoryList
										.map(function(item, index) {
											html += '<a href="#" class="button" data-category-id='
													+ item.shopCategoryId
													+ '>'
													+ item.shopCategoryName
													+ '</a>';
										});
								//拼接好的标签插入其那台组件
								$('#shoplist-search-div').html(html);
								var selectOptions = '<option value="">全部街道</option>';
								//后台返回的区域信息
								var areaList = data.areaList;
								areaList.map(function(item, index) {
									selectOptions += '<option value="'
											+ item.areaId + '">'
											+ item.areaName + '</option>';
								});
								$('#area-search').html(selectOptions);
							}
						});
	}
	//渲染出商铺类别列表以及区域列表以搜索
	getSearchDivData();

	function addItems(pageSize, pageIndex) {
		//拼接出查询的url，赋空值默认去掉这个条件限制，有值则代表按照条件查找
		// 生成新条目的HTML listUrl='/frontend/listshops';
		var url = listUrl + '?' + 'pageIndex=' + pageIndex + '&pageSize='
				+ pageSize + '&parentId=' + parentId + '&areaId=' + areaId
				+ '&shopCategoryId=' + shopCategoryId + '&shopName=' + shopName;
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
//下滑屏幕自动进行分页搜索
	$(document).on('infinite', '.infinite-scroll-bottom', function() {
		if (loading)
			return;
		addItems(pageSize, pageNum);
	});
//点击商铺卡片进入该商铺的详情页
	$('.shop-list').on('click', '.card', function(e) {
		var shopId = e.currentTarget.dataset.shopId;
		window.location.href = '/springboot/frontend/shopdetail?shopId=' + shopId;//点击商铺跳转到改商铺
	});
//点击商店种类，重置页码，清空原先的商铺列表，按照新的列表去拆线呢
	$('#shoplist-search-div').on(
			'click',
			'.button',
			function(e) {
				if (parentId) {// 如果传递过来的是一个父类下的子类
					shopCategoryId = e.target.dataset.categoryId;
					//若之前已选定其他的category则删除其选定选过，改成选新的
					if ($(e.target).hasClass('button-fill')) {
						$(e.target).removeClass('button-fill');
						shopCategoryId = '';//这是商铺类别选定会有特效，这是转移特效
					} else {
						$(e.target).addClass('button-fill').siblings()
								.removeClass('button-fill');
					}
					//由于查询条件改变，清空商铺列表再次进行查询
					$('.list-div').empty();
					//重置页码
					pageNum = 1;
					addItems(pageSize, pageNum);
				} else {// 如果传递过来的父类为空，则按照父类查询
					parentId = e.target.dataset.categoryId;
					if ($(e.target).hasClass('button-fill')) {
						$(e.target).removeClass('button-fill');
						parentId = '';//改变特效
					} else {
						$(e.target).addClass('button-fill').siblings()
								.removeClass('button-fill');
					}
					$('.list-div').empty();
					pageNum = 1;
					addItems(pageSize, pageNum);
					parentId = '';
				}

			});
//查询的店铺名字发生变化后，重置页码，清空原先的商铺列表，按照新的名字传销
	$('#search').on('change', function(e) {
		shopName = e.target.value;
		$('.list-div').empty();
		pageNum = 1;
		addItems(pageSize, pageNum);
	});
//区域信息发生变化后，充值页码，清空原先的商铺列表，按照新的区域去查询
	$('#area-search').on('change', function() {
		areaId = $('#area-search').val();
		$('.list-div').empty();
		pageNum = 1;
		addItems(pageSize, pageNum);
	});

	$('#me').click(function() {
		$.openPanel('#panel-left-demo');
	});

	$.init();
});
