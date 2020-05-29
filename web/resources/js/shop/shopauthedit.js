$(function() {
    var shopAuthId = getQueryString('shopAuthId');
    var infoUrl = '/springboot/shopadmin/getshopauthmapbyid?shopAuthId=' + shopAuthId;
    //授权url
    var shopAuthPostUrl = '/springboot/shopadmin/modifyshopauthmap';

    if (shopAuthId) {
        getInfo(shopAuthId);
    } else {
        $.toast('用户不存在！');
        window.location.href = '/springboot/shopadmin/shopmanage';
    }

    function getInfo(id) {
        $.getJSON(infoUrl, function(data) {
            if (data.success) {
                var shopAuthMap = data.shopAuthMap;
                $('#shopauth-name').val(shopAuthMap.employee.name);
                $('#title').val(shopAuthMap.title);
            }
        });
    }

    $('#submit').click(function() {
        var shopAuth = {};
        var employee={};
        shopAuth.employee = employee;
        shopAuth.employee.name = $('#shopauth-name').val();
        shopAuth.title = $('#title').val();
        shopAuth.shopAuthId = shopAuthId;
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码！');
            return;
        }
        $.ajax({
            url : shopAuthPostUrl,
            type : 'POST',
            contentType : "application/x-www-form-urlencoded; charset=utf-8",
            data : {
                shopAuthMapStr : JSON.stringify(shopAuth),
                verifyCodeActual :verifyCodeActual
            },
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

});