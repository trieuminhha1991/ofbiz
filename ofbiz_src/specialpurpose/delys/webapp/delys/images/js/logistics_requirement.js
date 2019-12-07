var isNull = false;
$(document).ready(function(){
	if ($("select[name='productStoreId']").val()){
		isNull = false;
	}
	update({
    	productStoreId: $("select[name='productStoreId']").val(),
    	facilityId : $("select[name='facilityId']").val(),
	}, 'getFacilities' , 'listFacilities', 'facilityId', 'facilityName', 'facilityId');
	$("select[name='productStoreId']").change(function(){
		isNull = false;
		update({
        	productStoreId: $(this).val(),
        	facilityId : $("select[name='facilityId']").val(),
		}, 'getFacilities' , 'listFacilities', 'facilityId', 'facilityName', 'facilityId');
	});
	update({
    	productStoreId: $("select[name='productStoreIdTo']").val(),
    	facilityId: $("input[name='facilityIdTo']").val()
	}, 'getFacilitiesByStore' , 'listFacilities', 'facilityId', 'facilityName', 'facilityTo');
	$("select[name='productStoreIdTo']").change(function(){
		isNull = false;
		update({
        	productStoreIdTo: $(this).val(),
        	productStoreId: $("select[name='productStoreId']").val(),
        	facilityId : $("select[name='facilityId']").val(),
        	facilityIdTo : $("select[name='facilityTo']").val()
		}, 'getFacilities' , 'listFacilitiesTo', 'facilityId', 'facilityName', 'facilityTo');
	});
	
	update({
		facilityId: $("select[name='facilityId']").val(),
		contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
		}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
	
	update({
		facilityId: $("select[name='facilityTo']").val(),
		contactMechPurposeTypeId: "SHIPPING_LOCATION",
		}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
	
	$("select[name='facilityId']").change(function(){
		isNull = false;
		update({
			facilityId: $("select[name='originFacilityId']").val(),
			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
		alert("abc");
	});
	$("select[name='facilityTo']").change(function(){
		isNull = false;
		update({
			facilityId: $("select[name='facilityTo']").val(),
			contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
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