<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Identificación</title>
        <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Identificarse</h1>

    <form action="${pageContext.request.contextPath}/Controller"
          method="post">

        <input type="hidden" name="action" value="LoginAction">

        <label for="username">Usuario:</label>

        <select id="username" name="username">
            <option value="user1">user1</option>
            <option value="user2">user2</option>
        </select>

        <button type="submit">Entrar</button>
    </form>

    <p>${requestScope.loginMessage}</p>

    <a href="${pageContext.request.contextPath}/index.html">
        Volver al inicio
    </a>
</body>
</html>