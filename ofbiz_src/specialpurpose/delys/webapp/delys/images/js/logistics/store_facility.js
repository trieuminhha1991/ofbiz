var isNull = false;
$(document).ready(function(){
//	if (!$("select[name='productStoreId']").val()){
//		isNull = true;
//	} else {
//		update({
//			productStoreId: $("select[name='productStoreId']").val()
//			}, 'getFacilities' , 'listFacilities', 'facilityId', 'facilityName', 'facilityId');
//	}
	$("select[name='productStoreId']").change(function(){
		var value = "_NA_";
		if($(this).val()){
			isNull = false;
			value = $(this).val();
		}else{
			isNull = true;
		}
		update({
			productStoreId: $("select[name='productStoreId']").val()
			}, 'getFacilities' , 'listFacilities', 'facilityId', 'facilityName', 'facilityId');
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