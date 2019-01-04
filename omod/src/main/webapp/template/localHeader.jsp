<spring:htmlEscape defaultHtmlEscape="true"/>
<ul id="menu">
    <li class="first">
        <a href="${pageContext.request.contextPath}/admin">
            <spring:message code="admin.title.short"/>
        </a>
    </li>
    <li <c:if test='<%= request.getRequestURI().contains("/atomfeed/atomfeed.") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/atomfeed/atomfeed.form">
            <spring:message code="atomfeed.title"/>
        </a>
    </li>
    <li <c:if test='<%= request.getRequestURI().contains("/atomfeedConfiguration") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/atomfeed/configuration.form">
            <spring:message code="atomfeed.configuration.label"/>
        </a>
    </li>
</ul>
