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
            text: 'Thống kê nhân viên theo độ tuổi'
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
            name: 'Độ tuổi',
            data: [
                ['Age < 30',  ${range1}],
                ['30 <= Age < 40', ${range2}],
                ['40 <= Age < 60', ${range3}],
                ['60 <= Age < 100', ${range4}],
                ['Other', ${range0}],
            ]
        }]
    });
});
</script>	