<#import "template.html" as t>

    <@t.page>

        <#include "sidebar.ftl" />

        <content>
            <div class="container-fluid mt-3 p-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h6 class="dashhead-subtitle">${labels["salesforce"]}</h6>
                        <h3 class="dashhead-title">${labels['event.listeners']}</h3>
                    </div>
                </div>
                <hr>
                <div id="events-last-7-days"></div>
                <br>
                <br>
                <table class="table">
                    <thead>
                        <tr class="d-flex">
                            <th class="col-3">${labels['id']}</th>
                            <th class="col-3">${labels['name']}</th>
                            <th class="col-3 text-center">${labels['enabled']}</th>
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
                                        <td class="col-3 text-center">
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
            </div>
        </content>

            <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
            <script type="text/javascript">
                google.charts.load('current', {
                    'packages': ['corechart', 'bar']
                });
                google.charts.setOnLoadCallback(drawChart);

                function drawChart() {
                    var data = new google.visualization.DataTable();
                    data.addColumn('string', 'Days');
                    data.addColumn('number', 'Events');
                    data.addRows([
                        ${data}
                    ]);

                    var options = {
                        title: "${labels['events.received.last.n.days']?replace(':s1','7')}",
                        focusTarget: 'category',
                        backgroundColor: 'transparent',
                        hAxis: {
                            viewWindow: {
                                min: [7, 30, 0],
                                max: [17, 30, 0]
                            },
                            textStyle: {
                                fontSize: 14,
                                color: '#053061',
                                bold: true,
                                italic: false
                            }
                        },
                        vAxis: {
                            textStyle: {
                                fontSize: 18,
                                color: '#67001f',
                                bold: false,
                                italic: false
                            }
                        }
                    };

                    var chart = new google.visualization.ColumnChart(document.getElementById('events-last-7-days'));

                    chart.draw(data, options);
                }

                $(document).ready(function() {
                    $(window).resize(function() {
                        drawChart();
                    });
                });
            </script>
    </@t.page>