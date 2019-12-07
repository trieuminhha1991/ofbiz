<div class="span6 widget-container-span ui-sortable " style="border-left: 1px solid #dddeee; border-right: 1px solid #dddeee;">
	<div class="widget-box" style="margin: 0px;">
		<div class="widget-header">
			<h5>${uiLabelMap.EmployeeStatistics}</h5>
				<div class="widget-toolbar" style = "float: right">
					<form id="EmplStatType" name="EmplStatType" method="post" action="<@ofbizUrl>BuildEmplStatChart</@ofbizUrl>">
						<select name="emplChartType" onchange="ajaxUpdateArea('EmployeeStatChart', 'BuildEmplStatChart', jQuery('#EmplStatType').serialize());" style="border-radius: 3px;margin-bottom: 0px; width: 150px;">
							<option value="1">${uiLabelMap.Gender}</option>
							<option value="2">${uiLabelMap.Agreement}</option>
							<option value="3">${uiLabelMap.Level}</option>
						</select>
					</form>
				</div>
				<h5 style = "float: right">${uiLabelMap.By}</h5>
		</div>

	<div class="widget-body">
		<div class="widget-main" id="EmployeeStatChart">
			<div id="container" style="height: 450px"></div>
		</div>
	</div>
</div>
</div>

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