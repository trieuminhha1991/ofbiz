var isNull = false;
$(document).ready(function(){
	update({
		facilityId: $("select[name='originFacilityId']").val(),
		contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
		}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
	
	update({
		shipmentMethodTypeId: $("select[name='shipmentMethodTypeId']").val(),
		}, 'getVehicles' , 'listVehicles', 'vehicleId', 'vehicleName', 'vehicleId');
	
	update({
		facilityId: $("select[name='destFacilityId']").val(),
		contactMechPurposeTypeId: "SHIPPING_LOCATION",
		}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
	
	$("select[name='originFacilityId']").change(function(){
		isNull = false;
		update({
			facilityId: $("select[name='originFacilityId']").val(),
			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
	});
	$("select[name='destFacilityId']").change(function(){
		isNull = false;
		update({
			facilityId: $("select[name='destFacilityId']").val(),
			contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
	});
	$("select[name='shipmentMethodTypeId']").change(function(){
		isNull = false;
		update({
			shipmentMethodTypeId: $("select[name='shipmentMethodTypeId']").val(),
			}, 'getVehicles' , 'listVehicles', 'vehicleId', 'vehicleName', 'vehicleId');
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