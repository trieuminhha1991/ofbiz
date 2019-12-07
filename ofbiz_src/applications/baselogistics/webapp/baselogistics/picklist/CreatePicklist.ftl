<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>
<script src="/logresources/js/picklist/CreatePicklist2.js?v=0.0.1"></script>
<script src="/logresources/js/picklist/LoadByPicklist.js?v=0.0.1"></script>
<script src="/logresources/js/picklist/LoadByOrderIds.js?v=0.0.1"></script>
<script>
	
	var cellclassname = function (row, column, value, data) {
    	if (column == 'quantityPicked') {
			return 'background-prepare';
    	}
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSPhone = '${StringUtil.wrapString(uiLabelMap.BSPhone)}';
	uiLabelMap.BSAddress = '${StringUtil.wrapString(uiLabelMap.BSAddress)}';
	uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
	uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';
	uiLabelMap.PleaseSelectTitle = '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}';
	uiLabelMap.CustomerId = '${StringUtil.wrapString(uiLabelMap.CustomerId)}';
	uiLabelMap.Description = '${StringUtil.wrapString(uiLabelMap.Description)}';
	uiLabelMap.AreYouSureCreate = '${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}';
	
</script>
<h4 class="smaller lighter blue" style="margin: 5px 0px 10px 10px !important;font-weight:500;line-height:20px;font-size:18px;">
	${StringUtil.wrapString(uiLabelMap.CreateNew)} ${StringUtil.wrapString(uiLabelMap.BLPickingList)?lower_case}
</h4>
<div id="stocking-form">
	<div class="form-window-content">
		<div class="row-fluid">
			<div class="span10">
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right asterisk">${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}</label></div>
					<div class="span8">
						<div id="productStoreId">
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span10">
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right asterisk">${StringUtil.wrapString(uiLabelMap.SelectFacilityToExport)}</label></div>
					<div class="span8">
						<div id="txtPickingFacility">
							<div id="jqxgridPickingFacility"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span10">
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right asterisk">${StringUtil.wrapString(uiLabelMap.BLDmsCusInfo)}</label></div>
					<div class="span8">
						<div id="customerId">
							<div id="customerGrid"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span10">
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right asterisk">${StringUtil.wrapString(uiLabelMap.SalesOrder)}</label></div>
					<div class="span8">
						<div id="txtPickingOrder">
							<div id="jqxgridPickingOrder"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span10">
				<div class="row-fluid margin-top10">
					<div class="span4">
					</div>
					<div class="span8">
						<button id="btnUpload" type="button" class="margin-top10 btn btn-primary form-action-button">
							${StringUtil.wrapString(uiLabelMap.CommonSelect)}
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<#assign dataField="[{ name: 'pickingItemId', type: 'string' },
					{ name: 'orderId', type: 'string' },
					{ name: 'orderItemSeqId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'quantityInPickFace', type: 'number' },
					{ name: 'quantityPickable', type: 'number' },
					{ name: 'otherPicklistBinId', type: 'string' },
					{ name: 'quantityPicked', type: 'number' }]"/>

<#assign columnlist = "
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', datafield: 'orderId', width: 120, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productCode', width: 120, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', minwidth: 130, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.BLQuantityNeedPickSum)}', datafield: 'quantity', filtertype: 'number', width: 100, editable: false,
						cellsrenderer: function (row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.QOH)}', datafield: 'quantityOnHandTotal', filtertype: 'number', width: 100, editable: false,
						cellsrenderer: function (row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BLQOHInPickFace)}', datafield: 'quantityInPickFace', filtertype: 'number', width: 100, editable: false,
						cellsrenderer: function (row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BLQuantityPermit)}', datafield: 'quantityPickable', filtertype: 'number', width: 100, editable: false,
						cellsrenderer: function (row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsSoLuongSoan)}', datafield: 'quantityPicked', columntype: 'numberinput', filtertype: 'number', width: 150, cellclassname: cellclassname,
						cellsrenderer: function (row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						},
						validation: function (cell, value) {
							if (value < 0) {
								return { result: false, message: '${StringUtil.wrapString(uiLabelMap.NotAllowNegative)}' };
							}
							if (value > $('#jqxgridPicking').jqxGrid('getcellvalue', cell.row, 'quantity')) {
								return { result: false, message: '${StringUtil.wrapString(uiLabelMap.BLKhongLonHonSoLuongTrenDon)}' };
							}
							if (value > ($('#jqxgridPicking').jqxGrid('getcellvalue', cell.row, 'quantityPickable') + 
									$('#jqxgridPicking').jqxGrid('getcellvalue', cell.row, 'quantityPicked'))) {
								return { result: false, message: '${StringUtil.wrapString(uiLabelMap.BLCannotGreaterThanPermitQty)}' };
							}
							return true;
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BLPickListHolding)}', datafield: 'otherPicklistBinId', width: 150, editable: false,
						cellsrenderer: function (row, column, value, a, b, data) {
						}
					},
					"/>

<#assign updateUrl = "jqxGeneralServicer?jqaction=U&sname=updatePickingItemTempData" />

<@jqGrid id="jqxgridPicking" url="" customTitleProperties="BLPickingList"
	dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
	showtoolbar="true" filtersimplemode="true" addrow="false" editable="true" deleterow="false"
	updateUrl=updateUrl editColumns="pickingItemId;quantityPicked(java.math.BigDecimal)"/>

<div class="row-fluid margin-top10">
	<div class="span12">
		<#if hasOlbPermission("MODULE", "LOG_PICKLIST", "UPDATE")>
		<button id="btnCommit" type="button" class="btn btn-primary form-action-button pull-right hidden">
			<i class="fa fa-check"></i>${uiLabelMap.POCommonOK}
		</button>
		<#if !parameters.picklistId?exists>
		<button id="btnDelete" type="button" class="btn btn-danger form-action-button pull-right hidden">
			<i class="fa fa-remove"></i>${uiLabelMap.DmsDeleteAll}
		</button>
		</#if>
		</#if>
	</div>
</div>

<div id="jqxNotification">
<div id="notificationContent"></div>
</div>

<script>
	multiLang = _.extend(multiLang, {
		FormatWrong: "${StringUtil.wrapString(uiLabelMap.FormatWrong)}",
		DmsUploaded: "${StringUtil.wrapString(uiLabelMap.DmsUploaded)}",
		EmployeeId: "${StringUtil.wrapString(uiLabelMap.EmployeeId)}",
		EmployeeName: "${StringUtil.wrapString(uiLabelMap.EmployeeName)}",
		BSOrderId: "${StringUtil.wrapString(uiLabelMap.BSOrderId)}",
		BSAbbOrderId: "${StringUtil.wrapString(uiLabelMap.BSAbbOrderId)} DTM",
		BSCustomer: "${StringUtil.wrapString(uiLabelMap.BSCustomer)}",
		BSCustomerId: "${StringUtil.wrapString(uiLabelMap.BSCustomerId)}",
		BSShippingAddress: "${StringUtil.wrapString(uiLabelMap.BSShippingAddress)}",
		BSCreateDate: "${StringUtil.wrapString(uiLabelMap.BSCreateDate)}",
		BSOrder: "${StringUtil.wrapString(uiLabelMap.BSOrder)}",
		BSProductName: "${StringUtil.wrapString(uiLabelMap.BSProductName)}",
		FacilityId: "${StringUtil.wrapString(uiLabelMap.FacilityId)}",
		FacilityName: "${StringUtil.wrapString(uiLabelMap.FacilityName)}",
		QOH: "${StringUtil.wrapString(uiLabelMap.QOH)}",
		Address: "${StringUtil.wrapString(uiLabelMap.Address)}",
		SequenceId: "${StringUtil.wrapString(uiLabelMap.SequenceId)}",
		ShipBeforeDate: "${StringUtil.wrapString(uiLabelMap.ShipBeforeDate)}",
		ShipAfterDate: "${StringUtil.wrapString(uiLabelMap.ShipAfterDate)}",
	});
	var mainGrid;
	const picklistId = "${(parameters.picklistId)?if_exists}";
	const orderIds = "${(parameters.orderIds)?if_exists}";
	$(document).ready(function () {
		CreatePicklist.init();
		if (picklistId) {
			$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.CommonUpdate)}");
			CreatePicklist.Update = true;
			LoadByPicklist.load(picklistId);
		}
		if (orderIds) {
			LoadByOrderIds.load("${(facility.facilityId)?if_exists}", "${(facility.facilityName)?if_exists}", orderIds.split(","));
		}
	});
</script>