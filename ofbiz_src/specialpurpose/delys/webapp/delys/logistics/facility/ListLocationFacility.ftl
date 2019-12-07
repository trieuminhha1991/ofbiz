<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/demos/sampledata/generatedata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script src="/delys/images/js/bootbox.min.js"></script>
<script>
	<#assign locationFacilityTypeList = delegator.findList("LocationFacilityType", null, null, null, null, false) />
	var locationFacilityTypeData = new Array();
	<#list locationFacilityTypeList as locationFacilityType>
		var row = {};
		row['locationFacilityTypeId'] = "${locationFacilityType.locationFacilityTypeId}";
		row['description'] = "${locationFacilityType.description}";
		locationFacilityTypeData[${locationFacilityType_index}] = row;
	</#list>

	function getLocationFacilityType(locationFacilityTypeId) {
		for ( var x in locationFacilityTypeData) {
			if (locationFacilityTypeId == locationFacilityTypeData[x].locationFacilityTypeId) {
				return locationFacilityTypeData[x].description+"["+locationFacilityTypeId+"]";
			}
		}
	}
	
	var arrayLocationFacilityTypeInLocationFacilityTypeData = [];
	for(var i in locationFacilityTypeData){
		arrayLocationFacilityTypeInLocationFacilityTypeData.push(locationFacilityTypeData[i].locationFacilityTypeId);
	}
</script>

<div>

	<div id="contentNotificationDeleteLocationFacilityTypeError" style="width:100%">
	</div>
	<div id="contentNotificationCreateLocationFacilityTypeSuccess" style="width:100%">
	</div>
	<div id="contentNotificationCheckDescription" style="width:100%">
	</div>
	<div id="contentNotificationCreateLocationFacilityTypeDeleteSuccess" style="width:100%">
	</div>
	<div id="contentNotificationCreateLocationFacilityTypeUpdateSuccess" style="width:100%">
	</div>
	<div id="contentNotificationCreateLocationFacilityTypeUpdateError" style="width:100%">
	</div>
	<div id="contentNotificationCreateLocationFacilityTypeUpdateErrorParent" style="width:100%">
	</div>
	<div id="contentNotificationSelectTreeGird" style="width:100%">
	</div>
	<div id="jqxTreeGirdLocationFacilityType">
	</div>
	<div id='Menu' class="hide">
		<ul>
		    <li>${uiLabelMap.DSDeleteRowGird}</li>
		    <li>${uiLabelMap.DSEditRowGird}</li>
		</ul>
	</div>
</div>

<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.AddLocationTypeInFacility}</div>
	<div>
		<div id="contentNotificationCreateLocationFacilityType" class="popup-notification">
		</div>
		<div id="contentNotificationCreateLocationFacilityTypeExits" class="popup-notification">
		</div>
		<div id="contentNotificationCreateLocationFacilityTypeError" class="popup-notification">
		</div>
		<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.LocationType)}</label>
			</div>  
			<div class="span7">
				<input id="locationFacilityTypeId">
				</input>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.ParentLocationFacilityTypeId)}</label>
			</div>  
			<div class="span7">
				<div id="parentLocationFacilityTypeId"></div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.description)}</label>
			</div>  
			<div class="span7">
				<input id="description"></input>
	   		</div>
	   	</div>
	   	<div class="form-action">
			<button id="alterCancel" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button id="alterSave" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>



<div id="alterpopupWindowEdit" class='hide'>
	<div>${uiLabelMap.LogEditLocationFacilityType}</div>
	<div>
		<div id="contentNotificationEditLocationFacilityTypeError" class="popup-notification">
		</div>
		<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.LocationType)}</label>
			</div>  
			<div class="span7">
				<input id="locationFacilityTypeIdEdit">
				</input>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.ParentLocationFacilityTypeId)}</label>
			</div>  
			<div class="span7">
				<div id="parentLocationFacilityTypeIdEdit"></div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.description)}</label>
			</div>  
			<div class="span7">
				<input id="descriptionEdit"></input>
	   		</div>
	   	</div>
	   	<div class="form-action">
			<button id="alterCancelEdit" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSaveEdit" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="jqxNotificationCreate" >
	<div id="notificationContentCreate">
	</div>
</div>

<div id="jqxNotificationEditLocationFacilityTypeError" >
	<div id="notificationContentEditLocationFacilityTypeError">
	</div>
</div>

<div id="jqxNotificationCreateSuccess" >
	<div id="notificationContentCreateSuccess">
	</div>
</div>

<div id="jqxNotificationCreateError" >
	<div id="notificationContentCreateError">
	</div>
</div>

<div id="jqxNotificationCreateExists" >
	<div id="notificationContentCreateExists">
	</div>
</div>

<div id="jqxNotificationUpdateSuccess" >
	<div id="notificationContentUpdateSuccess">
	</div>
</div>

<div id="jqxNotificationDeleteSuccess" >
	<div id="notificationContentDeleteSuccess">
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

<div id="jqxMessageNotificationSelectMyTree">
	<div id="notificationContentSelectMyTree">
	</div>
</div>

<div id="jqxMessageNotificationCheckDescription">
	<div id="notificationContentCheckDescription">
	</div>
</div>

<div id="jqxNotificationDeleteLocationFacilityTypeError">
	<div id="notificationContentDeleteLocationFacilityTypeError">
	</div>
</div>


<script>
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$("#alterpopupWindow").jqxWindow({
	    width: 600, height: 215, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:'olbius'           
	});
	
	$("#jqxNotificationCreate").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityType", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationCreateError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeError", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationCreateSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#jqxNotificationCreateExists").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeExits", opacity: 0.9, autoClose: true, template: "info" });
	$("#jqxNotificationUpdateSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeUpdateSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#jqxNotificationUpdateError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeUpdateError", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationUpdateErrorParent").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeUpdateErrorParent", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxMessageNotificationSelectMyTree").jqxNotification({ width: "100%", appendContainer: "#contentNotificationSelectTreeGird", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationDeleteSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateLocationFacilityTypeDeleteSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#jqxMessageNotificationCheckDescription").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckDescription", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationDeleteLocationFacilityTypeError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationDeleteLocationFacilityTypeError", opacity: 0.9, autoClose: true, template: "error" });
	
	function addLocationFacilityType(){
		$("#alterpopupWindow").jqxWindow('open');
	}
	
	function loadParentLocationFacilityTypeId(){
		$.ajax({
	  		  url: "loadParentLocationFacilityTypeIdInFacility",
	  		  type: "POST",
	  		  data: {},
	  		  dataType: "json",
	  		  async: false,
	  		  success: function(data) {
	  		  }
	  	}).done(function(data) {
	  		var listParentLocationFacilityTypeMap = data["listParentLocationFacilityTypeMap"];
	  		bindingParentLocationFacilityTypeId(listParentLocationFacilityTypeMap);
	  		bindingParentLocationFacilityTypeIdEdit(listParentLocationFacilityTypeMap);
	  	});
	}
	
	function bindingParentLocationFacilityTypeId(listParentLocationFacilityTypeMap){
		$("#parentLocationFacilityTypeId").jqxDropDownList({source: listParentLocationFacilityTypeMap, autoDropDownHeight: true ,placeHolder: '${uiLabelMap.LogPleaseSelect}' ,displayMember: 'description', valueMember: 'locationFacilityTypeId', width:'211',
			renderer: function (index, label, value) 
			{
			    var datarecord = listParentLocationFacilityTypeMap[index];
			    return datarecord.description +" ["+ datarecord.locationFacilityTypeId +"]";
			}
		});
	}
	
	$("#locationFacilityTypeId").jqxInput();
	$('#description').jqxInput();
	$('#alterpopupWindow').jqxValidator({
        rules: [
               { input: '#locationFacilityTypeId', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
               { input: '#locationFacilityTypeId', message: '${uiLabelMap.DSCheckCharacterValidate2To20}', action: 'keyup, blur', rule: 'length=2,20' },
               { input: '#description', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
               { input: '#description', message: '${uiLabelMap.DSCheckCharacterValidate2To10000}', action: 'keyup, blur', rule: 'length=2,10000' },
               ]
    });
	
	$("#alterSave").click(function (){
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		if(validate != false){
			var locationFacilityTypeId = $("#locationFacilityTypeId").val();
			var parentLocationFacilityTypeId = $("#parentLocationFacilityTypeId").val();
			var description = $("#description").val();
			if(description == '' && locationFacilityTypeId == ''){
				$("#notificationContentCreate").text('${StringUtil.wrapString(uiLabelMap.LogCheckDescription)}');
				$("#jqxNotificationCreate").jqxNotification('open');
			}
			else{
				document.getElementById("alterSave").disabled = true;
				createLocationFacilityType(locationFacilityTypeId, parentLocationFacilityTypeId, description);
			}
		}
	});
	
	function createLocationFacilityType(locationFacilityTypeId, parentLocationFacilityTypeId, description){
		$.ajax({
  		  url: "createLocationFacilityTypeInFacility",
  		  type: "POST",
  		  data: {locationFacilityTypeId: locationFacilityTypeId, parentLocationFacilityTypeId: parentLocationFacilityTypeId, description: description},
  		  dataType: "json",
  		  success: function(data) {
  		  }
	  	  }).done(function(data) {
	  		  var value = data["value"];
			  if(value == "error"){
				  $("#notificationContentCreateError").text('${StringUtil.wrapString(uiLabelMap.checkInventoryItemExits)}');
				  $("#jqxNotificationCreateError").jqxNotification('open');
				  document.getElementById("alterSave").disabled = false;
			  }
			  if(value == "descriptionMaxLength"){
				  $("#notificationContentCreateError").text('${StringUtil.wrapString(uiLabelMap.LogCheckDescriptionMaxLength)}');
				  $("#jqxNotificationCreateError").jqxNotification('open');
				  document.getElementById("alterSave").disabled = false;
			  }
			  if(value == "success"){
				  $("#notificationContentCreateSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiCreateSucess)}');
				  $("#jqxNotificationCreateSuccess").jqxNotification('open');
		  		  loadDataJqxTreeGirdLocationFacilityType();
		  		  $("#locationFacilityTypeId").val("");
		  		  $("#description").val("");
		  		  $('#alterpopupWindow').jqxWindow('close');
			  }
	  	 });
	}
	$('#alterpopupWindow').on('open', function (event) {
		$("#description").val("");
		loadParentLocationFacilityTypeId();
		document.getElementById("alterSave").disabled = false;
	});
	$('#alterpopupWindow').on('close', function (event) {
		$("#locationFacilityTypeId").val("");
		$("#parentLocationFacilityTypeId").jqxDropDownList('clearSelection'); 
		$("#locationFacilityTypeId").val("");
		$('#alterpopupWindow').jqxValidator('hide');
	});
	
	
	
	function deleteLocationFacilityType(locationFacilityTypeId){
		bootbox.confirm("${uiLabelMap.LogNotificationBeforeDelete}", function(result) {
            if(result) {
            	$.ajax({
      			  url: "deleteLocationFacilityTypeInFacility",
      			  type: "POST",
      			  data: {locationFacilityTypeId: locationFacilityTypeId},
      			  dataType: "json",
      			  success: function(data) {
      				  var value = data["value"];
      				  if(value == "error"){
      					  $("#notificationContentDeleteLocationFacilityTypeError").text('${StringUtil.wrapString(uiLabelMap.DSCheckLinkedTableData)}');
      					  $("#jqxNotificationDeleteLocationFacilityTypeError").jqxNotification('open');
      				  }
      				  else{
      					  $("#notificationContentDeleteSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiDeleteSucess)}');
      					  $("#jqxNotificationDeleteSuccess").jqxNotification('open');
      				  }
      			  }    
      		}).done(function(data) {
      			loadDataJqxTreeGirdLocationFacilityType();
      		});
            }
		});    
	}
	
	
	$("#locationFacilityTypeIdEdit").jqxInput();  
	$("#alterpopupWindowEdit").jqxWindow({
		width: 600, height: 215, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelEdit"), modalOpacity: 0.7           
	});
	$('#descriptionEdit').jqxInput();
	function bindingParentLocationFacilityTypeIdEdit(listParentLocationFacilityTypeMap){
		var objectData = {
			locationFacilityTypeId: null,
			parentLocationFacilityTypeId: null,
			description: '${uiLabelMap.LogTitleParentLocationFacilityTypeId}',
		};
		listParentLocationFacilityTypeMap.push(objectData);
		$("#parentLocationFacilityTypeIdEdit").jqxDropDownList({source: listParentLocationFacilityTypeMap, placeHolder: '${uiLabelMap.LogPleaseSelect}' ,displayMember: 'description', valueMember: 'locationFacilityTypeId', width:'211px', dropDownHeight: '150',
			renderer: function (index, label, value) 
			{
			    var datarecord = listParentLocationFacilityTypeMap[index];
			    if(datarecord != undefined){
			    	return datarecord.description +" ["+ datarecord.locationFacilityTypeId +"]";
			    }
			}
		});
	}
	$("#jqxNotificationEditLocationFacilityTypeError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationEditLocationFacilityTypeError", opacity: 0.9, autoClose: true, template: "error" });
	
	$('#alterpopupWindowEdit').on('open', function (event) {
		$('#locationFacilityTypeIdEdit').jqxInput({disabled: true });
		loadParentLocationFacilityTypeId();
	});
	
	$('#alterpopupWindowEdit').jqxValidator({
        rules: [
               { input: '#locationFacilityTypeIdEdit', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
               { input: '#locationFacilityTypeIdEdit', message: '${uiLabelMap.DSCheckCharacterValidate2To20}', action: 'keyup, blur', rule: 'length=2,20' },
               { input: '#descriptionEdit', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
               { input: '#descriptionEdit', message: '${uiLabelMap.DSCheckCharacterValidate2To10000}', action: 'keyup, blur', rule: 'length=2,10000' },
               ]
    });
	
	$("#alterSaveEdit").click(function (){
		var validate = $('#alterpopupWindowEdit').jqxValidator('validate');
		if(validate != false){
			var locationFacilityTypeId = $("#locationFacilityTypeIdEdit").val();
			var parentLocationFacilityTypeId = $("#parentLocationFacilityTypeIdEdit").val();
			var description = $("#descriptionEdit").val();
			if(description == ""){
				$("#notificationContentEditLocationFacilityTypeError").text('${StringUtil.wrapString(uiLabelMap.LogCheckDescription)}');
				$("#jqxNotificationEditLocationFacilityTypeError").jqxNotification('open');
			}
			else{
				document.getElementById("alterSaveEdit").disabled = true;
				$.ajax({
		    		  url: "updateLocationFacilityTypeInFacility",
		    		  type: "POST",
		    		  data: {locationFacilityTypeId: locationFacilityTypeId, parentLocationFacilityTypeId: parentLocationFacilityTypeId, description: description},
		    		  dataType: "json",
		    		  success: function(data) {
		    			  var value = data["value"];
		    			  if(value == "success"){
		    				  $("#notificationContentUpdateSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
		    				  $("#jqxNotificationUpdateSuccess").jqxNotification('open');
		    				  $("#alterpopupWindowEdit").jqxWindow('close');
		    			  }
		    			  if(value == "parentError"){
		    				  $("#notificationContentEditLocationFacilityTypeError").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateError)}');
		    				  $("#jqxNotificationEditLocationFacilityTypeError").jqxNotification('open');
		    				  document.getElementById("alterSaveEdit").disabled = false;
		    			  }
		    			  if(value == "errorParent"){
		    				  $("#notificationContentEditLocationFacilityTypeError").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateErrorParent)}');
		    				  $("#jqxNotificationEditLocationFacilityTypeError").jqxNotification('open');
		    				  document.getElementById("alterSaveEdit").disabled = false;
		    			  }
		    			  if(value == "notEdit"){
		    				  $("#alterpopupWindowEdit").jqxWindow('close');
		    			  }
		    		  }
		    	}).done(function(data) {
		    		loadDataJqxTreeGirdLocationFacilityType();
		    	});
			}
		}
	});
	$('#alterpopupWindowEdit').on('close', function (event) {
		$("#parentLocationFacilityTypeIdEdit").jqxDropDownList('clearSelection'); 
		$('#alterpopupWindowEdit').jqxValidator('hide');
		document.getElementById("alterSaveEdit").disabled = false;
	});
	$('#alterpopupWindowEdit').on('open', function (event) {
		$('#alterpopupWindowEdit').jqxValidator('hide');
		document.getElementById("alterSaveEdit").disabled = false;
	});
</script>	
<script>
	$(document).ready(function () {
		loadDataJqxTreeGirdLocationFacilityType();
	});
	function loadDataJqxTreeGirdLocationFacilityType() {
		var listlocationFacilityTypeMap;
		$.ajax({
			  url: "loadListLocationFacilityTypeInFacility",
			  type: "POST",
			  data: {},
			  dataType: "json",
			  success: function(data) {
				  listlocationFacilityTypeMap = data["listlocationFacilityTypeMap"];
			  }    
		}).done(function(data) {
			renderTree(listlocationFacilityTypeMap);
		});
	}
	
	var contextMenu = $("#Menu").jqxMenu({ width: 200, height: 58, autoOpenPopup: false, mode: 'popup' });
	$("#jqxTreeGirdLocationFacilityType").on('contextmenu', function () {
		return false;
	});
	
	$("#jqxTreeGirdLocationFacilityType").on('rowClick', function (event) {
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
        var selection = $("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('getSelection');
        var rows = $("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('getRows');
        var rowid = selection[0].uid;
        var checkText = '${StringUtil.wrapString(uiLabelMap.DSEditRowGird)}';
        if ($.trim($(args).text()) == checkText) {
        	bindingEditLocationFacilityType(rowid);
        } else {
        	deleteLocationFacilityType(rowid);
        }
    });
	
	function bindingEditLocationFacilityType(locationFacilityTypeId){
		$.ajax({
			  url: "bindingEditLocationFacilityType",
			  type: "POST",
			  data: {locationFacilityTypeId: locationFacilityTypeId},
			  dataType: "json",
			  success: function(data) {
				 var listLocationFacilityType = data["listLocationFacilityType"];
				 renderDataToInputByEditLocationFacilityType(listLocationFacilityType);
			  }    
		}).done(function(data) {
		});
	}
	
	function renderDataToInputByEditLocationFacilityType(listLocationFacilityType){
		for(var i in listLocationFacilityType){
			$("#locationFacilityTypeIdEdit").val(listLocationFacilityType[i].locationFacilityTypeId);
			var description =  getLocationFacilityType(listLocationFacilityType[i].parentLocationFacilityTypeId);
			$("#parentLocationFacilityTypeIdEdit").jqxDropDownList('setContent', description); 
			$("#parentLocationFacilityTypeIdEdit").val(listLocationFacilityType[i].parentLocationFacilityTypeId);
			$("#descriptionEdit").val(listLocationFacilityType[i].description);
		}
		$('#alterpopupWindowEdit').jqxWindow('open');
	}
	
	$("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('clearSelection');
	function renderTree(dataList) {
    	var source =
    	{
			dataType: "json",
		    dataFields: [
		        { name: 'locationFacilityTypeId', type: 'string' },         
		    	{ name: 'parentLocationFacilityTypeId', type: 'string' },
		    	{ name: 'description', type: 'string' },
		    ],
		    hierarchy:
		    {
		    	keyDataField: { name: 'locationFacilityTypeId' },
		        parentDataField: { name: 'parentLocationFacilityTypeId' }
		    },
		    id: 'locationFacilityTypeId',
		    localData: dataList
	    };
	    var dataAdapter = new $.jqx.dataAdapter(source, {
            loadComplete: function () {
            }
        });
         // create Tree Grid
	    bindingDataToJqxTreeGirdAddProductToLocation(dataAdapter);
    }  
	
	function bindingDataToJqxTreeGirdAddProductToLocation(dataAdapter){
		$("#jqxTreeGirdLocationFacilityType").jqxTreeGrid(
		{
	     	width: '100%',
	        source: dataAdapter,
            altRows: true,
            editable:true,
            ready: function()
            {
            	$("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('expandRow', '1');
            	$("#jqxTreeGirdLocationFacilityType").jqxTreeGrid('expandRow', '2');
            },
            columns: [
                       { text: '${uiLabelMap.SelectTypeLocationFacility}', dataField: 'locationFacilityTypeId', editable:false},
                       { text: '${uiLabelMap.ParentLocationFacilityTypeId}',  dataField: 'parentLocationFacilityTypeId', columnType: "template", editable:false },
                       { text: '${uiLabelMap.Description}', dataField: 'description', editable:false },
                    ],
		 });
	}
	
</script>