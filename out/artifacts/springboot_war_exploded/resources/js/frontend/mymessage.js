$(function () {
    getMyMessage();
    var id=0;
    function getMyMessage() {
        $.ajax({
            url:'/springboot/frontend/getmymessage',
            type:'post',
            success:function (data) {
                if (data.success){
                    id=data.user.userId;
                $('#name').val(data.user.phone);
                $('#phone').val(data.user.phone);
                $('#email').val(data.user.email);
                $('#addr').val(data.user.address);
                if (data.user.gender!=undefined&&data.user.gender==1){
                    $('#gender').html("男");
                }else if (data.user.gender!=undefined&&data.user.gender==0){
                    $('#gender').html("女");
                }

                $('#imgsrc').attr('src',data.user.profileImg);
            }else{
                    $.toast('获取信息失败');
                }
            }
        });

    }
    $('#tijiao').click(function () {
        console.log("提交");
        var root = {};
        root.userId = id;
        root.name=$('#name').val();
        root.phone=$('#phone').val();
        root.email = $('#email').val();
        root.address = $('#addr').val();
        var thumbnail = $('#imgchange')[0].files[0];
        var formData = new FormData();
        formData.append('thumbnail', thumbnail);
        formData.append("root",JSON.stringify(root));
        $.ajax({
            url:'/springboot/frontend/updatemessage',
            type:'POST',
            data: formData,
            processData: false,   // jQuery不要去处理发送的数据
            contentType: false,   // jQuery不要去设置Content-Type请求头
            success: function (data) {
                if (data.success){
                    $.toast('修改成功');
                }else {
                    $.toast('修改失败');
                }

            }
        });
    })
})