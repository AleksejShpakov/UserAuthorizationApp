package servlets;

import entity.User;
import org.json.JSONArray;
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

public class AuthorizationPage extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        execute(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        execute(request, response);
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

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("views/AuthorizationPage.jsp");

        try {
            requestDispatcher.forward(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }

        return outJSONObject;
    }

    private JSONObject authorize(HttpServletRequest request, HttpServletResponse response) {
        JSONObject outJSONObject = new JSONObject();
        String eMail;
        String password;
        String sessionID;
        boolean successAuthorization = false;

        eMail = request.getParameter("email");
        password = request.getParameter("password");
        sessionID = request.getSession().getId();

        successAuthorization = AuthorizationService.getInstance().authorize(eMail, password, sessionID);

        if(successAuthorization){
            outJSONObject.put("authorized", true);
        }else{
            outJSONObject.put("authorized", false);
        }

        return outJSONObject;
    }

}
