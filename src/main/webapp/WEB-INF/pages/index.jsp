<%--
  Created by IntelliJ IDEA.
  User: zld
  Date: 2021/12/8
  Time: 11:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>秒杀首页</title>
    <script type="text/javascript" src="../../js/jquery-1.12.4.min.js"></script>
</head>
<body>
<h1>秒杀页面！！！！！！！</h1>
<button id="bugButton">秒杀</button>
<script>
    $(document).ready(function (){
        $("#bugButton").click(function (){
            var commodityId = 11809;
            $.ajax({
                url : "bug",
                type: "post",
                contentType:"application/x-www-form-urlencoded",
                data:{
                    commodityId : commodityId
                },
                success:function (e){
                    alert(e);
                }
            })
        })
    })
</script>
</body>
</html>
