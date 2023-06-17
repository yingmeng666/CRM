layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    var tableIns = table.render({
        id: 'saleChanceTable'
        //表格id容器
        , elem: '#saleChanceList'
        //表格高度
        , height: 'full-125'
        //单元格最小宽度
        , cellMinWidth: 95
        , url: ctx + '/sale_chance/list' //数据接口
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
            {type: 'checkbox', fixed: 'center'}
            , {field: 'id', title: '编号', sort: true, fixed: 'left'}
            , {field: 'chanceSource', title: '机会来源', align: 'center'}
            , {field: 'customerName', title: '客户名称', align: 'center'}
            , {field: 'cgil', title: '成功几率', align: 'center'}
            , {field: 'overview', title: '概要', align: 'center'}
            , {field: 'linkMan', title: '联系人', align: 'center'}
            , {field: 'linkPhone', title: '联系电话', align: 'center'}
            , {field: 'description', title: '描述', align: 'center'}
            , {field: 'createMan', title: '创建人', align: 'center'}
            , {field: 'uname', title: '分配人', align: 'center'}
            , {field: 'assignTime', title: '分配时间', align: 'center'}
            , {field: 'createDate', title: '创建时间', align: 'center'}
            , {
                field: 'state', title: '分配状态', align: 'center', templet: function (d) {
                    return formatState(d.state);
                }
            }
            , {
                field: 'devResult', title: '开发状态', align: 'center', templet: function (d) {
                    return formatDevResult(d.devResult);
                }
            }
            , {field: 'updateDate', title: '更新时间', align: 'center'}
            , {title: '操作', templet: '#saleChanceListBar', fixed: 'right', align: 'center', minWidth: 150}
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
     * 格式化分配状态值
     * 0=未分配
     * 1=已分配
     * 其他=未知
     * @param d
     */
    function formatState(state) {
        if (state == 0) {
            return "<div style='color: grey'>未分配</div>"
        } else if (state == 1) {
            return "<div style='color: green'>已分配</div>"
        } else {
            return "<div style='color: rebeccapurple'>未知</div>"
        }
    }

    /**
     * 格式化开发状态
     * 0=未开发
     * 1=开发中
     * 2=开发成功
     * 3=开发失败
     * 其他=未知
     * @param d
     */
    function formatDevResult(devResult) {
        if (devResult == 0) {
            return "<div style='color: grey'>未开发</div>"
        } else if (devResult == 1) {
            return "<div style='color: orange'>开发中</div>"
        } else if (devResult == 2) {
            return "<div style='color: green'>开发成功</div>"
        } else if (devResult == 3) {
            return "<div style='color: red'>开发失败</div>"
        } else {
            return "<div style='color: rebeccapurple'>未知</div>"
        }
    }

    //搜索按钮点击事件
    $(".search_btn").click(function () {
        //表格重载
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                customerName: $("[name='customerName']").val()//客户名称
                , createMan: $("[name='createMan']").val()//创建人
                , state: $("[name='state']").val()//状态
            }
            , page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });
    /**
     * 监听头部工具栏事件
     * table.on('toolbar(数据表格的lay-filter属性值)',function (data){})
     */
    table.on('toolbar(saleChances)', function (data) {
        //data.event是头部工具栏的lay-event值
        data = data.event;
        if (data == "add") {//添加
            openSaleChanceDiglog();
        } else if (data == "del") {//删除
            deleteSaleChance(data);
        }
    });

    /**
     * 多选删除营销机会
     * @param data
     */
    function deleteSaleChance(data) {
        //获取表格中选中行的数据
        var checkStatus = table.checkStatus("saleChanceTable");
        var saleChanceData=checkStatus.data;
        //判断是否有数据
        if (saleChanceData == null || saleChanceData.length < 1) {
            layer.msg("请选择需要删除的数据！", {icon: 5});
            return
        }
        //询问层
        layer.confirm("您要删除这些数据吗！", {icon: 3, title: '营销机会管理'}, function (index) {
            //关闭询问层
            layer.close(index);
            //参数拼接
            var ids = "ids=";
            for (let i = 0; i < saleChanceData.length; i++) {
                if(i==saleChanceData.length-1){
                    ids=ids+saleChanceData[i].id;
                }else{
                    ids=ids+saleChanceData[i].id+"&ids=";
                }
            }
            //发送请求到后台
            $.ajax({
               type:"POST",
               url:ctx+"/sale_chance/delete",
               data:ids,
               success:function (result) {
                   if (result.code == 200) {//成功
                       layer.msg("删除成功！", {icon: 6});
                       tableIns.reload();
                   } else {//失败
                       layer.msg(result.msg, {icon: 5});
                   }
               }
            });
        });
    }

    /**
     * 监听行工具栏事件
     */
    table.on('tool(saleChances)', function (data) {
        //data.event是行工具栏的lay-event值
        if (data.event == "edit") {//编辑
            openSaleChanceDiglog(data.data.id);
        } else if (data.event == "del") {//删除
            //弹出询问层
            layer.confirm('确认删除记录吗', {icon: 3, title: '营销机会管理'}, function (index) {
                //关闭询问层
                layer.close(index);
                //发送ajax请求后台
                $.ajax({
                    type: "POST",
                    url: ctx + '/sale_chance/delete',
                    data: {
                        ids: data.data.id
                    },
                    success: function (result) {
                        if (result.code == 200) {//成功
                            layer.msg("删除成功！", {icon: 6});
                            tableIns.reload();
                        } else {//失败
                            layer.msg(result.msg, {icon: 5});
                        }
                    }
                });
            });
        }
    });

    /**
     * 修改或添加营销机会的弹出框
     */
    function openSaleChanceDiglog(saleChanceId) {
        //弹出层标题
        var title = "<h3>营销机会管理——添加营销机会</h3>";
        //url
        var url = ctx + '/sale_chance/toSaleChancePage';
        //saleChanceId不为空为修改操作
        if (saleChanceId != null && saleChanceId != "") {
            title = "<h3>营销机会管理——修改营销机会</h3>";
            url += '?saleChanceId=' + saleChanceId;
        }
        //layui弹出层函数
        layui.layer.open({
            type: 2,//类型iframe
            title: title,//标题
            area: ['500px', '620px'],//宽高
            content: url,//这里content是一个URL。
            maxmin: true//最大小化
        });
    }

});