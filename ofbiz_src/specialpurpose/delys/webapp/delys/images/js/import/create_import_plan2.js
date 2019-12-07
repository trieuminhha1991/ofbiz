var listYears = [];
var listMonths = [];
var listTempMonth = [];
var loops = 0;

function getMonth(id) {
	getTimeMonth(
				{customTimePeriodId: id,	}
				,"getMonth", "listMonths");
}
function getTimeMonth(jsonObject, url, data) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	listTempMonth = [];
        	listTempMonth = res[data];
        }
    }).done(function() {
    	listMonths = listMonths.concat(listTempMonth);    	
	});
}
function getYear() {	
	getTimeYear(
				{periodTypeId: "SALES_YEAR",}
				,"getYear", "listYears");
}
var quantityConvert;
function getQuantityConvert() {
	var productId = $("select[name='productId']").val();
	getThisQuantityConvert(
			{productId: productId,	}
			,"getThisquantityConvert", "quantityConvert");
}
function getThisQuantityConvert(jsonObject, url, data) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	quantityConvert = res[data];
        }
    }).done(function(){
    	renderTableHtml2(listMonth, "myTable");
    });
}
var table = "";
function renderTableHtml2(data, id){
	table = "";
	var productName = $("#ImportPlanHeader_productId option:selected").text();
	var productId = $("#ImportPlanHeader_productId option:selected").val();
	var paking = $("#ImportPlanHeader_productUom option:selected").text();	
	table += "<table class='table-bordered table table-striped table-hover dataTable' cellspacing='0'>" +
				"<thead><tr role='row' class='header-row'>" +
					"<th class='hidden-phone'>Nhan Hang:</th>" +
					"<th class='hidden-phone'>" + productName + "</th>" +
					"<th class='hidden-phone'>dvt: " + paking + "</th>" +
					"<th class='hidden-phone'>Quy cach pallet</th>" +
					"<th class='hidden-phone'>" + quantityConvert + "</th></tr></thead><tbody>";
	table += "<tr>" +
	"<td>" + "</td>" +
	"<td>" + "Sales Order" + "</td>" +
	"<td>" + "Import volume" + "</td>" +
	"<td>" + "Ton cuoi thang" + "</td>" +
	"<td>" + "Luong hang ton kho quy doi </br> thanh so ngay ban hang cua sales(ngay)" + "</td>" +
	"</tr>";
//	listMonth[0][0].thisSalesForecastDetails[0].productId
	var turn = 0;
	var turnMonth = 0;
	loops = turn;
	for ( var x in data) {		
		for ( var s in data[x]) {			
			table += "<tr><td><label id='lblMonth" + turnMonth + "'>" + data[x][s].thisMonth + "</label></td>";
			turnMonth += 1;
			var check = true;
			for ( var v in data[x][s].thisSalesForecastDetails) {
				var thisProductId = data[x][s].thisSalesForecastDetails[v].productId;
				if(thisProductId == productId){
					var thisQuantity = data[x][s].thisSalesForecastDetails[v].quantity;
					var min = Math.ceil(thisQuantity + (thisQuantity/30)*7);					
					var max = Math.ceil(thisQuantity + (thisQuantity/30)*10);
					var pallet = Math.round(min / quantityConvert);
					var importVolume = pallet*quantityConvert;
					var tonCuoiThang = importVolume - thisQuantity;
					var ngayTon = " " + (tonCuoiThang / (thisQuantity/30));
					ngayTon = ngayTon.substring(1,5);
					var txtPallet = "txtPallet" + turn;
					table += "<td><input type='text' id='txtThisQuantity" + turn + "' value='" + thisQuantity + "' disabled/></td>" +
							"<td><input type='text' onkeypress='return event.charCode >= 40 && event.charCode <= 57' onkeyup='txtPalletChange("+ turn + ',' + thisQuantity +")' size='4' id='" + txtPallet + "' value='" + pallet + "'/>" +
							"<input type='text' size='21' id='txtImportVolume" + turn + "' value='" + importVolume + "' disabled/></td>" +
							"<td><input type='text' id='txtTonCuoiThang" + turn + "' value='" + tonCuoiThang + "' disabled/></td>" +
							"<td><input type='text' id='txtNgayTon" + turn + "' maxlength='4' value='" + ngayTon + "' disabled/></td></tr>";
					check = false;					
					turn += 1;
					loops = turn;
				}
			}
			if (check) {
				table += "<td><input type='text' id='txtThisQuantity" + loops + "' value='-' disabled/></td>" +
				"<td><input type='text' size='21' id='txtImportVolume" + loops + "' value='-' disabled/></td>" +
				"<td><input type='text' id='txtTonCuoiThang" + loops + "' value='-' disabled/></td>" +
				"<td><input type='text' id='txtNgayTon" + loops + "' maxlength='4' value='-' disabled/></td></tr>";
				loops += 1;
			}
		}
	}
	table += "</tbody></table><button class='btn btn-primary' id='btnAddProduct' onclick='btnAddProductClick()'>Create</button>";
	$("#" + id).html(table);
}
function txtPalletChange(turn, thisQuantity) {
	var pallet = $("#txtPallet" + turn).val();
	var importVolume = pallet*quantityConvert;
	var tonCuoiThang = importVolume - thisQuantity;
	var ngayTon = " " + (tonCuoiThang / (thisQuantity/30));
	ngayTon = ngayTon.substring(1,5);
	$("#txtImportVolume" + turn).val(importVolume);
	$("#txtTonCuoiThang" + turn).val(tonCuoiThang);
	$("#txtNgayTon" + turn).val(ngayTon);
}
function getTimeYear(jsonObject, url, data) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	listYears = res[data];
        }
    }).done(function() {
    	bindYear();
    	bindMonth();
	});
}
function bindYear() {
	var lastcheck = -1;
   var source =
   {
       datatype: "local",
       localdata: listYears,
       datafields: [
           { name: 'customTimePeriodId', type: 'string' },
           { name: 'periodName', type:'string' }
       ],
       async: false
   };
   var dataAdapter = new $.jqx.dataAdapter(source);
   $("#jqxYear").jqxDropDownList({ checkboxes: true, source: dataAdapter, displayMember: "periodName", valueMember: "customTimePeriodId", width: 200, height: 25});
   
   $("#jqxYear").on('checkChange', function (event) {
       if (event.args) {
           var item = event.args.item;
           if (item.checked) {
        	   if (lastcheck == -1 || lastcheck == item.index - 1) {
        		   var id = item.value;
                   getMonth(id);
                   $("#jqxYear").jqxDropDownList('disableItem', item);
                   lastcheck = item.index;
        	   } else {
        		   $("#jqxYear").jqxDropDownList('uncheckItem', item);
        	   }
           }
       }
   });
   $("#jqxYear").on('close', function (event) {
	   $("#jqxMonth").jqxDropDownList('destroy');
	    var html222 = "<div style='float: left;' id='jqxMonth'></div>";
	    $("#222").html(html222);
	   bindMonth();
   });
}

function bindMonth() {
	var lastcheck = -1;
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

	   $("#jqxMonth").on('checkChange', function (event) {
	       if (event.args) {
	           var item = event.args.item;
	           if (item.checked) {
	        	   if (lastcheck == -1 || lastcheck == item.index - 1) {
	        		   var id = item.value;
		               var thisMonth = item.label;
		               lastcheck = item.index;		               
		            	   $("#jqxMonth").jqxDropDownList('disableItem', item);
		            	   getThisMonth(id, thisMonth);
		               		               
	        	   } else {
	        		   $("#jqxMonth").jqxDropDownList('uncheckItem', item);
	        	   }
	           }
	       }
	   });
	   $("#jqxMonth").on('close', function (event) {
//		   $("#btnCreate").css("-webkit-animation","ringing 1.0s 1 ease 1.0s");
	   });
}
function getThisMonth(id, thisMonth) {
	getMonthSalesForcast(
			{customTimePeriodId: id,
				thisMonth: thisMonth,}
			,"getMonthSalesForcast", "month");
}
var listMonth = [];
function getMonthSalesForcast(jsonObject, url, data) {
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	listTemp = res[data];
        }
    }).done(function() {
    	listMonth.push(listTemp);    	
	});
}
function renderTableHtml1(data, id){
	var productName = $("#ImportPlanHeader_productId option:selected").text();
	var paking = $("#ImportPlanHeader_productUom option:selected").text();
	var table = "";
	table += "<table class='table-bordered table table-striped table-hover dataTable' cellspacing='0'>" +
				"<thead><tr role='row' class='header-row'>" +
					"<th class='hidden-phone'>Nhan Hang:</th>" +
					"<th class='hidden-phone'>" + '-' + "</th>" +
					"<th class='hidden-phone'>dvt: " + '-' + "</th>" +
					"<th class='hidden-phone'>Quy cach pallet</th>" +
					"<th class='hidden-phone'>" + '-' + "</th></tr></thead><tbody>";
	table += "<tr>" +
	"<td>" + "</td>" +
	"<td>" + "Sales Order" + "</td>" +
	"<td>" + "Import volume" + "</td>" +
	"<td>" + "Ton cuoi thang" + "</td>" +
	"<td>" + "Luong hang ton kho quy doi </br> thanh so ngay ban hang cua sales(ngay)" + "</td>" +
	"</tr>";
	for ( var x in data) {
		table += "<tr>" +
				"<td>" + data[x][0].thisMonth + "</td>" +
				"<td>" + '-' + "</td>" +
				"<td>" + '-' + "</td>" +
				"<td>" + '-' + "</td>" +
				"<td>" + '-' + "</td>" +
				"</tr>";
	}
	table += "</tbody></table>";
	$("#" + id).html(table);
}

function btnCreateClick() {
	var txtPlan = $("#txtNamePlan").val();
	var yearCheck = $("#jqxYear").jqxDropDownList('getCheckedItems');
	var motnhCheck = $("#jqxMonth").jqxDropDownList('getCheckedItems');
	var validate = true;
	
	if (txtPlan == "") {
		$( "#txtNamePlan" ).effect("shake");
		$( "#txtNamePlan" ).focus();
		validate = false;
	}
	if (yearCheck == "") {
		$( "#jqxYear" ).effect("shake");
		validate = false;
	}
	if (motnhCheck == "") {
		$( "#jqxMonth" ).effect("shake");
		validate = false;
	}
	
	if (validate) {
		$("#breadcrumbs").append(" <i class='icon-angle-right'></i><a style='color:red'> " + txtPlan + "</a>");
		$("#namePlan").css("display", "none");
		$("#dateChoice").css("display", "none");
		$("#productChoice").css("display", "block");
		$("#myTable").css("display", "block");
//		$("#ImportPlanHeader_productCategoryId").css("-webkit-animation","ringing 1.0s 1 ease 0.3s");
		renderTableHtml1(listMonth, "myTable");
	}
}
function btnCancelClick() {
	clearOld();
	$( "#nav" ).effect( "shake" );
	setTimeout(function(){$( "#sidebar" ).effect( "shake" )}, 500);
	setTimeout(function(){$( "#shakeNow" ).effect( "shake" )}, 900);
}

$(document).ready(function(){
//	$("#main-container").css("-webkit-animation","ringing 2.0s 4 ease 1.0s");
	getYear();
	$("#productChoice").css("display", "none");
	$("#myTable").css("display", "none");
	bindProduct(
			{productCategoryId: $("select[name='productCategoryId']").val()}
			, 'getProductByCategory' , 'listProducts', 'productId', 'internalName', 'productId');
	$("select[name='productCategoryId']").change(function(){
//		$("#ImportPlanHeader_productId").css("-webkit-animation","ringing 1.0s 1 ease 1.0s");
		bindProduct(
				{productCategoryId: $("select[name='productCategoryId']").val()}
				, 'getProductByCategory' , 'listProducts', 'productId', 'internalName', 'productId');
	});
	$("select[name='productId']").change(function(){
		bindUomUnit();
	});
});

function clearOld() {
	listSalesForcastForThisProduct = [];
	listMonths = [];
	listMonth = [];
	listTempMonth = [];
    $("#jqxMonth").jqxDropDownList('destroy');
    $("#jqxYear").jqxDropDownList('destroy');
    var html111 = "<div style='float: left;' id='jqxYear'></div>";
    var html222 = "<div style='float: left;' id='jqxMonth'></div>";
    $("#111").html(html111);
    $("#222").html(html222);
	listYears = [];
	$("#txtNamePlan").val('');
//	getYear();
    setTimeout(function(){getYear()}, 200);	
}

function bindProduct(jsonObject, url, data, key, value, id) {
	jQuery.ajax({
		url : url,
		type : "POST",
		data : jsonObject,
		success : function(res) {
			var json = res[data];
			renderHtml2(json, key, value, id);
		}
	}).done(function() {
		var checkProductChoiced = $("select[name='productId']").val();
		if (checkProductChoiced != '-Select-') {
			bindUomUnit();
		}		
	});
}

function bindUomUnit() {
	update({productId: $("select[name='productId']").val()}, 'getUomUnit', 'listUom', 'description', 'description', 'productUom');
}
function update(jsonObject, url, data, key, value, id) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	var json = res[data];
            renderHtml(json, key, value, id);
        }
    }).done(function() {
    	getQuantityConvert();
	});
}
function renderHtml2(data, key, value, id){
	var y = "<option>-Select-</option>";
	for (var x in data){
		y += "<option value='" + data[x][key] + "'>";
		y += data[x][value] + "</option>";
	}
	$("select[name='"+id+"']").html(y);
}
function renderHtml(data, key, value, id){
	var y = "";
	for (var x in data){
		y += "<option value='" + data[x][key] + "'>";
		y += data[x][value] + "</option>";
	}
	$("select[name='"+id+"']").html(y);
}
//buoc 3
var listProduct = [];
var lastTable = [];
function btnAddProductClick() {
	storeHistoryProduct();
	var listProductAdded = [];
	for ( var m in listMonth) {
		var productId = $("#ImportPlanHeader_productId option:selected").val();
		var productName = $("#ImportPlanHeader_productId option:selected").text();
		var quantityConvert1 = quantityConvert;
		var importVolume1 = $("#txtImportVolume" + m).val();
		var product1 = new product(productId, productName, quantityConvert1, importVolume1);
		listProductAdded.push(product1);
	}
	listProduct.push(listProductAdded);
	caculatorTotal();
	$("#btnAddProduct").css("visibility","hidden");
}
function storeHistoryProduct() {
	thisHistory = [];
	var count = 0;
	var productNameH = $("#ImportPlanHeader_productId option:selected").text();
	var productIdH = $("#ImportPlanHeader_productId option:selected").val();
	var pakingH = $("#ImportPlanHeader_productUom option:selected").text();
	var quantityConvertH = quantityConvert;
	for ( var s in listMonth) {
		var thisQuantityH = $("#txtThisQuantity" + count).val();
		var palletH = $("#txtPallet" + count).val();
		var importVolumeH = $("#txtImportVolume" + count).val();
		var tonCuoiThangH = $("#txtTonCuoiThang" + count).val();
		var ngayTonH = $("#txtNgayTon" + count).val();
		var thisMonthH = $("#lblMonth" + count).html();
		var thisProductH = new historyProduct(thisQuantityH, palletH, importVolumeH, tonCuoiThangH, ngayTonH, productNameH, pakingH, quantityConvertH, thisMonthH, productIdH);
		thisHistory.push(thisProductH);
		count += 1;
	}
	
	lastTable[productIdH] = thisHistory;
}
function historyProduct(thisQuantityH, palletH, importVolumeH, tonCuoiThangH, ngayTonH, productNameH, pakingH, quantityConvertH, thisMonthH, productIdH) {
	this.thisQuantityH = thisQuantityH;
	this.palletH = palletH;
	this.importVolumeH = importVolumeH;
	this.tonCuoiThangH = tonCuoiThangH;
	this.ngayTonH = ngayTonH;
	this.productNameH = productNameH;
	this.pakingH = pakingH;
	this.quantityConvertH = quantityConvertH;
	this.thisMonthH = thisMonthH;
	this.productIdH = productIdH;
}

var listProductInThisMonth = [];
function caculatorTotal() {
	listProductInThisMonth = [];
	for ( var m in listMonth) {
		var month = listMonth[m][0].thisMonth;
		
		var productInMonth = [];
		for ( var y in listProduct) {
			productInMonth.push(listProduct[y][m]);
		}
		var tempArray = [];
		tempArray.push(month);
		tempArray.push(productInMonth);
		listProductInThisMonth.push(tempArray);
	}
	renderTotalTable();
}
windowsCount = 1;
function showDetails(thisId, productName) {
	var history = lastTable[thisId];
	var historyTable = "";
	historyTable += "<table class='table-bordered table table-striped table-hover dataTable' cellspacing='0'>" +
	"<thead><tr role='row' class='header-row'>" +
		"<th class='hidden-phone'>Nhan Hang:</th>" +
		"<th class='hidden-phone'>" + history[0].productNameH + "</th>" +
		"<th class='hidden-phone'>dvt: " + history[0].pakingH + "</th>" +
		"<th class='hidden-phone'>Quy cach pallet</th>" +
		"<th class='hidden-phone'>" + history[0].quantityConvertH + "</th></tr></thead><tbody>";
	historyTable += "<tr>" +
	"<td>" + "</td>" +
	"<td>" + "Sales Order" + "</td>" +
	"<td>" + "Import volume" + "</td>" +
	"<td>" + "Ton cuoi thang" + "</td>" +
	"<td>" + "Luong hang ton kho quy doi </br> thanh so ngay ban hang cua sales(ngay)" + "</td>" +
	"</tr>";
	var turnMonthH = 1;
	for ( var rerd in history) {
		var newTurn =  history[0].productIdH + turnMonthH.toString();
		
			historyTable += "<tr><td><label id='lblMonthH" + newTurn + "'>" + history[rerd].thisMonthH + "</label></td>" +
			"<td><input type='text' id='txtThisQuantityH" + newTurn + "' value='" + history[rerd].thisQuantityH + "' disabled/></td>";
			if (history[rerd].importVolumeH != '-') {
				historyTable += "<td><input type='text' onkeypress='return event.charCode >= 40 && event.charCode <= 57' onkeyup='txtPalletChangeH(\"" + newTurn + "\",\"" + history[rerd].thisQuantityH + "\",\"" + history[0].quantityConvertH + "\")' size='4' id='txtPalletH" + newTurn + "' value='" + history[rerd].palletH + "'/>";
			}else {
				historyTable += "<td>";
			}
			historyTable += "<input type='text' size='21' id='txtImportVolumeH" + newTurn + "' value='" + history[rerd].importVolumeH + "' disabled/></td>" +
			"<td><input type='text' id='txtTonCuoiThangH" + newTurn + "' value='" + history[rerd].tonCuoiThangH + "' disabled/></td>" +
			"<td><input type='text' id='txtNgayTonH" + newTurn + "' maxlength='4' value='" + history[rerd].ngayTonH + "' disabled/></td></tr>";
				
		turnMonthH += 1;
	}
	historyTable += "</tbody></table><button class='btn btn-primary' id='btnUpdateProduct' onclick='btnUpdateProductClick()'>Update</button>";
	$(document.body).append("<div id='newWindow" + windowsCount + "'><div>" + productName + "</div><div>" + historyTable +"</div></div>");	
	$('#newWindow' + windowsCount).jqxWindow({ height: 450, width: 800});
	windowsCount += 1;
}
function txtPalletChangeH(value, thisQuantity, quantityConvertH) {
//	value = value.trim();
	var thisValue = $("#txtPalletH" + value).val();
	var importVolume = thisValue*quantityConvertH;
	var tonCuoiThang = importVolume - thisQuantity;
	var ngayTon = " " + (tonCuoiThang / (thisQuantity/30));
	ngayTon = ngayTon.substring(1,5);
	var oldPalletTotal = $("#txtpalletTotalt2" + value).val();
	var oldPalet = $("#txtpallet2" + value).val();
	var lastChar = value.substr(value.length - 1);
	ngayTon = ngayTon.substring(1,5);
	var pallet = importVolume / thisQuantity;
	var newPalletTotal = oldPalletTotal - oldPalet + pallet;
	var newTotalCont = newPalletTotal / 33;
	var value2 = value + " ";
	$("#txtpalletTotalt2" + lastChar).val(newPalletTotal);
	$("#txttotalCont2" + lastChar).val(newTotalCont);
	$("#txtpallet2" + value2).val(pallet);
	$("#txtImportVolumeH" + value).val(importVolume);
	$("#txtimportVolume2" + value2).val(importVolume);
	$("#txtTonCuoiThangH" + value).val(tonCuoiThang);
	$("#txtNgayTonH" + value).val(ngayTon);
//	alert("#txtImportVolumeH" + value + " and " + "#txtimportVolume2" + value);
}
function btnUpdateProductClick() {
	
}
function renderTotalTable() {
	var tableTotal = "";
	
	
	tableTotal += "<table class='table-bordered table table-striped table-hover dataTable' cellspacing='0'><thead><tr role='row' class='header-row'><th class='hidden-phone'>Calendar</th><th class='hidden-phone'></th>";

	for ( var p in listProduct) {
		var thisId = listProduct[p][0].productId;
		tableTotal += "<th class='hidden-phone' onclick='showDetails(\"" + thisId + "\",\"" + listProduct[p][0].productName + "\")'>" + listProduct[p][0].productName + "(import/pallet)</th>";
	}		
	tableTotal += "<th class='hidden-phone'>Tong so Pallet</th><th class='hidden-phone'>So Container</th></tr></thead><tbody>";
	var turn2 = 1;
	
	for ( var m in listProductInThisMonth) {
			
			var month = listProductInThisMonth[m][0];
			tableTotal += "<tr><td>" + month + "</td><td></td>";
			
			var newListProduct = listProductInThisMonth[m][1];
			
			var pallet = 0;
			var palletTotal = 0;
			var totalCont = 0;
			for ( var gr in newListProduct) {
				var newTurn2 =  newListProduct[gr].productId + turn2.toString();
				newTurn2 = newTurn2.trim();
				var importVolume2 = newListProduct[gr].importVolume;
				var thisQuantity2 = newListProduct[gr].thisQuantity;
				
				if (importVolume2 != '-') {
					pallet = importVolume2 / thisQuantity2;
					palletTotal += pallet;
					totalCont = palletTotal / 33;
				} else {
					importVolume2 = 0;
					pallet = 0;
					palletTotal = 0;
					totalCont = 0;
				}
				
				tableTotal += "<td><input type='text' class='inputSmall' id='txtimportVolume2" + newTurn2 + " ' value='" + importVolume2 + "'/>" +
						"<input type='text' class='inputSmall' id='txtpallet2" + newTurn2 + " ' value='" + pallet + "'/></td>";				
				
			}
			
			
			tableTotal += "<td><input type='text' class='inputSmall' id='txtpalletTotalt2" + turn2 + " ' value='" + palletTotal + "'/></td>";
			tableTotal += "<td><input type='text' class='inputSmall' id='txttotalCont2" + turn2 + " ' value='" + totalCont + "'/></td>";
			tableTotal += "</tr>";
			turn2 += 1;
	}
//	listProductInThisMonth[0][1][0].productName
//	listProductInThisMonth[0][0] time
	tableTotal += "</tbody></table>";
	$("#myTotalTable").html(tableTotal);
}
function product(productId, productName, thisQuantity, importVolume) {
	this.productId = productId;
	this.productName = productName;
	this.thisQuantity = thisQuantity;
	this.importVolume = importVolume;
}