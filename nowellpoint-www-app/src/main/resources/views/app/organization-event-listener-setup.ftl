<#import "template.html" as t>

    <@t.page>

        <#include "event-listener-menu.ftl" />

        <div id="content" class="content">
            <div class="container-fluid p-3">
                <div class="dashhead mb-3">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${labels["setup.event.listener"]}</h6>
                        <h3 class="dashhead-title">${SOBJECT}</h3>
                    </div>
                </div>
                <hr>
                <form id="event-listener-setup-form" role="form" method="post" action="${ACCOUNT_EVENT_LISTENER_SETUP}">
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
                            <a href="${ORGANIZATION_EVENT_LISTENERS_URI}">${messages['cancel']}</a>&emsp;
                            <button id="save-event-listener" class="btn btn-primary">${messages['save']}</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </@t.page>