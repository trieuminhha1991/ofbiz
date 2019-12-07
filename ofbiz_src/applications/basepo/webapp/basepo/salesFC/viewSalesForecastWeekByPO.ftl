<style>
	.cell-green-color {
		color: black !important;
		background-color: #FFCCFF !important;
	}
	.cell-gray-color {
		color: black !important;
		background-color: #87CEEB !important;
	}
	
	.green1 {
		color: #black;
		background-color: #F0FFFF;
	}
	.green1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
		color: black;
		background-color: #F0FFFF;
	}
</style>

<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>

<#assign salesForecastId = parameters.salesForecastId !>

<div id="jqxNotificationCheckExists"> 
	<div id="notificationCheckExists">
	</div>
</div>
<div id="jqxNotification"> 
</div>

<div style="position:relative">
	<div id="loader_page_common" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 99998;" class="jqx-rc-all jqx-rc-all-olbius">
		<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div style="float: left;">
				<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
				<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
			</div>
		</div>
	</div>
</div>

<div style="position:relative">
	<div id="loader_page_common_save" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 99998;" class="jqx-rc-all jqx-rc-all-olbius">
		<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div style="float: left;">
				<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
				<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DAsaving}...</span>
			</div>
		</div>
	</div>
</div>

<div id="Menu"><ul></ul></div>

<div id="wdFilter" style="display: none;">
	<div>${uiLabelMap.DmsChoiseFilter}</div>
	<div>
		<div class="row-fluid" style="margin-bottom: 10px;">
			<div class="span6 div-inline-block">
				<div class="span8"><span style="float: right;">${uiLabelMap.optionFilter}:</span></div>
			</div>
			<div class="span6 div-inline-block">
				<div class="span6" id="monthMode">${uiLabelMap.Month}
				</div>
				<div class="span6" id="weekMode">${uiLabelMap.Week}
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6 div-inline-block">
				<div class="span8"><span style="float: right; margin-top: 5px;">${uiLabelMap.PrevWM}:</span></div>
				<div class="span4">
					<div id="fromMonth" class="green-label"></div>
				</div>
			</div>
			<div class="span6 div-inline-block">
				<div class="span8"><span style="float: right; margin-top: 5px;">${uiLabelMap.NextWM}:</span></div>
				<div class="span4">
					<div id="toMonth" class="green-label"></div>
				</div>
			</div>
		</div>
		<hr style="margin: 5px 0 !important;"/>
		<div class="row-fluid">
			<div class="">
				<button id="filterCancel" class="btn btn-mini form-action-button btn-danger pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="filterApply" class="btn btn-mini form-action-button btn-primary pull-right"><i class="icon-ok"></i>${uiLabelMap.DmsFilter}</button>
			</div>
		</div>
	</div>
</div>

<div id="contentNotificationCheckExists">
</div>
<div id="container">
</div>

<div id="jqxSFCByWeekItem"></div>

<script type="text/javascript">
	var uiLabelMap = {};
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	
	$("#jqxNotificationCheckExists").jqxNotification({
		width: "100%", appendContainer: "#contentNotificationCheckExists",
		opacity: 0.9, autoClose: false, template: "warning"
	});
	
	$("#jqxNotification").jqxNotification({
		width: "100%", appendContainer: "#container",
		opacity: 0.9, autoClose: false, template: "error"
	});
	
	$("#monthMode").jqxRadioButton({height: 25, checked: true});
	$("#weekMode").jqxRadioButton({height: 25});
	
	$("#monthMode").on("change", function (event) {
		$("#toMonth").jqxNumberInput({ max: 12 });
		$("#fromMonth").jqxNumberInput({ max: 12 });
	});
	$("#weekMode").on("change", function (event) {
		$("#toMonth").jqxNumberInput({ max: 52 });
		$("#fromMonth").jqxNumberInput({ max: 52 });
	});
	
	$("#wdFilter").jqxWindow({
		height:165, resizable: false, isModal: true, position:"center",
		width: 450, autoOpen: false, theme: theme
	});
	
	$("#fromMonth").jqxNumberInput({
		inputMode: "simple", spinMode: "simple", groupSeparator: ".", min:1,
		max:12, width:"90%", spinButtons: true, decimalDigits: 0, decimal: 1
	});
	$("#toMonth").jqxNumberInput({
		inputMode: "simple", spinMode: "simple", groupSeparator: ".", min:2,
		max:12, width:"90%", spinButtons: true, decimalDigits: 0, decimal: 2
	});
	
	var contextMenu = $("#Menu").jqxMenu({ width: 200, theme: theme, autoOpenPopup: false, mode: "popup"});
	$("#jqxSFCByWeekItem").on("contextmenu", function () {
		return false;
	});
		
	var listWeekHeader = [];
	var flagSFC = true;
	
	function initGridSFCWeek(){
		$("#jqxSFCByWeekItem").jqxGrid({
			source: {
				datatype: "array"
			},
			localization: getLocalization(),
			filterable: true,
			showfilterrow: true,
			theme: theme,
			rowsheight: 30,
			width: "100%",
			autoheight: true,
			columnsresize: true,
			enabletooltips: true,
			height: 450,
			autoheight: false,
			selectionmode: "singlecell",
			editmode: "click",
			pageable: true,
			sortable: true,
			pagesize: 10,
			editable: false,
			columns: [
				{ text: "${uiLabelMap.productId}", datafield: "productCode", pinned: true, width: 150 },
				{ text: "${uiLabelMap.BPOProductName}", datafield: "productName" }
			],
			showtoolbar: true,
			rendertoolbar: function(toolbar) {
				if (flagSFC) {
					var container = $("<div style=\"margin: 12px 4px 0px 0px;\" class=\"pull-right\"></div>");
					var btnSave = $("<a id=\"saveSFC\" style=\"cursor: pointer; margin-right: 15px; font-size: 15px;\"><i class=\"icon-save\"></i>${uiLabelMap.CommonSave}</a>");
					var btnFilter = $("<a id=\"filterPlan\" style=\"cursor: pointer; margin-right: 15px; font-size: 15px;\" class=\"icon-filter open-sans\">${uiLabelMap.DmsFilter}</a>");
					var btnCancelFilter = $("<a id=\"cancelFilterPlan\" style=\"cursor: pointer; font-size: 15px;\"><i class=\"icon-filter open-sans\"><span style=\"color: red; right: 3px; position: relative;\">x</span></i>${uiLabelMap.DmsCancelFilter}</a>");
					var titleProperty = $("<h4 style=\"color: #4383b4;\">${StringUtil.wrapString(uiLabelMap.DmsSalesFCWeek)}</h4>");
					toolbar.append(container);
					toolbar.append(titleProperty);
					<#if hasOlbPermission("MODULE", "PLANPO_SALESFC_EDIT", "UPDATE")>
						container.append(btnSave);
						container.append(btnFilter);
						container.append(btnCancelFilter);
					</#if>
					btnSave.click(function() {
						saveSFCWeek();
					});
					btnFilter.click(function() {
						$("#wdFilter").jqxWindow("open");
					});
					btnCancelFilter.click(function() {
						getDataForGridSFCWeek("${salesForecastId}", null);
					});
				}
				flagSFC = false;
			}
		});
	}
	
	function getDataForGridSFCWeek(salesForecastId, isFilter) {
		var data;
		if (isFilter) {
			var checkedMonth = $("#monthMode").jqxRadioButton("checked");
			var checkedWeek = $("#weekMode").jqxRadioButton("checked");
			var fromMonth = $("#fromMonth").val();
			var toMonth = $("#toMonth").val();
			data = {fromMonth: fromMonth, 
					toMonth: toMonth, 
					salesForecastId: salesForecastId, 
					checkedMonth: checkedMonth, 
					checkedWeek: checkedWeek };
		} else {
			data = {salesForecastId: salesForecastId};
		}
		$.ajax({
			beforeSend: function(){
				$("#loader_page_common").show();
			},
			complete: function(){
				$("#loader_page_common").hide();
			},
			url: "JqxGetSalesForecastByWeekForPO",
			type: "POST",
			data: data,
			dataType: "json",
			success: function(data) {
				var check = data.value;
				if (check == "success") {
					var listIterator = data.listIterator;
					listWeekHeader = data.listWeekHeader;
					updateDataForGridSFCWeek(listWeekHeader, listIterator);
				} else {
					$("#notificationCheckExists").text("${StringUtil.wrapString(uiLabelMap.POCheckPeriodExits)}");
					$("#jqxNotificationCheckExists").jqxNotification("open");
					var listIterator = data.listIterator;
					listWeekHeader = data.listWeekHeader;
					updateDataForGridSFCWeek(listWeekHeader, listIterator);
				}
			}
		});
	}
	
	function updateDataForGridSFCWeek(listWeekHeader, listIterator) {
		var obj = initDataFields(listWeekHeader);
		var tmpSource = $("#jqxSFCByWeekItem").jqxGrid("source");
		if (tmpSource) {
			tmpSource._source.datafields = obj.listHeader;
			tmpSource._source.localdata = listIterator;
		}
		$("#jqxSFCByWeekItem").jqxGrid({columns: obj.listColumns});
		$("#jqxSFCByWeekItem").jqxGrid("source", tmpSource);
		$("#jqxSFCByWeekItem").jqxGrid("updatebounddata");
	}
	function initDataFields(listWeekHeader) {
		var arrHeaders = [];
		var arrColumns = [];
		var h1 = {name: "productId", type: "string"};
		var h3 = {name: "productCode", type: "string"};
		var h2 = {name: "productName", type: "string"};
		var c1 = {text: "${uiLabelMap.productId}", datafield: "productId", editable: false, filterable: true, pinned: true, hidden: true};
		var c3 = {text: "${uiLabelMap.productId}", datafield: "productCode", editable: false, filterable: true, pinned: true, width: 150};
		var c2 = {text: "${uiLabelMap.BPOProductName}", datafield: "productName", editable: false,  width: 530,filterable: true, pinned: true};
		arrHeaders.push(h1);
		arrHeaders.push(h2);
		arrHeaders.push(h3);
		arrColumns.push(c1);
		arrColumns.push(c3);
		arrColumns.push(c2);
		for(var i=0; i<listWeekHeader.length; i++){
			var aa = listWeekHeader[i].periodName;
			var header = {name: listWeekHeader[i].customTimePeriodId, type: "number"};
			var column = {text: listWeekHeader[i].periodName, datafield: listWeekHeader[i].customTimePeriodId, editable: true, filterable: false, columntype: "numberinput", width: 90, cellsalign: "right",
				createeditor: function (row, cellvalue, editor) {
					editor.jqxNumberInput({inputMode: "simple", spinMode: "simple", groupSeparator: ".", min:0 });
				}, cellsrenderer: function(row, column, value, defaulthtml, columnproperties){
					if (value){
						var id = row +""+column;
						return '<span id=\"'+id+'\" style="text-align: right">' + value.toLocaleString(locale) +"</span>";
					}
				}, cellbeginedit: function (row, datafield, columntype) {
					var dateColumn = "";
					for(var i=0; i<listWeekHeader.length; i++){
						if(listWeekHeader[i].customTimePeriodId == datafield){
							dateColumn = listWeekHeader[i].periodName;
							break;
						}
					}
					var arr = dateColumn.split("-");
					var date = new Date(arr[2]+"-"+arr[1]+"-"+arr[0]);
					var today = new Date();
					var d = today.getDate();
					var m = today.getMonth() +1;
					var y = today.getFullYear();
					var t = today.getDay();
					if(t==0) d = d-6;
					else d = d-(t-1);
					today = new Date(y+"-"+m+"-"+d);
					if(date.getTime() < today.getTime()){
						$("#"+row +""+datafield).jqxTooltip({ disabled: false });
						$("#"+row +""+datafield).jqxTooltip({ content: "<span style=\"color: red;\">${uiLabelMap.DmsRestrictEditWeek}!</span>", theme: "customtooltip", position: "top", name: "movieTooltip"});
						$("#"+row +""+datafield).jqxTooltip("open");
						$("#"+row +""+datafield).bind("close", function () {
							$("#"+row +""+datafield).jqxTooltip({ disabled: true });
						});
						return false;
					}
				}
			};
			arrHeaders.push(header);
			arrColumns.push(column);
		}
		var arrRe = {listHeader: arrHeaders, listColumns: arrColumns};
		return arrRe;
	}
	
	$("#filterApply").on("click", function() {
		$("#wdFilter").jqxWindow("close");
		getDataForGridSFCWeek("${salesForecastId}", "Y");
	});
	
	$("#filterCancel").on("click", function() {
		$("#wdFilter").jqxWindow("close");
	});
	
	function saveSFCWeek() {
		var sfcItems = $("#jqxSFCByWeekItem").jqxGrid("getboundrows");
		for (var i = 0; i<sfcItems.length; i++) {
			delete sfcItems[i].productCode;
		}
		bootbox.dialog("" + "${uiLabelMap.BPOAreYouSureYouWantSave}" + "?",
			[{"label": "${uiLabelMap.wgcancel}",
				"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
				"callback": function() {bootbox.hideAll();}
			},
			{"label": "${uiLabelMap.wgok}",
				"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
				"callback": function() {
					$.ajax({
						beforeSend: function(){
							$("#loader_page_common_save").show();
						},
						complete: function(){
							$("#loader_page_common_save").hide();
						},
						url: "storeSaleForecastForPO",
						type: "POST",
						data: {salesForecastId: "${salesForecastId?if_exists}",
							organizationPartyId: "${organizationPartyId?if_exists}",
							internalPartyId: "${organizationPartyId?if_exists}", 
							currencyUomId: "${currencyUomId?if_exists}",
							sfcItems: JSON.stringify(sfcItems)
						},
						dataType: "json",
						success: function(data) {
							if (data.thisRequestUri == "json") {
								var errorMessage = "";
								if (data._ERROR_MESSAGE_LIST_ != null) {
									for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
										errorMessage += "<p>" + data._ERROR_MESSAGE_LIST_[i] + "</p>";
									}
								}
								if (data._ERROR_MESSAGE_ != null) {
									errorMessage += "<p>" + data._ERROR_MESSAGE_ + "</p>";
								}
								if (errorMessage != "") {
									$("#container").empty();
									$("#jqxNotification").jqxNotification({ template: "error"});
									$("#jqxNotification").html(errorMessage);
									$("#jqxNotification").jqxNotification("open");
								} else {
									$("#container").empty();
									$("#jqxNotification").jqxNotification({ template: "info"});
									$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
									$("#jqxNotification").jqxNotification("open");
								}
							}
						}
					});
				}
			}]);
	}
	
	$(document).ready(function(){
		initGridSFCWeek();
		getDataForGridSFCWeek("${salesForecastId}", null);
	});
</script>