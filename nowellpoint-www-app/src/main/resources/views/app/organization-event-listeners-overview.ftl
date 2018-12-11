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
                                <div id="curve_chart"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
                <script type="text/javascript">
                    google.charts.load('current', {
                        'packages': ['corechart']
                    });
                    google.charts.setOnLoadCallback(drawChart);

                    function drawChart() {
                        var data = google.visualization.arrayToDataTable([
                            ['Days', 'Events'],
                            ['7d', 15],
                            ['6d', 23],
                            ['5d', 28],
                            ['4d', 30],
                            ['3d', 37],
                            ['2d', 45],
                            ['1d', 17],
                            ['Today', 0],
                        ]);

                        var options = {
                            title: 'Events received the last 7 days',
                            curveType: 'function',
                            pointSize: 3,
                            lineWidth : 0,
                            legend: {
                                position: 'bottom'
                            }
                        };

                        var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

                        chart.draw(data, options);
                    }
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