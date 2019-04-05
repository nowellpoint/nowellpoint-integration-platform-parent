<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid pt-3 pr-2 pl-2">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle"></h6>
                        <h3 class="dashhead-title">${messages["notifications"]}</h3>
                    </div>
                    <div class="dashhead-toolbar">
                    </div>    
                </div>
            </div>
            <div class="container-fluid pb-2 pr-3 pl-3">
                <#include "notifications-list.ftl" />
            </div>
        </div>    
    </@t.page>