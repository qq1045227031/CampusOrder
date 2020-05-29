$(function() {
    //登录验证的controller URL
    var loginUrl='/springboot/local/logincheck';
    //从地址栏的URL里获取usertype
    //usertype=1则为customer其余为shopowner
    var usertype=getQueryString('usertype');
    //登录次数，累计登录三次失败之后，自动弹出验证码要求输入
    var loginCount=0;
    $('#submit').click(function(){
        //获取输入的账号
        var userName = $('#username').val();
        //获取输入的密码
        var password = $('#psw').val();
        //获取输入的验证码
        var verifyCodeActual = $('#j_captcha').val();
        //是否需要验证码验证，默认为false既不需要
        var needVerify = false;
        if(loginCount>=3){
            if (!verifyCodeActual) {
                $.toast('请输入验证码！');
                return;
            }else{
                needVerify=true;
            }
        }
        //访问后台绑定账号
        $.ajax({
            url : loginUrl,
            async : false,
            cache : false,
            type : "post",
            dataType : 'json',
            data : {
                userName : userName,
                password : password,
                verifyCodeActual : verifyCodeActual,
                //是否需要做验证码校验
                needVerify:needVerify
            },
            success:function(data){
                if(data.success){
                    $.toast("登录成功！");

                    // if(usertype==1){
                    //     window.location.href='/springboot/frontend/index';
                    // }else{
                    //     window.location.href='/springboot/shopadmin/shoplist';
                    // }
                    //通过判断用户类型转发到相应界面
                    var thistype=data.usertype;
                    if(thistype==2){
                        window.location.href='/springboot/shopadmin/shoplist';
                    }else if (thistype==3){
                        window.location.href='/springboot/superadmin/main';
                    }

                    else {
                        window.location.href='/springboot/frontend/index';
                    }
                }else{
                    $.toast('登录失败！'+data.errMsg);
                    loginCount++;
                    if(loginCount>=3){
                        //登录失败三次，需要做验证码校验
                        $('#verifyPart').show();
                    }
                }
            }
        });

    });
});