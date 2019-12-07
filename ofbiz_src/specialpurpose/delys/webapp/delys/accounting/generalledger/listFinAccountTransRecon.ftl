<script>
	<#assign finAccTransTypes = delegator.findList("FinAccountTransType", null, null, null, null, false) />
	var finAccTransTypeData = new Array();
	<#list finAccTransTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['finAccountTransTypeId'] = '${item.finAccountTransTypeId?if_exists}';
		row['description'] = '${description}';
		finAccTransTypeData[${item_index}] = row;
	</#list>
	
	<#assign glAccounts = delegator.findList("GlAccount", null, null, null, null, false) />
	var glAccountData = new Array();
	<#list glAccounts as item>
		<#assign description = StringUtil.wrapString(item.accountCode?if_exists  + " - " + item.accountName?if_exists + "[" + item.glAccountId?if_exists + "]" ) />
		var row = {};
		row['description'] = '${description}';
		row['glAccountId'] = '${item.glAccountId}';
		glAccountData[${item_index}] = row;
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "FINACT_TRNS_STATUS"), null, null, null, false) />
	var statusData = new Array();
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
	
	<#assign glReconciliations = delegator.findByAnd("GlReconciliation", {"statusId" : "GLREC_CREATED"}, Static["org.ofbiz.base.util.UtilMisc"].toList("reconciledDate DESC"), false) />
	var glReconciliationData = new Array();
	<#list glReconciliations as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.glReconciliationName?if_exists) />
		row['glReconciliationId'] = '${item.glReconciliationId?if_exists}';
		row['description'] = '${description}';
		glReconciliationData[${item_index}] = row;
	</#list>
	
</script>
<#assign dataField="[{ name: 'finAccountId', type: 'string' },
					{ name: 'finAccountTransId', type: 'string' },
                 	{ name: 'finAccountTransTypeId', type: 'string' },
                 	{ name: 'partyId', type: 'string' },
					{ name: 'glReconciliationName', type: 'string' },
					{ name: 'transactionDate', type: 'date' },
					{ name: 'entryDate', type: 'date' },
					{ name: 'amount', type: 'number' },
					{ name: 'paymentId', type: 'string' },
					{ name: 'orderId', type: 'string' },
					{ name: 'orderItemSeqId', type: 'string' },
					{ name: 'performedByPartyId', type: 'string' },
					{ name: 'reasonEnumId', type: 'string' },
					{ name: 'comments', type: 'string' },
					{ name: 'statusId', type: 'string' },
		 		 	]"/>
<#assign columnlist="{ text: '${uiLabelMap.finAccountId}', dataField: 'finAccountId', width: '150'},
					 { text: '${uiLabelMap.finAccountTransTypeId}', dataField: 'finAccountTransTypeId', width: '150',
						cellsrenderer: function(row, column, value){
							for(i = 0; i < finAccTransTypeData.length; i++){
								if(finAccTransTypeData[i].finAccountTransTypeId == value){
									return '<span title=' + value +'>' + finAccTransTypeData[i].description + '</span>'
								}
							}
							return ;
						}
					 },
					 { text: '${uiLabelMap.accPartyId}', dataField: 'partyId', width: '150',
						cellsrenderer: function(row, column, value){
							  var partyName = value;
							  $.ajax({
									url: 'getPartyName',
									type: 'POST',
									data: {partyId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											partyName = data.partyName;
										}
							        }
								});
							  return '<span title' + value + '>' + partyName + '</span>';
						 }
					 },
					 { text: '${uiLabelMap.glReconciliationName}', dataField: 'glReconciliationName', width: '150'},
					 { text: '${uiLabelMap.transactionDate}', dataField: 'transactionDate', width: '150', cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.entryDate}', dataField: 'entryDate', width: '150', cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.amount}', dataField: 'amount', width: '150'},
					 { text: '${uiLabelMap.paymentId}', dataField: 'paymentId', width: '150'},
					 { text: '${uiLabelMap.orderId}', dataField: 'orderId', width: '150'},
					 { text: '${uiLabelMap.accOrderItemSeqId}', dataField: 'orderItemSeqId', width: '150'},
					 { text: '${uiLabelMap.performedByPartyId}', dataField: 'performedByPartyId', width: '150'},
					 { text: '${uiLabelMap.reasonEnumId}', dataField: 'reasonEnumId', width: '150'},
					 { text: '${uiLabelMap.comments}', dataField: 'comments', width: '150'},
					 { text: '${uiLabelMap.statusId}', dataField: 'statusId', width: '150',
	                      cellsrenderer: function(row, column, value){
	                    	  for(i = 0; i < statusData.length; i++){
	                    		  if(statusData[i].statusId == value){
	                    			  return '<span title=' + value + '>' + statusData[i].description + '</span>'
	                    		  }
	                    	  }
	                    	  return ;
	                      }
					 },
					 { text: '${uiLabelMap.cancelTransactionStatus}', width: 200, 
						 cellsrenderer: function (row, column, value) {
							 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 if(data.statusId == 'FINACT_TRNS_CREATED'){
								 return '<span><a href=setFinAccountTransStatus?finAccountTransId=' + data.finAccountTransId + '&finAccountId=' + data.finAccountId + '&statusId=FINACT_TRNS_CANCELED>' + '${uiLabelMap.CommonCancel}' + '</a></span>'
							 }else{
								 return ;
							 }
					 	}
					 }
					"/>
<@jqGridMinimumLib/>
<div id='contextMenu'>
	<ul>
		<li><i class="icon-ok"></i>${StringUtil.wrapString(uiLabelMap.AccountingCreateAcctRecons)}</li>
	</ul>
</div>
<script type="text/javascript">
	$("#contextMenu").jqxMenu({ width: 300, height: 30, autoOpenPopup: false, mode: 'popup'});
	$("#contextMenu").on('itemclick', function (event) {
		var rowindexes = $("#jqxgrid").jqxGrid('selectedrowindexes');
		var data = {};
		var glReconciliationId = $('#dropdownlistjqxgrid').val();
		for(i = 0; i < rowindexes.length; i++){
			var row = $("#jqxgrid").jqxGrid('getrowdata', rowindexes[i]);
			var finAccountTransId = row.finAccountTransId;
			data["finAccountTransId_o_" + i] = finAccountTransId;
			data["glReconciliationId_o_" + i] = glReconciliationId;
			data["_rowSubmit_o_" + i] = 'Y';
		}
		var request = $.ajax({
			  url: "<@ofbizUrl>assignGlRecToFinAccTrans</@ofbizUrl>",
			  type: "POST",
			  data: data,
			  dataType: "html",
			  success: function(res){
				  var dataObj = JSON.parse(res);
				  if(dataObj && dataObj['_ERROR_MESSAGE_']){
					$('#jqxNotification').jqxNotification({ template: 'error'});
                  	$("#jqxNotification").text(dataObj['_ERROR_MESSAGE_']);
                  	$("#jqxNotification").jqxNotification("open");
				  }
			  }
			});
	});
</script>
<@jqGrid filtersimplemode="true" dropdownlist="true" ddlSource="glReconciliationData" displayMember="description" valueMember="glReconciliationId" addType="popup" showstatusbar="false" statusbarheight='100' dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="false" alternativeAddPopup="alterpopupWindow" deleterow="false" editable="false" 
		 url="jqxGeneralServicer?sname=JQListNotAssignFinAccountTrans&finAccountId=${parameters.finAccountId}" id="jqxgrid" addrefresh="true" mouseRightMenu="true" contextMenuId="contextMenu" selectionmode="checkbox"
		 createUrl="jqxGeneralServicer?sname=createFinAccountTrans&finAccountId=${parameters.finAccountId}&jqaction=C" addColumns="finAccountId[${parameters.finAccountId}];finAccountTransTypeId;partyId;glReconciliationId;transactionDate(java.sql.Timestamp);entryDate(java.sql.Timestamp);amount(java.math.BigDecimal);paymentId;orderId;orderItemSeqId;reasonEnumId;comments;statusId;glAccountId"
		/>
				 
<!--HTML for add form -->
<div id="alterpopupWindow">
	<div>${uiLabelMap.accCreateNew}</div>
		<div style="overflow: hidden;">
			<table>
				<tr>
					<td align="right">${uiLabelMap.finAccountTransTypeId}:</td>
					<td align="left">
					  	<div id="finAccountTransTypeId">
					  	</div>
					 </td>
				</tr>
				<tr>
					 <td align="right">${uiLabelMap.partyId}:</td>
					 <td align="left">
					 	<div id="partyId">
					 		<div id="jqxPartyGrid" />
					 	</div>
					 </td>
				</tr>
				<tr>
					 <td align="right">${uiLabelMap.glReconciliationId}:</td>
					 <td align="left"><input id="glReconciliationId"/></td>
				</tr>
				<tr>
					 <td align="right">${uiLabelMap.transactionDate}:</td>
					 <td align="left"><div id="transactionDate"/></td>
				</tr>
				<tr>
				 	<td align="right">${uiLabelMap.entryDate}:</td>
				 	<td align="left"><div id="entryDate"/></td>
				</tr>
				<tr>
			 		<td align="right">${uiLabelMap.amount}:</td>
			 		<td align="left"><input id="amount"/></td>
			 	</tr>
			 	<tr>
		 			<td align="right">${uiLabelMap.paymentId}:</td>
		 			<td align="left">
		 				<div id="paymentId">
		 					<div id="jqxGridPay" />
		 				</div>
		 			</td>
		 		</tr>
		 		<tr>
	 				<td align="right">${uiLabelMap.orderId}:</td>
	 				<td align="left">
	 					<div id="orderId">
	 						<div id="jqxGridOrder"></div>
	 					</div>
	 				</td>
	 			</tr>
	 			<tr>
 					<td align="right">${uiLabelMap.orderItemSeqId}:</td>
 					<td align="left">
 						<input id="orderItemSeqId">
 						</input>
 					</td>
 				</tr>
 				<tr>
					<td align="right">${uiLabelMap.reasonEnumId}:</td>
					<td align="left"><input id="reasonEnumId"/></td>
				</tr>
				<tr>
					<td align="right">${uiLabelMap.comments}:</td>
					<td align="left"><input id="comments"/></td>
				</tr>
				<tr>
					<td align="right">${uiLabelMap.statusId}:</td>
					<td align="left"><div id="statusId"/></td>
				</tr>
				<tr>
					<td align="right">${uiLabelMap.glAccountId}:</td>
					<td align="left"><div id="glAccountId"/></td>
				</tr>
				<tr>
					 <td align="right"></td>
					 <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
				</tr>
			</table>
		</div>
	</div>
</div>
<script>
	//Create theme
	$.jqx.theme='olbius';
	theme = $.jqx.theme;

	//Create Window popup
	$('#alterpopupWindow').jqxWindow({width: 600, height: 550, autoOpen: false, theme: theme, cancelButton: $('#alterCancel'), modalOpacity: 0.7});

	//Create finAccountTransTypeId
	$('#finAccountTransTypeId').jqxDropDownList({width: 200, source: finAccTransTypeData, selectedIndex: 0, valueMember: 'finAccountTransTypeId', displayMember: 'description', theme: theme});
	
	//Create glReconciliationId
	$('#glReconciliationId').jqxInput({width: 195, theme: theme});
	
	//Create transactionDate
	$('#transactionDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy', theme: theme});
	
	//Create entryDate
	$('#entryDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy', theme: theme});
	
	//Create amount
	$('#amount').jqxInput({width: 195, theme: theme});
	
	//Create orderItemSeqId
	$('#orderItemSeqId').jqxInput({width: 195, theme: theme});
	
	//Create comments
	$('#comments').jqxInput({width: 195, theme: theme});
	
	//Create orderItemSeqId
	$('#reasonEnumId').jqxInput({width: 195, theme: theme});
	
	//Create statusId
	$('#statusId').jqxDropDownList({width: 200, source: statusData, displayMember: 'description', valueMember: 'statusId', theme: theme});
	
	//Create statusId
	$('#glAccountId').jqxDropDownList({width: 200, source: glAccountData, displayMember: 'description', valueMember: 'glAccountId', theme: theme});

	//Order Grid
	var sourceOrder =
		{	
			datafields:
				[
				 { name: 'orderId', type: 'string' },
				 { name: 'orderTypeId', type: 'string' },
				 { name: 'orderName', type: 'string' },
				 { name: 'externalId', type: 'string' },
				 { name: 'salesChannelEnumId', type: 'string' },
				 { name: 'orderDate', type: 'date'},
				 { name: 'priority', type: 'number'},
				 { name: 'entryDate', type: 'date'},
				 { name: 'statusId', type: 'string'},
				],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
				sourceOrder.totalrecords = data.TotalRows;
			},
			filter: function () {
				// update the grid and send a request to the server.
				$("#jqxGridOrder").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#jqxGridOrder").jqxGrid('updatebounddata');
			},
			sortcolumn: 'orderId',
			sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:5,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=JQListOrders',
		};
	var dataAdapterOrder = new $.jqx.dataAdapter(sourceOrder);
	$("#orderId").jqxDropDownButton({ width: 200, height: 25});
	$("#jqxGridOrder").jqxGrid({
		source: dataAdapterPay,
		filterable: false,
		virtualmode: true, 
		sortable:true,
		theme: theme,
		editable: false,
		autoheight:true,
		pageable: true,
		rendergridrows: function(obj)
		{
			return obj.data;
		},
		columns: [
		          { text: '${uiLabelMap.orderId}', datafield: 'orderId' },
				  { text: '${uiLabelMap.orderTypeId}', datafield: 'orderTypeId' },
				  { text: '${uiLabelMap.orderName}', datafield: 'orderName' },
				  { text: '${uiLabelMap.externalId}', datafield: 'externalId' },
				  { text: '${uiLabelMap.salesChannelEnumId}', datafield: 'salesChannelEnumId'},
				  { text: '${uiLabelMap.orderDate}', datafield: 'orderDate'},
				  { text: '${uiLabelMap.priority}', datafield: 'priority'},
				  { text: '${uiLabelMap.entryDate}', datafield: 'entryDate'},
				  { text: '${uiLabelMap.statusId}', datafield: 'statusId'},
		         ]
			});
	$("#jqxGridOrder").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxGridOrder").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['orderId'] +'</div>';
		$('#orderId').jqxDropDownButton('setContent', dropDownContent);
	});
	
	//Payment Grid
	var sourcePay =
		{	
			datafields:
				[
				 { name: 'paymentId', type: 'string' },
				 { name: 'partyIdFrom', type: 'string' },
				 { name: 'partyIdTo', type: 'string' },
				 { name: 'effectiveDate', type: 'string' },
				 { name: 'amount', type: 'string' },
				 { name: 'currencyUomId', type: 'string'}
				],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
				sourcePay.totalrecords = data.TotalRows;
			},
			filter: function () {
				// update the grid and send a request to the server.
				$("#jqxGridPay").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#jqxGridPay").jqxGrid('updatebounddata');
			},
			sortcolumn: 'paymentId',
			sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:5,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=getListPayment',
		};
	var dataAdapterPay = new $.jqx.dataAdapter(sourcePay);
	$("#paymentId").jqxDropDownButton({ width: 200, height: 25});
	$("#jqxGridPay").jqxGrid({
		source: dataAdapterPay,
		filterable: false,
		virtualmode: true, 
		sortable:true,
		theme: theme,
		editable: false,
		autoheight:true,
		pageable: true,
		rendergridrows: function(obj)
		{
			return obj.data;
		},
		columns: [
		          { text: '${uiLabelMap.paymentId}', datafield: 'paymentId' },
				  { text: '${uiLabelMap.partyIdFrom}', datafield: 'partyIdFrom' },
				  { text: '${uiLabelMap.partyIdTo}', datafield: 'partyIdTo' },
				  { text: '${uiLabelMap.effectiveDate}', datafield: 'effectiveDate' },
				  { text: '${uiLabelMap.amount}', datafield: 'amount' },
				  { text: '${uiLabelMap.currencyUomId}', datafield: 'currencyUomId'}
		         ]
			});
	$("#jqxGridPay").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxGridPay").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['paymentId'] +'</div>';
		$('#paymentId').jqxDropDownButton('setContent', dropDownContent);
	});
	
	//Create partyId
	var sourceP =
	{
		datafields:
			[
			 { name: 'partyId', type: 'string' },
			 { name: 'partyTypeId', type: 'string' },
			 { name: 'firstName', type: 'string' },
			 { name: 'lastName', type: 'string' },
			 { name: 'groupName', type: 'string' }
			],
		cache: false,
		root: 'results',
		datatype: "json",
		updaterow: function (rowid, rowdata) {
			// synchronize with the server - send update command   
		},
		beforeprocessing: function (data) {
			sourceP.totalrecords = data.TotalRows;
		},
		filter: function () {
			// update the grid and send a request to the server.
			$("#jqxPartyGrid").jqxGrid('updatebounddata');
		},
		pager: function (pagenum, pagesize, oldpagenum) {
			// callback called when a page or page size is changed.
		},
		sort: function () {
			$("#jqxPartyGrid").jqxGrid('updatebounddata');
		},
		sortcolumn: 'partyId',
		sortdirection: 'asc',
		type: 'POST',
		data: {
			noConditionFind: 'Y',
			conditionsFind: 'N',
		},
		pagesize:5,
		contentType: 'application/x-www-form-urlencoded',
		url: 'jqxGeneralServicer?sname=getFromParty',
	};
	var dataAdapterP = new $.jqx.dataAdapter(sourceP);
	$("#partyId").jqxDropDownButton({ width: 200, height: 25});
	$("#jqxPartyGrid").jqxGrid({
		source: dataAdapterP,
		filterable: false,
		virtualmode: true, 
		sortable:true,
		theme: theme,
		editable: false,
		autoheight:true,
		pageable: true,
		rendergridrows: function(obj)
		{
			return obj.data;
		},
		columns: [
		          { text: '${uiLabelMap.partyId}', datafield: 'partyId'},
		          { text: '${uiLabelMap.partyTypeId}', datafield: 'partyTypeId'},
		          { text: '${uiLabelMap.firstName}', datafield: 'firstName'},
		          { text: '${uiLabelMap.lastName}', datafield: 'lastName'},
		          { text: '${uiLabelMap.groupName}', datafield: 'groupName'}
		          ]
		});
	$("#jqxPartyGrid").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxPartyGrid").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
		$('#partyId').jqxDropDownButton('setContent', dropDownContent);
	});
	
	//Create button
	$('#alterSave').jqxButton({theme: theme});
	$('#alterCancel').jqxButton({theme: theme});

	$('#alterSave').click(function(){
		var row = {
			finAccountTransTypeId: $('#finAccountTransTypeId').val(),
			glReconciliationId: $('#glReconciliationId').val(),
			glReconciliationId: $('#glReconciliationId').val(),
			transactionDate: $('#transactionDate').jqxDateTimeInput('getDate').getTime(),
			entryDate: $('#entryDate').jqxDateTimeInput('getDate').getTime(),
			partyId: $('#partyId').val(),
			amount: $('#amount').val(),
			paymentId: $('#paymentId').val(),
			orderId: $('#orderId').val(),
			orderItemSeqId: $('#orderItemSeqId').val(),
			reasonEnumId: $('#reasonEnumId').val(),
			comments: $('#comments').val(),
			statusId: $('#statusId').val(),
			glAccountId: $('#glAccountId').val(),
		};
		$('#jqxgrid').jqxGrid("addRow", null, row, 'first');
		$('#jqxgrid').jqxGrid("clearSelection");
		$('#alterpopupWindow').jqxWindow('close');
	});
</script>