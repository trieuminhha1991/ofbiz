<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/demos/sampledata/generatedata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script>
	var facilityId = '${parameters.facilityId}';
	
	<#assign locationFacilityTypeList = delegator.findList("LocationFacilityType", null, null, null, null, false) />
	var locationFacilityTypeData = new Array();
	<#list locationFacilityTypeList as locationFacilityType>
		var row = {};
		row['locationFacilityTypeId'] = "${locationFacilityType.locationFacilityTypeId}";
		row['description'] = "${locationFacilityType.description}";
		locationFacilityTypeData[${locationFacilityType_index}] = row;
	</#list>
	
	function getlocationFacilityType(locationFacilityTypeIdInput) {
		for ( var x in locationFacilityTypeData) {
			if (locationFacilityTypeIdInput == locationFacilityTypeData[x].locationFacilityTypeId) {
				return locationFacilityTypeData[x].description;
			}
		}
	}
	
	<#assign locationFacilityList = delegator.findList("LocationFacility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId)), null, null, null, false) />
	var locationFacilityData = new Array();
	<#list locationFacilityList as locationFacility>
		var row = {};
		row['locationId'] = "${locationFacility.locationId}";
		row['description'] = "${locationFacility.description}";
		locationFacilityData[${locationFacility_index}] = row;
	</#list>
	
	function getlocationFacility(locationIdInput) {
		for ( var x in locationFacilityData) {
			if (locationIdInput == locationFacilityData[x].locationId) {
				return locationFacilityData[x].description;
			}
		}
	}
	
	<#assign list = listUom.size()/>
    <#if listUom?size gt 0>
		<#assign uomId="var uomId = ['" + StringUtil.wrapString(listUom.get(0).uomId?if_exists) + "'"/>
		<#assign description="var description = ['" + StringUtil.wrapString(listUom.get(0).description?if_exists) + "'"/>
		<#if listUom?size gt 1>
			<#list 1..(list - 1) as i>
				<#assign uomId=uomId + ",'" + StringUtil.wrapString(listUom.get(i).uomId?if_exists) + "'"/>
				<#assign description=description + ",'" + StringUtil.wrapString(listUom.get(i).description?if_exists) + "'"/>
			</#list>
		</#if>
		<#assign uomId=uomId + "];"/>
		<#assign description=description + "];"/>
	<#else>
		<#assign uomId="var uomId = [];"/>
    	<#assign description="var description = [];"/>
    </#if>
	${uomId}
	${description}
	
	var adapter = new Array();
	for(var i = 0; i < ${list}; i++){
		var row = {};
		row['uomId'] = uomId[i];
		row['description'] = description[i];
		adapter[i] = row;
	}
	function getUom(uomId) {
		for ( var x in adapter) {
			if (uomId == adapter[x].uomId) {
				return adapter[x].description;
			}
		}
	}
	
	<#assign productList = delegator.findList("ListProductByInventoryItemId", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId)), null, null, null, false) />
	var productData = new Array();
	<#list productList as product>
		<#assign productId = StringUtil.wrapString(product.productId) />
		var row = {};
		row['productId'] = "${product.productId}";
		productData[${product_index}] = row;
	</#list>
	
	
</script>

<div>
	<div id="contentMessageNotificationSelectMyTree">
	</div>
	<div id="contentNotificationSelectMyTreeFullSelect" style="width:100%">
	</div>
	<div>
		<input type="button" value='${uiLabelMap.CreateNewLocationFacility}' id="showWindowButton" />	
		<input type="button" value='${uiLabelMap.AddProductInLocationFacility}' id="jqxButtonAddProduct" />	
		<input type="button" value='${uiLabelMap.StockProductIdForLocationInFacilityForLocation}' id="jqxButtonStockLocation" />	
		<input type="button" value='${uiLabelMap.FacilitylocationSeqIdCurrent}' id="jqxButtonStockLocationContrary" />
		<input type="button" value='${uiLabelMap.DSDeleteLocationFacilityType}' id="jqxButtonDeleteLocationFacility" />
	</div>
<div>

<#include "addProductToLocationInFacility.ftl" />

<div id="CreateNewLocation" class="hide">
	<div>${uiLabelMap.ProductNewLocationType}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="form-horizontal">
					<div class="control-group no-left-margin">
						<div class="control-label">${uiLabelMap.SelectTypeLocationFacility}: </div>
						<div class="controls">
							<div id="listLocationFacilityType"></div>
						</div>
					</div>
				</div>
			</div>
		</div>	
	</div>
</div>

<div id="CreateNewLocationFacility" class="hide">
	<div>${uiLabelMap.ProductNewLocationType}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="form-horizontal">
					<div id="contentNotifiCheckCreateLocationFacility"></div>
					<div class="control-group no-left-margin">
						<div class="control-label">${uiLabelMap.ParentLocationId}: </div>
						<div class="controls">
							<div id="listParentLocationId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div class="control-label">${uiLabelMap.DescriptionLocationTypeId}: </div>
						<div class="controls">
							<input id="locationTypeId"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div class="control-label">${uiLabelMap.LocationTypeIdDescription}: </div>
						<div class="controls">
							<input id="description"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
				    </div>
				    <div class="control-group no-left-margin">
				    </div>
				    <div class="control-group no-left-margin">
					<div class="controls">
						<input style="margin-right: 5px;" type="button" id="alterCreate" value="${uiLabelMap.CommonSave}" />
				       	<input id="alterExit" type="button" value="${uiLabelMap.CommonCancel}" />  
					</div>      	
			    </div>
				</div>
			</div>
		</div>	
	</div>
</div> 

<div>
	<div id="contentNotificationContentDeleteLocationFacilityError" style="width:100%"></div>
	<div id="contentNotificationContentCreateLocationFacility" style="width:100%"></div>
	<div id="contentNotificationCheckDescription" style="width:100%">
	</div>
	<div id="contentNotificationLocationFacilityUpdateSuccess" style="width:100%">
	</div>
	<div id="contentNotificationLocationFacilityUpdateError" style="width:100%">
	</div>
	<div id="contentNotificationLocationFacilityUpdateErrorParent" style="width:100%">
	</div>
	<div id="contentNotificationLocationFacilityDeleteSuccess" style="width:100%">
	</div>
	<div id="contentNotificationCreateInventoryItemSuccess" style="width:100%">
	</div>
	<div id="myTree">
	</div>
	<div id='Menu' class="hide">
	    <ul>
	        <li>${uiLabelMap.DSDeleteRowGird}</li>
	        <li>${uiLabelMap.DSEditRowGird}</li>
	    </ul>
	</div>
</div>

<#include "moveProductByLocationToLocationInFacility.ftl" />
<#include "moveProductByLocationToLocationInFacilityContrary.ftl" />

<div id="jqxMessageNotificationSelectMyTree">
	<div id="notificationContentSelectMyTree">
	</div>
</div>

<div id="jqxMessageNotificationSelectMyTreeFullSelect">
	<div id="notificationContentSelectMyTreeFullSelect">
	</div>
</div>

<div id="jqxNotificationNestedContrary" >
	<div id="notificationContentNestedContrary">
	</div>
</div>

<div id="jqxNotificationCheckCreateLocationFacility" >
	<div id="notificationCheckCreateLocationFacility">
	</div>
</div>

<div id="jqxNotificationCreateLocationFacilitySuccess" >
	<div id="notificationContentCreateLocationFacilitySuccess">
	</div>
</div>

<div id="jqxNotificationInventoryItemLocationSuccess" >
	<div id="notificationContentInventoryItemLocationSuccess">
	</div>
</div>

<div id="jqxNotificationCheckCreateLocationFacility" >
	<div id="notificationCheckContentCreateLocationFacility">
	</div>
</div>

<div id="jqxMessageNotificationCheckDescription">
	<div id="notificationContentCheckDescription">
	</div>
</div>

<div id="jqxNotificationUpdateSuccess" >
	<div id="notificationContentUpdateSuccess">
	</div>
</div>

<div id="jqxNotificationUpdateError" >
	<div id="notificationContentUpdateError">
	</div>
</div>

<div id="jqxNotificationUpdateErrorParent" >
	<div id="notificationContentUpdateErrorParent">
	</div>
</div>
<div id="jqxNotificationDeleteSuccess" >
	<div id="notificationContentDeleteSuccess">
	</div>
</div>

<div id="jqxNotificationDeleteLocationFacilityError" >
	<div id="notificationContentDeleteLocationFacilityError">
	</div>
</div>

<script>
//Create theme
	$.jqx.theme = 'olbius';  
theme = $.jqx.theme;

$("#CreateNewLocation").jqxWindow({
    width: 600 ,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterExit"), modalOpacity: 0.7           
});

$("#jqxMessageNotificationSelectMyTree").jqxNotification({ width: "100%", appendContainer: "#contentMessageNotificationSelectMyTree", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationCreateLocationFacilitySuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateLocationFacility", opacity: 0.9, autoClose: true, template: "success" });
$("#jqxNotificationCheckCreateLocationFacility").jqxNotification({ width: "100%", appendContainer: "#contentNotifiCheckCreateLocationFacility", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxMessageNotificationCheckDescription").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckDescription", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationUpdateSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationLocationFacilityUpdateSuccess", opacity: 0.9, autoClose: true, template: "success" });
$("#jqxNotificationUpdateError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationLocationFacilityUpdateError", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationUpdateErrorParent").jqxNotification({ width: "100%", appendContainer: "#contentNotificationLocationFacilityUpdateErrorParent", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationDeleteSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationLocationFacilityDeleteSuccess", opacity: 0.9, autoClose: true, template: "success" });
$("#jqxNotificationInventoryItemLocationSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateInventoryItemSuccess", opacity: 0.9, autoClose: true, template: "success" });
$("#jqxMessageNotificationSelectMyTreeFullSelect").jqxNotification({ width: "100%", appendContainer: "#contentNotificationSelectMyTreeFullSelect", opacity: 0.9, autoClose: true, template: "info" });
$("#jqxNotificationDeleteLocationFacilityError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentDeleteLocationFacilityError", opacity: 0.9, autoClose: true, template: "error" });

$("#jqxButtonAddProduct").jqxButton({ height: 30});
$("#jqxButtonStockLocation").jqxButton({ height: 30});
$("#jqxButtonStockLocationContrary").jqxButton({ height: 30});
$("#showWindowButton").jqxButton({ height: 30});
$("#jqxButtonDeleteLocationFacility").jqxButton({ height: 30});

$("#alterCreate").jqxButton({ height: 30, width: 80 });
$("#alterExit").jqxButton({ height: 30, width: 80 });
$("#locationTypeId").jqxInput();
$("#description").jqxInput();
$("#CreateNewLocationFacility").jqxWindow({
    width: 600 ,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterExit"), modalOpacity: 0.7           
});

var facilityId = '${facilityId}';
function loadParentLocationFacilityTypeIdInFacility(){
	var request = $.ajax({
		  url: "loadParentLocationFacilityTypeIdInFacility",
		  type: "POST",
		  data: null,
		  dataType: "json",
		  success: function(data) {
		  }
	});
	request.done(function(data) {
		var listParentLocationFacilityTypeMap = data["listParentLocationFacilityTypeMap"];
		bindingDataToJqxDropDownListLocationFacilityType(listParentLocationFacilityTypeMap);
		listParentLocationFacilityTypeMap = [];
	});
}

$("#showWindowButton").click(function (){
	loadParentLocationFacilityTypeIdInFacility();
	$('#CreateNewLocation').jqxWindow('open');
});

function bindingDataToJqxDropDownListLocationFacilityType(dataBinding){
	var soureValue = $("#listLocationFacilityType").jqxDropDownList({source: dataBinding, placeHolder: "Please select...." ,displayMember: 'description', valueMember: 'locationFacilityTypeId', width:'211px'});
}


$("#listLocationFacilityType").on('select', function (event) {
    if (event.args) {
        var item = event.args.item;
    }
    var locationFacilityTypeId = item.value;
    loadLocationFacilityByFacility(locationFacilityTypeId);
    $("#listParentLocationId").jqxDropDownList('setContent', 'Please select....'); 
	$("#listParentLocationId").jqxDropDownList('clearSelection');
});

function loadLocationFacilityByFacility(locationFacilityTypeId){
	var request = $.ajax({
		url: "loadLocationFacilityTypeId",
		type: "POST",
		data: {facilityId: facilityId, locationFacilityTypeId: locationFacilityTypeId},
		dataType: "json",
		success: function(data) {
			var listLocationFacilityTypeId = data["listlocationFacilityMap"];
			if(listLocationFacilityTypeId.length == 0){
				$("#listParentLocationId").jqxDropDownList('setContent', '${uiLabelMap.DSCommonNoParentLocationIdExits}'); 
				$("#listParentLocationId").jqxDropDownList({ disabled: true }); 
			}else{
				$("#listParentLocationId").jqxDropDownList({ disabled: false, selectedIndex: 0, placeHolder: 'Please select....' , source: listLocationFacilityTypeId, displayMember: 'description', valueMember: 'locationId', width:'211px'});
				$("#listParentLocationId").jqxDropDownList('setContent', 'Please select....'); 
			}
			listLocationFacilityTypeId = [];
		}
	});
	request.done(function(data) {
		locationFacilityTypeId = null;
	});
    
	if(locationFacilityTypeId != ""){
		$('#CreateNewLocationFacility').jqxWindow('open');
	}
}

$('#CreateNewLocationFacility').on('close', function (event) {
	$("#listParentLocationId").jqxDropDownList('setContent', 'Please select....'); 
	$("#listParentLocationId").jqxDropDownList('clearSelection');  
	$("#listLocationFacilityType").jqxDropDownList('setContent', 'Please select....'); 
	$("#listLocationFacilityType").jqxDropDownList('clearSelection');
}); 

$('#CreateNewLocation').on('close', function (event) {
	$("#listLocationFacilityType").jqxDropDownList('setContent', 'Please select....'); 
	$("#listLocationFacilityType").jqxDropDownList('clearSelection');
}); 

$("#alterCreate").click(function (){
	var locationCode = $('#locationTypeId').val();
	var locationFacilityTypeId = $('#listLocationFacilityType').val();
	var parentLocationId = $('#listParentLocationId').val();
	var description = $('#description').val();
	if(locationCode == "" || description == ""){
		$("#notificationCheckCreateLocationFacility").text('${StringUtil.wrapString(uiLabelMap.DSCheckIsEmptyCreateLocationFacility)}');
		$("#jqxNotificationCheckCreateLocationFacility").jqxNotification('open');
	}else{
		$.ajax({
			  url: "createNewLocationFacility",
			  type: "POST",
			  data: {facilityId: facilityId, locationCode: locationCode, parentLocationId: parentLocationId, locationFacilityTypeId: locationFacilityTypeId, description: description},
			  dataType: "html",
			  success: function(data) {
			  }
		}).done(function(data) {
			loadData();
			parentLocationId = " ";
			locationFacilityTypeId = " ";
			$('#locationTypeId').val("");
			$('#description').val("");
			$("#listLocationFacilityType").jqxDropDownList('setContent', 'Please select');
			$("#listParentLocationId").jqxDropDownList('setContent', 'Please select'); 
			$("#notificationContentCreateLocationFacilitySuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiCreateSucess)}');
			$("#jqxNotificationCreateLocationFacilitySuccess").jqxNotification('open');
			$("#CreateNewLocationFacility").jqxWindow('close');
		    $("#CreateNewLocation").jqxWindow('close');
		});
	}
	$("#listParentLocationId").jqxDropDownList('setContent', 'Please select....'); 
	$("#listParentLocationId").jqxDropDownList('clearSelection');  
	$("#listLocationFacilityType").jqxDropDownList('setContent', 'Please select....'); 
	$("#listLocationFacilityType").jqxDropDownList('clearSelection'); 
});

$("#alterExit").click(function (){
	$("#listLocationFacilityType").jqxDropDownList('setContent', 'Please select...');
	$("#listLocationFacilityType").jqxDropDownList('clearSelection'); 
	$("#listParentLocationId").jqxDropDownList('setContent', 'Please select...'); 
	$("#listParentLocationId").jqxDropDownList('clearSelection'); 
	
	$('#locationTypeId').val("");
	$('#description').val("");
});

var dataRow = new Array();
var kerArray = new Array();

$('#myTree').on('rowCheck', function (event) {
	var args = event.args;
    var row = args.row;
    var key = args.key;
    kerArray.push(key);
    dataRow.push(row);
});

$('#myTree').on('rowUncheck', function (event) {
	var row = args.row;
	var ii = dataRow.indexOf(row);
	dataRow.splice(ii, 1);
});

$("#jqxButtonDeleteLocationFacility").click(function (){
	if(dataRow.length == 0){
		$("#notificationContentSelectMyTree").text('${StringUtil.wrapString(uiLabelMap.SelectJqxTreeGirdToDelete)}');
     	$("#jqxMessageNotificationSelectMyTree").jqxNotification('open');
	}
	else{
		var locationIdData = [];
		for(var i= 0; i < dataRow.length; i++){
			locationIdData.push(dataRow[i].locationId);
		}
		deleteLocationFacility(locationIdData);
	}
	$("#myTree").jqxTreeGrid('clearSelection');
});

function deleteLocationFacility(locationIdData){
	$.ajax({
		  url: "deleteLocationFacility",
		  type: "POST",
		  data: {locationId: locationIdData},
		  dataType: "json",
		  success: function(data) {
			  var value = data["value"];
			  if(value == "error"){
				  $("#notificationContentDeleteLocationFacilityError").text('${StringUtil.wrapString(uiLabelMap.DSCheckLinkedTableData)}');
				  $("#jqxNotificationDeleteLocationFacilityError").jqxNotification('open');
			  }
			  else{
				  $("#notificationContentDeleteSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiDeleteSucess)}');
				  $("#jqxNotificationDeleteSuccess").jqxNotification('open');
			  }
		  }    
	}).done(function(data) {
		dataRow = [];
		loadData();
	});
	$("#myTree").jqxTreeGrid('clearSelection');
}

$("#dialogTranferProductToLocation").jqxWindow({
    resizable: false,
    width: '80%',
    height: 350,
    autoOpen: false,
    resizable: false, 
    isModal: true, 
    autoOpen: false, 
    cancelButton: $("#cancel"), 
    modalOpacity: 0.7
});


$('#jqxButtonAddProduct').click(function () {
	loadJqxTreeAndDetailOfInventoryItem();;
});

function loadJqxTreeAndDetailOfInventoryItem(){
	if(dataRow.length == 0){
    	$("#notificationContentSelectMyTree").text('${StringUtil.wrapString(uiLabelMap.SelectJqxTreeGird)}');
     	$("#jqxMessageNotificationSelectMyTree").jqxNotification('open');
    }else{
    	checkParentLocationIdInDataRow(dataRow);
    }
	loadData();
	dataRow = []; 
	$("#myTree").jqxTreeGrid('clearSelection');
}

$('#jqxButtonStockLocation').click(function () {
	loadDataRowDeatailByjqxTreeGirdClickJqxButtonStockLocation();
	$("#myTree").jqxTreeGrid('clearSelection');
});


$('#jqxButtonStockLocationContrary').click(function () {
	loadDataDetailByTreeGirdContraryWhenClickSaveContrary();
	$("#myTree").jqxTreeGrid('clearSelection');
});

function resultLoadProductByLocationIdInFacility(result, locationIdTranfer, locationIdTranferLable){
	var locationCode = locationIdTranferLable;
	var locationId = locationIdTranfer;
	var data = [];
	for(var i in locationCode){
		var locationFacility = {};
		locationFacility["locationId"] = locationId[i];
		locationFacility["locationCode"] = locationCode[i];
		data.push(locationFacility);
	}
	var source =
    {
        localdata: data,
        datatype: "local",
        datafields:
        [
         	{ name: 'locationId', type: 'string' },
            { name: 'locationCode', type: 'string' },
        ]
    };
    var dataAdapter = new $.jqx.dataAdapter(source);
    loadRowsDeatilsOfjqxgridProductByLocationInFacility(dataAdapter, result, locationId);
}

function loadRowsDeatilsOfjqxgridProductByLocationInFacility(dataAdapter, result, resultLocationId){
	var arrayValue;
	var arrayRowDetails = new Array();
	
	var values = [];
	for(var i = 0; i < resultLocationId.length;  i++){
		var locationId = resultLocationId[i];
		for (var k in result) {
		  if(locationId == k){
			  if (result.hasOwnProperty(k)) {
	    	    values.push(result[k]);
			  }
	    }
	    	}
	    	var values = Object.getOwnPropertyNames(result).map(function(key) {
	    	    return result[key];
	    	});
		
	}
	
	var inventoryItemId = [];
	var productId = new Array();
	var quantity = new Array();
	var uomId = new Array();
	var locationIdData = new Array();
	for (var valueArray in values){
		var arrayValue = values[valueArray];
		for(var a in arrayValue){
			inventoryItemId.push(arrayValue[a].inventoryItemId);
			locationIdData.push(arrayValue[a].locationId);
			productId.push(arrayValue[a].productId);
			quantity.push(arrayValue[a].quantity);
			uomId.push(arrayValue[a].uomId);
		}
	}
	
	for(var i = 0; i < locationIdData.length; i++){
		var inventoryItemIdDetails = inventoryItemId[i];
		var locationIdDetails = locationIdData[i];
		var quantityDetails = quantity[i];
		var productIdDetails = productId[i];
		var uomIdDetails = uomId[i];
		arrayValue = {
			inventoryItemId: inventoryItemIdDetails,
			locationId: locationIdDetails,
			productId: productIdDetails,
			quantity: quantityDetails,
			uomId: uomIdDetails,
		};
		arrayRowDetails.push(arrayValue);
	}
//	loadDetailsJqxGird(arrayRowDetails, dataAdapter);
	loadDetailsJqxGirdContrary(arrayRowDetails, dataAdapter);
}

$("#jqxNotificationNestedContrary").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentNestedContrary", opacity: 0.9, autoClose: true, template: "error" });

</script>




<script>    
    $(document).ready(function () {
    	loadData();
    });
    
    function loadData() {
    	$("#alterAddProduct").jqxButton({ disabled: false});
    	var facilityId = '${facilityId}';
    	var listlocationFacilityMap;
    	var listInventoryItemLocationDetailMap;
    	$.ajax({
    		  url: "loadListLocationFacility",
    		  type: "POST",
    		  data: {facilityId: facilityId},
    		  dataType: "json",
    		  success: function(data) {
    		  }    
    	}).done(function(data) {
    		listlocationFacilityMap = data["listlocationFacilityMap"];
    		for(var obj in listlocationFacilityMap){
    			listlocationFacilityMap[obj].expanded = true;
    		}
			listInventoryItemLocationDetailMap = data["listInventoryItemLocationDetailMap"];
			renderTree(listlocationFacilityMap, listInventoryItemLocationDetailMap);
    	});
    	unableJqxButtion();
    	$("#alterAddProduct").jqxButton({ disabled: false});
    	dataRow = [];
    }
    
    $('#myTree').on('rowEndEdit', function (event) {
    	disabledJqxButtion();
    	var args = event.args;
        var row = args.row;
        
        var rowKey = args.key;
        var locationId = row.locationId;
        var locationCode = row.locationCode;
        var parentLocationId = row.parentLocationId;
        var locationFacilityTypeId = row.locationFacilityTypeId;
        var description = row.description;
        
    	updateLocationFacility(locationId, facilityId, locationCode, parentLocationId, locationFacilityTypeId, description);
    });
    
    var locationFacilityTypeIdDataCheck;
    $('#myTree').on('rowBeginEdit', function (event) {
    	locationFacilityTypeIdDataCheck = "";
    	disabledJqxButtion();
    	var args = event.args;
        var row = args.row;
        locationFacilityTypeIdDataCheck = row.locationFacilityTypeId;
        loadParentLocationFacilityByLocationFacilityTypeId(locationFacilityTypeIdDataCheck);
    });

    var parentLocationFacilityTypeIdData = [];
    var resultLoadLocationParentFacilityTypeId = null; 
    function loadParentLocationFacilityByLocationFacilityTypeId(locationFacilityTypeIdData){
    	$.ajax({
    		url: "loadParentLocationFacilityByLocationFacilityTypeId",
    		type: "POST",
    		data: {locationFacilityTypeId: locationFacilityTypeIdData},
    		dataType: "json",
    		async: false,
    		success: function(data) {
    		}
    	}).done(function(data) {
    		var listParentLocationFacilityTypeId = data["listParentLocationFacilityTypeId"];
    		if(listParentLocationFacilityTypeId.length == 0){
    			parentLocationFacilityTypeIdData = [];
    		}else{
    			for(var i in listParentLocationFacilityTypeId){
        			var listLocationFacility = {
        				locationId: listParentLocationFacilityTypeId[i].locationId,	
        				parentLocationId: listParentLocationFacilityTypeId[i].parentLocationId,
        				locationFacilityTypeId : listParentLocationFacilityTypeId[i].locationFacilityTypeId,
        				description: listParentLocationFacilityTypeId[i].description
        			}
        			parentLocationFacilityTypeIdData.push(listLocationFacility);
        		}
    		}
    	});
    	$(resultLoadLocationParentFacilityTypeId).jqxDropDownList({source: parentLocationFacilityTypeIdData, placeHolder: "Please select....", width: '100%', height: '100%', displayMember: 'description', valueMember: 'locationId'});
    	parentLocationFacilityTypeIdData = [];
    	resultLoadLocationParentFacilityTypeId = "";
    	resultLoadLocationParentFacilityTypeId = null;
    }

    function updateLocationFacility(locationId, facilityId, locationCode, parentLocationId, locationFacilityTypeId, description){
    	if(description == "" || locationCode == ""){
    		$("#notificationContentCheckDescription").text('${StringUtil.wrapString(uiLabelMap.DSCheckDescription)}');
    		$("#jqxMessageNotificationCheckDescription").jqxNotification('open');
        }else{
    		$.ajax({
    			url: "updateLocationFacility",
    			type: "POST",
    			data: {locationId: locationId, locationCode: locationCode, parentLocationId: parentLocationId, locationFacilityTypeId: locationFacilityTypeId, description: description},
    			dataType: "json",
    			/*success: function(data) {
    			}*/
    	  	}).done(function(data) {
    	  		var value = data["value"];
				if(value == "success"){
					$("#notificationContentUpdateSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
					$("#jqxNotificationUpdateSuccess").jqxNotification('open');
  			  	}
				if(value == "parentError"){
					$("#notificationContentUpdateError").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateError)}');
					$("#jqxNotificationUpdateError").jqxNotification('open');
  			  	}	
				if(value == "errorParent"){
					$("#notificationContentUpdateErrorParent").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateErrorParent)}');
					$("#jqxNotificationUpdateErrorParent").jqxNotification('open');
  			  	}
				if(value == "parentLocationFacilityTypeId"){
					$("#notificationContentUpdateErrorParent").text('${StringUtil.wrapString(uiLabelMap.KhongBietThongBaoGI)}');
					$("#jqxNotificationUpdateErrorParent").jqxNotification('open');
  			  	}
    	  		parentLocationFacilityTypeIdData = [];
    	  		description = "";
//    	  		$("#myTree").jqxTreeGrid('setCellValue', rowKey, 'parentLocationId', null);
    	  		resultLoadLocationParentFacilityTypeId = null;
    	  		loadData();
    	  	});
        }
    	
    	parentLocationFacilityTypeIdData = [];
    	resultLoadLocationParentFacilityTypeId = "";
    	resultLoadLocationParentFacilityTypeId = null;
    	locationFacilityTypeIdDataCheck = "";
    }	
    
    function unableJqxButtion(){
    	$('#showWindowButton').jqxButton({disabled: false });
        $('#jqxButtonAddProduct').jqxButton({disabled: false });
        $('#jqxButtonStockLocation').jqxButton({disabled: false });
        $('#jqxButtonDeleteLocationFacility').jqxButton({disabled: false });
        $('#jqxButtonStockLocationContrary').jqxButton({disabled: false });
    }
    
    function disabledJqxButtion(){
    	$('#showWindowButton').jqxButton({disabled: true });
        $('#jqxButtonAddProduct').jqxButton({disabled: true });
        $('#jqxButtonStockLocation').jqxButton({disabled: true });
        $('#jqxButtonDeleteLocationFacility').jqxButton({disabled: true });
        $('#jqxButtonStockLocationContrary').jqxButton({disabled: true });
    }
    
    function renderTree(dataList, rowDetails) {
    	var sourceData =
    	{
    		localData: dataList,
			dataType: "json",
		    dataFields: [
		        { name: 'locationId', type: 'string' },         
		    	{ name: 'facilityId', type: 'string' }, 
		    	{ name: 'locationCode', type: 'string' },
		    	{ name: 'parentLocationId', type: 'string' },
		    	{ name: 'locationFacilityTypeId', type: 'string' },
		    	{ name: 'description', type: 'string' },
		    	{ name: 'expanded', type: 'bool' },
		    ],
		    hierarchy:
		    {
		    	keyDataField: { name: 'locationId' },
		        parentDataField: { name: 'parentLocationId' }
		    },
		    id: 'locationId',
	    };
    	
	    bindingDataToJqxTreeGirdMyTree(sourceData, rowDetails);
    }    
    
    function bindingDataToJqxTreeGirdMyTree(sourceData, rowDetailsDataAdapter){
    	var dataAdapter = new $.jqx.dataAdapter(sourceData,{
 	    	beforeLoadComplete: function (records) {
     	    	for (var i = 0; i < records.length; i++) {
     	    		for(var key in rowDetailsDataAdapter){
     	    			if(records[i].locationId == key){
     	    				records[i].rowDetailData = rowDetailsDataAdapter[key];
     	    			}
     	    		}
     	    	}
     	    	return records;
 	    	}
 	    });
    	eventClickMyTree();
    	loadDataInMyTreeAndRowDetail(dataAdapter);
    }
    
    function loadDataInMyTreeAndRowDetail(dataAdapter){
    	$("#myTree").jqxTreeGrid({
	     	width: '100%',
	        source: dataAdapter,
	        rowDetails: true,
	        rowDetailsRenderer: function (rowKey, row) {
	        	var indent = (1+row.level) * 20;
	        	var rowDetailDataInLocation = row.rowDetailData;
	        	if(rowDetailDataInLocation.length == 0){
	        	}else{
	        		var detailsData = [];
		        	var details = "<table class='table table-striped table-bordered table-hover dataTable' style='margin: 10px; min-height: 95px; height: 95px; width:100%" + indent + "px;'>" +
									"<thead>" +
			        					"<tr>" +
						    				"<th>" + '${uiLabelMap.ProductProductId}' + "</th>" +
						    			  	"<th>" + '${uiLabelMap.Quantity}' + "</th>" +
						    			  	"<th>" + '${uiLabelMap.QuantityUomId}' + "</th>" +
						    		    "</tr>" +
					    		    "</thead>";
		        	for(var i in rowDetailDataInLocation){
		        		var checkIsEmpty = 
		        		'function (){' +
		        			'var thisArray = this;' +
		        			'if (thisArray.length == 0) {'+
		        				'return true;'
		        			'}'+
		        				'return false;'
		        		'}';
//		        		if(rowDetailDataInLocation[i].isEmpty != undefined){
		        			var productIdData = rowDetailDataInLocation[i].productId;
			        		var quantityData = rowDetailDataInLocation[i].quantity;
			        		var uomIdData = rowDetailDataInLocation[i].uomId;
//			        		if(productIdData != undefined && quantityData && undefined && uomIdData != undefined){
			        			details += "<tbody>" +
		        					"<tr>" +
				        				"<td>" + productIdData + "</td>" +
				        			  	"<td>" + quantityData + "</td>" +
				        			  	"<td>" + getUom(uomIdData) + "</td>" +
				        			"</tr>" +
				        		"</tbody>";
//			        		}
//		        		}
		        	}
		        	details += "</table>";
		        	detailsData.push(details);
	                return detailsData;
	        	}
             },
             hierarchicalCheckboxes: true,
             checkboxes: true,
             altRows: true,
             selectionMode: 'multipleRows',
             sortable: true,
             editable:true,
             ready: function()
             {
            	 $("#myTree").jqxTreeGrid('expandRow', "2");
             },
             columns: [
                       { text: '${uiLabelMap.FacilityLocationPosition}', dataField: 'locationCode', editable:true
                       },
                       { text: '${uiLabelMap.SelectTypeLocationFacility}',  dataField: 'locationFacilityTypeId', editable:false,
                    	   cellsRenderer : function (row, column, value, rowData) {
	                		   for (var i = 0 ; i < locationFacilityTypeData.length; i++){
	                			   if (value == locationFacilityTypeData[i].locationFacilityTypeId){
	                				   return '<span title = ' + locationFacilityTypeData[i].description +'>' + locationFacilityTypeData[i].description + '</span>';
	                			   }
	       					   }
	       					   return '<span title=' + value +'>' + value + '</span>';
                    	   }
                       },
                       { text: '${uiLabelMap.ParentLocationId}',  dataField: 'parentLocationId', columnType: "template", editable:true,
                    	   cellsRenderer : function(row, column, value, rowData){
                    		   for (var i = 0 ; i < parentLocationFacilityTypeIdData.length; i++){
	      							if (value == parentLocationFacilityTypeIdData[i].locationId){
	      								return '<span title = ' + parentLocationFacilityTypeIdData[i].description +'>' + parentLocationFacilityTypeIdData[i].description + '</span>';
	      							}
      						   }
      						   return '<span title=' + value +'>' + value + '</span>';
                    	   },	 
                    	   initEditor: function (row, cellValue, editor, cellText, width, height) {
                    		   resultLoadLocationParentFacilityTypeId = editor;
                    	   },
                       },
                       { text: '${uiLabelMap.Description}', dataField: 'description', editable:true,
                       },
                    ],
    	});
    }
    
    function eventClickMyTree(){
    	var contextMenu = $("#Menu").jqxMenu({ width: 200, height: 58, autoOpenPopup: false, mode: 'popup' });
    	$("#myTree").on('contextmenu', function () {
    		return false;
    	});
    	
    	$("#myTree").on('rowClick', function (event) {
        	var args = event.args;
            if (args.originalEvent.button == 2) {
            	var scrollTop = $(window).scrollTop();
                var scrollLeft = $(window).scrollLeft();
                contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                return false;
            }
        });
    	$("#Menu").on('itemclick', function (event) {
            var args = event.args;
            var selection = $("#myTree").jqxTreeGrid('getSelection');
            var rowid = selection[0].uid;
            var checkText = '${StringUtil.wrapString(uiLabelMap.DSEditRowGird)}';
            if ($.trim($(args).text()) == checkText) {
                $("#myTree").jqxTreeGrid('beginRowEdit', rowid);
            } else {
            	deleteLocationFacilityNotParentLocation(rowid);
                $("#myTree").jqxTreeGrid('deleteRow', rowid);
            }
        });
    	$("#myTree").jqxTreeGrid('clearSelection');
    }
    
    function deleteLocationFacilityNotParentLocation(locationId){
    	$.ajax({
  		  url: "deleteLocationFacilityNotParentLocation",
  		  type: "POST",
  		  data: {locationId: locationId},
  		  dataType: "json",
  		  success: function(data) {
  			  var value = data["value"];
  			  if(value == "error"){
  				  $("#notificationContentDeleteLocationFacilityError").text('${StringUtil.wrapString(uiLabelMap.DSCheckLinkedTableData)}');
  				  $("#jqxNotificationDeleteLocationFacilityError").jqxNotification('open');
  			  }
  			  else{
  				  $("#notificationContentDeleteSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiDeleteSucess)}');
  				  $("#jqxNotificationDeleteSuccess").jqxNotification('open');
  			  }
  		  }    
	  	}).done(function(data) {
	  		loadData();
	  	});
	  	$("#myTree").jqxTreeGrid('clearSelection');
    }
</script>
