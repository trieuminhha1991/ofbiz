$(function(){
	ReqTemplateObj.init();
});
var ReqTemplateObj = (function() {
	var init = function() {
		initInputs();
		initElementComplex();
		initValidateForm();
	};
	var initInputs = function() {
	};
	var initElementComplex = function() {
	};
	
	function finishCreateRequirement(){
		/*var listProducts = [];
		if (listProductSelected != undefined && listProductSelected.length > 0){
			for (var i = 0; i < listProductSelected.length; i ++){
				var data = listProductSelected[i];
				var map = {};
		   		map['productId'] = data.productId;
		   		map['unitCost'] = data.unitCost;
		   		if(data.expireDate){
		   			map['expireDate'] = data.expireDate.getTime();
		   		}
		   		map['quantity'] = data.quantity;
		   		map['statusId'] = data.statusId;
		   		map['uomId'] = data.uomId;
		        listProducts.push(map);
			}
		}
		var listProducts = JSON.stringify(listProducts);
		$.ajax({
			type: 'POST',
			url: 'createNewRequirement',
			async: false,
			data: {
				listProducts: listProducts,
				originFacilityId: $('#originFacilityId').val(),
				requirementTypeId: $('#requirementTypeId').val(),
				reasonEnumId: $('#reasonEnumId').val(),
				estimatedBudget: $("#estimatedBudget").val(),
				requiredByDate: $("#requiredByDate").jqxDateTimeInput('getDate').getTime(),
				requirementStartDate: $("#requirementStartDate").jqxDateTimeInput('getDate').getTime(),
				currencyUomId: $("#currencyUomId").val(),
				description: $("#description").val(),
			},
			beforeSend: function(){
				$("#btnPrevWizard").addClass("disabled");
				$("#btnNextWizard").addClass("disabled");
				$("#loader_page_common").show();
			},
			success: function(data){
				viewRequirementDetail(data.requirementId);
			},
			error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
				$("#btnPrevWizard").removeClass("disabled");
				$("#btnNextWizard").removeClass("disabled");
			},
		});*/
	}
	
	/*function viewRequirementDetail(requirementId){
		window.location.href = 'viewRequirementDetail?requirementId=' + requirementId;
	}*/
	var initValidateForm = function(){
		
	};
	return {
		init: init,
	}
}());