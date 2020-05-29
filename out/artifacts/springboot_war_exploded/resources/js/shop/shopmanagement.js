$(function () {

   var shopId = getQueryString("shopId");//获取url上的shopId，这是一个自己定义的方法相当于前前台的getParameter
   var shopInfoUrl = '/springboot/shopadmin/getshopmanagementinfo?shopId='+shopId;//用于判断是有有用户
   //?shopId=1 就相当于get的表单元素，可以用request.getParameter("shopId");获取
   $.getJSON(shopInfoUrl,function (data) {
       if (data.redirect){
           window.location.href=data.url;//重定向到modelMap.put("url","/shop/shoplist");
       }else {
           if (data.shopId!=undefined&&data.shopId!=null){
               shopId = data.shopId;
               $('#msg').html(data.msg)
           }
           $('#shopInfo').attr('href','/springboot/shopadmin/shopoperation?shopId='+shopId);//顺序shoplist(商户管理的shop，点击进入进行管理)→shopmanag(商户对s该hop可执行的操作)→shopoperation(商户修改该个shop的信息页面)
               //如果有shopid进入修改页面，并传入id
               //.attr是增加属性 href是属性名
           $('#getproduct').attr('href','/springboot/shopadmin/productmanagement');
           $('#getproductcategory').attr('href','/springboot/shopadmin/productcategorymanage');//从session中获取shop，不用shopId='+shopId

       }
   })
})