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
            String userName  = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (SPConstants.LOGGED_IN_SESSION_ID.equals(cookie.getName())) {
                        sid = cookie.getValue();
                        if (UserStore.getInstance().isLoggedIn(sid)) {
                            userName = UserStore.getInstance().getLoggedInUserName(sid);
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
        <p>The applet goes here.</p>
        <applet code = 'org.rahasnym.spclient.SIDConfirmationApplet.class', archive = 'SPClient-1.0-SNAPSHOT.jar, CryptoLib-1.0-SNAPSHOT.jar, RahasNymLib-1.0-SNAPSHOT.jar, commons-logging-1.1.1.jar, commons-httpclient-3.1.jar, commons-codec-1.2.jar, json-20090211.jar, plugin.jar',
        width = 600
        height = 250>
        <param name="permissions" value="all-permissions"/>
        <param name=<%=Constants.USER_NAME%> value=<%=userName%>>
        <param name=<%=Constants.SESSION_ID%> value=<%=sid%>>
        </applet>
        <p><a href="portal.jsp">Back to portal</a> </p>
        <p><a href="logout">logout</a></p>

</body>
</html>