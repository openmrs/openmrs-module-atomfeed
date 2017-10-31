<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("atomfeed.configuration.label") ])
    if (context.hasPrivilege("Load Atomfeed config")) {
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("Atomfeed")}" }
    ];
</script>

<div id="apps">
    <a class="button app big" href="${ ui.pageLink("atomfeed", "loadFeedConf") }"
       id="atomfeed.configuration">
        <i class="icon-calendar"></i>
        ${ ui.message("atomfeed.configuration.label") }
    </a>
</div>

<% } %>