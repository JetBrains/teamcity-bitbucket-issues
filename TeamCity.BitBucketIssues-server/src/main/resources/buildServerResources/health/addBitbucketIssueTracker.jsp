<%--
  ~ Copyright 2000-2022 JetBrains s.r.o.
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

<%--suppress XmlPathReference --%>
<%@ taglib prefix="util" uri="/WEB-INF/functions/util" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="bs" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/include-internal.jsp"%>
<%--@elvariable id="healthStatusItem" type="jetbrains.buildServer.serverSide.healthStatus.BuildTypeSuggestedItem"--%>
<%--@elvariable id="project" type="jetbrains.buildServer.serverSide.SProject"--%>
<%--@elvariable id="suggestedTrackers" type="java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.Object>>"--%>
<c:set var="suggestedTrackers" value="${healthStatusItem.additionalData['suggestedTrackers']}"/>
<c:set var="numSuggestedTrackers" value="${fn:length(suggestedTrackers.keySet())}"/>

<div class="suggestionItem">
  There <bs:are_is val="${numSuggestedTrackers}"/>
  <c:choose>
    <c:when test="${numSuggestedTrackers == 1}"> a </c:when>
    <c:otherwise> ${numSuggestedTrackers} </c:otherwise>
  </c:choose>
  VCS root<bs:s val="${numSuggestedTrackers}"/> in the project <admin:editProjectLink projectId="${project.externalId}"><c:out value="${project.fullName}"/></admin:editProjectLink>
  pointing to Bitbucket Cloud. You can connect TeamCity to Bitbucket Cloud issue tracker<bs:s val="${numSuggestedTrackers}"/>.
  <c:forEach var="itemEntry" items="${suggestedTrackers}">
    <c:set var="item" value="${itemEntry.value}"/>
    <div class="suggestionAction">
      <c:url var="url" value="/admin/editProject.html?init=1&projectId=${project.externalId}&tab=issueTrackers&#addTracker=${item['type']}&repoUrl=${util:urlEscape(item['repoUrl'])}&suggestedName=${util:urlEscape(item['suggestedName'])}"/>
      <a class="tc-icon_before icon16 addNew" href="${url}">Add connection for ${item['suggestedName']}</a>
    </div>
  </c:forEach>
</div>
