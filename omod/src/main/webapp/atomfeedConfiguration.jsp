<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/atomfeed/styles/atomfeed.css"/>
<openmrs:require anyPrivilege="Atomfeed module Privilege" otherwise="/login.htm" redirect="/module/atomfeed/configuration.form"/>
<openmrs:htmlInclude file="/moduleResources/atomfeed/scripts/atomfeed.js"/>
<spring:htmlEscape defaultHtmlEscape="true"/>

<h2>
    <spring:message code="atomfeed.configuration.label" />
</h2>
<%@ include file="template/alertMessage.jsp" %>

<fieldset>
    <form action="importFeedConfiguration.form" method="POST" enctype="multipart/form-data">
        <p>
            <label for="json-file">
                <span>
                    <spring:message code="atomfeed.configuration.import.file.label"/>
                </span>
            </label>
            <br/><br/>
            <input id="json-file" type="file" name="file" accept=".json"/>
        </p>
        <input type="submit" id="import-button"
            value="<spring:message code='atomfeed.configuration.import.label'/>" disabled="disabled"/>
    </form>
</fieldset>
<br/>
<fieldset>
    <form method="POST" action="${pageContext.request.contextPath}/module/atomfeed/saveConfiguration.form">
        <span id="errorMsg" class="field-error" style="display: none">
            <spring:message code="atomfeed.configuration.errors.invalidJson"/>
        </span>
        <span id="server-error-msg" class="field-error" style="display: none">
            <spring:message code="atomfeed.configuration.errors.serverError"/>
        </span>
        <p>
            <label for="json-field">
                <span class="title">
                    <spring:message code="atomfeed.configuration.json.label"/>
                    (<spring:message code="emr.formValidation.messages.requiredField.label"/>)
                </span>
            </label>
            <br/>
            <textarea id="json-field" class="required" name="json" rows="15" cols="80">${configuration}</textarea>
        </p>
        <input type="button" value="<spring:message code='general.cancel'/>"
            onclick="javascript:window.location='${pageContext.request.contextPath}/module/atomfeed/atomfeed.form'" />
        <input type="submit" id="save-button" value="<spring:message code='general.save'/>" disabled="disabled"/>
    </form>
</fieldset>
