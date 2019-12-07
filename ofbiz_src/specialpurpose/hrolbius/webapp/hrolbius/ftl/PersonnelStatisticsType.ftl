				<div class="span6 widget-container-span ui-sortable " style="border-left: 1px solid #dddeee; border-right: 1px solid #dddeee;">
						<div class="widget-box" style="margin: 0px;">
							<div class="widget-header">
								<h5>${uiLabelMap.PersonnelChangeStatistics}</h5>
									<div class="widget-toolbar" style = "float: right">
										<form id="PersonnelStatType" name="PersonnelStatType" method="post" action="<@ofbizUrl>BuildEmplStatChart</@ofbizUrl>">
											<select name="personnelChartType" onchange="ajaxUpdateArea('PersonnelStatChart', 'BuildEmplStatChart', jQuery('#PersonnelStatType').serialize());" style="border-radius: 3px;margin-bottom: 0px; width: 150px;">
												<option value="100">${uiLabelMap.NumberOfEmployees}</option>
											</select>
										</form>
									</div>
									<h5 style = "float: right">${uiLabelMap.By}</h5>
							</div>

							<div class="widget-body">
							<div class="widget-main" id="PersonnelStatChart">
								<div id="container2" style="height: 400px"></div>
								<div id="sliders">
									<table>
										<tr><td>Alpha Angle</td><td><input id="R0" type="range" min="0" max="45" value="15"/> <span id="R0-value" class="value"></span></td></tr>
	   									 <tr><td>Beta Angle</td><td><input id="R1" type="range" min="0" max="45" value="15"/> <span id="R1-value" class="value"></span></td></tr>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
<script>

$(function () {
    // Set up the chart
    var chart = new Highcharts.Chart({
        chart: {
            renderTo: 'container2',
            type: 'column',
            margin: 75,
            options3d: {
                enabled: true,
                alpha: 15,
                beta: 15,
                depth: 50,
                viewDistance: 25
            }
        },
        title: {
            text: '${StringUtil.wrapString(uiLabelMap.StatisticsNumberOfEmployees)}'
        },
       
        plotOptions: {
            column: {
                depth: 25
            }
        },
        series: [{
            data: [${y2010}, ${y2011}, ${y2012}, ${y2013}, ${y2014}]
        }]
    });

    function showValues() {
        $('#R0-value').html(chart.options.chart.options3d.alpha);
        $('#R1-value').html(chart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R0').on('change', function () {
        chart.options.chart.options3d.alpha = this.value;
        showValues();
        chart.redraw(false);
    });
    $('#R1').on('change', function () {
        chart.options.chart.options3d.beta = this.value;
        showValues();
        chart.redraw(false);
    });

    showValues();
});
</script>	