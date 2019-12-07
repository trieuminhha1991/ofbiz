function anyOneChange() {
	$(window).bind('beforeunload', function(e) {
	    if (confirm) {
	        return "Are you sure?";
	    }
	});
}
var supplierChoiced = "", listCustomTimeId = [];
$(document).ready(function(){
	$.datepicker.setDefaults( $.datepicker.regional[ "en" ] );
	$('#madeOn').datepicker( {
        dateFormat: 'MM yy',
        showAnim: 'clip',
    });
	$("#dateChoice").css("visibility", "hidden");
	$("select[name='supplier']").change(function(){
		supplierChoiced  = $("select[name='supplier']").val();
		if (supplierChoiced != "-Select-") {
			$("#dateChoice").css("visibility", "visible");
		} else {
			$("#dateChoice").css("visibility", "hidden");
		}
	 });
	
	getYearInPlanHeaderAjax();
	bindMonthToJqw({});
	var lastcheck = -1;
	var bigCheck = 0;
	 $("#jqxYear").on('checkChange', function (event) {
	       if (event.args) {
	           var item = event.args.item;
	           if (item.checked) {
	        	   if (lastcheck == -1 || lastcheck == item.index - 1) {
	                   lastcheck = item.index;
	        	   } else {
	        		   $("#jqxYear").jqxDropDownList('uncheckItem', item);
	        	   }
	           }else {
	        	   var items = $("#jqxYear").jqxDropDownList('getCheckedItems');
	        	   if (items != "") {
	        		   for ( var x in items) {
		   	       			var thisId = items[x].index;
		   	       			if (thisId >= bigCheck) {
		   	       				bigCheck = thisId;
							}
	   	       			}
	        		   lastcheck = bigCheck;
	        	   } else {
	        		   lastcheck = -1;
	        	   }
			}
	       }
	   });
	 $('#jqxYear').on('close', function (event) { 
		 var items = $("#jqxYear").jqxDropDownList('getCheckedItems');
		 listId = [];
		 for ( var x in items) {
			var thisId = items[x].value;
			listId.push(thisId);
		}
		 getMonthInPlanHeaderAjax(listId);
	 });
	 var lastcheck2 = -1;
	 var bigCheck2 = 0;
	 var countCheck = 0;
	 $("#jqxMonth").on('checkChange', function (event) {
	       if (event.args) {
		    	   var items2 = $("#jqxMonth").jqxDropDownList('getCheckedItems');
		    	   var item = event.args.item;
			    	   if (items2.length < 4) {
						           if (item.checked) {
								        	   if (lastcheck2 == -1 || lastcheck2 == item.index - 1) {
								        		   var id = item.value;
								        		   getFromAndThruDate(
								        					{customTimePeriodId: id,}
								        					,"getFromAndThruDateAjax", "fromAndThruDate");
									               var thisMonth = item.label;
									               lastcheck2 = item.index;
								        	   } else {
								        		   $("#jqxMonth").jqxDropDownList('uncheckItem', item);
								        		   $.notify("Chỉ được chọn tháng liên tiếp!", "warn");
								        	   }
						           }else {
						        	   var items = $("#jqxMonth").jqxDropDownList('getCheckedItems');
								        	   if (items != "") {
									        		   for ( var x in items) {
										   	       			var thisId = items[x].index;
										   	       			if (thisId >= bigCheck) {
										   	       			bigCheck2 = thisId;
															}
									   	       			}
								        		   lastcheck2 = bigCheck2;
								        	   } else {
								        		   lastcheck2 = -1;
								        	   }
						           }
						} else {
							 $.notify("Chỉ được chọn 3 tháng!", "warn");
							 $("#jqxMonth").jqxDropDownList('uncheckItem', item);
						}
	       }
	   });
});
function getFromAndThruDate(jsonObject, url, data) {
	var fThr;
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	fThr = res[data];
        }
    }).done(function() {
    	getFromOrThru(fThr);
	});
}
var listDate = [];
function getFromOrThru(value) {
	var fromDate = value.fromDate;
	var strFromDate = new Date(fromDate);
	listDate.push(strFromDate);
	var thruDate = value.thruDate;
	var StrThruDate = new Date(thruDate);
	listDate.push(StrThruDate);
	compareDate();
}
var fromDateMin, thruDatemax;
function compareDate() {
	fromDateMin = listDate[0];
	thruDatemax = listDate[0];
	for ( var x in listDate) {
		var thisDate = listDate[x];
		if (thisDate < fromDateMin) {
			fromDateMin = thisDate;
		}
		if (thruDatemax < thisDate) {
			thruDatemax = thisDate;
		}
	}
//	fromDateMin = fromDateMin.toLocaleDateString();
//	thruDatemax = thruDatemax.toLocaleDateString();
	fromDateMin = convertDate(fromDateMin);
	thruDatemax = convertDate(thruDatemax);
}
function convertDate(date) {
	var dd = date.getDate();
	var mm = date.getMonth() + 1;
	var yyyy = date.getFullYear();
	if (dd < 10) {
		dd = '0' + dd;
	}
	if (mm < 10) {
		mm = '0' + mm;
	}
	var today = yyyy + '-' + mm + '-' + dd;
	return today;
}
function btnCreateClick() {
	 var items = $("#jqxMonth").jqxDropDownList('getCheckedItems');
	 listCustomTimeId = [];
	 for ( var x in items) {
		var thisId = items[x].value;
		listCustomTimeId.push(thisId);
	}
	console.log(listCustomTimeId);
	getlistProductInMonthsAjax(
			{customTimePeriodId: listCustomTimeId,
				supplierPartyId : supplierChoiced,}
			,"getPlanInThisMonthAjax", "listProductInMonths");
}
var currencyUomId = "", listProductInMonths = [];

function getlistProductInMonthsAjax(jsonObject, url, data, id) {
	var listProductInMonthTemp = [];
	var totalPriceAll = 0;
	var totalWeightAll = 0;
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	listProductInMonthTemp = res[data];
        	currencyUomId = res["currencyUomId"];
        	totalWeightAll = res["totalWeightAll"];
        	totalPriceAll = res["totalPriceAll"];
        }
    }).done(function() {
    	listProductInMonths = listProductInMonthTemp;
    	renderTable(totalWeightAll, totalPriceAll);
//    	console.log(listProductInMonths);
    	$("#totalWeightAll").val(totalWeightAll);
    	$("#totalPriceAll").val(totalPriceAll);
    	$("#currencyUomId").val(currencyUomId);
	});
}
function txtImportChange(countG) {
	var lastImport = $("#txtImport" + countG).val();
	var lastPriceW = $("#txtPrice" + countG).text();
	var total = lastImport*lastPriceW;
	$("#txtTotalPrice" + countG).text(total);
	setTimeout(function(){
		var lastTotals = 0;
		for (var int = 1; int <= count; int++) {
			var thisTotal = $("#txtTotalPrice" + int).text();
			lastTotals += parseFloat(thisTotal);
			$("#myTotal").text(lastTotals);
		}
//		storeProductToList();
	}, 1000);
}
function renderError(listError) {
	for ( var rx in listError) {
		var id = "totalWeight" + listError[rx].id;
		var mess = listError[rx].message;
		$("#" + id).css("background-color","rgb(163, 151, 151)");
		var timer;
		$("#" + id).hover(function() {
		    timer = setTimeout(function() {
		    	 $.notify( mess, "error");
		    }, 600);
		}, function() {
		    clearTimeout(timer);
		});
	}
}
function error(id, message) {
	this.id = id;
	this.message = message;
}
function getYearInPlanHeaderAjax() {
	getTimeYearInPlanHeaderAjax(
				{periodTypeId: "IMPORT_YEAR",}
				,"getYearInPlanHeaderAjax", "listYears");
}
function getMonthInPlanHeaderAjax(customTimePeriodId) {
	getTimeMonthInPlanHeaderAjax(
			{customTimePeriodId: customTimePeriodId,}
			,"getMonthInPlanHeaderAjax", "listMonths");
}
function getTimeYearInPlanHeaderAjax(jsonObject, url, data) {
	var listYear = [];
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	listYear = res[data];
        }
    }).done(function() {
    	bindYearToJqw(listYear);
	});
}
function bindYearToJqw(listYear) {
	 var source =
	   {
	       datatype: "local",
	       localdata: listYear,
	       datafields: [
	           { name: 'customTimePeriodId', type: 'string' },
	           { name: 'periodName', type:'string' }
	       ],
	       async: false
	   };
	   var dataAdapter = new $.jqx.dataAdapter(source);
	   $("#jqxYear").jqxDropDownList({ checkboxes: true, source: dataAdapter, displayMember: "periodName", valueMember: "customTimePeriodId", width: 200, height: 25});
}
var listMonths = [];
function getTimeMonthInPlanHeaderAjax(jsonObject, url, data) {
	var listMonthTemp = [];
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	listMonthTemp = res[data];
        }
    }).done(function() {
    	listMonths = listMonthTemp;
    	bindMonthToJqw(listMonths);
	});
}
function bindMonthToJqw(listMonths) {
	 var source =
	   {
	       datatype: "local",
	       localdata: listMonths,
	       datafields: [
	           { name: 'customTimePeriodId', type: 'string' },
	           { name: 'periodName', type:'string' }
	       ],
	       async: false
	   };
	   var dataAdapter = new $.jqx.dataAdapter(source);
	   $("#jqxMonth").jqxDropDownList({ checkboxes: true, source: dataAdapter, displayMember: "periodName", valueMember: "customTimePeriodId", width: 200, height: 25});
}
function btnCancelClick() {
	window.location.replace('AddQuotas');
}
function productInfo(internalName, productPackingUomId, quantityImport, lastPrice, totalWeight, weightUomId, totalPrice) {
	this.internalName = internalName;
	this.productPackingUomId = productPackingUomId;
	this.quantityImport = quantityImport;
	this.lastPrice = lastPrice;
	this.totalWeight = totalWeight;
	this.weightUomId = weightUomId;
	this.totalPrice = totalPrice;
}
var listProductInfor = [];
function storeProductToList() {
	var txtNamePlan = $("#txtNamePlan").val();
	if (txtNamePlan != "") {
		window.onbeforeunload = null;
		$("#myButton").html("<b>Saving...</b><img src='/delys/images/css/import/ajax-loader.gif'>");
		saveQuotaHeaderAjax(
				{quotaName: txtNamePlan,
				fromDate : fromDateMin,
				thruDate : thruDatemax,}
				,"saveQuotaHeaderAjax");
	} else {
		$('#txtNamePlan').focus();
	}
}

function saveQuotaHeaderAjax(jsonObject, url) {
	var quotaHeaderId ="";
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	quotaHeaderId = res["quotaId"];
        }
    }).done(function() {
    	saveItem(quotaHeaderId);
	});
}
function saveQuotaItemAjax(jsonObject, url) {
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	
        }
    }).done(function() {
    	
	});
}
function saveItem(quotaHeaderId) {
	var count2 = 0;
	for (var x = 0; x < count; x++) {
		count2  = parseInt(x) + 1;
		var internalNameS = listProductInMonths[x].primaryProductCategoryId + listProductInMonths[x].internalName;
		var productPackingUomIdS = listProductInMonths[x].productPackingUomId;
		var quantityImportS = $("#txtImport" + count2).val();
		var lastPriceS = $("#txtPrice" + count2).text();
		var totalWeightS = $("#totalWeight" + count2).text();
		var weightUomIdS = listProductInMonths[x].weightUomId;
		var totalPriceS = $("#txtTotalPrice" + count2).text();
		var newProductInfo = new productInfo(internalNameS, productPackingUomIdS, quantityImportS, lastPriceS, totalWeightS, weightUomIdS, totalPriceS);
		listProductInfor.push(newProductInfo);
		var iCmnD = "#productId" + count2;
		var productId = $(iCmnD).val();
		var quantityUomId = $("#quantityUomId" + count2).val();
		var productName = listProductInMonths[x].internalName;
		saveQuotaItemAjax(
				{quotaId: quotaHeaderId,
				productId : productId,
				productName : productName,
				quotaQuantity : quantityImportS,
				quantityUomId : quantityUomId,
				fromDate : fromDateMin,
				thruDate : thruDatemax,}
				,"saveQuotaItemAjax");
	}
	var param =  JSON.stringify(listProductInfor);
	$("#myList").val(param);
	newButton();
}
