<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Foundation for Sites</title>
        <link rel="stylesheet" href="http://localhost:8080/rms-spring-app-0.1.0/css/foundation.css">
        <link rel="stylesheet" href="http://localhost:8080/rms-spring-app-0.1.0/css/app.css">
    </head>
    <body>
        <div class="top-bar">
            <div class="top-bar-left">
                <ul class="dropdown menu" data-dropdown-menu>
                    <li class="menu-text">Site Title</li>
                    <li><a href="/rms-spring-app-0.1.0/app/blog/">Home</a></li>
                    <li><a href="/rms-spring-app-0.1.0/app/blog/admin?page=1">Admin</a></li>
                    <li>
                        <a href="#">Account</a>
                        <ul class="menu vertical">
                            <li><a href="#">One</a></li>
                            <li><a href="#">Two</a></li>
                            <li><a href="/rms-spring-app-0.1.0/app/blog/logout">Logout</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
            <div class="top-bar-right">
                <ul class="menu">
                    <li><input type="search" placeholder="Search"></li>
                    <li><button type="button" class="button">Search</button></li>
                </ul>
            </div>
        </div>
        <div class="grid-container">
            <c:forEach items="${blogs}" var="blog">
                <div class="grid-x grid-padding-x">
                    <div class="large-12 cell">
                        <div class="primary callout">
                            <a href="/rms-spring-app-0.1.0/app/blog/view/${blog.slug}"><h3>${blog.title}</h3></a>
                            ${blog.modified} - ${blog.owner.username}
                        </div>
                    </div>
                </div>
            </c:forEach>

            <div class="grid-x grid-padding-x">
                <div class="large-6 medium-6 cell">
                    <div class="primary callout">
                        <p>Six cell</p>
                    </div>
                </div>
                <div class="large-6 medium-6 cell">
                    <div class="primary callout">
                        <p>Six cell</p>
                    </div>
                </div>
            </div>
            <div class="grid-x grid-padding-x">
                <div class="large-4 medium-4 small-4 cell">
                    <div class="primary callout">
                        <p>Four cell</p>
                    </div>
                </div>
                <div class="large-4 medium-4 small-4 cell">
                    <div class="primary callout">
                        <p>Four cell</p>
                    </div>
                </div>
                <div class="large-4 medium-4 small-4 cell">
                    <div class="primary callout">
                        <p>Four cell</p>
                    </div>
                </div>
            </div>

            <script src="http://localhost:8080/rms-spring-app-0.1.0/js/vendor/jquery.js"></script>
            <script src="http://localhost:8080/rms-spring-app-0.1.0/js/vendor/what-input.js"></script>
            <script src="http://localhost:8080/rms-spring-app-0.1.0/js/vendor/foundation.js"></script>
            <script src="http://localhost:8080/rms-spring-app-0.1.0/js/app.js"></script>
    </body>
</html>
