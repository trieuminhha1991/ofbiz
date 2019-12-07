<style>
	.expired {
		background-color: #eeeeee !important;
		cursor: not-allowed;
	}
	.expired.jqx-widget-olbius .expired.jqx-grid-cell-selected-olbius, .jqx-widget-olbius .expired.jqx-grid-cell-hover-olbius {
		background-color: #eeeeee !important;
		cursor: not-allowed;
	}
</style>

<#if hasOlbPermission("MODULE", "SUP_PRODUCT_EDIT", "")>
	<#assign editable = "true" />
<#else>
	<#assign editable = "false" />
</#if>

<script type="text/javascript">
	var supplierIdsNotUpdate = ["khovt"];
	var cellclassnameListSupProd = function (row, column, value, data) {
		/*if (data.availableThruDate) {
			var availableThruDate = data.availableThruDate.getTime();
			var nowDate = new Date().getTime();
			if (availableThruDate < nowDate) {
				return "expired";
			}
		}*/
        <#if editable == "true">
        if (supplierIdsNotUpdate.indexOf(data.partyCode) < 0) {
        	if (column == "lastPrice" || column == "supplierProductId" || column == "availableThruDate") {
        		return "background-prepare";
        	} else {
        		if (data.availableFromDate) {
					var availableFromDate = data.availableFromDate.getTime();
					var nowDate = new Date().getTime();
					if (availableFromDate > nowDate) {
						return "background-waiting";
					}
				}
        	}
        }
        <#else>
        if (data.availableFromDate) {
			var availableFromDate = data.availableFromDate.getTime();
			var nowDate = new Date().getTime();
			if (availableFromDate > nowDate) {
				return "background-waiting";
			}
		}
        </#if>
		return "";
	};
	var cellbegineditListSupProd = function (row, datafield, columntype, value) {
		var data = $('#jqxgridSupplierProduct').jqxGrid('getrowdata', row);
        if (supplierIdsNotUpdate.indexOf(data.partyCode) > -1) return false;
    }
</script>

<div id="contentNotificationSuccess">
</div>
<div id="contentNotificationSupplierProductExits2">
</div>

<#assign dataField="[
				{name: 'availableFromDate', type: 'date', other: 'Timestamp'},
				{name: 'availableThruDate', type: 'date', other: 'Timestamp'},
				{name: 'productId', type: 'string'},
				{name: 'partyId', type: 'string'},
				{name: 'minimumOrderQuantity', type: 'number'},
				{name: 'currencyUomId', type: 'string'},
				{name: 'lastPrice', type: 'number'},
				{name: 'quantityUomId', type: 'string'},
				{name: 'supplierProductId', type: 'string'},
				{name: 'productName', type: 'string'},
				{name: 'canDropShip', type: 'string'},
				{name: 'productCode', type: 'string'},
				{name: 'groupName', type: 'string'},
				{name: 'partyCode', type: 'string'}]"/>

<#assign columnlist="
				{text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
					cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd, groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=\"margin:4px;\">' + (value + 1) + '</div>';
					}
				},
				{text: '${uiLabelMap.POProductId}', dataField: 'productId', width: '120', editable: false, filterable: true, cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd, hidden: true},
				{text: '${uiLabelMap.POProductId}', dataField: 'productCode', width: 120, pinned: true, editable: false, filterable: true, cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd},
				{text: '${uiLabelMap.BPOProductName}', datafield: 'productName',editable: false, width: '230', cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd},
				{text: '${uiLabelMap.BPOSupplierId}',align: 'left', datafield: 'partyCode', width: 80, editable: false, cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd},
				{text: '${uiLabelMap.POSupplier}',align: 'left', datafield: 'groupName', width: 150, editable: false, cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd},
				{text: '${uiLabelMap.MOQ}', dataField: 'minimumOrderQuantity', width: '60', editable: false, filterable: true, cellsalign: 'right', columntype: 'numberinput', filtertype: 'number', cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd},
				{text: '${uiLabelMap.BSUomId}', datafield: 'quantityUomId', width: 100, editable: false, cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd},
				{text: '${uiLabelMap.POLastPrice}', dataField: 'lastPrice', align: 'center', editable: true, filterable: true, cellsalign: 'right', 
					columntype: 'numberinput', width: 100, filtertype: 'number', cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd,
					cellsrenderer: function(row, column, value){
						if (value){
							return '<span style=\"text-align: right\">' + value.toLocaleString(locale) +'</span>';
						}
					}, createeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 2 });
					}, cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						if (newvalue < 0){
							return false;
						}
					}
				},
				{text: '${uiLabelMap.BPOCurrencyUomId}', dataField: 'currencyUomId', editable: false, filterable: true, width: 90, cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd},
				{text: '${uiLabelMap.BSSupplierProductId}', dataField: 'supplierProductId', editable: true, filterable: true, width: 140, cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd},
				{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'availableFromDate', columntype: 'datetimeinput',width: 140, editable: false, cellsformat: 'dd/MM/yyyy', filtertype:'range', cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd,
					cellsrenderer: function(row, column, value){
						if (value){
							return '<span style=\"text-align: right\">' + jOlbUtil.dateTime.formatFullDate(value) +'</span>';
						}
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'availableThruDate', columntype: 'datetimeinput',width: 140, cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype:'range', cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd,
					cellsrenderer: function(row, column, value){
						if (value){
							return '<span style=\"text-align: right\">' + jOlbUtil.dateTime.formatFullDate(value) +'</span>';
						}
					}
				},
				{text: '${uiLabelMap.FormFieldTitle_canDropShip}', dataField: 'canDropShip', width: '120', editable: false, filterable: true, filtertype : 'checkedlist',columntype: 'dropdownlist', cellclassname: cellclassnameListSupProd, cellbeginedit: cellbegineditListSupProd,
					columntype: 'dropdownlist',
					cellsrenderer : function(row, column, value){
						if(value == null)
							return '<div style=\"margin-top: 6px;  text-align:left\">' + '' + '</div>';
						if(value == 'Y')
							return '<div style=\"margin-top: 6px;  text-align:left\">' + canDropShipData[0].description + '</div>';
						if(value == 'N')
							return '<div style=\"margin-top: 6px;  text-align:left\">' + canDropShipData[1].description + '</div>';
						if(value != 'Y' && value != 'N')
							return '<div style=\"margin-top: 6px;  text-align:left\">' + '' + '</div>';
					}, createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxDropDownList({autoDropDownHeight: true, source: canDropShipData ,valueMember: 'id', displayMember: 'description' });
					}, createfilterwidget: function (column, htmlElement, editor) {
						editor.jqxDropDownList({ autoDropDownHeight: true, source: canDropShipData, displayMember: 'description', valueMember: 'id' ,
							renderer: function (index, label, value) {
								if (index == 0) {
									return value;
								}
								for(var i = 0; i < canDropShipData.length; i++){
									if(value == canDropShipData[i].id){
										return canDropShipData[i].description; 
									}
								}
							} });
					}
				}"/>
		
<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable=editable showtoolbar="false"
	id="jqxgridSupplierProduct" addrefresh="true" filterable="true"  selectionmode="multiplecellsadvanced"
	url="jqxGeneralServicer?sname=jqGetListSupplierProductConfig" 
	editColumns="productId;partyId;currencyUomId;minimumOrderQuantity;availableFromDate(java.sql.Timestamp);availableThruDate(java.sql.Timestamp);lastPrice;supplierProductId;canDropShip" 
	updateUrl="jqxGeneralServicer?sname=updateJqxSupplierProductConfig&jqaction=U"
	editmode="click" defaultSortColumn="productId" mouseRightMenu="false" contextMenuId="menu" enabletooltips="true"/>	

<div id="menu" style="display:none;">
	<ul>
		<li><i class="fa-trash"></i>&nbsp;&nbsp;${uiLabelMap.PODeleteRowGird}</li>
	</ul>
</div>

<div id="containerSuccess" class="container-noti"></div>
<div id="jqxNotificationSuccess" style="margin-bottom:5px">
    <div id="notificationContentSuccess">
    </div>
</div>

<div id="containerSupplierProductExits" class="container-noti"></div>
<div id="jqxNotificationSupplierProductExits" style="margin-bottom:5px">
    <div id="notificationContentSupplierProductExits">
    </div>
</div>

<script type="text/javascript">
	let mainGrid = $("#jqxgridSupplierProduct");
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
	uiLabelMap.BSValidateSequence = "${StringUtil.wrapString(uiLabelMap.BSValidateSequence)}";
	uiLabelMap.BSQuantityMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.BSQuantityMustBeGreaterThanZero)}";
</script>