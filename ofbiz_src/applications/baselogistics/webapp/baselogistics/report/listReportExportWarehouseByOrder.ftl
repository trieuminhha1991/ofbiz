<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>

<script>
	<#assign uomTypes = ["WEIGHT_MEASURE", "PRODUCT_PACKING"]>
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, uomTypes), null, null, null, true)>
	var mapUomData = {<#if uoms?exists><#list uoms as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	var uomData = [<#if uoms?exists><#list uoms as item>{
		uomId: "${item.uomId}",
		description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
	},</#list></#if>];
	
	<#assign priorities = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "ORDER_PRIORITY")), null, null, null, true)>
	var mapPriorityData = {<#if priorities?exists><#list priorities as item>
		"${item.enumId?if_exists}": "${StringUtil.wrapString(item.get("description")?if_exists)}",
	</#list></#if>};
	
	<#assign productStore = delegator.findList("ProductStore", null, null, null, null, true)>
	var mapPSData = {<#if productStore?exists><#list productStore as item>
		"${item.productStoreId?if_exists}": "${StringUtil.wrapString(item.get("storeName", locale)?if_exists)}",
	</#list></#if>};

	<#assign facilitys = delegator.findList("Facility", null, null, null, null, true)>
	var mapFacilityData = {<#if facilitys?exists><#list facilitys as item>
		"${item.facilityId?if_exists}": "${StringUtil.wrapString(item.get("facilityName", locale)?if_exists)}",
	</#list></#if>};
	var cellClass = function (row, columnfield, value) {
 		var data = $('#jqxgirdReportExport').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("PRIO_MEDIUM" == data.priority) {
 				return "background-cancel";
 			} else if ("PRIO_HIGH" == data.priority) {
 				return "background-important-nd";
 			} else if ("PRIO_LOW" == data.priority) {
 				return "background-prepare";
 			}
 			<#-- back favor ... -->
 		}
    }
</script>
<div style="position:relative" class="form-window-content-custom">
<#assign dataField="[
				{ name: 'orderId', type: 'string'},
				{ name: 'priority', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'productStoreId', type: 'string'},
				{ name: 'productName', type: 'string'},
				{ name: 'requireAmount', type: 'string'},
				{ name: 'weightUomId', type: 'string'},
				{ name: 'selectedAmount', type: 'string'},
				{ name: 'facilityId', type: 'string'},
				{ name: 'estimatedDeliveryDate', type: 'date', other: 'Timestamp'},
				{ name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
				{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
				{ name: 'quantity', type: 'number'},
				{ name: 'quantityUomId', type: 'string'},
				{ name: 'originFacilityId', type: 'string'},
				{ name: 'fullName', type: 'string'}
			]"/>
<#assign columnlist="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
				},
				{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', width: 150, pinned: true,
				},
				{ text: '${uiLabelMap.ProductName}', datafield: 'productName', minwidth: 200,
				},
				{ text: '${uiLabelMap.ExportQuantityExpected}', datafield: 'quantity', width: 120, cellsalign: 'right', filtertype: 'number',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						var data = $('#jqxgirdReportExport').jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') value = data.selectedAmount * data.quantity;
						if(value){
							return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
						}
					},
				},
				{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', width: 120, filtertype: 'checkedlist',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgirdReportExport').jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') value = data.weightUomId
						value = value?mapUomData[value]:value;
						return '<div style=margin:4px;>' + value + '</div>';
					},
					createfilterwidget: function (column, columnElement, widget) {
						widget.jqxDropDownList({ source: uomData, displayMember: 'description', valueMember: 'uomId' });
		   			}
				},
				{ text: '${uiLabelMap.DateExpectedWarehousing}', dataField: 'estimatedDeliveryDate', width: 100, cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						if(!value){
							var data = $('#jqxgirdReportExport').jqxGrid('getrowdata', row);
							if (data.shipAfterDate && data.shipBeforeDate) {
								return '<span>' + jOlbUtil.dateTime.formatDate(data.shipAfterDate) + '&nbsp;-&nbsp;' + jOlbUtil.dateTime.formatDate() + '</span>';
							}
						}
					}
				},
				{ text: '${uiLabelMap.OrderId}', datafield: 'orderId', width: 150 },
				{ text: '${uiLabelMap.Facility}', datafield: 'originFacilityId', width: 150,
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						if(value){
							return '<span>' + mapFacilityData[value] + '</span>';
						}
					}
				},
				{ text: '${uiLabelMap.BSSalesChannel}', datafield: 'productStoreId', align: 'center', width: 200,
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						if(value){
							return '<span>' + mapPSData[value] + '</span>';
						}
					}
				},
				{ text: '${uiLabelMap.Priority}', datafield: 'priority', width: 150,
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						if(value){
							return '<span>' + mapPriorityData[value] + '</span>';
						}
					}
				},
//				{ text: '${uiLabelMap.SalesExecutive}', datafield: 'fullName', width: 350,
//				},
			"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
	id="jqxgirdReportExport" addrefresh="true" filterable="true" groupable="true" columngrouplist=""
	url="jqxGeneralServicer?sname=JQGetReportExportWarehouseByOrder"
	customTitleProperties="WarehousingExportReportExpectedUnderSaleOrder" />
</div>