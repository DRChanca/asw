<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Acceso denegado</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Acceso denegado</h1>

    <p>${requestScope.errorMessage}</p>

    <a href="${pageContext.request.contextPath}/index.html">
        Volver al inicio
    </a>
</body>
</html>