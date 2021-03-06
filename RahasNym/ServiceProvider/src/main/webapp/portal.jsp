<%@ page import="org.rahasnym.api.Constants" %>
<%@ page import="org.rahasnym.serviceprovider.UserStore" %>
<%@ page import="org.rahasnym.serviceprovider.SPConstants" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
</head>
<body>
<%
    /*Check if a valid cookie is set.*/
    Cookie[] cookies = request.getCookies();
    String sid = null;
    String userName = null;
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
                    response.sendRedirect("/amazingshop/login.jsp");
                }
            }
        }
    }
%>
<%
    if(!UserStore.getInstance().getUser(userName).isFreeShippingEnabled()) {
%>
<p>Are you a student? Obtain your free-shipping membership <a href="free_shipping_membership.jsp">here.</a></p>
<%
    }else{
%>
<p>You have free shipping membership enabled.</p>
<%
    }
%>
<p><a href="shopping_portal.jsp"> Continue shipping.</a> </p>
<p><a href="logout">logout</a></p>
</body>