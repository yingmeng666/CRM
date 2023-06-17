layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    /**
     * 确定按钮监听表单提交
     */
    form.on('submit(addOrUpdateSaleChance)', function (data) {
        //提交数据时的加载层
        var index = layer.msg("数据提交中，请稍后...", {
            icon: 16,//图标
            time: false,//不关闭
            shade: 0.8//设置遮罩的透明度
        });
        //发送ajax请求
        var url = ctx + '/sale_chance/add';
        //通过弹出层中是否有saleChanceId来判断是添加还是更新
        var saleChanceId = $("[name='id']").val()
        if (saleChanceId != null && saleChanceId != "") {
            url = ctx + '/sale_chance/update';
        }
        $.post(url, data.field, function (result) {
            //判断操作是否成功
            if (result.code == 200) {//成功
                //提示成功
                layer.msg("操作成功！", {icon: 6});
                //关闭加载层
                layer.close(index);
                //关闭弹出层
                layer.closeAll('iframe');
                //刷新父窗口，重新加载表格
                parent.location.reload();
            } else {//失败
                layer.msg(result.msg, {icon: 5});
            }
        });
        //阻止表单提交
        return false;
    });
    /**
     * 点击取消关闭弹出层
     */
    $("#closeBtn").click(function () {
        //当你在iframe页面关闭自身时
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

    /**
     * 加载指派人的下拉选项框
     */
    $.ajax({
        type:"GET",
        url:ctx+"/users/querySales",
        success:function (result){
            //统一返回模型中的List对象
            data=result.result;
            if(data!=null){
                //循环结果
                for (let i = 0; i <data.length; i++) {
                    //根据assignManId隐藏域中是否有值来确定是否有指派人
                    var assignManId = $("[name='assignManId']").val();
                    if(assignManId==data[i].id){
                        //拼接下拉选项
                        var opt="<option value='"+data[i].id+"' selected>"+data[i].uname+"</option>";
                    }else {
                        opt = "<option value='"+data[i].id+"'>"+data[i].uname+"</option>";
                    }
                    //将下拉选项设置到下拉框中
                    $("#assignMan").append(opt)
                }
            }
            //重新加载下拉选项框
            layui.form.render();
        }
    });
})