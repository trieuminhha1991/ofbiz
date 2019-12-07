<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	<#assign requirementTypeIds = ["TRANSFER_REQUIREMENT", "BORROW_REQUIREMENT", "PAY_REQUIREMENT", "CHANGEDATE_REQUIREMENT"]/>
	<#assign requirementTypeList = delegator.findList("RequirementType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, requirementTypeIds), null, null, null, false)! />
	var requirementTypeData = [
	   	<#if requirementTypeList?exists>
	   	<#list requirementTypeList as item>
	   		{	requirementTypeId: "${item.requirementTypeId?if_exists}",
	   			description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
	   		},
	   	</#list>
	   	</#if>
	];
	<#--
	<#assign enumTypeIds = ["TRANSFER_REASON"]/>
	<#assign reasonEnums = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, enumTypeIds), null, null, null, false)>
	<#assign reasonEnums = delegator.findByAnd("Enumeration", {"enumId", "TRANS_DISTRIBUTOR"}, null, false)>
	var reasonEnumData = [
  	   	<#if reasonEnums?exists>
   		<#list reasonEnums as item>
   			{	enumId: "${item.enumId?if_exists}",
  	   			description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
   			},
   		</#list>
  	   	</#if>
  	];-->
	
	
	if (!uiLabelMap) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.BSCustomerId = "${StringUtil.wrapString(uiLabelMap.BSCustomerId)}";
	uiLabelMap.BSFullName = "${StringUtil.wrapString(uiLabelMap.BSFullName)}";
	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
	uiLabelMap.BLFacilityName = "${StringUtil.wrapString(uiLabelMap.BLFacilityName)}";
	uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';
	uiLabelMap.BSContactMechId = '${StringUtil.wrapString(uiLabelMap.BSContactMechId)}';
	uiLabelMap.BSReceiverName = '${StringUtil.wrapString(uiLabelMap.BSReceiverName)}';
	uiLabelMap.BSOtherInfo = '${StringUtil.wrapString(uiLabelMap.BSOtherInfo)}';
	uiLabelMap.BSAddress = '${StringUtil.wrapString(uiLabelMap.BSAddress)}';
	uiLabelMap.BSCity = '${StringUtil.wrapString(uiLabelMap.BSCity)}';
	uiLabelMap.BSStateProvince = '${StringUtil.wrapString(uiLabelMap.BSStateProvince)}';
	uiLabelMap.BSCountry = '${StringUtil.wrapString(uiLabelMap.BSCountry)}';
	uiLabelMap.BSCounty = '${StringUtil.wrapString(uiLabelMap.BSCounty)}';
	uiLabelMap.BSWard = '${StringUtil.wrapString(uiLabelMap.BSWard)}';
	uiLabelMap.validFieldRequire = '${StringUtil.wrapString(uiLabelMap.validFieldRequire)}';
</script>
<script type="text/javascript">
	$(function(){
		OlbReqSalesTransfer.init();
	});
	var OlbReqSalesTransfer = (function(){
		var requirementTypeDDL;
		var reasonEnumDDL;
		var customerDDB;
		var customerContactMechDDB;
		var facilityDDB;
		var destFacilityDDL;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initValidateForm();
			initEvent();
		};
		var initElement = function(){
			//jOlbUtil.dateTimeInput.create("#requiredByDate", {width: '100%', allowNullDate: true, value: null, disabled: true});
			jOlbUtil.dateTimeInput.create("#requirementStartDate", {width: '100%', allowNullDate: true, value: null});
			//$('#requiredByDate').jqxDateTimeInput('setDate', new Date());
		};
		var initElementComplex = function(){
			var configRequirementType = {
				width: '100%',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				key: 'requirementTypeId',
				value: 'description',
				autoDropDownHeight: true,
			}
			requirementTypeDDL = new OlbDropDownList($("#requirementTypeId"), requirementTypeData, configRequirementType, []);
			
			var configReasonEnum = {
				width: '100%',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: true,
				url: '',
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true,
				selectedIndex: 0
			}
			reasonEnumDDL = new OlbDropDownList($("#reasonEnumId"), null, configReasonEnum, []);
			
			var configDistributor = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
				columns: [
					{text: uiLabelMap.BSCustomerId, datafield: 'partyCode', width: '30%'},
					{text: uiLabelMap.BSFullName, datafield: 'fullName', width: '70%'}
				],
				url: 'JQGetCustomersBySeller',
				useUtilFunc: true,
				
				key: 'partyId',
				description: ['fullName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
				dropDownHorizontalAlignment: 'left'
			};
			customerDDB = new OlbDropDownButton($("#customerId"), $("#customerGrid"), null, configDistributor, []);
			
			var configCustomerAddress = {
				widthButton: '100%',
				width: 800,
				dropDownHorizontalAlignment: 'left',
				datafields: [
					{name: 'contactMechId', type: 'string'},
					{name: 'toName', type: 'string'},
					{name: 'attnName', type: 'string'},
					{name: 'address1', type: 'string'},
					{name: 'city', type: 'string'},
					{name: 'stateProvinceGeoId', type: 'string'},
					{name: 'stateProvinceGeoName', type: 'string'},
					{name: 'postalCode', type: 'string'},
					{name: 'countryGeoId', type: 'string'},
					{name: 'countryGeoName', type: 'string'},
					{name: 'districtGeoId', type: 'string'},
					{name: 'districtGeoName', type: 'string'},
					{name: 'wardGeoId', type: 'string'},
					{name: 'wardGeoName', type: 'string'},
				],
				columns: [
					{text: uiLabelMap.BSContactMechId, datafield: 'contactMechId', width: '100px'},
					{text: uiLabelMap.BSReceiverName, datafield: 'toName', width: '140px'},
					{text: uiLabelMap.BSOtherInfo, datafield: 'attnName', width: '140px'},
					{text: uiLabelMap.BSAddress, datafield: 'address1', width: '25%'},
					{text: uiLabelMap.BSWard, datafield: 'wardGeoName', width: '20%'},
					{text: uiLabelMap.BSCounty, datafield: 'districtGeoName', width: '120px'},
					{text: uiLabelMap.BSStateProvince, datafield: 'stateProvinceGeoName', width: '100px'},
					{text: uiLabelMap.BSCountry, datafield: 'countryGeoName', width: '100px'},
				],
				useUrl: true,
				root: 'results',
				url: '',
				useUtilFunc: true,
				selectedIndex: 0,
				key: 'contactMechId',
				description: ['toName', 'attnName', 'address1', 'wardGeoName', 'districtGeoName', 'city', 'countryGeoId'],
				autoCloseDropDown: false,
				filterable: true,
				sortable: true,
			};
			customerAddressDDB = new OlbDropDownButton($("#customerContactMechId"), $("#customerContactMechGrid"), null, configCustomerAddress, []);
			
			var configDestFacility = {
				width: '100%',
				//placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: true,
				url: '',
				key: 'facilityId',
				value: 'facilityName',
				autoDropDownHeight: true,
			}
			destFacilityDDL = new OlbDropDownList($("#destFacilityId"), null, configDestFacility, []);
			
			var configFacilityDDB = {
	        	useUrl: true,
	        	root: 'results',
	        	widthButton: '100%',
	        	showdefaultloadelement: false,
	        	autoshowloadelement: false,
	       	 	datafields: [{ name: 'facilityId', type: 'string' }, { name: 'facilityCode', type: 'string' }, { name: 'facilityName', type: 'string' }],
	        	columns: [
	            	{ text: uiLabelMap.BLFacilityId, datafield: 'facilityCode', width: '30%' },
	            	{ text: uiLabelMap.BLFacilityName, datafield: 'facilityName', width: '70%' }
	        	],
	        	url: '',
	        	useUtilFunc: true,
	        	key: 'facilityId',
	        	keyCode: 'facilityCode',
	        	description: ['facilityName'],
	        	autoCloseDropDown: true,
	        	filterable: true,
	        	sortable: true,
	        	dropDownHorizontalAlignment: 'right'
    		};
    		facilityDDB = new OlbDropDownButton($("#facilityIdDDB"), $("#facilityIdGrid"), null, configFacilityDDB, []);
			
		};
		var initEvent = function(){
			customerDDB.getGrid().rowSelectListener(function(rowData){
		    	var customerPartyId = rowData['partyId'];
				if (customerPartyId) {
					var serviceNameDestFac = "JQGetListFacilityByAdmin";
					var requirementTypeId = requirementTypeDDL.getValue();
					
					customerAddressDDB.updateSource("jqxGeneralServicer?sname=JQGetShippingAddressByPartyReceive&partyId="+customerPartyId, null, function(){
						customerAddressDDB.selectItem(null, 0);
					});
					
					if ("PAY_REQUIREMENT" == requirementTypeId) {
						serviceNameDestFac = "JQGetListFacilityByOwner";
						facilityDDB.updateSource("jqxGeneralServicer?sname=jqGetFacilities&excludePartyId="+ customerPartyId, null, function(){});
					} else if ("BORROW_REQUIREMENT" == requirementTypeId) {
						facilityDDB.updateSource("jqxGeneralServicer?sname=JQGetListFacilityByOwner&partyId=" + customerPartyId, null, function(){
							facilityDDB.selectItem(null, 0);
						});
					} else if ("TRANSFER_REQUIREMENT" == requirementTypeId) {
						facilityDDB.updateSource("jqxGeneralServicer?sname=jqGetFacilities&primaryFacilityGroupId=FACILITY_INTERNAL&facilityTypeId=WAREHOUSE", null, function(){
							facilityDDB.selectItem(null, 0);
						});
					}
					destFacilityDDL.updateSource("jqxGeneralServicer?sname=" + serviceNameDestFac + "&partyId=" + customerPartyId, null, function(){
						destFacilityDDL.selectItem(null, 0);
					});
				}
		    });
		    requirementTypeDDL.selectListener(function(itemData, index){
		    	var value = itemData.value;
		    	if (value) {
		    		reasonEnumDDL.updateSource("jqxGeneralServicer?sname=JQGetListReasonEnumReqForSales&requirementTypeId=" + value, null, function(){
						reasonEnumDDL.selectItem(null, 0);
					});
					customerAddressDDB.clearAll();
					customerDDB.clearAll();
					facilityDDB.clearAll();
					destFacilityDDL.clearAll();
					
					if (value == "CHANGEDATE_REQUIREMENT") {
						var parentFacilityObj = $("#facilityIdDDB").closest(".row-fluid");
						if (parentFacilityObj) parentFacilityObj.hide();
						var parentDestFacilityObj = $("#destFacilityId").closest(".row-fluid");
						if (parentDestFacilityObj) parentDestFacilityObj.hide();
						
						if (parentFacilityObj) parentFacilityObj.find("label").removeClass("required");
						if (parentDestFacilityObj) parentFacilityObj.find("label").removeClass("required");
					} else {
						var parentFacilityObj = $("#facilityIdDDB").closest(".row-fluid");
						if (parentFacilityObj) parentFacilityObj.show();
						var parentDestFacilityObj = $("#destFacilityId").closest(".row-fluid");
						if (parentDestFacilityObj) parentDestFacilityObj.show();
						
						if (value == "PAY_REQUIREMENT") {
							$("#facility").css("display","none");
							$("#facilityDDB").css("display","block");
							
							if (parentFacilityObj) parentFacilityObj.find("label").addClass("required");
						} else {
							$("#facility").css("display","block");
							$("#facilityDDB").css("display","none");
							
							if (parentFacilityObj) parentFacilityObj.find("label").removeClass("required");
						}
						if (parentDestFacilityObj) parentDestFacilityObj.find("label").addClass("required");
					}
					
		    	}
		    });
		    requirementTypeDDL.selectItem(["CHANGEDATE_REQUIREMENT"], null);
		};
		var initValidateForm = function(){
			var mapRules = [
				{input: '#requirementTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#reasonEnumId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#customerId', type: 'validInputNotNull'},
				//{input: '#requiredByDate', type: 'validDateTimeInputNotNull'},
				{input: '#requirementStartDate', type: 'validDateTimeInputNotNull'},
				{input: '#requirementStartDate', type: 'validDateCompareToday'},
				//{input: '#requiredByDate, #requirementStartDate', type: 'validCompareTwoDate', paramId1 : "requiredByDate", paramId2 : "requirementStartDate"},
		    ];
		    var extendRules = [
		    	{input: '#facilityIdDDB', message: uiLabelMap.validFieldRequire, action: 'close', 
					rule: function(input, commit){
						var requirementTypeId = requirementTypeDDL.getValue();
						if ('PAY_REQUIREMENT' == requirementTypeId) {
							return OlbValidatorUtil.validElement(input, commit, 'validObjectNotNull', {objType: 'dropDownButton'});
						} else {
							return true;
						}
					}
				},
		    	{input: '#destFacilityId', message: uiLabelMap.validFieldRequire, action: 'close', 
					rule: function(input, commit){
						var requirementTypeId = requirementTypeDDL.getValue();
						if ('CHANGEDATE_REQUIREMENT' != requirementTypeId) {
							return OlbValidatorUtil.validElement(input, commit, 'validObjectNotNull', {objType: 'dropDownList'});
						} else {
							return true;
						}
					}
				},
		    ];
			validatorVAL = new OlbValidator($('#initRequirementEntry'), mapRules, extendRules, {position: 'bottom', scroll: true});
		};
		var getValidator = function() {
			return validatorVAL;
		};
		var getObs = function() {
			return {
				requirementTypeDDL: requirementTypeDDL,
				reasonEnumDDL: reasonEnumDDL,
				customerDDB: customerDDB,
				customerAddressDDB: customerAddressDDB,
				destFacilityDDL: destFacilityDDL,
				facilityDDB: facilityDDB,
			}
		};
		
		return {
			init: init,
			getValidator: getValidator,
			getObs: getObs,
		};
	}());
</script>