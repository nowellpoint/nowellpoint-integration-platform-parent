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
                            <input type="checkbox" id="active" name="active" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['active']}" data-off="${labels['inactive']}" ${eventListener.active?then('checked','')}>
                        </div>
                    </div>
                    <hr>    
                    <div class="card">
                        <div class="card-body">
                            <div class="col-6">
                                <span class="align-middle">
                                <label for="notifyForOperationCreate">${labels["notify.on.create"]}</label>
                                </span>    
                                <div class="pull-right">
                                    <input type="checkbox" id="notifyForOperationCreate" name="notifyForOperationCreate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" ${eventListener.notifyForOperationCreate?then('checked','')}>
                                </div>        
                            </div>
                            
                            <div class="col-6">
                                <hr>
                            </div>
                            
                            <div class="col-6">
                                <label for="notifyForOperationUpdate">${labels["notify.on.update"]}</label>
                                <div class="pull-right">
                                    <input type="checkbox" id="notifyForOperationUpdate" name="notifyForOperationUpdate" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" ${eventListener.notifyForOperationUpdate?then('checked','')}>
                                </div>
                            </div>
                            
                            <div class="col-6">
                                <hr>
                            </div>
                            
                            <div class="col-6">
                                <label for="notifyForOperationDelete">${labels["notify.on.delete"]}</label>
                                <div class="pull-right">
                                    <input type="checkbox" id="notifyForOperationDelete" name="notifyForOperationDelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" ${eventListener.notifyForOperationDelete?then('checked','')}>
                                </div>  
                            </div>
                            
                            <div class="col-6">
                                <hr>
                            </div>
                            
                            <div class="col-6">
                                <label for="notifyForOperationUndelete">${labels["notify.on.undelete"]}</label>
                                <div class="pull-right">
                                    <input type="checkbox" id="notifyForOperationUndelete" name="notifyForOperationUndelete" value="true" data-toggle="toggle" data-onstyle="success" data-on="${labels['enabled']}" data-off="${labels['disabled']}" ${eventListener.notifyForOperationUndelete?then('checked','')}>
                                </div>    
                            </div> 
                        </div>        
                    </div>
                    
                    

                        
                        

                                <!--
                            <#if eventListener.topicId??>
                                <span>${labels["last.updated.by"]}:</span><br>
                                <span class="text-primary">${eventListener.lastUpdatedBy.name}</span>&emsp;
                                <br>
                                <span>${labels["last.updated.on"]}:</span><br>
                                <span class="text-primary">${eventListener.lastUpdatedOn?date?string.long} ${eventListener.lastUpdatedOn?time?string.medium}</span>
                             </#if> -->
  
                    
                
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