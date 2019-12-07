<script>
	<#assign finAccTransTypes = delegator.findList("FinAccountTransType", null, null, null, null, false) >
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
</script>
<!-- HTML for Party Lookup -->
<div id="jqxwindowpartyId">
	<div>${uiLabelMap.SelectPartyId}</div>
	<div style="overflow: hidden;">
		<table id="PartyId">
			<tr>
				<td>
					<input type="hidden" id="jqxwindowpartyIdkey" value=""/>
					<input type="hidden" id="jqxwindowpartyIdvalue" value=""/>
					<div id="jqxgridpartyid"></div>
				</td>
			</tr>
			<tr>
	        	<td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave1" value="${uiLabelMap.CommonSave}" /><input id="alterCancel1" type="button" value="${uiLabelMap.CommonCancel}" /></td>
	        </tr>
	   </table>
	</div>
</div>

<@jqGridMinimumLib/>

<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	//Create Popup Window
	$("#jqxwindowpartyId").jqxWindow({
		theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
	});
	
	//Create Button
	$('#alterCancel1').jqxButton({theme: theme, width: 100});
	$('#alterSave1').jqxButton({theme: theme, width: 100});
	
	$('#jqxwindowpartyId').on('open', function (event) {
		var offset = $("#jqxgrid").offset();
		$("#jqxwindowpartyId").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});

	$("#alterSave1").click(function () {
		var tIndex = $('#jqxgridpartyid').jqxGrid('selectedrowindex');
		var data = $('#jqxgridpartyid').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowpartyIdkey').val()).val(data.partyId);
		$("#jqxwindowpartyId").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowpartyIdkey').val()).trigger(e);
	});
	// Prepare party source
	var sourceF =
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
				sourceF.totalrecords = data.TotalRows;
			},
			filter: function () {
				// update the grid and send a request to the server.
				$("#jqxgridpartyid").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#jqxgridpartyid").jqxGrid('updatebounddata');
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
			url: 'jqxGeneralServicer?sname=getPartyDistributor',
	};
	var dataAdapterF = new $.jqx.dataAdapter(sourceF,
			{
				autoBind: true,
				formatData: function (data) {
					if (data.filterscount) {
						var filterListFields = "";
						for (var i = 0; i < data.filterscount; i++) {
							var filterValue = data["filtervalue" + i];
							var filterCondition = data["filtercondition" + i];
							var filterDataField = data["filterdatafield" + i];
							var filterOperator = data["filteroperator" + i];
							filterListFields += "|OLBIUS|" + filterDataField;
							filterListFields += "|SUIBLO|" + filterValue;
							filterListFields += "|SUIBLO|" + filterCondition;
							filterListFields += "|SUIBLO|" + filterOperator;
						}
						data.filterListFields = filterListFields;
					}
				return data;
				},
				loadError: function (xhr, status, error) {
					alert(error);
				},
				downloadComplete: function (data, status, xhr) {
					if (!sourceF.totalRecords) {
						sourceF.totalRecords = parseInt(data['odata.count']);
					}
				}
			});
	$('#jqxgridpartyid').jqxGrid(
			{
				width:800,
				source: dataAdapterF,
				filterable: true,
				virtualmode: true, 
				sortable:true,
				editable: false,
				showfilterrow: false,
				theme: theme, 
				autoheight:true,
				pageable: true,
				pagesizeoptions: ['5', '10', '15'],
				ready:function(){
				},
				rendergridrows: function(obj)
				{
					return obj.data;
				},
				columns: [
				          { text: '${uiLabelMap.accApInvoice_ToPartyId}', datafield: 'partyId', width:150},
				          { text: '${uiLabelMap.accApInvoice_ToPartyTypeId}', datafield: 'partyTypeId', width:200},
				          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width:150},
				          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName', width:150},
				          { text: '${uiLabelMap.accAccountingToParty}', datafield: 'groupName', width:150}
				         ]
			});

	$(document).keydown(function(event){
		if(event.ctrlKey)
			cntrlIsPressed = true;
	});

	$(document).keyup(function(event){
		if(event.which=='17')
			cntrlIsPressed = false;
	});
	var cntrlIsPressed = false;
</script>
<#assign dataField="[{ name: 'finAccountId', type: 'string' },
					{ name: 'finAccountTransId', type: 'string' },
                 	{ name: 'finAccountTransTypeId', type: 'string' },
                 	{ name: 'partyId', type: 'string' },
					{ name: 'glReconciliationName', type: 'string' },
					{ name: 'transactionDate', type: 'date', other: 'Timestamp' },
					{ name: 'entryDate', type: 'date', other: 'Timestamp' },
					{ name: 'amount', type: 'number' },
					{ name: 'paymentId', type: 'string' },
					{ name: 'orderId', type: 'string' },
					{ name: 'orderItemSeqId', type: 'string' },
					{ name: 'performedByPartyId', type: 'string' },
					{ name: 'reasonEnumId', type: 'string' },
					{ name: 'comments', type: 'string' },
					{ name: 'statusId', type: 'string' },
		 		 	]"/>
<#assign columnlist="{ text: '${uiLabelMap.finAccountId}', dataField: 'finAccountId', width: '150', filterable: false},
					 { text: '${uiLabelMap.finAccountTransTypeId}', dataField: 'finAccountTransTypeId', width: '150', filtertype:'checkedlist',
						cellsrenderer: function(row, column, value){
							for(i = 0; i < finAccTransTypeData.length; i++){
								if(finAccTransTypeData[i].finAccountTransTypeId == value){
									return '<span title=' + value +'>' + finAccTransTypeData[i].description + '</span>'
								}
							}
							return ;
						},
						createfilterwidget: function(column, columnElement, widget){
							var filterBoxAdapter = new $.jqx.dataAdapter(finAccTransTypeData,{
					                 autoBind: true
					             });
					        var uniqueRecords = filterBoxAdapter.records;
					   		uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: uniqueRecords, displayMember: 'finAccountTransTypeId', valueMember: 'finAccountTransTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < uniqueRecords.length; i++){
										if(uniqueRecords[i].finAccountTransTypeId == value){
											return uniqueRecords[i].description;
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
					 },
					 { text: '${uiLabelMap.accPartyId}', dataField: 'partyId', width: '150', filtertype: 'olbiusdropgrid',
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
					 { text: '${uiLabelMap.glReconciliationName}', dataField: 'glReconciliationName', width: '150', filterable: false},
					 { text: '${uiLabelMap.transactionDate}', dataField: 'transactionDate', width: '150', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					 { text: '${uiLabelMap.entryDate}', dataField: 'entryDate', width: '150', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					 { text: '${uiLabelMap.amount}', dataField: 'amount', width: '150', filterable: false},
					 { text: '${uiLabelMap.paymentId}', dataField: 'paymentId', width: '150', filterable: false},
					 { text: '${uiLabelMap.orderId}', dataField: 'orderId', width: '150', filterable: false},
					 { text: '${uiLabelMap.orderItemSeqId}', dataField: 'orderItemSeqId', width: '150', filterable: false},
					 { text: '${uiLabelMap.performedByPartyId}', dataField: 'performedByPartyId', width: '150', filterable: false},
					 { text: '${uiLabelMap.reasonEnumId}', dataField: 'reasonEnumId', width: '150', filterable: false},
					 { text: '${uiLabelMap.comments}', dataField: 'comments', width: '150', filterable: false},
					 { text: '${uiLabelMap.statusId}', dataField: 'statusId', width: '350', filtertype: 'checkedlist',
	                      aggregates: [
	                                   	{ '<span style=\"color: red\">${uiLabelMap.FormFieldTitle_grandTotal} / ${uiLabelMap.AccountingNumberOfTransaction}</span>':
	                                   		function (aggregatedValue, currentValue) {
	                                   			var rows = $('#jqxgrid').jqxGrid('getrows');
	                                   			var amount = 0;
	                                   			var transNumber = 0;
	                                   			for(i = 0; i < rows.length; i++){
	                                   				transNumber += 1;
	                                   				if(rows[i].finAccountTransTypeId == 'DEPOSIT' || rows[i].finAccountTransTypeId == 'ADJUSTMENT'){
	                                   					amount += rows[i].amount; 
	                                   				}else{
	                                   					amount -= rows[i].amount;
	                                   				}
	                                   			}
	                                   			return '<span style=\"color: red\"> ' + amount + '/' + transNumber + '</span>';
	                                   	}
	                                   	},
	                                   	{ '<span style=\"color: red\">${uiLabelMap.AccountingCreatedGrandTotal} / ${uiLabelMap.AccountingNumberOfTransaction}</span>':
	                                   		function (aggregatedValue, currentValue) {
	                                   			var rows = $('#jqxgrid').jqxGrid('getrows');
	                                   			var amount = 0;
	                                   			var transNumber = 0;
	                                   			for(i = 0; i < rows.length; i++){
	                                   				if(rows[i].statusId == 'FINACT_TRNS_CREATED'){
	                                   					transNumber += 1;
	                                   					if(rows[i].finAccountTransTypeId == 'DEPOSIT' || rows[i].finAccountTransTypeId == 'ADJUSTMENT'){
	                                   						amount += rows[i].amount; 
	                                   					}else{
	                                   						amount -= rows[i].amount;
	                                   					}
	                                   				}
	                                   			}
	                                   			return '<span style=\"color: red\"> ' + amount + '/' + transNumber + '</span>';
	                                   	}
	                                   	},
	                                   	{ '<span style=\"color: red\">${uiLabelMap.AccountingApprovedGrandTotal} / ${uiLabelMap.AccountingNumberOfTransaction}</span>':
	                                   		function (aggregatedValue, currentValue) {
	                                   		var rows = $('#jqxgrid').jqxGrid('getrows');
	                                   		var amount = 0;
	                                   		var transNumber = 0;
	                                   		for(i = 0; i < rows.length; i++){
	                                   			if(rows[i].statusId == 'FINACT_TRNS_APPROVED'){
	                                   				transNumber += 1;
	                                   				if(rows[i].finAccountTransTypeId == 'DEPOSIT' || rows[i].finAccountTransTypeId == 'ADJUSTMENT'){
	                                   					amount += rows[i].amount; 
	                                   				}else{
	                                   					amount -= rows[i].amount;
	                                   				}
	                                   			}
	                                   		}
	                                   		return '<span style=\"color: red\"> ' + amount + '/' + transNumber + '</span>';
	                                   	}
	                                  },
	                                  { '<span style=\"color: red\">${uiLabelMap.AccountingCreatedApprovedGrandTotal} / ${uiLabelMap.AccountingNumberOfTransaction}</span>':
	                                	  function (aggregatedValue, currentValue) {
			                        	  	var rows = $('#jqxgrid').jqxGrid('getrows');
			                        	  	var amount = 0;
			                        	  	var transNumber = 0;
			                        	  	for(i = 0; i < rows.length; i++){
			                        	  		if(rows[i].statusId == 'FINACT_TRNS_APPROVED' || rows[i].statusId == 'FINACT_TRNS_CREATED'){
			                        			  transNumber += 1;
			                        			  if(rows[i].finAccountTransTypeId == 'DEPOSIT' || rows[i].finAccountTransTypeId == 'ADJUSTMENT'){
			                        				  amount += rows[i].amount; 
			                        			  }else{
			                        				  amount -= rows[i].amount;
			                        			  }
			                        		  }
			                        	  	}
			                        	  return '<span style=\"color: red\"> ' + amount + '/' + transNumber + '</span>';
			                            }
	                                 },
	                      ],
	                      cellsrenderer: function(row, column, value){
	                    	  for(i = 0; i < statusData.length; i++){
	                    		  if(statusData[i].statusId == value){
	                    			  return '<span title=' + value + '>' + statusData[i].description + '</span>'
	                    		  }
	                    	  }
	                    	  return ;
	                      },
	                      createfilterwidget: function(column, columnElement, widget){
	                    	var filterBoxAdapter = new $.jqx.dataAdapter(statusData,{
					                 autoBind: true
					             });
					        var uniqueRecords = filterBoxAdapter.records;
					   		uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
	                    	  widget.jqxDropDownList({source: uniqueRecords, displayMember: 'statusId', valueMember: 'statusId',
	                    		  renderer: function(index, label, value){
	                    			  for(var i = 0; i < uniqueRecords.length; i++){
	                    				  if(uniqueRecords[i].statusId == value){
	                    					 return uniqueRecords[i].description; 
	                    				  }
	                    			  }
	                    			  return value;
	                    		  }
	                    	  });
	                    	  widget.jqxDropDownList('checkAll');
	                      }
					 },
					 { text: '${uiLabelMap.cancelTransactionStatus}', width: 200, filterable: false,
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
<@jqGrid filtersimplemode="true" addType="popup" showstatusbar="true" statusbarheight='100' dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" deleterow="false" editable="false" 
		 url="jqxGeneralServicer?sname=JQListFinAccountTrans&finAccountId=${parameters.finAccountId}" id="jqxgrid" addrefresh="true"
		 createUrl="jqxGeneralServicer?sname=createFinAccountTrans&finAccountId=${parameters.finAccountId}&jqaction=C" addColumns="finAccountId[${parameters.finAccountId}];finAccountTransTypeId;partyId;glReconciliationId;transactionDate(java.sql.Timestamp);entryDate(java.sql.Timestamp);amount(java.math.BigDecimal);paymentId;orderId;orderItemSeqId;reasonEnumId;comments;statusId;glAccountId"
		/>
					 
<!--HTML for add form -->
<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupWindow" style="display:none;">
			<div id="windowHeader">
	            <span>
	               ${uiLabelMap.NewFinAccountTrans} [${parameters.finAccountId}]
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContent">
		        <div class="basic-form form-horizontal" style="margin-top: 10px">
					<form name="formNew" id="formNew">	
						<div class="row-fluid" >
							<div class="span12">
								<div class='span6'>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.finAccountTransTypeId}:</label>
										<div class="controls">
											<div id="finAccountTransTypeId"></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.accPartyId}:</label>  
										<div class="controls">
											<div id="partyId">
										 		<div id="jqxPartyGrid" ></div>
										 	</div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.glReconciliationId}:</label>
										<div class="controls">
											<input id="glReconciliationId"></input>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.transactionDate}:</label>  
										<div class="controls">
											<div id="transactionDate"></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.entryDate}:</label>  
										<div class="controls">
											<div id="entryDate"></div>
										</div>
									</div>
									
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.amount}:</label>  
										<div class="controls">
											<input id="amount"></input>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.paymentId}:</label>  
										<div class="controls">
											<div id="paymentId">
							 					<div id="jqxGridPay" ></div>
							 				</div>
										</div>
									</div>
								</div>
								<div class='span6'>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.orderId}:</label>  
										<div class="controls">
											<div id="orderId">
						 						<div id="jqxGridOrder"></div>
						 					</div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.accOrderItemSeqId}:</label>  
										<div class="controls">
											<input id="orderItemSeqId">
					 						</input>
										</div>
									</div>
									
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.reasonEnumId}:</label>  
										<div class="controls">
											<input id="reasonEnumId">
											</input>
										</div>
									</div>
									
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.comments}:</label>  
										<div class="controls">
											<input id="comments">
											</input>
										</div>
									</div>
									
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.statusId}:</label>  
										<div class="controls">
											<div id="statusId">
											</div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.glAccountId}:</label>  
										<div class="controls">
											<div id="glAccountId">
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
				<div class="form-action">
					<div class='row-fluid'>
						<div class="span12 margin-top10">
							<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
							<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
						</div>
					</div>
				</div>
	        </div>
		</div>
	</div>
</div>

<script>
	//Create theme
	$.jqx.theme='olbius';
	theme = $.jqx.theme;

	//Create Window popup
	$('#alterpopupWindow').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "95%", height: 450, minWidth: '40%', width: "90%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel'});

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
	$('#statusId').jqxDropDownList({width: 200, selectedIndex: 0, source: statusData, displayMember: 'description', valueMember: 'statusId', theme: theme});
	
	//Create statusId
	$('#glAccountId').jqxDropDownList({width: 200, selectedIndex: 0, source: glAccountData, displayMember: 'description', valueMember: 'glAccountId', theme: theme});

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
	var dataAdapterOrder = new $.jqx.dataAdapter(sourceOrder,{
    	autoBind: true,
    	formatData: function (data) {
    		if (data.filterscount) {
                var filterListFields = "";
                for (var i = 0; i < data.filterscount; i++) {
                    var filterValue = data["filtervalue" + i];
                    var filterCondition = data["filtercondition" + i];
                    var filterDataField = data["filterdatafield" + i];
                    var filterOperator = data["filteroperator" + i];
                    filterListFields += "|OLBIUS|" + filterDataField;
                    filterListFields += "|SUIBLO|" + filterValue;
                    filterListFields += "|SUIBLO|" + filterCondition;
                    filterListFields += "|SUIBLO|" + filterOperator;
                }
                data.filterListFields = filterListFields;
            }else{
            	data.filterListFields = null;
            }
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourceOrder.totalRecords) {
                	sourceOrder.totalRecords = parseInt(data['odata.count']);
                }
        }
    });
	$("#orderId").jqxDropDownButton({ width: 200, height: 25});
	$("#jqxGridOrder").jqxGrid({
		source: dataAdapterOrder,
		filterable: true,
		showfilterrow: true,
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
		          { text: '${uiLabelMap.orderId}', datafield: 'orderId', width: 150},
				  { text: '${uiLabelMap.orderTypeId}', datafield: 'orderTypeId', width: 150},
				  { text: '${uiLabelMap.orderName}', datafield: 'orderName', width: 150},
				  { text: '${uiLabelMap.statusId}', datafield: 'statusId', width: 150},
		         ]
			});
	$("#jqxGridOrder").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxGridOrder").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['orderId'] +'</div>';
		$("#orderId").jqxDropDownButton('close');
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
	var dataAdapterPay = new $.jqx.dataAdapter(sourcePay,{
    	autoBind: true,
    	formatData: function (data) {
    		if (data.filterscount) {
                var filterListFields = "";
                for (var i = 0; i < data.filterscount; i++) {
                    var filterValue = data["filtervalue" + i];
                    var filterCondition = data["filtercondition" + i];
                    var filterDataField = data["filterdatafield" + i];
                    var filterOperator = data["filteroperator" + i];
                    filterListFields += "|OLBIUS|" + filterDataField;
                    filterListFields += "|SUIBLO|" + filterValue;
                    filterListFields += "|SUIBLO|" + filterCondition;
                    filterListFields += "|SUIBLO|" + filterOperator;
                }
                data.filterListFields = filterListFields;
            }else{
            	data.filterListFields = null;
            }
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourcePay.totalRecords) {
                	sourcePay.totalRecords = parseInt(data['odata.count']);
                }
        }
    });
	$("#paymentId").jqxDropDownButton({height: 25});
	$("#jqxGridPay").jqxGrid({
		source: dataAdapterPay,
		filterable: true,
		showfilterrow: true,
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
		          { text: '${uiLabelMap.paymentId}', datafield: 'paymentId', width: 100},
				  { text: '${uiLabelMap.partyIdFrom}', datafield: 'partyIdFrom', width: 100},
				  { text: '${uiLabelMap.partyIdTo}', datafield: 'partyIdTo', width: 100},
				  { text: '${uiLabelMap.effectiveDate}', datafield: 'effectiveDate', width: 100},
				  { text: '${uiLabelMap.amount}', datafield: 'amount', width: 100},
				  { text: '${uiLabelMap.currencyUomId}', datafield: 'currencyUomId', width: 100}
		         ]
			});
	$("#jqxGridPay").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxGridPay").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['paymentId'] +'</div>';
		$("#paymentId").jqxDropDownButton('close');
		$('#paymentId').jqxDropDownButton('setContent', dropDownContent);
	});
	
	//Create partyId
	var sourceP =
	{
		datafields:
			[
			 { name: 'partyId', type: 'string' },
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
	var dataAdapterP = new $.jqx.dataAdapter(sourceP,{
    	autoBind: true,
    	formatData: function (data) {
    		if (data.filterscount) {
                var filterListFields = "";
                for (var i = 0; i < data.filterscount; i++) {
                    var filterValue = data["filtervalue" + i];
                    var filterCondition = data["filtercondition" + i];
                    var filterDataField = data["filterdatafield" + i];
                    var filterOperator = data["filteroperator" + i];
                    filterListFields += "|OLBIUS|" + filterDataField;
                    filterListFields += "|SUIBLO|" + filterValue;
                    filterListFields += "|SUIBLO|" + filterCondition;
                    filterListFields += "|SUIBLO|" + filterOperator;
                }
                data.filterListFields = filterListFields;
            }else{
            	data.filterListFields = null;
            }
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourceP.totalRecords) {
                	sourceP.totalRecords = parseInt(data['odata.count']);
                }
        }
    });
	$("#partyId").jqxDropDownButton({height: 25});
	$("#jqxPartyGrid").jqxGrid({
		source: dataAdapterP,
		filterable: true,
		showfilterrow: true,
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
		          { text: '${uiLabelMap.accPartyId}', datafield: 'partyId', width: 150},
		          { text: '${uiLabelMap.firstName}', datafield: 'firstName', width: 150},
		          { text: '${uiLabelMap.lastName}', datafield: 'lastName', width: 150},
		          { text: '${uiLabelMap.groupName}', datafield: 'groupName', width: 150}
		          ]
		});
	$("#jqxPartyGrid").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxPartyGrid").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
		$('#partyId').jqxDropDownButton('close');
		$('#partyId').jqxDropDownButton('setContent', dropDownContent);
	});
	
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