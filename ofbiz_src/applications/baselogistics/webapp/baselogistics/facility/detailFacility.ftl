<script>
	var facilityId = '${parameters.facilityId}';
	
	<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.facilityId?if_exists}, false)!/>
	<#assign parent = delegator.findOne("Facility", {"facilityId" : facility.parentFacilityId?if_exists}, false)!/>
	var parent = {
		facilityId: '${parent.facilityId?if_exists}',
		facilityName: '${parent.facilityName?if_exists}',
	}
	
	
	<#assign listUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "AREA_MEASURE"), null, null, null, false) />
	var uomData = new Array();
	<#list listUom as uom>
		var row = {};
		row['uomId'] = "${uom.uomId}";
		row['abbreviation'] = "${StringUtil.wrapString(uom.get('abbreviation', locale)?if_exists)}";
		uomData[${uom_index}] = row;
	</#list>
	
	function getDescriptionByUomId(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].abbreviation;
			}
		}
	}
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	<#assign listStores = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("payToPartyId", company), null, null, null, false) />
	var productStoreData = new Array();
	<#list listStores as store>
		var row = {};
		row['productStoreId'] = "${store.productStoreId}";
		row['description'] = "${StringUtil.wrapString(store.get('storeName', locale)?if_exists)}";
		productStoreData.push(row);
	</#list>
	
	<#-- <#assign isDistributor = Static["com.olbius.basesales.util.SalesPartyUtil"].isDistributor(delegator, userLogin.getString("partyId"))!/> -->
	<#assign isDistributor = Static["com.olbius.baselogistics.util.LogisticsFacilityUtil"].isFacilityByOwnerParty(delegator, facilityId)!/>
</script>
<div id="jqxNotificationNestedSlide" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<div id="containerNotify" style="width: 100%; height: 20%; margin-top: 15px; overflow: auto;">
</div>
<div id="notifyId" style="display: none;">
	<div>
		${uiLabelMap.UpdateSuccessfully}
	</div>
</div>
<div>
	<div class="rowfluid">
		<div class="span2">
			<div id="productImageViewer">
			</div>
		</div>
		<div class="span9">
			<div class='row-fluid'>
				<div class='span6'>
					<div class='span3'>
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.FacilityId)}: </div>
					</div>  
					<div class="span8">
						<div id="facilityIdDT" class="green-label bold-label">${facility.facilityCode?if_exists}</div>
			   		</div>
				</div>
				<div class='span6'>
					<div class='span3'>
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.FacilityName)}: </div>
					</div>  
					<div class="span8">
						<div id="facilityNameDT" class="green-label bold-label"></div>
					</div>
				</div>
			</div>
		   	<div class='row-fluid'>
				<div class='span6'>
					<div class='span3'>
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.Owner)}:</div>
					</div>  
					<div class="span8">
						<div id="ownerPartyIdDT" class="green-label bold-label"></div>
					</div>
				</div>
				<div class='span6'>
					<div class='span3'>
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.BLRoles)}:</div>
					</div>  
					<div class="span8">
						<#if isDistributor?has_content && isDistributor == true>
							<div><a href="getLogFacilityPartyRoleDeposit?facilityId=${parameters.facilityId}" target="_blank">${uiLabelMap.Detail} <i class="fa-external-link"></i></a></div>
						<#else>
							<div><a href="getFacilityPartyRole?facilityId=${parameters.facilityId}" target="_blank">${uiLabelMap.Detail} <i class="fa-external-link"></i></a></div>
						</#if>
					</div>
				</div>
		   	</div>
		   	<div class='row-fluid'>
				<div class='span6'>
					<div class='span3'>
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.Address)}:</div>
					</div>  
					<div class="span8">
						<div id="addressDT" class="green-label bold-label">

						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='span3'>
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.PhoneNumber)}:</div>
					</div>  
					<div class="span8">
						<div id="phoneNumberDT" class="green-label bold-label"></div>
					</div>
				</div>
		   	</div>
		   	<div class='row-fluid'>
				<div class='span6'>
					<div class='span3'>
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}:</div>
					</div>  
					<div class="span8">
						<div id="storeList" class="green-label bold-label"></div>
					</div>
				</div>
				<div class='span6'>
					<div class='span3'>
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.DirectlyUnder)}:</div>
					</div>  
					<div class="span8">
						<div id="parentFacilityIdDT" class="green-label bold-label"></div>
					</div>
				</div>
		   	</div>
		   	<div class='row-fluid'>
			   	<div class='span6'>
					<div class='span3'>
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.SquareFootage)}:</div>
					</div>  
					<div class="span8">
						<div id="facilitySizeDT" class="green-label bold-label"></div>
			   		</div>
				</div>
				<div class='span6'>
			   		<div class='span3' >   
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.CommonDescription)}: </div>
					</div>  
					<div class="span8">
						<div  id="descriptionDT" class="green-label bold-label"></div >
					</div>
				</div>
		   	</div>
		   	<div class='row-fluid hide'>
		   		<div class='span6'>
			   		<div class='span3' >   
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.BLZone)}: </div>
					</div>  
					<div class="span8">
						<div  id="zoneDT" class="green-label bold-label">${facility.zone?if_exists}</div >
					</div>
				</div>
				<div class='span6'>
			   		<div class='span3' >   
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.BLFacilityCode)}: </div>
					</div>  
					<div class="span8">
						<div id="facilityCodeDT" class="green-label bold-label">${facility.facilityCode?if_exists}</div >
					</div>
				</div>
			</div>
			<div class='row-fluid hide'>
		   		<div class='span6'>
			   		<div class='span3' >   
						<div class="bold-label">${StringUtil.wrapString(uiLabelMap.BLUsingLocation)}: </div>
					</div>  
					<div class="span8">
						<#if facility.requireLocation?has_content && facility.requireLocation == 'Y'>
							<div  id="locationRequireDT" class="green-label bold-label">${StringUtil.wrapString(uiLabelMap.LogYes)}</div >						
						<#else>
							<div  id="locationRequireDT" class="green-label bold-label">${StringUtil.wrapString(uiLabelMap.LogNO)}</div >
						</#if>
					</div>
				</div>
				<div class='span6'>
			   		<div class='span3' >   
					</div>  
					<div class="span8">
					</div>
				</div>
			</div>
	   	</div>
   	</div>
</div>

<#include "listFacility.ftl"/>
<#include "viewFacilityDetailOnMap.ftl">
<script>
	<#assign localeStr = "VI" />
	<#if locale = "en">
	    <#assign localeStr = "EN" />
	</#if>
	
	$("#notifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success",
    });
	
	$( document ).ready(function() {
		loadFacilityDetailByFacilityId(facilityId);
	});
	var facilityDT;
	function loadFacilityDetailByFacilityId(facilityId){
		jQuery.ajax({
			url: "getFacilityDetail",
			type: "POST",
			dataType: "json",
            async: false,
			data: {
				facilityId: facilityId,
			},
			success: function(res){
				facilityDT = res;
			}
		});
		$('#blah').css("height", null)
		var image = facilityDT.imagesPath;
		if (image){
			$("#productImageViewer").html("");
			$("#productImageViewer").append("<img style=\"height: 150px; width: 150px; margin-top: 10px\" id=\"blah\" src="+image+"/>");
		} else {
			$("#productImageViewer").html("");
			$("#productImageViewer").append("<img style=\"height: 150px; width: 150px; margin-top: 10px\" id=\"blah\" src=\"/logresources/images/demo.png\"/>");
		}
		
		if (facilityDT.facilityName){
			$("#facilityNameDT").text(facilityDT.facilityName);
		} else {
			$("#facilityNameDT").text("");
		}
		if (facilityDT.facilityCode){
			$("#facilityCodeDT").text(facilityDT.facilityCode);
		} else {
			$("#facilityCodeDT").text("");
		}
		if (facilityDT.parentFacilityId){
			$("#parentFacilityIdDT").append(parent.facilityName + " [<a id='parentId' href='javascript:viewDetailFacility(\"" +facilityDT.parentFacilityId+"\")'>"+ facilityDT.parentFacilityId +"</a>]");
		} else {
			$("#parentFacilityIdDT").text("");
		}
		if (facilityDT.description){
			$("#descriptionDT").text(facilityDT.description);
		} else {
			$("#descriptionDT").text("");
		}
		var val = facilityDT.facilitySize;
		var uomDes = getDescriptionByUomId(facilityDT.facilitySizeUomId);
		if (val){
			$("#facilitySizeDT").text(val.toLocaleString('${localeStr}') + " (" + uomDes + ")");
		} else {
			$("#facilitySizeDT").text("");
		}
		if (facilityDT.ownerName){
			$("#ownerPartyIdDT").text(facilityDT.ownerName);
		} else {
			$("#ownerPartyIdDT").text("");
		}
		if (facilityDT.managerName){
			$("#managerNameDT").text(facilityDT.managerName);
		} else {
			$("#managerNameDT").text("");
		}
		if (facilityDT.address1){
			$("#addressDT").html(facilityDT.address1+"<a href=\"javascript:openEditLocation()\"> <i class=\"fa fa-globe open-sans\"></i></a>");
		} else {
			$("#addressDT").text("");
		}
		if (facilityDT.phoneNumber){
			$("#phoneNumberDT").text(facilityDT.phoneNumber);
		} else {
			$("#phoneNumberDT").text("");
		}
		if (facilityDT.listProductStoreId){
			var text = "";
			for ( var i = 0 ; i< facilityDT.listProductStoreId.length ; i++ )
				if (  i== facilityDT.listProductStoreId.length-1)
					text = text + facilityDT.listProductStoreId[i] ;
				else
			 		text = text + facilityDT.listProductStoreId[i] + ", ";
			$("#storeList").text(text);
		} else {
			$("#storeList").text("");
		}
	}
	
	function viewDetailFacility (facilityId){
		window.location.replace("detailFacility?facilityId="+facilityId);
	}

	function openEditLocation() {
	    var localFacilityId=facilityDT.facilityId;
	    var localGeoPointId=facilityDT.geoPointId;
	    var localPostalAddressId=facilityDT.postalAddressId;
	    var localAddress1=facilityDT.address1;
	    var localLatitue=facilityDT.faLat;
	    var localLongtitue=facilityDT.faLng;

        OlbFacilityDetailOnMap.openMapDetail(localFacilityId,localGeoPointId,localAddress1, localLatitue, localLongtitue,localPostalAddressId)
    }
</script>