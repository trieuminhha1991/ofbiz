$(document).ready(function(){
	$("input[name^='facilityId']").val($("select[name='facilityId']").val());
	$("input[name^='productStoreId']").val($("select[name='productStoreId']").val());
	
	$("select[name='contactMechId']").change(function(){
		$("input[name^='contactMechId']").val($("select[name='contactMechId']").val());
	});
	
	$("select[name='facilityId']").change(function(){
		$("input[name^='facilityId']").val($("select[name='facilityId']").val());
		$("input[name^='contactMechId']").val($("select[name='contactMechId']").val());
		
		update({
			facilityId: $("select[name='facilityId']").val(),
			contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
	});
	
	updateMultiElement({
    	productStoreId: $("select[name='productStoreId']").val(),
    	facilityId : $("select[name='facilityId']").val(),
    	contactMechPurposeTypeIdFrom: "SHIPPING_LOCATION"
	}, 'getFacilities' , 'listFacilities', 'listContactMechsFrom', 'facilityId', 'facilityName', 'contactMechId', 'address1', 'facilityId', 'contactMechId');
	
	$("select[name='productStoreId']").change(function(){
		$("input[name^='productStoreId']").val($("select[name='productStoreId']").val());
		
		updateMultiElement({
        	productStoreId: $(this).val(),
        	facilityId : $("select[name='facilityId']").val(),
        	contactMechPurposeTypeIdFrom: "SHIPPING_LOCATION"
		}, 'getFacilities' , 'listFacilities', 'listContactMechsFrom', 'facilityId', 'facilityName', 'contactMechId', 'address1', 'facilityId', 'contactMechId');
		
		$("input[name^='facilityId']").val($("select[name='facilityId']").val());
		$("input[name^='contactMechId']").val($("select[name='contactMechId']").val());
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
		for (var x in data){
			y += "<option value='" + data[x][key] + "'>";
			y += data[x][value] + "</option>";
			if(x == 0){
				$("input[name^='"+id+"']").val(data[x][key]);
			}
		}
		$("select[name='"+id+"']").html(y);
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
});