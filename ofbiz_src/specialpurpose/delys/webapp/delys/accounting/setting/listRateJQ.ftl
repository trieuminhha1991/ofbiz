
<#assign dataField="[	{ name: 'rateDescription', type: 'string' },
						 { name: 'periodDescription', type: 'string' },
						 { name: 'firstName', type: 'string' },
						 { name: 'middleName', type: 'string' },
						 { name: 'lastName', type: 'string'},
						 { name: 'groupName', type: 'string'},
						 { name: 'employeePositionDescription', type: 'string'},
						 { name: 'workEffortName', type: 'string'},
						 { name: 'emplPositionTypeId', type: 'string'},
						 { name: 'rateAmount', type: 'number'},
						 { name: 'periodTypeId', type: 'string'},
						 { name: 'workEffortId', type: 'string'},
						 { name: 'rateTypeId', type: 'string'},		
						 { name: 'fromDate', type: 'date', other: 'Timestamp'},
						 { name: 'rateCurrencyUomId', type: 'string' },
						 { name: 'partyId', type: 'string' },
						 { name: 'rateTypeId', type: 'number' },
						 { name: 'fullName', type: 'string'}
						 ]
						 "/>
						 
<#assign columnlist="{ text: '${uiLabelMap.RateDescription}', datafield: 'rateDescription', width: 100},
					 { text: '${uiLabelMap.PeriodTypeId}', datafield: 'periodDescription', width: 190},
					 { text: '${uiLabelMap.PartyId}', width:300, datafield: 'fullName'},
                     {text : 'rateCurrencyUomId', datafield: 'rateCurrencyUomId' , hidden: true},
                     {text : 'periodTypeId', datafield: 'periodTypeId' , hidden: true},
                     {text : 'workEffortId', datafield: 'workEffortId' , hidden: true},
                     {text : 'fromDate', datafield: 'fromDate' , hidden: true},
                     {text : 'rateTypeId', datafield: 'rateTypeId' , hidden: true},
                     { text: '${uiLabelMap.FormFieldTitle_workEffortId}', datafield: 'workEffortName', width: 150},
                     { text: '${uiLabelMap.EmplPositionTypeId}', datafield: 'emplPositionTypeId', width: 150 ,cellsrenderer : function(row){
                     	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
                     	for(var key in dataLEPT){
                     		if(dataLEPT[key].emplPositionTypeId == data.emplPositionTypeId){
                     			return '<span>' +dataLEPT[key].description+ '</span>';
                     		}
                     	}
                     	return '<span>' + data.emplPositionTypeId+ '</span>';
                     }},
                     { text: '${uiLabelMap.RateAmount}', datafield: 'rateAmount',filtertype : 'number',cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.rateAmount,data.rateCurrencyUomId) + \"</span>\";
					 	}}
				
					 "/>
<@jqGrid url="jqxGeneralServicer?sname=JQListRates" dataField=dataField columnlist=columnlist
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" addrefresh="true"
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow" createUrl="jqxGeneralServicer?jqaction=C&sname=updateRateAmount"
		 addColumns="rateAmount(java.math.BigDecimal);rateDescription;partyId;emplPositionTypeId;workEffortId;rateCurrencyUomId;rateTypeId"
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=deleteRateAmount&jqaction=D" 
		 deleteColumn="emplPositionTypeId;periodTypeId;workEffortId;fromDate(java.sql.Timestamp);rateCurrencyUomId;partyId;rateTypeId" 
 />
  <div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.RateType}
    				</div>
    				<div class='span7'>
    					<div id="rateTypeIdAdd"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.RateAmount}
    				</div>
    				<div class='span7'>
						<div id="rateAmountAdd"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.PartyId}
    				</div>
    				<div class='span7'>
    					<div id="partyFilterIdFrom">
				            <div style="border-color: transparent;" id="jqxPartyFromFilterGrid">
				            </div>
		        		</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_workEffortId}
    				</div>
    				<div class='span7'>
    					<div id="workIdEffortAdd">
				            <div style="border-color: transparent;" id="jqxWorkEffortFilterGrid">
				            </div>
			        	</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.PeriodTypeId}
    				</div>
    				<div class='span7'>
    					<div id="periodTypeIdAdd"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.currencyUomId}
    				</div>
    				<div class='span7'>
    					<div id="currencyUomIdAdd"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.EmplPositionTypeId}
    				</div>
    				<div class='span7'>
    					<div id="emplPositionTypeIdAdd"></div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>	
<#assign isGetListEmpl="true"/>
 <script src="/delys/images/js/generalUtils.js"></script>
<#include "component://delys/webapp/delys/accounting/initGeneralDropdown.ftl"/>
<#include "component://delys/webapp/delys/accounting/popup/popupGridPartyGeneralFilter.ftl"/>
<script type="text/javascript">
	
	var dataLRT = new Array();
	dataLRT = [
	<#if listRateType?exists && listRateType?has_content>
		<#list listRateType as type>
			{
				'rateTypeId' : '${type.rateTypeId?if_exists}',
				'description' : '${StringUtil.wrapString(type.get('description',locale)?default(''))}'
			},
		</#list>
	</#if>		
	]
	
	var dataLPT = new Array();
	dataLPT = [
	<#if listPeriodType?exists && listPeriodType?has_content>
		<#list listPeriodType as type>
			{
				'periodTypeId' : '${type.periodTypeId?if_exists}',
				'description' : "${StringUtil.wrapString(type.get("description",locale))}"
			},
		</#list>	
	</#if>	
	]
	
	var dataLC = new Array();
	dataLC = [
	<#if listCurrency?exists && listCurrency?has_content>
		<#list listCurrency as type>
			{
				'uomId' : '${type.uomId?if_exists}',
				'description' : "${StringUtil.wrapString(type.get("description",locale))}"
			},
		</#list>	
	</#if>	
	]
	
	var dataLEPT = new Array();
	dataLEPT = [
	<#if listEmplPositionType?exists && listEmplPositionType?has_content>
		<#list listEmplPositionType as type>
			{
				'emplPositionTypeId' : '${type.emplPositionTypeId?if_exists}',
				'description' : "${StringUtil.wrapString(type.get("description",locale))}"
			},
		</#list>	
	</#if>	
	]
	
</script> 
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;


	var action = (function(){
		 var initElement = function(){
		 	$('#rateTypeIdAdd').jqxDropDownList({theme:theme, width  :250, source: dataLRT, displayMember: "description", valueMember: "rateTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$('#periodTypeIdAdd').jqxDropDownList({autoDropDownHeight : true,theme:theme, width  :250,  source: dataLPT, displayMember: "description", valueMember: "periodTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$('#currencyUomIdAdd').jqxDropDownList({theme:theme,  width  :250, source: dataLC,filterable : true, displayMember: "description", valueMember: "uomId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$('#emplPositionTypeIdAdd').jqxDropDownList({theme:theme,filterable : true,  width  :250, source: dataLEPT, displayMember: "description", valueMember: "emplPositionTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$('#rateAmountAdd').jqxNumberInput({ width: '245', inputMode: 'simple', spinButtons: true });
			initDropDownGrid();
			initjqxWindow();
		 }
		 
		var initjqxWindow = function(){
			$("#alterpopupWindow").jqxWindow({
			        width: 600, height : 400,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
			    });
		}
		
		var initDropDownGrid = function(){
			initWorkEffortSelect($('#workIdEffortAdd'),$('#jqxWorkEffortFilterGrid'),{width : 500,dropDownHorizontalAlignment : true });
			initPartySelect($("#partyFilterIdFrom"),$("#jqxPartyFromFilterGrid"),{dropDownHorizontalAlignment : true});
			$('#workIdEffortAdd').jqxDropDownButton('width',250);
			$('#partyFilterIdFrom').jqxDropDownButton('width',250)
		}
		
		var save = function(){
			var row;
		        row = {
		        		rateTypeId: $('#rateTypeIdAdd').val(),
		        		rateCurrencyUomId: $('#currencyUomIdAdd').val(),
		        		rateAmount: $('#rateAmountAdd').val(),
		        		periodTypeId: $('#periodTypeIdAdd').val(),
		        		emplPositionTypeId: $('#emplPositionTypeIdAdd').val(),
		        		workEffortId : $('#workIdEffortAdd').val(),
		        		partyId:$('#partyFilterIdFrom').val()        
		        	  };
        	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		      return true;
		}
		
		var clear = function(){
			GridUtils.clearForm($('#alterpopupWindow'));
		}
		
		var bindEvent = function(){
			$("#save").click(function () {
				if(save()) $("#alterpopupWindow").jqxWindow('close');
			});
			$("#saveAndContinue").click(function () {
				if(save()) return;
			});
			
			$('#alterpopupWindow').on('close',function(){
				clear();
			})
		}
	
		return {
			init : function(){
				initElement();
				bindEvent();
			}
		}
	}())
	$(document).ready(function(){
		action.init();
	})
</script>
