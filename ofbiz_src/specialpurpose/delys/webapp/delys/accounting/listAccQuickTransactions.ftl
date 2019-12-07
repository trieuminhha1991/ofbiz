<script>
	<#assign acctgTransTypes = delegator.findList("AcctgTransType", null, null, null, null, false) />
	var acctgTransTypesData =  new Array();
	<#list acctgTransTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['acctgTransTypeId'] = "${item.acctgTransTypeId?if_exists}";
		row['description'] = "${description}";
		acctgTransTypesData[${item_index}] = row;
	</#list>
	
	<#assign glFiscalTypes = delegator.findList("GlFiscalType", null, null, null, null, false) />
	var glFiscalTypesData =  new Array();
	<#list glFiscalTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['glFiscalTypeId'] = "${item.glFiscalTypeId?if_exists}";
		row['description'] = "${description}";
		glFiscalTypesData[${item_index}] = row;
	</#list>
	
	<#assign roleTypes = delegator.findList("RoleType", null, null, null, null, false) />
	var roleTypesData =  new Array();
	<#list roleTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['roleTypeId'] = "${item.roleTypeId?if_exists}";
		row['description'] = "${description}";
		roleTypesData[${item_index}] = row;
	</#list>
	
	<#assign fixedAssets = delegator.findList("FixedAsset", null, null, null, null, false) />
	var fixedAssetsData =  new Array();
	<#list fixedAssets as item>
		var row = {};
		row['fixedAssetId'] = "${item.fixedAssetId?if_exists}";
		row['fixedAssetId'] = "${item.fixedAssetId?if_exists}";
		fixedAssetsData[${item_index}] = row;
	</#list>
	
	<#assign glAccountOACs = delegator.findList("GlAccountOrganizationAndClass", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", "company"), null, null, null, false) />
	var glAccountOACsData =  new Array();
	<#list glAccountOACs as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.accountCode?if_exists + "-" + item.accountName?if_exists + "[" + item.glAccountId?if_exists +"]")>
		row['glAccountId'] = "${item.fixedAssetId?if_exists}";
		row['description'] = "${description?if_exists}";
		glAccountOACsData[${item_index}] = row;
	</#list>
	
	<#assign glJournals = delegator.findList("GlJournal", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", "company"), null, null, null, false) />
	var glJournalsData =  new Array();
	<#list glJournals as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.glJournalName?if_exists + "[" + item.glJournalId?if_exists +"]")>
		row['glJournalId'] = "${item.glJournalId?if_exists}";
		row['description'] = "${description?if_exists}";
		glJournalsData[${item_index}] = row;
	</#list>
	
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ACCTG_ENREC_STATUS"), null, null, null, false) />
	var statusItemsData =  new Array();
	<#list statusItems as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['statusId'] = "${item.statusId?if_exists}";
		row['description'] = "${description?if_exists}";
		statusItemsData[${item_index}] = row;
	</#list>
	
	<#assign invoices = delegator.findList("InvoiceAndType", null, null, null, null, false) />
	var invoiceData = new Array();
	<#list invoices as item>
		<#assign description = StringUtil.wrapString(item.description?if_exists + "[" + item.invoiceTypeDesc?if_exists + "]") />
		var row = {};
		row['invoiceId'] = '${item.invoiceId?if_exists}';
		row['description'] = '${description}';
		invoiceData[${item_index}] = row;
	</#list>
	
	<#assign payments = delegator.findList("PaymentAndType", null, null, null, null, false) />
	var paymentData = new Array();
	<#list payments as item>
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		var row = {};
		row['paymentId'] = '${item.paymentId?if_exists}';
		row['description'] = '${description}';
		paymentData[${item_index}] = row;
	</#list>
	
	<#assign workEfforts = delegator.findList("WorkEffort", null, null, null, null, false) />
	var workEffortData = new Array();
	<#list workEfforts as item>
		<#assign description = StringUtil.wrapString(item.workEffortName?if_exists) />
		var row = {};
		row['workEffortId'] = '${item.workEffortId?if_exists}';
		row['description'] = '${description}';
		workEffortData[${item_index}] = row;
	</#list>
	
	<#assign shipments = delegator.findList("ShipmentAndType", null, null, null, null, false) />
	var shipmentData = new Array();
	<#list shipments as item>
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		var row = {};
		row['shipmentId'] = '${item.shipmentId?if_exists}';
		row['description'] = '${description}';
		shipmentData[${item_index}] = row;
	</#list>
	
	var isPostedData = new Array();
	var row1 = {};
	row1['isPosted'] = 'Y';
	row1['description'] = 'Yes';
	isPostedData[0] = row1;
	var row2 = {};
	row2['isPosted'] = 'N';
	row2['description'] = 'No';
	isPostedData[1] = row2;
</script>
<#assign columnlist="{ text: '${uiLabelMap.acctgTransId}', dataField: 'acctgTransId', width: 150,
						cellsrenderer: function (row, column, value){
							acctgTransId = value;
							return '<span> <a href=' + 'EditAccountingTransaction?acctgTransId='+ value + '&organizationPartyId=company' + '>' + value + '</a></span>'
						}
					 },
 					 { text: '${uiLabelMap.transactionDate}',filtertype: 'range', dataField: 'transactionDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss'},
					 { text: '${uiLabelMap.acctgTransTypeId}', dataField: 'acctgTransTypeId', width: 150,
						cellsrenderer: function (row, column, value) {
							for(i = 0; i < acctgTransTypesData.length; i++){
									if(value == acctgTransTypesData[i].acctgTransTypeId){
										return '<span title=' + acctgTransTypesData[i].acctgTransTypeId + '>' + acctgTransTypesData[i].description + '</span>';
									}
							}
						}
					 },
					 { text: '${uiLabelMap.glFiscalTypeId}', dataField: 'glFiscalTypeId', width: 150,
						cellsrenderer: function (row, column, value) {
							for(i = 0; i < glFiscalTypesData.length; i++){
									if(value == glFiscalTypesData[i].glFiscalTypeId){
										return '<span title=' + glFiscalTypesData[i].glFiscalTypeId + '>' + glFiscalTypesData[i].description + '</span>';
								}
							}
						}
					 },
					 { text: '${uiLabelMap.invoiceId}', dataField: 'invoiceId', width: 150,
						cellsrenderer: function (row, column, value) {
							for(i = 0; i < invoiceData.length; i++){
									if(value == invoiceData[i].invoiceId){
										return '<span> <a title=' + invoiceData[i].invoiceId + ' href=' +'/accounting/control/invoiceOverview?invoiceId=' + invoiceData[i].invoiceId + '>' + invoiceData[i].description + '</a> </span>';
								}
							}
						}
					 },
					 { text: '${uiLabelMap.paymentId}', dataField: 'paymentId', width: 150,
						cellsrenderer: function (row, column, value) {
							for(i = 0; i < paymentData.length; i++){
									if(value == paymentData[i].paymentId){
										return '<span> <a title=' + paymentData[i].paymentId + ' href=' +'/accounting/control/editPayment?paymentId=' + paymentData[i].paymentId + '>' + paymentData[i].description + '</a> </span>';
								}
							}
						}
					 },
					 { text: '${uiLabelMap.workEffortId}', dataField: 'workEffortId', width: 150,
						cellsrenderer: function (row, column, value) {
							for(i = 0; i < workEffortData.length; i++){
									if(value == workEffortData[i].workEffortId){
										return '<span> <a title=' + workEffortData[i].workEffortId + ' href=' +'/workeffort/control/EditWorkEffort?workEffortId=' + workEffortData[i].workEffortId + '>' + workEffortData[i].description + '</a> </span>';
								}
							}
						}
					 },
					 { text: '${uiLabelMap.shipmentId}', dataField: 'shipmentId', width: 150,
						cellsrenderer: function (row, column, value) {
							for(i = 0; i < shipmentData.length; i++){
									if(value == shipmentData[i].shipmentId){
										return '<span> <a title=' + shipmentData[i].shipmentId + ' href=' +'/facility/control/EditShipment?shipmentId=' + shipmentData[i].shipmentId + '>' + shipmentData[i].description + '</a> </span>';
								}
							}
						}
					 },
					 { text: '${uiLabelMap.isPosted}', dataField: 'isPosted', width: 150,
						cellsrenderer: function (row, column, value) {
							for(i = 0; i < isPostedData.length; i++){
									if(value == isPostedData[i].isPosted){
										return '<span title=' + isPostedData[i].isPosted + '>' + isPostedData[i].description + '</a> </span>';
								}
							}
						}
					 },
					 { text: '${uiLabelMap.postedDate}',filtertype: 'range', dataField: 'postedDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss'},
					 { text: '${uiLabelMap.Post}', width: 150, 
						 cellsrenderer: function (row, column, value) {
						 	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 	if(data.isPosted == 'Y'){
						 		return ;
						 	}else{
						 		return '<span><a href=postAcctgTrans?acctgTransId=' + data.acctgTransId +'>' + '${uiLabelMap.Post}' + '</a></span>';
						 	}
					 	}
					 },
					 { text: '${uiLabelMap.PDF}', width: 150, 
						 cellsrenderer: function (row, column, value) {
						 	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 	if(data.isPosted == 'N'){
						 		return ;
						 	}else{
						 		return '<span><a href=acctgTransDetailReportPdf?acctgTransId=' + data.acctgTransId +'>' + '${uiLabelMap.PDF}' + '</a></span>';
						 	}
					 	}
					 }
					 "/>
<#assign dataField="[{ name: 'acctgTransId', type: 'string' },
                 	{ name: 'transactionDate', type: 'date' },
                 	{ name: 'acctgTransTypeId', type: 'string' },
					{ name: 'glFiscalTypeId', type: 'string' },
					{ name: 'invoiceId', type: 'string' },
                 	{ name: 'paymentId', type: 'string' },
                 	{ name: 'workEffortId', type: 'string' }, 
                 	{ name: 'shipmentId', type: 'string'},
                 	{ name: 'isPosted', type: 'string'},
                 	{ name: 'postedDate', type: 'date'},
		 		 	]"/>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		 url="jqxGeneralServicer?sname=JQListTransaction" id="jqxgrid"
		 createUrl="jqxGeneralServicer?sname=createAcctgTrans&jqaction=C"	 
	     addColumns="acctgTransTypeId;glFiscalTypeId;finAccountTransId;description;createdDate(java.sql.Timestamp);lastModifiedDate(java.sql.Timestamp);" +
	     "shipmentId;fixedAssetId;invoiceId;paymentId;productId;workEffortId;voucherRef;voucherDate(java.sql.Timestamp);receiptId;theirAcctgTransId;glJournalId;inventoryItemId;" +
	     "physicalInventoryId;groupStatusId;partyId;roleTypeId;transactionDate(java.sql.Timestamp);scheduledPostingDate(java.sql.Timestamp);isPosted;postedDate(java.sql.Timestamp);organizationPartyId[company]"
	     />

<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    	<div style="overflow: hidden;">
		<table>
			<tr>
				<td align="right">
					${uiLabelMap.acctgTransTypeId}
				</td>
				<td align="left">
			       <div id="acctgTransTypeId"></div>
			    </td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.glFiscalTypeId}
				</td>
				<td align="left">
			       <div id="glFiscalTypeId">
			       </div>
			    </td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.partyId}
				</td>
				<td align="left">
					<div id="partyId">
						<div id="jqxGridPartyId" />
					</div>
			    </td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.roleTypeId}
				</td>
				<td align="left">
			       <div id="roleTypeId"></div>
			    </td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.invoiceId}
				</td>
				<td align="left">
			       <div id="invoiceId">
			       		<div id="jqxGridInvoice"/>
			       </div>
			    </td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.paymentId}
				</td>
				<td align="left">
			       	<div id="paymentId">
			       		<div id="jqxGridPay"/>
			       	</div>
			    </td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.productId}
				</td>
				<td align="left">
			       <div id="productId">
			       	<div id="jqxGridProd"/>
			       </div>
			    </td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.workEffortId}
				</td>
				<td align="left">
			       <div id="workEffortId" >
			       		<div id="jqxGridWE"/>
			       </div>
			    </td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.shipmentId}
				</td>
				<td align="left">
			       <div id="shipmentId">
			       		<div id="jqxGridShip" />
			       </div>
			    </td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.fixedAssetId}
				</td>
				<td align="left">
					<div id="fixedAssetId"></div>
				</td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.debitGlAccountId}
				</td>
				<td align="left">
					<div id="debitGlAccountId"></div>
				</td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.creditGlAccountId}
				</td>
				<td align="left">
					<div id="creditGlAccountId"></div>
				</td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.amount}
				</td>
				<td align="left">
					<input id="amount" />
				</td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.transactionDate}
				</td>
				<td align="left">
					<div id="transactionDate"></div>
				</td>
			</tr>
			<tr>
				<td align="right">
					${uiLabelMap.description}
				</td>
				<td align="left">
					<input id="description"></input>
				</td>
			</tr>
			<tr>
		        <td align="right"></td>
		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
		    </tr>
		</table>
	</div>
</div>		
<script type="text/javascript">

	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var outFilterCondition = "";
	$("#alterpopupWindow").jqxWindow({
		minWidth: 700, maxWidth: 1000, minHeight: 700, maxHeight: 1000, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	$("#alterSave").jqxButton({theme: theme});
	$("#alterCancel").jqxButton({theme: theme});
	$("#acctgTransTypeId").jqxDropDownList({ theme: theme, source: acctgTransTypesData, displayMember: "description", valueMember: "acctgTransTypeId", selectedIndex: 1, width: '200', height: '25'});
	$("#glFiscalTypeId").jqxDropDownList({ theme: theme, source: glFiscalTypesData, displayMember: "description", valueMember: "glFiscalTypeId", selectedIndex: 1, width: '200', height: '25'});
	$("#roleTypeId").jqxDropDownList({ theme: theme, source: roleTypesData, displayMember: "description", valueMember: "roleTypeId", selectedIndex: 1, width: '200', height: '25'});
	$("#fixedAssetId").jqxDropDownList({ theme: theme, source: fixedAssetsData, displayMember: "fixedAssetId", valueMember: "fixedAssetId", selectedIndex: 1, width: '200', height: '25'});
	$("#debitGlAccountId").jqxDropDownList({ theme: theme, source: glAccountOACsData, displayMember: "description", valueMember: "glAccountId", selectedIndex: 1, width: '200', height: '25'});
	$("#creditGlAccountId").jqxDropDownList({ theme: theme, source: glAccountOACsData, displayMember: "description", valueMember: "glAccountId", selectedIndex: 1, width: '200', height: '25'});
	$('#amount').jqxInput({width:195});
	$("#transactionDate").jqxDateTimeInput({width: '200px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});
	$('#description').jqxInput({width:195});
	// Party Grid
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
				$("#jqxGridPartyId").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#jqxGridPartyId").jqxGrid('updatebounddata');
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
	$("#jqxGridPartyId").jqxGrid({
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
	$("#jqxGridPartyId").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxGridPartyId").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
		$('#partyId').jqxDropDownButton('setContent', dropDownContent);
	});

	//Invoice Grid
	var sourceINV =
		{	
			datafields:
				[
				 { name: 'invoiceId', type: 'string' },
				 { name: 'invoiceTypeId', type: 'string' },
				 { name: 'partyIdFrom', type: 'string' },
				 { name: 'partyId', type: 'string' },
				 { name: 'statusId', type: 'string' },
				 { name: 'description', type: 'string'}
				],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
				sourceINV.totalrecords = data.TotalRows;
			},
			filter: function () {
				// update the grid and send a request to the server.
				$("#jqxGridInvoice").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#jqxGridInvoice").jqxGrid('updatebounddata');
			},
			sortcolumn: 'invoiceId',
			sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:5,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=getListInvoice',
		};
	var dataAdapterINV = new $.jqx.dataAdapter(sourceINV);
	$("#invoiceId").jqxDropDownButton({ width: 200, height: 25});
	$("#jqxGridInvoice").jqxGrid({
		source: dataAdapterINV,
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
		          { text: '${uiLabelMap.invoiceId}', datafield: 'invoiceId' },
				  { text: '${uiLabelMap.invoiceTypeId}', datafield: 'invoiceTypeId' },
				  { text: '${uiLabelMap.partyIdFrom}', datafield: 'partyIdFrom' },
				  { text: '${uiLabelMap.partyId}', datafield: 'partyId' },
				  { text: '${uiLabelMap.statusId}', datafield: 'statusId' },
				  { text: '${uiLabelMap.description}', datafield: 'description'}
		         ]
			});
	$("#jqxGridInvoice").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxGridInvoice").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['invoiceId'] +'</div>';
		$('#invoiceId').jqxDropDownButton('setContent', dropDownContent);
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
				sourceINV.totalrecords = data.TotalRows;
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
	
	//Product Grid
	var sourceProd =
		{	
			datafields:
				[
				 { name: 'productId', type: 'string' },
				 { name: 'brandName', type: 'string' },
				 { name: 'internalName', type: 'string' },
				 { name: 'productTypeId', type: 'string' }
				],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
				sourceProd.totalrecords = data.TotalRows;
			},
			filter: function () {
				// update the grid and send a request to the server.
				$("#jqxGridProd").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#jqxGridProd").jqxGrid('updatebounddata');
			},
			sortcolumn: 'productId',
			sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:5,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=getListProduct',
		};
	var dataAdapterProd = new $.jqx.dataAdapter(sourceProd);
	$("#productId").jqxDropDownButton({ width: 200, height: 25});
	$("#jqxGridProd").jqxGrid({
		source: dataAdapterProd,
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
		          { text: '${uiLabelMap.productId}', datafield: 'productId' },
				  { text: '${uiLabelMap.brandName}', datafield: 'brandName' },
				  { text: '${uiLabelMap.internalName}', datafield: 'internalName' },
				  { text: '${uiLabelMap.productTypeId}', datafield: 'productTypeId' }
		         ]
			});
	$("#jqxGridProd").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxGridProd").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['productId'] +'</div>';
		$('#productId').jqxDropDownButton('setContent', dropDownContent);
	});
	
	//WorkEffort Grid
	var sourceWE =
		{	
			datafields:
				[
				 { name: 'workEffortId', type: 'string' },
				 { name: 'workEffortName', type: 'string' },
				 { name: 'workEffortTypeId', type: 'string' },
				 { name: 'contactMechTypeId', type: 'string' }
				],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
				sourceWE.totalrecords = data.TotalRows;
			},
			filter: function () {
				// update the grid and send a request to the server.
				$("#jqxGridWE").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#jqxGridWE").jqxGrid('updatebounddata');
			},
			sortcolumn: 'workEffortId',
			sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:5,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=getListWorkEffort',
		};
	var dataAdapterWE = new $.jqx.dataAdapter(sourceWE);
	$("#workEffortId").jqxDropDownButton({ width: 200, height: 25});
	$("#jqxGridWE").jqxGrid({
		source: dataAdapterWE,
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
		          { text: '${uiLabelMap.workEffortId}', datafield: 'workEffortId' },
				  { text: '${uiLabelMap.workEffortName}', datafield: 'workEffortName' },
				  { text: '${uiLabelMap.workEffortTypeId}', datafield: 'workEffortTypeId' },
				  { text: '${uiLabelMap.contactMechTypeId}', datafield: 'contactMechTypeId' }
		         ]
			});
	$("#jqxGridWE").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxGridWE").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['workEffortId'] +'</div>';
		$('#workEffortId').jqxDropDownButton('setContent', dropDownContent);
	});
	
	//Shipment Grid
	var sourceShip =
		{	
			datafields:
				[
				 { name: 'shipmentId', type: 'string' },
				 { name: 'shipmentTypeId', type: 'string' },
				 { name: 'statusId', type: 'string' },
				 { name: 'partyIdFrom', type: 'string' },
				 { name: 'partyIdTo', type: 'string' }
				],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
				sourceShip.totalrecords = data.TotalRows;
			},
			filter: function () {
				// update the grid and send a request to the server.
				$("#jqxGridShip").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#jqxGridShip").jqxGrid('updatebounddata');
			},
			sortcolumn: 'shipmentId',
			sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:5,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=getListShipment',
		};
	var dataAdapterShip = new $.jqx.dataAdapter(sourceShip);
	$("#shipmentId").jqxDropDownButton({ width: 200, height: 25});
	$("#jqxGridShip").jqxGrid({
		source: dataAdapterShip,
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
		          { text: '${uiLabelMap.shipmentId}', datafield: 'shipmentId' },
				  { text: '${uiLabelMap.shipmentTypeId}', datafield: 'shipmentTypeId' },
				  { text: '${uiLabelMap.statusId}', datafield: 'statusId' },
				  { text: '${uiLabelMap.partyIdFrom}', datafield: 'partyIdFrom' },
				  { text: '${uiLabelMap.partyIdTo}', datafield: 'partyIdTo' }
		         ]
			});
	$("#jqxGridShip").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxGridShip").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['shipmentId'] +'</div>';
		$('#shipmentId').jqxDropDownButton('setContent', dropDownContent);
	});
	
	
	// update the edited row when the user clicks the 'Save' button.
	$("#alterSave").click(function () {
		var row;
        row = { 
        		acctgTransTypeId:$('#acctgTransTypeId').val(), 
        		glFiscalTypeId:$('#glFiscalTypeId').val(),
        		partyId:$('#partyId').val(),
        		roleTypeId:$('#roleTypeId').val(),
        		invoiceId:$('#invoiceId').val(),
        		paymentId:$('#paymentId').val(),
        		productId:$('#productId').val(),
        		workEffortId:$('#workEffortId').val(),
        		shipmentId:$('#shipmentId').val(),
        		fixedAssetId:$('#fixedAssetId').val(),
        		debitGlAccountId:$('#debitGlAccountId').val(),
        		creditGlAccountId:$('#creditGlAccountId').val(),
        		amount:$('#amount').val(),
        		transactionDate:$('#transactionDate').jqxDateTimeInput('getDate').getTime(),
        		description:$('#description').val()
        	  };
	    $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
	}); 
</script>