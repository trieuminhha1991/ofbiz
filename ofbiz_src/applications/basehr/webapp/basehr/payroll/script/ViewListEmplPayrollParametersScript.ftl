<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>		
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>				
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>	
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>	
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(monthStart, timeZone, locale)/>
<script type="text/javascript">
	var periodTypeArr = [
   		<#if periodTypeList?has_content> 	                     	
   			<#list periodTypeList as periodType>
   				{
   					periodTypeId: "${periodType.periodTypeId}",
   					description: "${StringUtil.wrapString(periodType.description?if_exists)}" 
   				},
   			</#list>
   		</#if>	
   	];

   	var codeArr = [
   		<#if payrollParam?has_content>
   			<#list payrollParam as param>
   				{
   					code: "${param.code}",
   					description: '${StringUtil.wrapString(param.name)}',
   					type: "${StringUtil.wrapString(param.type)}",
   					periodTypeId: '${param.periodTypeId}'
   				},
   			</#list>
   		</#if>
   	];
   	
   	<#assign rowDetails = "function (index, parentElement, gridElement, datarecord){
		var partyId = datarecord.partyId;
		var id = datarecord.uid.toString();
		var urlStr = 'getEmplParamCharacteristic';
	 	var tabsdiv = $($(parentElement).children()[0]);
	 	if(tabsdiv != null){
	 		var bonusEmpl = tabsdiv.find('.bonusEmpl');
	 		var allowance = tabsdiv.find('.allowanceEmpl');
	 		var selection = $('#dateTimeInput').jqxDateTimeInput('getRange');
	 		var fromDate = selection.from.getTime();
	    	var thruDate = selection.to.getTime();
	    	
	    	var bonusDatafield = [
					{name: 'code', type: 'string'},				           
					{name: 'value', type: 'number'},
					{name: 'fromDate', type: 'date'},
					{name: 'thruDate', type: 'date'},
					{name: 'orgId', type: 'string'},
					{name: 'groupName', type: 'string'}
			];
	    	
	    	var allowanceDatafield = [
					{name: 'code', type: 'string'},				           
					{name: 'value', type: 'number'},
					{name: 'fromDate', type: 'date'},
					{name: 'thruDate', type: 'date'},
					{name: 'orgId', type: 'string'},
					{name: 'groupName', type: 'string'}
			];
				
	    	var columnlistBonus = [
 	   		        	{text: '${StringUtil.wrapString(uiLabelMap.TypeBonus)}', width: 230, datafield: 'code',
 			        		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
 			        			for(var i = 0; i < codeArr.length; i++){
 			        				if(codeArr[i].code == value){
 			        					return '<span title=\"' + value + '\">' + codeArr[i].description + '</span>';
 			        				}
 			        			}
 			        			return '<span>' + value + '</span>';
 			        		}
 			        	},
 			        	{text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', filterable: false,editable: false, 
 			        		cellsalign: 'left', width: 120, cellsformat: 'dd/MM/yyyy ', columntype: 'datetimeinput'},
 			        	{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', filterable: false,editable: false, 
 				        		cellsalign: 'left', width: 120, cellsformat: 'dd/MM/yyyy ', columntype: 'datetimeinput'},
 				        {text: '${StringUtil.wrapString(uiLabelMap.OrganizationPaid)}', width: 200, datafield:'groupName'},			
 		        		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}', datafield: 'value',
 				        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
 								return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
 							}	
 			        	},
 			        	{datafield: 'orgId', hidden: true},
 			        	
 			];
	    	
	    	var columnlistAllowance = [
   		        	{text: '${StringUtil.wrapString(uiLabelMap.AllowancesType)}', width: 230, datafield: 'code',
		        		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		        			for(var i = 0; i < codeArr.length; i++){
		        				if(codeArr[i].code == value){
		        					return '<span title=\"' + value + '\">' + codeArr[i].description + '</span>';
		        				}
		        			}
		        			return '<span>' + value + '</span>';
		        		}
		        	},
		        	{text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', filterable: false,editable: false, 
		        		cellsalign: 'left', width: 120, cellsformat: 'dd/MM/yyyy ', columntype: 'datetimeinput'},
		        	{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', filterable: false,editable: false, 
			        		cellsalign: 'left', width: 120, cellsformat: 'dd/MM/yyyy ', columntype: 'datetimeinput'},
			        {text: '${StringUtil.wrapString(uiLabelMap.OrganizationPaid)}', width: 200, datafield:'groupName'},		
	        		{text: '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}', datafield: 'value',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
						}	
		        	},
		        	{datafield: 'orgId', hidden: true},
			];
	    	
	    	var configBonus = {
	    	   		width: '100%', 
	    	   		height: 170,
	    	   		autoheight: false,
	    	   		virtualmode: true,
	    	   		filterable: false,
	    	   		showtoolbar: false,
	    	   		selectionmode: 'singlerow',
	    	   		pageable: true,
	    	   		sortable: false,
	    	        filterable: false,
	    	        editable: false,
	    	        rowsheight: 26,
	    	        selectionmode: 'singlerow',
	    	        url: 'getEmplParameters&hasrequest=Y&partyId='+partyId+'&fromDate='+fromDate+'&thruDate='+thruDate+'&paramCharacteristicId=THUONG',                
	    	        source: {}
	    	};

	    	var configAllowance = {
	    	   		width: '100%', 
	    	   		height: 170,
	    	   		autoheight: false,
	    	   		virtualmode: true,
	    	   		showtoolbar: false,
	    	   		selectionmode: 'singlerow',
	    	   		pageable: true,
	    	   		sortable: false,
	    	        filterable: false,
	    	        editable: false,
	    	        rowsheight: 26,
	    	        url: 'getEmplParameters&hasrequest=Y&partyId='+partyId+'&fromDate='+fromDate+'&thruDate='+thruDate+'&paramCharacteristicId=PHU_CAP',                
	    	        source: {}
	    	};
	    	Grid.initGrid(configBonus, bonusDatafield, columnlistBonus, null, bonusEmpl);
	    	Grid.initGrid(configAllowance, allowanceDatafield, columnlistAllowance, null, allowance);
	    	$(tabsdiv).jqxTabs({ width: '950px', height: 220});
	 	}
	}
	"/>
	<#assign rowdetailstemplateAdvance = "<ul style='margin-left: 30px;'><li class='title'>${StringUtil.wrapString(uiLabelMap.HRCommonBonus)}</li><li class='title'>${StringUtil.wrapString(uiLabelMap.HREmplAllowances)}</li></ul><div class='bonusEmpl'></div><div class='allowanceEmpl'></div>"/>
	function refreshGridData(partyGroupId, fromDate, thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=getEmplBonusAllowances&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	}	
	
	<#if !rootOrgList?exists>
	<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
	</#if>
	var globalVar = {
			rootPartyArr: [
	   			<#if rootOrgList?has_content>
	   				<#list rootOrgList as rootOrgId>
	   				<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
	   				{
	   					partyId: "${rootOrgId}",
	   					partyName: "${rootOrg.groupName}"
	   				},
	   				</#list>
	   			</#if>
	   		],
			nowTimestamp: ${nowTimestamp.getTime()},
			monthStart: ${monthStart.getTime()},
			monthEnd: ${monthEnd.getTime()}
	};

	var globalObject = (function(){
		<#assign defaultSuffix = ""/>
		${setContextField("defaultSuffix", defaultSuffix)}
		<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
		return{
			createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
		}
	}());
</script>
