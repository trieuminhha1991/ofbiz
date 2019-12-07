<div id="container"></div>
<script type="text/javascript">
	$(function () {
	    $('#container').highcharts({
	        chart: {
	            type: 'column'
	        },
	        title: {
	            text: '${StringUtil.wrapString(uiLabelMap.CommonTotal)}'
	        },
	        subtitle: {
	            text: '${StringUtil.wrapString(uiLabelMap.CommonSource)}: olbius.com'
	        },
	        xAxis: {
	            categories: [
	            	<#list balanceTotalList as btl>
	            		'${StringUtil.wrapString(uiLabelMap.get(btl.totalName))}'
	            		<#if (btl_index+1)!=balanceTotalList?size>,</#if>
	            	</#list>
	            ]
	        },
	        yAxis: {
	            min: 0,
	            title: {
	                text: '${StringUtil.wrapString(uiLabelMap.CommonUnit)}(${currencyUomId})'
	            }
	        },
	        tooltip: {
	            headerFormat: '<span style="font-size:12px;"><b>{point.key}</b></span><table style="margin-top:3px;">',
	            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
	                '<td style="padding:0"><b>&nbsp;{point.y:,2f} ${currencyUomId}</b></td></tr>',
	            footerFormat: '</table>',
	            shared: true,
	            useHTML: true,
	            decimalPoint: '.'
	        },
	        plotOptions: {
	            column: {
	                pointPadding: 0.2,
	                borderWidth: 0
	            }
	        },
	        series: [{
	            name: '2014',
	            data: [<#list balanceTotalList as btl>${btl.balance1}<#if (btl_index+1)!=balanceTotalList?size>,</#if></#list>]
	        }, {
	            name: '2015',
	            data: [<#list balanceTotalList as btl>${btl.balance2}<#if (btl_index+1)!=balanceTotalList?size>,</#if></#list>]
	        }]
	    });
	});
</script>