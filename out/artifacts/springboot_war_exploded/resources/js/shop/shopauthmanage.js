$(function() {
    var listUrl = '/springboot/shopadmin/listshopauthmapsbyshop?pageIndex=1&pageSize=9999&shopId=';
    var deleteUrl = '/springboot/shopadmin/modifyshopauthmap';
    getList();
    function getList() {
        $.getJSON(listUrl, function (data) {
            if (data.success) {
                var shopauthList = data.shopAuthMapList;
                var tempHtml = '';
                shopauthList.map(function (item, index) {
                    var testOp = "恢复";
                    var contraryStatus = 0;
                    if (item.enableStatus ==1){
                    testOp='删除';
                    contraryStatus = 1;
                    }
                    tempHtml = ''+'<div class="row row-shopauth">'+'<div class="col-40">'
                    +item.employee.name+'</div>';
                    if (item.titleFlag!=0){
                    tempHtml += '<div class="col-20">'+ item.title +'</div>'
                        +          '<div class="col-40">'
                        +              '<a href="#" class="edit" data-employee-id="'+ item.employeeId +'" data-auth-id="'+ item.shopAuthId +'" data-status="'+ item.enableStatus +'">编辑</a>'
                        +              '<a href="#" class="status" data-employee-id="'+ item.employeeId +'" data-auth-id="'+ item.shopAuthId +'" data-status="'+ item.enableStatus +'">删除</a>'
                        +          '</div>'
                        +      '</div>';
                    }else {
                        tempHtml+='<div class="col-20">'+item.title+'</div>'
                            +'<div class="col-40">'+'<span>不可操作</span>'+'</div>';
                    }
                    tempHtml +='</div>'
                });
                $('.shopauth-wrap').html(tempHtml);
            }
        });
    }
    $('.shopauth-wrap').on('click', 'a', function (e) {
        var target = $(e.currentTarget);
        if (target.hasClass('edit')) {
            window.location.href = '/springboot/shopadmin/shopauthedit?shopAuthId=' + e.currentTarget.dataset.authId;
        } else if (target.hasClass('status')) {
            changeStatus(e.currentTarget.dataset.authId,e.currentTarget.dataset.status);
        }
    });

    function changeStatus(id,status) {
        var shopAuth = {};
        shopAuth.shopAuthId = id;
        shopAuth.enableStatus = status;
        $.confirm('确定么?', function () {
            $.ajax({
                url: deleteUrl,
                type: 'POST',
                data: {
                    shopAuthMapStr:JSON.stringify(shopAuth),
                    statusChange:true
                },
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        $.toast('删除成功！');
                        getList();
                    } else {
                        $.toast('删除失败！');
                    }
                }
            });
        });
    }



    // $('#new').click(function () {
    //     window.location.href = '/myo2o/shop/shopauthedit';
    // });
});