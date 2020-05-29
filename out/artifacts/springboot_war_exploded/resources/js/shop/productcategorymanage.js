$(function() {
    // var shopId = getQueryString("shopId");不需要了，后台通过取session的shop进行查询商铺类别
    var listUrl = '/springboot/shopadmin/getproductcategory';
    var addUrl = '/springboot/shopadmin/addproductcategorys';
    var deleteUrl = '/springboot/shopadmin/removeproductcategory';
    $.getJSON(
            listUrl,
            function (data) {
                if (data.success) {
                    var dataList = data.data;
                    $('.category-wrap').html('');
                    var tempHtml = '';
                    dataList
                        .map(function (item, index) {
                            tempHtml += ''
                                + '<div class="row row-product-category now">'
                                + '<div class="col-33 product-category-name">'
                                + item.productCategoryName
                                + '</div>'
                                + '<div class="col-33">'
                                + item.priority
                                + '</div>'
                                + '<div class="col-33"><a href="#" class="button delete" data-id="'
                                + item.productCategoryId
                                + '">删除</a></div>' + '</div>';
                        });
                    $('.category-wrap').append(tempHtml);
                }
            });
    $('#new')
        .click(
            function() {
                var tempHtml = '<div class="row row-product-category temp">'
                    + '<div class="col-33"><input class="category-input category" type="text" placeholder="分类名"></div>'
                    + '<div class="col-33"><input class="category-input priority" type="number" placeholder="优先级"></div>'
                    + '<div class="col-33"><a href="#" class="button delete">删除</a></div>'
                    + '</div>';
                $('.category-wrap').append(tempHtml);
            });
    //删除新增行
    $('.category-wrap').on('click','.row-product-category.temp .delete',function (e) {
         console.log($(this).parent().parent());
         $(this).parent().parent().remove();//选定这行的div删除

    });
    //删除已有存在数据库的上
    $('.category-wrap').on('click','.row-product-category.now .delete',function (e) {//e是一个事件
//e相当于一个时间这里是点击时间
        var target = e.currentTarget;//e.currentTarget是绑定时间的标签,这里是绑定了这个<a href="#" class="button delete" data-id="'></a>
        $.confirm("确定删除?",function () {
            $.ajax({
                url: deleteUrl,
                type: 'POST',
                data: {
                    productCategoryId:target.dataset.id
                },
                dataType:'json',
                success:function (data) {
                    if (data.success){
                        $.toast('删除成功!');
                        getList();
                    }else{
                        $.toast('删除失败');
                    }
                }
            });
        });
    });

    $('#submit').click(function () {
        var temArr = $('.temp');//取得新增的商品类别表
        var productCategoryList = [];//定义一个数组，接收表内数据，所谓数组传入给后台
        temArr.map(function (index,item) {//遍历新增商品类别表
            var tempObj ={};//定义一个对象，其本质上是一个productcategory后面赋属性
            tempObj.productCategoryName = $(item).find('.category').val();//取得表内中一行叫category的属性的值赋值给tempObj
            tempObj.priority = $(item).find('.priority').val();
            if (tempObj.productCategoryName&&tempObj.priority){//如果两项都有值
                productCategoryList.push(tempObj);//存入到list中
            }
        });
        $.ajax({
            url:addUrl,
            type:'POST',
            data:JSON.stringify(productCategoryList),//传入后台要先变成string类型
            contentType:'application/json',//声明内容为json
            success:function (data) {
                if (data.success) {
                    $.toast('提交成功！');
                }else {
                    $.toast('提交失败');
                }
            }
        });
        });
    // getProductCategory();
    // function getProductCategory() {
    //     $.ajax({
    //         url:listUrl,
    //         type:"get",
    //         dataType:"json",
    //         success:function (data) {
    //             var productCategoryList=data.productCategoryList;
    //             handerProductCategory(productCategoryList);
    //         }
    //     })
// 直接在里面写了
    // function handerProductCategory(data) {//要求传入一个productCategoryList
    //     var html='';
    //     data.map(function (item,index) {
    //         html += '<div class="row row-shop"><div class="col-40">' + item.productCategoryName +
    //             '</div><div class="col-40">' + item.priority +
    //             '</div><div class="col-20">' + '删除' +
    //             '</div></div>';
    //     });
    //     $('.shop-wrap').html(html);
    // }
// }


})