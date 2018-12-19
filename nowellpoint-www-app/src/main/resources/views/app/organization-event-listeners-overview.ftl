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
                <div class="card-columns">
                    <div class="card border-success border-right-0 border-bottom-0 border-left-0">
                        <div class="card-body">
                            <h5 class="card-title">${labels['events.received.last.n.days']?replace(':s1','7')}</h5>
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
                    var data = new google.visualization.DataTable();
                    data.addColumn('string', 'Days');
                    data.addColumn('number', 'Events');
                    data.addRows([
                        ${data}
                    ]);

                    var options = {
                        curveType: 'function',
                        pointSize: 3,
                        lineWidth: 0,
                        legend: 'none',
                        vAxis: {
                            viewWindow: {
                                min: 0
                            }
                        },
                        width: '100%',
                        chartArea: {
                            left: 40,
                            width: '100%'
                        }
                    };

                    var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

                    chart.draw(data, options);
                }
            </script>
        </div>
    </@t.page>