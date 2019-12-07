$(function(){
	OlbShippingAdrNewPopup.init();
});
var OlbShippingAdrNewPopup = (function(){
	var countryGeoCBB;
	var stateProvinceGeoCBB;
	var districtGeoCBB;
	var wardGeoCBB;
	var allowSolicitationDDL;
	
	var init = function(){
		initOther();
	};
	var initOther = function(){
		initElement();
		initElementComplex();
		initEvent();
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
			selectedIndex: 0,
			displayDetail: false,
			dropDownHorizontalAlignment: 'right',
		};
		allowSolicitationDDL = new OlbDropDownList($("#wn_allowSolicitation"), localYN, configYN, []);
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
	};
	return {
		init: init,
		initOther: initOther,
	};
}());