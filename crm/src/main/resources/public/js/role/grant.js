$(function () {
    loadModuleInfo();
});


var zTreeObj;

function loadModuleInfo() {
    var setting = {
        check: {
            enable: true
        },
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
            onCheck: zTreeOnCheck
        }
    }
    $.ajax({
        type: "GET",
        url: ctx + '/modelus/queryAllModelus',
        data: {
            roleId: $("[name='roleId']").val()
        },
        dataType: "json",
        success: function (result) {
            zTreeObj = $.fn.zTree.init($("#test1"), setting, result);
        }
    });

}


function zTreeOnCheck(event, treeId, treeNode) {
    var nodes = zTreeObj.getCheckedNodes(true);

    //获取所有的资源的ids，格式?ids=1&ids=2&ids=3
    if (nodes.length > 0) {
        var mIds = "mIds=";
        for (let i = 0; i < nodes.length; i++) {
            if (i == nodes.length - 1) {
                mIds += nodes[i].id;
            } else {
                mIds += nodes[i].id + "&mIds=";
            }
        }
    }

    var roleId = $("[name='roleId']").val();

    $.ajax({
        type: "post",
        url: ctx + '/roles/addGrand',
        data: mIds + "&roleId=" + roleId,
        success: function (data) {
            console.log(data);
        }
    })

};