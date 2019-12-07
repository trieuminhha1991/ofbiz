var supplierFinAccountObj = (function(){
	var init = function(){
		initContextMenu();
		$("#jqxNotificationjqxgirdSupplierFinAcc").jqxNotification({ width: "100%", 
			appendContainer: "#containerjqxgirdSupplierFinAcc", opacity: 0.9, template: "info" });
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 150)
		$('#contextMenu').on('itemclick', function(event){
			var args = event.args;
			var boundIndex = $("#jqxgirdSupplierFinAcc").jqxGrid('getselectedrowindex');
			var data = $("#jqxgirdSupplierFinAcc").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("id");
			if(action == "deactiveFinAccount"){
				bootbox.dialog(uiLabelMap.DeactiveSupplierFinAccountConfirm,
						[
						 {
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								updateFinAccount(data.finAccountId, "FNACT_MANFROZEN");
							}
						},
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
			}else if(action == "activeFinAccount"){
				bootbox.dialog(uiLabelMap.ActiveSupplierFinAccountConfirm,
						[
						 {
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								updateFinAccount(data.finAccountId, "FNACT_ACTIVE");
							}
						},
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
			}
		});
		$('#contextMenu').on('shown', function(event){
			var rowindex = $("#jqxgirdSupplierFinAcc").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgirdSupplierFinAcc").jqxGrid('getrowdata', rowindex);
			var statusId = dataRecord.statusId;
			if(statusId == "FNACT_ACTIVE"){
				$(this).jqxMenu('disable', "activeFinAccount", true);
				$(this).jqxMenu('disable', "deactiveFinAccount", false);
			}else if(statusId == "FNACT_MANFROZEN"){
				$(this).jqxMenu('disable', "activeFinAccount", false);
				$(this).jqxMenu('disable', "deactiveFinAccount", true);
			}
		});
	};
	var updateFinAccount = function(finAccountId, statusId){
		var data = {finAccountId: finAccountId, statusId: statusId};
		Loading.show('loadingMacro');
		$.ajax({
			url: "updateSupplierFinAccount",
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgirdSupplierFinAcc', response.successMessage, {template : 'success', appendContainer : '#containerjqxgirdSupplierFinAcc'});
					$("#jqxgirdSupplierFinAcc").jqxGrid('updatebounddata');
				}else{
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);			
				}
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var createJqxMenu = function(id, itemHeight, width, config){
		var liElement = $("#" + id + ">ul>li").length;
		var contextMenuHeight = itemHeight * liElement;
		if(typeof(config) == 'undefined'){
			config = {};
		}
		config.width = width;
		config.height = contextMenuHeight;
		config.autoOpenPopup = false;
		config.mode = "popup";
		$("#" + id).jqxMenu(config);
	};
	return{
		init: init
	}
}());


var createSupplierFinAccountObj = (function(){
	var init = function(){
		initInput();
		initComboBox();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#addFinAccountCode").jqxInput({width: '95%', height: 20});
		$("#addFinAccountName").jqxInput({width: '95%', height: 20});
		$("#useAccountCheck").jqxCheckBox({width: '90%', height: 25});
	};
	var initWindow = function(){
		$("#AddSupplierFinAccountWindow").jqxWindow({showCollapseButton: false, width: 420, height: 300, theme:'olbius', 
			autoOpen: false, isModal: true, maxWidth: 1000,})
	};
	var initValidator = function(){
		$("#AddSupplierFinAccountWindow").jqxValidator({
			rules: [
				{ input: '#addFinAccountCode', message: uiLabelMap.validFieldRequire, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
				{ input: '#addFinAccountName', message: uiLabelMap.validFieldRequire, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
				{ input: '#addFinAccountState', message: uiLabelMap.validFieldRequire, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
				{ input: '#addFinAccountCountry', message: uiLabelMap.validFieldRequire, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
	        ]
		});
	};
	var initEvent = function(){
		$("#AddSupplierFinAccountWindow").on('open', function(){
			$("#useAccountCheck").jqxCheckBox({checked: true});
			if(typeof(globalVar.defaultCountryGeoId) != "undefined"){
				$("#addFinAccountCountry").val(globalVar.defaultCountryGeoId);
			}
		});
		$("#AddSupplierFinAccountWindow").on('close', function(){
			Grid.clearForm($("#AddSupplierFinAccountWindow"));
		});
		$("#cancelAddSupplierFinAcc").click(function(){
			$("#AddSupplierFinAccountWindow").jqxWindow('close');
		});
		$("#saveAddSupplierFinAcc").click(function(){
			var valid = $("#AddSupplierFinAccountWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateSupplierFinAccountConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createFinAccount();
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		initEventComboboxGeo("PROVINCE", "addFinAccountCountry", "addFinAccountState", "", "COUNTRY");
	};
	var createFinAccount = function(){
		var data = {
				partyId: globalVar.partyId, 
				finAccountName: $("#addFinAccountName").val(), 
				finAccountCode: $("#addFinAccountCode").val(), 
				stateProvinceGeoId: $("#addFinAccountState").val(),
				countryGeoId: $("#addFinAccountCountry").val(),
		};
		data.useAccount = $("#useAccountCheck").jqxCheckBox('checked')? "Y" : "N";
		Loading.show('loadingMacro');
		$.ajax({
			url: "createSupplierFinAccount",
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgirdSupplierFinAcc', response.successMessage, {template : 'success', appendContainer : '#containerjqxgirdSupplierFinAcc'});
					$("#jqxgirdSupplierFinAcc").jqxGrid('updatebounddata');
					$("#AddSupplierFinAccountWindow").jqxWindow('close');
				}else{
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);			
				}
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var initComboBox = function(){
		initComboboxGeo("", "PROVINCE", "addFinAccountState");
		initComboboxGeo("", "COUNTRY", "addFinAccountCountry");
	};
	var initEventComboboxGeo = function (geoTypeId, element, elementAffected, elementParents, thisGeoTypeId) {
		$("#" + element).on("change", function (event) {
			var args = event.args;
			if (args) {
				var index = args.index;
				var item = args.item;
				if (item) {
					var label = item.label;
					var value = item.value;
					if (elementAffected && value) {
						initComboboxGeo(value, geoTypeId, elementAffected);
					}
				}
			}
		});
	};
	var initComboboxGeo = function(geoId, geoTypeId, element) {
		var url = "";
		if(geoTypeId != "COUNTRY" && geoId){
			url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId=" + geoId;
		}else if(geoTypeId == "COUNTRY"){
			url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId;
		}
		var source =
		{
			datatype: "json",
			datafields: [
				{ name: "geoId" },
				{ name: "geoName" }
			],
			url: url,
			cache: true
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#" + element).jqxComboBox({ source: dataAdapter, theme: theme, displayMember: "geoName", valueMember: "geoId",
			width: '97%', height: 25});
	};
	return{
		init: init
	}
}());

$(document).ready(function () {
	supplierFinAccountObj.init();
	createSupplierFinAccountObj.init();
});