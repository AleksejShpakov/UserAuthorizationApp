<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Главная</title>
    <%@ include file="/views/main/defaultResources.jsp" %>
</head>
<body>
<%@ include file="/views/main/header.jsp" %>

<div class="container-fluid" style="padding-top: 15px;">
    <div class="row justify-content-center">

        <div class="col-12 col-sm-8 col-md-6 col-lg-4 col-xl-4">

            <div class="card">
                <div class="card-body">
                    Добро пожаловать
                    <%if (request.getAttribute("user_email") != null) {
                        out.print(request.getAttribute("user_email").toString());
                    }%>!
                    </br>
                    Дата регистрации:
                    <%if (request.getAttribute("user_registration_date") != null) {
                        out.print(request.getAttribute("user_registration_date").toString());
                    }%>

                    </br>
                    </br>
                    <button type="button" class="btn btn-primary" onclick="document.location.href='${pageContext.request.contextPath}/logout'">Выйти</button>
                </div>
            </div>

        </div>



    </div>
</div>

<%@ include file="/views/main/footer.jsp" %>

<script>

</script>

</body>
</html>

