<#import "template.html" as t>
    <@t.page>
        <div class="container-fluid p-3">
            <div class="dashhead">
                <div class="dashhead-titles">
                    <h6 class="dashhead-subtitle">${labels["user.profile"]}</h6>
                    <h3 class="dashhead-title">${labels["details"]}</h3>
                </div>
            </div>
            <#include "user-profile-information.ftl" />
            <!--
            <div class="card border-light">
                <div class="card-header pt-3 unique-color">
                    <h4 class="text-white font-weight-bold">${labels["user.profile"]}</h4>
                </div>
                <div id="user-information" class="card-body">
                    <#include "user-profile-information.ftl" />
                </div>
            </div>
-->
        </div>
    </@t.page>