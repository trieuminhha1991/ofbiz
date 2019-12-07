$(function(){
	ReqTemplateObj.init();
});
var ReqTemplateObj = (function() {
	var btnClick = false;
	var confirmClick = false;
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		var listProductRelate = [];
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				// check form valid
				$('#containerNotify').empty();
				var resultValidate = !ReqInfoObj.getValidator().validate();
				if(resultValidate) return false;
				if (listProductSelected.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
					return false;
				}
				showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
				if (!btnClick){
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		finishCreateRequirement();
		            	Loading.hide('loadingMacro');
	            	}, 300);
	            	btnClick = true;
				} 
            }, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
            	btnClick = false;
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	function showConfirmPage(){
		if (requirementTypeId){
			$("#requirementTypeDT").text(getReqTypeDesc(requirementTypeId));
		}
		if (reasonEnumId){
			$("#reasonEnumDT").text($("#reasonEnumId").text());
		}
		if (originFacilitySelected){
			if (originFacilitySelected.facilityCode){
				$("#facilityDT").text("[" + originFacilitySelected.facilityCode + "] " + originFacilitySelected.facilityName);
			} else {
				$("#facilityDT").text("[" + originFacilitySelected.facilityId + "] " + originFacilitySelected.facilityName);
			}
		}
		if (destFacilitySelected){
			if (destFacilitySelected.facilityCode){
				$("#facilityToDT").text("[" + destFacilitySelected.facilityCode + "] " + destFacilitySelected.facilityName);
			} else {
				$("#facilityToDT").text("[" + destFacilitySelected.facilityId + "] " + destFacilitySelected.facilityName);
			}
		}
		$("#requirementStartDateDT").text(DatetimeUtilObj.formatToMinutes(new Date($("#requirementStartDate").jqxDateTimeInput('getDate').getTime())));
		$("#descriptionDT").text($("#description").jqxInput('val'));
		
		if ($("#tableProduct").length > 0){
			var totalValue = 0;
			$('#tableProduct tbody').empty();
			var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];
			for (var i in listProductSelected){
				var data = listProductSelected[i];
				var newRow = tableRef.insertRow(tableRef.rows.length);
				var newCell0 = newRow.insertCell(0);
				var newText = document.createTextNode(parseInt(i)+1);
				newCell0.appendChild(newText);
				
				var newCell1 = newRow.insertCell(1);
				newText = document.createTextNode(data.productCode);
				newCell1.appendChild(newText);
				
				var newCell2 = newRow.insertCell(2);
				newText = document.createTextNode(data.productName);
				newCell2.appendChild(newText);
				
				var newCell3 = newRow.insertCell(3);
				if (data.comment){
					newText = document.createTextNode(data.comment);
				} else {
					newText = document.createTextNode("");
				}
				newCell3.appendChild(newText);
				
				var newCell4 = newRow.insertCell(4);
				newCell4.className = 'align-right';
				if (data.requireAmount && data.requireAmount == 'Y' && data.amountUomTypeId == 'WEIGHT_MEASURE') {
					newText = document.createTextNode(data.amountOnHandTotal + " (" + getUomDesc(data.weightUomId) + ")");
				} else {
					newText = document.createTextNode(data.quantityOnHandTotal + " (" + getUomDesc(data.quantityUomId) + ")");
				}
				newCell4.appendChild(newText);
				
				var newCell5 = newRow.insertCell(5);
				newText = document.createTextNode(getUomDesc(data.uomId));
				newCell5.appendChild(newText);
				
				var newCell6 = newRow.insertCell(6);
				newCell6.className = 'align-right';
				newText = document.createTextNode(formatnumber(data.quantity));
				newCell6.appendChild(newText);
				
				if (!hidePrice){
					var newCell7 = newRow.insertCell(7);
					newCell7.className = 'align-right';
					newText = document.createTextNode(formatnumber(data.unitCost));
					newCell7.appendChild(newText);
					
					var newCell8 = newRow.insertCell(8);
					newCell8.className = 'align-right';
					if (data.unitCost && data.quantity){
						var u = data.unitCost;
						if (locale && locale === 'vi' && typeof(u) === 'string'){
							u = u.toString().replace('.', '');
							u = u.replace(',', '.');
						}
						var lastPrice = parseFloat(u.toString());
						
						var v = data.quantity;
						if (locale && locale === 'vi' && typeof(v) === 'string'){
							v = v.toString().replace('.', '');
							v = v.replace(',', '.');
						}
						var quantity = parseFloat(v.toString());
						
						value = lastPrice*parseFloat(quantity);
						totalValue = totalValue + value;
						if (value) {
							newText = document.createTextNode(formatnumber(value));
						} 
						newCell8.appendChild(newText);
					}
				}
			}
			if (!hidePrice) {
				if (totalValue){
					var newRowTotal = tableRef.insertRow(tableRef.rows.length);
					var newCellTotal0 = newRowTotal.insertCell(0);
					newCellTotal0.colSpan = 8;
					newCellTotal0.className = 'align-right';
					newCellTotal0.style.fontWeight="bold";
					newCellTotal0.style.background="#f2f2f2";
					var str = uiLabelMap.OrderItemsSubTotal.toUpperCase();
					var newTextTotal = document.createTextNode(str);
					newCellTotal0.appendChild(newTextTotal);
					
					var newCellTotal8 = newRowTotal.insertCell(1);
					newCellTotal8.className = 'align-right';
					newCellTotal8.style.background="#f2f2f2";
					var newTextTotal = document.createTextNode(formatnumber(totalValue));
					newCellTotal8.appendChild(newTextTotal);
				}
			}
		}
	}
	
	function finishCreateRequirement(){
		var listProducts = [];
		if (listProductSelected != undefined && listProductSelected.length > 0){
			for (var i = 0; i < listProductSelected.length; i ++){
				var data = listProductSelected[i];
				var map = {};
		   		map['productId'] = data.productId;
		   		map['quantity'] = data.quantity;
		   		if (data.unitCost != null && data.unitCost != undefined && data.unitCost != ''){
		   			map['unitCost'] = data.unitCost;
		   		} else {
		   			map['unitCost'] = 0;
		   		}
		   		map['uomId'] = data.uomId;
		   		if (data.comment){
		   			map['description'] = data.comment;
		   		}
		        listProducts.push(map);
			}
		}
		var listProducts = JSON.stringify(listProducts);
		var dataMap = {
			listProducts: listProducts,
			facilityId: originFacilitySelected.facilityId,
			requirementTypeId: requirementTypeId,
			reasonEnumId: reasonEnumId,
			requirementStartDate: $("#requirementStartDate").jqxDateTimeInput('getDate').getTime(),
			description: $("#description").jqxInput('val').split('\n').join(' '),
		};
		if (destFacilitySelected){
			dataMap.destFacilityId = destFacilitySelected.facilityId;
		}
		
		$.ajax({
			type: 'POST',
			url: 'createNewRequirement',
			async: false,
			data: dataMap,
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
		});
	}
	
	function viewRequirementDetail(requirementId){
		window.location.href = 'viewRequirementDetail?requirementId=' + requirementId;
	}
	var initValidateForm = function(){
		
	};
	
	var validateData = function (){
		if ($('#reasonEnumId').jqxDropDownList('val') == "RETURN_OUT_OF_DATE_REQ") {
			for ( var x in listProductSelected) {
				if (!(listProductSelected[x].expiredDate)) {
					jOlbUtil.alert.error(uiLabelMap.DetectProductNotHaveExpiredDate);
					return false;
				}
			}
		}
		if ($("#requirementTypeId").jqxDropDownList('val') == "COMBINE_PRODUCT"){
			if ($('#reasonEnumId').jqxDropDownList('val') == "STAMP"){
				listProductRelate = [];
				var listProductJson = JSON.stringify(listProductSelected);
				var resultTmp = [];
				$.ajax({
					type: 'POST',
					url: 'getProductRelateds',
					async: false,
					data: {
						listProducts: listProductJson,
						facilityId: $('#originFacilityId').jqxDropDownList('val'),
					},
					success: function(data){
						listProductRelate = data.listProductRelateds;
					},
					error: function(data){
					},
					complete: function(data){
					},
				});
			
				if (listProductRelate == undefined || listProductRelate.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.ProductNotCongfigCombine);
					return false;
				} else {
					var listPrHasConfig = [];
					for (var i = 0; i < listProductRelate.length; i ++){
						var listPrOrigins = listProductRelate[i]["listOriginProductIds"];
						for (var j = 0; j < listPrOrigins.length; j ++){
							var check = false;
							for (var k = 0; k < listPrHasConfig.length; k ++){
								if (listPrOrigins[j] == listPrHasConfig[k]){
									check = true;
									break;
								}
							}
							if (!check){
								listPrHasConfig.push(listPrOrigins[j]);	
							}
						}
					}
					var listPrNotHasConfig = [];
					for (var i = 0; i < listProductSelected.length; i ++){
						var check = true;
						for (var j = 0; j < listPrHasConfig.length; j ++){
							if (listPrHasConfig[j] == listProductSelected[i].productId){
								check = false;
								break;
							}
						}
						if (check){
							listPrNotHasConfig.push(listProductSelected[i].productId);
						}
					}
					if (listPrNotHasConfig.length > 0){
						var des = '';
						for (var j = 0; j < listPrNotHasConfig.length; j ++){
							des = des + listPrNotHasConfig[j] + '; ';
						}
						jOlbUtil.alert.error(uiLabelMap.ProductNotCongfigCombine);
						return false;
					}
				}
			} 
		} else if (($("#requirementTypeId").jqxDropDownList('val') == "EXPORT_REQUIREMENT") && ($('#reasonEnumId').jqxDropDownList('val') == "EXPORT_AGGREGATED")){
			listProductRelate = [];
			var listProductJson = JSON.stringify(listProductSelected);
			var resultTmp = [];
			var existedNotConfig = false;
			$.ajax({
				type: 'POST',
				url: 'getProductConfigRelateds',
				async: false,
				data: {
					listProducts: listProductJson,
					facilityId: $('#originFacilityId').jqxDropDownList('val'),
				},
				success: function(data){
					listProductRelate = data.listProductRelateds;
					existedNotConfig = data.existedNotConfig;
				},
				error: function(data){
				},
				complete: function(data){
				},
			});
		
			if (listProductRelate == undefined || listProductRelate.length <= 0 || (existedNotConfig != undefined && existedNotConfig == true)){
				jOlbUtil.alert.error(uiLabelMap.ProductNotCongfigAggregated);
				return false;
			} 
		}
	}
	
	return {
		init: init,
	}
}());