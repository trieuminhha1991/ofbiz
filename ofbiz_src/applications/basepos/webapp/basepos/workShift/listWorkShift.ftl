<@jqGridMinimumLib/>
<#include "component://widget/templates/jqwLocalization.ftl" />
<style>
#actualCash{
	font-weight: bold;
	 color: #cc0000;
	 font-size: 24px;
	 height: 25px;
}
.labelAmount{
	font-size: 15px;
	padding-top: 5px;
}
</style>
<#assign dataField="[{ name: 'posTerminalStateId', type: 'string'},
					 { name: 'posTerminalId', type: 'string'},
					 { name: 'openedDate', type: 'date', other:'Timestamp', pattern:'HH:mm:ss dd-MM-yyyy'},
					 { name: 'closedDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd-MM-yyyy'},
					 { name: 'startingTxId', type: 'string'},
					 { name: 'endingTxId', type: 'string'},
					 { name: 'openedByUserLoginId', type: 'string'},
					 { name: 'startingDrawerAmount', type: 'number'},
					 { name: 'actualEndingCash', type: 'number'},
					 { name: 'actualReceivedAmount', type: 'number'},
					 { name: 'otherInCome', type: 'number'},
					 { name: 'otherCost', type: 'number'},
					 { name: 'amountTotal', type: 'number'},
					 { name: 'amountCash', type: 'number'},
					 { name: 'amountCard', type: 'number'},
					 { name: 'differenceAmount', type: 'number'},
					 { name: 'currency', type: 'string'},
				]"
/>

<#assign columngroups = "
		 { text: '${uiLabelMap.BPOSGrandTotal}', align: 'center', name: 'GrandTotal' }
		"/>

<#assign columnlist="{ text: '${uiLabelMap.BPOSWorkShiftId}', width: 100, datafield: 'posTerminalStateId', pinned: true },
                     { text: '${uiLabelMap.BPOSStartTime}', filtertype: 'range', width: 150, datafield: 'openedDate', cellsformat: 'HH:mm:ss dd-MM-yyyy', pinned: true },
					 { text: '${uiLabelMap.BPOSFinishTime}', filtertype: 'range', width: 150, datafield: 'closedDate',cellsformat: 'HH:mm:ss dd-MM-yyyy', pinned: true },
					 { text: '${uiLabelMap.BPOSFacility}', width: 150, datafield: 'posTerminalId'},
					 { text: '${uiLabelMap.BPOSStartTrans}', width: 150, datafield: 'startingTxId'},
					 { text: '${uiLabelMap.BPOSFinishTrans}', width: 150, datafield: 'endingTxId'},
					 { text: '${uiLabelMap.BPOSEmployee}', width: 150, datafield: 'openedByUserLoginId'},
					 { text: '${uiLabelMap.BPOSStartAmount}', width: 150, datafield: 'startingDrawerAmount', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgridListWorkShift').jqxGrid('getrowdata', row);
						 	if(data.startingDrawerAmount &&data.currency){
								return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(data.startingDrawerAmount, data.currency) + \"</div>\";
						 	}
					 	}
					 },
					 { text: '${uiLabelMap.BPOSFinishAmount}', width: 150, datafield: 'actualEndingCash', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
						 	var data = $('#jqxgridListWorkShift').jqxGrid('getrowdata', row);
						 	if(data.actualEndingCash && data.currency){
								return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(data.actualEndingCash, data.currency) + \"</div>\";
						 	}
					 	}
					 },

					 { text: '${uiLabelMap.BPOSOtherIncome}', width: 150, datafield: 'otherInCome', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
						 	var data = $('#jqxgridListWorkShift').jqxGrid('getrowdata', row);
						 	if(data.otherInCome && data.currency){
							 	return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(data.otherInCome, data.currency) + \"</div>\";
						 	}
					 	}
					 },
					 { text: '${uiLabelMap.BPOSOtherCost}', width: 150, datafield: 'otherCost', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
						 	var data = $('#jqxgridListWorkShift').jqxGrid('getrowdata', row);
						 	if(data.otherCost && data.currency){
							 	return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(data.otherCost, data.currency) + \"</div>\";
						 	}
					 	}
					 },
					 { text: '${uiLabelMap.BPOSPayCash}', width: 150, datafield: 'amountCash', cellsalign: 'right', filtertype: 'number', columngroup: 'GrandTotal',
						cellsrenderer: function (row, column, value) {
						 	var data = $('#jqxgridListWorkShift').jqxGrid('getrowdata', row);
						 	if( data.amountTotal && data.currency){
							 	return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(data.amountCash, data.currency) + \"</div>\";
						 	}
					 	}
					 },
					 { text: '${uiLabelMap.BPOSCreditCard}', width: 150, datafield: 'amountCard', cellsalign: 'right', filtertype: 'number', columngroup: 'GrandTotal',
						cellsrenderer: function (row, column, value) {
						 	var data = $('#jqxgridListWorkShift').jqxGrid('getrowdata', row);
						 	if( data.amountTotal && data.currency){
							 	return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(data.amountCard, data.currency) + \"</div>\";
						 	}
					 	}
					 },
					 { text: '${uiLabelMap.BPOSAcutalAmount}', width: 150, datafield: 'actualReceivedAmount', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
						 	var data = $('#jqxgridListWorkShift').jqxGrid('getrowdata', row);
						 	if(data.actualReceivedAmount && data.currency){
							 	return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(data.actualReceivedAmount, data.currency) + \"</div>\";
						 	}
					 	}
					 },
					 { text: '${uiLabelMap.BPOSDifference}', width: 150, datafield: 'differenceAmount', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
						 	var data = $('#jqxgridListWorkShift').jqxGrid('getrowdata', row);
						 	if(data.differenceAmount && data.currency){
							 	return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(data.differenceAmount, data.currency) + \"</div>\";
						 	}
					 	},
					 	aggregates: ['sum'],
                      	aggregatesrenderer: function (aggregates, column, element, summaryData) {
                          	var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%;'>\";
							$.each(aggregates, function (key, value) {

							renderstring += '<div style=\"color:red; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '${StringUtil.wrapString(uiLabelMap.BPOSTotalCost)}' + ': ' + formatcurrency(value, currencyDefault) + '</div>';
							});
							renderstring += \"</div>\";
							return renderstring;
						}
					 },
					 { text: '${uiLabelMap.BPOSTakeMoney}', width: 100, sortable: false, filterable: false,
						cellsrenderer: function(row, colum, value){
				  			return \"<div style='margin-left: 5px; margin-top: 5px;'><button class='btn-minier btn-primary' onclick='takeMoneyFromEmployee(\" + row + \")' ><i class='icon-money'></i></button></div>\";
						}
					 }
					 "/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetListWorkShift&organizationPartyId=${parameters.organizationPartyId?if_exists}" dataField=dataField columnlist=columnlist sortdirection="desc" defaultSortColumn="openedDate" jqGridMinimumLibEnable="false" filterable="true" filtersimplemode="true" showstatusbar="true" showtoolbar="true"
		 id="jqxgridListWorkShift" showlist="false" selectionmode="singlecell" showstatusbar="true" clearfilteringbutton="true" columngrouplist = columngroups />
		 
<div id="container" style="width: 100%; margin-top: 15px; background-color: #F2F2F2;
        border: 0px dashed #AAAAAA; overflow: auto;">
</div>

<div id="jqxNotification">
    <div id="notificationContent"></div>
</div>		 

<div id="takeMoneyFromEmployee" style="display:none;">
    <div>${uiLabelMap.BPOSTakeMoneyFromEmployee}</div>
    <div style="overflow: hidden;">
	<div class='row-fluid form-window-content'>
		<div class='span12'>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 align-right asterisk labelAmount'>
					${uiLabelMap.BPOSAmount}
				</div>
				<div class='span9'>
					<input type="text" name="actualCash" id="actualCash" style="width: 95%">
				</div>
			</div>
		</div>
	</div>
	<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right' onclick="cancelTakeMoneyFromEmployee()"><i class='icon-trash'></i> ${uiLabelMap.BPOSDelete}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right' onclick="processTakeMoneyFromEmployee()"><i class='icon-ok'></i> ${uiLabelMap.BPOSSave}</button>
				</div>
			</div>
		</div>
    </div>
</div>

<div id="otherIncomeWindow" style="display:none;">
    <div>${uiLabelMap.BPOSListOtherIncome}</div>
    <div style="overflow: hidden;">
	<div class='row-fluid'>
		<div id="listOtherIncome">
			
		</div>
	</div>
</div>

<div id="otherCostWindow" style="display:none;">
    <div>${uiLabelMap.BPOSListOtherCost}</div>
    <div style="overflow: hidden;">
	<div class='row-fluid'>
		<div id="listOtherCost">
			
		</div>
	</div>
</div>

<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var BPOSWorkShiftIsReceivedMoney = "${StringUtil.wrapString(uiLabelMap.BPOSWorkShiftIsReceivedMoney)}";
	var BPOSWorkShiftNotFinish = "${StringUtil.wrapString(uiLabelMap.BPOSWorkShiftNotFinish)}";
	var BPOSAreYouSureTakeMoneyFromEmployee = "${StringUtil.wrapString(uiLabelMap.BPOSAreYouSureTakeMoneyFromEmployee)}";
	var BPOSTakeMoneySuccess = "${StringUtil.wrapString(uiLabelMap.BPOSTakeMoneySuccess)}";
	var BPOSOK = "${StringUtil.wrapString(uiLabelMap.BPOSOK)}";
	var BPOSCancel = "${StringUtil.wrapString(uiLabelMap.BPOSCancel)}";
	$('#otherIncomeWindow').jqxWindow({
		width: 380, height: 400, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius'
	});
	$('#otherCostWindow').jqxWindow({
		width: 380, height: 400, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius'
	});
	var currencyDefault = "${currencyDefault}";
	$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	
	$("#jqxgridListWorkShift").on("celldoubleclick", function (event){
	    var args = event.args;
	    var dataField = args.datafield;
	    var rowBoundIndex = args.rowindex;
	    
	    if (dataField == 'otherInCome'){
	    	$("#otherIncomeWindow").jqxWindow('open');
	    	var data = $("#jqxgridListWorkShift").jqxGrid('getrowdata', rowBoundIndex);
	    	getListOtherIncome(data.posTerminalStateId);
	    }
	    
		if (dataField == 'otherCost'){
			$("#otherCostWindow").jqxWindow('open');
			var data = $("#jqxgridListWorkShift").jqxGrid('getrowdata', rowBoundIndex);
			getListOtherCost(data.posTerminalStateId)
	    }
	    
	});
	
	function getListOtherIncome(posTerminalStateId){
		var urlStr = 'jqxGeneralServicer?sname=JQGetListOtherIncome&posTerminalStateId=' + posTerminalStateId; 
		var sourceOtherIncome =
    	{
	        datafields:
	        [
	            { name: 'count', type: 'number' },
	            { name: 'description', type: 'string' },
	            { name: 'paidAmount', type: 'number' },
	        ],
	        cache: false,
	        root: 'results',
	        datatype: "json",
	        updaterow: function (rowid, rowdata) {
	           
	        },
	        beforeprocessing: function (data) {
	           
	        },
	        
	        pager: function (pagenum, pagesize, oldpagenum) {
	           
	        },
	        
	        sortcolumn: 'paidAmount',
			sortdirection: 'asc',
	        type: 'POST',
	        data: {
		        noConditionFind: 'Y',
		        conditionsFind: 'N',
		    },
		    pagesize:5,
	        contentType: 'application/x-www-form-urlencoded',
	        url: urlStr
    	};
		var dataAdapterOtherIncome = new $.jqx.dataAdapter(sourceOtherIncome);
		
		jQuery("#listOtherIncome").jqxGrid({
			source: dataAdapterOtherIncome,
			width: '100%',
	        height: 350,
	        filterable: false,
	        sortable: false,
			pageable: false,
	        sortable: false,
	        theme: 'energyblue',
	        selectionmode: 'singlerow',
	        localization: getLocalization(),
	        columns: [
	                     {
		                      text: '#', sortable: false, filterable: false, editable: false,
		                      groupable: false, draggable: false, resizable: false,
		                      datafield: '', columntype: 'number', width: 30,
		                      cellsrenderer: function (row, column, value) {
		                          return "<div style='margin:4px;'>" + (value + 1) + "</div>";
		                 	  }
                  		 },
	                     { text: '${uiLabelMap.BPOSReason}', datafield: 'description', width: 250},
	                     { text: '${uiLabelMap.BPOSAmount}', datafield: 'paidAmount',
	                    	cellsrenderer: function (row, column, value) {
		                    	return "<div style='text-align: right; margin-right: 5px;margin-top: 4px;'>" + formatcurrency(value, 'VND') + "</div>";
		                 	} 
	                     },
	                 ],
		});
	}
	
	function getListOtherCost(posTerminalStateId){
		var urlStr = 'jqxGeneralServicer?sname=JQGetListOtherCost&posTerminalStateId=' + posTerminalStateId; 
		var sourceOtherCost =
    	{
	        datafields:
	        [
	            { name: 'count', type: 'number' },
	            { name: 'description', type: 'string' },
	            { name: 'paidAmount', type: 'number' },
	        ],
	        cache: false,
	        root: 'results',
	        datatype: "json",
	        updaterow: function (rowid, rowdata) {
	           
	        },
	        beforeprocessing: function (data) {
	           
	        },
	        
	        pager: function (pagenum, pagesize, oldpagenum) {
	           
	        },
	        
	        sortcolumn: 'paidAmount',
			sortdirection: 'asc',
	        type: 'POST',
	        data: {
		        noConditionFind: 'Y',
		        conditionsFind: 'N',
		    },
		    pagesize:5,
	        contentType: 'application/x-www-form-urlencoded',
	        url: urlStr
    	};
		
		var dataAdapterOtherCost = new $.jqx.dataAdapter(sourceOtherCost);
		jQuery("#listOtherCost").jqxGrid({
			source: dataAdapterOtherCost,
			width: '100%',
	        height: 350,
	        filterable: false,
	        sortable: false,
			pageable: false,
	        sortable: false,
	        theme: 'energyblue',
	        selectionmode: 'singlerow',
	        localization: getLocalization(),
	        columns: [
	                     {
		                      text: '#', sortable: false, filterable: false, editable: false,
		                      groupable: false, draggable: false, resizable: false,
		                      datafield: '', columntype: 'number', width: 30,
		                      cellsrenderer: function (row, column, value) {
		                          return "<div style='margin:4px;'>" + (value + 1) + "</div>";
		                 	  }
                  		 },
	                     { text: '${uiLabelMap.BPOSReason}', datafield: 'description', width: 250},
	                     { text: '${uiLabelMap.BPOSAmount}', datafield: 'paidAmount',
	                    	cellsrenderer: function (row, column, value) {
		                    	return "<div style='text-align: right; margin-right: 5px;margin-top: 4px;'>" + formatcurrency(value, 'VND') + "</div>";
		                 	} 
	                     },
	                 ],
		});
	}
</script>