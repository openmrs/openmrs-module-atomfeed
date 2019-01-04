<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/atomfeed/styles/atomfeed.css"/>
<openmrs:require anyPrivilege="Atomfeed module Privilege" otherwise="/login.htm" redirect="/module/atomfeed/atomfeed.form"/>

<h2>
    <spring:message code="atomfeed.title" />
</h2>
<br/><br/>
<%@ include file="template/alertMessage.jsp" %>

<div id="apps">
    <a class="button"
        href="${pageContext.request.contextPath}/module/atomfeed/configuration.form">
        <spring:message code="atomfeed.configuration.label"/>
    </a>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
