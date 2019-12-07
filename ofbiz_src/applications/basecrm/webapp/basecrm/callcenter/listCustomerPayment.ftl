<script type="text/javascript">
	var dataStatusItem = [<#list listStatusItem as item>{
		"statusId" : "${StringUtil.wrapString(item.statusId?if_exists)}",
		"description" : "${StringUtil.wrapString(item.description?if_exists)}"
	},</#list>];
	var dataPaymentType = [<#list listPaymentType as item>{
		"paymentTypeId" : "${StringUtil.wrapString(item.paymentTypeId?if_exists)}",
		"description" : "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list>];
	var dataPAGA = [<#list listPartyAcctgPrefAndGroup as lpaga>{
		partyId : "${lpaga.partyId}",
		groupName : "${lpaga.groupName}"
	},</#list>];
	var dataPTE = [<#list listPaymentTypeExport as lpte><#assign des = lpte.get("description",locale)/>{
		paymentTypeId : "${lpte.paymentTypeId}",
		description : "${StringUtil.wrapString(des)}"
	},</#list>];
	var dataPM = [<#list listPaymentMethod as lpm>{
		paymentMethodId : "${lpm.paymentMethodId}",
		description : "${lpm.description}"
	},</#list>];
	var dataFA = [<#list listFinAccount as lfa>{
		finAccountId : "${lfa.finAccountId}",
		description : "${lfa.finAccountName}[${lfa.finAccountId}]"
	},</#list>];
</script>

<#assign dataField="[{ name: 'paymentId', type: 'string' },
					{ name: 'paymentTypeId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'comments', type: 'string' },
					{ name: 'partyIdFrom', type: 'string' },
					{ name: 'partyIdTo', type: 'string' },
					{ name: 'fullNameFrom', type: 'string' },
					{ name: 'fullNameTo', type: 'string' },
					{ name: 'effectiveDate', type: 'date', other:'Timestamp' },
					{ name: 'amount', type: 'number' },
					{ name: 'amountToApply', type: 'number' },
					{ name: 'currencyUomId', type: 'string' }]"/>

<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_paymentId}', width:120, filtertype:'input', datafield: 'paymentId',
						cellsrenderer: function(row, colum, value){
							return \"<span><a href='/delys/control/accAppaymentOverview?paymentId=\" + value + \"'>\" + value + \"</a></span>\";
						}
					},
					{ text: '${uiLabelMap.FormFieldTitle_paymentTypeId}', filtertype: 'checkedlist', width:200, datafield: 'paymentTypeId',
						cellsrenderer: function(row, colum, value){
							for(i=0; i<dataPaymentType.length;i++){
								if(dataPaymentType[i].paymentTypeId==value){
									return \"<span>\" + dataPaymentType[i].description +\"</span>\";
								}
							}
							return \"<span>\" + value + \"</span>\";
						}, createfilterwidget: function (column, columnElement, widget) {
							var sourcePT =
							{
								localdata: dataPaymentType,
								datatype: \"array\"
							};
							var filterBoxAdapterPT = new $.jqx.dataAdapter(sourcePT, {
								autoBind: true
							});
							var uniqueRecordsPT = filterBoxAdapterPT.records;
							widget.jqxDropDownList({filterable: true, dropDownWidth: 300, autoDropDownHeight: false, dropDownHeight: 300, source: uniqueRecordsPT, displayMember: 'paymentTypeId', valueMember : 'paymentTypeId', renderer: function (index, label, value)
								{
								for(i=0;i < dataPaymentType.length; i++){
									if(dataPaymentType[i].paymentTypeId == value){
										return dataPaymentType[i].description;
									}
								}
								return value;
								}});
						}
					},
					{ text: '${uiLabelMap.FormFieldTitle_statusId}', filtertype: 'checkedlist', width:180, datafield: 'statusId',
						cellsrenderer: function(row, colum, value){
							for(i=0; i<dataStatusItem.length;i++){
								if(dataStatusItem[i].statusId==value){
									return \"<span>\" + dataStatusItem[i].description +\"</span>\";
								}
							}
							return \"<span>\" + value + \"</span>\";
						}, createfilterwidget: function (column, columnElement, widget) {
							var sourceSI = {
								localdata: dataStatusItem,
								datatype: \"array\"
							};
							var filterBoxAdapterSI = new $.jqx.dataAdapter(sourceSI, {
								autoBind: true
							});
							var uniqueRecordsSI = filterBoxAdapterSI.records;
							widget.jqxDropDownList({ source: uniqueRecordsSI, displayMember: 'statusId', valueMember : 'statusId', height: '25px',renderer: function (index, label, value)
							{
								for(i=0;i < dataStatusItem.length; i++){
									if(dataStatusItem[i].statusId == value){
										return dataStatusItem[i].description;
									}
								}
								return value;
							}});
						}
					},
					{ text: '${uiLabelMap.accAccountingToParty}', width:300, datafield: 'partyIdFrom', displayfield: 'fullNameFrom',
						createfilterwidget: function (column, columnElement, widget) {
							widget.width(290);
						}
					},
					{ text: '${uiLabelMap.accAccountingFromParty}', width:300, datafield: 'partyIdTo', displayfield: 'fullNameTo',
						createfilterwidget: function (column, columnElement, widget) {
							widget.width(290);
						}
					},
					{ text: '${uiLabelMap.FormFieldTitle_effectiveDate}', filtertype: 'range', width:100, datafield: 'effectiveDate', cellsformat: 'dd/MM/yyyy' },
					{ text: '${uiLabelMap.FormFieldTitle_amount}', width:150, cellsalign: 'right', filtertype: 'number',datafield: 'amount', cellsrenderer:
						function(row, colum, value){
							var data = $('#listCustomerPayment').jqxGrid('getrowdata', row);
							return \"<div><div class='pull-right'>\" + formatcurrency(data.amount,data.currencyUomId) + \"</div></div>\";
						}
					},
					{ text: '${uiLabelMap.FormFieldTitle_amountToApply}', width:150, cellsalign: 'right',filtertype: 'number', datafield: 'amountToApply', cellsrenderer:
						function(row, colum, value){
							var data = $('#listCustomerPayment').jqxGrid('getrowdata', row);
							return \"<div><div class='pull-right'>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</div></div>\";
						}
					},
					{ text: '${uiLabelMap.FormFieldTitle_comments}', filtertype:'input', width: 300, datafield: 'comments' }"/>

<script type="text/javascript">
	var globalPaymentType = "I";
</script>
<#if !timeout?exists>
	<#assign timeout="0"/>
</#if>
<#if !customLoadFunction?exists>
	<#assign customLoadFunction="false"/>
</#if>
<#if !jqGridMinimumLibEnable?exists>
	<#assign jqGridMinimumLibEnable=""/>
</#if>

<@jqGrid url="" dataField=dataField columnlist=columnlist customLoadFunction=customLoadFunction timeout=timeout
	clearfilteringbutton="true" showtoolbar="true" autorowheight="true" id="listCustomerPayment" isShowTitleProperty="false"
	jqGridMinimumLibEnable=jqGridMinimumLibEnable filterable="true" filtersimplemode="true"  customTitleProperties="${uiLabelMap.ListPayment}"
	otherParams="amountToApply:S-getListApPayment(inputValue{paymentId})<outputValue>" usecurrencyfunction="true" />
