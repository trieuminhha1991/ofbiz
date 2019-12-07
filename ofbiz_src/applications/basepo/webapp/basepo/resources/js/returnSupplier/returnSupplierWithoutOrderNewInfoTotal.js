$(function() {
	returnSupNewTmp.init();
});

var returnSupNewTmp = (function() {
	var init = function() {
		initElement();
		initEvent();
		initElementComplex();
		initValidateForm();
	};

	var initElement = function() {
	};

	var initElementComplex = function() {
	};

	var initEvent = function() {

		$("#fuelux-wizard").ace_wizard().on("change", function(e, info) {
			if (info.step == 1 && (info.direction == "next")) {
				// check form valid
				$("#containerNotify").empty();
				var resultValidate = !OlbReturnWithoutOrderInfo.getValidator().validate();
				if (resultValidate)
					return false;
				if (listProductSelected.length <= 0) {
					jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
					return false;
				}
				showConfirmPage();
			}
		}).on("finished", function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
				Loading.show("loadingMacro");
				setTimeout(function() {
					finishCreateSupReturnWoutOrder(listProductSelected);
					Loading.hide("loadingMacro");
				}, 500);
			});
		}).on("stepclick", function(e) {
			// prevent clicking on steps
		});
	};

	var finishCreateSupReturnWoutOrder = function(listProductSelected) {
		var supplier = $("#toPartyId").val();
		var currencyUomId = $("#currencyUomIdDes").val();
		var description = $("#description").val();
		createNewReturnSupplier(supplier, currencyUomId, description, listProductSelected);
	};

	function showConfirmPage() {
		var supSelectedId = $("#toPartyId").val();
		$("#supplierIdDT").text($("#supplierId").val());
		var currencyUomIdSelected = $("#currencyUomIdDes").val();
		$("#currencyUomIdDT").text($("#currencyUomId").val());
//		var currencyUomId = $("#currencyUomId").jqxDropDownList("val");
//		for (var i = 0; i < currencyUomData.length; i++) {
//			var obj = currencyUomData[i];
//			if (obj.uomId == currencyUomId) {
//				$("#currencyUomIdDT").text(obj.description);
//			}
//		}
		if ($("#description").val()) {
			$("#descriptionDT").text($("#description").val());
		}
		var tmpSource = $("#jqxgridProductSelected").jqxGrid("source");
		if (typeof (tmpSource) != "undefined") {
			tmpSource._source.localdata = listProductSelected;
			$("#jqxgridProductSelected").jqxGrid("source", tmpSource);
		}
	}

	var createNewReturnSupplier = function(supplier, currencyUomId, description, listProductSelected) {
		for (x in listProductSelected){
			delete listProductSelected[x]["productName"];
			delete listProductSelected[x]["itemDescription"];
		}
		var dataMap = OlbReturnWithoutOrderInfo.getValue();
		dataMap.listProduct = JSON.stringify(listProductSelected);
		$.ajax({
			beforeSend : function() {
				$("#loader_page_common").show();
			},
			complete : function() {
				$("#loader_page_common").hide();
			},
			url : "createReturnSupplierWithoutOrder",
			type : "POST",
			data : dataMap,
			dataType : "json",
			success: function(data){
				jOlbUtil.processResultDataAjax(data, "default", function(data){
					$('#container').empty();
		        	$('#jqxNotification').jqxNotification({ template: 'info'});
		        	$("#jqxNotification").html(uiLabelMap.CreateSuccess);
		        	$("#jqxNotification").jqxNotification("open");
		        	var returnId = data.returnId;
		        	if (OlbCore.isNotEmpty(returnId)) {
		        		window.location.href = "viewGeneralReturnSupplier?returnId=" + returnId;
		        	}
				});
			},
			}).done(function(data) {
		});
	};
	var initValidateForm = function() {
		var mapRules = [];
		var extendRules = [];
	};

	return {
		init : init,
	};
}());