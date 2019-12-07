<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#if security.hasEntityPermission("FACILITY", "_UPDATE", session) && !parameters.sub?has_content>
<div style="text-align:right">
<a href="UpdateFacility?sub=${parameters.sub?if_exists}&facilityId=${parameters.facilityId?if_exists}" style="margin-right: 7px;font-size: 20px;" data-rel="tooltip" title="" data-placement="left" class="button-action" data-original-title="${uiLabelMap.BSEdit}">
	<i class="icon-edit open-sans"></i>
</a>
</div>
</#if>
<style>
	.text-header {
		color: black !important;
	}
	.form-window-content-custom label {
	    margin-top: -5px;
	}
</style>
<script>
var facilityId = '${parameters.facilityId}';

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

</script>
<div id="containerNotify" style="width: 100%; height: 20%; margin-top: 15px; overflow: auto;">
</div>
<div id="notifyId" style="display: none;">
<div>
	${uiLabelMap.UpdateSuccessfully}
</div>
</div>
<div>
<div class="rowfluid">
	<div class="span10">
		<div class='row-fluid margin-bottom8'>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${StringUtil.wrapString(uiLabelMap.FacilityId)}: </div>
				</div>  
				<div class="span7">
					<div id="facilityIdDT" class="green-label"></div>
		   		</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${StringUtil.wrapString(uiLabelMap.FacilityName)}: </div>
				</div>  
				<div class="span7">
					<div id="facilityNameDT" class="green-label"></div>
				</div>
			</div>
		</div>
	   	<div class='row-fluid margin-bottom8'>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${StringUtil.wrapString(uiLabelMap.Owner)}:</div>
				</div>  
				<div class="span7">
					<div id="ownerPartyIdDT" class="green-label"></div>
				</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${StringUtil.wrapString(uiLabelMap.Storekeeper)}:</div>
				</div>  
				<div class="span7">
					<div id="managerNameDT" class="green-label"></div>
				</div>
			</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8'>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${StringUtil.wrapString(uiLabelMap.Address)}:</div>
				</div>  
				<div class="span7">
					<div id="addressDT" class="green-label"></div>
				</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${StringUtil.wrapString(uiLabelMap.PhoneNumber)}:</div>
				</div>  
				<div class="span7">
					<div id="phoneNumberDT" class="green-label"></div>
				</div>
			</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8'>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${StringUtil.wrapString(uiLabelMap.ProductStore)}:</div>
				</div>  
				<div class="span7">
					<div id="storeList" class="green-label"></div>
				</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${StringUtil.wrapString(uiLabelMap.DirectlyUnder)}:</div>
				</div>  
				<div class="span7">
					<div id="parentFacilityIdDT" class="green-label"></div>
				</div>
			</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8'>
		   	<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${StringUtil.wrapString(uiLabelMap.SquareFootage)}:</div>
				</div>  
				<div class="span7">
					<div id="facilitySizeDT" class="green-label"></div>
		   		</div>
			</div>
			<div class='span6'>
		   		<div class='span5 text-algin-right' >   
					<div>${StringUtil.wrapString(uiLabelMap.CommonDescription)}: </div>
				</div>  
				<div class="span7">
					<div  id="descriptionDT" class="green-label"></div >
				</div>
			</div>
	   	</div>
   	</div>
	</div>
</div>

<script>
<#assign localeStr = "vi" />
<#if locale != "vi">
<#assign localeStr = "en" />
</#if>


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
		$("#productImageViewer").append("<img style=\"height: 200px; width: 350px; margin-top: 20px\" id=\"blah\" src="+image+"/>");
	} else {
		$("#productImageViewer").html("");
		$("#productImageViewer").append("<img style=\"height: 200px; width: 350px; margin-top: 20px\" id=\"blah\" src=\"/logresources/images/demo.png\"/>");
	}
	
	$("#facilityIdDT").text(facilityDT.facilityId);	
	if (facilityDT.facilityName){
		$("#facilityNameDT").text(facilityDT.facilityName);
	} else {
		$("#facilityNameDT").text("");
	}
	if (facilityDT.parentFacilityId){
		$("#parentFacilityIdDT").text(facilityDT.parentFacilityId);
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
		$("#addressDT").html(facilityDT.address1);
	} else {
		$("#addressDT").text("");
	}
	if (facilityDT.phoneNumber){
		$("#phoneNumberDT").text(facilityDT.phoneNumber);
	} else {
		$("#phoneNumberDT").text("");
	}
	if (facilityDT.listProductStoreId && facilityDT.listProductStoreId.length > 0){
		if (productStoreData.length > 0){
			var text = "";
			for (var i = 0; i < facilityDT.listProductStoreId.length; i ++){
				for (var j = 0; j < productStoreData.length; j ++){
					if (productStoreData[j].productStoreId == facilityDT.listProductStoreId[i]){
						if (!text){
							text = productStoreData[j].description;
						} else {
							text = text + " | " + productStoreData[j].description;
						}
					}
				}
			}	
			$("#storeList").text(text);
		} else {
			$("#storeList").text("");
		}	
	} else {
		$("#storeList").text("");
	}
}

</script>