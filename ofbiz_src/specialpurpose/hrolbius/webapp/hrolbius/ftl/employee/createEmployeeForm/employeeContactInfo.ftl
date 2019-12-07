<#include "component://hrolbius/webapp/hrolbius/ftl/recruitment/jqxEditContact.ftl">
<script type="text/javascript">
	//Prepare Country Geo
	<#assign countrylList = delegator.findByAnd("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY"), null, false)>
	var countryData = new Array();
	<#list countrylList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['description'] = "${description}";
		countryData[${item_index}] = row;
	</#list>
	
	//Prepare Province/City Geo
	<#assign provincelList = delegator.findList("GeoAssocAndGeoToDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["CITY", "PROVINCE"]), null, null, null, false)>
	var provinceData = new Array();
	<#list provincelList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['geoIdFrom'] = '${item.geoIdFrom}';
		row['description'] = "${description}";
		provinceData[${item_index}] = row;
	</#list>
	
	//Prepare District Geo
	<#assign districtlList = delegator.findByAnd("GeoAssocAndGeoToDetail", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "DISTRICT"), null, false)>
	var districtData = new Array();
	<#list districtlList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['geoIdFrom'] = '${item.geoIdFrom}';
		row['description'] = "${description}";
		districtData[${item_index}] = row;
	</#list>
	
	//Prepare Ward Geo
	<#assign wardList = delegator.findByAnd("GeoAssocAndGeoToDetail", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "WARD"), null, false)>
	var wardData = new Array();
	<#list wardList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['geoIdFrom'] = '${item.geoIdFrom}';
		row['description'] = "${description}";
		wardData[${item_index}] = row;
	</#list>
	$(document).ready(function () {
		var theme = 'olbius';
		/******************************************************Contact Information**************************************************************************/
		//Create homeTel
		$("#homeTel").jqxInput({width: 195, theme: theme});
		
		//Create diffTel
		$("#diffTel").jqxInput({width: 195, theme: theme});
		
		//Create mobile
		$("#mobile").jqxInput({width: 195, theme: theme});
		
		//Create email
		$("#email").jqxInput({width: 195, theme: theme});
		
		//Create prAddress
		$("#prAddress").jqxInput({width: 195, theme: theme});
		
		//Create prCountry
		var prCountryIndex = 0;
		for(var i = 0; i < countryData.length; i++){
			if('VNM' == countryData[i].geoId){
				prCountryIndex = i;
				break;
			}
		}
		$("#prCountry").jqxDropDownList({selectedIndex: prCountryIndex, source: countryData, valueMember: 'geoId', displayMember:'description', theme: theme});
		//Handle on change prCountry
		$('#prCountry').on('change', function (event){    
			    var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	
			    	//Create Province
			    	var prProvinceData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < provinceData.length; i++){
			    		if(value == provinceData[i].geoIdFrom){
			    			prProvinceData[index] = provinceData[i];
			    			index++;
			    		}
			    	}
			    	$("#prProvince").jqxDropDownList({source: prProvinceData});
			    	$("#prProvince").jqxDropDownList({selectedIndex: 0}); 
			    	
			    	//Create district
			    	var prDistrictData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < districtData.length; i++){
			    		if(prProvinceData[0] && prProvinceData[0].geoId == districtData[i].geoIdFrom){
			    			prDistrictData[index] = districtData[i];
			    			index++;
			    		}
			    	}
			    	$("#prDistrict").jqxDropDownList({source: prDistrictData});
			    	$("#prDistrict").jqxDropDownList({selectedIndex: 0});
			    	//Create ward
			    	var prWardData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < wardData.length; i++){
			    		if(prDistrictData[0] && prDistrictData[0].geoId == wardData[i].geoIdFrom){
			    			prWardData[index] = wardData[i];
			    			index++;
			    		}
			    	}
			    	$("#prWard").jqxDropDownList({source: prWardData});
			    	$("#prWard").jqxDropDownList({selectedIndex: 0});
		    	} 
		});
		
		//Create prProvince
		var prProvinceData = new Array();
		var index = 0;
		for(var i = 0; i < provinceData.length; i++){
			if('VNM' == provinceData[i].geoIdFrom){
				prProvinceData[index] = provinceData[i];
				index++;
			}
		}
		$("#prProvince").jqxDropDownList({selectedIndex: 0, source: prProvinceData, valueMember: 'geoId', displayMember:'description', theme: theme});
		//Handle on change prProvince
		$('#prProvince').on('change', function (event){    
			    var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	
			    	//Create district
			    	var prDistrictData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < districtData.length; i++){
			    		if(value == districtData[i].geoIdFrom){
			    			prDistrictData[index] = districtData[i];
			    			index++;
			    		}
			    	}
			    	$("#prDistrict").jqxDropDownList({source: prDistrictData});
			    	$("#prDistrict").jqxDropDownList({selectedIndex: 0});
			    	//Create ward
			    	var prWardData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < wardData.length; i++){
			    		if(prDistrictData[0] && prDistrictData[0].geoId == wardData[i].geoIdFrom){
			    			prWardData[index] = wardData[i];
			    			index++;
			    		}
			    	}
			    	$("#prWard").jqxDropDownList({source: prWardData});
			    	$("#prWard").jqxDropDownList({selectedIndex: 0});
		    	} 
		});
		//Create prDistrict
		var prDistrictData = new Array();
		var index = 0;
		for(var i = 0; i < districtData.length; i++){
			if(prProvinceData[0].geoId == districtData[i].geoIdFrom){
				prDistrictData[index] = districtData[i];
				index++;
			}
		}
		$("#prDistrict").jqxDropDownList({selectedIndex: 0 ,source: prDistrictData, valueMember: 'geoId', displayMember:'description', theme: theme});
		//Handle on change prDistrict
		$('#prDistrict').on('change', function (event){    
			    var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	
			    	//Create ward
			    	var prWardData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < wardData.length; i++){
			    		if(value == wardData[i].geoIdFrom){
			    			prWardData[index] = wardData[i];
			    			index++;
			    		}
			    	}
			    	$("#prWard").jqxDropDownList({source: prWardData});
			    	$("#prWard").jqxDropDownList({selectedIndex: 0});
		    	} 
		});
		//Create prWard
		var prWardData = new Array();
		var index = 0;
		for(var i = 0; i < wardData.length; i++){
			if(prDistrictData[0].geoId == wardData[i].geoIdFrom){
				prWardData[index] = wardData[i];
				index++;
			}
		}
		$("#prWard").jqxDropDownList({selectedIndex: 0,source: prWardData, valueMember: 'geoId', displayMember:'description', theme: theme});
		
		//Create crAddress
		$("#crAddress").jqxInput({width: 195, theme: theme});
		
		//Create crCountry
		var crCountryIndex = 0;
		for(var i = 0; i < countryData.length; i++){
			if('VNM' == countryData[i].geoId){
				crCountryIndex = i;
				break;
			}
		}
		$("#crCountry").jqxDropDownList({selectedIndex: crCountryIndex,source: countryData, valueMember: 'geoId', displayMember:'description', theme: theme});
		//Handle on change crCountry
		$('#crCountry').on('change', function (event){    
			    var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	
			    	//Create Province
			    	var crProvinceData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < provinceData.length; i++){
			    		if(value == provinceData[i].geoIdFrom){
			    			crProvinceData[index] = provinceData[i];
			    			index++;
			    		}
			    	}
			    	$("#crProvince").jqxDropDownList({source: crProvinceData});
			    	$("#crProvince").jqxDropDownList({selectedIndex: 0}); 
			    	
			    	//Create district
			    	var crDistrictData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < districtData.length; i++){
			    		if(crProvinceData[0] && crProvinceData[0].geoId == districtData[i].geoIdFrom){
			    			crDistrictData[index] = districtData[i];
			    			index++;
			    		}
			    	}
			    	$("#crDistrict").jqxDropDownList({source: crDistrictData});
			    	$("#crDistrict").jqxDropDownList({selectedIndex: 0});
			    	//Create ward
			    	var crWardData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < wardData.length; i++){
			    		if(crDistrictData[0] && crDistrictData[0].geoId == wardData[i].geoIdFrom){
			    			crWardData[index] = wardData[i];
			    			index++;
			    		}
			    	}
			    	$("#crWard").jqxDropDownList({source: crWardData});
			    	$("#crWard").jqxDropDownList({selectedIndex: 0});
		    	} 
		});
		//Create crProvince
		var crProvinceData = new Array();
		var index = 0;
		for(var i = 0; i < provinceData.length; i++){
			if('VNM' == provinceData[i].geoIdFrom){
				crProvinceData[index] = provinceData[i];
				index++;
			}
		}
		$("#crProvince").jqxDropDownList({selectedIndex: 0, source: crProvinceData, valueMember: 'geoId', displayMember:'description', theme: theme});
		//Handle on change crProvince
		$('#crProvince').on('change', function (event){    
			    var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	
			    	//Create district
			    	var crDistrictData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < districtData.length; i++){
			    		if(value == districtData[i].geoIdFrom){
			    			crDistrictData[index] = districtData[i];
			    			index++;
			    		}
			    	}
			    	$("#crDistrict").jqxDropDownList({source: crDistrictData});
			    	$("#crDistrict").jqxDropDownList({selectedIndex: 0});
			    	//Create ward
			    	var crWardData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < wardData.length; i++){
			    		if(crDistrictData[0] && crDistrictData[0].geoId == wardData[i].geoIdFrom){
			    			crWardData[index] = wardData[i];
			    			index++;
			    		}
			    	}
			    	$("#crWard").jqxDropDownList({source: crWardData});
			    	$("#crWard").jqxDropDownList({selectedIndex: 0});
		    	} 
		});
		//Create crDistrict
		var crDistrictData = new Array();
		var index = 0;
		for(var i = 0; i < districtData.length; i++){
			if(crProvinceData[0].geoId == districtData[i].geoIdFrom){
				crDistrictData[index] = districtData[i];
				index++;
			}
		}
		$("#crDistrict").jqxDropDownList({selectedIndex: 0, source: crDistrictData, valueMember: 'geoId', displayMember:'description', theme: theme});
		//Handle on change crDistrict
		$('#crDistrict').on('change', function (event){    
			    var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	
			    	//Create ward
			    	var crWardData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < wardData.length; i++){
			    		if(value == wardData[i].geoIdFrom){
			    			crWardData[index] = wardData[i];
			    			index++;
			    		}
			    	}
			    	$("#crWard").jqxDropDownList({source: crWardData});
			    	$("#crWard").jqxDropDownList({selectedIndex: 0});
		    	} 
		});
		//Create crWard
		var crWardData = new Array();
		var index = 0;
		for(var i = 0; i < wardData.length; i++){
			if(crDistrictData[0].geoId == wardData[i].geoIdFrom){
				crWardData[index] = wardData[i];
				index++;
			}
		}
		$("#crWard").jqxDropDownList({selectedIndex: 0, source: crWardData, valueMember: 'geoId', displayMember:'description', theme: theme});
		
		$("#createNewContact").on('validationSuccess', function (event) {
			$("#jqxTabs").jqxTabs('enableAt', 2);
		});
		
		$("#createNewContact").jqxValidator({
			rules: [
				{input: '#mobile', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
				{input: '#prAddress', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
				{input: '#crAddress', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
				{input: '#mobile', message: '${uiLabelMap.NumberFieldRequired}', action: 'keyup, blur',
					rule: function (input, commit) {
	                    if (isFinite(input.val())) {
	                        return true;
	                    }
	                    return false;
	                }
				},
				{input: '#homeTel', message: '${uiLabelMap.NumberFieldRequired}', action: 'keyup, blur',
					rule: function (input, commit) {
	                    if (isFinite(input.val())) {
	                        return true;
	                    }
	                    return false;
	                }
				},
				{input: '#diffTel', message: '${uiLabelMap.NumberFieldRequired}', action: 'keyup, blur',
					rule: function (input, commit) {
	                    if (isFinite(input.val())) {
	                        return true;
	                    }
	                    return false;
	                }
				},
				{input: '#email', message: '${uiLabelMap.EmailFieldRequired}', action: 'keyup, blur', rule: 'email'}
			]
		});
		
		//Handle click copy-next
		$(".copy-next").click(function(){
			//Copy Address
			var prAddress = $('#prAddress').val();
			$('#crAddress').val(prAddress);
			
			//Copy country geo
			var items = $("#prCountry").jqxDropDownList('getItems');
			var cpyCountryData =  new Array();
			for(var i = 0; i < items.length; i++){
				var row = {};
				row['geoId'] = items[i].value;
				row['description'] = items[i].label;
				cpyCountryData[i] = row;
			}
			var index = $("#prCountry").jqxDropDownList('getSelectedIndex'); 
			$("#crCountry").jqxDropDownList({source: cpyCountryData});
			$("#crCountry").jqxDropDownList({selectedIndex: index});
			
			//Copy province geo
			var items = $("#prProvince").jqxDropDownList('getItems');
			var cpyProvinceData =  new Array();
			for(var i = 0; i < items.length; i++){
				var row = {};
				row['geoId'] = items[i].value;
				row['description'] = items[i].label;
				cpyProvinceData[i] = row;
			}
			var index = $("#prProvince").jqxDropDownList('getSelectedIndex'); 
			$("#crProvince").jqxDropDownList({source: cpyProvinceData});
			$("#crProvince").jqxDropDownList({selectedIndex: index});
			
			//Copy district geo
			var items = $("#prDistrict").jqxDropDownList('getItems');
			var cpyDistrictData =  new Array();
			for(var i = 0; i < items.length; i++){
				var row = {};
				row['geoId'] = items[i].value;
				row['description'] = items[i].label;
				cpyDistrictData[i] = row;
			}
			var index = $("#prDistrict").jqxDropDownList('getSelectedIndex'); 
			$("#crDistrict").jqxDropDownList({source: cpyDistrictData});
			$("#crDistrict").jqxDropDownList({selectedIndex: index});
			
			//Copy ward geo
			var items = $("#prWard").jqxDropDownList('getItems');
			var cpyWardData =  new Array();
			for(var i = 0; i < items.length; i++){
				var row = {};
				row['geoId'] = items[i].value;
				row['description'] = items[i].label;
				cpyWardData[i] = row;
			}
			var index = $("#prWard").jqxDropDownList('getSelectedIndex'); 
			$("#crWard").jqxDropDownList({source: cpyWardData});
			$("#crWard").jqxDropDownList({selectedIndex: index});
		});
		
		//Handle click copy-back
		$(".copy-back").click(function(){
			//Copy Address
			var crAddress = $('#crAddress').val();
			$('#prAddress').val(crAddress);
			
			//Copy country geo
			var items = $("#crCountry").jqxDropDownList('getItems');
			var cpyCountryData =  new Array();
			for(var i = 0; i < items.length; i++){
				var row = {};
				row['geoId'] = items[i].value;
				row['description'] = items[i].label;
				cpyCountryData[i] = row;
			}
			var index = $("#crCountry").jqxDropDownList('getSelectedIndex'); 
			$("#prCountry").jqxDropDownList({source: cpyCountryData});
			$("#prCountry").jqxDropDownList({selectedIndex: index});
			
			//Copy province geo
			var items = $("#crProvince").jqxDropDownList('getItems');
			var cpyProvinceData =  new Array();
			for(var i = 0; i < items.length; i++){
				var row = {};
				row['geoId'] = items[i].value;
				row['description'] = items[i].label;
				cpyProvinceData[i] = row;
			}
			var index = $("#crProvince").jqxDropDownList('getSelectedIndex'); 
			$("#prProvince").jqxDropDownList({source: cpyProvinceData});
			$("#prProvince").jqxDropDownList({selectedIndex: index});
			
			//Copy district geo
			var items = $("#crDistrict").jqxDropDownList('getItems');
			var cpyDistrictData =  new Array();
			for(var i = 0; i < items.length; i++){
				var row = {};
				row['geoId'] = items[i].value;
				row['description'] = items[i].label;
				cpyDistrictData[i] = row;
			}
			var index = $("#crDistrict").jqxDropDownList('getSelectedIndex'); 
			$("#prDistrict").jqxDropDownList({source: cpyDistrictData});
			$("#prDistrict").jqxDropDownList({selectedIndex: index});
			
			//Copy ward geo
			var items = $("#crWard").jqxDropDownList('getItems');
			var cpyWardData =  new Array();
			for(var i = 0; i < items.length; i++){
				var row = {};
				row['geoId'] = items[i].value;
				row['description'] = items[i].label;
				cpyWardData[i] = row;
			}
			var index = $("#crWard").jqxDropDownList('getSelectedIndex'); 
			$("#prWard").jqxDropDownList({source: cpyWardData});
			$("#prWard").jqxDropDownList({selectedIndex: index});
		});
		
		/******************************************************End Contact Information**************************************************************************/
	});
	function getEmployeeContactInfo(){
		var contactInfo = {};
		contactInfo['homeTel'] = $("#homeTel").val();
		contactInfo['mobile'] = $("#mobile").val();
		contactInfo['diffTel'] = $("#diffTel").val();
		contactInfo['email'] = $("#email").val();
		contactInfo['prAddress'] = $("#prAddress").val();
		contactInfo['prCountry'] = $("#prCountry").val();
		contactInfo['prProvince'] = $("#prProvince").val();
		contactInfo['prDistrict'] = $("#prDistrict").val();
		contactInfo['prWard'] = $("#prWard").val();
		contactInfo['crAddress'] = $("#crAddress").val();
		contactInfo['crCountry'] = $("#crCountry").val();
		contactInfo['crProvince'] = $("#crProvince").val();
		contactInfo['crDistrict'] = $("#crDistrict").val();
		contactInfo['crWard'] = $("#crWard").val();
		return contactInfo; 
	}
</script>