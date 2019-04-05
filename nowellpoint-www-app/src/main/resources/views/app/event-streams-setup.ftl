<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid pt-3 pr-2 pl-2">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${labels['salesforce']}</h6>
                        <h3 class="dashhead-title">${messages["event.streams"]}</h3>
                    </div>
                    <div class="dashhead-toolbar">
                        <div class="dashhead-toolbar-item">
                            
                        </div>    
                    </div>
                </div>
                <hr>
            </div>
        </div>    
        
        
        
        <!-- content -->
        <script type="text/javascript" src="/js/organization.js"></script>
    </@t.page>