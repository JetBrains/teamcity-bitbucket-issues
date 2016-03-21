<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/include.jsp"%>
<jsp:useBean id="issue" scope="request" type="jetbrains.buildServer.issueTracker.IssueEx"/>
<c:set var="stateClass" value="hidden"/>
<bs:issueDetailsPopup issue="${issue}" stateClass="${stateClass}"/>