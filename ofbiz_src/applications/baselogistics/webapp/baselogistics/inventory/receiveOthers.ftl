<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script>
<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>

<#assign invItemTypes = delegator.findList("InventoryItemType", null, null, null, null, false)>
var listInventoryItemTypesData = [];
<#if invItemTypes?exists>
	<#list invItemTypes as item>
		var row = {};
		row["inventoryItemTypeId"] = "${item.inventoryItemTypeId?if_exists}",
		row["description"] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
		listInventoryItemTypesData.push(row);
	</#list>
</#if>
<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false)>
var listFacilityData = [
	<#if facilities?exists>
		<#list facilities as item>
			{
				facilityId: "${item.facilityId?if_exists}",
				facilityName: "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}"
			},
		</#list>
	</#if>
	];

<#assign packingUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
var listUomData = [
			<#if packingUoms?exists>
				<#list packingUoms as item>
					{
						uomId: "${item.uomId?if_exists}",
						description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
					},
				</#list>
			</#if>
              ];
var mapUomData = {
		<#if listUomData?exists>
			<#list listUomData as item>
				"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
			</#list>
		</#if>	
	};


	<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "REQUIREMENT_STATUS"), null, null, null, false) />

	var listStatusItem = [
			<#if listStatusItem?exists>
				<#list listStatusItem as item>
					{
						statusId: "${item.statusId?if_exists}",
						description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
					},
				</#list>
			</#if>
              ];
	var mapStatusItem = {
		<#if listStatusItem?exists>
			<#list listStatusItem as item>
				"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
			</#list>
		</#if>	
	};

	var packingData = new Array();
	<#list packingUoms as item>
		var row = {};
		row['uomId'] = '${item.uomId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		packingData[${item_index}] = row;
	</#list>

	function getDescriptionByUomId(uomId) {
		for ( var x in packingData) {
			if (uomId == packingData[x].uomId) {
				return packingData[x].description;
			}
		}
	}

</script>

<div id="contentNotificationReceiveSuccess">
</div>

<div id="alterpopupWindowReceiveInventory">
	<div style="overflow-y: hidden;">
		<div class="row-fluid margin-top10">
			<div class="span6">
				<div class="row-fluid">
	 				<div class="span5" style="text-align: right;"><label class="asterisk" >${uiLabelMap.Facility} </label></div>
	 				<div class="span7"><div id="facilityId"></div></div>
 				</div>
 			</div>
 			<div class="span6">
 				<div class="row-fluid">
		 			<div class="span5" style="text-align: right;"><label class="asterisk" >${uiLabelMap.ReceivedDate} </label></div>
		 			<div class="span7"><div id="datetimeReceived"></div></div>
	 			</div>
 			</div>
		</div>
		<div class="row-fluid">
		    <div id="jqxgridProduct"></div>
	    </div>
 		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
 		<div class="row-fluid">
            <div class="span12 margin-top10">
            	<div class="span12">
            		<button id='alterSave' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
        		</div>
            </div>
    	</div>
   </div>
</div>

<div id="jqxNotificationReceiveSuccess" >
	<div id="notificationReceiveSuccess">
	</div>
</div>
<#include "listProducts.ftl" />
<script> 
	$("#jqxNotificationReceiveSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationReceiveSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#facilityId").jqxDropDownList({ source: listFacilityData, displayMember: 'facilityName', valueMember: 'facilityId', disabled: false, placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', autoDropDownHeight: true, theme: 'olbius', selectedIndex: 0});
	$("#datetimeReceived").jqxDateTimeInput({formatString: 'dd/MM/yyyy HH:mm:ss', showFooter:true, theme: 'olbius'});
	var dateCurrent = new Date();
    var minTimeCurrent = dateCurrent.getTime();
    var timeCurrent = dateCurrent.getTime();
	$('#datetimeReceived ').jqxDateTimeInput('setMinDate', new Date(minTimeCurrent));
	$('#datetimeReceived ').jqxDateTimeInput('setDate', new Date(timeCurrent));
	
	$("#alterSave").click(function () {
		var dataSoureInput = []; 
		var selectedIndexs = $('#jqxgridProduct').jqxGrid('getselectedrowindexes');
		for (var i = 0; i < selectedIndexs.length; i ++){
    		var data = $('#jqxgridProduct').jqxGrid('getrowdata', selectedIndexs[i]);
    		dataSoureInput.push(data);
		}
		if(dataSoureInput.length != 0){
			var listProducts = [];
			for(var i in dataSoureInput){
				var row = {};
				row['productId'] = dataSoureInput[i].productId;
				row['quantity'] = dataSoureInput[i].quantityAccepted;
				if (dataSoureInput[i].unitCost){
					row['unitCost'] = dataSoureInput[i].unitCost;
				} else {
					row['unitCost'] = 0;
				}
				row['quantityUomId'] = dataSoureInput[i].quantityUomId;
				row['inventoryItemTypeId'] = dataSoureInput[i].inventoryItemTypeId;
				var tmpMft = new Date(dataSoureInput[i].datetimeManufactured);
				row['datetimeManufactured'] = tmpMft.getTime();
				row['expireDate'] = dataSoureInput[i].expireDate.getTime();
				listProducts.push(row);
			}
			listProducts = JSON.stringify(listProducts);
			var validate = $('#alterpopupWindowReceiveInventory').jqxValidator('validate');
			if(validate != false){
				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}", 
						[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
							"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				            "callback": function() {bootbox.hideAll();}
				        }, 
				        {"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
				            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				            "callback": function() {
				            	receiveInventoryProductByLog(listProducts);
				        }
			    }]);
			}
		}else{
			bootbox.dialog("${uiLabelMap.YouNotYetChooseItem}", [{
                "label" : "${uiLabelMap.OK}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		}
	});
	
	$('#alterpopupWindowReceiveInventory').jqxValidator({
		rules: 
			[
		        { input: '#facilityId', message: '${uiLabelMap.FieldRequired}', action: 'valueChanged, blur', 
	        	   rule: function () {
	        		    var facilityId = $('#facilityId').val();
	            	    if(facilityId == ""){
	            	    	return false; 
	            	    }else{
	            	    	return true; 
	            	    }
	            	    return true; 
	        	    }
                },
		    ]
	});
	
</script>