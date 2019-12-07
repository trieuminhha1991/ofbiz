<script type="text/javascript">
	var dataInvoiceType = [<#if listInvoiceType?exists><#list listInvoiceType as type>{invoiceTypeId : "${type.invoiceTypeId}",description : "${StringUtil.wrapString(type.description)}"},</#list></#if>];
	var dataStatusType = [<#if listStatusItem?exists><#list listStatusItem as type>{statusId : "${type.statusId}",description : "${StringUtil.wrapString(type.description)}",},</#list></#if>];
</script>
<#assign dataField="[{ name: 'invoiceId', type: 'string' },
					{ name: 'invoiceTypeId', type: 'string' },
					{ name: 'invoiceDate', type: 'date', other:'Timestamp' },
					{ name: 'statusId', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'partyIdFrom', type: 'string' },
					{ name: 'billingAccountId', type: 'string' },
					{ name: 'partyId', type: 'string' },
					{ name: 'total', type: 'number' },
					{ name: 'currencyUomId', type: 'string' },
					{ name: 'amountToApply', type: 'number' },
					{ name: 'partyNameResultFrom', type: 'string' },
					{ name: 'partyNameResultTo', type: 'string' }]"/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', width:100, datafield: 'invoiceId', pinned: true,
						cellsrenderer: function(row, colum, value) {
							var data = $('#listCustomerInvoice').jqxGrid('getrowdata', row);
							return \"<span><a href='accApinvoiceOverview?invoiceId=\" + data.invoiceId + \"'>\" + data.invoiceId + \"</a></span>\";
						}
					},
					{ text: '${uiLabelMap.accBillingAccountId}', width:150, datafield: 'billingAccountId', hidden: true,
						createfilterwidget: function (column, columnElement, widget) {
							widget.width(140);
						}
					},
					{ text: '${uiLabelMap.accAccountingToParty}', width:300, datafield: 'partyId',
						cellsrenderer: function(row, colum, value){
							var data = $('#listCustomerInvoice').jqxGrid('getrowdata', row);
							return \"<span>\" + data.partyNameResultTo + '[' + data.partyId + ']' + \"</span>\";
						},
						createfilterwidget: function (column, columnElement, widget) {
							widget.width(140);
						}},
					{ text: '${uiLabelMap.accAccountingFromParty}', width:300, datafield: 'partyIdFrom',
						cellsrenderer: function(row, colum, value) {
							var data = $('#listCustomerInvoice').jqxGrid('getrowdata', row);
							return \"<span>\" + data.partyNameResultFrom + '[' + data.partyIdFrom + ']' + \"</span>\";
						},
						createfilterwidget: function (column, columnElement, widget) {
							widget.width(490);
						}},
					{ text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', filtertype: 'checkedlist', width:130, datafield: 'invoiceTypeId',
						cellsrenderer: function(row, colum, value) {
							for(i=0; i < dataInvoiceType.length;i++){
								if(value==dataInvoiceType[i].invoiceTypeId){
									return \"<span>\" + dataInvoiceType[i].description + \"</span>\";
								}
							}
							return \"<span>\" + value + \"</span>\";
						}, createfilterwidget: function (column, columnElement, widget) {
							var sourceIT =
							{
								localdata: dataInvoiceType,
								datatype: \"array\"
							};
							var filterBoxAdapter = new $.jqx.dataAdapter(sourceIT,
							{
								autoBind: true
							});
							var uniqueRecords = filterBoxAdapter.records;
							widget.jqxDropDownList({ filterable: true,source: uniqueRecords, displayMember: 'invoiceTypeId', valueMember : 'invoiceTypeId', renderer: function (index, label, value)
							{
								for(i=0;i < dataInvoiceType.length; i++){
									if(dataInvoiceType[i].invoiceTypeId == value){
										return dataInvoiceType[i].description;
									}
								}
								return value;
							}});
						}},
					{ text: '${uiLabelMap.FormFieldTitle_invoiceDate}', filtertype: 'range', width:130, datafield: 'invoiceDate', cellsformat: 'dd/MM/yyyy' },
					{ text: '${uiLabelMap.CommonStatus}', filtertype: 'checkedlist', width:120, datafield: 'statusId',
						 cellsrenderer: function(row, colum, value) {
							 for(i=0; i < dataStatusType.length;i++){
								 if(dataStatusType[i].statusId == value){
									 return \"<span>\" + dataStatusType[i].description + \"</span>\";
								 }
							 }
							 return value;
						 }, createfilterwidget: function (column, columnElement, widget) {
							var sourceST =
							{
									localdata: dataStatusType,
									datatype: \"array\"
							};
							var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {
								autoBind: true
							});
							var uniqueRecords2 = filterBoxAdapter2.records;
							widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'statusId', valueMember : 'statusId', renderer: function (index, label, value)
								{
									for(i=0;i < dataStatusType.length; i++){
										if(dataStatusType[i].statusId == value){
											return dataStatusType[i].description;
										}
									}
									return value;
								}});
						}},
					{ text: '${uiLabelMap.description}', width:150, datafield: 'description' },
					{ text: '${uiLabelMap.FormFieldTitle_total}', sortable:false, filterable: false, width:200, datafield: 'total',
						 cellsrenderer: function(row, colum, value){
							var data = $('#listCustomerInvoice').jqxGrid('getrowdata', row);
							return \"<span>\" + formatcurrency(data.total,data.currencyUomId) + \"</span>\";
						}
					 },
					{ text: '${uiLabelMap.FormFieldTitle_amountToApply}', sortable:false, filterable: false, width:200, datafield: 'amountToApply',
						 cellsrenderer: function(row, colum, value){
							var data = $('#listCustomerInvoice').jqxGrid('getrowdata', row);
							return \"<span>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</span>\";
						}
					 }"/>
<#if !timeout?exists>
	<#assign timeout="0"/>
</#if>
<#if !customLoadFunction?exists>
	<#assign customLoadFunction=""/>
</#if>
<#if !jqGridMinimumLibEnable?exists>
	<#assign jqGridMinimumLibEnable=""/>
</#if>

<@jqGrid url="" dataField=dataField columnlist=columnlist jqGridMinimumLibEnable=jqGridMinimumLibEnable
	 filterable="true" filtersimplemode="true" customLoadFunction=customLoadFunction timeout=timeout
	 showtoolbar="true"  id="listCustomerInvoice" selectionmode="checkbox" altrows="true"
	 defaultSortColumn="-invoiceDate" usecurrencyfunction="true" clearfilteringbutton="true"
	 autorowheight="true" viewSize="5" customTitleProperties="${uiLabelMap.ListInvoice}" isShowTitleProperty="false"/>
