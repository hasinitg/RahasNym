package org.rahasnym.serviceprovider;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/14/15
 * Time: 11:37 PM
 */
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //obtain user name and password from the request
        String userName = req.getParameter("username");
        if (!UserStore.getInstance().isEnrolled(userName)) {
            System.out.println("No user with username found.");
            Cookie loggedInStatusCookie = new Cookie(SPConstants.LOGGED_IN_STATUS, SPConstants.LOG_IN_FAILURE);
            resp.addCookie(loggedInStatusCookie);
            resp.sendRedirect("/amazingshop/login.jsp");
        }
        String password = req.getParameter("password");
        boolean isAuthenticated = UserStore.getInstance().authenticate(userName, password);
        if (isAuthenticated) {
            System.out.println("login successful.");
            Cookie loggedInStatusCookie = new Cookie(SPConstants.LOGGED_IN_STATUS, SPConstants.LOG_IN_SUCCESS);
            resp.addCookie(loggedInStatusCookie);
            String sessionID = UUID.randomUUID().toString();
            UserStore.getInstance().addLoggedIn(sessionID, userName);
            Cookie loggedInSessionCookie = new Cookie(SPConstants.LOGGED_IN_SESSION_ID, sessionID);
            resp.addCookie(loggedInSessionCookie);
            resp.sendRedirect("/amazingshop/portal.jsp");

        } else {
            System.out.println("login failed.");
            Cookie loggedInStatusCookie = new Cookie(SPConstants.LOGGED_IN_STATUS, SPConstants.LOG_IN_FAILURE);
            resp.addCookie(loggedInStatusCookie);
            resp.sendRedirect("/amazingshop/login.jsp");
        }

    }
}
