package org.rahasnym.serviceprovider;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 1/19/15
 * Time: 10:34 AM
 */
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        String sid = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SPConstants.LOGGED_IN_SESSION_ID.equals(cookie.getName())) {
                    sid = cookie.getValue();
                    if (UserStore.getInstance().isLoggedIn(sid)) {
                        cookie.setMaxAge(0);
                        UserStore.getInstance().removeLoggedIn(sid);
                    }
                }
            }
        }
        resp.sendRedirect("/amazingshop/login.jsp");
    }
}
