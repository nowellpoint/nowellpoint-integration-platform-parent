<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid pt-3 pr-5 pl-5 mb-3">
                <div class="dashhead mb-2">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${labels['salesforce']}</h6>
                        <h3 class="dashhead-title">${messages["event.streams"]}</h3>
                    </div>
                    <div class="dashhead-toolbar">
                        <div class="dashhead-toolbar-item">
                        </div>
                    </div>
                </div>
                <!--<div class="p-3 mb-2 unique-color text-light rounded"><span class="h6">${labels['standard.objects']}</span></div>-->
                <div class="card w-100">
                    <div class="card-header unique-color">
                        <span class="h5 text-white">${labels['standard.objects']}</span>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-2">
                                ${labels['source']}
                            </div>
                            <div class="col-3">
                                ${labels['topic.id']}
                            </div>
                            <div class="col-1">
                                ${labels['status']}
                            </div>
                            <div class="col-3">
                                ${labels['started.on']}
                            </div>
                            <div class="col-3 text-right">
                                ${labels['action']}
                            </div>
                        </div>
                        <#if organization.streamingEventListeners?size == 0>
                            <hr>
                        </#if>
                        <#list organization.streamingEventListeners?sort_by( "source") as eventListener>
                            <#if ! eventListener.custom>
                                <hr>
                                <div class="row align-items-center">
                                    <div class="col-2">
                                        <a href="${eventListener.href}">${eventListener.source}</a>
                                    </div>
                                    <div class="col-3">
                                        <span class="text-muted">${eventListener.topicId!''}</span>
                                    </div>
                                    <div class="col-1">
                                        <span class="text-muted">${eventListener.active?then("Started","")}</span>
                                    </div>
                                    <div class="col-3">
                                        <span class="text-muted">${eventListener.active?then(eventListener.startedOn?date?string.iso,"")}&nbsp;
                                            ${eventListener.active?then(eventListener.startedOn?time?string.iso,"")}</span>
                                    </div>
                                    <div class="col-3 text-right">
                                        ${eventListener.active?then("
                                        <button id='action-button' name='action-button' class='btn btn-sm btn-danger' data-href='${EVENT_STREAMS_URI}${eventListener.source}/stop/' type='button'><i class='fas fa-stop-circle'></i>&nbsp;${labels['stop']}</button>", "
                                        <button id='action-button' name='action-button' class='btn btn-sm btn-success' data-href='${EVENT_STREAMS_URI}${eventListener.source}/start/' type='button'><i class='fas fa-play-circle'></i>&nbsp;${labels['start']}</button>")}
                                    </div>
                                </div>
                            </#if>
                        </#list>
                    </div>
                </div>


                <!--
<span class="fa-stack fa-lg" style="color:#00cc6a">
                                        <i class="fas fa-circle fa-stack-2x"></i>
                                        <i class="fas fa-inverse fa-stack-1x"></i>
                                    </span>-->


            </div>
        </div>
        <#include "success-popup.ftl" />
        <script type="text/javascript" src="/js/event-stream.js"></script>
    </@t.page>