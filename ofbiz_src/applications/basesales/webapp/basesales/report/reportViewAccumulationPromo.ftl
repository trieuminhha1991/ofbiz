<style>
	.olbiusChartContainer, .olbiusGridContainer{
		margin-bottom: 50px!important;
	}
</style>
<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";

	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
</script>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>

<div class="grid">
	<script id="test">
		$(function(){
	        var config = {
				sortable: true,
		    	filterable: true,
		    	showfilterrow: true,
	            title: '${uiLabelMap.BSAccumulationPromo}',
	            service: 'salesOrder',
	            columns: [
	        		{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			    	  datafield: 'stt', columntype: 'number', width: '3%',
			    	  cellsrenderer: function (row, column, value) {
			    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  		}
			 		},   
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductPromoId)}', datafield: 'product_promo_id', type: 'string', width: '20%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSPromoName)}', datafield: 'product_promo_name', type: 'string', width: '20%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSUseLimitPerPromotion)}', datafield: 'user_limit', type: 'number', width: '19%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSJoinCustomer)}', datafield: 'count_cus', type: 'number', width: '19%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSPassCustomer)}', datafield: 'count_pass', type: 'number', width: '19%', cellsformat: 'f0', cellsalign: 'right', filterable: false}
	            ]
	        };
	
	        var configPopup = [
	        	{
		            action : 'addDateTimeInput',
		            params : [{
		                id : 'from_date',
		                label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
		                value: OLBIUS.dateToString(past_date),
		             	disabled: true,
		            }],
		            before: 'thru_date'
		        },
		        {
		            action : 'addDateTimeInput',
		            params : [{
		                id : 'thru_date',
		                label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
		                value: OLBIUS.dateToString(cur_date),
		                disabled: true,
		            }],
		            after: 'from_date'
		        },
		        {
		            action : 'addDateTimeInput',
		            params : [{
		                id : 'from_date_1',
		                label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
		                value: OLBIUS.dateToString(past_date),
		                hide: true,
		            }],
		            before: 'thru_date_1'
		        },
		        {
		            action : 'addDateTimeInput',
		            params : [{
		                id : 'thru_date_1',
		                label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
		                value: OLBIUS.dateToString(cur_date),
		                hide: true,
		            }],
		            after: 'from_date_1'
		        },
		        {
			        action : 'addDropDownList',
			        params : [{
			            id : 'customTime',
			            label : '${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}',
			            data : customDate,
			            index: 3,
			        }],
		            event : function(popup) {
		                popup.onEvent('customTime', 'select', function(event) {
		                    var args = event.args;
		                    var item = popup.item('customTime', args.index);
		                    var filter = item.value;
		                    popup.clear('from_date');
		                    popup.clear('thru_date');
		                    if(filter == 'oo') {
		                        popup.show('from_date_1');
		                        popup.show('thru_date_1');
		                        popup.hide('from_date');
		                        popup.hide('thru_date');
		                    } else {
		                    	popup.show('from_date');
		                        popup.show('thru_date');
		                    	popup.hide('from_date_1');
		                        popup.hide('thru_date_1');
		                    }
		                    popup.resize();
		                });
		            }
			    },
	        ];
	
	
	        gGridC = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateAPGrid', true);
	
	        gGridC.funcUpdate(function (oLap) {
	            oLap.update({
	                'fromDate': oLap.val('from_date_1'),
	                'thruDate': oLap.val('thru_date_1'),
	                'customTime': oLap.val('customTime'),
	            });
	        });
	
	        gGridC.init(function () {
	        	gGridC.runAjax();
	        }, false, function(oLap){
	        	var dataAll = oLap.getAllData();
	        	if(dataAll.length != 0){
	            	var fromDateInput = oLap.val('from_date_1');
	            	var thruDateInput = oLap.val('thru_date_1');
	            	var dateFromDate = new Date(fromDateInput);
	            	var dateThruDate = new Date(thruDateInput);
	            	var dateFrom = dateFromDate.getTime();
	            	var thruFrom = dateThruDate.getTime();
	            	
	            	window.location.href = "exportTurnoverProChaReportToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom;
	        	}else{
	        		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
	        		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
	        		    "class" : "btn-small btn-primary width60px",
	        		    }]
	        		   );
	        	}
	    	});
	    });
	</script>
</div>
