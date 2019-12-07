<@jqGridMinimumLib/>
<script>
	var contactMechTypeId = '${parameters.contactMechTypeId}';
	<#assign contactMechTypeIdCheck = '${parameters.contactMechTypeId}' !>
	<#if mechMap.purposeTypes?exists>
		var listcontactMechPurposeType = [
                      <#list mechMap.purposeTypes as purpuseType>
                      		{
                      			contactMechPurposeTypeId : "${purpuseType.contactMechPurposeTypeId?if_exists}",
                      			description : "${StringUtil.wrapString(purpuseType.get("description",locale))?if_exists}"
                      		},
                      </#list>
              ];
		<#else> var listcontactMechPurposeType = [];
	</#if>
	var source = {
			localdata: listcontactMechPurposeType,
			dataType : "array",
			datafield : [
	             {name : "contactMechPurposeTypeId"},
	             {name : "description"}
             ]
			
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	<#assign countryGeo = delegator.findList("Geo",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "COUNTRY"),null,null,null,false) !>
	<#if countryGeo?exists>
		var listCountryGeo = [
                  <#list countryGeo as cg>
                  	{
                  		geoId : "${cg.geoId?if_exists}",
                  		geoName : "${StringUtil.wrapString(cg.get("geoName",locale))?if_exists}"
                  	},
                  </#list>
          ];
		<#else> var listCountryGeo = [];
	</#if>
	var sourceCountryGeoId = {
			localdata : listCountryGeo,
			datatype : "array",
			datafield : [
	             {name : "geoId"},
	             {name : "geoName"}
             ]
	};
	var dataAdapterCountryGeoId = new $.jqx.dataAdapter(sourceCountryGeoId);
	var sourcCountyGeo = {
			type : "POST",
			datatype : "json",
			data : {
				geoId : "VNM"
			},
			url : "getCountyGeoByCountry",
			dataField : [
	             {name : "geoId"},
	             {name : "geoName"}
             ],
	};
	var dataAdapterCountyGeo = new $.jqx.dataAdapter(sourcCountyGeo);
</script>
<div class="row-fluid form-window-content">
	<div class="span12">
		<div class="row-fluid margin-bottom10">
			<div class='span6 align-right asterisk'>
				${uiLabelMap.PartyContactPurposes}
		    </div>
		    <div class="span6">
		    	<div id="contactMechPurposeTypeId" name="contactMechPurposeTypeId"></div>
		    </div>
		</div>
	</div>
</div>
<#if "POSTAL_ADDRESS" = '${parameters.contactMechTypeId}'>
	<div class="row-fluid form-window-content">
		<div class="span12">
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.PartyToName}
		        </div>
				<div class="span6">
					<input type="text" size="30" style="height:18px;" maxlength="60" id="toName" />
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.PartyAttentionName}
		        </div>
				<div class="span6">
					<input type="text" size="30" style="height:18px;" maxlength="60" id="attnName" />
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.PartyAddressLine1}
				</div>
				<div class="span6">
					<input type="text" class="required" size="30" style="height:18px;" maxlength="30" id="address1" />
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.PartyAddressLine2}
				</div>
				<div class="span6">
					<input type="text" size="30" style="height:18px;" maxlength="30" id="address2" />
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.PartyCity}
				</div>
				<div class="span6">
					<input type="text" class="required" size="30" style="height:18px;" maxlength="30" id="city" />
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.CommonCountry}
				</div>
				<div class="span6">
					<div id="editcontactmechform_countryGeoId"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.PartyZipCode}
				</div>
				<div class="span6">
					<input type="text" class="required" size="12" style="height:18px;" maxlength="10" id="postalCode" />
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.PartyState}
				</div>
				<div class="span6">
					<div id="stateProvinceGeoId"></div>
				</div>
			</div>
		</div>
	</div>
	<#elseif "TELECOM_NUMBER" = '${parameters.contactMechTypeId}'>
	<div class="row-fluid form-window-content">
		<div class="span12">
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.PartyPhoneNumber}
				</div>
				<div class="span6">
					<input type="text" size="4" maxlength="10" name="countryCode" id="countryCode" />
			        -&nbsp;<input type="text" size="4" maxlength="10" name="areaCode" id="areaCode" />
			        -&nbsp;<input type="text" size="15" maxlength="15" name="contactNumber" id="contactNumber" />
			        &nbsp;ext&nbsp;<input type="text" size="6" maxlength="10" name="extension" id="extension" />
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'></div>
				<div class="span6">
					[${uiLabelMap.CommonCountryCode}] [${uiLabelMap.PartyAreaCode}] [${uiLabelMap.PartyContactNumber}] [${uiLabelMap.PartyExtension}]
				</div>
			</div>
		</div>
	</div>
	<#elseif "EMAIL_ADDRESS" = '${parameters.contactMechTypeId}'>
	<div class="row-fluid form-window-content">
		<div class="span12">
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
					${uiLabelMap.PartyEmailAddress}
				</div>
				<div class="span6">
					<input type="text" class="required" size="60" maxlength="255" name="emailAddress" id="emailAddress" />
				</div>
			</div>
		</div>
	</div>
	<#else>
	<div class="row-fluid form-window-content">
		<div class="span12">
			<div class="row-fluid margin-bottom10">
				<div id="ContactTypeDes" class='span6 align-right asterisk'></div>
				<div class="span6">
					<input type="text" class="required" size="60" maxlength="255" id="infoString"" />
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span6 align-right asterisk'>
				</div>
				<div class="span6">
					[${uiLabelMap.CommonCountryCode}] [${uiLabelMap.PartyAreaCode}] [${uiLabelMap.PartyContactNumber}] [${uiLabelMap.PartyExtension}]
				</div>
			</div>
		</div>
	</div>
</#if>
<script type="text/javascript">
	$( document ).ready(function() {
		start();
	});
	function start(){
		$("#contactMechPurposeTypeId").jqxComboBox('setContent', 'Please select....');
		$("#contactMechPurposeTypeId").jqxComboBox({width : 220, height : 25, source: dataAdapter, displayMember: "description", valueMember : "contactMechPurposeTypeId"});
		<#if contactMechTypeIdCheck = "POSTAL_ADDRESS">
			$("#editcontactmechform_countryGeoId").jqxComboBox({width :220, height :25, source : dataAdapterCountryGeoId, displayMember:"geoName",valueMember:"geoId"});
			$("#stateProvinceGeoId").jqxComboBox({width : 220, height:25,source : dataAdapterCountyGeo,displayMember: "geoName", valueMember : "geoId"});
			$("#editcontactmechform_countryGeoId").on('select', function(event){
				sourcCountyGeo.data.geoId = $("#editcontactmechform_countryGeoId").val();
				dataAdapterCountyGeo.dataBind();
			});
			<#elseif "TELECOM_NUMBER" = contactMechTypeIdCheck>
				$("#countryCode").jqxInput({height:25,width:220});
				$("#areaCode").jqxInput({height:25,width:220});
				$("#contactNumber").jqxInput({height:25,width:220});
				$("#extension").jqxInput({height:25,width:220});
			<#elseif "EMAIL_ADDRESS" = contactMechTypeIdCheck>
				$("#emailAddress").jqxInput({height:25,width:220});
			<#else>
				var tmp = $('#preContactMechTypeId').jqxComboBox('getSelectedItem');
				$("#ContactTypeDes").text(tmp.label);
				$("#infoString").jqxInput({height:25,width:220});
		</#if>
	}
</script>