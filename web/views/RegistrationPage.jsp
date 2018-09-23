<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Регистрация</title>
    <%@ include file="/views/main/defaultResources.jsp" %>
</head>
<body>
<%@ include file="/views/main/header.jsp" %>

<div class="container-fluid" style="padding-top: 15px;">
    <div class="row justify-content-center">

        <div class="col-12 col-sm-8 col-md-6 col-lg-4 col-xl-4">

            <div class="card">
                <div class="card-body">
                    <form name="registrationForm" method="post" action="registration">

                        <div class="form-group">
                            <label for="inputEmail">E-Mail</label>
                            <input type="email" class="form-control" id="inputEmail" name="email" aria-describedby="emailHelp" placeholder="example@mail.com">
                            <small id="emailHelp" class="form-text text-muted">Ваш E-Mail</small>
                            <div class="invalid-feedback">Пользователь с таким E-Mail уже зарегистрирован</div>
                        </div>


                        <div class="form-group">
                            <label for="inputPassword">Пароль</label>
                            <input type="password" class="form-control" id="inputPassword" name="password" aria-describedby="passwordHelp" placeholder="пароль">
                            <small id="passwordHelp" class="form-text text-muted">Пароль должен состоять из любых цифр и латинских букв. Минимальное количество символов - 6</small>
                            <div class="invalid-feedback">Некорректный пароль</div>
                        </div>

                        <button type="button" onclick="register()" class="btn btn-primary">Зарегистрироваться</button>
                    </form>
                </div>
            </div>

        </div>



    </div>
</div>

<%@ include file="/views/main/footer.jsp" %>

<script>
    function register() {
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/registration",
            data: {
                method: 'register',
                email: document.getElementById('inputEmail').value,
                password: document.getElementById('inputPassword').value
            },
            success: function(data){
                console.log(data);
                if(!data.status) {
                    console.error('field "status" in data is undefined');
                    return;
                }

                if(data.status === 'SUCCESS'){
                    document.location.href = "${pageContext.request.contextPath}/MainPage";
                    return;
                }

                if(data.status === 'ERROR'){
                    if(data.code === undefined){
                        console.error('field "code" in data is undefined');
                        return;
                    }
                    switch(data.code){
                        case 0:  $('#inputEmail').addClass('is-invalid');
                                break;
                        case 1:  $('#inputPassword').addClass('is-invalid');
                                break;
                    }
                }
            }
        });
    };
</script>

</body>
</html>

