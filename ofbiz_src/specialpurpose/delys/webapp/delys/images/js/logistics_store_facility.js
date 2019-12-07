var isNull = false;
$(document).ready(function(){
	if ($("select[name='productStoreId']").val()){
		isNull = false;
	}
	updateMultiElement({
    	productStoreId: $("select[name='productStoreId']").val(),
    	facilityId : $("select[name='facilityId']").val(),
    	contactMechPurposeTypeIdFrom: "SHIPPING_LOCATION"
	}, 'getFacilities' , 'listFacilities', 'listContactMechsFrom', 'facilityId', 'facilityName', 'contactMechId', 'address1', 'facilityId', 'originContactMechId');
	
	$("select[name='productStoreId']").change(function(){
		isNull = false;
		updateMultiElement({
        	productStoreId: $(this).val(),
        	facilityId : $("select[name='facilityId']").val(),
        	contactMechPurposeTypeIdFrom: "SHIPPING_LOCATION"
		}, 'getFacilities' , 'listFacilities', 'listContactMechsFrom', 'facilityId', 'facilityName', 'contactMechId', 'address1', 'facilityId', 'originContactMechId');
	});
	$("select[name='facilityId']").change(function(){
		isNull = false;
		update({
			facilityId: $("select[name='facilityId']").val(),
			contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
	});
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
function updateMultiElement(jsonObject, url, data1, data2, key1, value1, key2, value2, id1, id2) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	var json1 = res[data1];
            renderHtml(json1, key1, value1, id1);
            var json2 = res[data2];
            renderHtml(json2, key2, value2, id2);
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