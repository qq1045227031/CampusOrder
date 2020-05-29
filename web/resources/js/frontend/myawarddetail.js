$(function () {
    var userAwardId=getQueryString("userAwardId");
    var awardUrl ='/springboot/frontend/getawardbyuserawardid?userAwardId='+userAwardId;
    $.getJSON(awardUrl,function (data) {
        if (data.success){
            var award = data.award;
            $('#award-img').attr('src',award.awardImg);
            $('#create-time').text(new Date(data.userAwardMap.createTime).Format('yyyy-MM-dd'));
            $('#award-name').text("商品名:"+award.awardName);
            $('#award-desc').text("详情:"+award.awardDesc);
            // var imgListHtml = '';
            // if (data.usedStatus==0){
            //     imgListHtml+='<div><img src="/springboot/frontend/generateqrcode4award?userAwardId='
            //     +userAwardId
            //     +'" width="100%"/></div>';
            //     $('#imgList').html(imgListHtml);
            // }

        }

    });
    $('#me').click(function() {
        $.openPanel('#panel-left-demo');
    });
    $.init();


})