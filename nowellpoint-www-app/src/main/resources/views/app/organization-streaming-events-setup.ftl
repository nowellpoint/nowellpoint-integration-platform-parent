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
                <h5>${eventListener.source}</h5>
                <form id="event-listener-setup-form" role="form" method="post" action="${eventListener.href}">
                    <div class="form-check">
                        <#if eventListener.notifyForOperationCreate>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationCreate" name="notifyOn" value="create" checked>
                        <#else>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationCreate" name="notifyOn" value="create" >
                        </#if>
                        <label class="form-check-label" for="notifyForOperationCreate">${labels["notify.on.create"]}</label>
                    </div>
                    <div class="form-check">
                        <#if eventListener.notifyForOperationUpdate>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationUpdate" name="notifyOn" value="update" checked>
                        <#else>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationUpdate" name="notifyOn" value="update">
                        </#if>
                        <label class="form-check-label" for="notifyForOperationUpdate">${labels["notify.on.update"]}</label>
                    </div>
                    <div class="form-check">
                        <#if eventListener.notifyForOperationDelete>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationDelete" name="notifyOn" value="delete" checked>
                        <#else>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationDelete" name="notifyOn" value="delete">
                        </#if>
                        <label class="form-check-label" for="notifyForOperationDelete">${labels["notify.on.delete"]}</label>
                    </div>
                    <div class="form-check">
                        <#if eventListener.notifyForOperationUndelete>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationUndelete" name="notifyOn" value="undelete" checked>
                        <#else>
                            <input class="form-check-input" type="checkbox" id="notifyForOperationUndelete" name="notifyOn" value="undelete">
                        </#if>
                        <label class="form-check-label" for="notifyForOperationUndelete">${labels["notify.on.undelete"]}</label>
                    </div>
                    <hr>
                    <div>
                        <div class="text-right">
                            <a href="${ORGANIZATION_STREAMING_EVENTS_URI}">${messages['cancel']}</a>&emsp;
                            <button id="save-streaming-event-listener" class="btn btn-primary">${messages['save']}</button>
                        </div>
                    </div>
                </form>
            </div>
        </content>
    </@t.page>