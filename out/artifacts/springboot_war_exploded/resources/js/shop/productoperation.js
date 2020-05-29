$(function () {
    //从url获取productId参数值
    var productId = getQueryString("productId");
// 通过productid获取商品信息的url  这是编辑的时候用的
    var infoUrl = '/springboot/shopadmin/getproductbyid?productId=' + productId;

    var categoryUrl = '/springboot/shopadmin/getproductcategory';
    //获取当前店铺设定的url比如商品种类，这是注册的时候用的，目前不需要识别shopid
    // var categoryUrl = '/shopadin/getproductcategorylistbyshopId?shopId='
    //     + shopId;
    //更新商品信息的url
    var productPostUrl = '/springboot/shopadmin/modifyproduct';
    //由于商品添加和编辑使用的是同一个野蛮 该标识符用来表明是添加（false）还是编辑（true）
    var isEdit = false;
    if (productId) {
        getInfo(productId);
        isEdit = true;
    } else {
        getCategory();
        //如果为false是为添加，那么要修改url由编辑改为url
        productPostUrl = '/springboot/shopadmin/addproduct';
    }
    //获取需要编辑的商品的商品信息并赋值给表单
    function getInfo(id) {
        $
            .getJSON(
                infoUrl,//var infoUrl = '/shopadmin/getproductbyid?productId=' + productId
                function(data) {
                    if (data.success) {
                        //从返回的json当中获取product对象信息并赋值给表单
                        var product = data.product;
                        $('#product-name').val(product.productName);
                        $('#product-desc').val(product.productDesc);
                        $('#priority').val(product.priority);
                        $('#point').val(product.point);//
                        $('#normal-price').val(product.normalPrice);
                        $('#promotion-price').val(
                            product.promotionPrice);
                        //获取原本商品类别以及该店铺的所有商品类别列表
                        var optionHtml = '';
                        //所有商品列表
                        var optionArr = data.productCategoryList;
                        //原有的商品类别
                        var optionSelected = product.productCategory.productCategoryId;
                        optionArr
                            .map(function(item, index) {
                                //选择所有商品种类列表中是这个商品的种类，如果是则添加 seleted 否则不添加
                                var isSelect = optionSelected === item.productCategoryId ? 'selected'
                                    : '';
                                optionHtml += '<option data-value="'
                                    + item.productCategoryId
                                    + '"'
                                    + isSelect
                                    + '>'
                                    + item.productCategoryName
                                    + '</option>';
                            });
                        $('#category').html(optionHtml);
                    }
                });
    }
    //商品添加时候获取 商品类别  /getproductcategory?
    function getCategory() {//var categoryUrl = '/shopadmin/getproductcategory';
        $.getJSON(categoryUrl, function(data) {
            if (data.success) {
                var productCategoryList = data.data;//注意传入的是result类所以用data
                var optionHtml = '';
                productCategoryList.map(function(item, index) {
                    optionHtml += '<option data-value="'
                        + item.productCategoryId + '">'
                        + item.productCategoryName + '</option>';
                });
                $('#category').html(optionHtml);
            }
        });
    }
//针对商品详情图片控件组，若改空间组的最后一个元素发生了变化（上传了图片）
    //且控件总数未达到六个，则生成一个新的文件上传空间.detail-img:last-child代表类.detail-img下最后的字控件
    $('.detail-img-div').on('change', '.detail-img:last-child', function() {
        if ($('.detail-img').length < 6) {
            $('#detail-img').append('<input type="file" class="detail-img">');
        }
    });

//点击提交按钮，分别针对商品添加和编辑做不同相应
    $('#submit').click(
        function() {
            var product = {};
            product.productName = $('#product-name').val();
            product.productDesc = $('#product-desc').val();
            product.priority = $('#priority').val();
            product.point = $('#point').val();
            product.normalPrice = $('#normal-price').val();
            product.promotionPrice = $('#promotion-price').val();
            product.productCategory = {
                productCategoryId : $('#category').find('option').not(
                    function() {
                        return !this.selected;
                    }).data('value')
            };
            product.productId = productId;
            //获取缩略图文件流
            var thumbnail = $('#small-img')[0].files[0];
            console.log(thumbnail);
            //用于传给后台 data数据
            var formData = new FormData();
            formData.append('thumbnail', thumbnail);
            //遍历详情图片获取里面的文件流
            $('.detail-img').map(
                function(index, item) {
                    //判断该控件是否已选文件
                    if ($('.detail-img')[index].files.length > 0) {
                        //将低i个文件流赋值给key为productImg的表单键值对里
                        formData.append('productImg' + index,
                            $('.detail-img')[index].files[0]);
                        //这把formDate.append对应,这并不是一个list集合 ，只是Key名字是 productImg1 ,productImg2
                        // for (int i = 0; i < IMAGEMAXCOUNT; i++) {
                        //     CommonsMultipartFile productImgFile = (CommonsMultipartFile) multipartRequest
                        //         .getFile("productImg" + i);
                    }
                });
            //将product json对象转化为字符流保存至表单对象key为productStr的键值里
            formData.append('productStr', JSON.stringify(product));
            //获取页面输入的验证码
            var verifyCodeActual = $('#j_captcha').val();
            if (!verifyCodeActual) {
                $.toast('请输入验证码！');
                return;
            }
            formData.append("verifyCodeActual", verifyCodeActual);
            $.ajax({
                url : productPostUrl,
                type : 'POST',
                data : formData,
                contentType : false,
                processData : false,
                cache : false,
                success : function(data) {
                    if (data.success) {
                        $.toast('提交成功！');
                        $('#captcha_img').click();
                    } else {
                        $.toast('提交失败！');
                        $('#captcha_img').click();
                    }
                }
            });
        });
})