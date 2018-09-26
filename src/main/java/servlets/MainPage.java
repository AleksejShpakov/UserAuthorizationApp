package servlets;

import entity.User;
import services.AuthorizationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class MainPage extends HttpServlet{

    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        User user;
        HttpSession httpSession;
        SimpleDateFormat simpleDateFormat;

        httpSession = request.getSession();
        try {
            if(AuthorizationService.getInstance().isAuthorized(httpSession.getId())){
                user = AuthorizationService.getInstance().getUserBySessionId(httpSession.getId());
                if(user == null){
                    response.sendRedirect(request.getContextPath() + "/authorization");
                    return;
                }
                request.setAttribute("user_email", user.getMail());
                simpleDateFormat = new SimpleDateFormat();
                request.setAttribute("user_registration_date", simpleDateFormat.format(user.getRegistrationDate()));
                request.getRequestDispatcher("views/MainPage.jsp").forward(request, response);
            }else{
                response.sendRedirect(request.getContextPath() + "/authorization");
            }
        } catch (ServletException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
