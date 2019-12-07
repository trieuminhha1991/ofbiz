/**
 * init common object highchart for apps
 * @param all configs for highchart,default with default options base highchart
 * 
 * 
 * */
(function(w){
	
	if(w._chart !== undefined)
		return;
	
	 var _chart = function(){};	
	
	 _chart.prototype = {
			 
			 _object : null,
			 _config : {},
			 getObjectChart :  function(){
				 return this._object
			 },
			 setObjectChart : function($instance){
				 if($instance == null || typeof $instance == "undefined")
					 return;
				 this._object = $instance;
			 },
			 getConfigDefault : function(){
				 return {
				        chart: {
				            type: 'column'
				        },
				        title: {
				            text: ''
				        },
				        subtitle: {
				            text: ''
				        },
				        xAxis: {
				            categories: [],
				            crosshair: true
				        },
				        yAxis: {
				            min: 0,
				            title: {
				                text: ''
				            }
				        },
				        tooltip: {
				            headerFormat: '<span style="font-size:10px"></span><table>',
				            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
				                '<td style="padding:0"><b>{point.y:,.1f}</b></td></tr>',
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
				        series: []
				    };
			 },
			 refresh : function(chart){
				 var seriesLength = chart.series.length;
				  for(var i = seriesLength -1; i > -1; i--) {
			            chart.series[i].remove();
			        }
			  return;
			 },
			 prepareData : function(chart,data){
				 if(!data) return;
				 
				 if(_.isEmpty(data.xAxis) || _.isEmpty(data.yAxis))
				{
					 this.refresh(chart);
					 return;
				} 
					 
				 chart.series = [];
				 var color  = 0;
				 if(_.has(data,'yAxis'))
					 angular.forEach(data['yAxis'],function(val,i){
						 chart.addSeries({
							 	name : i,
							 	data : val,
							 	color : Highcharts.getOptions().colors[color++]
						 })
					 })
					 
				 if(_.has(data,'xAxis'))
				{
					 chart.xAxis[0].update({
						showInLegend: false,
						labels: {
			                    enabled: true
		                },
	                	categories: data.xAxis
					 },false);
				} 
				 
				 chart.redraw();	 
			 },
			 setConfig : function($config){
				 if($config == null  || typeof $config == "undefined")
					 return;
				 this._config = $config;
			 },
			 getConfig : function(){
				 if(typeof $ != 'function')
					 throw new Error('not init jQuery,check Again');
				 if(_.has(this._config,'object'))
					 delete this._config['object']
				 
				 return angular.extend(this.getConfigDefault(),this._config);
			 },
			 initChart : function(_config){
				 this.setObjectChart(_config.object);
				 
				 if(!Highcharts)	
					 return;
					 
				 $(this.getObjectChart()).highcharts(this.getConfig(this.setConfig(_config)))
				 
				 var _chart = $(this.getObjectChart()).highcharts();
				 
				 this.prepareData(_chart,_config.data);
				 
				return _chart;
			 },
			 run : function(_config){
			 	return this.initChart(_config);
			 }
	 }
	 
	 w._chart = new _chart();
	
}(window))
