<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <content id="content">
            <div class="container-fluid mt-2 pt-3 pr-3 pl-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h4 class="dashhead-title font-weight-normal">${labels["organization"]}</h4>
                    </div>
                </div>
            </div>
            <hr>
            <div class="container-fluid p-3">
                <div class="card-columns">
                    <#include "organization-information.ftl" />
                    <#include "organization-subscription.ftl" />
                    <#include "organization-billing-address.ftl" />
                </div>
            </div>
        </content>
    </@t.page>