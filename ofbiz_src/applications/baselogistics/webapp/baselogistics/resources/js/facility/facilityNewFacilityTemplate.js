$(function(){
	FacilityTemplateObj.init();
});
var FacilityTemplateObj = (function() {
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
			if(info.step == 1) {
				if (info.direction == "next") {
					var resultValidate = !FacilityInfoObj.getValidator().validate();
					if(resultValidate) return false;
				} else if (info.direction == "previous"){
					FacilityInfoObj.getValidator().hide();
				}
			} else if(info.step == 2) {
				if (info.direction == "next") {
					var resultValidate = !FacilityRoleObj.getValidator().validate();
					if(resultValidate) return false;
				} else if (info.direction == "previous"){
					FacilityRoleObj.getValidator().hide();
				}
			} else if(info.step == 3) {
				if (info.direction == "next") {
					var resultValidate = !FacilityAddressObj.getValidator().validate();
					if(resultValidate) return false;
				} else if (info.direction == "previous"){
					FacilityAddressObj.getValidator().hide();
				}
				showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		finishCreateFacility();
	            	Loading.hide('loadingMacro');
            	}, 500);
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	
	function showConfirmPage(){
		$("#facilityNameDT").text($("#facilityName").val());
		$("#facilityCodeDT").text($("#facilityCode").val());
		var x = $("#requireLocation").jqxDropDownList('val');
		if (x == 'Y'){
			$("#requireLocationDT").text(uiLabelMap.LogYes);
		} else {
			$("#requireLocationDT").text(uiLabelMap.LogNO);
		}
		var x = $("#requireDate").jqxDropDownList('val');
		if (x == 'Y'){
			$("#requireDateDT").text(uiLabelMap.LogYes);
		} else {
			$("#requireDateDT").text(uiLabelMap.LogNO);
		}
		
		for (var i = 0; i < facilityData.length; i ++){
			if (facilityData[i].facilityId == $("#parentFacilityId").val()){
				$("#parentFacilityIdDT").text(facilityData[i].facilityName);
			}
		}
		
		var productStoreItems = $("#productStoreId").jqxComboBox('getCheckedItems');
    	var productStoreIds = [];
    	var stores = null;
    	for (var j = 0; j < productStoreItems.length; j ++){
    		var storeName = null;
    		for (var i = 0; i < prodStoreData.length; i ++){
    			if (prodStoreData[i].productStoreId == productStoreItems[j].value){
    				storeName = prodStoreData[i].description;
    			}
    		}
    		if (stores == null){
        		stores = storeName;
    		} else {
    			stores = stores + " | " + storeName;
    		}
    	}
    	
		$("#productStoreIdDT").text(stores);
		$("#openedDateDT").text($("#openedDate").val());
		
		var size = $("#facilitySizeAdd").val();
		var sizeUomId = $("#facilitySizeUomId").jqxDropDownList('val');
		var desc = null;
		for (var i = 0; i < areaUomData.length; i ++){
			if (areaUomData[i].uomId == sizeUomId){
				desc = areaUomData[i].description;
			}
		}
		$("#facilitySizeDT").text(formatnumber(size) + ' (' + desc + ')');
		 
		var dataResourceName = null;
		if ($('#imagesPath')[0]){
    		var file = $('#imagesPath')[0].files[0];
    		if (file){
    			dataResourceName = file.name;
	    		if (dataResourceName.length > 50){
					bootbox.dialog(uiLabelMap.NameOfImagesMustBeLessThan50Character, [{
		                "label" : uiLabelMap.OK,
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
		            return false;
				}
    			var folder = "/baseLogistics/facility";
				var form_data= new FormData();
				form_data.append("uploadedFile", file);
				form_data.append("folder", folder);
				jQuery.ajax({
					url: "uploadImages",
					type: "POST",
					async: false,
					data: form_data,
					cache : false,
					contentType : false,
					processData : false,
					
					success: function(res) {
						path = res.path;
			        }
				}).done(function() {
				});
    		}
		}
		if (path != null){
			var tmp = dataResourceName;
			if (dataResourceName.length > 20){
				tmp = dataResourceName.substring(0, 15) + ' ... .' + dataResourceName.substring(dataResourceName.length - 3, dataResourceName.length);
			}
			$('#imagesDT').html("");
			$('#imagesDT').append("<a href='"+path+"' title='"+dataResourceName+"' onclick='' target='_blank'><i class='fa-file-image-o'></i>"+tmp+"</a>");
		}
		
		$("#descriptionDT").text($("#description").val());
		var item1 = $("#countryGeoId").jqxDropDownList('getSelectedItem');
		$("#countryGeoIdDT").text(item1.label);
		
		$("#provinceGeoIdDT").text($("#provinceGeoId").label);
		var item2 = $("#provinceGeoId").jqxDropDownList('getSelectedItem');
		$("#provinceGeoIdDT").text(item2.label);
		
		$("#districtGeoIdDT").text($("#districtGeoId").jqxDropDownList('val'));
		var item3 = $("#districtGeoId").jqxDropDownList('getSelectedItem');
		$("#districtGeoIdDT").text(item3.label);
		
		$("#wardGeoIdDT").text($("#wardGeoId").jqxDropDownList('val'));
		var item4 = $("#wardGeoId").jqxDropDownList('getSelectedItem');
		$("#wardGeoIdDT").text(item4.label);
		
		$("#addressDT").text($("#address").val());
		$("#ownerPartyIdDT").text($("#ownerPartyId").jqxComboBox('getSelectedItem').label);
		
		$("#fromDateDT").text($("#fromDate").val());
		$("#thruDateDT").text($("#thruDate").val());
		
		var rows = $('#managerPartyId').jqxGrid('getselectedrowindexes');
		if (listStoreKeeperParty.length > 0) {
			var text = "";
			rows = $("#managerPartyId").jqxGrid('getselectedrowindexes');
			for (var x in rows){
				var data = $("#managerPartyId").jqxGrid('getrowdata', rows[x]);
				text = text + data.fullName + ' [' +data.employeePartyCode +']</br>';
			}
			$("#managerPartyIdDT").html(text);
		}
		
		$("#fromDateManagerDT").text($("#fromDateManager").val());
		$("#thruDateManagerDT").text($("#thruDateManager").val());
	}
	
	function finishCreateFacility(){
    	var row;
    	var fromDate;
    	var thruDate;
    	var fromDateManager;
    	var thruDateManager;
    	var openedDate;
    	if ($('#fromDate').jqxDateTimeInput('getDate')){
    		fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime();
    	}
    	if ($('#thruDate').jqxDateTimeInput('getDate')){
    		thruDate = $('#thruDate').jqxDateTimeInput('getDate').getTime();
    	}
    	if ($('#fromDateManager').jqxDateTimeInput('getDate')){
    		fromDateManager = $('#fromDateManager').jqxDateTimeInput('getDate').getTime();
    	}
    	if ($('#thruDateManager').jqxDateTimeInput('getDate')){
    		thruDateManager = $('#thruDateManager').jqxDateTimeInput('getDate').getTime();
    	}
    	if ($('#openedDate').jqxDateTimeInput('getDate')){
    		openedDate = $('#openedDate').jqxDateTimeInput('getDate').getTime();
    	}
    	
    	var productStoreItems = $("#productStoreId").jqxComboBox('getCheckedItems');
    	var productStoreIds = [];
    	for (var j = 0; j < productStoreItems.length; j ++){
    		var item = {};
    		item["productStoreId"] = productStoreItems[j].value; 
    		productStoreIds.push(item);
    	}
    	productStoreIds = JSON.stringify(productStoreIds);
    	var listStoreKeepers = JSON.stringify(listStoreKeeperParty);
        row = { 
    		fromDate: fromDate,
    		fromDateManager: fromDateManager,
    		ownerPartyId:$('#ownerPartyId').val(),
    		listStoreKeepers: listStoreKeepers,
    		facilityName:$('#facilityName').jqxInput('val').split('\n').join(' '),
    		facilityCode:$('#facilityCode').jqxInput('val'),
    		parentFacilityId:$('#parentFacilityId').val(),
    		facilitySize:$('#facilitySizeAdd').val(),
    		facilitySizeUomId:$('#facilitySizeUomId').val(),
    		facilityTypeId: 'WAREHOUSE',
    		periodTypeId:$('#periodTypeId').val(),
    		thruDate: thruDate,            
    		thruDateManager: thruDateManager,
    		countryGeoId: $('#countryGeoId').val(),
    		provinceGeoId: $('#provinceGeoId').val(),
    		districtGeoId: $('#districtGeoId').val(),
    		requireLocation: $('#requireLocation').val(),
    		requireDate: $('#requireDate').val(),
    		wardGeoId: $('#wardGeoId').val(),
    		phoneNumber: $('#phoneNumber').val(),
    		address: $('#address').jqxInput('val').split('\n').join(' '),
    		description: $('#description').jqxInput('val').split('\n').join(' '),
    		openedDate: openedDate,
    		imagesPath: path,
    		listProductStoreId: productStoreIds,
        };
		
		$.ajax({
			type: 'POST',
			url: 'updateFacility',
			async: false,
			data: row,
			beforeSend: function(){
				$("#btnPrevWizard").addClass("disabled");
				$("#btnNextWizard").addClass("disabled");
				$("#loader_page_common").show();
			},
			success: function(data){
				if (data._ERROR_MESSAGE_ || data._ERROR_MESSAGE_LIST_) {
					if (data._ERROR_MESSAGE_ === uiLabelMap.BLNotifyFacilityCodeExists) {
						jOlbUtil.alert.error(data._ERROR_MESSAGE_);
					} else {
						jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
					}
    				return false;
    			}
				viewFacilityDetail(data.facilityId);
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
	
	function viewFacilityDetail(facilityId){
		window.location.href = 'detailFacility?facilityId=' + facilityId;
	}
	var initValidateForm = function(){
		
	};
	var reloadPages = function(){
		window.location.reload();
	};
	return {
		init: init,
		viewFacilityDetail: viewFacilityDetail,
		reloadPages: reloadPages,
	}
}());