<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid pt-3 pr-2 pl-2">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${labels['salesforce']}</h6>
                        <h3 class="dashhead-title">${messages["event.streams"]}</h3>
                    </div>
                    <div class="dashhead-toolbar">
                        <div class="dashhead-toolbar-item">
                            <a class="btn btn-secondary" href="${EVENT_STREAMS_SETUP_URI}" role="button">${labels["create.event.stream"]}</a>
                        </div>    
                    </div>
                </div>
                <hr>
                
                
                <!--
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb bg-transparent">
                        <li class="breadcrumb-item"><a href="${EVENT_STREAMS_URI}">${messages["event.streams"]}</a></li>
                        <li class="breadcrumb-item">${messages["topics"]}</li>
                    </ol>
                </nav>
            
                
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

               <!-- <div class="accordian pl-3 pr-3" id="event-listener-list"> 
                <div class="row">
                    <#list organization.streamingEventListeners?sort_by( "source") as eventListener>
                        <div class="card mb-3">
                            <div class="card-header" id="heading-${eventListener.source}">
                                <div class="row">
                                    <div class="col-6">
                                        <button class="btn btn-link" type="button" data-toggle="collapse" data-target="#collapse-${eventListener.source}" aria-expanded="true" aria-controls="collapse-${eventListener.source}"><i class="fas fa-angle-right fa-lg"></i></button>&nbsp;<a href="${eventListener.href}">${eventListener.source}</a> 
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
               <!-- </div> -->
                <!--
<span class="fa-stack fa-lg" style="color:#00cc6a">
                                        <i class="fas fa-circle fa-stack-2x"></i>
                                        <i class="fas fa-inverse fa-stack-1x"></i>
                                    </span>-->


            </div>
        </div>
    </@t.page>