<#import "template.html" as t>

    <@t.page>

        <#include "event-listener-menu.ftl" />

        <div id="content" class="content">
            <div class="container-fluid p-3">
                <div class="dashhead mb-3">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${labels["salesforce"]}</h6>
                        <h3 class="dashhead-title">${labels['event.listeners']}</h3>
                    </div>
                </div>
                <div class="container-fluid pt-3 mb-1">
                    <div class="card-columns">
                        <div class="card">
                            <div class="card-body">
                                <canvas id="events-received-chart" width="800" height="800"></canvas>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Chart JS -->
                <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.7.3/Chart.bundle.min.js"></script>

                <script>
                    var ctx = document.getElementById("events-received-chart").getContext('2d');
                    var myChart = new Chart(ctx, {
                        type: 'line',
                        data: {
                            labels: ["7d", "6d", "5d", "4d", "3d", "2d", "1d", "0d"],
                            datasets: [{
                                label: "All Events",
                                data: [15, 23, 28, 30, 37, 45, 17, 3],
                                backgroundColor: "rgba(255,153,0,0.4)",
                                fill: false
                            }]
                        },
                        options: {
                            title: {
                                display: true,
                                text: 'Events received the last 7 days'
                            }
                        }
                    });
                </script>


                <!--
                <table class="table">
                    <thead>
                        <tr class="d-flex">
                            <th class="col-3">${labels['id']}</th>
                            <th class="col-3">${labels['name']}</th>
                            <th class="col-3">${labels['enabled']}</th>
                            <th class="col-3">${labels['last.event.received.on']}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#if organization.eventListeners?size==0>
                            <tr>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                            <#else>
                                <#list organization.eventListeners?sort_by("name") as listener>
                                    <tr class="d-flex">
                                        <td class="col-3">${listener.id}</td>
                                        <td class="col-3">${listener.name}</td>
                                        <td class="col-3">
                                            <#if listener.enabled>
                                                <i class="fa fa-check mr-2 text-success" aria-hidden="true"></i>
                                                <#else>
                                                    <i class="fa fa-close mr-2 text-danger" aria-hidden="true"></i>
                                            </#if>
                                        </td>
                                        <td class="col-3">
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
-->
            </div>
        </div>
    </@t.page>