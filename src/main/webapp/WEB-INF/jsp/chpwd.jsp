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
                    <li><a href="${contextPath}/edit" class="button success">New Blog</a></li>
                </ul>
            </div>
            <div class="top-bar-right">
                <ul class="dropdown menu" data-dropdown-menu>
                    <li>
                        <a href="#">Account</a>
                        <ul class="menu vertical">
                            <li class="active"><a href="${contextPath}/chpwd">Change Password</a></li>
                            <li><a href="${contextPath}/logout">Logout</a></li>
                        </ul>
                    </li>
                </ul>
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
                        <c:if test="${not empty message}">
                            <div class="success callout">
                                <c:out value="${message}" />
                            </div>
                        </c:if>
                        <form method="POST">
                            <div class="grid-x grid-padding-x">
                                <div class="large-12 cell">
                                    <label>Original Password</label>
                                    <input name="original" type="password" placeholder="original password" />
                                </div>
                            </div>
                            <div class="grid-x grid-padding-x">
                                <div class="large-12 cell">
                                    <label>New Password</label>
                                    <input name="password" type="password" placeholder="new password" />
                                </div>
                            </div>
                            <input type="submit" class="button" value="CHANGE" />
                            <a class="button secondary" href="${contextPath}/admin">BACK</a>
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

