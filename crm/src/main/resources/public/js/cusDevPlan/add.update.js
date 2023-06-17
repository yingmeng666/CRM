layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    /**
     * 点击确定提交添加
     */
    form.on('submit(addOrUpdateCusDevPlan)', function (data) {
        //提交数据时的加载层
        var index = top.layer.msg("数据提交中，请稍后...", {
            icon: 16,//图标
            time: false,//不关闭
            shade: 0.8//设置遮罩的透明度
        });
        var data = data.field;
        var url = ctx + '/cus_dev_plan/add';
        //通过是否有id值判断是添加还是修改
        if($("[name='id']").val()!=null){
            url = ctx+'/cus_dev_plan/update';
        }
        $.post(url, data, function (result) {
            //判断操作是否成功
            if (result.code == 200) {//成功
                //提示成功
                top.layer.msg("操作成功！", {icon: 6});
                //关闭加载层
                top.layer.close(index);
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
});