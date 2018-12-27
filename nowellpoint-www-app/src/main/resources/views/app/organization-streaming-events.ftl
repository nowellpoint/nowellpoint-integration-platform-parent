<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <content id="content">
            <div class="container-fluid mt-2 pt-3 pr-3 pl-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h4 class="dashhead-title font-weight-normal">${messages["streaming.events"]}</h4>
                    </div>
                </div>
            </div>
            <hr>
            <div class="container-fluid p-3">
                <div id="events-last-7-days"></div>
                <br>
                <br>
                <table class="table">
                    <thead>
                        <tr class="d-flex">
                            <th class="col-3">${labels['prefix']}</th>
                            <th class="col-3">${labels['source']}</th>
                            <th class="col-3 text-center">${labels['enabled']}</th>
                            <th class="col-3">${labels['last.event.received.on']}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#if organization.streamingEventListeners?size==0>
                            <tr>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                            <#else>
                                <#list organization.streamingEventListeners as eventListener>
                                    <tr class="d-flex">
                                        <td class="col-3">${eventListener.prefix}</td>
                                        <td class="col-3"><a href="${eventListener.href}">${eventListener.source}</a></td>
                                        <td class="col-3 text-center">
                                            <#if eventListener.enabled>
                                                <i class="fa fa-check mr-2 text-success" aria-hidden="true"></i>
                                                <#else>
                                                    <i class="fa fa-close mr-2 text-danger" aria-hidden="true"></i>
                                            </#if>
                                        </td>
                                        <td class="col-3">
                                            <#if eventListener.lastEventReceivedOn??>
                                                ${eventListener.lastEventReceivedOn?date?string.long - eventListener.lastEventReceivedOn?time?string.medium}
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
                    title: "${labels['streaming.events.received.last.n.days']?replace(':s1','7')}",
                    focusTarget: 'category',
                    backgroundColor: 'transparent',
                    animation: {"startup": true},
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
                            //fontSize: 18,
                            //color: '#67001f',
                            bold: false,
                            italic: false
                        }
                    }
                };

                var chart = new google.visualization.ColumnChart(document.getElementById('events-last-7-days'));
                
                google.visualization.events.addListener(chart, 'select', function () {
                    var selectedItem = chart.getSelection()[0];
                    if (selectedItem) {
                        var value = data.getValue(selectedItem.row, 0);
                        alert('The user selected ' + value);
                    }
                });
                
                chart.draw(data, options);
            }

            $(document).ready(function() {
                $(window).resize(function() {
                    drawChart();
                });
            });
        </script>
    </@t.page>