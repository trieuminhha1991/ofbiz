<#--<div id="container" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>-->
<script type="text/javascript">

    $(function () {

        var containerUuid = appendContainer(
            getParentScript('function loadSalaryStructure(person, group, from_date, thru_date, chartId, idLoading)'),
            310, 600, 400
        );

        var popup = initJQXWindow();

        $('#'+containerUuid).highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: 'Salary Structure'
            },
//            subtitle: {
//                text: 'test'
//            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            },
            series: [{
                type: 'pie',
                name: 'Salary Structure',
//                data: [
//					['Lương thực tế', 5000000],
//					['Trợ cấp đi lại', 100000],
//					['Phụ cấp ăn trưa', 200000],
//					['Phụ cấp chức vụ', 1000000],
//					['Phụ cấp xăng xe', 150000],
//					['Phụ cấp điện thoại', 100000],
//					['Thưởng', 500000]
//                ]
            }],
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

        //@debug
//        var from_date = '2015-02-01'
//        var thru_date = '2015-04-01'

        loadSalaryStructure('', '', from_date, thru_date, containerUuid, id);

        //@debug
//        loadSalaryStructure('DHR006', '', from_date, thru_date, containerUuid, id);


        popup.addJQXTreeChild('JQXTree', 'Party', 'company', 200, 200);
        var fromDateId = popup.addDateTimeInput("from_date", "From", "yyyy-MM-dd", from_date, 140, 25);
        var thruDateId = popup.addDateTimeInput("thru_date", "Thru", "yyyy-MM-dd", thru_date, 140, 25)

        popup.addOK(function () {
            popup.close();
            salary = loadSalaryStructure('', popup.getParty(), $('#'+fromDateId).val(), $('#'+thruDateId).val(), containerUuid, id);
        });
    });

    function loadSalaryStructure(person, group, from_date, thru_date, chartId, idLoading) {

        var chart = $('#'+chartId).highcharts();
        jQuery.ajax({
            url: 'payroll-salary-structure',
            async: true,
            type: 'POST',
            beforeSend: function () {
                $('#'+idLoading).show();
            },
            data: {
                'party_person': person,
                'party_group' : group,
                'fromDate': from_date,
                'thruDate': thru_date
            },
            success: function (data) {

                if(!data.xAxis) {
                    chart.series[0].setData(null);
                    chart.setTitle({
                        text : null
                    }, {
                        text : null
                    });
                } else {
                    // if exists data response, then update data.
                    // if not, using default.
                    var array = [];

                    for (var i in data.yAxis) {
                        var tmp = [];
                        tmp.push(i);
                        tmp.push(data.yAxis[i][0]);
                        array.push(tmp);
                    }
                    chart.series[0].setData(array);
                }
            },
            complete: function() {
                $('#'+idLoading).hide();
            }
        });
    }

</script>