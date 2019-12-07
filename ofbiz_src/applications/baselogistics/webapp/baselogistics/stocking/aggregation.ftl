<div id="aggregation-tab" class="tab-pane<#if activeTab?exists && activeTab == "aggregation-tab"> active</#if>">

<script type="text/javascript" src="/logresources/js/stocking/addVariance.js"></script>

<#include "aggregation_rowDetail.ftl"/>

<#assign dataField="[{ name: 'eventId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'primaryProductCategoryId', type: 'string' },
					{ name: 'lastInventoryCount', type: 'number' },
					{ name: 'quantityDifference', type: 'number' },
					{ name: 'quantity', type: 'number' },
					{ name: 'quantityRecheck', type: 'number' },
					{ name: 'unitPrice', type: 'number' },
					{ name: 'purCost', type: 'number' },
					{ name: 'priceDifference', type: 'number' }]"/>

<#assign columnlist = "
				{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (row + 1) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', pinned: true, datafield: 'productCode', width: 100, cellclassname: cellclassname },
				{ text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', pinned: true, datafield: 'productName', minwidth: 200, cellclassname: cellclassname },
				{ text: '${StringUtil.wrapString(uiLabelMap.BLCategoryProduct)}', datafield: 'primaryProductCategoryId', width: 100, cellclassname: cellclassname },
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsSoLuongKiem)}', datafield: 'quantity', filtertype: 'number', width: 120, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
						return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsSLKiemCheo)}', datafield: 'quantityRecheck', filtertype: 'number', width: 120, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
						return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.QOH)}', datafield: 'lastInventoryCount', filtertype: 'number', width: 120, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
						return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.Deviation)}', datafield: 'quantityDifference', filtertype: 'number', width: 110, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
						return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.UnitPrice)}', datafield: 'unitPrice', filtertype: 'number', width: 120, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
						if (!value) {
							value = 0;
						}
						return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.SettingTotalCostDiff)}', datafield: 'priceDifference', filtertype: 'number', width: 120, cellclassname: cellclassname, sortable: false, filterable: false,
					cellsrenderer: function (row, column, value, a, b, data) {
						if (data.quantityDifference && data.unitPrice) {
							value = data.quantityDifference * data.unitPrice;
						} else if (data.productId){
							value = 0;
						}
						return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					}
				}"/>

<@jqGrid id="jqxGridAggregation" url="jqxGeneralServicer?sname=JQGetListStockEventAggregated&eventId=${stockEvent.eventId}"
	dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" customTitleProperties="DmsAggregation"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="270" isSaveFormData="true" formData="filterObjData"
	customcontrol1="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()"
	showtoolbar="true" filtersimplemode="true" addrow="false" editable="false" deleterow="false"/>

<#if isThru && stockEvent.isClosed == "N">
<div class="row-fluid margin-top10">
	<div class="span12">
		<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "APPROVE")>
		<button id="btnSubmit" type="button" class="btn btn-primary form-action-button pull-right">
			<i class="fa fa-check"></i>${StringUtil.wrapString(uiLabelMap.BLFinishStocking)}
		</button>
		</#if>
	</div>
</div>
<script>
$(document).ready(function () {
	var btnClick = false;
	$("#btnSubmit").click(function () {
		var check = DataAccess.getData({
			url: "checkStockEventSubmitable",
			data: { eventId: "${stockEvent.eventId}" },
			source: "check"});
		if (check == "true") {
			jOlbUtil.confirm.dialog('${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}', function() {
				if (!btnClick){
					Loading.show('loadingMacro');
					setTimeout(function(){
						$.ajax({
							type: 'POST',
							url: 'submitQuantityAggregated',
							async: false,
							data: {
								eventId: "${stockEvent.eventId}"
							},
							success: function(data){
								if (data._ERROR_MESSAGE_ || data._ERROR_MESSAGE_LIST_) {
									if (data._ERROR_MESSAGE_) {
										jOlbUtil.alert.error(data._ERROR_MESSAGE_);
									}
									if (data._ERROR_MESSAGE_LIST_) {
										jOlbUtil.alert.error(data._ERROR_MESSAGE_LIST_);
									}
								} else {
									<#if hasOlbPermission("MODULE", "PARTY_DISTRIBUTOR", "VIEW")>
										<#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>
											location.href = "disViewInventoryStockingDis?eventId=${stockEvent.eventId}&activeTab=aggregation-tab";
										<#else>
											location.href = "viewInventoryStockingDis?eventId=${stockEvent.eventId}&activeTab=aggregation-tab";
										</#if>
									<#else>
										location.href = "InventoryStocking?eventId=${stockEvent.eventId}&activeTab=aggregation-tab";
									</#if>
								}
							},
						});
					Loading.hide('loadingMacro');
	        		}, 500);
					btnClick = true;
				}
	        }, '${StringUtil.wrapString(uiLabelMap.CommonCancel)}', '${StringUtil.wrapString(uiLabelMap.OK)}', function(){
        	btnClick = false;
	        });
		} else {
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.DmsCoSanPhamChuaBoSungLyDo)}");
		}
	});
});
</script>
</#if>
<script>
	var filterObjData = new Object();
	var exportExcel = function(){
		var winName="ExportExcel";
		var winURL = "exportListStockEventAggregatedExcel";
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");
		var params = filterObjData.data != "undefined"? filterObjData.data : {};
		params.eventId = "${stockEvent.eventId}";
		for(var key in params){
			if (params.hasOwnProperty(key)) {
				var input = document.createElement("input");
				input.type = "hidden";
				input.name = key;
				input.value = params[key];
				form.appendChild(input);
			}
		}
		document.body.appendChild(form);
		window.open(" ", winName);
		form.target = winName;
		form.submit();
		document.body.removeChild(form);
	}
	var mapVarianceReason = ${StringUtil.wrapString(mapVarianceReason!"{}")};
	var varianceReasons = ${StringUtil.wrapString(varianceReasons!'[]')};
	$(document).ready(function () {
		$("a[href='#aggregation-tab']").click(function () {
			$("#jqxGridAggregation").jqxGrid("updatebounddata");
		});
	});
	var cellclassname = function (row, column, value, data) {
		if (data.quantity !== data.quantityRecheck) {
			return "background-important-nd";
		}
		return "";
	};
	var editable_RDT = false;
	<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "APPROVE")>
		editable_RDT = true;
	</#if>
</script>
</div>