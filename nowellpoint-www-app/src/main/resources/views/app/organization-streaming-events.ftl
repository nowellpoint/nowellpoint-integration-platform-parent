<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid pt-4 pb-2 pr-3 pl-3">
                <div class="dashhead">
                    <div class="dashhead-titles">
                        <h4 class="dashhead-title font-weight-normal color-white">${messages["streaming.events"]}</h4>
                    </div>
                    <div class="dashhead-toolbar">

                    </div>
                </div>
            </div>
            <div class="container-fluid border-top">
                <div class="row">
                    <div class="col-10 p-3">
                        <div id="events-last-7-days"></div>
                        <br>
                        <br>
                        <table class="table">
                            <thead>
                                <tr class="d-flex">
                                    <th class="col-3">${labels['prefix']}</th>
                                    <th class="col-3">${labels['source']}</th>
                                    <th class="col-3">${labels['topic.id']}</th>
                                    <th class="col-3 text-center">${labels['active']}</th>
                                </tr>
                            </thead>
                            <tbody>
                                <#list organization.streamingEventListeners?sort_by("source") as eventListener>
                                    <tr class="d-flex">
                                        <td class="col-3"><div class="circle"><span class="initials">${eventListener.prefix}</span></div></td>
                                        <td class="col-3 align-middle"><a href="${eventListener.href}">${eventListener.source}</a></td>
                                        <td class="col-3 align-middle">
                                            <#if eventListener.topicId??>
                                                ${eventListener.topicId} 
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </td>
                                        <td class="col-3 text-center align-middle">
                                            <#if eventListener.active>
                                                <h5><span class="badge badge-success">${labels['active']}</span></h5>
                                            <#else>
                                                <h5><span class="badge badge-danger">${labels['inactive']}</span></h5>
                                            </#if>
                                        </td>
                                    </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                    <div class="col-2 pb-3">
                        <div class="activity-feed">
                                    <#list feedItems as feedItem>
                                        <div class="feed-item">
                                            <div class="date">${feedItem.lastUpdatedOn?date?string.long}<span>${feedItem.lastUpdatedOn?time?string.medium}</div>
                                            <div class="text">${feedItem.subject}<span>${feedItem.body}</span></div>
                                        </div>
                                    </#list>x
                                </div>        
                    </div>
                </div>
            </div>
        </div>

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