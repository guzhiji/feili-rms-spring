<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login</title>
        <link rel="stylesheet" href="/css/foundation.css">
        <link rel="stylesheet" href="/css/app.css">
    </head>
    <body>
        <div class="top-bar">
            <div class="top-bar-left">
                <ul class="menu">
                    <li class="menu-text">Site Title</li>
                    <li><a href="${contextPath}/">Home</a></li>
                    <li><input type="search" placeholder="Search"></li>
                    <li><button type="button" class="button">Search</button></li>
                </ul>
            </div>
            <div class="top-bar-right">
            </div>
        </div>

        <div class="grid-container">
            <div class="grid-x grid-padding-x">
                <div class="large-12 cell">
                    <div class="callout">
                        <c:if test="${not empty error_message}">
                            <div class="alert callout">
                                <c:out value="${error_message}" />
                            </div>
                        </c:if>
                        <form method="POST">
                            <div class="grid-x grid-padding-x">
                                <div class="large-12 cell">
                                    <label>Username</label>
                                    <input name="username" type="text" placeholder="username" />
                                </div>
                            </div>
                            <div class="grid-x grid-padding-x">
                                <div class="large-12 cell">
                                    <label>Password</label>
                                    <input name="password" type="password" placeholder="password" />
                                </div>
                            </div>
                            <input type="submit" class="button" value="LOGIN" />
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <script src="/js/vendor/jquery.js"></script>
        <script src="/js/vendor/what-input.js"></script>
        <script src="/js/vendor/foundation.js"></script>
        <script src="/js/app.js"></script>
    </body>
</html>

