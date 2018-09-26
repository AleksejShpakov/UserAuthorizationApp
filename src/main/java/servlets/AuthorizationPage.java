package servlets;

import org.json.JSONObject;
import services.AuthorizationService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class AuthorizationPage extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        execute(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        execute(request, response);
    }

    private void execute(HttpServletRequest request, HttpServletResponse response){
        JSONObject outJSONObject;
        String methodName;
        Method method;

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

    @SuppressWarnings("unused")
    private JSONObject showPage(HttpServletRequest request, HttpServletResponse response){
        JSONObject outJSONObject = new JSONObject();

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("views/AuthorizationPage.jsp");

        try {
            requestDispatcher.forward(request, response);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }

        return outJSONObject;
    }

    @SuppressWarnings("unused")
    private JSONObject authorize(HttpServletRequest request, HttpServletResponse response) {
        JSONObject outJSONObject = new JSONObject();
        String eMail;
        String password;
        String sessionID;
        boolean successAuthorization = false;

        eMail = request.getParameter("email");
        password = request.getParameter("password");
        sessionID = request.getSession().getId();

        try {
            successAuthorization = AuthorizationService.getInstance().authorize(eMail, password, sessionID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(successAuthorization){
            outJSONObject.put("authorized", true);
        }else{
            outJSONObject.put("authorized", false);
        }

        return outJSONObject;
    }

}
