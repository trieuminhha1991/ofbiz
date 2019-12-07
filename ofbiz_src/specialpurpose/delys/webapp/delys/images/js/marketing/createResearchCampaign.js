function setNotification(message){
	$("#notificationMk").html(message);
}
/*cost type is chosen*/
// var costChosen = [];
$(document).ready(function() {
	$("#submit").click(submitForm);
	$("#reset").click(resetForm);
	$("#productId").chosen();
	initDate();
	initInput();
	initValidate();
	var date = {};
	
	function initDate() {
		var currentDate = Utils.getCurrentDate();
		$('#fromDate').daterangepicker({
			format : "DD-MM-YYYY",
			minDate : currentDate,
			startDate : currentDate
		}, function(start, end, label) {
			date = {
				fromDate : start.format("YYYY-MM-DD"),
				thruDate : end.format("YYYY-MM-DD")
			};
		});
	}
	/*add costs category*/
	
	function initValidate(){
		$("#EditResearchCampaign").jqxValidator({
		   	rules: [{
				input: '#campaignName',
				message: uiLabelMap.FieldRequired,
				action: 'blur',
				rule: 'required'
			}, { 
				input: '#fromDate', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				rule: function (input, commit) {
                	if(date.fromDate && date.thruDate){
                		return true;
                	}
                	return false;
                }
			}, { 
				input: '#estimatedCost', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				rule: function (input, commit) {
                	if(isNaN(input.jqxNumberInput('val'))){
                		return false;
                	}
                	return true;
                }
			}, { 
				input: '#productId_chzn', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				rule: function (input, commit) {
                	if(!$("#productId").val()){
                		return false;
                	}
                	return true;
                }
			}, { 
				input: '#people', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				rule: function (input, commit) {
                	if(isNaN(input.val())){
                		return false;
                	}
                	return true;
                }
			}, { 
				input: '#place', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				rule: function (input, commit) {
                	if(input.jqxDropDownList('getSelectedIndex') != -1){
                		return true;
                	}
                	return false;
                }
			}]
		 });
	}
	function initInput(){
		 $("#estimatedCost").jqxNumberInput({ width: '220px', height: '25px', digits:12, max: 999999999999 });
		 $('#place').jqxDropDownList({
			theme: 'olbius',
			source: places,
			width: 220,
			dropDownHeight: 200,
			displayMember: "geoName",
			valueMember: 'geoId'
		});
	}
	function submitForm() {
		if(!$("#EditResearchCampaign").jqxValidator('validate')){ return;}
		var mkId = Utils.getUrlParameter("id");
		var action = !mkId ? "createResearchCampaign" : "updateResearchCampaign";
		var costList = JSON.stringify(getCostList());
		var products = JSON.stringify($("#productId").val());
		var p = $("#place").jqxDropDownList('getSelectedItem');
		var place = p ? p.value : "";
		var data = {
			fromDate : date.fromDate,
			thruDate : date.thruDate,
			people : $("#people").val(),
			marketingPlace: place,
			estimatedCost : $("#estimatedCost").jqxNumberInput('val'),
			isActive : $("#isActive").val(),
			campaignName : $("#campaignName").val(),
			campaignSummary : $("#campaignSummary").val(),
			// statusId: $("#isActive").val(),
			productId : products,
			costList : costList
		};
		// return;/	
		$.ajax({
			url : action,
			type : "POST",
			data : data,
			dataType : "json",
			success : function(res) {
				if(res.message && res.message == "success"){
					var su = uiLabelMap && uiLabelMap.sendRequestSuccess ? uiLabelMap.sendRequestSuccess : "success"; 
					setNotification(su);
				}else{
					var su = uiLabelMap && uiLabelMap.sendRequestError ? uiLabelMap.sendRequestError : "error"; 
					setNotification(su);
				}
			}
		});
	}


	function validateForm() {

	}

	function resetForm() {

	}

});
