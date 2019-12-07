<@jqGridMinimumLib/>
<script>
	var facilityId = '${parameters.facilityId}';
	var contactMechTypeId = '${parameters.contactMechTypeId}';
	<#assign contactMechTypeIdCheck = '${parameters.contactMechTypeId}' !>
	<#assign geoList = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "COUNTRY"), null, null, null, false) />
	var geoData = new Array();
	<#list geoList as geo>
		<#assign geoId = StringUtil.wrapString(geo.geoId) />
		<#assign geoName = StringUtil.wrapString(geo.geoName) />
		var row = {};
		row['geoId'] = "${geo.geoId}";
		row['geoName'] = "${geo.geoName}";
		geoData[${geo_index}] = row;
	</#list>
</script>

<div class='row-fluid margin-bottom8 padding-top8'>
	<div class='span5 text-algin-right'>
		<label class="asterisk">${StringUtil.wrapString(uiLabelMap.PartyContactPurpose)}</label>
	</div>  
	<div class="span7">
		<div id="contactMechPurposeTypeId">
		</div>
	</div>
</div>

<#if contactMechTypeIdCheck=="POSTAL_ADDRESS">
	<#include "createContactMechTest.ftl" />
<#else>
</#if>

<#if contactMechTypeIdCheck=="TELECOM_NUMBER">
	<#include "createContectMectPhoneNumberWindown.ftl" />
<#else>
</#if>

<#if contactMechTypeIdCheck=="EMAIL_ADDRESS" || contactMechTypeIdCheck=="IP_ADDRESS">
	<#include "createContectMectIPOrEmailAddress.ftl" />
<#else>
</#if>

<#if contactMechTypeIdCheck=="WEB_ADDRESS" || contactMechTypeIdCheck=="LDAP_ADDRESS">
	<#include "createContectMectWebAdrressOrLDAPAddress.ftl" />
<#else>
</#if>

<#if contactMechTypeIdCheck=="DOMAIN_NAME">
	<#include "createContactMechDomainName.ftl" />
<#else>
</#if>

<#if contactMechTypeIdCheck == "ELECTRONIC_ADDRESS" ||contactMechTypeIdCheck=="INTERNAL_PARTYID">
	<#include "createContectMectElectricAddressOrRemine.ftl" />
<#else>
</#if>

<script>
	$(document).ready(function () {
		loadContactMechPurposeTypeId();
	});
	$("#contactMechPurposeTypeId").jqxDropDownList('setContent', 'Please select....');
	$("#contactMechPurposeTypeId").jqxDropDownList({ disabled: false}); 
	function loadContactMechPurposeTypeId(){
		var contactMechTypeId = '${contactMechTypeId}';
			var request = $.ajax({
				  url: "loadContactMechTypePurposeList",
				  type: "POST",
				  data: {contactMechTypeId : contactMechTypeId},
				  dataType: "json",
				  success: function(data) {
					  var listcontactMechPurposeTypeMap = data["listcontactMechPurposeTypeMap"];
					  var contactMechPurposeTypeId = new Array();
					  var description = new Array();
					  var array_keys = new Array();
					  var array_values = new Array();
					  for(var i = 0; i < listcontactMechPurposeTypeMap.length; i++){
						  for (var key in listcontactMechPurposeTypeMap[i]) {
						      array_keys.push(key);
						      array_values.push(listcontactMechPurposeTypeMap[i][key]);
						  }
					  }
					  var dataTest = new Array();
					  for (var j =0; j < array_keys.length; j++){
								var row = {};
								row['id'] = array_keys[j];
								row['value'] = array_values[j];
								dataTest[j] = row;
					  }
					  if(contactMechTypeId == "IP_ADDRESS"){
						  $("#contactMechPurposeTypeId").jqxDropDownList({placeHolder: "Please Choose..."});
						  $("#contactMechPurposeTypeId").jqxDropDownList('setContent', 'Not Data');
						  $("#contactMechPurposeTypeId").jqxDropDownList({ disabled: true });
						  return;
					  }
					  if(contactMechTypeId == "INTERNAL_PARTYID"){
						  $("#contactMechPurposeTypeId").jqxDropDownList({placeHolder: "Please Choose..."});
						  $("#contactMechPurposeTypeId").jqxDropDownList('setContent', 'Not Data');
						  $("#contactMechPurposeTypeId").jqxDropDownList({ disabled: true });
						  return;
					  }
					  if(contactMechTypeId == "ELECTRONIC_ADDRESS"){
						  $("#contactMechPurposeTypeId").jqxDropDownList({placeHolder: "Please Choose..."});
						  $("#contactMechPurposeTypeId").jqxDropDownList('setContent', 'Not Data');
						  $("#contactMechPurposeTypeId").jqxDropDownList({ disabled: true });
						  return;
					  }
					  if(contactMechTypeId == "DOMAIN_NAME"){
						  $("#contactMechPurposeTypeId").jqxDropDownList({placeHolder: "Please Choose..."});
						  $("#contactMechPurposeTypeId").jqxDropDownList('setContent', 'Not Data');
						  $("#contactMechPurposeTypeId").jqxDropDownList({ disabled: true });
						  return;
					  }
					  else{
						  $("#contactMechPurposeTypeId").jqxDropDownList({ selectedIndex: 0,  source: dataTest,  placeHolder: "Please select....", displayMember: 'value', valueMember: 'id'});
						  $("#contactMechPurposeTypeId").jqxDropDownList('setContent', 'Please select....');
						  $("#contactMechPurposeTypeId").jqxDropDownList({ disabled: false }); 
						  return;
					  }
				  }
			});
			request.done(function(data) {
			});
	}
	
</script>