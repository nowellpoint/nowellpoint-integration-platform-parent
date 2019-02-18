<#import "template.html" as t>
    <@t.page>
        <#include "sidebar.ftl" />
        <div id="content">
            <div class="container-fluid p-4">
                <div class="flextable">
                    <div class="flextable-item flextable-primary">
                        <h2>${messages["dashboard"]}</h2>
                    </div>
                    <div class="flextable-item">

                    </div>
                </div>
                <div class="row">
                    <div class="col-10 p-3">
                        <div id="events-last-7-days"></div>
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