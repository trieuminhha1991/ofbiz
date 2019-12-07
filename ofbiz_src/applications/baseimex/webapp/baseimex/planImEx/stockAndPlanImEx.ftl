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
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>

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

<div id='Menu'>
	<ul>
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
<div id="jqxNotification"> 
</div>
<div id="container" class="">
</div>

<div class="row-fluid">
	<div class="row-fluid span6" style="margin-top:5px;">
		<H4><a>${uiLabelMap.BIEUpdatePlan}</a></H4>
	</div>
	<div class="row-fluid span6" style="margin-top:5px;">
		<div class="span6"></div>
		<div class="span6" style="text-align: right; margin: inherit;cursor: pointer;">
			<div class="span3">
				<a id="saveSFC"><i class="icon-save"></i>${uiLabelMap.CommonSave}</a>
			</div>
			<div class="span3"><a id="filterPlan" class="icon-filter open-sans">${uiLabelMap.DmsFilter}</a></div>
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
<div id="jqxStockAndPlan"></div>

<script type="text/javascript">
//var local
	var uiLabelMap = {};
	var gridProduct = $("#jqxStockAndPlan");
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
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
$("#jqxStockAndPlan").on('contextmenu', function () {
    return false;
});
$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: false, template: "error" });
//END MENU

function loadDataForGrid(productPlanId){
	$.ajax({
		beforeSend: function(){
			$("#loader_page_common").show();
	    },
	    complete: function(){
	    	$("#loader_page_common").hide();
	    },
		url: "JqxGetStockAndPlan",
		type: "POST",
		data: {productPlanId: productPlanId},
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
		        	var listIterator = data.listIterator;
					listWeekHeader = data.listWeekHeader;
					periodTypeId = data.periodTypeId;
					loadProductDataSumToJqx(listIterator, listWeekHeader, periodTypeId);
		        }
			}
		}
	});
}

//load jqxGrid
function loadProductDataSumToJqx(listIterator, listWeekHeader, periodTypeId){
	var obj = initDataFields(listWeekHeader, periodTypeId);
	var sourceProduct =
	    {
	        datafields: obj.listHeader,
	        localdata: listIterator,
	        datatype: "array",
	    };
	    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
	    $("#jqxStockAndPlan").jqxGrid({
	        source: dataAdapterProduct,
	        localization: getLocalization(),
	        filterable: true,
	        showfilterrow: true,
	        theme: 'olbius',
	        rowsheight: 30,
	        width: '100%',
	        autoheight: true,
	        columnsresize: true,
	        showaggregates: true,
	        showstatusbar: true,
	        statusbarheight: 70,
	        editmode: 'click',
	        enabletooltips: true,
	        autoheight: true,
	        selectionmode: 'singlecell',
	        pageable: true,
	        pagesize: 12,
	        pagesizeoptions: ['12', '24', '36'],
	        editable: true,
	//        selectionmode: 'checkbox',
		    columns: obj.listColumns
	    });
}

function initDataFields(listWeekHeader, periodTypeId){
	var arrHeaders = [];
		var arrColumns = [];
		var h1 = {name: 'productId', type: 'string'};
		var h5 = {name: 'productCode', type: 'string'};
		var h2 = {name: 'productName', type: 'string'};
//		var h3 = {name: 'MOQ', type: 'number'};
		var h4 = {name: 'SalesCycle', type: 'string'};
		var cellclassname = function (row, column, value, data) {
		    if((row+1) % 6 == 0){
		    	return 'yellow1';
		    }
		};
		var c5 = {text: '${StringUtil.wrapString(uiLabelMap.ProductId)}', datafield: 'productCode', editable: false,filterable: true, pinned: true, width: 110,
				cellclassname: cellclassname
		};
		var c2 = {text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', minwidth: 150, editable: false, filterable: true, pinned: true,
				cellclassname: cellclassname
		};
//		var c3 = {text: '${StringUtil.wrapString(uiLabelMap.MOQ)}', datafield: 'MOQ', editable: false, filterable: true, width: '8%', pinned: true, hidden: false,
//				cellsrenderer: function(row, column, value){
//					var data = gridProduct.jqxGrid('getrowdata', row);
//					return '<span id=\"'+id+'\" style="text-align: right; font-size: 14px; color: blue;" >' + formatnumber(value) +'</span>';
//				},
//				cellclassname: cellclassname
//		};
		var c4 = {text: '${StringUtil.wrapString(uiLabelMap.BIESalesCycle)}', datafield: 'SalesCycle', editable: false, width: 150, filterable: true, pinned: true,
			cellclassname: cellclassname,
			cellsrenderer: function(row, column, value){
	        	if((row+1) % 6 == 0){
						return '<span style="text-align: right; color: black;"><b>' + value +'</b></span>';
	        	}
			},
			aggregates: [{['${StringUtil.wrapString(uiLabelMap.BIEPalletTotal)}']:
				function (aggregatedValue, currentValue, column, record) {
					return aggregatedValue;
	            }
			}],
			aggregatesrenderer: function (aggregates) {
		        var renderstring = "";
		        $.each(aggregates, function (key, value) {
		        	if(key == '${StringUtil.wrapString(uiLabelMap.BIEPalletTotal)}'){
		        		renderstring += "<span style='font-size: 14px;'><b>" + '${StringUtil.wrapString(uiLabelMap.BIEPalletTotal)}'+"</b></span><hr style='margin: 2px !important;'/>" +
		        				"<span style='font-size: 14px; color: green;'><b>" + '${StringUtil.wrapString(uiLabelMap.BIENumContainer)}' +"</b></span><hr style='margin: 2px !important;'/>" +
		        				"<span style='font-size: 14px; color: orange;'><b>" + '${StringUtil.wrapString(uiLabelMap.BIERemain)}' + " (Pallet)</b></span>";
		        	}
		        });
		        return renderstring;
		    },
		};
		arrHeaders.push(h1);
		arrHeaders.push(h5);
		arrHeaders.push(h2);
//		arrHeaders.push(h3);
		arrHeaders.push(h4);
		arrColumns.push(c5);
		arrColumns.push(c2);
//		arrColumns.push(c3);
		arrColumns.push(c4);
		var cellbeginedit = function (row, datafield, columntype, value) {
	        if ((row+1)%6 != 4) return false;
	        else{
	        	var dateColumn = '';
					for(var i=0; i<listWeekHeader.length; i++){
						if(listWeekHeader[i].customTimePeriodId == datafield){
							dateColumn = listWeekHeader[i].periodName;
							break;
						}
					}
	        	var arr = dateColumn.split('-');
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
	        		$("#"+row +''+datafield).jqxTooltip({ disabled: false });
	        		$("#"+row +''+datafield).jqxTooltip({ content: '<span style="color: red;">' +'${StringUtil.wrapString(uiLabelMap.DmsRestrictEditWeek)}' + '</span>', theme: 'customtooltip', position: 'top', name: 'movieTooltip'});
			        	$("#"+row +''+datafield).jqxTooltip('open');
			        	$("#"+row +''+datafield).bind('close', function () {
			        		$("#"+row +''+datafield).jqxTooltip({ disabled: true });
			        	}); 
	        		return false;
	        	}
	        }
	    }
		
		for(var i=0; i<listWeekHeader.length; i++){
			var df = listWeekHeader[i].customTimePeriodId;
			var header = {name: listWeekHeader[i].customTimePeriodId, type: 'number'};
			var column = {text: listWeekHeader[i].periodName, datafield: listWeekHeader[i].customTimePeriodId, editable: true, filterable: false, columntype: 'numberinput', width: 80, cellsalign: 'right',
					createeditor: function (row, cellvalue, editor) {
		                editor.jqxNumberInput({inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0 });
		            },
		            cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
	  			        if(newvalue <0) return false;
		            	if((row+1)%6 ==4){
			            	if (newvalue != oldvalue){
			            		if(!oldvalue){
			            			oldvalue=0;
			            		}
		  			        	var cell = gridProduct.jqxGrid('getcellvalue', row+1, datafield);
		  			        	var cellMOQValue = gridProduct.jqxGrid('getcellvalue', row, "MOQ");
		  			        	var newCell = parseFloat(cell) + (parseFloat(newvalue) - parseFloat(oldvalue));
		  			        	gridProduct.jqxGrid('setcellvalue', row+1, datafield, newCell);
		  			        	
		  			        	var cellOpen = newCell;
		  			        	var m = i;
		  			        	for(var k=0; k<listWeekHeader.length; k++){
		  							if(listWeekHeader[k].customTimePeriodId == datafield){
		  								m = k;
		  								break;
		  							}
		  						}
		  			        	
		  			        	for(var j=k+1; j<listWeekHeader.length; j++){
		  			        		var cellOpenNextOld = gridProduct.jqxGrid('getcellvalue', row-3, listWeekHeader[j].customTimePeriodId);
		  			        		gridProduct.jqxGrid('setcellvalue', row-3, listWeekHeader[j].customTimePeriodId, cellOpen);
		  			        		var cellEnding = gridProduct.jqxGrid('getcellvalue', row+1, listWeekHeader[j].customTimePeriodId);
		  			        		var cellEndingNext = parseFloat(cellOpen) - parseFloat(cellOpenNextOld) + parseFloat(cellEnding);
		  			        		gridProduct.jqxGrid('setcellvalue', row+1, listWeekHeader[j].customTimePeriodId, cellEndingNext);
		  			        		
		  			        		cellOpen = cellEndingNext;
		  			        	}
		  			        	
		  			        }
	  			        }
	  			    },
		            cellbeginedit: cellbeginedit,
		            cellsrenderer: function(row, column, value, defaulthtml, columnproperties){
		            	var id = row +''+column;
		            	if((row+1) % 6 == 1 || (row+1) % 6 == 2){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
		            	}if((row+1)%6==3){
		            		return '<span class="align-right">' + formatnumber(value) +'</span>';
		            	}
		            	if((row+1) % 6 == 0){
			            		var cellEnd = gridProduct.jqxGrid('getcellvalue', row-1, column); // ton cuoi ky cua chu ky hien tai
			            		var vl = 0;
			            		var m = 0;
			            		var vl1 = 0;
			            		for(var j=0; j<listWeekHeader.length; j++){
			            			if(listWeekHeader[j].customTimePeriodId == column){
			            				m = j;
			            				break;
			            			}
			            		}
			            		if(listWeekHeader[m+1]) vl1 = gridProduct.jqxGrid('getcellvalue', row-4, listWeekHeader[m+1].customTimePeriodId);
			            		var daysNum = 1;
			            		if ("COMMERCIAL_WEEK" == periodTypeId) {
			            			daysNum = 7;
			            		} else if ("COMMERCIAL_MONTH" == periodTypeId) {
			            			daysNum = 30;
			            		} else if ("COMMERCIAL_QUARTER" == periodTypeId) {
			            			daysNum = 90;
			            		} else if ("COMMERCIAL_YEAR" == periodTypeId) {
			            			daysNum = 365;
			            		} else {
			            			daysNum = 1;
			            		}
			            		var avg = parseFloat(vl1)/daysNum; // sales forcasts chu ky tiep theo chia trung binh theo ngay
			            		if(avg > 0){
			            			vl = parseFloat(cellEnd)/avg;
			            		}
			            		if(vl > 0){
			            			vl = Math.round(vl);
			            			if(vl > (daysNum*2)) return '<span style="text-align: right; color: red; font-size: 14px;"><b>' + formatnumber(vl) +'</b></span>';
			            			else if(vl < daysNum) return '<span style="text-align: right; color: red; font-size: 14px;"><b>' + formatnumber(vl) +'</b></span>';
			            			else return '<span style="text-align: right; color: green; font-size: 14px;"><b>' + formatnumber(vl) +'</b></span>';
			            		} else return '<span style="text-align: right; color: #black; font-size: 14px;"><b>-</b></span>';
		            	}else if((row+1) % 6 == 4){
		            		var cellValue = gridProduct.jqxGrid('getcellvalue', row, "MOQ");
		            		var titleValue = parseFloat(cellValue)*parseFloat(value);
	  						return '<span style="text-align: right; font-size: 14px;" class="background-prepare">' + formatnumber(value) +'</span>';
		            	}else return '<span class="align-right" >' + formatnumber(value) +'</span>';
	  				},
	  				aggregates: [{['${StringUtil.wrapString(uiLabelMap.BIEPalletTotal)}']:
	  					function (aggregatedValue, currentValue, column, record) {
	  						if(record.SalesCycle == '${StringUtil.wrapString(uiLabelMap.BIEOrderQuantity)}'){
	  							aggregatedValue += currentValue;
	  						}
	  						return aggregatedValue;
	  		            }
	  				}
	  				],
	  				aggregatesrenderer: function (aggregates) {
	  			        var renderstring = "";
	  			        $.each(aggregates, function (key, value) {
	  			        	if(key == '${StringUtil.wrapString(uiLabelMap.BIEPalletTotal)}'){
	  			        		var container = Math.floor(parseFloat(value)/33);
	  			        		var remain = parseFloat(value)%33;
	  			        		renderstring += "<span style='margin-right: 10px; font-size: 14px;'><b>"+formatnumber(value)+"</b></span><hr style='margin: 2px !important;'/>" +
	  			        				"<span style='margin-right: 10px; font-size: 14px; color: green'><b>"+formatnumber(container)+"</b></span><hr style='margin: 2px !important;'/>" +
	  			        				"<span style='margin-right: 10px; font-size: 14px; color: orange'><b>"+formatnumber(remain)+"</b></span>";
	  			        	}
	  			        });
	  			        return renderstring;
	  			    },
	  			    cellclassname: function (row, column, value, data) {
					    if((row+1) % 6 == 0){
					    	return 'green1';
					    }
		  			  if((row+1)%6==4){
					    	return 'bluewhite';
					    }
					}
			};
			arrHeaders.push(header);
			arrColumns.push(column);
		}
		var arrRe = {listHeader: arrHeaders, listColumns: arrColumns};
		return arrRe;
}

$('#filterCancel').on('click', function(){
	$('#wdFilter').jqxWindow('close');
});

$('#filterPlan').on('click', function(){
	$('#wdFilter').jqxWindow('open');
});

$('#saveSFC').on('click', function(){
	
	var sfcItems = $('#jqxStockAndPlan').jqxGrid('getboundrows');
	var dataPlans = [];
	var dataEnds = [];
	for(var i=0; i<= sfcItems.length; i++){
		if((i+1)%6 == 4){
			dataPlans.push(sfcItems[i]);
		}
		if((i+1)%6 == 5){
			dataEnds.push(sfcItems[i]);
		}
	}
	$.ajax({
		beforeSend: function(){
			$("#loader_page_common_save").show();
	    },
	    complete: function(){
	    	$("#loader_page_common_save").hide();
	    	window.location.href = "listImExPlanItem?productPlanId=${productPlanId}";
	    },
		url: "storePlanAndStock",
		type: "POST",
		data: {dataPlans: JSON.stringify(dataPlans), dataEnds: JSON.stringify(dataEnds), productPlanId: '${productPlanId}'},
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
		url: "JqxGetStockAndPlan",
		type: "POST",
		data: {fromMonth: fromMonth, toMonth: toMonth, productPlanId: '${productPlanId}', checkedMonth: checkedMonth, checkedWeek: checkedWeek},
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
		        	var listIterator = data.listIterator;
					listWeekHeader = data.listWeekHeader;
					loadProductDataSumToJqx(listIterator, listWeekHeader);
		        }
			}
		}
	});
});
$('#cancelFilterPlan').on('click', function() {
	loadDataForGrid('${productPlanId}');
});

$(document).ready(function(){
	loadDataForGrid('${productPlanId}');
});
</script>