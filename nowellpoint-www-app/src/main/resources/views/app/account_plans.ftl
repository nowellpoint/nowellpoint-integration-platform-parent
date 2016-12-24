<#import "template.html" as t>
    <@t.page>
        <div>
            <a href="${labels['base.path']}/${id}#environments" id="back" class="btn btn-sm btn-primary-outline" role="button"><span class="icon icon-back fa fa-1x"></span></a>&emsp;${labels["back.to.salesforce.connector"]}
        </div>
        <div class="flextable p-t">
            <div class="flextable-item flextable-primary">
                <div class="dashhead">
                <div class="dashhead-titles">
                    <h3 class="dashhead-title">${labels["sobjects"]}</h3>
                </div>
                </div>    
            </div>
        </div>
        <div class="hr-divider"></div>
        <br>
        <div class="flexgrid">
            <#list environment.sobjects?sort_by( "label") as sobject>
                <div class="offer offer-radius offer-info">
                    <div class="offer-content">
                        <h3 class="lead">
							<#if icons[ "${sobject.name}"]??>
                                    <img src="${icons['${sobject.name}']}" />
                                </#if>
                                <#if ! icons[ "${sobject.name}"]??>
                                    <img src="/images/salesforce-logo.png" style="height: 32px; width: 32px" />
                                </#if><a href="${labels['base.path']}/${id}/environments/${environment.key}/sobject/${sobject.name}">${(sobject.label)!}</a> 
						</h3>
                        <p>${(sobject.name)!}</p>
                        <div class="text-right">

                        </div>
                    </div>
                </div>
            </#list>
        </div>
    </@t.page>