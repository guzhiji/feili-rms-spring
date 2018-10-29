<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Foundation for Sites</title>
        <link rel="stylesheet" href="/css/foundation.css">
        <link rel="stylesheet" href="/css/app.css">
    </head>
    <body>
        <div class="top-bar">
            <div class="top-bar-left">
                <ul class="menu">
                    <li class="menu-text">Site Title</li>
                    <li><a href="${contextPath}/">Home</a></li>
                    <c:if test="${user == null}">
                        <li><a href="${contextPath}/login">Login</a></li>
                    </c:if>
                    <c:if test="${user != null}">
                        <li><a href="${contextPath}/admin">Admin</a></li>
                    </c:if>
                    <li><input type="search" placeholder="Search"></li>
                    <li><button type="button" class="button">Search</button></li>
                </ul>
            </div>
            <div class="top-bar-right">
                <c:if test="${user != null}">
                    <ul class="dropdown menu" data-dropdown-menu>
                        <li>
                            <a href="#">Account</a>
                            <ul class="menu vertical">
                                <li><a href="${contextPath}/chpwd">Change Password</a></li>
                                <li><a href="${contextPath}/logout">Logout</a></li>
                            </ul>
                        </li>
                    </ul>
                </c:if>
            </div>
        </div>
        <div class="grid-container">
            <div class="grid-x grid-padding-x">
                <div class="large-12 cell">
                    <c:forEach items="${blogs}" var="blog">
                        <div class="primary callout">
                            <a href="${contextPath}/view/${blog.slug}"><h3>${blog.title}</h3></a>
                            <div>
                                ${blog.modified} - ${blog.owner.username}
                            </div>
                            <div>
                                <c:forEach items="${blog.tags}" var="tag">
                                    <span class="label success"><c:out value="${tag.name}" /></span>
                                </c:forEach>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

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

            <script src="/js/vendor/jquery.js"></script>
            <script src="/js/vendor/what-input.js"></script>
            <script src="/js/vendor/foundation.js"></script>
            <script src="/js/app.js"></script>
    </body>
</html>
