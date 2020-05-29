$(function () {
    var shopId = getQueryString('shopId');
    var isEdit = shopId?true:false;//如果有shopid代表是修改用户信息，如果没表示注册
    var initUrl = '/springboot/shopadmin/getshopinitinfo';//获取用户信息,返回店铺种类和区域
    var registerShopUrl = '/springboot/shopadmin/registershop';//  /getshopinitinfo/registershop
    var shopInfoUrl ="/springboot/shopadmin/getshopbyid?shopId="+shopId;
    var editShopUrl = '/springboot/shopadmin/modifyshop';
    console.log("调用成功");
    if (!isEdit){
        getShopInitInfo();
    }else {
        getShopInfo(shopId);
    }
    //通过获取用户信息
    //页面上区域和category种类的获取
    function getShopInfo(shopId) {
        $.getJSON(shopInfoUrl, function(data) {
            if (data.success) {
                var shop = data.shop;
                $('#shop-name').val(shop.shopName);
                $('#shop-addr').val(shop.shopAddr);
                $('#shop-phone').val(shop.phone);
                $('#shop-desc').val(shop.shopDesc);
                var shopCategory = '<option data-id="'
                    + shop.shopCategory.shopCategoryId + '" selected>'
                    + shop.shopCategory.shopCategoryName + '</option>';
                var tempAreaHtml = '';
                data.area.map(function(item, index) {
                    tempAreaHtml += '<option data-id="' + item.areaId + '">'
                        + item.areaName + '</option>';
                });
                $('#shop-category').html(shopCategory);
                $('#shop-category').attr('disabled','disabled');
                $('#area').html(tempAreaHtml);
                $('#area').attr('data-id',shop.areaId);
            }
        });
    }
    function getShopInitInfo() {
        $.ajax({
            url:'/springboot/shopadmin/getshopinitinfo',
            type: 'GET',
            success: function (data) {
                if (data.success) {
                    var tempHtml = '';
                    var tempAreaHtml = '';
                    data.shopCategoryList.map(function (item, index) {
                        tempHtml += '<option data-id="' + item.shopCategoryId + '">' + item.shopCategoryName + '</option>';
                    });//通过map方法来遍历，item是每次遍历存的值，index是索引
                    data.areaList.map(function (item, index) {
                        tempAreaHtml += '<option data-id="' + item.areaId + '">' + item.areaName + '</option>';
                    });
                    $('#shop-category').html(tempHtml);
                    $('#area').html(tempAreaHtml);
                    // $('#errMsg').html(data.ceshi);//测试?可行，估计没有进入到register的方法
                }
            }
        });
        // $.getJSON('/springboot/shopadmin/getshopinitinfo', function (data) {// '/springboot/shopadmin/getshopinitinfo';//获取用户信息,返回店铺种类和区域
        //     if (data.success) {
        //         var tempHtml = '';
        //         var tempAreaHtml = '';
        //         data.shopCategoryList.map(function (item, index) {
        //             tempHtml += '<option data-id="' + item.shopCategoryId + '">' + item.shopCategoryName + '</option>';
        //         });//通过map方法来遍历，item是每次遍历存的值，index是索引
        //         data.areaList.map(function (item, index) {
        //             tempAreaHtml += '<option data-id="' + item.areaId + '">' + item.areaName + '</option>';
        //         });
        //         $('#shop-category').html(tempHtml);
        //         $('#area').html(tempAreaHtml);
        //         // $('#errMsg').html(data.ceshi);//测试?可行，估计没有进入到register的方法
        //     }
        // });
    }
     $('#submit').click(function () {
           var shop = {};//定义是一个对象
           if (isEdit){
               shop.shopId = shopId;
           }
           shop.shopName = $('#shop-name').val();
           shop.shopAddr = $('#shop-addr').val();
           shop.phone = $('#shop-phone').val();
           shop.shopDesc = $('#shop-desc').val();
           shop.shopCategory = {
               shopCategoryId:$('#shop-category').find('option').not(function(){
                return !this.selected;
               }).data('id')  //双重否定表肯定
           };
           shop.area = {
               areaId:$('#area').find('option').not(function(){
                   return !this.selected;
               }).data('id')  //双重否定表肯定
           };
           //处理文件流
           var shopImg =$('#shop-img')[0].files[0];
           var formData = new FormData;
           formData.append('shopImg',shopImg);//相当于把数据合并？
           formData.append('shopStr',JSON.stringify(shop));//javasript转化为json对象
           var verifyCodeActual = $('#j_captcha').val();//获取验证码
           if (!verifyCodeActual){
           $.toast('请输入验证!');//弹窗
           return;
           }
           formData.append('verifyCodeActual',verifyCodeActual);
           $.ajax({
               url:isEdit?editShopUrl:registerShopUrl,
               type:'POST',
               data:formData,
               contentType:false,
               processData:false,
               cache:false,
               success:function (data) {
                   if (data.success){
                       $.toast('成功');
                   }else {
                       $.toast('失败');
                   }
                //    var err ='';//
                //    if (data.success==null){
                //        err+='success为空';//没进入方法？
                //    }else {
                //        err+='success不为空';
                //    }
                // if (data.success){
                //     $.toast('提交成功!');
                //     window.location.href("/springboot/shopadmin/shoplist");
                // }else {
                //     $.toast('提交失败');
                //
                //     if (data.errMsg!=null){//加入错误信息
                //         err+=data.errMsg;
                //     }else {
                //         err+='Msg信息为';
                //     }
                    // if (data.ceshi!=null){
                    //     err+=data.ceshi;
                    // }else{
                    //     err+="测试也为空";
                    // }

                    // $('#errMsg').html(err);
                    $('#captcha_img').click();//如果不加function就是触发点击事件，这是触发点击事件
                }

           });
       });


})