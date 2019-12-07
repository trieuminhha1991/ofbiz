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
	background-color: #DEEDF5;
}
.green1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
	color: black;
	background-color: #DEEDF5;
}

.yellow1 {
	color: black;
	background-color: #FBFF05;
}
.yellow1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
	color: black;
	background-color: #FBFF05;
}
.bluewhite {
	color: black;
	background-color: #08F5CA;
}
.bluewhite:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
	color: black;
	background-color: #08F5CA;
}
</style>

<@jqGridMinimumLib/>

<#assign productPlanId = parameters.productPlanId !>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>

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

<div id="jqxNotification"></div>
<div id="container"></div>

<div class="row-fluid">
	<div class="span1" id="notificationCreatePurchaseOrder"></div>
	<div class="span3" style="margin-top:5px;">
	</div>
	<div class="span4" style="margin-top:5px;font-size: 25px; text-align: center">
		<b>${uiLabelMap.DmsPlanItemStock}</b>
	</div>
	<div class="span4" style="margin-top:5px;">
	</div>
</div>

<div id="jqxStockAndPlan"></div>

<script type="text/javascript">
	var uiLabelMap = {};
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
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
		inputMode: "simple", spinMode: "simple", groupSeparator: ".", min:1, max:12, 
		width:"90%", spinButtons: true, decimalDigits: 0, decimal: 1
	});
	$("#toMonth").jqxNumberInput({
		inputMode: "simple", spinMode: "simple", groupSeparator: ".", min:2, max:12, 
		width:"90%", spinButtons: true, decimalDigits: 0, decimal: 2
	});
	var contextMenu = $("#Menu").jqxMenu({ width: 200, theme: theme, autoOpenPopup: false, mode: "popup" });
	$("#jqxStockAndPlan").on("contextmenu", function () {
		return false;
	});
	$("#jqxNotification").jqxNotification({ 
		width: "100%", appendContainer: "#container", opacity: 0.9,
		autoClose: false, template: "error"
	});
	
	var listWeekHeader = [];
	var flag = true;
	
	function initGridPlanPO(){
		$("#jqxStockAndPlan").jqxGrid({
			source: {
				datatype: "array"
			},
			localization: getLocalization(),
			filterable: true,
			showfilterrow: true,
			virtualmode: false,
			theme: theme,
			rowsheight: 30,
			width: "100%",
			autoheight: true,
			columnsresize: true,
			editmode: "click",
			enabletooltips: true,
			height: 500,
			autoheight: false,
			selectionmode: "singlecell",
			pageable: true,
			pagesize: 12,
			pagesizeoptions: ["12", "24", "36"],
			editable: true,
			columns: [
				{text: "${uiLabelMap.productId}", datafield: "productCode", editable: false, filterable: true, pinned: true, width: 150},
				{text: "${uiLabelMap.BPOProductName}", datafield: "productName", editable: false, width: 200, filterable: true},
				{text: "${uiLabelMap.SalesCycle}", datafield: "SalesCycle", editable: false, width: 150, filterable: true}
			],
			showtoolbar: true,
			rendertoolbar: function(toolbar){
				if (flag){
					var container = $("<div style="margin: 12px 4px 0px 0px;" class="pull-right"></div>");
					var btnSave = $("<a id="saveSFC" style="cursor: pointer; margin-right: 15px; font-size: 15px;"><i class="icon-save"></i>${uiLabelMap.CommonSave}</a>");
					var btnFilter = $("<a id="filterPlan" style="cursor: pointer; margin-right: 15px; font-size: 15px;" class="icon-filter open-sans">${uiLabelMap.DmsFilter}</a>");
					var btnCancelFilter = $("<a id="cancelFilterPlan" style="cursor: pointer; font-size: 15px;"><i class="icon-filter open-sans"><span style="color: red; right: 3px; position: relative;">x</span></i>${uiLabelMap.DmsCancelFilter}</a>");
					toolbar.append(container);
					<#if hasOlbPermission("MODULE", "PLANPO_PLAN_EDIT", "UPDATE")>
					container.append(btnSave);
					container.append(btnFilter);
					container.append(btnCancelFilter);
					</#if>
					btnSave.click(function() {
						saveStockPlan();
					});
					btnFilter.click(function() {
						$("#wdFilter").jqxWindow("open");
					});
					btnCancelFilter.click(function() {
						getDataForGrid("${productPlanId}", null);
					});
				}
				flag = false;
			}
		});
	}

	function getDataForGrid(productPlanId, isFilter){
		var data;
		if (isFilter){
			var checkedMonth = $("#monthMode").jqxRadioButton("checked");
			var checkedWeek = $("#weekMode").jqxRadioButton("checked");
			var fromMonth = $("#fromMonth").val();
			var toMonth = $("#toMonth").val();
			data = {fromMonth: fromMonth, 
					toMonth: toMonth, productPlanId: productPlanId, 
					checkedMonth: checkedMonth, 
					checkedWeek: checkedWeek };
		} else {
			data = {productPlanId: productPlanId};
		}
		$.ajax({
			beforeSend: function(){
				$("#loader_page_common").show();
			},
			complete: function(){
				$("#loader_page_common").hide();
			},
			url: "JqxGetStockAndPlanPO",
			type: "POST",
			data: data,
			dataType: "json",
			success: function(data) {
				var listIterator = data.listIterator;
				listWeekHeader = data.listWeekHeader;
				updateDataForGrid(listWeekHeader, listIterator)
			}
		});
	}
	
	function updateDataForGrid(listWeekHeader, listIterator){
		var obj = initDataFields(listWeekHeader);
		var tmpSource = $("#jqxStockAndPlan").jqxGrid("source");
		if (tmpSource){
			tmpSource._source.datafields = obj.listHeader;
			tmpSource._source.localdata = listIterator;
		}
		$("#jqxStockAndPlan").jqxGrid({columns: obj.listColumns});
		$("#jqxStockAndPlan").jqxGrid("source", tmpSource);
		$("#jqxStockAndPlan").jqxGrid("updatebounddata");
	}

	function initDataFields(listWeekHeader){
		var arrHeaders = [];
		var arrColumns = [];
		var h1 = {name: "productId", type: "string"};
		var h5 = {name: "productCode", type: "string"};
		var h2 = {name: "productName", type: "string"};
		var h3 = {name: "MOQ", type: "string"};
		var h4 = {name: "SalesCycle", type: "string"};
		var cellclassname = function (row, column, value, data) {
			if((row+1) % 6 == 0){
				return "yellow1";
			}
		};
		var c1 = {text: "${uiLabelMap.productId}", datafield: "productId", editable: false,
					filterable: true, hidden: true, pinned: true, cellclassname: cellclassname};
		var c5 = {text: "${uiLabelMap.productId}", datafield: "productCode", editable: false,
					filterable: true, pinned: true, cellclassname: cellclassname, width: 150};
		var c2 = {text: "${uiLabelMap.BPOProductName}", datafield: "productName", editable: false,
					filterable: true, pinned: true, width: 200, cellclassname: cellclassname};
		var c3 = {text: "${uiLabelMap.MOQ}", datafield: "MOQ", editable: false, 
					filterable: true, width: 50, pinned: true, hidden: true, cellclassname: cellclassname};
		var c4 = {text: "${uiLabelMap.SalesCycle}", datafield: "SalesCycle", editable: false, 
					width: 160, filterable: true, pinned: true, cellclassname: cellclassname,
					cellsrenderer: function(row, column, value){
						if((row+1) % 6 == 0){
							return "<span style="text-align: right; color: black;"><b>" + value +"</b></span>";
						}
					}
		};
		arrHeaders.push(h1);
		arrHeaders.push(h5);
		arrHeaders.push(h2);
		arrHeaders.push(h3);
		arrHeaders.push(h4);
		arrColumns.push(c1);
		arrColumns.push(c5);
		arrColumns.push(c2);
		arrColumns.push(c3);
		arrColumns.push(c4);
		var cellbeginedit = function (row, datafield, columntype, value) {
			if ((row+1)%6 != 4) 
				return false;
			else {
				var dateColumn = "";
				for(var i = 0; i < listWeekHeader.length; i++){
					if(listWeekHeader[i].customTimePeriodId == datafield){
						dateColumn = listWeekHeader[i].periodName;
						break;
					}
				}
				var arr = dateColumn.split("-");
				var today = new Date();
				var date = new Date(today);
				date.setFullYear(arr[2]);
				date.setMonth(arr[1]-1);
				date.setDate(arr[0]);
				var yesterday = new Date(today);
					
				var d = today.getDate();
				var m = today.getMonth() +1;
				var y = today.getFullYear();
				var t = today.getDay();
				if(t==0) yesterday.setDate(today.getDate() - 6);
				else yesterday.setDate(today.getDate() - (t-1));
				if(date < yesterday){
					$("#"+row +""+datafield).jqxTooltip({ disabled: false });
					$("#"+row +""+datafield).jqxTooltip({ content: "<span style="color: red;">${uiLabelMap.DmsRestrictEditWeek}!</span>", theme: "customtooltip", position: "top", name: "movieTooltip"});
					$("#"+row +""+datafield).jqxTooltip("open");
					$("#"+row +""+datafield).bind("close", function () {
						$("#"+row +""+datafield).jqxTooltip({ disabled: true });
					}); 
					return false;
				}
			}
		}
		
		for(var i = 0; i < listWeekHeader.length; i++){
			var df = listWeekHeader[i].customTimePeriodId;
			var header = {name: listWeekHeader[i].customTimePeriodId, type: "number"};
			var column = {text: listWeekHeader[i].periodName, datafield: listWeekHeader[i].customTimePeriodId, editable: true, filterable: false, columntype: "numberinput", width: 90, cellsalign: "right",
					createeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({inputMode: "simple", spinMode: "simple", groupSeparator: ".", min:0 });
					},
					cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						if(newvalue < 0) return false;
						if((row+1)%6 == 4){
							if (newvalue != oldvalue){
								if(!oldvalue){
									oldvalue = 0;
								}
								var cell = $("#jqxStockAndPlan").jqxGrid("getcellvalue", row+1, datafield);
								var newCell = parseFloat(cell) + parseFloat(newvalue) - parseFloat(oldvalue);
								$("#jqxStockAndPlan").jqxGrid("setcellvalue", row+1, datafield, newCell);
								
								var cellOpen = newCell;
								var m = i;
								for(var k=0; k<listWeekHeader.length; k++){
									if(listWeekHeader[k].customTimePeriodId == datafield){
										m = k;
										break;
									}
								}
								
								for(var j=k+1; j<listWeekHeader.length; j++){
									var cellOpenNextOld = $("#jqxStockAndPlan").jqxGrid("getcellvalue", row-3, listWeekHeader[j].customTimePeriodId);
									$("#jqxStockAndPlan").jqxGrid("setcellvalue", row-3, listWeekHeader[j].customTimePeriodId, cellOpen);
									var cellEnding = $("#jqxStockAndPlan").jqxGrid("getcellvalue", row+1, listWeekHeader[j].customTimePeriodId);
									var cellEndingNext = parseFloat(cellOpen) - parseFloat(cellOpenNextOld) + parseFloat(cellEnding);
									$("#jqxStockAndPlan").jqxGrid("setcellvalue", row+1, listWeekHeader[j].customTimePeriodId, cellEndingNext);
									
									cellOpen = cellEndingNext;
								}
							}
						}
					},
					cellbeginedit: cellbeginedit,
					cellsrenderer: function(row, column, value, defaulthtml, columnproperties){
						var id = row +""+column;
						if((row+1) % 6 == 1 || (row+1) % 6 == 2){
							return "<span style="text-align: right; color: #0F48E8; font-size: 14px;"><b>" + value.toLocaleString(locale) +"</b></span>";
						} if ((row+1) % 6 == 0){
							var cellEnd = $("#jqxStockAndPlan").jqxGrid("getcellvalue", row-1, column);
							var vl = 0;
							var m = 0;
							var vl1 = 0;
							var vl2 = 0;
							var vl3 = 0;
							var vl4 = 0;
							for(var j=0; j<listWeekHeader.length; j++){
								if(listWeekHeader[j].customTimePeriodId == column){
									m = j;
									break;
								}
							}
							if(listWeekHeader[m+1]) vl1 = $("#jqxStockAndPlan").jqxGrid("getcellvalue", row-4, listWeekHeader[m+1].customTimePeriodId);
							if(listWeekHeader[m+2]) vl2 = $("#jqxStockAndPlan").jqxGrid("getcellvalue", row-4, listWeekHeader[m+2].customTimePeriodId);
							if(listWeekHeader[m+3]) vl3 = $("#jqxStockAndPlan").jqxGrid("getcellvalue", row-4, listWeekHeader[m+3].customTimePeriodId);
							if(listWeekHeader[m+4]) vl4 = $("#jqxStockAndPlan").jqxGrid("getcellvalue", row-4, listWeekHeader[m+4].customTimePeriodId);
							var avg = (parseFloat(vl1)+parseFloat(vl2)+parseFloat(vl3)+parseFloat(vl4));
							if(avg>0){
								vl = parseFloat(cellEnd)*4*7/avg;
							}
							if(vl > 0){
								vl = Math.round(vl);
								if(vl>15) return "<span style="text-align: right; color: red; font-size: 14px;"><b>" + vl.toLocaleString(locale) +"</b></span>";
								else if(vl<7) return "<span style="text-align: right; font-size: 14px;"><b>" + vl.toLocaleString(locale) +"</b></span>";
								else return "<span style="text-align: right; color: #0F48E8; font-size: 14px;"><b>" + vl.toLocaleString(locale) +"</b></span>";
							} else return "<span style="text-align: right; color: #0F48E8; font-size: 14px;"><b>-</b></span>";
						} else if((row+1) % 6 == 4){
							return "<span id=\""+id+"\" style="text-align: right; font-size: 14px;" >" + value.toLocaleString(locale) +"</span>";
						} else return "<span style="text-align: right; font-size: 14px;" >" + value.toLocaleString(locale) +"</span>";
					},
					cellclassname: function (row, column, value, data) {
						if((row+1) % 6 == 0){
							return "green1";
						}
						if((row+1) % 6 == 3){
							return "bluewhite";
						}
					}
			};
			arrHeaders.push(header);
			arrColumns.push(column);
		}
		var arrRe = {listHeader: arrHeaders, listColumns: arrColumns};
		return arrRe;
	}

	$("#filterCancel").on("click", function(){
		$("#wdFilter").jqxWindow("close");
	});
	
	function saveStockPlan(){
		var sfcItems = $("#jqxStockAndPlan").jqxGrid("getboundrows");
		var dataPlans = [];
		var dataEnds = [];
		for(var i=0; i <= sfcItems.length; i++){
			if((i+1)%6 == 4){
				dataPlans.push(sfcItems[i]);
			}
			if((i+1)%6 == 5){
				dataEnds.push(sfcItems[i]);
			}
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
							window.location.href = "listPlanItem?productPlanId=${productPlanId}";
						},
						url: "storePlanAndStock",
						type: "POST",
						data: {dataPlans: JSON.stringify(dataPlans), dataEnds: JSON.stringify(dataEnds), productPlanId: "${productPlanId}"},
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

	$("#filterApply").on("click", function(){
		$("#wdFilter").jqxWindow("close");
		getDataForGrid("${productPlanId}", "Y");
	});
	
	$(document).ready(function(){
		initGridPlanPO();
		getDataForGrid("${productPlanId}", null);
	});
</script>