var OlbShippingAdrNewPopup = (function(){
	var countryGeoCBB;
	var stateProvinceGeoCBB;
	var districtGeoCBB;
	var wardGeoCBB;
	var allowSolicitationDDL;
	var isPrimaryLocationDDL;
	
	var init = function(){
		initWindow();
		initOther();
	};
	var initOther = function(){
		initElement();
		initElementComplex();
		initEvent();
		initValidateForm();
	};
	var initWindow = function(){
		jOlbUtil.windowPopup.create($("#alterpopupWindowContactMechNew"), {width: 540, height: 480, cancelButton: $("#alterCancel")});
	};
	var initElement = function(){
		jOlbUtil.input.create("#wn_toName", {maxLength: 100});
		jOlbUtil.input.create("#wn_attnName", {maxLength: 100});
		jOlbUtil.input.create("#wn_address1", {maxLength: 255});
		jOlbUtil.input.create("#wn_postalCode", {maxLength: 60});
		
		jOlbUtil.notification.create($("#containerContactMech"), $("#jqxNotificationContactMech"));
	};
	var initElementComplex = function(){
		var configCountry = {
    		width: "99%", 
    		height: 25, 
    		key: "geoId", 
    		value: "geoName", 
    		displayDetail: true,
    		placeHolder: uiLabelMap.BSClickToChoose,
    		useUrl: true, url: "getListCountryGeo",
    		datafields: [{name: 'geoId'}, {name: 'geoName'}],
	        autoComplete: true, searchMode: 'containsignorecase', renderer: null, renderSelectedItem: null, 
	        selectedIndex: 0, 
    	};
		countryGeoCBB = new OlbComboBox($("#wn_countryGeoId"), null, configCountry, [dataContactNew.countryGeoId]);
	    
	    var initDropDownGeoState = function(elementObj, sname, selectArr){
	    	var url = "jqxGeneralServicer?sname=" + sname;
	    	var configGeo = {
	    		width: "99%", 
	    		height: 25, 
	    		key: "geoId", 
	    		value: "geoName", 
	    		displayDetail: true,
	    		placeHolder: uiLabelMap.BSClickToChoose,
	    		useUrl: true, url: url,
	    		datafields: [{name: 'geoId'}, {name: 'geoName'}],
		        autoComplete: true, searchMode: 'containsignorecase', renderer: null, renderSelectedItem: null,
		        selectedIndex: 0, 
	    	};
	    	return new OlbComboBox(elementObj, null, configGeo, selectArr);
	    };
	    
	    var urlDropDownStateProvince = 'JQGetAssociatedStateListGeo';
	    if (dataContactNew.countryGeoId) {
	    	urlDropDownStateProvince += "&geoId=" + dataContactNew.countryGeoId + "&pagesize=0&pagenum=1";
	    }
	    stateProvinceGeoCBB = initDropDownGeoState($("#wn_stateProvinceGeoId"), urlDropDownStateProvince, [dataContactNew.stateProvinceGeoId]);
	    districtGeoCBB = initDropDownGeoState($("#wn_countyGeoId"), 'JQGetAssociatedStateOtherListGeo&pagesize=0&geoId=' + dataContactNew.stateProvinceGeoId);
	    wardGeoCBB = initDropDownGeoState($("#wn_wardGeoId"), 'JQGetAssociatedStateOtherListGeo&pagesize=0');
	    
	    var localYN = [{id: 'Y', description: uiLabelMap.BSYes}, {id: 'N', description: uiLabelMap.BSNo}];
		var configYN = {
			width: '99%',
			placeHolder: uiLabelMap.BSClickToChoose,
			key: 'id',
			value: 'description',
			autoDropDownHeight: true,
			selectedIndex: 1,
			displayDetail: false,
			dropDownHorizontalAlignment: 'right',
		};
		allowSolicitationDDL = new OlbDropDownList($("#wn_allowSolicitation"), localYN, configYN, []);
		isPrimaryLocationDDL = new OlbDropDownList($("#wn_isPrimaryLocation"), localYN, configYN, []);
	};
	var initEvent = function(){
		$('#wn_countryGeoId').on('change', function (event){
			getAssociatedState($('#wn_stateProvinceGeoId'), event);
		});
		$('#wn_stateProvinceGeoId').on('change', function (event){
		    getAssociatedState($('#wn_countyGeoId'), event);
		});
		$('#wn_countyGeoId').on('change', function (event){
		    getAssociatedState($('#wn_wardGeoId'), event);
		});
		
		var getAssociatedState = function(comboBoxObj, event) {
			var args = event.args;
		    if (args) {
			    var item = args.item;
			    if (item) {
			    	var geoId = item.value;
			    	if (geoId) {
			    		var tmpSource = $(comboBoxObj).jqxComboBox('source');
						if(typeof(tmpSource) != 'undefined'){
							tmpSource._source.url = "jqxGeneralServicer?sname=JQGetAssociatedStateOtherListGeo&geoId=" + geoId + "&pagesize=0&pagenum=1";
							$(comboBoxObj).jqxComboBox('clearSelection');
							$(comboBoxObj).jqxComboBox('source', tmpSource);
							$(comboBoxObj).jqxComboBox("selectIndex", 0);
						}
			    	}
			    }
			}
		};
		
		$('#alterSave').on('click', function(){
			if(!$("#alterpopupWindowContactMechNew").jqxValidator('validate')) return false;
			jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCreate, 
					function() {
		            	var m_countryGeoId = countryGeoCBB.getValue();
		            	var m_stateProvinceGeoId = stateProvinceGeoCBB.getValue();
		            	var m_countyGeoId = districtGeoCBB.getValue();
		            	var m_wardGeoId = wardGeoCBB.getValue();
		            	var data = {
							partyId : 				typeof($('#wn_partyId').val()) != 'undefined' ? $('#wn_partyId').val() : null,
							contactMechTypeId : 	typeof($('#wn_contactMechTypeId').val()) != 'undefined' ? $('#wn_contactMechTypeId').val() : null,
							contactMechPurposeTypeId: typeof($('#wn_contactMechPurposeTypeId').val()) != 'undefined' ? $('#wn_contactMechPurposeTypeId').val() : null,
							toName: 				typeof($('#wn_toName').val()) != 'undefined' ? $('#wn_toName').val() : null,
							attnName: 				typeof($('#wn_attnName').val()) != 'undefined' ? $('#wn_attnName').val() : null,
							countryGeoId: 			typeof(m_countryGeoId) != 'undefined' ? m_countryGeoId : null,
							stateProvinceGeoId: 	typeof(m_stateProvinceGeoId) != 'undefined' ? m_stateProvinceGeoId : null,
							districtGeoId: 			typeof(m_countyGeoId) != 'undefined' ? m_countyGeoId : null,
							wardGeoId: 				typeof(m_wardGeoId) != 'undefined' ? m_wardGeoId : null,
							address1: 				typeof($('#wn_address1').val()) != 'undefined' ? $('#wn_address1').val() : null,
							postalCode: 			typeof($('#wn_postalCode').val()) != 'undefined' ? $('#wn_postalCode').val() : null,
							allowSolicitation: 		typeof($('#wn_allowSolicitation').val()) != 'undefined' ? $('#wn_allowSolicitation').val() : null,
							isPrimaryLocation: 		typeof($('#wn_isPrimaryLocation').val()) != 'undefined' ? $('#wn_isPrimaryLocation').val() : null,
						};
						$.ajax({
		                    type: "POST",
		                    url: "createPostalAddressShippingForParty",
		                    data: data,
		                    beforeSend: function(){
		                        $("#loader_page_common_nctm").show();
		                    }, 
		                    success: function(data){
		                        var resultValue = jOlbUtil.processResultDataAjax(data, "ContactMech", "default");
		                        if (resultValue && typeof(data.contactMechId) != 'undefined') {
		                        	$('body').trigger('createContactmechComplete', [data.contactMechId]);
		                        	clearWindowData();
		                        }
		                    },
		                    error: function(){
		                        alert("Send to server is false!");
		                    },
		                    complete: function(){
		                    	$("#loader_page_common_nctm").hide();
		                    }
		                });
		            }
			);
		});
	};
	var clearWindowData = function(){
		$("#wn_toName").jqxInput("val", null);
		$("#wn_attnName").jqxInput("val", null);
		countryGeoCBB.selectItem(null, 0);
		stateProvinceGeoCBB.selectItem(null, 0);
		districtGeoCBB.selectItem(null, 0);
		wardGeoCBB.selectItem(null, 0);
		$("#wn_address1").jqxInput("val", null);
		$("#wn_postalCode").jqxInput("val", null);
		allowSolicitationDDL.selectItem(null, 0);
		isPrimaryLocationDDL.selectItem(null, 0);
	};
	var initValidateForm = function(){
   		new OlbValidator($('#alterpopupWindowContactMechNew'), [
	        {input: '#wn_countryGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
	        {input: '#wn_stateProvinceGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
	        {input: '#wn_countyGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
	        {input: '#wn_wardGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
	        {input: '#wn_address1', type: 'validInputNotNull'},
	    ]);
	};
	return {
		init: init,
		initWindow: initWindow,
		initOther: initOther,
	};
}());
$(function(){
    OlbShippingAdrNewPopup.initWindow();
});