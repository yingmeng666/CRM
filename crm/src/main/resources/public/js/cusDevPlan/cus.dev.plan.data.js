layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    var tableIns = table.render({
        id: 'cusDevPlanTable'
        //表格id容器
        , elem: '#cusDevPlanList'
        //表格高度
        , height: 'full-125'
        //单元格最小宽度
        , cellMinWidth: 95
        , url: ctx + '/cus_dev_plan/list?saleChanceId='+$("[name='id']").val() //数据接口
        /*
        {//统一响应模型，
            "code": 200,
            "msg": "success",
            "result": {
                "data": [],
                "count": 0
            }
        }
         */
        , parseData: function (res) { //数据格式化成layui表格能接收的模式，res 即为原始返回的数据，
            return {
                "code": getCode(res),//解析接口状态
                "msg": res.msg, //解析提示文本
                "count": res.result.count, //解析数据长度
                "data": res.result.data //解析数据列表
            };
        }
        , page: true //开启分页
        //每页默认页数
        , limit: 10
        //可选每页页数
        , limits: [10, 20, 30, 40, 50]
        //开启头部工具栏
        , toolbar: '#toolbarDemo'
        , cols: [[ //表头，field：对应实体类的属性名，title：表格列名，sort：是否排序默认否，fixed：固定列
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执行效果',align:"center"},
            {field: 'planDate', title: '执行时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#cusDevPlanListBar"}
        ]]
    });

    /**
     * 获得返回值状态并格式化成layui表格能识别的
     */
    function getCode(res) {
        if (res.code == 200) {
            return 0;
        }
    }

    /**
     * 监听头部工具栏
     */
    table.on('toolbar(cusDevPlans)',function (data){
       if(data.event=="add") {//添加
           openAddOrUpdateCusDevPlanPage();
       }else if(data.event=="success"){//开发成功
            updateSaleChanceDevResult(2);
       }else if(data.event=="failed"){//开发失败
           updateSaleChanceDevResult(3);
       }
    });

    /**
     * 修改开发状态
     * @param devResult
     */
    function updateSaleChanceDevResult(devResult){
        //弹出询问层
        layer.confirm('确定执行操作吗',{icon:3,title:'营销机会管理'},function (index){
            //得到需要更改的营销机会的id
            var sId = $("[name='id']").val();
            var url = ctx+'/sale_chance/updateSaleChanceDevResult';
            $.post(url,{id:sId,devResult:devResult},function (result){
               if(result.code==200){
                   layer.msg("操作成功",{icon:6});
                   layer.closeAll("iframe");
                   parent.location.reload();
               } else{//失败
                   layer.msg(result.msg,{icon:5});
               }
            });
        });
    }

    /**
     * 监听行工具栏
     */
    table.on('tool(cusDevPlans)',function (data){
        if (data.event=="edit"){
            openAddOrUpdateCusDevPlanPage(data.data.id);
        }else if(data.event=="del"){
            deleteCusDevPlan(data.data.id);
        }
    });

    /**
     * 删除计划项
     */
    function deleteCusDevPlan(id){
        layer.confirm("您确定删除该记录吗？",{icon:3,title:"开发项管理"},function (index){
            //关闭询问层
            layer.close(index);
            //发送请求
            $.post(ctx+'/cus_dev_plan/delete',{id:id},function (result){
                if(result.code==200){//成功
                    layer.msg("删除成功",{icon:6});
                    tableIns.reload();
                }else{//失败
                    layer.msg(result.msg,{icon:5});
                }
            });
        });
    }

    /**
     * 开发计划项添加和修改页面
     */
    function openAddOrUpdateCusDevPlanPage(id){
        var title = "<h3>开发项管理——添加计划</h3>";
        var url = ctx+'/cus_dev_plan/toAddOrUpdateCusDevPlanPage?sId='+$("[name='id']").val();
        if(id!=null && id!=""){
            title = "<h3>开发项管理——修改计划</h3>";
            url+='&id='+id;
        }
        //layui弹出层函数
        layui.layer.open({
            type: 2,//类型iframe
            title: title,//标题
            area: ['500px', '300px'],//宽高
            content: url,//这里content是一个URL。
            maxmin: true//最大小化
        });
    }
});
