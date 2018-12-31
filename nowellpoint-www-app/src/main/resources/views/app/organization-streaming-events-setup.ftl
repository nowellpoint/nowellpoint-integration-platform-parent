<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <content id="content">
            <div class="container-fluid mt-2 pt-3 pr-3 pl-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h4 class="dashhead-title font-weight-normal">${labels["setup.event.listener"]}</h4>
                    </div>
                </div>
            </div>
            <hr>
            <div class="container-fluid p-3">
                <form id="streaming-event-listener-form" role="form" method="post" action="${eventListener.href}">
                    <div class="flextable">
                        <div class="flextable-item flextable-primary">
                            <h5>${eventListener.source}</h5>
                        </div>
                        <div class="flextable-item">
                            <#if eventListener.topicId??>
                                <span>${labels["last.updated.by"]}:</span>&nbsp;
                                <span class="text-primary">${eventListener.lastUpdatedBy.name}</span>&emsp;
                                <span>${labels["last.updated.on"]}:</span>&nbsp;
                                <span class="text-primary">${eventListener.lastUpdatedOn?date?string.long} ${eventListener.lastUpdatedOn?time?string.medium}</span>&emsp;&emsp;&emsp;
                            </#if>   
                            <#if eventListener.isActive()>
                                <input type="checkbox" id="active" name="active" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['active']}" data-off="${labels['inactive']}" checked>
                            <#else>
                                <input type="checkbox" id="active" name="active" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['active']}" data-off="${labels['inactive']}">
                            </#if>
                        </div>
                    </div>
                    <hr>
                    <div class="form-check">
                        <#if eventListener.notifyForOperationCreate>
                            <input type="checkbox" id="notifyForOperationCreate" name="notifyForOperationCreate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" checked>
                        <#else>
                            <input type="checkbox" id="notifyForOperationCreate" name="notifyForOperationCreate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}">
                        </#if>&emsp;
                        <label for="notifyForOperationCreate">${labels["notify.on.create"]}</label>
                    </div>
                    <br>
                    <div class="form-check">
                        <#if eventListener.notifyForOperationUpdate>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationUpdate" name="notifyForOperationUpdate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" checked>
                        <#else>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationUpdate" name="notifyForOperationUpdate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}">
                        </#if>&emsp;
                        <label class="form-check-label" for="notifyForOperationUpdate">${labels["notify.on.update"]}</label>
                    </div>
                    <br>
                    <div class="form-check">
                        <#if eventListener.notifyForOperationDelete>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationDelete" name="notifyForOperationDelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" checked>
                        <#else>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationDelete" name="notifyForOperationDelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}">
                        </#if>&emsp;
                        <label class="form-check-label" for="notifyForOperationDelete">${labels["notify.on.delete"]}</label>
                    </div>
                    <br>
                    <div class="form-check">
                        <#if eventListener.notifyForOperationUndelete>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationUndelete" name="notifyForOperationUndelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" checked>
                        <#else>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationUndelete" name="notifyForOperationUndelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}">
                        </#if>&emsp;
                        <label class="form-check-label" for="notifyForOperationUndelete">${labels["notify.on.undelete"]}</label>
                    </div>
                </form>
                <hr>     
                <div>
                    <div class="text-right">
                        <a class="btn btn-secondary" role="button" href="${ORGANIZATION_STREAMING_EVENTS_URI}">${messages['cancel']}</a>&nbsp;
                        <button type="button" id="save-streaming-event-listener" name="save-streaming-event-listener" class="btn btn-primary">${messages['save']}</button>
                    </div>
                </div>
            </div>
        </content>            
        <script type="text/javascript" src="/js/organization.js"></script>
    </@t.page>