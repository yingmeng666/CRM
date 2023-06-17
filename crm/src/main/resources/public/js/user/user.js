layui.use(['table', 'layer', "form"], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //用户列表展示
    var tableIns = table.render({
        elem: '#userList',
        url: ctx + '/users/list',
        cellMinWidth: 95,
        page: true,
        height: "full-125",
        limits: [10, 15, 20, 25],
        limit: 10,
        toolbar: "#toolbarDemo",
        id: "userListTable",
        cols: [[
            {type: "checkbox", fixed: "left", width: 50},
            {field: "id", title: '编号', fixed: "true", width: 80},
            {field: 'userName', title: '用户名', minWidth: 50, align: "center"},
            {field: 'email', title: '用户邮箱', minWidth: 100, align: 'center'},
            {field: 'phone', title: '手机号', minWidth: 100, align: 'center'},
            {field: 'trueName', title: '真实姓名', align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center', minWidth: 150},
            {field: 'updateDate', title: '更新时间', align: 'center', minWidth: 150},
            {title: '操作', minWidth: 150, templet: '#userListBar', fixed: "right", align: "center"}
        ]]
    });


    // 多条件搜索
    $(".search_btn").on("click", function () {
        table.reload("userListTable", {
            page: {
                curr: 1
            },
            where: {
                userName: $("input[name='userName']").val(),// 用户名
                email: $("input[name='email']").val(),// 邮箱
                phone: $("input[name='phone']").val()    //手机号
            }
        })
    });


    // 头工具栏事件
    table.on('toolbar(users)', function (obj) {
        switch (obj.event) {
            case "add":
                openAddOrUpdateUserDialog();
                break;
            case "del":
                var checkStatus = table.checkStatus(obj.config.id);
                deleteUsers(checkStatus.data);
                break;
        }
    });

    table.on('tool(users)', function (obj) {
        if (obj.event == "edit") {
            openAddOrUpdateUserDialog(obj.data.id)
        } else if (obj.event == "del") {
            deleteUser(obj.data.id)
        }
    });

    function deleteUser(id) {
        //弹出询问层
        layer.confirm('确认删除记录吗', {icon: 3, title: '用户管理'}, function (index) {
            //关闭询问层
            layer.close(index);
            //发送ajax请求后台
            $.ajax({
                type: "POST",
                url: ctx + '/users/delete',
                data: {
                    ids: id
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

    function openAddOrUpdateUserDialog(id) {
        var title = "<h3>用户管理-用户添加</h3>";
        var url = ctx + "/users/toAddOrUpdateUserPage";
        if (id != null && id != "") {
            title = "<h3>用户管理-用户更新</h3>";
            url = url + "?id=" + id;
        }
        layui.layer.open({
            title: title,
            type: 2,
            area: ["650px", "400px"],
            maxmin: true,
            content: url
        })
    }

    function deleteUsers(userData) {
        if (userData.length < 1) {
            layer.msg("请选择需要删除的用户记录", {icon: 5});
            return;
        }
        //询问层
        layer.confirm("您要删除这些数据吗！", {icon: 3, title: '用户管理'}, function (index) {
            //关闭询问层
            layer.close(index);
            //参数拼接
            var ids = "ids=";
            for (let i = 0; i < userData.length; i++) {
                if (i == userData.length - 1) {
                    ids = ids + userData[i].id;
                } else {
                    ids = ids + userData[i].id + "&ids=";
                }
            }
            //发送请求到后台
            $.ajax({
                type: "POST",
                url: ctx + "/users/delete",
                data: ids,
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
