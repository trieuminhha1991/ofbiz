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
            text: '${StringUtil.wrapString(uiLabelMap.EmployeeStatistics)} ${StringUtil.wrapString(uiLabelMap.By)} ${StringUtil.wrapString(uiLabelMap.Level)}'
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
                    format: '{point.name}',
                    distance:30
                },
            }
        },
        series: [{
            type: 'pie',
            name: '${StringUtil.wrapString(uiLabelMap.Level)}',
            data: [
            	['${StringUtil.wrapString(uiLabelMap.Master)}',  ${masterDegree}],
                ['${StringUtil.wrapString(uiLabelMap.University)}', ${uniDegree}],
                ['${StringUtil.wrapString(uiLabelMap.College)}', ${collegeDegree}],
                ['${StringUtil.wrapString(uiLabelMap.Intermediate)}', ${intermediateDegree}],
                ['${StringUtil.wrapString(uiLabelMap.Secondary)}', ${secondaryDegree}],
                ['${StringUtil.wrapString(uiLabelMap.Base)}', ${baseDegree}],
            ]
        }]
    });
});
</script>	