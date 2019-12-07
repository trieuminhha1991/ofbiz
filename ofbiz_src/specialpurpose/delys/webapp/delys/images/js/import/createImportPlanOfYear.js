var listMonth = [];
var productPlanId;
$(document).ready(function(){
	
	productPlanId = $("#thisPlanId").val();
	if (productPlanId != null) {
		loadPlanAvailable(productPlanId);
		onLoad();
		onLoadData(5);
		bindProductCategory();
	}
	var historyCategoryItem = {};
	var historyCategoryItemF = {};
	var historyProductItem = {};
	var historyProductItemF = {};
	$('#ImportPlanHeader_productCategoryId').on('select', function (event){
		var args = event.args;
		if (args) {               
		    var index = args.index;
		    var item = args.item;
		    var label = item.label;
		    var tempLabel = label.split('>');
		    if (tempLabel[1] == "<i><u") {
		    	label = tempLabel[2].split('<')[0];
			}
		    var value = item.value;
		    if (value) {
		    	$("#myTable").css("opacity", "0.5");
		    	var newItem = {label: "<i><u>" + label + "</u></i>", value:value};
		    	if (historyCategoryItem != null) {
		    		$("#ImportPlanHeader_productCategoryId").jqxDropDownList('updateItem', historyCategoryItemF, historyCategoryItem);
				}
		    	$("#ImportPlanHeader_productId").jqxDropDownList('clear');
		    	$("#ImportPlanHeader_productUom").jqxDropDownList('clear');
		    	historyCategoryItem = item;
		    	historyCategoryItemF = newItem;
		    	historyProductItem = {};
		    	historyProductItemF = {};
		    	bindProductAndCategoryAjax(
						{productCategoryId: value,}
						, 'getProductByCategory' , 'listProducts', 'productId', 'internalName', 'ImportPlanHeader_productId', -1);
			}
		}                        
	});
	$('#jqxProgressBar').on('complete', function (event) {
		setTimeout(function(){ $("#jqxProgressBar").css("display", "none"),showProcessbar(0),$("#myTable").css("opacity", "1"); }, 900);
	});
	$('#ImportPlanHeader_productId').on('select', function (event){
		var args1 = event.args;
		if (args1) {     
		    var index = args1.index;
		    var item = args1.item;
		    var label = item.label;
		    var tempLabel = label.split('>');
		    if (tempLabel[1] == "<i><u") {
		    	label = tempLabel[2].split('<')[0];
			}
		    var value = item.value;
		    if (value) {
		    	var newItem = {label: "<i><u>" + label + "</u></i>", value:value};
		    	if (historyProductItem != null) {
			    	$("#ImportPlanHeader_productId").jqxDropDownList('updateItem', historyProductItemF, historyProductItem);
				}
		    	$("#ImportPlanHeader_productUom").jqxDropDownList('clear');
		    	$("#myTable").css("opacity", "0.5");
		    	historyProductItem = item;
		    	historyProductItemF = newItem;
		    	bindProductAndCategoryAjax({productId: value,},
		    			'getUomUnit', 'listUom', 'uomId', 'description', 'ImportPlanHeader_productUom', -1);
			}
		}                        
	});
	$('#ImportPlanHeader_productUom').on('select', function (event){
		var args1 = event.args;
		if (args1) {
		    var index = args1.index;
		    var item = args1.item;
		    var label = item.label;
		    var value = item.value;
		    if (value) {
		    	onLoadData(40);
		    	var productId = $("#ImportPlanHeader_productId").val();
		    	getMonthSalesForcastAjax(
						{productPlanId: productPlanId,
							productId: productId,
							uomToId: value,}
						,"getAllMonthSalesForcast", "month");
			}
		}                        
	});
	
	 $.contextMenu({
	        selector: '#myTable',
	        callback: function(key, options) {
	        	getMonthSalesForcastAjax(
	        			{productPlanId: productPlanId,
	        				productId: productIdN,
	        				uomToId: uomToIdN,},"getAllMonthSalesForcast", "month");
	        },
	        items: {
	            "refresh": {name: "Refresh", icon: "refresh"}
	        }
	    });
});
function checkAllClick() {
	 if ($("#checkAll").prop('checked')) {
         for ( var s in listMonth) {
        	 $("#check" + s).prop('checked', true);
		}
      }
      else {
    	  for ( var s in listMonth) {
         	 $("#check" + s).prop('checked', false);
 		}
      }
}
function anyOneUncheck() {
	if ($("#checkAll").prop('checked')) {
       	 $("#checkAll").prop('checked', false);
     }
}
function rowClick(count) {
	alert(count);
}
function bindAvailibleProduct(productAvailable) {
	var praLength = productAvailable.length;
	table = "";
	if (praLength > 6) {
		var myCo = 0;
		var myLoops = 0;
		myLoops = 4;
		table += "<table class='table-bordered table table-striped table-hover dataTable' cellspacing='0'>";
		while (myCo < praLength) {
			table += "<tr>";
			for (var i = 0; i < myLoops; i++) {
				var a = productAvailable[myCo];
				myCo += 1;
				if (a == null) {
					a = "";
				}
				table += "<td>" + a + "</td>";
			}
			table += "</tr>";
		}
		table += "</table>";
	} else {
		if (praLength > 0) {
			table += "<table class='table-bordered table table-striped table-hover dataTable' cellspacing='0'>";
			for ( var r in productAvailable) {
				table += "<tr><td>" + productAvailable[r] + "</td></tr>";
			}
			table += "</table>";
		}
	}
	if (table == "") {
		table = "<p style='color: red'>Not Plan Item In This Plan!</p>";
	}
	$("#myCount").text(praLength);
	$("#myCount").jqxTooltip({ content: table, position: 'left', autoHideDelay: 19000});
}

function bindTableAddProduct(productPlanId, productId, value) {
	 getMonthSalesForcastAjax2(
				{productPlanId: productPlanId,
					productId: productId,
					uomToId: value,}
				,"getAllMonthSalesForcast", "month");
}
function onLoad() {
	$("#ImportPlanHeader_productId").jqxDropDownList({
	       source: [],
	       theme: 'olbius',
	       width: '200px',
	       height: '25px',
	       placeHolder: "Select"
	 });
	$("#ImportPlanHeader_productUom").jqxDropDownList({
	       source: [],
	       theme: 'olbius',
	       width: '150px',
	       height: '25px',
	       placeHolder: "Select"
	 });
	$("#jqxProgressBar").jqxProgressBar({ width: 250, height: 30, value: 0, showText:true,  theme:'energyblue'});
}
function loadPlanAvailable(productPlanId2) {
	loadPlanAvailableAjax(
			{productPlanId: productPlanId2,}
			,"loadPlanAvailableAjax", "listPlanAvailable");
}
function loadPlanAvailableAjax(jsonObject, url, data) {
	var listPlanAvailable = [];
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	listPlanAvailable = res[data];
        }
    }).done(function() {
    	loadDone();
	});
}
function getAllSalesForcast(productPlanId) {
	getMonthSalesForcastAjax(
			{productPlanId: productPlanId,}
			,"getAllMonthSalesForcast", "month");
}
var productIdN = "";
var uomToIdN = "";
function getMonthSalesForcastAjax(jsonObject, url, data) {
	listMonth = [];
	var maps = [];
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	maps = res[data];
        }
    }).done(function() {
    	if (maps != null) {
    		var configEmpy = maps.configEmpy;
    		if (configEmpy != "isEmpty") {
    			listMonth = maps.listMonth;
            	productIdN = maps.productId;
            	uomToIdN = maps.uomToId;
            	setTimeout(function(){ renderTableHtml2(listMonth, "myTable", productIdN, uomToIdN); }, 500);
            	loadDone();
			} else {
				insertConfigPacking(jsonObject, 0, false);
			}
    		
		} else {
			$.notify("Data Not Find!", "error");
		}
	});
}


var state = true;
function getMonthSalesForcastAjax2(jsonObject, url, data) {
	listMonth = [];
	var maps = [];
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	maps = res[data];
        }
    }).done(function() {
    	if (maps != null) {
    		listMonth = maps.listMonth;
	    	productIdN = maps.productId;
	    	uomToIdN = maps.uomToId;
	    	setTimeout(function(){ renderTableHtml2(listMonth, "myTable"); }, 500);
	    	loadDone();
//	    	$.notify("Load done!", "info");
	    	$("#ntfIfHave").effect("bounce", "slow");
		} else {
			$.notify("Data Not Find!", "error");
		}
	});
}
function bindProductCategory() {
	bindProductAndCategoryAjax(
			{productCategoryTypeId: "CATALOG_CATEGORY",}
			, 'getProductCategory' , 'listProductCategorys', 'productCategoryId', 'productCategoryId', 'ImportPlanHeader_productCategoryId', -1);
}

function renderByJqx(data, key, value, id, index) {
	var width = '200px';
	if (id == 'ImportPlanHeader_productUom') {
		width = '150px';
	}
	$("#" + id).jqxDropDownList({
	       source: data,
	       theme: 'olbius',
	       width: width,
	       height: '25px',
	       selectedIndex: index,
	       displayMember: value,
	       valueMember: key,
	       searchMode:'startswithignorecase',
	       autoDropDownHeight: true,
	       enableHover :true,
	       itemHeight: 35,
	       placeHolder: "Select"
	 });
}
function loadDone() {
	showProcessbar(100);
}
function onLoadData(timer) {
	$("#jqxProgressBar").css("display", "block");
	showProcessbar(timer);
	$("#myTable").css("opacity", "0.5");
}
function showProcessbar(value) {
	$('#jqxProgressBar').jqxProgressBar(
	        'actualValue', value);
}
function reload() {
	setTimeout(function(){ loadPlanAvailable(productPlanId); }, 500);
}
$(document).ready(function(){
	var datevalue = "28/03/2015";
	var timeNow = new Date();
	var test = "";
	var b = test.toTimeStamp();
});
function btnAddProductClick() {
	onLoadData(10);
	oneTurn = true;
	for ( var m in listMonth) {
		var productPlanIdLess = $("#lblProductPlanId" + m).text();
		var planQuantity = $("#txtImportVolume" + m).text();
		var tonCuoiThang = $("#txtTonCuoiThang" + m).text();
		var check = $("#check" + m).is(":checked");
		if (check) {
			planQuantity = planQuantity.replaceAll(".", "");
			planQuantity = planQuantity.replaceAll(",", "");
			tonCuoiThang = tonCuoiThang.replaceAll(".", "");
			tonCuoiThang = tonCuoiThang.replaceAll(",", "");
			createPlanItemToDatabaseAjax({
				productPlanId: productPlanIdLess,
				productId: productIdN,
				planQuantity: planQuantity,
				statusId: "PLAN_ITEM_CREATED",
				inventoryForecast: tonCuoiThang,
				quantityUomId: uomToIdN,
    			},"createPlanItemToDatabaseAjax", oneTurn);
			oneTurn = false;
		}
	}
	setTimeout(function(){ loadPlanAvailable(productPlanId),getMonthSalesForcastAjax(
			{productPlanId: productPlanId,
				productId: productIdN,
				uomToId: uomToIdN,}
			,"getAllMonthSalesForcast", "month"), loadDone(); }, 1500);
}

function hiddenClick() {
	$('#contentMessages').css('display','none');
}