<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid pt-3 pr-3 pl-3 mb-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${messages["event.streams"]}</h6>
                        <h3 class="dashhead-title">${eventListener.source}</h3>
                    </div>
                    <div class="dashhead-toolbar">
                        <div class="dashhead-toolbar-item">

                        </div>
                    </div>
                </div>
                <hr>
                <ul class="nav nav-bordered">
                    <li class="nav-item">
                        <a class="nav-link active" href="#monitoring" data-toggle="tab">${labels["monitoring"]}</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#configuration" data-toggle="tab">${labels["configuration"]}</a>
                    </li>
                </ul>
                <hr>
                <div class="tab-content clearfix">
                    <div class="tab-pane" id="configuration">
                        <form id="streaming-event-listener-form" role="form" method="post" action="${eventListener.href}">
                            <div class="card bg-light">
                                <div class="card-body">
                                    <div class="row align-items-center">
                                        <div class="col-6">
                                            <label for="notifyForOperationCreate">${labels["streaming.event.listener.status"]}</label>
                                        </div>
                                        <div class="col-6 text-right">
                                            <input type="checkbox" id="active" name="active" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['started']}" data-off="${labels['stopped']}" data-width="100" ${eventListener.active?then( 'checked', '')}>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <div class="card bg-light">
                                <div class="card-body">
                                    <div class="row align-items-center">
                                        <div class="col-6">
                                            <label for="notifyForOperationCreate">${labels["notify.on.create"]}</label>
                                        </div>
                                        <div class="col-6 text-right">
                                            <input type="checkbox" id="notifyForOperationCreate" name="notifyForOperationCreate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" data-width="100" ${eventListener.notifyForOperationCreate?then( 'checked', '')}>
                                        </div>
                                    </div>
                                    <hr>
                                    <div class="row align-items-center">
                                        <div class="col-6">
                                            <label for="notifyForOperationUpdate">${labels["notify.on.update"]}</label>
                                        </div>
                                        <div class="col-6 text-right">
                                            <input type="checkbox" id="notifyForOperationUpdate" name="notifyForOperationUpdate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" data-width="100" ${eventListener.notifyForOperationUpdate?then( 'checked', '')}>
                                        </div>
                                    </div>
                                    <hr>
                                    <div class="row align-items-center">
                                        <div class="col-6">
                                            <label for="notifyForOperationDelete">${labels["notify.on.delete"]}</label>
                                        </div>
                                        <div class="col-6 text-right">
                                            <input type="checkbox" id="notifyForOperationDelete" name="notifyForOperationDelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" data-width="100" ${eventListener.notifyForOperationDelete?then( 'checked', '')}>
                                        </div>
                                    </div>
                                    <hr>
                                    <div class="row align-items-center">
                                        <div class="col-6">
                                            <label for="notifyForOperationUndelete">${labels["notify.on.undelete"]}</label>
                                        </div>
                                        <div class="col-6 text-right">
                                            <input type="checkbox" id="notifyForOperationUndelete" name="notifyForOperationUndelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" data-width="100" ${eventListener.notifyForOperationUndelete?then( 'checked', '')}>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <#if eventListener.topicId??>
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <div class="row">
                                            <div class="col-5">
                                                <span>${labels["created"]}:</span>
                                                <span class="text-primary">${eventListener.createdBy.name}</span>
                                                <span>,&nbsp;${eventListener.createdOn?date?string.long} ${eventListener.createdOn?time?string.medium}</span>
                                            </div>
                                            <div class="col-5">
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
                                <a class="btn btn-secondary" role="button" href="${EVENT_STREAMS_URI}">${messages['cancel']}</a>&nbsp;
                                <button type="button" id="save-streaming-event-listener" name="save-streaming-event-listener" class="btn btn-primary">${messages['save']}</button>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane active" id="monitoring">
                        <div class="flextable">
                            <div class="flextable-item flextable-primary">

                            </div>
                            <div class="flextable-item">
                                ${labels['timezone']}:
                                <div class="btn-group">
                                    <a class="btn btn-outline-primary ${viewAsUtc?then('active','')}" href="${VIEW_AS_UTC_HREF}" role="button">${UTC}</a>
                                    <a class="btn btn-outline-primary ${viewAsDefaultTimeZone?then('active','')}" href="${VIEW_AS_DEFAULT_TIMEZONE_HREF}" role="button">${DEFAULT_TIME_ZONE}</a>
                                </div>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-3">
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <span>${labels['events.received.today']}</span>
                                        <h2>${eventStreamMonitor.countToday?string(",##0")}</h2>
                                        <span class="text-muted">${TODAY}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <span>${labels['events.received.this.week']}</span>
                                        <h2>${eventStreamMonitor.countThisWeek?string(",##0")}</h2>
                                        <span class="text-muted">${FIRST_DAY_OF_WEEK}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <span>${labels['events.received.this.month']}</span>
                                        <h2>${eventStreamMonitor.countThisMonth?string(",##0")}</h2>
                                        <span class="text-muted">${FIRST_DAY_OF_MONTH}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <span>${labels['events.received.this.year']}</span>
                                        <h2>${eventStreamMonitor.countThisYear?string(",##0")}</h2>
                                        <span class="text-muted">${FIRST_DAY_OF_YEAR}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-12">
                                <h4>${labels['streaming.events.feed']}</h4>
                            </div>
                            <div class="col-4 pb-3 border-right">
                                <div class="activity-feed">
                                    <#list feedItems1 as feedItem>
                                        <div class="feed-item">
                                            <div class="date">${feedItem.lastUpdatedOn?date?string.long}&nbsp;&nbsp;${feedItem.lastUpdatedOn?time?string.medium}</div>
                                            <div class="text">${feedItem.body}</div>
                                        </div>
                                    </#list>
                                </div>
                            </div>
                            <div class="col-4 pb-3 border-right">
                                <div class="activity-feed">
                                    <#list feedItems2 as feedItem>
                                        <div class="feed-item">
                                            <div class="date">${feedItem.lastUpdatedOn?date?string.long}&nbsp;&nbsp;${feedItem.lastUpdatedOn?time?string.medium}</div>
                                            <div class="text">${feedItem.body}</div>
                                        </div>
                                    </#list>
                                </div>
                            </div>
                            <div class="col-4 pb-3">
                                <div class="activity-feed">
                                    <#list feedItems3 as feedItem>
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
        </div>
        <script type="text/javascript" src="/js/event-stream.js"></script>
    </@t.page>