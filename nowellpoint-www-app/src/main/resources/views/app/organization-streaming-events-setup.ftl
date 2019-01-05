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

                        </div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-3">
                            <div class="card bg-light">
                                <div class="card-body">
                                    <span>${labels['events.received.today']}</span>
                                    <h1 class="display-3">${today}</h1>
                                </div>
                            </div>

                        </div>
                        <div class="col-3">
                            <div class="card bg-light">
                                <div class="card-body">
                                    <span>${labels['events.received.this.week']}</span>
                                    <h1 class="display-3">${thisWeek}</h1>
                                </div>
                            </div>
                        </div>
                        <div class="col-3">
                            <div class="card bg-light">
                                <div class="card-body">
                                    <span>${labels['events.received.this.month']}</span>
                                    <h1 class="display-3">${thisMonth}</h1>
                                </div>
                            </div>
                        </div>
                        <div class="col-3">
                            <div class="card bg-light">
                                <div class="card-body">
                                    <span>${labels['events.received.this.year']}</span>
                                    <h1 class="display-3">${thisYear}</h1>
                                </div>
                            </div>
                        </div>
                    </div>
                    <br>
                    <div class="card">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-6">
                                    <label for="notifyForOperationCreate">${labels["streaming.event.listener.status"]}</label>
                                    <div class="pull-right">
                                        <input type="checkbox" id="active" name="active" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['active']}" data-off="${labels['inactive']}" ${eventListener.active?then('checked','')}>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <br>
                    <div class="card">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-6">
                                    <span>
                                        <label for="notifyForOperationCreate">${labels["notify.on.create"]}</label>
                                    </span>
                                    <div class="pull-right">
                                        <input type="checkbox" id="notifyForOperationCreate" name="notifyForOperationCreate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" ${eventListener.notifyForOperationCreate?then('checked','')}>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-6">
                                    <hr>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-6">
                                    <label for="notifyForOperationUpdate">${labels["notify.on.update"]}</label>
                                    <div class="pull-right">
                                        <input type="checkbox" id="notifyForOperationUpdate" name="notifyForOperationUpdate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" ${eventListener.notifyForOperationUpdate?then('checked','')}>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-6">
                                    <hr>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-6">
                                    <label for="notifyForOperationDelete">${labels["notify.on.delete"]}</label>
                                    <div class="pull-right">
                                        <input type="checkbox" id="notifyForOperationDelete" name="notifyForOperationDelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" ${eventListener.notifyForOperationDelete?then('checked','')}>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-6">
                                    <hr>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-6">
                                    <label for="notifyForOperationUndelete">${labels["notify.on.undelete"]}</label>
                                    <div class="pull-right">
                                        <input type="checkbox" id="notifyForOperationUndelete" name="notifyForOperationUndelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" ${eventListener.notifyForOperationUndelete?then('checked','')}>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <br>

                    <#if eventListener.topicId??>
                        <div class="card">
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-6">
                                        <span>${labels["created"]}:</span>
                                        <span class="text-primary">${eventListener.createdBy.name}</span>
                                        <span>,&nbsp;${eventListener.createdOn?date?string.long} ${eventListener.createdOn?time?string.medium}</span>
                                    </div>
                                    <div class="col-6">
                                        <span>${labels["last.updated"]}:</span>
                                        <span class="text-primary">${eventListener.lastUpdatedBy.name}</span>
                                        <span>,&nbsp;${eventListener.lastUpdatedOn?date?string.long} ${eventListener.lastUpdatedOn?time?string.medium}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#if>
                </form>
                <br>
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