$(document).ready(function() {
	init();
});
function init() {
	$("#cooler").chosen();
	$("#neededDate").datepicker({
		dateFormat : 'dd-mm-yy'
	});
	$("#submitRequest").click(submitform);
}

function submitform() {
	var data = {
		fixedAssetId : $("#cooler").val(),
		estimatedCost : $("#cashAdvance").val(),
		startDate: $("#neededDate").val()
	};
	$.ajax({
		url : 'requestCashAdvance',
		data : data,
		type: "POST",
		timeout : 10000,
		success : function(res) {
			if(res.message && res.message == "success"){
				$("#success").show();
				$("#error").hide;
			}else{
				$("#success").hide();
				$("#error").show();
			}
		},
		error : function() {
			$("#success").hide();
			$("#error").show();
		}
	});
}
