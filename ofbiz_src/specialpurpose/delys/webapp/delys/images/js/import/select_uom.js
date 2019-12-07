$(document).ready(function(){
	update({
		uomId: $("select[name='shipmentUomId']").val(),
		uomTypeId: "SHIPMENT_PACKING",
		}, 'getChildUoms' , 'listChildUoms', 'uomId', 'description', 'productPackingUomId');
	
	$("select[name='shipmentUomId']").change(function(){
		update({
			uomId: $("select[name='shipmentUomId']").val(),
			uomTypeId: "SHIPMENT_PACKING",
			}, 'getChildUoms' , 'listChildUoms', 'uomId', 'description', 'productPackingUomId');
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
	for (var x in data){
		y += "<option value='" + data[x][key] + "'>";
		y += data[x][value] + "</option>";
	}
	$("select[name='"+id+"']").html(y);
}