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
        , url: ctx + '/sale_chance/list?flag=1' //数据接口客户开发计划flag=1
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
            , {field: 'createDate', title: '创建时间', align: 'center'}
            , {
                field: 'devResult', title: '开发状态', align: 'center', templet: function (d) {
                    return formatDevResult(d.devResult);
                }
            }
            , {field: 'updateDate', title: '更新时间', align: 'center'}
            , {title: '操作', templet: '#op', fixed: 'right', align: 'center', minWidth: 150}
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
                , devResult: $("[name='devResult']").val()//开发状态
            }
            , page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });

    //开发/详情点击事件
    table.on('tool(saleChances)',function (data){
        if(data.event=='dev'){//开发
            openCusDevPlanPage("计划项数据开发",data.data.id);
        }else if(data.event=='info'){//详情
            openCusDevPlanPage("计划项数据详情",data.data.id);
        }
    });

    //页面打开
    function openCusDevPlanPage(title,saleChanceId){
        //layui弹出层函数
        layui.layer.open({
            type: 2,//类型iframe
            title: title,//标题
            area: ['750px', '550px'],//宽高
            content: ctx+'/cus_dev_plan/toCusDevPlanPage?saleChanceId='+saleChanceId,//这里content是一个URL。
            maxmin: true//最大小化
        });
    }
});
