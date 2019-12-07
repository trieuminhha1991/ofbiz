<script src="/crmresources/js/generalUtils.js"></script>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "component://baseecommerce/webapp/baseecommerce/backend/content/viewImageOnGrid.ftl"/>
<#assign dataField="[{ name: 'costAccDepId', type: 'string' },
					 { name: 'costAccMapDepId', type: 'string' },
					 { name: 'orderId', type: 'string' },
					 { name: 'invoiceId', type: 'string' },
					 { name: 'partyId', type: 'string' },
					 { name: 'partyName', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'costPriceTemporary', type: 'number' },
					 { name: 'costPriceActual', type: 'number' },
					 { name: 'currencyUomId', type: 'string' },
					 { name: 'statusId', type: 'string' },
					 { name: 'pathScanFile', type: 'string' },
					 { name: 'costAccDate', type: 'date', other: 'Timestamp' },
					 { name: 'createdByUserLogin', type: 'string' },
					 { name: 'invoiceItemTypeId', type: 'string' },
					 { name: 'invoiceItemTypeName', type: 'string' },
					 { name: 'changedByUserLogin', type: 'string' }]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
							cellsrenderer: function (row, column, value) {
								return '<div style=margin:4px;>' + (row + 1) + '</div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.DatetimeCreated)}', datafield: 'costAccDate', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
						{ text: '${StringUtil.wrapString(uiLabelMap.invoiceItemTypeId)}', datafield: 'invoiceItemTypeId', filtertype: 'checkedlist', width: 200,
							cellsrenderer: function(row, column, value, a, b, data){
								return '<div style=margin:4px;>' + data.invoiceItemTypeName + '</div>';
							},
							createfilterwidget: function (column, htmlElement, editor) {
						    	editor.jqxDropDownList({ source: dataAdapterInvoiceItemType, displayMember: 'invoiceItemTypeName', valueMember: 'invoiceItemTypeId', autoDropDownHeight: false });
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.AmountMoney)}', datafield: 'costPriceActual', filtertype: 'number', width: 150,
							cellsrenderer: function (row, column, value) {
								return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.ProductCurrency)}', datafield: 'currencyUomId', filtertype: 'checkedlist', width: 130,
							cellsrenderer: function(row, column, value, a, b, data){
								value = value?mapCurrencyUom[value]:value;
								return '<div style=margin:4px;>' + value + '</div>';
							},
							createfilterwidget: function (column, htmlElement, editor) {
						    	editor.jqxDropDownList({ source: currencyUoms, displayMember: 'description', valueMember: 'currencyUomId', autoDropDownHeight: false });
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.Description)}', datafield: 'description', width: 200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.BLPartyName)}', datafield: 'partyName', width: 200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.CreatedBy)}', datafield: 'createdByUserLogin', width: 200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.LastChangedBy)}', datafield: 'changedByUserLogin', width: 200 },
						{text: '${uiLabelMap.BSLinkImage}',datafield: 'pathScanFile', width: 130, filterable: false, sortable: false,
							cellsrenderer:  function (row, column, value, a, b, data){
								return value.picturable();
							}
						}
						"/>

<@jqGrid url="jqxGeneralServicer?sname=jqGetCostByDepartment" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
	 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" 
	 customcontrol1="icon-plus open-sans@${uiLabelMap.BSAddNew}@javascript:AddCostByDepartment.open()"
	 addrow="false" contextMenuId="contextMenu" mouseRightMenu="true"/>

<#include "component://basesalesmtl/webapp/basesalesmtl/supervisor/popup/addCostByDepartment.ftl"/>
 
<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
<script>
var source = { datatype: "json",
		datafields: [{ name: "costAccMapDepId" },
		             { name: "departmentId" },
		             { name: "invoiceItemTypeName" }],
		             url: "loadInvoiceItemTypeByDepartment"};

var dataAdapterInvoiceItemType = new $.jqx.dataAdapter(source, {async: false});

var currencyUoms = [<#if currencyUoms?has_content><#list currencyUoms as item>{
                    	currencyUomId: "${item.uomId?if_exists}",
                    	description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
                    },</#list></#if>];
var mapCurrencyUom = {<#if currencyUoms?has_content><#list currencyUoms as item>
	"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get('description', locale)?if_exists)}",
</#list></#if>};

multiLang = _.extend(multiLang, {
	BACCCustomerId: "${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}",
	BACCFullName: "${StringUtil.wrapString(uiLabelMap.BACCFullName)}",
	});
var urlGetParty = "${urlGetParty?if_exists}";
</script>