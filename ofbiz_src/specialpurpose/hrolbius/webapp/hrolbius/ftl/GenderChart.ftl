<div id="container" style="height: 450px"></div>
<script>
	$(function () {
    $('#container').highcharts({
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45,
                beta: 0
            }
        },
        title: {
            text: '${StringUtil.wrapString(uiLabelMap.EmployeeStatistics)} ${StringUtil.wrapString(uiLabelMap.By)} ${StringUtil.wrapString(uiLabelMap.Gender)}'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                depth: 35,
                dataLabels: {
                    enabled: true,
                    format: '{point.name}'
                }
            }
        },
        series: [{
            type: 'pie',
            name: '${StringUtil.wrapString(uiLabelMap.Gender)}',
            data: [
                ['${StringUtil.wrapString(uiLabelMap.Male)}',   ${maleNumber}],
                ['${StringUtil.wrapString(uiLabelMap.Female)}', ${femaleNumber}],
            ]
        }]
    });
});
</script>	