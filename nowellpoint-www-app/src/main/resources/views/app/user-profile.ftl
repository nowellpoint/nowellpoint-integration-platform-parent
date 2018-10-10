<#import "template.html" as t>
    <@t.page>
        <div class="container-fluid m-3">
            <div class="card border-light">
                <div class="card-header pt-3 unique-color">
                    <div class="row">
                        <div class="col-6">
                            <h4 class="text-white font-weight-bold">${labels["user.information"]}</h4>
                        </div>
                        <div class="col-6 text-right">
                            &nbsp;
                        </div>
                    </div>
                </div>
                <div id="user-information" class="card-body">
                    <#include "user-profile-information.ftl" />
                </div>
            </div>
        </div>
    </@t.page>