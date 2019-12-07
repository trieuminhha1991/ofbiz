<script type="text/javascript">
    $(function(){
        var containerUuid = appendContainer(
            getParentScript('loadSalaryRange(group, fromdate, thrudate, chartId, idLoading)'),
            310, 600, 400
        );

        var popup = initJQXWindow();

        var salary = {};

        $('#'+containerUuid).highcharts({

            title: {
                text: 'Salary Range'
            },
            subtitle: {
                text: 'By Month'
            },
            xAxis: {
                /*type: 'datetime',
                dateTimeLabelFormats: {
                    day: '%b %e, %y'
                }*/
            },
            yAxis: {
                title: {
                    text: null
                },
                min: 0
            },

            tooltip: {
                crosshairs: true,
                shared: true,
                formatter: function() {
                    return '<b>' + Highcharts.dateFormat('%A, %B %e, %Y', new Date(this.x)) + '<b><br/>'
                            + '<b>' + this.points[0].series.name +' : '+ salary['averages'][ this.x] + '<b><br/>'
                            + '<b>' + this.points[1].series.name + ' : ' + salary['ranges'][this.x][0]
                            + ' - ' + salary['ranges'][this.x][1] + '<b>';
                }
            },
            legend: {
                enabled: false
            },
            exporting : {
                buttons: {
                    'config': {
                        align: 'left',
                        x: 5,
                        onclick: popup.open(),
                        symbol: 'url(/aceadmin/assets/images/cogs_icon.png)'
                    }
                }
            }
        });

        var id = appendLoading('#'+containerUuid);

        /* set default date range*/
        var now = new Date();
        now.setHours(0,0,0);

        var from_date = now.getFullYear() + '-01-01';
        var thru_date = now.getFullYear() + "-" + (now.getMonth()+1) + "-" + now.getDate();


//        popup.addJQXTree('JQXTree', 'Party', 200, 200);
        // TODO: fixed party_group_parent='company'
        popup.addJQXTreeChild('JQXTree', 'Party', 'company', 200, 200);

        salary = loadSalaryRange('', from_date, thru_date, containerUuid, id);
        var fromDateId = popup.addDateTimeInput("from_date", "From", "yyyy-MM-dd", from_date, 140, 25);
        var thruDateId = popup.addDateTimeInput("thru_date", "Thru", "yyyy-MM-dd", thru_date, 140, 25)


        popup.addOK(function () {
            popup.close();
            salary = loadSalaryRange(popup.getParty(), $('#'+fromDateId).val(), $('#'+thruDateId).val(), containerUuid, id);
        });
    });

    function loadSalaryRange(group, fromdate, thrudate, chartId, idLoading) {
        var salary = {};
        var chart = $('#'+chartId).highcharts();
        jQuery.ajax({
            url: 'payroll-salary-range',
            async: false,
            type: 'POST',
            beforeSend: function () {
                $('#'+idLoading).show();
            },
            data: {
                'group': group,
                'fromDate': fromdate,
                'thruDate': thrudate
            },
            success: function (data) {

                chart.xAxis[0].update({categories: data.xAxis}, false);

                while (chart.series.length > 0) {
                    chart.series[0].remove(true);
                }

                var tmp = [];
                for(var i in data.xAxis) {
                    if(!salary['averages']) {
                        salary['averages'] = {};
                    }
                    salary['averages'][data.yAxis['averages'][i][0]] = data.yAxis['averages'][i][1];
                    var _tmp = data.yAxis['averages'][i];
                    tmp.push(_tmp);
                }

                chart.addSeries({
                    name : 'Averages',
//                    data: data.yAxis['averages'],
                    data:tmp,
                    marker: {
                        fillColor: 'white',
                        lineWidth: 2,
                        lineColor: Highcharts.getOptions().colors[0]
                    }
                }, true);

                tmp = [];

                for(var i in data.xAxis) {
                    if(!salary['ranges']) {
                        salary['ranges'] = {};
                    }
                    salary['ranges'][data.yAxis['ranges'][i][0]] = [];
                    salary['ranges'][data.yAxis['ranges'][i][0]].push(data.yAxis['ranges'][i][1]);
                    salary['ranges'][data.yAxis['ranges'][i][0]].push(data.yAxis['ranges'][i][2]);
                    var _tmp = data.yAxis['ranges'][i];
                    tmp.push(_tmp);
                }

                chart.addSeries({
                    name : 'Range',
//                    data: data.yAxis['ranges'],
                    data:tmp,
                    type: 'arearange',
                    lineWidth: 0,
                    linkedTo: ':previous',
                    color: Highcharts.getOptions().colors[0],
                    fillOpacity: 0.3,
                    zIndex: 0
                }, true);
            },
            complete: function() {
                $('#'+idLoading).hide();
            }
        });
        return salary;
    }
</script>