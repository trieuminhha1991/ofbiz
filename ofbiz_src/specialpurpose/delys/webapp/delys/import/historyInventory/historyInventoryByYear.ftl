<#--
${listSalesForecastAndItems}
-->
<#if facilityFacts?exists>
<div class="widget-box transparent">
<div class="row-fluid">
    <div class="span12 widget-container-span">
	    <div class="widget-box transparent">
	    <div class="widget-body">
	            <div class="widget-main padding-12 no-padding-left no-padding-right">
	                <div class="tab-content padding-4">
        <table id="sale-forecast-view" class="table table-striped table-bordered table-hover">
            <thead>
                <tr class="sf-product">
                	<td class="sf-months" colspan="1" rowspan="2" style="text-align:center">NAM</td>
                	<#list products.history as productItem>
                    	<td colspan="3" rowspan="1" style="text-align:center">
							${productItem.internalName}
                    	</td>
                	</#list>
                	<td class="sf-value" rowspan="2" style="text-align:center">VALUE (1sp)</td>
                </tr>
                <tr class="sf-product-child">
                	<#list products.history as productItem>
                			<td style="text-align:center">SL nhap</td>
                			<td style="text-align:center">SL ATP</td>
                			<td style="text-align:center">SL ton</td>
                	</#list>
                </tr>
            </thead>
            <tbody>
            <#list facilityFacts as faci>
                <tr>
                    <td colspan="1" class="sf-month" style="text-align:center">
                    	<a style="margin-right:10px;cursor: pointer; text-decoration:none;" class="cl" show="0" year="${faci.year}">+</a>${faci.year}
                    </td>
                    <#if faci.history?size != 0>
                    	<#list faci.history as productItem>
                    		<td  style="text-align:center">${productItem.receiveTotal?string(",##0")}</td>
	        				<td  style="text-align:center">${productItem.availableToPromiseTotal?string(",##0")}</td>
	        				<td  style="text-align:center">${productItem.inventoryTotal?string(",##0")}</td>
		        		</#list>
		        	<#else>
		        		<#list products.history as productItem>
                				<td style="text-align:center">0</td>
                				<td style="text-align:center">0</td>
                				<td style="text-align:center">0</td>
                		</#list>
                    </#if>
                    
		        	<td style="text-align:center">#</td>
                </tr>
                <tr style="display:none;" class="year_${faci.year}">
              		<td><a style="margin-right:10px;cursor: pointer; text-decoration:none;" class="month child_${faci.year}" show="0" month="month_${faci.year}">+</a>THANG</td>
              		<#list products.history as productItem>
                    	<td colspan="3" rowspan="1" style="text-align:center">
							-
                    	</td>
                	</#list>
		        	<td style="text-align:center">#</td>
              	</tr>
              	<#list faci.historyMonth as monthItem>
                <tr style="display:none;" class="month_${faci.year}">
                	<td colspan="1" class="sf-month" style="text-align:center">
                    	${monthItem.month}
                    </td>
                    <#if monthItem.monthHis?size != 0>
                    	<#list monthItem.monthHis as productItem>
                    		<td  style="text-align:center">${productItem.receiveTotal?string(",##0")}</td>
	        				<td  style="text-align:center">${productItem.availableToPromiseTotal?string(",##0")}</td>
	        				<td  style="text-align:center">${productItem.inventoryTotal?string(",##0")}</td>
		        		</#list>
		        	<#else>
		        		<#list products.history as productItem>
                				<td style="text-align:center">-</td>
                				<td style="text-align:center">-</td>
                				<td style="text-align:center">-</td>
                		</#list>
                    </#if>
		        	<td style="text-align:center">#</td>
                </tr>
              </#list>
              	<tr style="display:none;" class="year_${faci.year}">
              		<td><a style="margin-right:10px;cursor: pointer; text-decoration:none;" class="week child_${faci.year}" show="0" week="week_${faci.year}">+</a>TUAN</td>
              		<#list products.history as productItem>
                    	<td colspan="3" rowspan="1" style="text-align:center">
							-
                    	</td>
                	</#list>
		        	<td style="text-align:center">#</td>
              	</tr>
              	
              	<#list faci.historyWeek as weekItem>
                <tr style="display:none;" class="week_${faci.year}">
                	<td colspan="1" class="sf-month" style="text-align:center">
                    	${weekItem.week}
                    </td>
                    <#if weekItem.weekHis?size != 0>
                    	<#list weekItem.weekHis as productItem>
                    		<td  style="text-align:center">${productItem.receiveTotal?string(",##0")}</td>
	        				<td  style="text-align:center">${productItem.availableToPromiseTotal?string(",##0")}</td>
	        				<td  style="text-align:center">${productItem.inventoryTotal?string(",##0")}</td>
		        		</#list>
		        	<#else>
		        		<#list products.history as productItem>
                				<td style="text-align:center">-</td>
                				<td style="text-align:center">-</td>
                				<td style="text-align:center">-</td>
                		</#list>
                    </#if>
		        	<td style="text-align:center">#</td>
                </tr>
              </#list>
              	
            </#list>
            </tbody>
        </table>
</div>
</div>
</div>
</div>
</div>
</div>
</div>


<div id="container" style="margin: 0 auto"></div>

<input type="button" value="bieu do thang" id="chartsMonth"/>
<div id="containerLine" style="margin: 0 auto"></div>

<script type="text/javascript">
//click year show month and week
	$('.cl').on('click', function(){
		//var click = $(this).val();
		var click = $(this).attr("year");
		if($(this).attr("show") == 0){
		$('.year_' +click).css("display","table-row");
			$(this).attr("show","1");
			$(this).text("-");
			$(this).css("color","red");
		}else{
			$('.year_' +click).css("display","none");
			$('.month_' +click).css("display","none");
			$('.week_' +click).css("display","none");
			$(this).attr("show","0");
			$(this).text("+");
			$('.child_' +click).text("+");
			$('.child_' +click).css("color","#08c");
			$(this).css("color","#08c");
			$('.child_' +click).attr("show","0");
		}
	});
//click month show some month of year
	$('.month').on('click', function(){
		//var click = $(this).val();
		var click = $(this).attr("month");
		if($(this).attr("show") == 0){
			$('.' +click).css("display","table-row");
			$(this).attr("show","1");
			$(this).text("-");
			$(this).css("color","red");
		}else{
			$('.' +click).css("display","none");
			$(this).attr("show","0");
			$(this).text("+");
			$(this).css("color","#08c");
		}
	});
	
	$('.week').on('click', function(){
		//var click = $(this).val();
		var click = $(this).attr("week");
		if($(this).attr("show") == 0){
			$('.' +click).css("display","table-row");
			$(this).attr("show","1");
			$(this).text("-");
			$(this).css("color","red");
		}else{
			$('.' +click).css("display","none");
			$(this).attr("show","0");
			$(this).text("+");
			$(this).css("color","#08c");
		}
	});
	
	$(document).ready(function(){
		<#assign width = 0/>
		<#if products.history.size() <= 12>
			<#assign width = 1100/>
			<#else>
			<#assign width = 2000/>
		</#if>
Highcharts.theme = {
   colors: ["#7cb5ec", "#f7a35c", "#90ee7e", "#7798BF", "#aaeeee", "#ff0066", "#eeaaee",
      "#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
   chart: {
   		width: ${width},
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
		
		
	var options = {
    chart: {
        renderTo: 'container'
    },
    title: {
        text: '${StringUtil.wrapString(uiLabelMap.inventoryChartsProYear)}'
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
		<#list productDim as proDim>
		<#if proDim.dimensionId != '_NA_' && proDim.dimensionId != '_NF_'>
			options.xAxis.categories.push('${StringUtil.wrapString(proDim.internalName)}');
		</#if>
        </#list>
		<#if facilityFacts?exists>
		<#list facilityFacts as faci>
			var series = {
				type: 'column',
                data: []
            };
			series.name = ${faci.year};
			<#if faci.history?size != 0>
                    	<#list faci.history as productItem>
                    		series.data.push(${productItem.inventoryTotal});
		        		</#list>
                    </#if>
                    options.series.push(series);
             
             
            var series = {
				type: 'spline',
                data: [],
                marker: {
                lineWidth: 2,
                lineColor: Highcharts.getOptions().colors[3],
                fillColor: 'white',
                
            }
            };
			series.name = 'ATP_' +${faci.year};
			<#if faci.history?size != 0>
                    	<#list faci.history as productItem>
                    		series.data.push(${productItem.availableToPromiseTotal});
		        		</#list>
                    </#if>
                    options.series.push(series);
		</#list>
		var chart = new Highcharts.Chart(options);
		</#if>
	});
	
//button month charts	
	$('#chartsMonth').on('click', function(){
		var div = $("<div>", {id: "foo", class: "a"});
		$('#container').append(div);
		//line charts
	var optionsLine = {
    chart: {
        renderTo: 'foo',
        defaultSeriesType: 'spline'
    },
    title: {
        text: 'Bieu Do Ton Kho Theo Thang Nam 2014'
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
    series: []
};
		<#if facilityFacts?exists>
		<#list facilityFacts as facility>
		<#if facility.year == 2014>
			<#list facility.historyMonth as faci>
				optionsLine.xAxis.categories.push(${faci.month});
			</#list>
		</#if>
		</#list>	
		</#if>
		
		<#list products.history as proDim>
		<#if facilityFacts?exists>
			var series = {
                data: []
            };
            series.name = '${StringUtil.wrapString(proDim.internalName)}';
		<#list facilityFacts as facility>
		<#if facility.year == 2014>
		<#list facility.historyMonth as faci>
			<#if faci.monthHis?size != 0>
                <#list faci.monthHis as productItem>
                	<#if proDim.productDimId == productItem.productDimId>
                    	series.data.push(${productItem.inventoryTotal});
                    </#if>
	    		</#list>
	    	<#else>
	    		series.data.push(0);
            </#if>
		</#list>
		</#if>
		</#list>
			optionsLine.series.push(series);
		
		</#if>
        </#list>
        var chart = new Highcharts.Chart(optionsLine);
	});
	
</script>
</#if>
