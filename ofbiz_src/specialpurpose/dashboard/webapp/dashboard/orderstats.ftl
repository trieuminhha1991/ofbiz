<script type="text/javascript"  charset="UTF-8">
	$(function() {
		var data = [
			{ label: "${StringUtil.wrapString(uiLabelMap.OrderGrossDollarAmountsIncludesAdjustmentsAndPendingOrders)}" , data: "${weekItemTotal}".replace(',', '.'), color: "#68BC31"},
			{ label: "${StringUtil.wrapString(uiLabelMap.OrderPaidDollarAmountsIncludesAdjustments)}" ,  data: "${weekItemTotalPaid}".replace(',', '.'), color: "#2091CF"},
			{ label: "${StringUtil.wrapString(uiLabelMap.OrderPendingPaymentDollarAmountsIncludesAdjustments)}" ,  data: "${weekItemTotalPending}".replace(',', '.'), color: "#AF4E96"}
		  ];
		 var placeholder = $('#piechart-placeholder').css({'width':'90%' , 'min-height':'150px'});
		 $.plot(placeholder, data, {
			
			series: {
		        pie: {
		            show: true,
					tilt:0.8,
					highlight: {
						opacity: 0.25
					},
					stroke: {
						color: '#fff',
						width: 2
					},
					startAngle: 2
					
		        }
		    },
		    legend: {
		        show: true,
				position: "ne", 
			    labelBoxBorderColor: null,
				margin:[-30,15]
		    }
			,
			grid: {
				hoverable: true,
				clickable: true
			},
			tooltip: true, //activate tooltip
			tooltipOpts: {
				content: "%s : %y.1",
				shifts: {
					x: -30,
					y: -50
				}
			}
			
		 });
		 
		   var $tooltip = $("<div class='tooltip top in' style='display:none;'><div class='tooltip-inner'></div></div>").appendTo('body');
		  placeholder.data('tooltip', $tooltip);
		  var previousPoint = null;
		
		  placeholder.on('plothover', function (event, pos, item) {
			if(item) {
				if (previousPoint != item.seriesIndex) {
					previousPoint = item.seriesIndex;
					var tip = item.series['label'] + " : " + item.series['percent']+'%';
					tip = escapeHtml(tip);
					$(this).data('tooltip').show().children(0).text(tip);
				}
				$(this).data('tooltip').css({top:pos.pageY + 10, left:pos.pageX + 10});
			} else {
				$(this).data('tooltip').hide();
				previousPoint = null;
			}
			
		 });
	 
	 })
	 function escapeHtml(text) {
	  return text.replace("&#40;","(").replace("&#41;",")");
	}
</script>
<div class="widget-box">
		<div class="widget-header widget-header-flat widget-header-small">
			<h5><i class="icon-signal"></i> ${uiLabelMap.OrderOrdersTotals} - ${uiLabelMap.OrderWTD}</h5>
		<!--	<div class="widget-toolbar no-border">
				<button class="btn btn-minier btn-primary dropdown-toggle" data-toggle="dropdown">This Week <i class="icon-angle-down icon-on-right"></i></button>
				<ul class="dropdown-menu dropdown-info pull-right dropdown-caret">
					<li class="active"><a href="#">This Week</a></li>
					<li><a href="#">Last Week</a></li>
					<li><a href="#">This Month</a></li>
					<li><a href="#">Last Month</a></li>
				</ul>
			</div> -->
		</div>
		
		<div class="widget-body">
		 <div class="widget-main">
			<div id="piechart-placeholder" style="width: 90%; min-height: 150px; padding: 0px; position: relative;">
				<canvas class="flot-base" style="direction: ltr; position: absolute; left: 0px; top: 0px; width: 382px; height: 150px;" width="382" height="150"></canvas>
				<canvas class="flot-overlay" style="direction: ltr; position: absolute; left: 0px; top: 0px; width: 382px; height: 150px;" width="382" height="150"></canvas>
				<div class="legend">
					<div style="position: absolute; width: 101px; height: 110px; top: 15px; right: -30px; background-color: rgb(255, 255, 255); opacity: 0.85;"> </div>
					<table style="position:absolute;top:15px;right:-30px;;font-size:smaller;color:#545454"><tbody><tr><td class="legendColorBox"><div style="border:1px solid null;padding:1px"><div style="width:4px;height:0;border:5px solid #68BC31;overflow:hidden"></div></div></td><td class="legendLabel">social networks</td></tr><tr><td class="legendColorBox"><div style="border:1px solid null;padding:1px"><div style="width:4px;height:0;border:5px solid #2091CF;overflow:hidden"></div></div></td><td class="legendLabel">search engines</td></tr><tr><td class="legendColorBox"><div style="border:1px solid null;padding:1px"><div style="width:4px;height:0;border:5px solid #AF4E96;overflow:hidden"></div></div></td><td class="legendLabel">ad campaings</td></tr><tr><td class="legendColorBox"><div style="border:1px solid null;padding:1px"><div style="width:4px;height:0;border:5px solid #DA5430;overflow:hidden"></div></div></td><td class="legendLabel">direct traffic</td></tr><tr><td class="legendColorBox"><div style="border:1px solid null;padding:1px"><div style="width:4px;height:0;border:5px solid #FEE074;overflow:hidden"></div></div></td><td class="legendLabel">other</td></tr></tbody></table></div></div>
			
			<div class="hr hr8 hr-double"></div>
			
			<div class="clearfix">
				<div class="grid3">
					<span class="grey">${uiLabelMap.OrderGrossDollarAmountsIncludesAdjustmentsAndPendingOrders}</span>
					<h4 class="bigger pull-right">${weekItemTotal}</h4>
				</div>
				
				<div class="grid3">
					<span class="grey">${uiLabelMap.OrderPaidDollarAmountsIncludesAdjustments}</span>
					<h4 class="bigger pull-right">${weekItemTotalPaid}</h4>
				</div>
				
				<div class="grid3">
					<span class="grey">${uiLabelMap.OrderPendingPaymentDollarAmountsIncludesAdjustments}</span>
					<h4 class="bigger pull-right">${weekItemTotalPending}</h4>
				</div>
			</div>
		 </div><!--/widget-main-->
		</div><!--/widget-body-->
	</div>