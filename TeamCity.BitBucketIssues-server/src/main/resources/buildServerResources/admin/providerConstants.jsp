<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jetbrains.buildServer.issueTracker.bitbucket.BitBucketConstants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="name" value="<%=BitBucketConstants.PARAM_NAME%>"/>
<c:set var="repository" value="<%=BitBucketConstants.PARAM_REPOSITORY%>"/>
<c:set var="authType" value="<%=BitBucketConstants.PARAM_AUTH_TYPE%>"/>
<c:set var="username" value="<%=BitBucketConstants.PARAM_USERNAME%>"/>
<c:set var="password" value="<%=BitBucketConstants.PARAM_PASSWORD%>"/>
<c:set var="pattern" value="<%=BitBucketConstants.PARAM_PATTERN%>"/>

<c:set var="authAnonymous" value="<%=BitBucketConstants.AUTH_ANONYMOUS%>"/>
<c:set var="authLoginPassword" value="<%=BitBucketConstants.AUTH_LOGINPASSWORD%>"/>


