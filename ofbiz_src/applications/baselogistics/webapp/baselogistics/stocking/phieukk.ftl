<div id="phieukk-tab" class="tab-pane<#if activeTab?exists && activeTab == "phieukk-tab"> active</#if>">

<#assign addrowgridPhieukk="false"/>
<div class="row-fluid form-window-content">
	<div class="span3">
		<div class="row-fluid margin-top10">
			<div class="span12"><div id="txtLocation"></div></div>
		</div>
	</div>
	<#if !isThru>
	<#assign addrowgridPhieukk="true"/>
	<div class="span9">
		<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "UPDATE")>
		<button id="btnFinishStockEvent" type="button" style="margin-right: 2px !important;" class="btn btn-primary form-action-button pull-right">
			${uiLabelMap.DmsKetThucKyKiemKe}
		</button>
		</#if>
	</div>
	</#if>
</div>

<#if !hasOlbPermission("MODULE", "INVENTORY_STOCKING", "UPDATE")>
<#assign addrowgridPhieukk="false"/>
</#if>

<#assign dataField="[{ name: 'eventId', type: 'string' },
					{ name: 'eventItemSeqId', type: 'string' },
					{ name: 'idValue', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'requireAmount', type: 'string' },
					{ name: 'amountUomTypeId', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'quantityRecheck', type: 'number' },
					{ name: 'unitPrice', type: 'number' },
					{ name: 'totalPrice', type: 'number' },
					{ name: 'location', type: 'string' },
					
					{ name: 'expireDate', type: 'date', other: 'Timestamp'},
					{ name: 'expireDateRecheck', type: 'date', other: 'Timestamp'},
					{ name: 'manufactureDate', type: 'date', other: 'Timestamp'},
					{ name: 'manufactureDateRecheck', type: 'date', other: 'Timestamp'},
					{ name: 'lot', type: 'string' },
					{ name: 'lotRecheck', type: 'string' },
					
					{ name: 'editable', type: 'string' }]"/>

<#assign columnlist = "
				{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, pinned: true, editable: false, groupable: false, draggable: false, resizable: false, width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (row + 1) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', pinned: true, datafield: 'productCode', editable: false, width: 120, cellclassname: cellclassname },
				{ text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', pinned: true, datafield: 'productName', editable: false, minwidth: 200, cellclassname: cellclassname },
				{ text: '${StringUtil.wrapString(uiLabelMap.UPC)}', pinned: true, datafield: 'idValue', editable: false, width: 180, cellclassname: cellclassname },
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsSoLuongKiem)}', datafield: 'quantity', filtertype: 'number', editable: false, width: 120, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
						return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsSoLuongKiemCheo)}', datafield: 'quantityRecheck', columntype: 'numberinput', filtertype: 'number', width: 150, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
						return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					},
					createeditor: function (row, cellvalue, editor) {
						var data = $('#jqxGridPhieukk').jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y' && data.amountUomTypeId && data.amountUomTypeId == 'WEIGHT_MEASURE') {
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });							
						} else {
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
						}
					},
					validation: function (cell, value) {
						if (value < 0) {
							return { result: false, message: '${StringUtil.wrapString(uiLabelMap.NotAllowNegative)}' };
						}
						return true;
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ManufacturedDateSum)}', datafield: 'manufactureDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: false, width: 120, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ManufacturedDateSum)} ${StringUtil.wrapString(uiLabelMap.BLRecheck?lower_case)}', columntype: 'datetimeinput', datafield: 'manufactureDateRecheck', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: true, width: 120, cellclassname: cellclassname,
					createeditor: function (row, column, editor) {
						editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
						var data = $('#jqxGridPhieukk').jqxGrid('getrowdata', row);
			            editor.jqxDateTimeInput({disabled: false});
				 	},
				 	validation: function (cell, value) {
				 		if (value) {
				        	var n = new Date();
				        	if (n < new Date(value)){
				        		console.log(12121212);
					        	return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeNow}'};
					        }
				        } 
				        return true;
					 },
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)}', datafield: 'expireDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: false, width: 120, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)} ${StringUtil.wrapString(uiLabelMap.BLRecheck?lower_case)}', columntype: 'datetimeinput', datafield: 'expireDateRecheck', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: true, width: 120, cellclassname: cellclassname,
					createeditor: function (row, column, editor) {
						editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
						var data = $('#jqxGridPhieukk').jqxGrid('getrowdata', row);
			            editor.jqxDateTimeInput({disabled: false});
				 	},
				 	validation: function (cell, value) {
				 		if (value) {
					        var data = $('#jqxGridPhieukk').jqxGrid('getrowdata', cell.row);
					        if (data.manufactureDateRecheck){
					        	var mnf = new Date(data.manufactureDateRecheck);
					        	if (mnf > new Date(value)){
						        	return { result: false, message: '${uiLabelMap.ExpireDateMustBeBeforeManufactureDate}'};
						        }
					        }
				        } 
				        return true;
					 },
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.Batch)}', datafield: 'lot', editable: false, width: 120, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.Batch)} ${StringUtil.wrapString(uiLabelMap.BLRecheck?lower_case)}', columntype: 'input', datafield: 'lotRecheck', editable: true, width: 120, cellclassname: cellclassname,
				},
				"/>
<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "APPROVE")>
<#assign columnlist = columnlist + "
				{ text: '${StringUtil.wrapString(uiLabelMap.UnitPrice)}', datafield: 'unitPrice', filtertype: 'number', width: 120, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
						if (!value && data.productId) {
							value = 0;
						}
						return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSGrandTotal)}', datafield: 'totalPrice', filtertype: 'number', width: 120, cellclassname: cellclassname,
					cellsrenderer: function (row, column, value, a, b, data) {
						if (!value && data.productId) {
							value = 0;
						}
						return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					}
				},"/>
</#if>
<#assign columnlist = columnlist + "
				{ text: '${StringUtil.wrapString(uiLabelMap.Location)}', datafield: 'location', width: 120, cellclassname: cellclassname }"/>

<#assign customcontrol1 = ""/>
<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "VIEW")>
	<#assign customcontrol1 = "fa fa-file-pdf-o@${uiLabelMap.DmsXuatPhieuKiemKe}@javascript: void(0);@Stocking.exportPDF()"/>
</#if>
	
<#assign url = ""/>
<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "APPROVE")>
<#assign url = "jqxGeneralServicer?sname=JQGetListStockEventItem&eventId=${stockEvent.eventId}"/>
</#if>

<@jqGrid id="jqxGridPhieukk" url=url
	dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" customTitleProperties="DmsDanhSachKiemKe"
	showtoolbar="true" filtersimplemode="true" addrow=addrowgridPhieukk editable="false" deleterow="false" contextMenuId="contextMenu" mouseRightMenu="true"
	alternativeAddPopup="addStockEventItem" addType="popup"
	createUrl="jqxGeneralServicer?sname=createStockEventItem&jqaction=C" addColumns="eventId;idValue;quantity(java.math.BigDecimal);location;editable;productId"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateStockEventItem" editColumns="eventId;eventItemSeqId;idValue;quantityRecheck(java.math.BigDecimal);expireDateRecheck(java.sql.Timestamp);manufactureDateRecheck(java.sql.Timestamp);lotRecheck;productId"
	customcontrol1 = customcontrol1/>

<#include "popup/addStockEventItem.ftl"/>
<div id="contextMenu" style="display:none;">
	<ul>
		<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "UPDATE")>
		<li id="mnuDelete"><i class="fa fa-trash-o"></i>&nbsp;${uiLabelMap.CommonDelete}</li>
		</#if>
		<li id="mnuRefresh"><i class="fa fa-refresh"></i>&nbsp;${uiLabelMap.BSRefresh}</li>
	</ul>
</div>


<#if !isThru && stockEvent.isClosed == "N">
<div class="row-fluid margin-top10">
	<div class="span12">
		<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "UPDATE")>
		<button id="btnUpdateQuantityRecheck" type="button" class="btn btn-primary form-action-button pull-right hidden">
			<i class="fa fa-check"></i>${uiLabelMap.DmsCapNhatSoLuongKiemCheo}
		</button>
		</#if>
	</div>
</div>
</#if>
<script>
	$(document).ready(function () {
		Stocking.init();
	});
	var cellclassname = function (row, column, value, data) {
		if (data.quantityRecheck && data.quantity !== data.quantityRecheck) {
			return "background-important-nd";
		} else if (data.editable == "Y") {
			return "background-prepare";
		}
		return "";
	};
	var Stocking = (function () {
		var mainGrid, contextmenu;
		var initElements = function () {
			$("#txtLocation").jqxComboBox({ source: [], displayMember: "text", valueMember: "value", width: 218, height: 27, theme: theme });
			if ("${activeTab?if_exists}" == "phieukk-tab") {
				reloadLocation();
			}

			contextmenu = $("#contextMenu").jqxMenu({ theme: theme, width: 180, autoOpenPopup: false, mode: "popup" });
		};
		var handleEvents = function () {
			mainGrid.on("bindingcomplete", function (event) {
				var item = $("#txtLocation").jqxComboBox("getSelectedItem");
				if (item && item.originalItem.statusId == "STOCKING_GUARANTEED") {
					$("#customcontroljqxGridPhieukk2").show();
				} else {
					$("#customcontroljqxGridPhieukk2").hide();
				}
			});
			$("#txtLocation").on("change", function (event) {
				var args = event.args;
				if (args) {
					var item = args.item;
					if (item) {
						var value = item.value;
						var adapter = mainGrid.jqxGrid("source");
						if (adapter) {
							adapter.url = "jqxGeneralServicer?sname=JQGetListStockEventItem&eventId=${stockEvent.eventId}&location=" + value;
							adapter._source.url = adapter.url;
							mainGrid.jqxGrid("source", adapter);
						}
						if (item.originalItem.statusId == "STOCKING_CREATED") {
							$("#btnUpdateQuantityRecheck").removeClass("hidden");
						} else {
							if (!$("#btnUpdateQuantityRecheck").hasClass("hidden")) {
								$("#btnUpdateQuantityRecheck").addClass("hidden");
							}
						}
						<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "UPDATE")>
						<#if !isThru>
						if (item.originalItem.statusId == "STOCKING_GUARANTEED") {
							mainGrid.jqxGrid({ editable: true });
							mainGrid.jqxGrid({ selectionmode: 'singlecell' });
							mainGrid.jqxGrid({ editmode: 'selectedcell' });
						} else {
							mainGrid.jqxGrid({ editable: false });
						}
						</#if>
						</#if>
					}
				}
			});
			$("#btnUpdateQuantityRecheck").click(function () {
				DataAccess.executeAsync({ url: "updateQuantityRecheck", data: { eventId: "${stockEvent.eventId}", location: $("#txtLocation").jqxComboBox("val") } }, function (res) {
					mainGrid.jqxGrid("updatebounddata");
					reloadLocation($("#txtLocation").jqxComboBox("getSelectedIndex"));
				});
			});
			$("#btnFinishStockEvent").click(function () {
				
				var check = DataAccess.getData({
					url: "checkStockEventFinishable",
					data: { eventId: "${stockEvent.eventId}" },
					source: "check"});
				
				if (check == "true") {
					bootbox.confirm("${StringUtil.wrapString(uiLabelMap.DmsBanCoChacChanKetThucKyKiemKe)}", "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}", function (result) {
						if (result) {
							DataAccess.executeAsync({ url: "finishStockEvent", data: { eventId: "${stockEvent.eventId}" } }, function (res) {
								location.href = "InventoryStocking?eventId=${stockEvent.eventId}&activeTab=phieukk-tab";
							});
						}
					});
				} else {
					bootbox.alert("${StringUtil.wrapString(uiLabelMap.DmsCoViTriChuaKiemCheo)}");
				}
			});
			$("a[href='#phieukk-tab']").click(function () {
				mainGrid.jqxGrid("updatebounddata");
				reloadLocation($("#txtLocation").jqxComboBox("getSelectedIndex"));
			});
			
			contextmenu.on("itemclick", function (event) {
				var args = event.args;
				var itemId = $(args).attr("id");
				switch (itemId) {
				case "mnuDelete":
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var eventId = mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "eventId");
					var eventItemSeqId = mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "eventItemSeqId");
					if (eventId && eventItemSeqId) {
						bootbox.confirm("${StringUtil.wrapString(uiLabelMap.ConfirmDelete)}", "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}", function (result) {
							if (result) {
								DataAccess.executeAsync({ url: "deleteStockEventItem", data: { eventId: eventId, eventItemSeqId: eventItemSeqId } }, function (res) {
									mainGrid.jqxGrid("updatebounddata");
								});
							}
						});
					}
					break;
				case "mnuRefresh":
					mainGrid.jqxGrid("clearselection");
					mainGrid.jqxGrid("updatebounddata");
				default:
					break;
				}
			});
			contextmenu.on("shown", function () {
				<#if isThru>
					contextmenu.jqxMenu("disable", "mnuDelete", true);
					<#else>
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					if (mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "editable") == "N") {
						contextmenu.jqxMenu("disable", "mnuDelete", true);
					} else {
						contextmenu.jqxMenu("disable", "mnuDelete", false);
					}
				</#if>
			});
		};
		var reloadLocation = function (selectIndex) {
			var source =
			{
				datatype: "json",
				datafields: [
						{ name: "text" },
						{ name: "value" },
						{ name: "statusId" }
				],
				url: "loadLocationInEvent?eventId=${stockEvent.eventId}",
				async: false
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#txtLocation").jqxComboBox({ source: dataAdapter });
			if (typeof (selectIndex) == "number") {
				$("#txtLocation").jqxComboBox("selectIndex", selectIndex);
			}
		};
		var exportPDF = function () {
			if ($("#txtLocation").jqxComboBox("val")) {
				window.open("InventoryStocking.pdf?eventId=${stockEvent.eventId}&location=" + $("#txtLocation").jqxComboBox("val"), "_blank");
			}
		};
		return {
			init: function () {
				mainGrid = $("#jqxGridPhieukk");
				initElements();
				handleEvents();
			},
			exportPDF: exportPDF
		};
	})();
</script>
</div>