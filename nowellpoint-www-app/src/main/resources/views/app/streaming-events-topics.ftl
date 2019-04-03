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
            <div class="container-fluid">
                <!--
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
                -->

                <div class="accordian pl-3 pr-3" id="event-listener-list">
                    <#list organization.streamingEventListeners?sort_by( "source") as eventListener>
                        <div class="card mb-3">
                            <div class="card-header" id="heading-${eventListener.source}">
                                <div class="row">
                                    <div class="col-6">
                                        <button class="btn btn-link" type="button" data-toggle="collapse" data-target="#collapse-${eventListener.source}" aria-expanded="true" aria-controls="collapse-${eventListener.source}"><i class="fas fa-angle-right fa-lg"></i></button>&nbsp;${eventListener.source}    
                                    </div>
                                    <div class="col-6 text-right">
                                        <button class="btn btn-link" type="button">${eventListener.active?then("<i class='fas fa-circle-notch text-success fa-lg'></i>","<i class='fas fa-circle-notch text-danger fa-lg'></i>")}</button>
                                    </div>
                                </div>
                            </div>
                            <div id="collapse-${eventListener.source}" class="collapse" aria-labelledBy="heading-${eventListener.source}" data-parent="#event-listener-list">
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-6">
                                            <label for="notifyForOperationCreate">${labels["notify.on.create"]}</label>
                                        </div>
                                        <div class="col-6 text-right">
                                            <input type="checkbox" id="notifyForOperationCreate" name="notifyForOperationCreate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" data-width="100" ${eventListener.notifyForOperationCreate?then( 'checked', '')}>
                                        </div>
                                    </div>
                                    <hr>
                                    <div class="row">
                                        <div class="col-6">
                                            <label for="notifyForOperationUpdate">${labels["notify.on.update"]}</label>
                                        </div>
                                        <div class="col-6 text-right">
                                            <input type="checkbox" id="notifyForOperationUpdate" name="notifyForOperationUpdate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" data-width="100" ${eventListener.notifyForOperationUpdate?then( 'checked', '')}>
                                        </div>
                                    </div>
                                    <hr>
                                    <div class="row">
                                        <div class="col-6">
                                            <label for="notifyForOperationDelete">${labels["notify.on.delete"]}</label>
                                        </div>
                                        <div class="col-6 text-right">
                                            <input type="checkbox" id="notifyForOperationDelete" name="notifyForOperationDelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" data-width="100" ${eventListener.notifyForOperationDelete?then( 'checked', '')}>
                                        </div>
                                    </div>
                                    <hr>
                                    <div class="row">
                                        <div class="col-6">
                                            <label for="notifyForOperationUndelete">${labels["notify.on.undelete"]}</label>
                                        </div>
                                        <div class="col-6 text-right">
                                            <input type="checkbox" id="notifyForOperationUndelete" name="notifyForOperationUndelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" data-width="100" ${eventListener.notifyForOperationUndelete?then( 'checked', '')}>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#list>
                </div>
                <!--
<span class="fa-stack fa-lg" style="color:#00cc6a">
                                        <i class="fas fa-circle fa-stack-2x"></i>
                                        <i class="fas fa-inverse fa-stack-1x"></i>
                                    </span>-->


            </div>

    </@t.page>