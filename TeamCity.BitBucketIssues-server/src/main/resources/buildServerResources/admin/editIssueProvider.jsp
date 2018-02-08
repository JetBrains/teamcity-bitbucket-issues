<%@ page import="jetbrains.buildServer.web.util.SessionUser" %>
<%@ include file="/include.jsp"%>
<%@ include file="providerConstants.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags"%>

<jsp:useBean id="providerType" scope="request" type="jetbrains.buildServer.issueTracker.bitbucket.BitBucketIssueProviderType"/>

<script type="text/javascript">
  (function() {
    BS.BitbucketIssues = {
      selectedAuth: undefined,
      selector: undefined,

      init: function(select) {
        this.selector = $(select);
        this.selectAuthType();
        BS.Util.hide('privateNote');
      },

      selectAuthType: function() {
        this.selectedAuth = this.selector.value;
        this.onTypeChanged();
      },

      onTypeChanged: function() {
        var s = '.' + this.selectedAuth;
        $j('.js_authsetting')
                .filter(s).removeClass('hidden').end()
                .not(s).addClass('hidden');
        BS.MultilineProperties.updateVisible();
      },

      onRepoChanged: function() {
        BS.Util.hide('privateNote');
      }
    };
  })();
</script>

<div>
  <table class="editProviderTable">
    <%--@elvariable id="showType" type="java.lang.Boolean"--%>
    <c:if test="${showType}">
      <tr>
        <th><label class="shortLabel">Connection Type:</label></th>
        <td><bs:out value=" ${providerType.displayName}"/></td>
      </tr>
    </c:if>
    <tr>
      <th><label for="${name}" class="shortLabel">Display Name:<l:star/></label></th>
      <td>
        <props:textProperty name="${name}" maxlength="100"/>
        <span id="error_${name}" class="error"></span>
      </td>
    </tr>
    <tr>
      <th><label for="${repository}" class="shortLabel">Repository URL:<l:star/></label></th>
      <td>
        <div>
          <props:textProperty name="${repository}" maxlength="100" onkeyup="BS.BitbucketIssues.onRepoChanged();"/>
          <jsp:include page="/admin/repositoryControls.html?projectId=${project.externalId}&pluginName=bitbucket"/>
          <span id="error_${repository}" class="error"></span>
        </div>
        <p>
          <div id="privateNote" class="attentionComment" style="width:90%">
            This repository is private and requires a username and password to access
          </div>
        </p>
      </td>
    </tr>
    <tr>
      <th><label for="${authType}_select">Authentication:</label></th>
      <td>
        <props:selectProperty name="${authType}"
                              id="${authType}_select"
                              onchange="BS.BitbucketIssues.selectAuthType();">
          <props:option value="${authAnonymous}">Anonymous</props:option>
          <props:option value="${authLoginPassword}">Username / Password</props:option>
        </props:selectProperty>
        <span id="error_${authType}" class="error"></span>
      </td>
    </tr>
    <tr class="js_authsetting ${authLoginPassword}">
      <th><label for="${username}" class="shortLabel">Username:<l:star/></label></th>
      <td>
        <props:textProperty name="${username}" maxlength="100"/>
        <span id="error_${username}" class="error"></span>
      </td>
    </tr>
    <tr class="js_authsetting ${authLoginPassword}">
      <th><label for="${password}" class="shortLabel">Password:<l:star/></label></th>
      <td>
        <props:passwordProperty name="${password}" maxlength="100"/>
        <span id="error_${password}" class="error"></span>
      </td>
    </tr>

    <tr>
      <th><label for="${pattern}" class="shortLabel">Issue ID Pattern:<l:star/></label></th>
      <td>
        <props:textProperty name="${pattern}" maxlength="100"/>
        <span class="fieldExplanation">Use the regex syntax, e.g. #(\d+)<bs:help file="Integrating+TeamCity+with+Issue+Tracker"/></span>
        <span id="error_${pattern}" class="error"></span>
      </td>
    </tr>
  </table>
</div>

<script type="text/javascript">
  BS.BitbucketIssues.init('${authType}_select');
  $j(document).ready(function() {
    if (BS.Repositories != null) {
      BS.Repositories.installControls($('repository'), function(repoInfo, cre) {
        console.log(repoInfo);
        $('${name}').value = repoInfo.owner + "/" + repoInfo.name;
        $('${repository}').value = repoInfo.htmlUrl;
        if (repoInfo.isPrivate) {
          $('${authType}_select').value = "${authLoginPassword}";
          BS.Util.show('privateNote');
        } else {
          $('${authType}_select').value = "${authAnonymous}";
          BS.Util.hide('privateNote');
        }
        BS.BitbucketIssues.selectAuthType();
      });
    }
    // if we have received some init values
    // var params = window.location.search.toQueryParams();
    var params = BS.IssueProviderForm.initOptions;
    console.log(params);
    if (params && params['addTracker']) {
      $('${name}').value = decodeURIComponent(params['suggestedName']);
      $('${repository}').value = decodeURIComponent(params['repoUrl']);
    }
  });
</script>
