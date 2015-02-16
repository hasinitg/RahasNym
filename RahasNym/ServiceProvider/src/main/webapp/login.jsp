<%@ page import="org.rahasnym.api.Constants" %>
<%@ page import="org.rahasnym.serviceprovider.SPConstants" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login Page.</title>
</head>
<body>

<form method="post" action="login">
    <fieldset>
        <%
            if (((request.getAttribute(SPConstants.LOGGED_IN_STATUS)) != null) &&
                    (request.getAttribute(SPConstants.LOGGED_IN_STATUS).equals(SPConstants.LOG_IN_FAILURE))) {
        %>
        <p>Login Failed. Please try again.</p>
        <legend> Please Login here.</legend>
        <% } else {%>
        <legend> Please Login here.</legend>
        <%
            }
        %>

        Name: <input type="text" name="username"/><br/><br/>
        Password: <input type="password" name="password"/><br/><br/>
    </fieldset>

    <!--TODO: need to set the hidden type value properly to avoid cxrf attacks.-->
    <input type="hidden" name="secret" value="888"/>
    <input type="submit" value="SEND"/>
    <input type="reset" value="CLEAR"/>
</form>
</body>
</html>