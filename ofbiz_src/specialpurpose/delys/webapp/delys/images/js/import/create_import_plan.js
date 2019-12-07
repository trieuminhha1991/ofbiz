function selectCategory() {
	$.when(update(
			{productCategoryId: $("select[name='productCategoryId']").val()}, 'getProductByCategory' , 'listProducts', 'productId', 'internalName', 'productId')).then(function(){
		selectUnit({productId: $("select[name='productId']").val()}, 'getUomUnit', 'listUom', 'uomId', 'description', 'productUom');
		
	});
//	update({
//    	productStoreId: $("select[name='productStoreIdTo']").val(),
//    	facilityId: $("input[name='facilityIdTo']").val()
//	}, 'getFacilitiesByStore' , 'listFacilities', 'facilityId', 'facilityName', 'facilityTo');
//
//	
}

$(document).ready(function(){
	
	
	bindProduct(
			{productCategoryId: $("select[name='productCategoryId']").val()}
			, 'getProductByCategory' , 'listProducts', 'productId', 'internalName', 'productId');
	$("select[name='productCategoryId']").change(function(){
		bindProduct(
				{productCategoryId: $("select[name='productCategoryId']").val()}
				, 'getProductByCategory' , 'listProducts', 'productId', 'internalName', 'productId');
	});
	$("select[name='productId']").change(function(){
		bindUomUnit();
	});
});

function readyForRender() {
	var fromDate = $("#hoanmMonthPicker").val();
	var thruDate = $("#hoanmMonthPicker2").val();
//	alert(fromDate + " and " + thruDate);
	getTimeAndSalesForcast(
					{fromDate: fromDate,
					thruDate: thruDate	}
					,"getTimeAndSalesForcast", "listTimeAndSalesForcast", "planDetails");
}
function getTimeAndSalesForcast(jsonObject, url, data, id) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	var json = res[data];
//            renderTableHtml(json, id);
        }
    });
}
function renderTableHtml(data, id){
	var table = "";
	table += "<table class='table-bordered table table-striped table-hover dataTable' cellspacing='0'>" +
				"<thead><tr role='row' class='header-row'>" +
					"<th class='hidden-phone'></th>" +
					"<th class='hidden-phone'></th>" +
					"<th class='hidden-phone'></th>" +
					"<th class='hidden-phone'></th>" +
					"<th class='hidden-phone'></th></tr></thead>";
	
	table += "</table>";
	$("#" + id).html(table);
}


function bindProduct(jsonObject, url, data, key, value, id) {
	jQuery.ajax({
		url : url,
		type : "POST",
		data : jsonObject,
		success : function(res) {
			var json = res[data];
			renderHtml(json, key, value, id);
		}
	}).done(function() {
		bindUomUnit();
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
    });
}
function renderHtml(data, key, value, id){
	var y = "";
	for (var x in data){
		y += "<option value='" + data[x][key] + "'>";
		y += data[x][value] + "</option>";
	}
	$("select[name='"+id+"']").html(y);
}
function selectUnit(jsonData, key, value, id){
	
}

//
//$(document).ready(function(){
//	selectCategory();
//});
