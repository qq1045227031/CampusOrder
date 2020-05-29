$(function () {
    var registerUrl = '/springboot/local/toregister';

    $('#submit').click(function () {
        // if (!$('#password').val()==$('#repassword').val()){
        //     $.toast('两次输入密码不一致');
        // }
        var root = {};
        var person={};
        var verifyCodeActual = $('#j_captcha').val();//获取验证码
        if (!verifyCodeActual){
            $.toast('请输入验证!');//弹窗
            return;
        }
        var repassword = $('#repassword').val();
        root.password = $('#password').val();
        person.name = $('#name').val();
        person.phone = $('#phone').val();
        person.email = $('#email').val();
        root.username=$('#username').val();
        person.userType= $('#usertype').val()
        if (!(root.password&&person.name&&person.phone&&root.username&&repassword)){
            $.toast('请填写完信息')
            return;
        }
        if (root.password!=repassword){
            $.toast('密码不一致');
            console.log("密码不一致");
        }
        var formatDate = new FormData;
        formatDate.append('root',JSON.stringify(root));
        formatDate.append('person',JSON.stringify(person));
        formatDate.append("verifyCodeActual",verifyCodeActual)
        $.ajax({
            url: registerUrl,
            type: 'POST',
            data: formatDate,
            contentType:false,
            processData:false,
            cache:false,
            success: function (data) {
                if (data.success==true){
                    $.toast('创建成功');
                    window.location.replace('/springboot/local/login');
                }else {
                    $.toast('用户名已存在');
                }
            }
        })

    })
})