$(function () {
    //获取改商铺下所有商品的url
    var listUrl ='/springboot/shopadmin/getproductlistbyshop?pageIndex=1&pageSize=999';
    //商铺下架的url
    var statusUrl = '/springboot/shopadmin/modifyproduct';
    getList();
    /**
     * 获取改商铺下的商品列表
     */
    function getList() {
        //从后台获取此店铺的商店列表
        $.getJSON(listUrl,function (data) {
            if (data.success){
                var productList = data.productList;
                var tempHtml ='';
                //遍历每条商品信息，拼接成一行显示，列信息包括"
                //商品名称，优先级，上下架（含productId），编辑按钮(含productId)
                //预览 含productId
                productList.map(function (item,index) {
                    var textOp="下架";
                    var contraryStatus=0;
                    if (item.enableStatus==0){
                        //若状态值为0，表示是已下架的商品，操作变为上架（即点击上架按钮上架相关商品）
                    textOp= '上架';
                    contraryStatus = 1;
                    }else{
                        contraryStatus=0;
                    }
                    //拼接没捡商品的航信息
                    tempHtml +='<div class="row row-product">'
                     +'<div class="col-20">'
                     + item.productName
                     +'</div>'
                     +'<div class="col-20">'
                    +item.priority
                    +'</div>'
                     +'<div class="col-20">'
                     +item.point
                     +'</div>'
                    +'<div class="col-40">'
                    +'<a href="#" class="edit" data-id="'
                    +item.productId
                    +'" data-status="'
                    +item.enableStatus
                    +'">编辑</a>'
                    +'<a href="#" class="status" data-id="'
                     +item.productId
                    +'" data-status="'
                    +contraryStatus
                    +'">'
                    +textOp
                    + '</a>'
                    +'<a href="#" class="preview" data-id="'
                    +item.productId
                    +'" data-status="'
                    +item.enableStatus
                    +'">预览</a>'
                    +'</div>'
                    +'</div>';
                });
                //将人凭借好的信息赋值进html空间中
                $('.product-wrap').html(tempHtml);
            }
            console.log(tempHtml)
        });
    }
    // currentTarget 事件属性返回其监听器触发事件的节点，即当前处理该事件的元素、文档或窗口 event.currentTarget
   //e是这个点击事件 e.currentTarget是这个事件的结点s
    $('.product-wrap').on('click','a',function (e) {
        var target = $(e.currentTarget);//触发事件的结点
        if (target.hasClass('edit'))//如果是个结点有edit类
        {//如果有class edit则进入到店铺信息编辑页面，冰带有productId参数
            window.location.href='/springboot/shopadmin/productoperation?productId='
            +e.currentTarget.dataset.id;
        }else if (target.hasClass('status')) {
            //如果有calss status 则调用后台上架下架相关商品，并滴啊有productId参数
            changeItemStatus(e.currentTarget.dataset.id,e.currentTarget.dataset.status);
        }else if (target.hasClass('preview')){
            //如果有class preview 则去前台展示系统改商品详情页预览商品情况
            window.location.href='/springboot/shopadmin/productshow?productId='
                +e.currentTarget.dataset.id;
        }
    });
    function changeItemStatus(id,enableStatus) {
        //定义product json对象冰添加productId以及状态 上/下架
        var product={};
        product.productId  = id;
        product.enableStatus = enableStatus;
        $.confirm('确定要修改吗',function () {
            //上下架是相关商品的修改
            $.ajax({
                url:statusUrl,
                type:'POST',
                data:{
                    productStr:JSON.stringify(product),
                    statusChange:true//跳过验证码
                },
                dataType:'json',
                success:function (data) {
                    if (data.success){
                        $.toast('修改成功');
                        getList();
                    }else {
                        $.toast('修改失败');
                    }

                }

            });
        });


    }



})