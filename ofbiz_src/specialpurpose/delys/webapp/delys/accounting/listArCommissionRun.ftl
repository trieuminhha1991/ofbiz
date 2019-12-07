 <script type="text/javascript">
	var row;
	var partyList = new Array();
	<#list listPartyRoleAndPartyDetail as list>
		row = {};
		row.key = '${StringUtil.wrapString(list.partyId)}';
		row.description = "${StringUtil.wrapString(list.firstName?if_exists)} ${StringUtil.wrapString(list.middleName?if_exists)} ${StringUtil.wrapString(list.lastName?if_exists)} ${StringUtil.wrapString(list.groupName?if_exists)}(${list.partyId})";
		partyList[${list_index}] = row;
	</#list>
	var statusItemList = new Array();
	<#list listStatusItem as list>
		row = {};
		row.key = '${StringUtil.wrapString(list.statusId)}';
		row.description = "${StringUtil.wrapString(list.description?if_exists)}";
		statusItemList[${list_index}] = row;
	</#list>
</script>	
 <#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', dataField: 'invoiceId', width: 150, cellsrenderer:
                     	function(row, colum, value)
                        {
                        	return \"<span><a href='accArinvoiceOverview?invoiceId=\" + value + \"'>\" + value + \"</a></span>\"
                        }},
 					  { text: '${uiLabelMap.AccountingFromParty}', dataField: 'partyIdFromName', width: 150},
 					  { text: '${uiLabelMap.AccountingToParty}', dataField: 'invoiceRolePartyIdName', width: 150},
 					  { text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: 150, cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i = 0; i < statusItemList.length;i++){
                        		if(statusItemList[i].key == value){
                        			return \"<span>\" + statusItemList[i].description + \"</span>\";
                        		}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        }},
 					  { text: '${uiLabelMap.AccountingReferenceNumber}', dataField: 'referenceNumber', width: 150},
 					  { text: '${uiLabelMap.CommonDescription}', dataField: 'description', width: 150},
 					  { text: '${uiLabelMap.AccountingInvoiceDate}', dataField: 'invoiceDate', width: 150, cellsformat: 'dd-MM-yyyy'},
 					  { text: '${uiLabelMap.AccountingDueDate}', dataField: 'dueDate', width: 150, cellsformat: 'dd-MM-yyyy'},
 					  { text: '${uiLabelMap.AccountingAmount}', sortable: false, dataField: 'amount', width: 150, cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.amount,data.currencyUomId) + \"</span>\";
					 	}},
 					  { text: '${uiLabelMap.FormFieldTitle_paidAmount}', sortable: false, dataField: 'paidAmount', width: 150, cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.paidAmount,data.currencyUomId) + \"</span>\";
					 	}},
 					  { text: '${uiLabelMap.FormFieldTitle_outstandingAmount}', sortable: false, dataField: 'outstandingAmount', width: 150, cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.outstandingAmount,data.currencyUomId) + \"</span>\";
					 	}},
                      "/>
<#assign dataField="[{ name: 'invoiceId', type: 'string' },
					 { name: 'partyIdFromName', type: 'string' },
					 { name: 'invoiceRolePartyIdName', type: 'string' },
					 { name: 'partyIdFrom', type: 'string' },
					 { name: 'invoiceRolePartyId', type: 'string' },
					 { name: 'referenceNumber', type: 'string' },
					 { name: 'statusId', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'amount', type: 'number' },
					 { name: 'paidAmount', type: 'number' },
					 { name: 'outstandingAmount', type: 'number' },
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'invoiceDate', type: 'date' },
					 { name: 'dueDate', type: 'date' }
					]
		 		 "/>	
<@jqGridMinimumLib/>

<div id="jqxPanel" style="width:100%;">
	<table style="margin:0 auto;margin-top:10px;width:100%;position:relative;">
		<tr>
			<td><div style="width:100px;">${uiLabelMap.PartyPartyId}</div></td>
			<td>
				<div id="partyPartyId"></div>
			</td>
		</tr>
		<tr>
			<td width="30">${uiLabelMap.fromDate}</td>
			<td>
				<div id="fromDate"></div>
			</td>
		</tr>
		<tr>
			<td width="30">${uiLabelMap.thruDate}</td>
			<td>
		        <div id="thruDate"></div>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
		       <input type="button" value="${uiLabelMap.filter}" id='jqxButton' style="margin-left:8px;"/>
		    </td>
		</tr>
		<tr>
			<td colspan="2" align="left">
		       <input type="button" value="${uiLabelMap.CommonRun}" id='jqxButtonExecute' style="margin-left:8px;"/>
		    </td>
		</tr>
	</table>
</div>	 	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var alterData = new Object();
	var salesRepPartyList = [];
	$("#partyPartyId").jqxComboBox({source: partyList, checkboxes: true, width: 350, height: 25, displayMember: "description", valueMember: "key"});           
	$("#fromDate").jqxDateTimeInput({ width: '175px', height: '25px',  formatString: 'dd/MM/yyyy hh:mm:ss tt', theme:theme});
	$("#fromDate").jqxDateTimeInput('val','');
	$("#thruDate").jqxDateTimeInput({ width: '175px', height: '25px',  formatString: 'dd/MM/yyyy hh:mm:ss tt', theme:theme});
	$("#thruDate").jqxDateTimeInput('val','');
	$("#jqxPanel").jqxPanel({ height: 215, theme:theme});
	$("#jqxButton").jqxButton({ width: '154', height: '30', theme:theme});
	$("#jqxButtonExecute").jqxButton({ width: '154', disabled: true, height: '30', theme:theme});
	$("#jqxButtonExecute").on('click', function () {
		var selectedrowindexes = $('#jqxgrid').jqxGrid('selectedrowindexes'); 
    	if(selectedrowindexes == null || selectedrowindexes.length < 1){
    		alert("Chưa hóa đơn nào được chọn!");
    		$("#jqxButtonExecute").jqxButton({ width: '154', disabled: true});
    	}else{
    		var arrInvoiceIds = "";
    		var row = $("#jqxgrid").jqxGrid('getrowdata', selectedrowindexes[0]);
    		arrInvoiceIds = row.invoiceId;
    		for(i = 1; i < selectedrowindexes.length; i++){
    			row = $("#jqxgrid").jqxGrid('getrowdata', selectedrowindexes[i]);
    			arrInvoiceIds += "," + row.invoiceId;
    		}
    		var tmpsalesRepPartyList = "";
    		if(salesRepPartyList.length > 0){
	    		tmpsalesRepPartyList = salesRepPartyList[0];
	    		for(i = 1; i < salesRepPartyList.length; i++){
	    			tmpsalesRepPartyList += "," + salesRepPartyList[i];
	    		}
    		}
    		var request = $.ajax({
			  url: "processCommissionRun",
			  type: "POST",
			  data: {serviceName : 'processCommissionRun', checkAllInvoices: 'off', invoiceIds: arrInvoiceIds, partyIds: tmpsalesRepPartyList},
			  dataType: "html"
			});
			
			request.done(function(data) {
			  	if(data.responseMessage == "error"){
	            	$('#jqxNotification').jqxNotification({ template: 'error'});
	            	$("#jqxNotification").text(data.errorMessage);
	            	$("#jqxNotification").jqxNotification("open");
	            }else{
	            	$('#container').empty();
	            	$('#jqxNotification').jqxNotification({ template: 'info'});
	            	$("#jqxNotification").text("Thuc thi thanh cong!");
	            	$("#jqxNotification").jqxNotification("open");
	            }
			});
			
			request.fail(function(jqXHR, textStatus) {
			  alert( "Request failed: " + textStatus );
			});
    	}
	});
	$("#jqxButton").on('click', function () {
        alterData.pagenum = "0";
        alterData.pagesize = "20";
        alterData.noConditionFind = "Y";
        alterData.conditionsFind = "N";
        if($('#fromDate').val() != null && $('#fromDate').val()){
        	alterData.fromDate = $('#fromDate').val();
    	}
    	if($('#thruDate').val() != null && $('#thruDate').val()){
        	alterData.thruDate = $('#thruDate').val();
        }
        var selectedItems = $('#partyPartyId').jqxComboBox('getCheckedItems');
        
        if(selectedItems != null && selectedItems){
        	for(i=0; i < selectedItems.length;i++){
        		salesRepPartyList[i] = selectedItems[i].value;
        	}
        }else{
        	salesRepPartyList = [];
        }
        alterData.salesRepPartyList = salesRepPartyList;
		$('#jqxgrid').jqxGrid('updatebounddata');
    });
    function rowselectevent(){
    	$("#jqxButtonExecute").jqxButton({ width: '154', disabled: false});
    };
    function rowunselectevent(){
    	var selectedrowindexes = $('#jqxgrid').jqxGrid('selectedrowindexes'); 
    	if(selectedrowindexes == null || selectedrowindexes.length < 1){
    		$("#jqxButtonExecute").jqxButton({ width: '154', disabled: true});
    	}else{
    	}
    };
</script>	
<style type="text/css">
	#jqxPanel td{
			padding:5px;
	}	 
</style>	
<@jqGrid url="jqxGeneralServicer?sname=getListApCommisionRun" dataField=dataField columnlist=columnlist filterable="true" filtersimplemode="false" usecurrencyfunction="true"
		 id="jqxgrid" jqGridMinimumLibEnable="false" selectionmode="checkbox" altrows="true" sourceId="invoiceId" rowunselectfunction="rowunselectevent();" rowselectfunction="rowselectevent();"
		 otherParams="amount,paidAmount,outstandingAmount:SL-getInvoicePaymentInfoList(invoiceId{invoiceId})<invoicePaymentInfoList>;partyIdFromName:M-org.ofbiz.party.party.PartyHelper(getPartyName)<partyIdFrom>;invoiceRolePartyIdName:M-org.ofbiz.party.party.PartyHelper(getPartyName)<invoiceRolePartyId>"/>
