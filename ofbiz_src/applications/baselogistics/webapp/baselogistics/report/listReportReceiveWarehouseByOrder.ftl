<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>

<script>
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, true)>
	var mapUomData = {<#if uoms?exists><#list uoms as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	var uomData = [<#if uoms?exists><#list uoms as item>{
		uomId: "${item.uomId}",
		description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
	},</#list></#if>];
	
	<#assign facilitys = delegator.findList("Facility", null, null, null, null, true)>
	var mapFacilityData = {<#if facilitys?exists><#list facilitys as item>
		"${item.facilityId?if_exists}": "${StringUtil.wrapString(item.get("facilityName", locale)?if_exists)}",
	</#list></#if>};
</script>

<#assign dataField="[
				{ name: 'orderId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'productName', type: 'string'},
				{ name: 'facilityId', type: 'string'},
				{ name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
				{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
				{ name: 'quantity', type: 'number'},
				{ name: 'quantityUomId', type: 'string'},
				{ name: 'originFacilityId', type: 'string'},
				{ name: 'fullName', type: 'string'},
			]"/>
<#assign columnlist="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
				},
				{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', width: 150, pinned: true },
				{ text: '${uiLabelMap.ProductName}', datafield: 'productName', minwidth: 200 },
				{ text: '${uiLabelMap.DateExpectedReceiveWarehousing}', dataField: 'shipAfterDate', width: 200, cellsformat: 'dd/MM/yyyy', filtertype:'range' },
				{ text: '${uiLabelMap.ReceiveQuantityExpected}', datafield: 'quantity', width: 120, cellsalign: 'right', filtertype: 'number',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						if(value){
							return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
						}
					}
				},
				{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', width: 120, filtertype: 'checkedlist',
					cellsrenderer: function (row, column, value){
						value = value?mapUomData[value]:value;
						return '<div style=margin:4px;>' + value + '</div>';
					},
					createfilterwidget: function (column, columnElement, widget) {
						widget.jqxDropDownList({ source: uomData, displayMember: 'description', valueMember: 'uomId' });
		   			}
				},
				{ text: '${uiLabelMap.OrderId}', datafield: 'orderId', width: 150 },
				{ text: '${uiLabelMap.LogWarehouse}', datafield: 'originFacilityId', width: 150,
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						if(value){
							return '<span>' + mapFacilityData[value] + '</span>';
						}
					}
				},
				{ text: '${uiLabelMap.CreatedBy}', datafield: 'fullName', width: 250 },
			"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdReportReceive" addrefresh="true" filterable="true"
	url="jqxGeneralServicer?sname=JQGetReportReceiveWarehouseByOrder"
	customTitleProperties="WarehousingReceiveReportExpectedUnderPurchaseOrder" />	