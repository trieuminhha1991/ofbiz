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
<#assign salesForecastId = parameters.salesForecastId !>
<div id="jqxNotificationSupplierProductExits"> 
	<div id="notificationSupplierProductExits">
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

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<div id='Menu'>
	<ul>
	    <li id="linkToOrder" customTimePeriodId=""><a id="hrToOrder" href="">${uiLabelMap.DmsGoCreatePO}</a></li>
	</ul>
</div>

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
 			<button id='filterCancel' class="btn btn-mini form-action-button btn-danger pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
 			<button id='filterApply' class="btn btn-mini form-action-button btn-primary pull-right"><i class='icon-ok'></i>${uiLabelMap.DmsFilter}</button>
        </div>
    </div>
	
</div>
</div>


<#-- <div class="row-fluid">
	<div class="span1" id="notificationCreatePurchaseOrder"></div>
	<div class="span3" style="margin-top:5px;">
	</div>
	<div class="span4" style="margin-top:5px;font-size: 25px; text-align: center">
		<b>${uiLabelMap.DmsPlanItem}</b>
	</div>
	<div class="span4" style="margin-top:5px;">
	</div>
</div> -->
<div id="contentNotificationSupplierProductExits" class="">
</div>
<div id="container" class="">
</div>
<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span6"></div>
		<div class="span6" style="text-align: right; margin: inherit;cursor: pointer;">
			<div class="span3">
				<a id="saveSFC"><i class="icon-save"></i>${uiLabelMap.CommonSave}</a>
			</div>
			<div class="span2"><a id="filterPlan" class="icon-filter open-sans">${uiLabelMap.DmsFilter}</a></div>
			<div class="span4">
				<a id="cancelFilterPlan">
					<i class="icon-filter open-sans"><span style="color: red;right: 6px; position: relative;">x</span></i>${uiLabelMap.DmsCancelFilter}
				</a>
			</div>
			<div class="span2"></div>
		</div>
	</div>
</div>
<hr style="margin: 0px !important;"/>
<div id="jqxPlanItem"></div>

<script type="text/javascript">
var uiLabelMap = {};
uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";

//var local
$("#jqxNotificationSupplierProductExits").jqxNotification({ width: "100%", appendContainer: "#contentNotificationSupplierProductExits", opacity: 0.9, autoClose: false, template: "warning" });

$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: false, template: "error" });
	$("#monthMode").jqxRadioButton({height: 25, checked: true});
	 $("#weekMode").jqxRadioButton({height: 25});
	 
	 $("#monthMode").on('change', function (event) {
		 $('#toMonth').jqxNumberInput({ max: 12 });
		 $('#fromMonth').jqxNumberInput({ max: 12 });
    });
	 $("#weekMode").on('change', function (event) {
		 $('#toMonth').jqxNumberInput({ max: 52 });
		 $('#fromMonth').jqxNumberInput({ max: 52 });
    });
	 
	
	 var getLocalization = function () {
		    var localizationobj = {};
		    localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
		    localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
		    localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
		    localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
		    localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
		    localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
		    localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
		    localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
		    localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
		    localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
		    localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
		    localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
		    localizationobj.groupsheaderstring = "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
		    localizationobj.todaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
		    localizationobj.clearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
		    return localizationobj;
		};
var listWeekHeader = [];
//BEGIN init element jqx

$('#wdFilter').jqxWindow({height:165, resizable: false, isModal: true, position:'center', width: 450, autoOpen: false, theme: 'olbius' });

$("#fromMonth").jqxNumberInput({inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:1, max:12, width:'90%', spinButtons: true, decimalDigits: 0, decimal: 1});
$("#toMonth").jqxNumberInput({inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:2, max:12, width:'90%', spinButtons: true, decimalDigits: 0, decimal: 2});
//MENU
var contextMenu = $("#Menu").jqxMenu({ width: 200, height: 29, theme: 'olbius', autoOpenPopup: false, mode: 'popup'});
$("#jqxPlanItem").on('contextmenu', function () {
    return false;
});


//END MENU

function loadDataForGrid(salesForecastId){
	$.ajax({
		beforeSend: function(){
			$("#loader_page_common").show();
	    },
	    complete: function(){
	    	$("#loader_page_common").hide();
	    },
		url: "JqxGetSalesForecastByWeek",
		type: "POST",
		data: {salesForecastId: salesForecastId},
		dataType: "json",
		success: function(data) {
			var check = data.value;
			if(check == 'success'){
				var listIterator = data.listIterator;
				listWeekHeader = data.listWeekHeader;
				loadProductDataSumToJqx(listIterator, listWeekHeader);
			}else{
				$("#notificationSupplierProductExits").text('${StringUtil.wrapString(uiLabelMap.POCheckPeriodExits)}');
				$("#jqxNotificationSupplierProductExits").jqxNotification('open');
				var listIterator = data.listIterator;
				listWeekHeader = data.listWeekHeader;
				loadProductDataSumToJqx(listIterator, listWeekHeader);
			}
		}
	});
}

//load jqxGrid
function loadProductDataSumToJqx(listIterator, listWeekHeader){
	var obj = initDataFields(listWeekHeader);
	var sourceProduct =
	    {
	        datafields: obj.listHeader,
	        localdata: listIterator,
	        datatype: "array",
	    };
	    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
	    $("#jqxPlanItem").jqxGrid({
	        source: dataAdapterProduct,
	        localization: getLocalization(),
	        filterable: true,
	        showfilterrow: true,
	        theme: 'olbius',
	        rowsheight: 30,
	        width: '100%',
	        autoheight: true,
	        columnsresize: true,
	        enabletooltips: true,
	        height: 410,
	        autoheight: false,
	        selectionmode: 'singlecell',
	        editmode: 'click',
	        pageable: true,
	        sortable: true,
	        pagesize: 10,
	        editable: false,
	//        selectionmode: 'checkbox',
		    columns: obj.listColumns
	    });
}

function initDataFields(listWeekHeader){
	var arrHeaders = [];
	var arrColumns = [];
	var h1 = {name: 'productId', type: 'string'};
	var h3 = {name: 'productCode', type: 'string'};
	var h2 = {name: 'productName', type: 'string'};
	var c1 = {text: '${uiLabelMap.productId}', datafield: 'productId', editable: false, filterable: true, pinned: true, hidden: true};
	var c3 = {text: '${uiLabelMap.productId}', datafield: 'productCode', editable: false, filterable: true, pinned: true};
	var c2 = {text: '${uiLabelMap.BPOProductName}', datafield: 'productName', editable: false,  width: 240,filterable: true, pinned: true};
	arrHeaders.push(h1);
	arrHeaders.push(h2);
	arrHeaders.push(h3);
	arrColumns.push(c1);
	arrColumns.push(c3);
	arrColumns.push(c2);
	for(var i=0; i<listWeekHeader.length; i++){
		var aa = listWeekHeader[i].periodName;
		var header = {name: listWeekHeader[i].customTimePeriodId, type: 'number'};
		var column = {text: listWeekHeader[i].periodName, datafield: listWeekHeader[i].customTimePeriodId, editable: true, filterable: false, columntype: 'numberinput', width: 90, cellsalign: 'right',
				createeditor: function (row, cellvalue, editor) {
	                editor.jqxNumberInput({inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0 });
	            },
	            cellsrenderer: function(row, column, value, defaulthtml, columnproperties){
  					if (value){
  						var id = row +''+column;
  						return '<span id=\"'+id+'\" style="text-align: right">' + value.toLocaleString('${localeStr}') +'</span>';
  					}
  				},
  				cellbeginedit: function (row, datafield, columntype) {
  					var dateColumn = '';
  					for(var i=0; i<listWeekHeader.length; i++){
  						if(listWeekHeader[i].customTimePeriodId == datafield){
  							dateColumn = listWeekHeader[i].periodName;
  							break;
  						}
  					}
	            	var arr = dateColumn.split('-');
	            	var date = new Date(arr[2]+'-'+arr[1]+'-'+arr[0]);
	            	var today = new Date();
	            	var d = today.getDate();
	            	var m = today.getMonth() +1;
	            	var y = today.getFullYear();
	            	var t = today.getDay();
	            	if(t==0) d = d-6;
	            	else d = d-(t-1);
	            	today = new Date(y+'-'+m+'-'+d);
	            	if(date.getTime() < today.getTime()){
	            		$("#"+row +''+datafield).jqxTooltip({ disabled: false });
	            		$("#"+row +''+datafield).jqxTooltip({ content: '<span style="color: red;">${uiLabelMap.DmsRestrictEditWeek}!</span>', theme: 'customtooltip', position: 'top', name: 'movieTooltip'});
  			        	$("#"+row +''+datafield).jqxTooltip('open');
  			        	$("#"+row +''+datafield).bind('close', function () {
  			        		$("#"+row +''+datafield).jqxTooltip({ disabled: true });
  			        	}); 
	            		return false;
	            	}
  			    }
//	            cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
//  			        if (newvalue != oldvalue){
//  			        	var data = $('#jqxPlanItem').jqxGrid('getrowdata', row);
//  			        	updatePlanItemWeek(newvalue, data.productId, datafield);
//  			        }
//  			    },
//  			    cellclassname: function (row, column, value, data) {
//				    if(row % 2 == 0){
//				    	return 'green1';
//				    }
//				}
		};
		arrHeaders.push(header);
		arrColumns.push(column);
	}
	var arrRe = {listHeader: arrHeaders, listColumns: arrColumns};
	return arrRe;
}

$('#filterApply').on('click', function(){
	var checkedMonth = $('#monthMode').jqxRadioButton('checked');
	var checkedWeek = $('#weekMode').jqxRadioButton('checked');
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
		url: "JqxGetSalesForecastByWeek",
		type: "POST",
		data: {fromMonth: fromMonth, toMonth: toMonth, salesForecastId: '${salesForecastId}', checkedMonth: checkedMonth, checkedWeek: checkedWeek},
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

$('#saveSFC').on('click', function(){
	var sfcItems = $('#jqxPlanItem').jqxGrid('getboundrows');
	for(var i = 0; i<sfcItems.length; i++){
		delete sfcItems[i].productCode;
	}
	$.ajax({
		beforeSend: function(){
			$("#loader_page_common_save").show();
	    },
	    complete: function(){
	    	$("#loader_page_common_save").hide();
	    },
		url: "storeSaleForecastByPO",
		type: "POST",
		data: {salesForecastId: '${salesForecastId?if_exists}', organizationPartyId: '${organizationPartyId?if_exists}', internalPartyId: '${organizationPartyId?if_exists}', currencyUomId: '${currencyUomId?if_exists}', sfcItems: JSON.stringify(sfcItems)},
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
    	        	$("#jqxNotification").jqxNotification({ template: 'error'});
    	        	$("#jqxNotification").html(errorMessage);
    	        	$("#jqxNotification").jqxNotification("open");
		        } else {
        			$('#container').empty();
    	        	$('#jqxNotification').jqxNotification({ template: 'info'});
    	        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
    	        	$("#jqxNotification").jqxNotification("open");
		        }
			}
		}
			
	});
});
$('#cancelFilterPlan').on('click', function() {
	loadDataForGrid('${salesForecastId}');
});

$(document).ready(function(){
//	var listWeek=[];
	loadDataForGrid('${salesForecastId}');
});
</script>
