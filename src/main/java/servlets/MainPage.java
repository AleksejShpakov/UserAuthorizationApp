package servlets;

import entity.User;
import services.AuthorizationService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class MainPage extends HttpServlet{

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user;
        HttpSession httpSession = null;
        ServletContext servletContext = null;
        RequestDispatcher requestDispatcher = null;
        SimpleDateFormat simpleDateFormat = null;

        httpSession = request.getSession();
        try {
            if(AuthorizationService.getInstance().isAuthorized(httpSession.getId())){
                user = AuthorizationService.getInstance().getUserBySessionId(httpSession.getId());
                request.setAttribute("user_email", user.getMail());
                simpleDateFormat = new SimpleDateFormat();
                request.setAttribute("user_registration_date", simpleDateFormat.format(user.getRegistrationDate()));
                request.getRequestDispatcher("views/MainPage.jsp").forward(request, response);
            }else{
                response.sendRedirect(request.getContextPath() + "/authorization");
            }
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
