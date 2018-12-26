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
                <form id="event-listener-setup-form" role="form" method="post" action="${eventListener.href}">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="notifyForOperationCreate" name="notifyOn" value="create">
                        <label class="form-check-label" for="notifyForOperationCreate">${labels["notify.on.create"]}</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="notifyForOperationUpdate" name="notifyOn" value="update">
                        <label class="form-check-label" for="notifyForOperationUpdate">${labels["notify.on.update"]}</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="notifyForOperationDelete" name="notifyOn" value="delete">
                        <label class="form-check-label" for="notifyForOperationDelete">${labels["notify.on.delete"]}</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="notifyForOperationUndelete" name="notifyOn" value="undelete">
                        <label class="form-check-label" for="notifyForOperationUndelete">${labels["notify.on.undelete"]}</label>
                    </div>
                    <hr>
                    <div>
                        <div class="text-right">
                            <a href="${ORGANIZATION_STREAMING_EVENTS_URI}">${messages['cancel']}</a>&emsp;
                            <button id="save-event-listener" class="btn btn-primary">${messages['save']}</button>
                        </div>
                    </div>
                </form>
            </div>
        </content>
    </@t.page>