<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid mt-2 pt-3 pr-3 pl-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h4 class="dashhead-title font-weight-normal">${messages["notifications"]}</h4>
                    </div>
                </div>
                <#include "notifications-list.ftl" />
            </div>
        </div>    
    </@t.page>