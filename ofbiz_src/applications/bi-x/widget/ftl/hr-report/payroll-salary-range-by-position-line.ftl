<script type="text/javascript">
    $(function(){
        var containerUuid = appendContainer(
            getParentScript('loadSalaryRangeByPosition(group, date_type, from_date, thru_date, chartId, idLoading)'),
            310, 600, 400
        );

        var popup = initJQXWindow();

        $('#' + containerUuid).highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: 'Salary Range by Position',
                x: -20 //center
            },
//            subtitle: {
//                text: 'Chart',
//                x: -20
//            },
            xAxis: {
                labels: {
                    enabled: true
                }

            },
            yAxis: {
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }],
                title: {
                    text: null
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>{point.y:.1f}</b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                }
            },
            exporting: {
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

        // set default: date range
        var now = new Date();
        now.setHours(0,0,0);

        // get first date of year
        var from_date = now.getFullYear() + '-01-01';

        // get date now
        var thru_date = now.getFullYear() + "-" + (now.getMonth()+1) + "-" + now.getDate();


        loadSalaryRangeByPosition('company', '', from_date, thru_date, containerUuid, id);

//        popup.addJQXTree('JQXTree', 'Party', 200, 200);
        // TODO: fixed party_group_parent='company'
        popup.addJQXTreeChild('JQXTree', 'Party', 'company', 200, 200);

        var date_type_id = popup.addDropDownList('dateType', 'Statistics', ["DAY", "WEEK", "MONTH", "QUARTER", "YEAR" ],  0, 140, 25);
        var fromDateId = popup.addDateTimeInput("from_date", "From", "yyyy-MM-dd", from_date, 140, 25);
        var thruDateId = popup.addDateTimeInput("thru_date", "Thru", "yyyy-MM-dd", thru_date, 140, 25)

        popup.addOK(function () {
            popup.close();
            loadSalaryRangeByPosition(popup.getParty(), $('#'+date_type_id).val(), $('#'+fromDateId).val(), $('#'+thruDateId).val(), containerUuid, id);
        });
    });

    function loadSalaryRangeByPosition(group, date_type, from_date, thru_date, chartId, idLoading) {
        if (!group) return;
        var chart = $('#' + chartId).highcharts();
        var date = new Date();
        jQuery.ajax({
            url: 'payroll-salary-range-by-position',
            async: true,
            type: 'POST',
            beforeSend: function () {
                $('#' + idLoading).show();
            },
            data: {
                'group': group,
                'dateType': date_type,
                'fromDate': from_date,
                'thruDate': thru_date
            },
            success: function (data) {
                chart.xAxis[0].update({categories: data.xAxis}, false);

                while (chart.series.length > 0) {
                    chart.series[0].remove(true);
                }

                for (var i in data.yAxis) {
                    chart.addSeries({name: i, data: data.yAxis[i]}, true);
                }
            },
            complete: function () {
                $('#' + idLoading).hide();
            }
        });
    }
</script>