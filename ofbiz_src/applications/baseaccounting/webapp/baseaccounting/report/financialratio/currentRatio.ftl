<script type="text/javascript">
	$(function () {
		/**
 			* Sand-Signika theme for Highcharts JS
 			* @author Torstein Honsi
 		*/

		// Load the fonts
		Highcharts.createElement('link', {
		   href: '//fonts.googleapis.com/css?family=Signika:400,700',
		   rel: 'stylesheet',
		   type: 'text/css'
		}, null, document.getElementsByTagName('head')[0]);
		
		// Add the background image to the container
		Highcharts.wrap(Highcharts.Chart.prototype, 'getContainer', function (proceed) {
		   proceed.call(this);
		   this.container.style.background = 'url(http://www.highcharts.com/samples/graphics/sand.png)';
		});


		Highcharts.theme = {
		   colors: ["#f45b5b", "#8085e9", "#8d4654", "#7798BF", "#aaeeee", "#ff0066", "#eeaaee",
		      "#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
		   chart: {
		      backgroundColor: null,
		      style: {
		         fontFamily: "Signika, serif"
		      }
		   },
		   title: {
		      style: {
		         color: 'black',
		         fontSize: '16px',
		         fontWeight: 'bold'
		      }
		   },
		   subtitle: {
		      style: {
		         color: 'black'
		      }
		   },
		   tooltip: {
		      borderWidth: 0
		   },
		   legend: {
		      itemStyle: {
		         fontWeight: 'bold',
		         fontSize: '13px'
		      }
		   },
		   xAxis: {
		      labels: {
		         style: {
		            color: '#6e6e70'
		         }
		      }
		   },
		   yAxis: {
		      labels: {
		         style: {
		            color: '#6e6e70'
		         }
		      }
		   },
		   plotOptions: {
		      series: {
		         shadow: true
		      },
		      candlestick: {
		         lineColor: '#404048'
		      },
		      map: {
		         shadow: false
		      }
		   },
		
		   // Highstock specific
		   navigator: {
		      xAxis: {
		         gridLineColor: '#D0D0D8'
		      }
		   },
		   rangeSelector: {
		      buttonTheme: {
		         fill: 'white',
		         stroke: '#C0C0C8',
		         'stroke-width': 1,
		         states: {
		            select: {
		               fill: '#D0D0D8'
		            }
		         }
		      }
		   },
		   scrollbar: {
		      trackBorderColor: '#C0C0C8'
		   },
		
		   // General
		   background2: '#E0E0E8'
		   
		};
		
		// Apply the theme
		Highcharts.setOptions(Highcharts.theme);
		$('#container').highcharts({
		        chart: {
		            type: 'column'
		        },
		        title: {
		            text: '${StringUtil.wrapString(uiLabelMap.BACCCurrentRatio)}'
		        },
		        subtitle: {
		            text: ''
		        },
		        xAxis: {
		            categories: [
		                <#if listCycle?has_content>
			                <#list listCycle as list>
								'${StringUtil.wrapString(list)}'
								<#if list_index != (listCycle?size - 1)>
									,
								</#if>
							</#list>
						</#if>
		            ]
		        },
		        yAxis: {
		            title: {
		                text: '(%)'
		            }
		        },
		        tooltip: {
		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		                '<td style="padding:0"><b>{point.y:.1f} %</b></td></tr>',
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
		        series: [{
		            name: 'Current Ratio',
		            data: [
		               <#if listCurrentRatio?has_content>
		            	<#list listCurrentRatio as list>
							${list?string?replace(",",".")}
							<#if list_index != (listCurrentRatio?size-1)>
								,
							</#if>
						</#list>
					   </#if>
					]
		        }]
	    });
	});
</script>
<div id="container">
</div>