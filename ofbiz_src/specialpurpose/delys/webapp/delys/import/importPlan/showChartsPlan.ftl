<div id="container" style="min-width: 310px; max-width: 800px; height: 600px; margin: 0 auto"></div>
<div id="containerNow" style="min-width: 310px; max-width: 800px; height: 600px; margin: 0 auto"></div>

<#if result?exists>
<script type="text/javascript">
	Highcharts.theme = {
   	colors: ["#7cb5ec", "#f7a35c", "#90ee7e", "#7798BF", "#aaeeee", "#ff0066", "#eeaaee",
      "#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
   chart: {
      backgroundColor: null,
      style: {
         fontFamily: "Dosis, sans-serif"
      }
   },
   title: {
      style: {
         fontSize: '16px',
         fontWeight: 'bold',
         textTransform: 'uppercase'
      }
   },
   tooltip: {
      borderWidth: 0,
      backgroundColor: 'rgba(219,219,216,0.8)',
      shadow: false
   },
       
   legend: {
      itemStyle: {
         fontWeight: 'bold',
         fontSize: '13px'
      }
   },
   xAxis: {
      gridLineWidth: 1,
      labels: {
         style: {
            fontSize: '12px'
         }
      }
   },
   yAxis: {
      minorTickInterval: 'auto',
      title: {
         style: {
            textTransform: 'uppercase'
         }
      },
      labels: {
         style: {
            fontSize: '12px'
         }
      }
   },
   plotOptions: {
      series: {
                pointWidth: 10
            }
   },


   // General
   background2: '#F0F0EA'
   
};
// Apply the theme
Highcharts.setOptions(Highcharts.theme);
      
        
// current chart
	$('#containerNow').highcharts({
        chart: {
            type: 'waterfall'
        },

        title: {
            text: '${StringUtil.wrapString(uiLabelMap.PlanChartsOfProduct)}'
        },

        xAxis: {
            type: 'category'
        },

        yAxis: {
            title: {
                text: 'USD'
            }
        },

        legend: {
            enabled: false
        },

        series: [{
            upColor: Highcharts.getOptions().colors[2],
            color: Highcharts.getOptions().colors[3],
            data: [{
                name: 'Actual',
                y: ${recentPlanQuantity}
            }, {
                name: 'remain',
                y: ${planQuantity} - ${recentPlanQuantity},
                color: Highcharts.getOptions().colors[4]
            }, 
            {
                name: 'plan',
                isIntermediateSum: true,
                color: Highcharts.getOptions().colors[1]
            }],
            dataLabels: {
                enabled: true,
                formatter: function () {
                    return Highcharts.numberFormat(this.y / 1000, 0, ',') + 'k';
                },
                style: {
                    color: '#FFFFFF',
                    fontWeight: 'bold',
                    textShadow: '0px 0px 3px black'
                }
            },
            pointPadding: 0
        }]
    });
    
//  
	var options = {
    chart: {
        renderTo: 'container'
    },
    title: {
        text: '${StringUtil.wrapString(uiLabelMap.PlanChartsForYear)}'
    },
    xAxis: {
        categories: []
    },
    yAxis: {
    	allowDecimals: true,
        title: {
            text: 'sum'
        }
    },
    tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table border="1">',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>{point.y:.1f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
    series: []
};
		<#assign flag = 1/>
		<#list result as product>
			options.xAxis.categories.push('Thang' +${flag});
			<#assign flag = flag +1/>
        </#list>
			var series = {
				type: 'bar',
                data: []
            };
			series.name = '${StringUtil.wrapString(uiLabelMap.PlanQuantity)}';
			<#list result as product>
				series.data.push(${product.planQuantity});
        	</#list>
			options.series.push(series);
			var series = {
				type: 'bar',
                data: []
            };
			series.name = '${StringUtil.wrapString(uiLabelMap.RecentPlanQuantity)}';
        	<#list result as product>
				series.data.push(${product.recentPlanQuantity});
        	</#list>
			options.series.push(series);
        	
        var chart = new Highcharts.Chart(options);
  
</script>
	
</#if>