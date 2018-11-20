<#import "template.html" as t>

    <@t.page>

        <div id="sidebar" class="sidebar">
            <ul class="nav nav-stacked flex-md-column pt-3">
                <li class="nav-header">${labels["standard.objects"]}</li>
                <li class="nav-item">
                    <a class="nav-link font-weight-normal grey-text" href="#">
                        ${labels["account"]}
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link font-weight-normal grey-text" href="#">
                        ${labels["case"]}
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link font-weight-normal grey-text" href="#">
                        ${labels["contact"]}
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link font-weight-normal grey-text" href="#">
                        ${labels["lead"]}
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link font-weight-normal grey-text" href="#">
                        ${labels["opportunity"]}
                    </a>
                </li>
            </ul>
        </div>

        <div id="content" class="content">
            <div class="container-fluid p-3">
                <div class="dashhead mb-3">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${labels["salesforce"]}</h6>
                        <h3 class="dashhead-title">${labels['event.listeners']}</h3>
                    </div>
                </div>
                <table class="table">
                    <thead>
                        <tr class="col-12">
                            <th class="col-3">${labels['id']}</th>
                            <th class="col-3">${labels['name']}</th>
                            <th class="col-3">${labels['enabled']}</th>
                            <th class="col-3">${labels['last.event.received.on']}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#if organization.eventListeners?size==0>
                            <tr>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                            <#else>
                                <#list organization.eventListeners as listener>
                                    <tr>
                                        <th scope="row">${listener.id}</th>
                                        <td>${listener.name}</td>
                                        <td>
                                            <#if listener.enabled>
                                                <i class="fa fa-check mr-2 green-text" aria-hidden="true"></i>
                                                <#else>
                                                    <i class="fa fa-close mr-2 red-text" aria-hidden="true"></i>
                                            </#if>
                                        </td>
                                        <td>
                                            <#if listener.lastEventReceivedOn??>
                                                ${listener.lastEventReceivedOn?date?string.long - listener.lastEventReceivedOn?time?string.medium}
                                                <#else>
                                                    &nbsp;
                                            </#if>
                                        </td>
                                    </tr>
                                </#list>
                        </#if>
                    </tbody>
                </table>
            </div>
        </div>
    </@t.page>