<#import "template.html" as t>

    <@t.page>

        <div id="sidebar" class="sidebar">
            <a class="nav-link font-weight-normal grey-text" href="#">
                ${labels["account"]}
            </a>
            <a class="nav-link font-weight-normal grey-text" href="#">
                ${labels["case"]}
            </a>
            <a class="nav-link font-weight-normal grey-text" href="#">
                ${labels["contact"]}
            </a>
            <a class="nav-link font-weight-normal grey-text" href="#">
                ${labels["lead"]}
            </a>
            <a class="nav-link font-weight-normal grey-text" href="#">
                ${labels["opportunity"]}
            </a>
        </div>

        <div class="panel panel-default pt-3 pl-3 pb-2 mb-3 border-bottom">
            <span class="clearfix d-none d-sm-inline-block">
                <h5>${labels['event.listeners']}</h5>
            </span>
        </div>

        <div class="content">
            <table class="table w-100 d-block">
                <thead>
                    <tr>
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
    </@t.page>