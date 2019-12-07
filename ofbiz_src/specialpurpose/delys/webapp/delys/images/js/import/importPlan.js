$(document).ready(function(){
	var date = new Date();
	date = date.getFullYear();
	getImportPlan(date);
});
function getImportPlan(date) {
	var jsonObject = {date: date};
	var productPlanId = "";
	jQuery.ajax({
        url: "getImportPlanAjax",
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	productPlanId = res["productPlanId"];
        }
    }).done(function() {
    	if (productPlanId != "PlanNotFound") {
    		$("input[name='productPlanHeaderId']").val(productPlanId);
    		$("#hasPlan").submit();
		} else {
			$("#myAlert").css("display", "block");
		}
	});
}