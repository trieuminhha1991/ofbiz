<#--Import LIB-->
<#--/Import LIB-->
<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for uom data
	<#assign statusList = delegator.findList("StatusItem", null, null, null, null, false)>
	var statusData = [
		<#list statusList as item>
			{
				<#assign description = StringUtil.wrapString(item.description) />
				statusId : '${item.statusId}',
				description : '${description}',
			},
		</#list>
	]
	
	//Prepare for role type data
	<#assign invoiceTypeList = delegator.findList("InvoiceType", null, null, null, null, false) />
	invoiceTypeData = [
	              <#list invoiceTypeList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'invoiceTypeId': '${item.invoiceTypeId}', 'description': '${description}'},
				  </#list>
				];
</script>
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'billingAccountId', type: 'string'},
					 { name: 'invoiceId', type: 'string'},
					 { name: 'invoiceTypeId', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', datafield: 'invoiceId', editable: false, editable: 'false',
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							return '<span title=' + value + '><a href=accArinvoiceOverview?invoiceId=' + value + '>' + value + '</a></span>';
						}
					 },
                     { text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', datafield: 'invoiceTypeId', filtertype: 'checkedlist', width: 250, editable: 'false',
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	 							for(var i = 0; i < invoiceTypeData.length; i++){
	 								if(value == invoiceTypeData[i].invoiceTypeId){
	 									return '<span title=' + value + '>' + invoiceTypeData[i].description + '</span>';
	 								}
	 							}
	 							return '<span> ' + value + '</span>';
 						},
 						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(invoiceTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'invoiceTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < invoiceTypeData.length; i++){
										if(invoiceTypeData[i].invoiceTypeId == value){
											return '<span>' + invoiceTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
                     },
                     { text: '${uiLabelMap.statusId}', datafield: 'statusId', width: 250, editable: 'false', filtertype: 'checkedlist',
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	 							for(var i = 0; i < statusData.length; i++){
	 								if(value == statusData[i].statusId){
	 									return '<span title=' + value + '>' + statusData[i].description + '</span>';
	 								}
	 							}
	 							return '<span> ' + value + '</span>';
 						},
 						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'statusId',
								renderer: function(index, label, value){
									for(var i = 0; i < statusData.length; i++){
										if(statusData[i].statusId == value){
											return '<span>' + statusData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
                     },
                     { text: '${uiLabelMap.description}', datafield: 'description', width: 150, editable: 'false'},
                     { text: '${uiLabelMap.partyIdFrom}', datafield: 'partyIdFrom', width: 150, editable: 'false',
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
                     { text: '${uiLabelMap.partyIdTo}', datafield: 'partyId', width: 150, editable: 'false',
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
                     { text: '${uiLabelMap.FormFieldTitle_total}', datafield: 'total', width: 150, editable: 'false',
                    	 cellsrenderer: function(row, column, value){
							  var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
							  $.ajax({
									url: 'getInvoiceTotal',
									type: 'POST',
									data: {invoiceId: rowData['invoiceId']},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											total = data.total;
										}
							        }
								});
							  return '<span >' + formatcurrency(total,'${defaultCurrencyUomId?if_exists}') + '</span>';
						}
                     },
                     { text: '${uiLabelMap.FormFieldTitle_amountToApply}', datafield: 'amountToApply', width: 150, editable: 'false',
                    	 cellsrenderer: function(row, column, value){
							  var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
							  $.ajax({
									url: 'getInvoiceNotApplied',
									type: 'POST',
									data: {invoiceId: rowData['invoiceId']},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											total = data.total;
										}
							        }
								});
							  return '<span >' + formatcurrency(total,'${defaultCurrencyUomId?if_exists}') + '</span>';
						}
                     }
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrow="false" addrefresh="true" editable="false" deleterow="false" addType="popup" alternativeAddPopup="wdwNewBillingAccRole" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListBillingAccountInvoices&billingAccountId=${parameters.billingAccountId}" dataField=dataField columnlist=columnlist
		 />
                     
<#--=================================/Init Grid======================================================-->
<#--====================================================/Setup JS======================================-->