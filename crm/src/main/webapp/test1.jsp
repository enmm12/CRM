
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>Title</title>
</head>
<body>

    $.ajax({

        url : "",
        data : {

        },
        type : "",
        dataType : "json",
        success : function(data){

        }
    })

    String createBy = ((User)request.getSession().getAttribute("user")).getName();
    String createTime = DateTimeUtil.getSysTime();

    $(".time").datetimepicker({
        minView: "month",
        language:  'zh-CN',
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true,
        pickerPosition: "bottom-left"
    });


</body>
</html>
