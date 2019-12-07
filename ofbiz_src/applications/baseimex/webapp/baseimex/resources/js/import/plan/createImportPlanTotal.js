$(function(){
	PlanTotalObj.init();
});
var PlanTotalObj = (function() {
	var btnClick = false;
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				// check form valid
				$('#containerNotify').empty();
				
				var check = true;
				var validate = $('#AddPlan').jqxValidator('validate');
				if(!validate){
					return false;
				}
				if (typeof (ObjProduct) != 'undefined'){
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		ObjProduct.loadDataForGrid();
		            	Loading.hide('loadingMacro');
	            	}, 500);
				}
			} else if(info.step == 2 && (info.direction == "next")) {
				$.each(listProductSelected, function(i){
	   				var olb = listProductSelected[i];
	   				if (olb.quantity > 0){
	   					check = false;
	   				}
	   			});
				if (check || listProductSelected.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
					return false;
				}
				showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureSave, function() {
				if (!btnClick){
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		finishCreateAgreement();
		            	Loading.hide('loadingMacro');
	            	}, 500);
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
		$("#productPlanCodeDT").text($("#productPlanCode").val());
		$("#productPlanNameDT").text($("#productPlanName").val());
		$("#descriptionDT").text($("#description").val());
		if (partySelected){
			$("#partyDT").text(partySelected.partyCode + " - " + partySelected.groupName);
		}
//		if ($("#customTimePeriod").jqxDropDownList('getSelectedItem')){
//			$("#customTimePeriodDT").text($("#customTimePeriod").jqxDropDownList('getSelectedItem').label);
//		}
		if (customTimePeriod) {
			$("#customTimePeriodDT").text(customTimePeriod.periodName);
		}
		if ($("#currencyUomId").jqxDropDownList('getSelectedItem')){
			$("#currencyUomIdDT").text($("#currencyUomId").jqxDropDownList('getSelectedItem').label);
		}
		
		if ($("#tableProduct").length > 0){
			var table = document.getElementById('tableProduct');
			table.deleteTHead();
			var header = table.createTHead();
			header.insertRow(0);
			var tr = document.getElementById('tableProduct').tHead.children[0];
			
			th1 = document.createElement('th');
			th1.innerHTML = uiLabelMap.SequenceId;
			tr.appendChild(th1);
			
			th2 = document.createElement('th');
			th2.innerHTML = uiLabelMap.ProductId;
			tr.appendChild(th2);
			
			th3 = document.createElement('th');
			th3.innerHTML = uiLabelMap.ProductName;
			tr.appendChild(th3);
			
			th4 = document.createElement('th');
			th4.innerHTML = uiLabelMap.Unit;
			tr.appendChild(th4);
			
			for (var i in listPeriods){
				var x = listPeriods[i];
				th = document.createElement('th');
				th.innerHTML = x.periodName;
				tr.appendChild(th);
			}
			
			$('#tableProduct tbody').empty();
			var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];

			for (var i in listProductSelected){
				var product = listProductSelected[i];
				
				var newRow = tableRef.insertRow(tableRef.rows.length);
				var newCell0 = newRow.insertCell(0);
				var u = parseInt(i) + 1;
				var newText = document.createTextNode(u);
				newCell0.appendChild(newText);
				
				var newCell1 = newRow.insertCell(1);
				newText = document.createTextNode(product.productCode);
				newCell1.appendChild(newText);
				
				var newCell2 = newRow.insertCell(2);
				newText = document.createTextNode(product.productName);
				newCell2.appendChild(newText);
				
				var newCell3 = newRow.insertCell(3);
				newText = document.createTextNode(getUomDesc(product.uomId));
				newCell3.appendChild(newText);
				
				var listData = product.data;
				var cellNum = 4;
				for (var m in listPeriods){
					var period = listPeriods[m];
					for (var n in listData){
						var data = listData[n];
						if (period.customTimePeriodId == data.periodId){
							var newCell = newRow.insertCell(cellNum);
							newCell.className = 'align-right';
							newText = document.createTextNode(formatnumber(data.OrderQuantity));
							newCell.appendChild(newText);
							cellNum++;
						}
					}
				}
			}
		}
	}
	
	function finishCreateAgreement(){
		var productPlanCode = $("#productPlanCode").val();
		var productPlanName = $("#productPlanName").val();
		var description = $("#description").val();
		
		var customTimePeriodId = null;
//		var customTimePeriod = $('#customTimePeriod').jqxDropDownList('getSelectedItem');
		if (customTimePeriod) {
			customTimePeriodId = customTimePeriod.customTimePeriodId;
		}
		var currencyUomId = null;
		var currency = $('#currencyUomId').jqxDropDownList('getSelectedItem');
		if (currency) {
			currencyUomId = currency.value;
		}
		var supplierPartyId = null;
		if (partySelected){
			supplierPartyId = partySelected.partyId;
		}
    	
		var listProducts = [];
		for (var i in listProductSelected){
			let pr = listProductSelected[i];
			let productId = pr.productId;
			let uomId = pr.uomId;
			let lists = pr.data;
			for (var j in lists){
				let data = lists[j];
				let periodId = data.periodId;
				let planQuantity = data.OrderQuantity;
				let inventoryForecast = data.EndInventory;
				let map = {
						"productId": productId,
						"uomId": uomId,
						"customTimePeriodId": periodId,
						"planQuantity": planQuantity,
						"inventoryForecast": inventoryForecast,
				}
				listProducts.push(map);
			}
		}
		listProducts = JSON.stringify(listProducts);
    	$.ajax({	
			 type: "POST",
			 url: "createImportPlan",
			 data: {
				 salesForecastId: salesForecastId, 
				 customTimePeriodId: customTimePeriodId, 
				 supplierPartyId: supplierPartyId, 
				 currencyUomId: currencyUomId, 
				 productPlanName: productPlanName, 
				 productPlanCode: productPlanCode, 
				 description: description, 
				 productPlanTypeId: "IMPORT_PLAN", 
				 statusId: "IMPORT_PLAN_CREATED",
				 listProducts: listProducts,
			 },
			 dataType: "json",
			 async: false,
			 success: function(data){
				 if (data._ERROR_MESSAGE_ != undefined && data._ERROR_MESSAGE_ != null) {
					 jOlbUtil.alert.error(uiLabelMap.UpdateError + ". " + data._ERROR_MESSAGE_);
					 Loading.hide("loadingMacro");
					 return false;
				 }
				 var productPlanId = data.productPlanId;
				 viewDetailPlan(productPlanId);
			 },
 		}).done(function(data) {
  		});
	}
	
	function viewDetailPlan(productPlanId){
		 window.location.href = "listImExPlanItem?productPlanId="+productPlanId;
	}
	
	var initValidateForm = function(){
		
	};
	
	return {
		init: init,
		viewDetailPlan: viewDetailPlan,
	}
}());