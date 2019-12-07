<#include "script/ViewORGPayrollSummaryReportScript.ftl"/>
<#--<!-- <#include "script/overideConfigPara.ftl" /> -->

<script type="text/javascript" src="/hrresources/js/popup.extends.js"></script>
<script type="text/javascript" src="/poresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/poresources/js/popup_extend_grid.js"></script>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<style type="text/css">
.aquaCell{
	background-color: aqua !important;
}
.bisqueCell{
	background-color: bisque !important;
}
.azureCell{
	background-color: azure !important;
}
.yellowCell{
	background-color: yellow !important;
}
div[id^="statusbartreeGrid"] {
 width: 0 !important;
}
</style>
<script type="text/javascript" id="grid">
	$(function(){
		/* var config = {
				//FIXME CHUA TEST SERVICE
				service : 'payrollJob',
				title : '${StringUtil.wrapString(uiLabelMap.ORGPayrollSummaryReport)}',
				columns : []
		}; */
		
		var columns = [
				{text : '${StringUtil.wrapString(uiLabelMap.DepartmentCode)}', datafield : {name: 'partyCode', type: 'string'}, width : '13%'},
				{datafield : {name: 'partyId', type: 'string'}, hidden: true},
				{text : '${StringUtil.wrapString(uiLabelMap.HROrganization)}', datafield : {name: 'departmentName', type: 'string'}, width : '18%'},
				{text : '${StringUtil.wrapString(uiLabelMap.FromDate)}', datafield: {name: 'fromDate', type: 'date'}, width : '13%', filterable: false,
					cellsRenderer: function (row, column, value) {
	                    	if (value) {
	                			value = new Date(value).toTimeOlbius();
							}
					        return "<div class=\"text-right\">" + value + "</div>";
					    }
		           },
		           {text : '${StringUtil.wrapString(uiLabelMap.ThruDate)}', datafield: {name: 'thruDate', type: 'date'}, width : '13%', 
		        	   filterable: false,
		        	   cellsRenderer: function (row, column, value) {
	                    	if (value) {
	                			value = new Date(value).toTimeOlbius();
							}
					        return "<div class=\"text-right\">" + value + "</div>";
					    }
		           }, 
		           <#if payrollItemTypeList?has_content>
		           	   <#list payrollItemTypeList as payrollItemType>
			           	   {text: '${StringUtil.wrapString(payrollItemType.description)}', datafield: {name: '${payrollItemType.payroll_item_type_id}_item', type: 'number'}, width: '13%',
			           			columngroup : 'INCOME', filterable: false,
			           			cellsRenderer: function(row, column, value){
									if(typeof(value) == 'number'){
										return '<span style=\"text-align: right\">' + formatcurrency(value) + '<span>';
									}
								}
			           	   },
		           	   </#list>
		           </#if>
		           {text: '${StringUtil.wrapString(uiLabelMap.SumSalary)}', datafield: {name: 'totalIncome', type: 'number'}, width: '13%',
	           			 columngroup : 'INCOME', filterable: false,
	           			cellsrenderer: function(row, column, value){
							if(typeof(value) == 'number'){
								return '<span style=\"text-align: right\">' + formatcurrency(value) + '<span>';
							}
						},
						cellclassname: function (row, column, value, data) {
						    return 'bisqueCell';
						}
						
	           	   },
	           	   <#if payrollFormulaByCharList?has_content>
			           	<#list payrollFormulaByCharList as payrollFormulaByChar>
			           	   {text: '${StringUtil.wrapString(payrollFormulaByChar.name)}', datafield: {name: '${payrollFormulaByChar.code}_code', type: 'number'}, width: '13%',
			           			columngroup : 'ORG_PAID' , filterable: false,
			           			cellsRenderer: function(row, column, value){
									if(typeof(value) == 'number'){
										return '<span style=\"text-align: right\">' + formatcurrency(value) + '<span>';
									}
								}
			           	   },
		        	   </#list>
	           	   </#if>
		           {text: '${StringUtil.wrapString(uiLabelMap.SumSalary)}', datafield: {name: 'totalOrgPaid', type: 'number'}, width: '13%',
		        	   columngroup : 'ORG_PAID',
	           			cellsrenderer: function(row, column, value){
							if(typeof(value) == 'number'){
								return '<span style=\"text-align: right\">' + formatcurrency(value) + '<span>';
							}
						},
						cellclassname: function (row, column, value, data) {
						    return 'aquaCell';
						}
	           	   },
		];
		
		var columngroup = [
              {text : '${StringUtil.wrapString(uiLabelMap.PaidForEmplSalary)}', name : 'INCOME', align: 'center'},
              {text : '${StringUtil.wrapString(uiLabelMap.OrgDetailPaid)}', name : 'ORG_PAID', align: 'center'}
        ];
		 var config_popup = [
			{
                action : 'jqxDateTimeInput',
                params : {
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date),
                 	disabled: true,
                },
                before: 'thru_date'
            },
            {
                action : 'jqxDateTimeInput',
                params : {
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date),
                    disabled: true,
                },
                after: 'from_date'
            },
            {
                action : 'jqxDateTimeInput',
                params : {
                    id : 'from_date_1',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date),
                },
                hide: true,
                before: 'thru_date_1'
            },
            {
                action : 'jqxDateTimeInput',
                params : {
                    id : 'thru_date_1',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date),
                },
                hide: true,
                after: 'from_date_1'
            },
            {
		        action : 'jqxDropDownList',
		        params : {
		            id : 'customTime',
		            label : '${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}',
		            source: customDate,
		           // data : customDate,
		            selectedIndex: 0
		        },
		        event: [{
					name: 'select',
					action: function (p, e) {
						var item = e.element.jqxDropDownList('getSelectedItem');
                        var value = item.value;
						var popup = p.element;
                        if(value == 'oo') {
                            popup('from_date_1').show();
                            popup('thru_date_1').show();
                            popup('from_date').hide();
                            popup('thru_date').hide();
                        } else {
                        	popup('from_date').show();
                            popup('thru_date').show();
                        	popup('from_date_1').hide();
                            popup('thru_date_1').hide();
                        }
					}
		        }]
		    },
		    /* {
            	action : 'addJqxTree',
            	params : [{
            		id : 'organization',
            		label : '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}',
            		source : globalVar.rootPartyArr,
            		value: globalVar.rootPartyArr[0]['partyId']
            	}],
            }*/
        ]; 
      
		var payrollTreeGrid = OlbiusUtil.treeGrid({
			service: 'payrollJob',
			button: 'button',
			id: 'grid',
			columns: columns,
			columnGroups: columngroup,
			url: 'getPayrollTableReportByOrg',
			hierarchy: {
				keyDataField: {name: 'partyId'},
				parentDataField: 'parentId'
			},
			pageable: true,
			pagerMode: 'advanced',
			theme: 'olbius',
			width: '100%',
			title: '${StringUtil.wrapString(uiLabelMap.ORGPayrollSummaryReport)}',
			columnsHeight: 30,
			showStatusbar: false,
			popup: config_popup,
 			apply: function (grid, popup) {
				var data = {
                	'fromDate' : popup.val('from_date_1'),
     				'thruDate' : popup.val('thru_date_1'),
     				'customTime' : popup.val('customTime'),
                } 
                return data; 
         	},
		});
	});
</script>