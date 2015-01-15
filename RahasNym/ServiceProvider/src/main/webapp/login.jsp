<!DOCTYPE html>
<html>
<head>
    <title>Login Page.</title>
</head>
<body>

<form method="post" action="login">
    <fieldset>
        <legend>Please Login here.</legend>
        Name: <input type="text" name="username"/><br /><br />
        Password: <input type="password" name="password"/><br /><br />
    </fieldset>

    <!--TODO: need to set the hidden type value properly to avoid cxrf attacks.-->
    <input type="hidden" name="secret" value="888" />
    <input type="submit" value="SEND" />
    <input type="reset" value="CLEAR" />
</form>
</body>
</html>