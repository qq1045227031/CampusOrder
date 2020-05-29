$(function () {
    getlist();
    function getlist(e) {
        $.ajax({
            url: "/springboot/shopadmin/getshoplist",
            type: "get",
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    handleList(data.shopList);
                    handleUser(data.user);
                }
            }
        });
    }

    function handleUser(data) {//传进一个user(personInfo）对象
        $('#user-name').text(data.name);
    }

    function handleList(data) {//传入一个shoplist对象
        var html = '';
        data.map(function (item, index) {
            html += '<div class="row row-shop"><div class="col-40">' + item.shopName +
                '</div><div class="col-40">' + shopStatus(item.enableStatus) +
                '</div><div class="col-20">' + goShop(item.enableStatus, item.shopId) +
                '</div></div>';

        });
        $('.shop-wrap').html(html);
    }
    function shopStatus(status) {//handleList调用传入shop对象里的enableStatus 来判断其店铺审核属性
        if (status==0){
            return '审核中';
        }else if (status==-1){
            return '店铺非法';
        }else if (status==1){
            return '审核通过';
        }
    }
    function goShop(status, id) {//进去修改店铺的判断,只有正常的才能进入修改，没有则为空不能修改
        if (status != 0 && status != -1) {
            return '<a href="/springboot/shopadmin/shopmanagement?shopId='+ id +'">进入</a>';
        } else {
            return '';
        }
    }
})