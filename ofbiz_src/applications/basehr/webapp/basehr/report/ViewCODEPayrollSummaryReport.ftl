<#include "script/ViewInsuranceRecordsReportScript.ftl"/>
<#include "script/overideConfigPara.ftl" />
<script type="text/javascript" src="/hrresources/js/popup.extends.js"></script>
<script type="text/javascript" src="/poresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/poresources/js/popup_extend_grid.js"></script>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<script type="text/javascript" id="grid">
	$(function(){
		var config = {
				//FIXME CHUA TEST SERVICE
				service : 'person',
				title : '${StringUtil.wrapString(uiLabelMap.CODEPayrollSummaryReport)}',
				columns : [
				       {text : '${StringUtil.wrapString(uiLabelMap.Name)}', datafield : 'name', width : '15%'},
			           {text : '${StringUtil.wrapString(uiLabelMap.Description)}', datafield : 'description'},
			           {text : '${StringUtil.wrapString(uiLabelMap.FromDate)}', datafield : 'fromDate', width : '15%',
			        	   cellsrenderer : function(row, columnfield, value){
			        		   if(value != null && value != undefined){
			        			   var date = new Date(value);
			        			   return '<span>' + date.toLocaleDateString('vi') + '</span>';
			        		   }else{
			        			   return '';
			        		   }
			        	   }
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.ThruDate)}', datafield : 'thruDate', width : '15%',
			        	   cellsrenderer : function(row, columnfield, value){
			        		   if(value != null && value != undefined){
			        			   var date = new Date(value);
			        			   return '<span>' + date.toLocaleDateString('vi') + '</span>';
			        		   }else{
			        			   return '';
			        		   }
			        	   }   
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.Value)}', datafield : 'value', width : '15%',
			        	   cellsrenderer: function(row, col, value){
			        		   if(value != null && value != undefined){
			        			   var value = new Number(value);
			        			   var options = {style : 'currency', currency : 'VND'};
			        			   value = value.toLocaleString('vi-VN', options);
			        			   var arr = value.split(/\s+/);
			        			   var tmp = arr[1]
			        			   arr[1] = arr[0];
			        			   arr[0] = tmp;
			        			   value = arr.join(' ');
			        			   return '<span>' + value + '</span>';
			        		   }else{
			        			   return '';
			        		   }
			        	   }
			           }
		       ]
		};
		
		var config_popup = [
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
		            index: 1,
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
            {
            	action : 'addDropDownListMultil',
            	params : [{
            		id : 'codeList',
            		label : '${StringUtil.wrapString(uiLabelMap.CodeList)}',
            		data : codeList,
            	}]
            }
        ];
		var payrollGrid = OLBIUS.oLapGrid('grid', config, config_popup, 'getPayrollTableReportByCode', true, true, OLBIUS.defaultLineFunc);
		payrollGrid.funcUpdate(function(oLap){
			gFromDate = oLap.val('from_date_1'),
            gThruDate = oLap.val('thru_date_1'),
			oLap.update({
				'fromDate' : gFromDate,
				'thruDate' : gThruDate,
				'codeList[]' : oLap.val('codeList'),
				'customTime' : oLap.val('customTime'),
			});
		});
		payrollGrid.init(function(){
			payrollGrid.runAjax();
		})
	});
</script>