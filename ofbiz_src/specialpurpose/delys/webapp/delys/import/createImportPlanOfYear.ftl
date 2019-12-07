<@useLocalizationNumberFunction />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript">
var toMonth;
$(document).ready(function() {
	var myMenu = "<li class='active' id='ntfIfHave'><a data-toggle='tab' href='#home'>${uiLabelMap.AddNewProduct}</a></li><li class='' onclick='navi()'><a data-toggle='tab' href='#profile'>${uiLabelMap.TotalProduct} <span class='badge badge-important' id = 'myCount'></span></a></li>";
// $("#myTab").html(myMenu);
	var today = new Date();
	toMonth = today.getMonth() + 1;
});
function navi() {
// window.location.href = 'resultPlanOfYear?productPlanHeaderId=' +
}

function bindProductAndCategoryAjax(jsonObject, url, data, key, value, id, index) {
	var dataResponse = [];
	jQuery.ajax({
		url : url,
		type : "POST",
		data : jsonObject,
		success : function(res) {
			dataResponse = res[data];
		}
	}).done(function() {
		renderByJqx(dataResponse, key, value, id, index);
		if (url == "getProductByCategory") {
			if (dataResponse[0] == undefined) {
				var productCategory = jsonObject['productCategoryId'];
				$("#ImportPlanHeader_productId").notify(productCategory + " ${StringUtil.wrapString(uiLabelMap.NotHaveProductInCategory)}.", "error");
			}
		}
	});
}

function renderTableHtml2(data, id, productIdN, uomToIdN){
	var myJSObject = {productPlanId: productPlanId,
						productId: productIdN,
						uomToId: uomToIdN};
	myJSObject = JSON.stringify(myJSObject);
	myJSObject = myJSObject.replace(/"/g, "'");
	var table = "";
	var productName = data[0][0].internalName;
	var paking = data[0][0].quantityUomId;
	quantityConvert = data[0][0].quantityConvertRS;
	table += "<br/><table>";
	table += "<tr>";
	table += "<td>${uiLabelMap.ProductPackedQty}: <a href='#' title='Edit config packing' onclick=\"insertConfigPacking(&quot;" + myJSObject + "&quot;,'" + quantityConvert + "',true);return false;\">" + quantityConvert + "</a></td>";
	table += "</tr></table><table class='table table-bordered dataTable' cellspacing='0'>" +
				"<thead><tr role='row' class='header-row'>" +
				"<th class='hidden-phone'>${uiLabelMap.Time}</td>" +
					"<th class='hidden-phone'>${uiLabelMap.SalesForecast}</th>" +
					"<th class='hidden-phone'>${uiLabelMap.ImportVolume}</th>" +
					"<th class='hidden-phone'>${uiLabelMap.Pallet}</th>" +
					"<th class='hidden-phone'>${uiLabelMap.ForecastSurvive}</th>" +
					"<th class='hidden-phone'>${uiLabelMap.InventoriesInMonth}</th>" +
					"<th class='hidden-phone' style='width:163px;'>${uiLabelMap.InventoriesOnDays}</th>" +
					"<th class='hidden-phone' style='width:160px;'>${uiLabelMap.Note}</th>" +
					"<th class='hidden-phone'><label><input id='checkAll' onclick='checkAllClick()' type='checkbox' ><span class='lbl'></span></label></th>" +
							"</tr></thead><tbody>";
	var listLoops = [];
	var turn = 0;
	var turnMonth = 0;
	var loops = turn;
	var hoanm;
	for ( var x in data) {		
		for ( var s in data[x]) {
			var thisMonth = data[x][s].thisMonth;
			var isUpdate = data[x][s].isUpdate;
			thisMonth = thisMonth.replace(/month:/g, '');
			if (thisMonth < toMonth) {
					isUpdate = "${uiLabelMap.StoredAndHaveInventory}";
					table += "<tr class='isStoredAndHaveInventory past' id='tr"+ x + "'><td><label id='lblMonth" + turnMonth + "'>${uiLabelMap.Month}" + thisMonth + "</label><br/><label style='display:none' id='lblProductPlanId" + turnMonth + "'>" + data[x][s].thisProductPlanId + "</label></td>";
			} else {
				if (isUpdate == "New") {
					isUpdate = "${uiLabelMap.New}";
					table += "<tr class='isNew' id='tr"+ x + "'><td><label id='lblMonth" + turnMonth + "'>${uiLabelMap.Month}" + thisMonth + "</label><br/><label style='display:none' id='lblProductPlanId" + turnMonth + "'>" + data[x][s].thisProductPlanId + "</label></td>";
				}
				if (isUpdate == "Stored") {
					isUpdate = "${uiLabelMap.Stored}";
					table += "<tr class='isStored' id='tr"+ x + "'><td><label id='lblMonth" + turnMonth + "'>${uiLabelMap.Month}" + thisMonth + "</label><br/><label style='display:none' id='lblProductPlanId" + turnMonth + "'>" + data[x][s].thisProductPlanId + "</label></td>";
				}
				if (isUpdate == "StoredAndHaveInventory") {
					isUpdate = "${uiLabelMap.StoredAndHaveInventory}";
					table += "<tr class='isStoredAndHaveInventory' id='tr"+ x + "'><td><label id='lblMonth" + turnMonth + "'>${uiLabelMap.Month}" + thisMonth + "</label><br/><label style='display:none' id='lblProductPlanId" + turnMonth + "'>" + data[x][s].thisProductPlanId + "</label></td>";
				}
				if (isUpdate == "NewAndHaveInventory") {
					isUpdate = "${uiLabelMap.NewAndHaveInventory}";
					table += "<tr class='isNewAndHaveInventory' id='tr"+ x + "'><td><label id='lblMonth" + turnMonth + "'>${uiLabelMap.Month}" + thisMonth + "</label><br/><label style='display:none' id='lblProductPlanId" + turnMonth + "'>" + data[x][s].thisProductPlanId + "</label></td>";
				}
			}
			
			turnMonth += 1;
			var check = true;
			for ( var v in data[x][s].thisSalesForecastDetails) {
					var thisQuantity = data[x][s].thisSalesForecastDetails[v].thisQuantity;
					var pallet = data[x][s].thisSalesForecastDetails[v].pallet;
					var importVolume = data[x][s].thisSalesForecastDetails[v].planImport;
					var tonCuoiThang = data[x][s].thisSalesForecastDetails[v].tonCuoiThang;
					var ngayTon = " " + data[x][s].thisSalesForecastDetails[v].ngayTon;
					var inventoryFocast = " " + data[x][s].thisSalesForecastDetails[v].inventoryFocast;
					hoanm = data[x][s].thisSalesForecastDetails[v].hoanm;
					var inventoryOfMonth = " " + data[x][s].thisSalesForecastDetails[v].inventoryOfMonth;
					inventoryOfMonth==undefined?inventoryOfMonth=0:inventoryOfMonth=inventoryOfMonth;
					ngayTon = ngayTon.substring(1,5);
					importVolume = numberFormat(importVolume);
					var thisQuantityShow = convertLocalNumber(thisQuantity);
					tonCuoiThang = convertLocalNumber(tonCuoiThang);
					inventoryOfMonth = convertLocalNumber(inventoryOfMonth);
					ngayTon = convertLocalNumber(ngayTon);
					var txtPallet = "txtPallet" + turn;
					table += "<td style='text-align: right;'><label id='txtThisQuantity" + turn + "'>" + thisQuantityShow + "</label><input type='hidden' id='txtInventoryFocast" + turn + "' value='" + inventoryFocast + "'/></td>" +
					"<td style='text-align: right;'><label id='txtImportVolume" + turn + "'>" + importVolume + "</label></td>" +
							"<td style='text-align: right;'><input type='text' class='inputNormal' onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' onkeyup='txtPalletChange("+ turn + ',' + thisQuantity +")' size='4' id='" + txtPallet + "' value='" + pallet + "'/></td>" +
							"<td style='text-align: right;'><label id='txtTonCuoiThang" + turn + "' >" + tonCuoiThang + "</label></td>" +
							"<td style='text-align: right;'><label  id='txtinventoryOfMonth" + turn + "' >" + inventoryOfMonth + "</label></td>" +
							"<td style='text-align: right;'><label id='txtNgayTon" + turn + "'>" + ngayTon + "</label></td>" +
							"<td><label  id='lblisUpdate" + turn + "'>" + isUpdate + "</label></td>" +
							"<td><label><input id='check" + turn + "' type='checkbox' onclick='anyOneUncheck()'><span class='lbl'></span></label></td></tr>";
					check = false;
					turn += 1;
					loops = turn;
			}
			if (check) {
				var txtPallet = "txtPalletException" + loops;
				table += "<td style='text-align: right;'><label id='txtThisQuantity" + loops + "' >0</label></td>" +
				"<td style='text-align: right;'><label id='txtImportVolume" + loops + "' >0</label></td>" +
				"<td style='text-align: right;'><input type='text' class='inputNormal' onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' onkeyup='txtPalletChange("+ loops + ',' + 0 +")' size='4' id='" + txtPallet + "' value='" + 0 + "'/></td>" +
				"<td style='text-align: right;'><label id='txtTonCuoiThang" + loops + "' >0</label></td>" +
				"<td style='text-align: right;'><label id='txtinventoryOfMonth" + loops + "' >" + 0 + "</label></td>" +
				"<td style='text-align: right;'><label id = 'txtNgayTon" + loops + "' maxlength='4' >0</label></td>" +
				"<td><label id='lblisUpdate" + loops + "'>" + '${uiLabelMap.SalesForcastNotAvailable}' + "</label></td>" +
				"<td><label><input type='checkbox' id='check" + loops + "' onclick='anyOneUncheck()'><span class='lbl'></span></label></td></tr>";
				listLoops.push(loops);
				loops += 1;
			}
		}
	}
	table += "</tbody></table>";
	$("#" + id).delay(1000).html(table);
	table = "<div style='padding-left: 94%;margin-top: 12px;'><button type='submit' class='btn btn-primary btn-small' id='btnAddProduct' onclick='btnAddProductClick()' name='submitButton'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button></div>";
	$("#myButton").delay(1200).html(table);
	for ( var s in listLoops) {
        $("#tr" + listLoops[s]).removeClass("isNew");
        $("#tr" + listLoops[s]).removeClass("isStored");
        $("#tr" + listLoops[s]).removeClass("isStoredAndHaveInventory");
        $("#tr" + listLoops[s]).removeClass("isNewAndHaveInventory");
        $("#tr" + listLoops[s]).addClass("hasNotValue");
 	}
	$('.past :input').attr('disabled', true);
}
function txtPalletChange(turn, thisQuantity) {
//	$("#btnAddProduct").css("visibility","hidden");
	$("#check" + turn).prop('checked', false);
	var pallet = $("#txtPallet" + turn).val();
	var iventoryTotal = 0;
	if (!pallet) {
		pallet = $("#txtPalletException" + turn).val();
		var importVolume = pallet*quantityConvert;
		importVolume = convertLocalNumber(importVolume);
		$("#txtImportVolume" + turn).text(importVolume);
		$("#check" + turn).prop('checked', true);
		return;
	}
	if(turn != 0){
		var oldturn =  parseInt(turn) - 1;
		var txtTonCuoiThang = $("#txtTonCuoiThang" + oldturn).text();
		txtTonCuoiThang = txtTonCuoiThang.replaceAll(",", "");
		txtTonCuoiThang = txtTonCuoiThang.replaceAll(".", "");
		
		var txtinventoryOfMonth = $("#txtinventoryOfMonth" + turn).text();
		txtinventoryOfMonth = txtinventoryOfMonth.replaceAll(",", "");
		txtinventoryOfMonth = txtinventoryOfMonth.replaceAll(".", "");
		if (txtinventoryOfMonth != 0) {
			txtTonCuoiThang = 0;
		}
		iventoryTotal = parseInt(txtTonCuoiThang)  + parseInt(txtinventoryOfMonth);
	}else {
		var txtinventoryOfMonth = $("#txtinventoryOfMonth" + turn).text();
		txtinventoryOfMonth = txtinventoryOfMonth.replaceAll(",", "");
		txtinventoryOfMonth = txtinventoryOfMonth.replaceAll(".", "");
		iventoryTotal = parseInt(txtinventoryOfMonth);
	}
	var importVolume = pallet*quantityConvert;
	var tonCuoiThang = importVolume - thisQuantity + iventoryTotal;
	var ngayTon = " " + (tonCuoiThang / (thisQuantity/30));
	if(ngayTon < 7){
		$("#txtNgayTon" + turn).notify("${StringUtil.wrapString(uiLabelMap.DayLessThan7)}", { className: 'warn',autoHideDelay: 1300});
	}
	if(ngayTon > 10){
		$("#txtNgayTon" + turn).notify("${StringUtil.wrapString(uiLabelMap.DayMoreThan10)}", { className: 'warn',autoHideDelay: 1300});
	}
	if(ngayTon >= 7 && ngayTon <= 10){
		ngayTon = ngayTon.substring(1,5);
		$("#txtNgayTon" + turn).notify(ngayTon + "days", { className: 'success'});
	}
	ngayTon = ngayTon.substring(0,6);
	if (tonCuoiThang > 0) {
		$("#btnAddProduct").css("visibility","visible");
		$("#check" + turn).prop('checked', true);
		$("#check" + turn).prop("disabled", false);
		var x = "${StringUtil.wrapString(uiLabelMap.HaveChanged)}";
		$("#lblisUpdate" + turn).text(x);
	}else {
		$("#lblisUpdate" + turn).text("Not Valid!");
		$("#check" + turn).prop("disabled", true);
	}
	importVolume = convertLocalNumber(importVolume);
	tonCuoiThang = convertLocalNumber(tonCuoiThang);
	ngayTon = convertLocalNumber(ngayTon);
	$("#txtImportVolume" + turn).text(importVolume);
	$("#txtTonCuoiThang" + turn).text(tonCuoiThang);
	$("#txtNgayTon" + turn).text(ngayTon);
	var hasValue = true;
	var forcus = turn + 1;
	if ($("#txtPallet" + forcus).val() != "") {
		$("#txtPallet" + forcus).trigger("onkeyup");
	}
}
function renderTotalTable(listPlanAvailable) {
	var thisListProduct = [];
	var tableTotal = "";
	var tableTotal2 = "";
	var productAvailable = [];
	var productIdAvailable = [];
	var tableTotal3 = "";
	var listMonth = listPlanAvailable.listMonth;
	var listProductCategory = listPlanAvailable.listProductCategory;
	var listProducts = listPlanAvailable.listProducts2;
	tableTotal += "<table class='table-bordered table table-striped table-hover dataTable' cellspacing='0'><thead><tr role='row' class='header-row'><th rowspan='2' class='hidden-phone'>${uiLabelMap.Time}</th>";
	var count = 0;
	for ( var p in listProductCategory) {
		var tableTotal23 = "";
		var tableTotal42 = "";
		var thisCategory = listProductCategory[p].catagory;
		thisListProduct = listProductCategory[p].listProductInCatagorys;
		var length = thisListProduct.length;
		tableTotal += "<th colspan='" + length*2 + "' class='hidden-phone'>" + thisCategory + "</th>";
		for ( var fe in thisListProduct) {
			var internalName = thisListProduct[fe].internalName;
			productAvailable.push(internalName);
			var productPlanIdS = thisListProduct[fe].productPlanIdS;
			var productIdS = thisListProduct[fe].productIdS;
			productIdAvailable.push(productIdS);
			var quantityUomIdS = thisListProduct[fe].quantityUomIdS;
			tableTotal23 += "<th colspan='2' class='hidden-phone' >" + internalName + "<i class='red icon-edit bigger-210' onclick='bindTableAddProduct(\"" + productPlanIdS + "\",\"" + productIdS + "\",\"" + quantityUomIdS + "\")'></i></th>";
		}
		tableTotal2 += tableTotal23;
	}
	var thisLists = [];
	for ( var e in listMonth) {
		var thisMonth = listMonth[e].thisMonth;
		var thisId = listMonth[e].thisPlanId;
		thisLists = listProducts[e];
		var totalPallet = 0;
		var totalCont = 0;
		var test = "<tr class='myRow'><td><a style='cursor: pointer; text-decoration: none;' href='resultDevideTime?productPlanHeader="+thisId+"'>" + thisMonth + "</a></td>";
		var j = 0;
		for ( var sd in productIdAvailable) {
			var myId = productIdAvailable[sd];
			var yourId = thisLists[j];
			if (yourId != null) {
				var yourId2 = thisLists[j].productId;
				if (myId == yourId2) {
						var thisQuantity = thisLists[j].planQuantity;
						var quantityConvert = thisLists[j].quantityConvert;
						var pallet = thisQuantity/quantityConvert;
						totalPallet += pallet;
						totalCont = " " + totalPallet/33;
						totalCont = totalCont.substring(1,5);
						if (thisQuantity == 0) {
							test += "<td><label>" + thisQuantity + "</label></td>";
							test += "<td><label>" + pallet + "</label></td>";
						} else {
							test += "<td><input type='text' onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' value='" + thisQuantity + "'/></td>";
							test += "<td><input type='text' onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' value='" + pallet + "'/></td>";
						}
						j += 1;
				}else {
					test += "<td></td>";
					test += "<td></td>";
				}
			} else {
				test += "<td></td>";
				test += "<td></td>";
			}
		}
		test += "<td><label>" + totalPallet + "</label></td>";
		test += "<td><label>" + totalCont + "</label></td>";
		test += "</tr>";
		tableTotal3 += test;
	}
	tableTotal += "<th rowspan='2' class='hidden-phone'>${uiLabelMap.TotalPallet}</th><th rowspan='2' class='hidden-phone'>${uiLabelMap.TotalContainer}</th></tr>";
	tableTotal += "<tr>";
	tableTotal += tableTotal2;
	tableTotal += "</tr></thead><tbody>";
	tableTotal += tableTotal3;
	tableTotal += "</tbody></table>";
	$("#myTotalTable").html(tableTotal);
	bindAvailibleProduct(productAvailable);
}
function insertConfigPacking(jsonObject, quantityConvert, isUpdate) {
	var alert = "",height = 0,configPackingInfo;
	if (isUpdate){
		height = 210;
		jsonObject = jsonObject.replace(/'/g, '"');
		jsonObject = JSON.parse(jsonObject);
		configPackingInfo = getConfigPackingAjax(jsonObject);
		
	}else {
		height = 270;
		alert = "<p style='color:red'>${uiLabelMap.quantityConvertInvalid}</p>";
		alert += "<p style='color:red'>${uiLabelMap.insertRequired}</p>";
	}
	var productId = jsonObject.productId;
	var wd = "";
	wd += "<div id='window01'><div>${uiLabelMap.CommonEdit}" + " ${uiLabelMap.ProductCapacity}".toLowerCase() +"</div><div>";
	wd += alert;
	wd += "<div class='row-fluid'>" +
				"<div class='span12 no-left-margin'>" +
					"<div class='span4' style='text-align: right;'>${uiLabelMap.accProductId}:</div>" +
					"<div class='span8'><div id='productId1' >" + productId + "</div></div>";
				"</div>";
	wd += "<div class='row-fluid'>" +
				"<div class='span12 no-left-margin'>" +
					"<div class='span4' style='text-align: right;'>${uiLabelMap.StorageCapacity}<span style='color:red;'> *</span></div>" +
					"<div class='span8'><input type='text' id='quantityConvert1' name='quantityConvert1' onkeydown='if (event.keyCode == 13) enterPress()'/></div>" +
				"</div>";
	wd += "<div class='row-fluid'>" +
			"<div class='span12 no-left-margin'>" +
				"<div class='span4' style='text-align: right;'>${uiLabelMap.AvailableFromDate}<span style='color:red;'> *</span></div>" +
				"<div class='span8'><div id='availableFromDate1'></div></div>";
			"</div>";
	wd += "<div class='row-fluid'>" +
			"<div class='span12 no-left-margin'>" +
				"<div class='span4' style='text-align: right;'>${uiLabelMap.AvailableThruDate}</div>" +
				"<div class='span8'><div id='expireDate1'></div></div>" +
			"</div>";
	wd += "<div class='row-fluid'>" +
			"<div class='span12 no-left-margin'>" +
				"<div class='span4'></div>" +
				"<div class='span8'><button id='alterSave'><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button><button id='alterCancel'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button></div>" +
			"</div>";
	wd += "</div></div>";
	$("#myImage").html(wd);
	$('#window01').jqxWindow({ height: height, width: 450, isModal: true, modalOpacity: 0.7, theme:'olbius' });
	if (isUpdate) {
		configPackingInfo.fromDate == undefined?configPackingInfo.fromDate = null : configPackingInfo.fromDate = configPackingInfo.fromDate['time'];
		configPackingInfo.thruDate == undefined?configPackingInfo.thruDate = null : configPackingInfo.thruDate = configPackingInfo.thruDate['time'];
		$("#availableFromDate1").jqxDateTimeInput({ value: new Date(configPackingInfo.fromDate), width: 220 });
		if (configPackingInfo.thruDate == null) {
			$("#expireDate1").jqxDateTimeInput({width: 220});
			$("#expireDate1").jqxDateTimeInput( "val", "");
		} else {
			$("#expireDate1").jqxDateTimeInput({ value : new Date(configPackingInfo.thruDate) });
		}
	}else {
		$("#availableFromDate1").jqxDateTimeInput({width: 220});
		$("#expireDate1").jqxDateTimeInput({width: 220});
		$("#expireDate1").jqxDateTimeInput( "val", "");
	}
	 $("#alterCancel").jqxButton();
     $("#alterSave").jqxButton();
     $("input[name='quantityConvert1']").change(function() {
    	 
     });
     $('#window01').mouseover(function() {
// $("#alterSave").focus();
     });
     $("#quantityConvert1").val(quantityConvert);
     var checkFromDate = $('#availableFromDate1').val();
     var dateFRM = checkFromDate.split('/');
     checkFromDate = new Date(dateFRM[2], dateFRM[1] - 1, dateFRM[0], 0, 0, 0, 0);
     $('#availableFromDate1').on('close', function (event)
     		{
     		    var jsDate = event.args.date;
     		    checkFromDate = jsDate;
     		});
     $('#expireDate1').on('close', function (event)
     		{
     		    var jsDate = event.args.date;
     		    if (checkFromDate < jsDate) {
					} else {
						 $('#expireDate1').val('');
						 $('#expireDate1').notify( "Expire Date Invalid!", "error");
					}
     });
     $("#alterSave").click(function () {
    	 var tempFrDate = $('#availableFromDate1').val();
    	 var frDate = toTimeStamp(tempFrDate);
	     var tempThrDate = $('#expireDate1').val();
	     var thrDate = toTimeStamp(tempThrDate);
	     var quantityConvert= $("#quantityConvert1").val();
	     if (quantityConvert == 0 || quantityConvert == "") {
	    	 $("#quantityConvert1").focus();
		}else {
			insertConfigPackingAjax({
					productId: productId,
					uomFromId: "PALLET",
					uomToId: jsonObject.uomToId,
					quantityConvert: $("#quantityConvert1").val(),
					fromDate: frDate,
					thruDate: thrDate,
 			},"updateProductCapacity", jsonObject);
			$('#window01').jqxWindow('destroy');	
		}
     });
     $("#alterCancel").click(function () {
    	 destroyAll();
     });
     $('#window01').on('close', function (event) {
    	 destroyAll();
     });
}
function getConfigPackingAjax(jsonObject) {
	var configPackingInfo;
	jQuery.ajax({
        url: "getConfigPackingAjax",
        type: "POST",
        data: jsonObject,
        async: false,
        success : function(res) {
        	configPackingInfo = res["configPackingInfo"];
		}
    });
	return configPackingInfo;
}
function enterPress() {
	$("#alterSave").click();
}
function toTimeStamp(date) {
	if (date == "") {
		return "";
	} else {
		var splDate = date.split('/');
		var timeStamp = splDate[2] + '-' + splDate[1] + '-' + splDate[0];
		return timeStamp;
	}
}
function destroyAll() {
	 $('#window01').jqxWindow('destroy');
}
function numberFormat(value) {
	value = String(value);
	var length = value.length;
	var thousands;
	if (locale == "vi") {
		thousands = ".";
	} else {
		thousands = ",";
	}
	var newValue = "";
	if (length < 3) {
		return value;
	}
	value = reverseString(value);
	for (var i = 0; i < length; i++) {
		var thisChar = value.charAt(i);
		var unit = i%3;
		if (i!= 0 && unit == 0) {
			newValue += thousands;
		}
		newValue += thisChar;
	}
	return reverseString(newValue);
}
function reverseString(value) {
	value = String(value);
	var length = value.length;
	var newValue = "";
	for (var i1 = length; i1 >= 0; i1--) {
		var thisChar = value.charAt(i1);
		newValue += thisChar;
	}
	return newValue;
}
function insertConfigPackingAjax(jsonObject, url, myJSON) {
	var checkSave;
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success : function(res) {
        	var message = "<div id='contentMessages' class='alert alert-success' onclick='hiddenClick()'>" +
			"<p id='thisP'>" + '${uiLabelMap.DAUpdateSuccessful}' + "</p></div>";
	    	$("#myAlert").html(message);
		}
    }).done(function() {
    	getMonthSalesForcastAjax(
    			myJSON
				,"getAllMonthSalesForcast", "month");
	});
}
function createPlanItemToDatabaseAjax(jsonObject, url, oneTurn) {
	var checkSave;
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success : function(res) {
        	checkSave = res["mess"];
		}
    }).done(function() {
    	var message = "";
    	if (checkSave != "") {
    		if (oneTurn) {
    			message = "<div id='contentMessages' class='alert alert-success' onclick='hiddenClick()'>" +
    			"<p id='thisP'>" + '${uiLabelMap.DAUpdateSuccessful}' + "</p></div>";
    	    	$("#myAlert").html(message);
			}
		} else {
			if (oneTurn) {
    			message = "<div id='contentMessages' class='alert alert-error' onclick='hiddenClick()'>" +
    			"<p id='thisP'>" + '${uiLabelMap.DAUpdateError}' + "</p></div>";
    	    	$("#myAlert").html(message);
			}
		}
    	
	});
}
var locale;
$(document).ready(function () {
	locale = '${locale}';
});
</script>
<style>
.colColor {
	background-color: #555555 !important;
}
.inputSmall {
	width: 50px !important;
}
.inputNormal {
	width: 70px !important;
	text-align: right;
}
#myHistoryTable {
	visibility: hidden;
}
.ced {
	color:#CCCCCC;
}
.hasNotValue {
	background-color: #d9edf7 !important;
}
.isNew {
	background-color: #dff0d8 !important;
}
.isStored {
	background-color: #dff0d8 !important;
}
.isStoredAndHaveInventory {
	background-color: #fcf8e3 !important;
}
.isNewAndHaveInventory {
	background-color: #fcf8e3 !important;
}
.table thead tr th [class*="icon-"]:first-child {
	  float: right;
	}
#jqxProgressBar {
	position: fixed;
	top: 300px;
	right: 550px;
	display: none;
}
#myTable {
  -webkit-user-select: none;
     -moz-user-select: -moz-none;
      -ms-user-select: none;
          user-select: none;
}
#myTable {
	width: 100% !important;
	overflow-x: auto;
}
#myImage {
	visibility: hidden;
}
</style>

<div id="myImage"></div>

<div class="row-fluid">
	<div class="span12">
		<div class="span4">
			<form class="form-horizontal form-third-column">
				<div class="control-group no-left-margin ">
					<label class="control-label">
						<label for="Category">${uiLabelMap.Category}:</label>
					</label>
					<div class="controls">
						<div id="ImportPlanHeader_productCategoryId" ></div>
					</div>
				</div>
			</form>
		</div>
		<div class="span4">
			<form class="form-horizontal form-third-column">
				<div class="control-group no-left-margin ">
					<label class="control-label">
						<label for="Product">${uiLabelMap.Product}: </label>
					</label>
					<div class="controls">
						<div id="ImportPlanHeader_productId" ></div>
					</div>
				</div>
			</form>
		</div>
		<div class="span4">
			<form class="form-horizontal form-third-column">
				<div class="control-group no-left-margin ">
						<label class="control-label">
							<label for="Packing">${uiLabelMap.Packing}: </label>
							</label>
						<div class="controls">
							<div id="ImportPlanHeader_productUom" ></div>
						</div>
				</div>
			</form>
		</div>
	</div>
</div>
<div id="myTable"></div>
<div id ='jqxProgressBar'></div>
<input type="hidden" id="thisPlanId" value = "${productPlanId}"/>
<div id="myButton"></div>
