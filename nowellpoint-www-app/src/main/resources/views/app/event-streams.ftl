<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid pt-3 pr-3 pl-3 mb-3">
                <div class="dashhead mb-2">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${labels['salesforce']}</h6>
                        <h3 class="dashhead-title">${messages["event.streams"]}</h3>
                    </div>
                    <div class="dashhead-toolbar">
                        <div class="dashhead-toolbar-item"></div>
                    </div>
                </div>
                <hr>
                <div class="row">
                    <div class="col-3">
                        ${labels['standard.objects']}
                    </div>
                    <div class="col-3">
                        ${labels['topic.id']}
                    </div>
                    <div class="col-3">
                        ${labels['last.updated']}
                    </div>
                    <div class="col-3 text-center">
                        ${labels['status']}
                    </div>
                </div>
                <#if organization.eventStreamListeners?size==0>
                    <hr>
                </#if>
                <#list organization.eventStreamListeners?sort_by( "source") as eventListener>
                    <#if ! eventListener.custom>
                        <hr>
                        <div class="row align-items-center">
                            <div class="col-3">
                                <a href="${eventListener.href}">${eventListener.source}</a>
                            </div>
                            <div class="col-3">
                                <span class="text-muted">${eventListener.topicId!''}</span>
                            </div>
                            <div class="col-3">
                                <span class="text-muted">
                                    <#if eventListener.active>
                                        ${(eventListener.startedOn?date?string.medium)!}&nbsp;${(eventListener.startedOn?time?string.medium)!}</span>
                                    <#else>
                                        ${(eventListener.stoppedOn?date?string.medium)!}&nbsp;${(eventListener.stoppedOn?time?string.medium)!}
                                    </#if>
                            </div>
                            <div class="col-3 text-center">
                                <#if ! eventListener.topicId??>
                                    <button type="button" class="btn btn-primary dropdown-toggle w-75" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${labels["not.started"]}</button>
                                    <div class="dropdown-menu dropdown-menu-right">
                                        <button type="button" class="btn btn-link dropdown-item" id="action" data-href="${EVENT_STREAMS_URI}${eventListener.source}/start/">${labels["create.topic"]}</button>
                                    </div>
                                <#else>    
                                    <#if eventListener.active>
                                        <button type="button" class="btn btn-success dropdown-toggle w-75" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${labels["subscribed"]}</button>
                                        <div class="dropdown-menu dropdown-menu-right">
                                            <button type="button" class="btn btn-link dropdown-item" id="action" data-href="${EVENT_STREAMS_URI}${eventListener.source}/stop/">${labels["stop.listener"]}</button>
                                        </div>
                                    <#else>
                                        <button type="button" class="btn btn-danger dropdown-toggle w-75" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${labels["unsubscribed"]}</button>
                                        <div class="dropdown-menu dropdown-menu-right">
                                            <button type="button" class="btn btn-link dropdown-item" id="action" data-href="${EVENT_STREAMS_URI}${eventListener.source}/start/">${labels["start.listener"]}</button>
                                        </div>
                                    </#if>
                                </#if>
                            </div>
                        </div>
                    </#if>
                </#list>
            </div>
        </div>
        <#include "success-popup.ftl" />
        <script type="text/javascript" src="/js/event-stream.js"></script>
    </@t.page>