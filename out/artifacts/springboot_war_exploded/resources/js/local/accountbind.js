$(function() {
    //绑定账号的controller URL
    var bindUrl = '/springboot/local/bindlocalauth';
    //从地址栏的URL里获取usertype
    //usertype=1则为前端展示系统，其余为店家管理系统
    var  usertype=getQueryString('usertype');
    $('#submit').click(function() {
        //获取输入的账号
        var userName = $('#username').val();
        //获取输入的密码
        var password = $('#psw').val();
        //获取输入的验证码
        var verifyCodeActual = $('#j_captcha').val();
        var needVerify = false;
        if (!verifyCodeActual) {
            $.toast('请输入验证码！');
            return;
        }
        //访问后台绑定账号
        $.ajax({
            url : bindUrl,
            async : false,
            cache : false,
            type : "post",
            dataType : 'json',
            data : {
                userName : userName,
                password : password,
                verifyCodeActual : verifyCodeActual
            },
            success : function(data) {
                if (data.success) {
                    $.toast('绑定成功！');
                    if(usertype==1){
                        //若用户在前端展示系统页面则自动退回到前端展示系统首页
                        window.location.href='/springboot/frontend/index';
                    }else{
                        //若用户在店家管理系统页面则自动退回到店家管理系统首页
                        window.location.href='/springboot/shopadmin/shoplist';
                    }
                } else {
                    $.toast('绑定失败！');
                    $('#captcha_img').click();
                }
            }
        });
    });
});