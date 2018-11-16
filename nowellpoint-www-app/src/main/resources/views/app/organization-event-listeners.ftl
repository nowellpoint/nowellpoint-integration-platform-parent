<#import "template.html" as t>
    <@t.page>
        
        
        <div class="pl-3 pt-4 pb-1">
            <h5>${labels['event.listeners']}</h5>
        </div> 
        
       
        
        <!-- <i class="fa fa-arrow-left fa-2x" aria-hidden="true"></i> -->
       <table class="table">
            <thead>
                <tr>
                    <th scope="col">${labels['id']}</th>
                    <th scope="col">${labels['name']}</th>
                    <th scope="col">${labels['enabled']}</th>
                    <th scope="col">${labels['last.event.received.on']}</th>
                </tr>
            </thead>
            <tbody>
                <#if organization.eventListeners?size==0>
                    <tr>
                        <td colspan="4">&nbsp;</td>
                    </tr>
                <#else>
                    <#list organization.eventListeners as listener>
                        <tr>
                            <th scope="row">${listener.id}</th>
                            <td>${listener.name}</td>
                            <td>
                                <#if listener.enabled>
                                    <i class="fa fa-check mr-2 green-text" aria-hidden="true"></i>
                                <#else>
                                    <i class="fa fa-close mr-2 red-text" aria-hidden="true"></i>
                                </#if>
                            </td>
                            <td>
                                <#if listener.lastEventReceivedOn??>
                                    ${listener.lastEventReceivedOn?date?string.long - listener.lastEventReceivedOn?time?string.medium}
                                <#else>
                                    &nbsp;
                                </#if>
                            </td>
                        </tr>
                    </#list>
                </#if>
            </tbody>
        </table>
    </@t.page>