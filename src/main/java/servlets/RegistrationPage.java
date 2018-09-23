package servlets;

import constants.Error;
import constants.Status;
import entity.User;
import org.json.JSONObject;
import services.AuthorizationService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPage extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        execute(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        execute(req, resp);
    }

    private void execute(HttpServletRequest request, HttpServletResponse response){
        JSONObject outJSONObject = new JSONObject();
        String methodName = "";
        Method method = null;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        methodName = request.getParameter("method");
        if (methodName == null || methodName.equals("")){
            methodName = "showPage";
        }

        try {
            method = this.getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        try {
            outJSONObject = (JSONObject) method.invoke(this, request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        if (outJSONObject != null) {
            try {
                response.getWriter().write(outJSONObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject showPage(HttpServletRequest request, HttpServletResponse response){
        JSONObject outJSONObject = new JSONObject();

        try {
            request.getRequestDispatcher("views/RegistrationPage.jsp").forward(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }

        return outJSONObject;
    }

    private JSONObject register(HttpServletRequest request, HttpServletResponse response) {
        JSONObject outJSONObject = new JSONObject();
        User user = null;
        String eMail;
        String password;
        String sessionID;

        eMail = request.getParameter("email");
        password = request.getParameter("password");
        sessionID = request.getSession().getId();

        if( AuthorizationService.getInstance().isExist(eMail) ){
            outJSONObject.put("status", Status.ERROR);
            outJSONObject.put("message", Error.EMAIL_ALREADY_EXISTS.message());
            outJSONObject.put("code", Error.EMAIL_ALREADY_EXISTS.code());

            return outJSONObject;
        }else{
            if(!isCorrectPassword(password)){
                outJSONObject.put("status", Status.ERROR);
                outJSONObject.put("message", Error.PASSWORD_IS_INCORRECT.message());
                outJSONObject.put("code", Error.PASSWORD_IS_INCORRECT.code());

                return outJSONObject;
            }

            user = new User(eMail, password);
            AuthorizationService.getInstance().addUser(user);
            AuthorizationService.getInstance().authorize(eMail, password, sessionID);

            outJSONObject.put("status", Status.SUCCESS);
        }

        return outJSONObject;
    }

    private boolean isCorrectPassword(String password){
        final String regex = "\\W";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(password);

        return password.length() >= 6 && !matcher.find();
    }


}
