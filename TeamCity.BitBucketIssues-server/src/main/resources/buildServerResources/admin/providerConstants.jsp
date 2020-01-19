<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jetbrains.buildServer.issueTracker.bitbucket.BitBucketConstants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  ~ Copyright 2000-2020 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<c:set var="name" value="<%=BitBucketConstants.PARAM_NAME%>"/>
<c:set var="repository" value="<%=BitBucketConstants.PARAM_REPOSITORY%>"/>
<c:set var="authType" value="<%=BitBucketConstants.PARAM_AUTH_TYPE%>"/>
<c:set var="username" value="<%=BitBucketConstants.PARAM_USERNAME%>"/>
<c:set var="password" value="<%=BitBucketConstants.PARAM_PASSWORD%>"/>
<c:set var="pattern" value="<%=BitBucketConstants.PARAM_PATTERN%>"/>

<c:set var="authAnonymous" value="<%=BitBucketConstants.AUTH_ANONYMOUS%>"/>
<c:set var="authLoginPassword" value="<%=BitBucketConstants.AUTH_LOGIN_PASSWORD%>"/>


