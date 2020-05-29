$(function() {
    var productId = getQueryString('productId');
    var productUrl = '/springboot/frontend/listproductdetailpageinfo?productId='
        + productId;
    var imgListHtml='';
    var qrc = '';

    $
        .getJSON(
            productUrl,
            function(data) {
                if (data.success) {
                    var product = data.product;
                    $('#product-img').attr('src', product.imgAddr);
                    $('#product-time').text(
                        new Date(product.lastEditTime)
                            .Format("yyyy-MM-dd"));
                    if (product.point!=undefined){
                        $('#product-point').text("购买可得"+product.point+"积分");
                    }
                    $('#product-name').text(product.productName);
                    $('#titlename').html(product.productName);
                    $('#product-desc').text(product.productDesc);
                    if (product.normalPrice!=undefined&&product.promotionPrice!=undefined){
                        if (product.normalPrice!=product.promotionPrice) {
                            $('#price').show();
                            $('#normalPrice').html('<del>' + "原价" + product.normalPrice + '</del>');//中间有横线
                            $('#promotionPrice').text("现价" + product.promotionPrice);//￥
                        }else {
                            $('#price').show();
                            $('#normalPrice').html('<p>'+"价格:" + product.promotionPrice+ '</p>');

                        }
                    }
                    //遍历详情图片
                    product.productImgList.map(function(item, index) {
                        if (item.imgDesc!=undefined){
                            imgListHtml += '<div style="display:inline"><p>描述:'+item.imgDesc+'</p>'+'<img style="width: 100%" src="' + item.imgAddr + '"/></div><HR style="border:1 dashed #987cb9" width="80%"color=#987cb9 SIZE=1>';
                        }else{
                        imgListHtml += '<div style="display:inline;width: 100%"><img style="width: 100%" src="' + item.imgAddr + '"/></div><hr/>';
                        }
                    });
                    // if (data.needQRCode){
                        //客户登录了就生成商品二维码供商家扫描'<img src="/springboot/frontend/generateqrcode4shopauth?productId="'+ product.productId+ '" width="44">'
                        // qrc += '<div><img src="/springboot/frontend/generateqrcode4shopauth?productId=' + product.productId +'"/></div>';//二维码 失效
                    // }
                    $('#imgList').html(imgListHtml);
                    // $('.list-div').html(qrc);
                    // var imgListHtml = '';
                    // product.productImgList.map(function(item, index) {
                    //     imgListHtml += '<div> <img src="'
                    //         + item.imgAddr + '"/></div>';
                    // });
                    // // 生成购买商品的二维码供商家扫描
                    // imgListHtml += '<div> <img src="/springboot/frontend/generateqrcode4product?productId='
                    //     + product.productId + '"/></div>';
                    // $('#imgList').html(imgListHtml);
                }
            });
    $('.zhifu').click(function () {
       $.confirm('确认购买吗？',function () {
           $.ajax({
               url:'/springboot/frontend/adduserproductmap',
               type:"post",
               data:{
                   productId:productId
               },
               dataType:"json",
               success:function (data) {
                   if (data.success){
                       $.toast("购买成功");
                   }else {
                       $.toast("交易失败,"+data.errMsg);
                   }

               }
           });

       });

    })
    $('#me').click(function() {
        $.openPanel('#panel-left-demo');
    });
    $.init();
});
