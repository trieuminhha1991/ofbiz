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
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>
<#assign productPlanId = parameters.productPlanId !>
<#assign customTimePeriodId = parameters.customTimePeriodId !>

<script>
	<#if planHeader?has_content>
		var statusId = '${planHeader.statusId?if_exists}';
		<#assign customTimePeriodId = planHeader.customTimePeriodId?if_exists !>
	</#if>
</script>

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

<div id='Menu'>
	<ul>
	    <li id="linkToOrder"><a id="hrToOrder" href="">${uiLabelMap.BPOCreateNewOrderPO}</a></li>
	</ul>
</div>

<div id="wdFilter" style="display: none;">
	<div>${uiLabelMap.DmsChoiseFilter}</div>
	<div>
		<div class="row-fluid">
			<div class="span6 div-inline-block">
				<div class="span7"><span style="float: right; margin-top: 5px;">${uiLabelMap.fromMonth}:</span></div>
				<div class="span5">
					<div id="fromMonth" class="green-label"></div>
				</div>
			</div>
			<div class="span6 div-inline-block">
				<div class="span7"><span style="float: right; margin-top: 5px;">${uiLabelMap.toMonth}:</span></div>
				<div class="span5">
					<div id="toMonth" class="green-label"></div>
				</div>
			</div>
		</div>
		<hr/>
		<div class="row-fluid">
	 		<div class="">
	 			<button id='filterCancel' class="btn btn-mini form-action-button btn-danger pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
	 			<button id='filterApply' class="btn btn-mini form-action-button btn-primary pull-right"><i class='icon-ok'></i>${uiLabelMap.DmsFilter}</button>
	        </div>
        </div>
	</div>
</div>

<div id="notificationCreatePurchaseOrder"></div>
<div id="imexPlanItemView-tab" class="tab-pane<#if activeTab?exists && activeTab == "imexPlanItemView-tab"> active</#if>">
	<div class="title-status" id="statusTitle" style="margin-top: -10px;">
		<#assign status = delegator.findOne("StatusItem", {"statusId" : planHeader.statusId?if_exists}, false)!>
		${status?if_exists.get("description", locale)}
	</div>
	<div class="widget-box transparent">
		<div class="row-fluid"><div class="span12 widget-container-span">
			 	<div class="widget-box transparent">
			 		<div class="widget-body">
					<div class="span11">
						<div class="row-fluid margin-top5">
							<div class="span5">
								<div class="row-fluid">
									<div class='span4'>
										<span>${uiLabelMap.POProductPlanID}</span>
									</div>
									<div class="span7">
										<div class="green-label">
											<#if planHeader.productPlanCode?has_content>
												${planHeader.productPlanCode?if_exists}
											<#else>
												${planHeader.productPlanId?if_exists}
											</#if>
										</div>
							   		</div>
								</div>
							</div>
							<div class="span5">
								<div class="row-fluid">
									<div class='span4'>
										<span>${uiLabelMap.Supplier}</span>
									</div>
									<div class="span7">
										<div class="green-label">
											${planHeader.supplierPartyCode?if_exists} - ${planHeader.supplierPartyName?if_exists}
										</div>
							   		</div>
								</div>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span5">
								<div class="row-fluid">
									<div class='span4'>
										<span>${uiLabelMap.DmsNamePlan}</span>
									</div>
									<div class="span7">
										<div class="green-label">
											${planHeader.productPlanName?if_exists}
										</div>
							   		</div>
								</div>
							</div>
							<div class="span5">
								<div class="row-fluid">
									<div class='span4'>
										<span>${uiLabelMap.CurrencyUom}</span>
									</div>
									<div class="span7">
										<div class="green-label">
											${planHeader.currencyUomId?if_exists}
										</div>
							   		</div>
								</div>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span5">
								<div class="row-fluid">
									<div class='span4'>
										<span>${uiLabelMap.CommercialPeriod}</span>
									</div>
									<div class="span7">
										<div class="green-label">
											${planHeader.periodName?if_exists}
										</div>
							   		</div>
								</div>
							</div>
							<div class="span5">
								<div class="row-fluid">
									<div class='span4'>
										<span>${uiLabelMap.Description}</span>
									</div>
									<div class="span7">
										<div class="green-label">
											${StringUtil.wrapString(planHeader.description?if_exists)}
										</div>
							   		</div>
								</div>
							</div>
						</div>
					</div>
						<div class="row-fluid">
							<div class="row-fluid span8" style="margin-top:5px;">
								<div class="span1" id="containerNtf"></div>
							</div>
							<div class="row-fluid span4" style="margin-top:5px;">
								<div class="span6"></div>
								<div class="span6" style="text-align: right; margin: inherit;cursor: pointer;">
									<div class="span6"><a id="filterPlan" class="icon-filter open-sans">${uiLabelMap.DmsFilter}</a></div>
									<div class="span6">
										<a id="cancelFilterPlan">
											<i class="icon-filter open-sans"><span style="color: red;right: 6px; position: relative;">x</span></i>${uiLabelMap.DmsCancelFilter}
										</a>
									</div>
								</div>
							</div>
						</div>
						<hr style="margin: 0px !important;"/>
						<div id="jqxPlanItem">
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
var listWeekHeader = [];

$('#wdFilter').jqxWindow({height:155, resizable: false, isModal: true, position:'center', width: 400, autoOpen: false, theme: 'olbius' });
$("#fromMonth").jqxNumberInput({inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:1, max:12, width:'90%', spinButtons: true, decimalDigits: 0, decimal: 1});
$("#toMonth").jqxNumberInput({inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:1, max:12, width:'90%', spinButtons: true, decimalDigits: 0, decimal: 1});
var contextMenu = $("#Menu").jqxMenu({ width: 200, height: 29, theme: 'olbius', autoOpenPopup: false, mode: 'popup'});
$("#jqxPlanItem").on('contextmenu', function () {
    return false;
});

function loadDataForGrid(productPlanId){
	$.ajax({
		beforeSend: function(){
			$("#loader_page_common").show();
	    },
	    complete: function(){
	    	$("#loader_page_common").hide();
	    },
		url: "JqxGetPlanOfYear",
		type: "POST",
		data: {
			productPlanId: productPlanId,
			isImEx: 'Y'			
		},
		dataType: "json",
		success: function(data) {
			var listIterator = data.listIterator;
			listWeekHeader = data.listWeekHeader;
			loadProductDataSumToJqx(listIterator, listWeekHeader);
		}
	});
}

$("#jqxPlanItem").on('cellclick', function (event) {
	var args = event.args;
    var rowBoundIndex = args.rowindex;
    var rowVisibleIndex = args.visibleindex;
    var rightclick = args.rightclick; 
    var ev = args.originalEvent;
    var columnindex = args.columnindex;
    var dataField = args.datafield;
    var value = args.value;
    if (args.rightclick && dataField != "productId" && dataField != "productName" && ('IMPORT_PLAN_APPROVED' == statusId)) {
        $("#jqxPlanItem").jqxGrid('selectcell', event.args.rowindex, dataField);
        $('#hrToOrder').attr('href', 'newPurchaseOrder?customTimePeriodId='+dataField+'&productPlanId=${productPlanId}');
        $('#hrToOrder').attr('target', '_blank');
        var scrollTop = $(window).scrollTop();
        var scrollLeft = $(window).scrollLeft();
        contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
        return false;
    }
});

function initDataFields(listWeekHeader){
	var arrHeaders = [];
	var arrColumns = [];
	var arrGroups = [];
	var h0 = {name: 'sequenceId', type: 'string'};
	var h1 = {name: 'productId', type: 'string'};
	var h2 = {name: 'productName', type: 'string'};
	var h3 = {name: 'productCode', type: 'string'};
	var c0 = {text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
		    };
	var c1 = {text: '${uiLabelMap.productId}', datafield: 'productId', editable: false, hidden: true, width: 100,filterable: true, pinned: true};
	var c3 = {text: '${uiLabelMap.productId}', datafield: 'productCode', editable: false, width: 100,filterable: true, pinned: true};
	var c2 = {text: '${uiLabelMap.BPOProductName}', datafield: 'productName', editable: false, filterable: true, pinned: true, minwidth: 250};
	arrHeaders.push(h0);
	arrHeaders.push(h1);
	arrHeaders.push(h2);
	arrHeaders.push(h3);
	arrColumns.push(c0);
	arrColumns.push(c1);
	arrColumns.push(c3);
	arrColumns.push(c2);
	for(var i in listWeekHeader){
		var customTimePeriodId2 = listWeekHeader[i].customTimePeriodId;
		var column2 = {text: "${uiLabelMap.BIEPlan}", datafield: listWeekHeader[i].customTimePeriodId+"_plan", columngroup: listWeekHeader[i].customTimePeriodId, editable: false, filterable: false, columntype: 'numberinput', width: 70, cellsalign: 'right',
		  			    cellsrenderer: function(row, column, value){
							if (value) return '<span class="align-right">' + formatnumber(value) + '<span>';
							return '<span class="align-right">' + 0 + '<span>';
						},
		  			    cellclassname: function (row, column, value, data) {
						    if(row % 2 == 0){
						    	return 'green1';
						    }
						}
					};
		var header2 = {name: listWeekHeader[i].customTimePeriodId+"_plan", type: 'number'};
		arrHeaders.push(header2);
		arrColumns.push(column2);
		
		var header = {name: listWeekHeader[i].customTimePeriodId, type: 'number'};
		var column1 = {text: "${uiLabelMap.BIERemain}", datafield: listWeekHeader[i].customTimePeriodId, columngroup: listWeekHeader[i].customTimePeriodId, editable: false, filterable: false, columntype: 'numberinput', width: 70, cellsalign: 'right',
		  			    cellsrenderer: function(row, column, value){
		  			    	var data = $("#jqxPlanItem").jqxGrid("getrowdata",row);
		  			    	var hf = "newPurchaseOrder?customTimePeriodId="+ column +"&productPlanId=${productPlanId}";
		  			    	if ('IMPORT_PLAN_APPROVED' == statusId) {
								if (value) return '<span class="align-right" title="${uiLabelMap.BPOCreateNewOrderPO}"><a href="'+hf+'">' + formatnumber(value) + '</a><span>';
							}
							if (value) return '<span class="align-right">' + formatnumber(value) + '<span>';
							return '<span class="align-right">' + 0 + '<span>';
						},
		  			    cellclassname: function (row, column, value, data) {
						    if(row % 2 == 0){
						    	return 'green1';
						    }
						}
					};
		arrHeaders.push(header);
		arrColumns.push(column1);
		arrGroups.push({ text: listWeekHeader[i].periodName, align: 'center', name: listWeekHeader[i].customTimePeriodId},);
	}
	var arrRe = {listHeader: arrHeaders, listColumns: arrColumns, listGroups: arrGroups};
	return arrRe;
}

function updatePlanItemWeek(newvalue, productId, datafield){
	$.ajax({
		url: "updatePlanItemWeek",
		type: "POST",
		data: {planQuantity: newvalue, productPlanId: '${productPlanId}', productId: productId, customTimePeriodId: datafield, statusId: "PLANITEM_CREATED"},
		dataType: "json",
		success: function(data) {
			
		}
	});
}

//load jqxGrid
function loadProductDataSumToJqx(listIterator, listWeekHeader){
	var obj = initDataFields(listWeekHeader);
	var sourceProduct = {
	    datafields: obj.listHeader,
	    localdata: listIterator,
	    datatype: "array",
	};
    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
    $("#jqxPlanItem").jqxGrid({
        source: dataAdapterProduct,
        filterable: true,
        localization: getLocalization(),
        showfilterrow: true,
        theme: 'olbius',
        rowsheight: 30,
        width: '100%',
        height: 585,
        autoloadstate: true,
        autoheight: true,
        columnsresize: true,
        enabletooltips: true,
        selectionmode: 'singlecell',
        pageable: true,
        pagesize: 15,
        editable: true,
	    columns: obj.listColumns,
	    columngroups: obj.listGroups,
    });
}

$('#filterApply').on('click', function(){
	var fromMonth = $('#fromMonth').val();
	var toMonth = $('#toMonth').val();
	$('#wdFilter').jqxWindow('close');
	$.ajax({
		beforeSend: function(){
			$("#loader_page_common").show();
	    },
	    complete: function(){
	    	$("#loader_page_common").hide();
	    },
		url: "JqxGetPlanOfYear",
		type: "POST",
		data: {fromMonth: fromMonth, toMonth: toMonth, productPlanId: '${productPlanId}', isImEx: 'Y'},
		dataType: "json",
		success: function(data) {
			var listIterator = data.listIterator;
			listWeekHeader = data.listWeekHeader;
			loadProductDataSumToJqx(listIterator, listWeekHeader);
		}
	});
});

$('#filterCancel').on('click', function(){
	$('#wdFilter').jqxWindow('close');
});

$('#filterPlan').on('click', function(){
	$('#wdFilter').jqxWindow('open');
});

$('#cancelFilterPlan').on('click', function() {
	loadDataForGrid('${productPlanId}');
})

$(document).ready(function(){
	var listWeek=[];
	loadDataForGrid('${productPlanId}');
});
</script>