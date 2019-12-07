<@jqOlbCoreLib />

<script type="text/javascript">
	<#assign countryGeoId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCountryGeo(delegator)!/>
	<#assign organizationCurrent = Static['com.olbius.basesales.util.SalesUtil'].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	var dataContactNew = {};
	dataContactNew.countryGeoId = <#if countryGeoId?exists>'${countryGeoId}'<#else>null</#if>;
	dataContactNew.stateProvinceGeoId = "<#if organizationCurrent?exists && "MN" == organizationCurrent>VNM-HCM<#else>VNM-HN2</#if>";
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.BSYes = "${StringUtil.wrapString(uiLabelMap.BSYes)}";
	uiLabelMap.BSNo = "${StringUtil.wrapString(uiLabelMap.BSNo)}";
	uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.validFieldRequire = "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}";
	
	jOlbUtil.setUiLabelMap("wgupdatesuccess", uiLabelMap.wgupdatesuccess);
	
	$(function(){
		OlbShippingAdressNew${prefixName}.init();
	});
	
	var OlbShippingAdressNew${prefixName} = (function(){
		var countryGeoCBB;
		var stateProvinceGeoCBB;
		var districtGeoCBB;
		var wardGeoCBB;
		var allowSolicitationDDL;
		var prefixName = "#${prefixName}";
		
		var init = function(){
			initOther();
		};
		var initOther = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.input.create(prefixName + "_toName", {maxLength: 100});
			jOlbUtil.input.create(prefixName + "_attnName", {maxLength: 100});
			jOlbUtil.input.create(prefixName + "_address1", {maxLength: 255});
			jOlbUtil.input.create(prefixName + "_postalCode", {maxLength: 60});
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
			countryGeoCBB = new OlbComboBox($(prefixName + "_countryGeoId"), null, configCountry, [dataContactNew.countryGeoId]);
		    
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
		    stateProvinceGeoCBB = initDropDownGeoState($(prefixName + "_stateProvinceGeoId"), urlDropDownStateProvince, [dataContactNew.stateProvinceGeoId]);
		    wardGeoCBB = initDropDownGeoState($(prefixName + "_wardGeoId"), 'JQGetAssociatedStateOtherListGeo&pagesize=0');
		    setTimeout(function(){
		    	districtGeoCBB = initDropDownGeoState($(prefixName + "_countyGeoId"), 'JQGetAssociatedStateOtherListGeo&pagesize=0&geoId=' + dataContactNew.stateProvinceGeoId);
		    }, 200);
		    
		    
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
			allowSolicitationDDL = new OlbDropDownList($(prefixName + "_allowSolicitation"), localYN, configYN, []);
		};
		var initEvent = function(){
			$(prefixName + '_countryGeoId').on('change', function (event){
				getAssociatedState($(prefixName + '_stateProvinceGeoId'), event);
			});
			$(prefixName + '_stateProvinceGeoId').on('change', function (event){
			    getAssociatedState($(prefixName + '_countyGeoId'), event);
			});
			$(prefixName + '_countyGeoId').on('change', function (event){
			    getAssociatedState($(prefixName + '_wardGeoId'), event);
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
			$(prefixName + "_toName").jqxInput("val", null);
			$(prefixName + "_attnName").jqxInput("val", null);
			countryGeoCBB.selectItem(null, 0);
			stateProvinceGeoCBB.selectItem(null, 0);
			districtGeoCBB.selectItem(null, 0);
			wardGeoCBB.selectItem(null, 0);
			$(prefixName + "_address1").jqxInput("val", null);
			$(prefixName + "_postalCode").jqxInput("val", null);
			allowSolicitationDDL.selectItem(null, 0);
		};
		return {
			init: init,
			initOther: initOther,
		};
	}());
</script>