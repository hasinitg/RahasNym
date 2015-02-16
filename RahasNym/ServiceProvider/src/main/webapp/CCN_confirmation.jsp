<%@ page import="org.rahasnym.api.Constants" %>
<%@ page import="org.rahasnym.serviceprovider.SPConstants" %>
<%@ page import="org.rahasnym.serviceprovider.UserStore" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register for free shipping membership.</title>
</head>
<body>
<%
    /*Check if a valid cookie is set.*/
    Cookie[] cookies = request.getCookies();
    String sid = null;
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (SPConstants.LOGGED_IN_SESSION_ID.equals(cookie.getName())) {
                sid = cookie.getValue();
                if (UserStore.getInstance().isLoggedIn(sid)) {
                    String userName = UserStore.getInstance().getLoggedInUserName(sid);
%>
<h3>Welcome to the amazingshop portal <%=userName%>.</h3>
<%
                } else {
                    response.sendRedirect("login.jsp");
                }
            }
        }
    } else {
        response.sendRedirect("login.jsp");
    }
%>
<p>The receipt is shown here too.</p>
<p>The CCN confirmation applet goes here.</p>
<p><a href="logout">logout</a></p>

</body>
</html>