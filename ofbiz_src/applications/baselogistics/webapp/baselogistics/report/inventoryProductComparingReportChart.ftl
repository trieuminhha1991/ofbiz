<div id="olbiusInventoryProductComparing" class="margin-top30 container-chart-inner-page"></div>
<script type="text/javascript">
	var dataFilterTop = ["5", "10", "15", "20"];
	var dataFilterSortQty = [
		{'text': '${StringUtil.wrapString(uiLabelMap.ExportHighest)}', 'value': 'EXPORT_DESC'},
		{'text': '${StringUtil.wrapString(uiLabelMap.ExportLowest)}', 'value': 'EXPORT_ASC'},
		{'text': '${StringUtil.wrapString(uiLabelMap.ReceiveHighest)}', 'value': 'RECEIVE_DESC'},
		{'text': '${StringUtil.wrapString(uiLabelMap.ReceiveLowest)}', 'value': 'RECEIVE_ASC'}
	];
	
	$(function(){
		var configData = {};
		var config = {
            id: "olbiusInventoryProductComparing",
            olap: "olapInventoryProductTotal",
            
            chart: {
                type: 'column'
            },
            title: {
            	text: '${StringUtil.wrapString(uiLabelMap.ComparingExportAndRecieveChart)}',
                x: -20 //center
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: -30,
                    style: {
                        fontSize: '11px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                },
                title : {
                    text: null
                }
            },
            yAxis: {
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#000000'
                }],
                title : {
                    text: null
                },
                min: 0
            },
            legend: {
                enabled: true,
                labelFormatter: function () {
                	if(this.name == "RECEIVE") {
                		return "<span title=\"clickToHide\">${StringUtil.wrapString(uiLabelMap.RECEIVE)}</span>";
                	} else if(this.name == "EXPORT") {
                		return "<span title=\"clickToHide\">${StringUtil.wrapString(uiLabelMap.EXPORT)}</span>";
                	}
	            }
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} </b><br/>',
                formatter: function() {
				    var pre = "";
				    var full = "";
				    if (this.point.name) {
				    } else {
				    	if (this.series.name == "RECEIVE"){
				    		pre = "${StringUtil.wrapString(uiLabelMap.RECEIVE)}";
				    	} else if (this.series.name == "EXPORT"){
				    		pre = "${StringUtil.wrapString(uiLabelMap.EXPORT)}";
				    	}
				        full = pre + ' : ' + '<b>' + formatnumber(this.y) + '</b>';
				    }
				    return full;
				}
            },
            plotOptions: {
	            series: {
	                maxPointWidth: 30
	            }
	        },
	        height: 0.35,
	        chartRender : OlbiusUtil.getChartRender('defaultColumnFunc'),
           	popup: [
           		{
                    group: "dateTime",
                    id: "dateTime",
                    params: {
                    	index: 5,
                    	dateTypeIndex: 2,
                    	fromDate: past_date,
                    	thruDate: cur_date
                    }
                },
                {
                    action: 'jqxGridMultiple',
                    params: {
                        id: 'facilityId',
                        label: "${StringUtil.wrapString(uiLabelMap.Facility)}",
                        grid: {
        	            	source: LocalData.array.facilities,
        	            	id: "value",
        	            	width: 550,
        	            	sortable: true,
        	                pagesize: 5,
        	                columnsresize: true,
        	                pageable: true,
        	                altrows: true,
        	                showfilterrow: true,
        	                filterable: true,
        	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.FacilityId)}", datafield: 'value', width: 150 }, 
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.FacilityName)}", datafield: 'text' }
	            	        ]
        	            }
                    },
                    hide: false
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterTop',
                        label: "${StringUtil.wrapString(uiLabelMap.BSTop)}",
                        source: dataFilterTop,
                        selectedIndex: 0
                    }
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterSort',
                        label: "${StringUtil.wrapString(uiLabelMap.olap_filter)}",
                        source: dataFilterSortQty,
                        selectedIndex: 0
                    }
                },
			],
            
            apply: function (grid, popup) {
           	 	var dateTimeData = popup.group("dateTime").val();
            	return $.extend({
                	'filterTop': popup.element("filterTop").val(),
                	'filterSort': popup.element("filterSort").val(),
                	'facility': popup.element("facilityId").val(),
					olapType: 'COLUMNCHART',
                }, dateTimeData);
            }
        };
		OlbiusUtil.chart(config);
	});
</script>