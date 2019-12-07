<script>
	var facilityId = '${parameters.facilityId}';
	
	
	<#assign listFacilityType = delegator.findList("FacilityType", null, null, null, null, false) />
	var facilityTypeData = new Array();
	<#list listFacilityType as facilityType>
		var row = {};
		row['facilityTypeId'] = "${facilityType.facilityTypeId}";
		row['description'] = "${facilityType.get('description', locale)?if_exists}";
		facilityTypeData[${facilityType_index}] = row;
	</#list>
	
	function getDescriptionByFacilityTypeId(facilityTypeId) {
		for ( var x in facilityTypeData) {
			if (facilityTypeId == facilityTypeData[x].facilityTypeId) {
				return facilityTypeData[x].description;
			}
		}
	}
	
	
	<#assign listFacilityGroup = delegator.findList("FacilityGroup", null, null, null, null, false) />
	var facilityGroupData = new Array();
	<#list listFacilityGroup as facilityGroup>
		var row = {};
		row['facilityGroupId'] = "${facilityGroup.facilityGroupId}";
		row['facilityGroupName'] = "${facilityGroup.get('facilityGroupName', locale)?if_exists}";
		facilityGroupData[${facilityGroup_index}] = row;
	</#list>
	
	function getDescriptionByFacilityGroupId(facilityGroupId) {
		for ( var x in facilityGroupData) {
			if (facilityGroupId == facilityGroupData[x].facilityGroupId) {
				return facilityGroupData[x].facilityGroupName;
			}
		}
	}
	
	<#assign listPartyNameView = delegator.findList("PartyNameView", null, null, null, null, false) />
	var partyNameViewData = new Array();
	<#list listPartyNameView as partyNameView>
		var row = {};
		row['partyId'] = "${partyNameView.partyId}";
		row['groupName'] = "${partyNameView.get('groupName', locale)?if_exists}";
		partyNameViewData[${partyNameView_index}] = row;
	</#list>
	
	function getDescriptionByPartyNameViewId(partyId) {
		for ( var x in partyNameViewData) {
			if (partyId == partyNameViewData[x].partyId) {
				return partyNameViewData[x].groupName + ' ['+ partyId +']';
			}
		}
	}
	
	<#assign listFacility = delegator.findList("Facility", null, null, null, null, false) />
	var facilityData = new Array();
	<#list listFacility as facility>
		var row = {};
		row['facilityId'] = "${facility.facilityId}";
		row['facilityName'] = "${facility.get('facilityName', locale)?if_exists}";
		facilityData[${facility_index}] = row;
	</#list>
	
	function getDescriptionByFacilityId(facilityId) {
		for ( var x in facilityData) {
			if (facilityId == facilityData[x].facilityId) {
				return facilityData[x].facilityName;
			}
		}
	}
	
	<#assign listUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "AREA_MEASURE"), null, null, null, false) />
	var uomData = new Array();
	<#list listUom as uom>
		var row = {};
		row['uomId'] = "${uom.uomId}";
		row['description'] = "${uom.get('description', locale)?if_exists}";
		uomData[${uom_index}] = row;
	</#list>
	
	function getDescriptionByUomId(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].description;
			}
		}
	}
</script>

<div>
	<div id="contentNotificationUpdateSuccessByViewDetail" style="width:100%">
	</div>
	<div class='row-fluid margin-bottom8 padding-top8'>
		<div class='span6'>
			<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.facilityId)}: </label>
			</div>  
			<div class="span7">
				<label id="facilityId" style="color: green"></label>
	   		</div>
		</div>
		<div class='span6'>
			<div class='span3 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.FacilityName)}: </label>
			</div>  
			<div class="span9">
				<label id="facilityName" style="color: green">
				</label>
			</div>
		</div>
	</div>
	<div class='row-fluid margin-bottom8 padding-top8'>
		<div class='span6'>
			<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.ProductFacilityType)}:</label>
			</div>  
			<div class="span7">
				<label id="facilityTypeId" style="color: green">
				</label>
			</div>
		</div>
		<div class='span6'>
			<div class='span3 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.GroupFacility)}:</label>
			</div>  
			<div class="span9">
				<label id="primaryFacilityGroupId" style="color: green"></label>
	   		</div>
		</div>
   	</div>
   	<div class='row-fluid margin-bottom8 padding-top8'>
		<div class='span6'>
			<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.Owner)}:</label>
			</div>  
			<div class="span7">
				<label id="ownerPartyId" style="color: green">
				</label>
			</div>
		</div>
		<div class='span6'>
			<div class='span3 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.ParentFacility)}:</label>
			</div>  
			<div class="span9">
				<label id="parentFacilityId" style="color: green">
				</label>
			</div>
		</div>
   	</div>
   	<div class='row-fluid margin-bottom8 padding-top8'>
	   	<div class='span6'>
			<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.SquareFootage)}:</label>
			</div>  
			<div class="span7">
				<label id="facilitySize" style="color: green"></label>
	   		</div>
		</div>
		<div class='span6'>
			<div class='span3 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.facilitySizeUomId)}:</label>
			</div>  
			<div class="span9">
				<label id="facilitySizeUomId" style="color: green">
				</label>
			</div>
		</div>
   	</div>
   	<div class='row-fluid margin-bottom8 padding-top8'>
   		<div class='span6'>
	   		<div class='span5 text-algin-right' >   
				<label>${StringUtil.wrapString(uiLabelMap.CommonDescription)}: </label>
			</div>  
			<div class="span7">
				<label  id="description" style="color: green"></label >
			</div>
		</div>
   	</div>
</div>

<div id="jqxNotificationUpdateSuccessByViewData" >
	<div id="jqxNotificationUpdateSuccessByViewData">
	</div>
</div>
<#include "editFacilityByFacilityId.ftl" />
<script>
	$( document ).ready(function() {
		loadFacilityDetailByFacilityId();
	});
	$("#jqxNotificationUpdateSuccessByViewData").jqxNotification({ width: "100%", appendContainer: "#contentNotificationUpdateSuccessByViewDetail", opacity: 0.9, autoClose: true, template: "success" });
	function loadFacilityDetailByFacilityId(){
		var listFacilityDetail;
		$.ajax({
			url: "loadFacilityDetailByFacilityId",
			type: "POST",
			data: {facilityId: facilityId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listFacilityDetail = data["listFacilityDetail"];
			bindingDataDetail(listFacilityDetail);
		});
	}
	var objectData;
	function bindingDataDetail(listFacilityDetail){
		objectData = null;
		for(var i in listFacilityDetail){
			objectData = listFacilityDetail[i];
		}
		
		loadDataToLableDetail(objectData);
	}
	
	
	
	function loadDataToLableDetail(objectData){
		var facilityId = document.getElementById("facilityId");
		var facilityName = document.getElementById("facilityName");
		var facilityTypeId = document.getElementById("facilityTypeId");
		var primaryFacilityGroupId = document.getElementById("primaryFacilityGroupId");
		var ownerPartyId = document.getElementById("ownerPartyId");
		var parentFacilityId = document.getElementById("parentFacilityId");
		var facilitySize = document.getElementById("facilitySize");
		var facilitySizeUomId = document.getElementById("facilitySizeUomId");
		var description = document.getElementById("description");
		document.getElementById("facilityId").innerHTML = objectData.facilityId;
		facilityName.innerHTML = objectData.facilityName;
		if(objectData.facilityTypeId != null){
			facilityTypeId.innerHTML = getDescriptionByFacilityTypeId(objectData.facilityTypeId);
		}
		if(objectData.facilityTypeId == null){
			facilityTypeId.innerHTML = '${StringUtil.wrapString(uiLabelMap.LogFacilityDetailNotValue)}';
		}
		if(objectData.primaryFacilityGroupId != null){
			primaryFacilityGroupId.innerHTML = getDescriptionByFacilityGroupId(objectData.primaryFacilityGroupId);
		}
		if(objectData.primaryFacilityGroupId == null){
			primaryFacilityGroupId.innerHTML = '${StringUtil.wrapString(uiLabelMap.LogFacilityDetailNotValue)}';
		}
		if(objectData.ownerPartyId != null){
			ownerPartyId.innerHTML = getDescriptionByPartyNameViewId(objectData.ownerPartyId);
		}
		if(objectData.ownerPartyId == null){
			ownerPartyId.innerHTML = '${StringUtil.wrapString(uiLabelMap.LogFacilityDetailNotValue)}';
		}
		if(objectData.parentFacilityId != null){
			parentFacilityId.innerHTML = getDescriptionByFacilityId(objectData.parentFacilityId);
		}
		if(objectData.parentFacilityId == null){
			parentFacilityId.innerHTML = '${StringUtil.wrapString(uiLabelMap.LogFacilityDetailNotValue)}';
		}
		if(objectData.facilitySize != null){
			facilitySize.innerHTML = objectData.facilitySize;
		}
		if(objectData.facilitySize == null){
			facilitySize.innerHTML = '${StringUtil.wrapString(uiLabelMap.LogFacilityDetailNotValue)}';
		}
		if(objectData.facilitySizeUomId != null){
			facilitySizeUomId.innerHTML = getDescriptionByUomId(objectData.facilitySizeUomId);
		}
		if(objectData.facilitySizeUomId == null){
			facilitySizeUomId.innerHTML = '${StringUtil.wrapString(uiLabelMap.LogFacilityDetailNotValue)}';
		}
		if(objectData.description != null){
			description.innerHTML = objectData.description;
		}
		if(objectData.description == null){
			description.innerHTML = '${StringUtil.wrapString(uiLabelMap.LogFacilityDetailNotValue)}';
		}
	}
	
	function editFacilityByFacilityId(){
		var checkUpdateFacilityByViewFacility = 1;
		loadDataEditByFacility(objectData, checkUpdateFacilityByViewFacility);
	}
</script>