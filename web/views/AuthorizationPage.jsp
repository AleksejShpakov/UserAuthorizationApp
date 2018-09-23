<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Авторизация</title>
    <%@ include file="/views/main/defaultResources.jsp" %>
</head>
<body>
<%@ include file="/views/main/header.jsp" %>

<div class="container-fluid" style="padding-top: 15px;">
    <div class="row justify-content-center">

        <div class="col-12 col-sm-8 col-md-6 col-lg-4 col-xl-4">

            <div class="card authorization">
                <div class="card-body">
                    <form>
                        <div class="form-group">
                            <label for="inputEmail">E-Mail</label>
                            <input type="email" class="form-control" id="inputEmail" aria-describedby="emailHelp" placeholder="example@mail.com">
                            <small id="emailHelp" class="form-text text-muted">Введите E-Mail, указанный при регистрации</small>
                        </div>
                        <div class="form-group">
                            <label for="inputPassword">Пароль</label>
                            <input type="password" class="form-control" id="inputPassword" placeholder="пароль">
                            <div class="invalid-feedback">Неверный логин или пароль</div>
                        </div>
                        <button type="button" class="btn btn-success" onclick="authorize();">Войти</button>
                    </form>

                    <label class="text-muted">или</label>

                    <a href="${pageContext.request.contextPath}/registration" class="card-link">Зарегистрироваться</a>
                </div>
            </div>

        </div>



    </div>
</div>

<%@ include file="/views/main/footer.jsp" %>

<script>
    $(document).ready(function() {
        $('.authorization').css('top', 0);
    });

    function authorize() {
       $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/authorization",
            data: {
                method: 'authorize',
                email: document.getElementById('inputEmail').value,
                password: document.getElementById('inputPassword').value
            },
            success: function(data){
                if(data.authorized){
                    document.location.href = "${pageContext.request.contextPath}/MainPage";
                }else{
                    $('#inputEmail').addClass('is-invalid');
                    $('#inputPassword').addClass('is-invalid');
                }
            }
        });
    };

</script>

</body>
</html>

