layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //角色列表展示
    var tableIns = table.render({
        elem: '#roleList',
        url: ctx + '/roles/list',
        cellMinWidth: 95,
        page: true,
        height: "full-125",
        limits: [10, 15, 20, 25],
        limit: 10,
        toolbar: "#toolbarDemo",
        id: "roleListTable",
        cols: [[
            {type: "checkbox", fixed: "left", width: 50},
            {field: "id", title: '编号', fixed: "true", width: 80},
            {field: 'roleName', title: '角色名', minWidth: 50, align: "center"},
            {field: 'roleRemark', title: '角色备注', minWidth: 100, align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center', minWidth: 150},
            {field: 'updateDate', title: '更新时间', align: 'center', minWidth: 150},
            {title: '操作', minWidth: 150, templet: '#roleListBar', fixed: "right", align: "center"}
        ]]
    });

    // 多条件搜索
    $(".search_btn").on("click", function () {
        table.reload("roleListTable", {
            page: {
                curr: 1
            },
            where: {
                // 角色名
                roleName: $("[name='roleName']").val()
            }
        })
    });

    // 头工具栏事件
    table.on('toolbar(roles)', function (obj) {
        switch (obj.event) {
            case "add":
                openAddOrUpdateRoleDialog();
                break;
            case "grant":
                var checkStatus = table.checkStatus(obj.config.id);
                openAddGrantPage(checkStatus.data);
                break;
        }
    });

    function openAddGrantPage(data) {
        if (data.length < 1) {
            layer.msg("请选择需要授权的角色", {icon: 5});
            return;
        }
        if (data.length > 1) {
            layer.msg("暂不支持批量角色授权", {icon: 5});
            return;
        }
        var title = "<h3>角色管理——角色授权</h3>"
        var url = ctx + '/modelus/toAddGrantPage?roleId='+data[0].id;
        //layui弹出层函数
        layui.layer.open({
            type: 2,//类型iframe
            title: title,//标题
            area: ['600px', '550px'],//宽高
            content: url,//这里content是一个URL。
            maxmin: true//最大小化
        });
    }

    function openAddOrUpdateRoleDialog(id) {
        var title = "<h3>用户角色管理——角色添加</h3>"
        var url = ctx + '/roles/toAddOrUpdateRolePage';
        if (id != null && id != "") {
            title = "<h3>用户角色管理——角色修改</h3>"
            url += "?id=" + id;
        }
        //layui弹出层函数
        layui.layer.open({
            type: 2,//类型iframe
            title: title,//标题
            area: ['500px', '400px'],//宽高
            content: url,//这里content是一个URL。
            maxmin: true//最大小化
        });
    }


    table.on('tool(roles)', function (obj) {
        var layEvent = obj.event;
        if (layEvent === "edit") {
            openAddOrUpdateRoleDialog(obj.data.id);
        } else if (layEvent === "del") {
            layer.confirm("确认删除当前记录?", {icon: 3, title: "角色管理"}, function (index) {
                $.post(ctx + "/roles/delete", {roleId: obj.data.id}, function (data) {
                    if (data.code == 200) {
                        layer.msg("删除成功", {icon: 6});
                        tableIns.reload();
                    } else {
                        layer.msg(data.msg, {icon: 5});
                    }
                })
            })
        }
    });


});
