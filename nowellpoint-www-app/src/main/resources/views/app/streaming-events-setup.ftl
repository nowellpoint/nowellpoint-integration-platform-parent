<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid mt-2 pt-3 pr-1 pl-1">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb bg-transparent">
                        <li class="breadcrumb-item"><a href="${STREAMING_EVENTS_URI}">${messages["streaming.events"]}</a></li>
                        <li class="breadcrumb-item"><a href="${STREAMING_EVENTS_SOURCES_URI}">${labels["source"]}</a></li>
                        <li class="breadcrumb-item active" aria-current="page">${labels["setup"]}</li>
                    </ol>
                </nav>
            </div>
            <div class="container-fluid p-4">
                <div class="flextable">
                    <div class="flextable-item flextable-primary">
                        <h2>${eventListener.source}</h2>
                    </div>
                    <div class="flextable-item">

                    </div>
                </div>

                <ul class="nav nav-bordered">
                    <li class="nav-item">
                        <a class="nav-link active" href="#configuration" data-toggle="tab">${labels["configuration"]}</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#monitoring" data-toggle="tab">${labels["monitoring"]}</a>
                    </li>
                </ul>
                <hr>
                <div class="tab-content clearfix">
                    <div class="tab-pane active" id="configuration">
                        <form id="streaming-event-listener-form" role="form" method="post" action="${eventListener.href}">
                            <div class="card bg-light">
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
                            <div class="card bg-light">
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
                                <div class="card bg-light">
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
                                <a class="btn btn-secondary" role="button" href="${STREAMING_EVENTS_URI}">${messages['cancel']}</a>&nbsp;
                                <button type="button" id="save-streaming-event-listener" name="save-streaming-event-listener" class="btn btn-primary">${messages['save']}</button>
                            </div>
                        </div>
                    </div>

                    <div class="tab-pane" id="monitoring">
                        <div class="row">
                            <div class="col-3">
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <span>${labels['events.received.today']}</span>
                                        <h1 class="display-3">${EVENTS_RECEIVED_TODAY}</h1>
                                        <span class="text-primary">${TODAY} (${labels['utc']})</span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <span>${labels['events.received.this.week']}</span>
                                        <h1 class="display-3">${EVENTS_RECEIVED_THIS_WEEK}</h1>
                                        <span class="text-primary">${FIRST_DAY_OF_WEEK} (${labels['utc']})</span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <span>${labels['events.received.this.month']}</span>
                                        <h1 class="display-3">${EVENTS_RECEIVED_THIS_MONTH}</h1>
                                        <span class="text-primary">${FIRST_DAY_OF_MONTH} (${labels['utc']})</span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <span>${labels['events.received.this.year']}</span>
                                        <h1 class="display-3">${EVENTS_RECEIVED_THIS_YEAR}</h1>
                                        <span class="text-primary">${FIRST_DAY_OF_YEAR} (${labels['utc']})</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-4 pb-3">
                                <div class="activity-feed">
                                    <#list feedItems as feedItem>
                                        <div class="feed-item">
                                            <div class="date">${feedItem.lastUpdatedOn?date?string.long}&nbsp;&nbsp;${feedItem.lastUpdatedOn?time?string.medium}</div>
                                            <div class="text">${feedItem.body}</div>
                                        </div>
                                    </#list>
                                </div>    
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div> <!-- content -->
        <script type="text/javascript" src="/js/organization.js"></script>
    </@t.page>