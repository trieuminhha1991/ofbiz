$(document).ready(function(){
	if ($("select[name='uomFromId']").val()){
		update({
			uomFromId: $("select[name='uomFromId']").val(),
			uomToId: $("select[name='uomToId']").val(),
			productId: $("input[name='productId']").val(),
			}, 'getConvertPackingNumber' , 'convertNumber', 'quantityConvert');
	}
	$("select[name='uomFromId']").change(function(){
		update({
			uomFromId: $("select[name='uomFromId']").val(),
			uomToId: $("select[name='uomToId']").val(),
			productId: $("input[name='productId']").val(),
			}, 'getConvertPackingNumber' , 'convertNumber', 'quantityConvert');
	});
	$("select[name='uomToId']").change(function(){
		update({
			uomFromId: $("select[name='uomFromId']").val(),
			uomToId: $("select[name='uomToId']").val(),
			productId: $("input[name='productId']").val(),
			}, 'getConvertPackingNumber' , 'convertNumber', 'quantityConvert');
	});
});
function update(jsonObject, url, data, id) {
    jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	var json = res[data];
        	renderHtml(json, id);
        }
    });
}
function renderHtml(data, id){
	if (data != -1){
		$("input[name='"+id+"']").val(data);
		$("input[name='"+id+"']").attr('disabled', false);
		$("button[name='submitButton']").attr('disabled', false);
	} else {
		$("input[name='"+id+"']").val("");
		$("input[name='"+id+"']").attr('disabled', true);
		$("button[name='submitButton']").attr('disabled', true);
	}
}