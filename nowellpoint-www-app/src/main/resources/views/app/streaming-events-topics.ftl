<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid mt-2 pt-3 pr-1 pl-1">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb bg-transparent">
                        <li class="breadcrumb-item"><a href="${STREAMING_EVENTS_URI}">${messages["streaming.events"]}</a></li>
                        <li class="breadcrumb-item">${messages["topics"]}</li>
                    </ol>
                </nav>
            </div>
            <div class="container-fluid p-4">
                <table class="table">
                    <thead>
                        <tr class="d-flex">
                            <th class="col-2">${labels['source']}</th>
                            <th class="col-4">${labels['channel']}</th>
                            <th class="col-3">${labels['topic.id']}</th>
                            <th class="col-3 text-center">${labels['active']}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list organization.streamingEventListeners?sort_by( "source") as eventListener>
                            <tr class="d-flex">
                                <td class="col-2">
                                    <a href="${eventListener.href}">${eventListener.source}</a>
                                </td>
                                <td class="col-4">
                                    ${eventListener.channel}
                                </td>
                                <td class="col-3">
                                    ${eventListener.topicId!''}    
                                </td>
                                <td class="col-3 text-center">
                                    <h5>${eventListener.active?then("<span class='badge badge-success'>${labels['active']}</span>","<span class='badge badge-danger'>${labels['inactive']}</span>")}</h5>
                                </td>
                            </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
            <!--
<span class="fa-stack fa-lg" style="color:#00cc6a">
                                        <i class="fas fa-circle fa-stack-2x"></i>
                                        <i class="fas fa-inverse fa-stack-1x"></i>
                                    </span>-->
    </@t.page>