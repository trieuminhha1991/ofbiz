<script type="text/javascript">
	if (!uiLabelMap) var uiLabelMap = {};
	uiLabelMap.BSAreYouSureYouWantToCreate = "${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCreate)}?";
	uiLabelMap.BSYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseProduct)}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	
	var listProductSelected = [];
	
	$(function(){
		OlbReqSalesTransferTotal.init();
	});
	var OlbReqSalesTransferTotal = (function() {
		var init = function() {
			initEvent();
		};
		var initEvent = function() {
			$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
				if(info.step == 1 && (info.direction == "next")) {
					// check form valid
					if(!OlbReqSalesTransfer.getValidator().validate()){
						setTimeout(function(){OlbReqSalesTransfer.getValidator().hide()}, 2000);
						return false;
					}
					if (listProductSelected.length <= 0){
						jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseProduct);
						return false;
					}
					transferDataToConfirm();
				}
			}).on('finished', function(e) {
				jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCreate, function() {
            		createRequirement();
	            });
			}).on('stepclick', function(e){
				//prevent clicking on steps
			});
		};
		function transferDataToConfirm(){
			var requirementTypeId = OlbReqSalesTransfer.getObs().requirementTypeDDL.getValue();
			if ("CHANGEDATE_REQUIREMENT" == requirementTypeId) {
				var parentFacilityObj = $("#strFacilityId").closest(".row-fluid");
				if (parentFacilityObj) parentFacilityObj.hide();
				var parentDestFacilityObj = $("#strDestFacilityId").closest(".row-fluid");
				if (parentDestFacilityObj) parentDestFacilityObj.hide();
			} else {
				var parentFacilityObj = $("#strFacilityId").closest(".row-fluid");
				if (parentFacilityObj) parentFacilityObj.show();
				var parentDestFacilityObj = $("#strDestFacilityId").closest(".row-fluid");
				if (parentDestFacilityObj) parentDestFacilityObj.show();
			}
			
			<#-- $("#strRequiredByDate").text($("#requiredByDate").val());  -->
			$("#strRequirementStartDate").text($("#requirementStartDate").val());
			$("#strDescription").text($("#description").val());
			$("#strRequirementTypeId").text(OlbReqSalesTransfer.getObs().requirementTypeDDL.getLabel());
			$("#strReasonEnumId").text(OlbReqSalesTransfer.getObs().reasonEnumDDL.getLabel());
			$("#strCustomerId").text(OlbReqSalesTransfer.getObs().customerDDB.getValue());
			$("#strCustomerContactMechId").text(OlbReqSalesTransfer.getObs().customerAddressDDB.getButtonObj().val());
			$("#strFacilityId").text(OlbReqSalesTransfer.getObs().facilityDDB.getButtonObj().val());
			$("#strDestFacilityId").text(OlbReqSalesTransfer.getObs().destFacilityDDL.getLabel());
			
			var tmpSource = $("#jqxgridProductSelected").jqxGrid('source');
			if(typeof(tmpSource) != 'undefined'){
				tmpSource._source.localdata = listProductSelected;
				$("#jqxgridProductSelected").jqxGrid('source', tmpSource);
			}
		}
		
		function createRequirement(){
			var listProducts = [];
			if (listProductSelected != undefined && listProductSelected.length > 0){
				for (var i = 0; i < listProductSelected.length; i ++){
					var data = listProductSelected[i];
					var map = {};
			   		map['productId'] = data.productId;
			   		if (data.expiredDate){
			   			map['expireDate'] = data.expiredDate.getTime();
			   		}
			   		map['quantity'] = data.quantity;
			   		if (data.unitCost != null && data.unitCost != undefined && data.unitCost != ''){
			   			map['unitCost'] = data.unitCost;
			   		} else {
			   			map['unitCost'] = 0;
			   		}
			   		map['uomId'] = data.quantityUomId;
			   		if (data.description) {
			   			map['description'] = data.description;
			   		}
			        listProducts.push(map);
				}
			}
			var listProducts = JSON.stringify(listProducts);
			//var statusId = "REQ_CREATED";
			$.ajax({
				type: 'POST',
				url: 'createNewRequirement',
				async: false,
				data: {
					customerId: OlbReqSalesTransfer.getObs().customerDDB.getValue(),
					destContactMechId: OlbReqSalesTransfer.getObs().customerAddressDDB.getValue(),
					facilityId: OlbReqSalesTransfer.getObs().facilityDDB.getValue(),
					destFacilityId: OlbReqSalesTransfer.getObs().destFacilityDDL.getValue(),
					requirementTypeId: OlbReqSalesTransfer.getObs().requirementTypeDDL.getValue(),
					reasonEnumId: OlbReqSalesTransfer.getObs().reasonEnumDDL.getValue(),
					<#-- 
					estimatedBudget: $("#estimatedBudget").val(),
					requiredByDate: $("#requiredByDate").jqxDateTimeInput('getDate').getTime(),
					 -->
					requirementStartDate: $("#requirementStartDate").jqxDateTimeInput('getDate').getTime(),
					currencyUomId: $("#currencyUomId").val(),
					description: $("#description").val(),
					//statusId: statusId
					listProducts: listProducts,
				},
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	return false;
							}, function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
					        	$("#jqxNotification").jqxNotification("open");
					        	if (data.requirementId) {
					        		window.location.href = "viewReqSalesTransfer?requirementId=" + data.requirementId;
					        	}
							}
					);
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		}
		return {
			init: init,
		}
	}());
</script>