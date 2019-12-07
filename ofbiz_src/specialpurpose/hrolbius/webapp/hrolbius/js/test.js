var isNull = false;
$(document).ready(function(){
	if ($("select[name='productStoreId']").val()){
		isNull = false;
	}
	if (!$("input[name='requirementId']").val()){
		updateMultiElement({
			productStoreId: $("select[name='productStoreId']").val(),
			productStoreIdTo: $("select[name='productStoreIdTo']").val(),
			facilityId : $("select[name='facilityId']").val(),
			contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
			contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
		}, 'getFacilities' , 'listFacilities', 'listFacilitiesTo', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'contactMechId', 'address1', 'facilityId', 'facilityTo', 'originContactMechId', 'destContactMechId');
	} else {
		update({
        	productStoreId: $("select[name='productStoreId']").val(),
        	facilityId: $("input[name='facilityIdFrom']").val()
		}, 'getFacilitiesByStore' , 'listFacilities', 'facilityId', 'facilityName', 'facilityId');
		update({
        	productStoreId: $("select[name='productStoreIdTo']").val(),
        	facilityId: $("input[name='facilityIdTo']").val()
		}, 'getFacilitiesByStore' , 'listFacilities', 'facilityId', 'facilityName', 'facilityTo');
	}
//	if (!$("input[name='originCTM']").val()){
//		updateNewList({
//			facilityId: $("select[name='facilityId']").val(),
//			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
//			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
//	}
//	if (!$("input[name='destCTM']").val()){
//		updateNewList({
//			facilityId: $("select[name='facilityTo']").val(),
//			contactMechPurposeTypeId: "SHIPPING_LOCATION",
//			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
//	}
	/*$("select[name='productStoreIdTo']").change(function(){
		isNull = false;
		updateMultiElement({
        	productStoreIdTo: $(this).val(),
        	productStoreId: $("select[name='productStoreId']").val(),
        	facilityId : $("select[name='facilityId']").val(),
        	facilityIdTo : $("select[name='facilityTo']").val(),
        	contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
			contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
		}, 'getFacilities' , 'listFacilities', 'listFacilitiesTo', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'contactMechId', 'address1', 'facilityId', 'facilityTo', 'originContactMechId', 'destContactMechId');
	});*/
	$("select[name='productStoreId']").change(function(){
		isNull = false;
		if ($("select[name='productStoreIdTo']").val()){
			updateMultiElement({
				productStoreId: $("select[name='productStoreId']").val(),
				productStoreIdTo: $("select[name='productStoreIdTo']").val(),
				facilityId : $("select[name='facilityId']").val(),
				facilityIdTo : $("select[name='facilityTo']").val(),
				contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
				contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
				}, 'getFacilities' , 'listFacilities', 'listFacilitiesTo', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'contactMechId', 'address1', 'facilityId', 'facilityTo', 'originContactMechId', 'destContactMechId');
		} else {
			updateMultiElement({
	        	productStoreId: $(this).val(),
	        	productStoreIdTo: $("select[name='productStoreIdTo']").val(),
	        	facilityId : $("select[name='facilityId']").val(),
	        	facilityIdTo : $("select[name='facilityTo']").val(),
	        	contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
				contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
			}, 'getFacilities' , 'listFacilities', 'listFacilitiesTo', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'contactMechId', 'address1', 'facilityId', 'facilityTo', 'originContactMechId', 'destContactMechId');
		}
	});
	$("select[name='facilityId']").change(function(){
		isNull = false;
		if ($("select[name='productStoreId']").val() == $("select[name='productStoreIdTo']").val()){
			updateFacilityContactMech({
				productStoreId: $("select[name='productStoreIdTo']").val(),
	        	facilityIdTo : $("select[name='facilityId']").val(),
	        	contactMechPurposeTypeIdFrom: "SHIP_ORIG_LOCATION",
				contactMechPurposeTypeIdTo: "SHIPPING_LOCATION"
			}, 'getDiffFacilities' , 'listFacilities', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'contactMechId', 'address1', 'facilityTo', 'originContactMechId', 'destContactMechId');
		} else {
			update({
				facilityId: $("select[name='facilityId']").val(),
				contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
		}
	});
	/*$("select[name='facilityTo']").change(function(){
		isNull = false;
		if ($("select[name='productStoreId']").val() == $("select[name='productStoreIdTo']").val()){
			updateFacilityContactMech({
				productStoreId: $("select[name='productStoreId']").val(),
	        	facilityIdTo : $("select[name='facilityTo']").val(),
	        	contactMechPurposeTypeIdFrom: "SHIPPING_LOCATION",
				contactMechPurposeTypeIdTo: "SHIP_ORIG_LOCATION"
			}, 'getDiffFacilities' , 'listFacilities', 'listContactMechsFrom', 'listContactMechsTo', 'facilityId', 'facilityName', 'contactMechId', 'address1', 'facilityId', 'destContactMechId', 'originContactMechId');
		} else {
			update({
				facilityId: $("select[name='facilityTo']").val(),
				contactMechPurposeTypeId: "SHIPPING_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
		}
	});*/
});
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
function updateNewList(jsonObject, url, data, key, value, id) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	var json = res[data];
            renderHtmlNewList(json, key, value, id);
        }
    });
}
function updateFacilityContactMech(jsonObject, url, data1, data2, data3, key1, value1, key2, value2, id1, id2, id3) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	var json1 = res[data1];
            renderHtml(json1, key1, value1, id1);
            var json2 = res[data2];
            renderHtml(json2, key2, value2, id2);
            var json3 = res[data3];
            renderHtml(json3, key2, value2, id3);
        }
    });
}
function updateMultiElement(jsonObject, url, data1, data2, data3, data4, key1, value1, key2, value2, id1, id2, id3, id4) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	var json1 = res[data1];
            renderHtml(json1, key1, value1, id1);
            var json2 = res[data2];
            renderHtml(json2, key1, value1, id2);
            var json3 = res[data3];
            renderHtml(json3, key2, value2, id3);
            var json4 = res[data4];
            renderHtml(json4, key2, value2, id4);
        }
    });
}
function renderHtml(data, key, value, id){
	var y = "";
	if(isNull){
		y = "<option value=''></option>";
	}
	for (var x in data){
		y += "<option value='" + data[x][key] + "'>";
		y += data[x][value] + "</option>";
	}
	$("select[name='"+id+"']").html(y);
}
function renderHtmlNewList(data, key, value, id){
	var y = "";
	y = "<option value=''></option>";
	for (var x in data){
		y += "<option value='" + data[x][key] + "'>";
		y += data[x][value] + "</option>";
	}
	$("select[name='"+id+"']").html(y);
}