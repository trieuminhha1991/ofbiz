<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/demos/sampledata/generatedata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script>
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
	
	<#-- assign listPartyNameView = delegator.findList("PartyNameView", null, null, null, null, false) />
	var partyNameViewData = new Array();
	<list listPartyNameView as partyNameView>
		var row = {};
		row['partyId'] = "${partyNameView.partyId}";
		row['groupName'] = "${partyNameView.get('groupName', locale)?if_exists}";
		partyNameViewData[${partyNameView_index}] = row;
	</list> -->
	
	function getDescriptionByPartyNameViewId(partyId) {
		/*for ( var x in partyNameViewData) {
			if (partyId == partyNameViewData[x].partyId) {
				return partyNameViewData[x].groupName + ' ['+ partyId +']';
			}
		}*/
		// send ajax request
		
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

<div id="alterpopupWindowEdit" class='hide'>
	<div>${uiLabelMap.ProductNewFacility}</div>
	<div class='form-window-container margin-top10'>
		<div class='form-window-content' style="overflow-x: hidden">
			<div class='row-fluid margin-bottom10'>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.facilityId)}</label>
					</div>  
					<div class="span7">
						<input id="facilityIdEdit" style="height: 22px"></input>
			   		</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.FacilityName)}</label>
					</div>  
					<div class="span7">
						<input id="facilityNameEdit" style="height: 22px">
						</input>
					</div>
				</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.ProductFacilityType)}</label>
					</div>  
					<div class="span7">
						<div id="facilityTypeIdEdit">
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.GroupFacility)}</label>
					</div>  
					<div class="span7">
						<div id="primaryFacilityGroupIdEdit"></div>
			   		</div>
				</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.Owner)}</label>
					</div>  
					<div class="span7">
						<div id="ownerPartyIdEdit">
							<div id="jqxgridPartyId">
				            </div>
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.ParentFacility)}</label>
					</div>  
					<div class="span7">
						<div id="parentFacilityIdEdit">
							<div id="jqxgridParentFacility">
							</div>
						</div>
					</div>
				</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
			   	<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.SquareFootage)}</label>
					</div>  
					<div class="span7">
						<div id="facilitySizeEdit"></div>
			   		</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.facilitySizeUomId)}</label>
					</div>  
					<div class="span7">
						<div id="facilitySizeUomIdEdit">
						</div>
					</div>
				</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10' style="margin-left: 31px;">
		   		<div class='span2 text-algin-right'>
					<label>${StringUtil.wrapString(uiLabelMap.CommonDescription)}</label>
				</div>  
				<div class="span10">
					<textarea  id="descriptionEdit"></textarea >
				</div>
			</div>
			<div class="form-action">
		        <div class='row-fluid'>
		            <div class="span12 margin-top20" style="margin-bottom:10px;">
		                <button id="alterCancelEdit" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		                <button id="alterSaveEdit" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		            </div>
		        </div>
		    </div>
	    </div>
	</div>
</div>


<script>
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$('#facilityIdEdit').jqxInput({disabled: true});
	$('#facilityNameEdit').jqxInput();
	$('#facilitySizeEdit').jqxNumberInput({ width: '210px', inputMode: 'simple', decimalDigits: 0, min: 0});
	$('#ownerPartyIdEdit').jqxDropDownButton({ width: '210px'});
	$('#parentFacilityIdEdit').jqxDropDownButton({ width: '210px'});
	$("#facilityTypeIdEdit").jqxDropDownList({source: facilityTypeData, autoDropDownHeight: true ,placeHolder: '${uiLabelMap.LogPleaseSelect}' ,displayMember: 'description', valueMember: 'facilityTypeId', width:'210',});
	$("#primaryFacilityGroupIdEdit").jqxDropDownList({source: facilityGroupData, autoDropDownHeight: true ,placeHolder: '${uiLabelMap.LogPleaseSelect}' ,displayMember: 'facilityGroupName', valueMember: 'facilityGroupId', width:'210',});
	$("#facilitySizeUomIdEdit").jqxDropDownList({source: uomData, autoDropDownHeight: true ,placeHolder: '${uiLabelMap.LogPleaseSelect}' ,displayMember: 'description', valueMember: 'uomId', width:'210',});
	$("#ownerPartyIdEdit").jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}');
	$("#parentFacilityIdEdit").jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}');
	$("#alterpopupWindowEdit").jqxWindow({
		maxWidth: 1000, minWidth: 700, height: 510, width:850, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelEdit"), modalOpacity: 0.7, theme:theme           
    });
	
	var checkUpdateFacility;
	function loadDataEditByFacility(data, checkUpdateFacilityByEdit){
		var facilityId = data.facilityId;
		var facilityName = data.facilityName;
		var facilitySize = data.facilitySize;
		var facilitySizeUomId = data.facilitySizeUomId;
		var description = data.description;
		var facilityNameP = data.facilityNameP;
		var facilityGroupName = data.facilityGroupName;
		var facilityTypeId = data.facilityTypeId;
		var uomDesc = data.uomDesc;
		var ownerPartyId = data.ownerPartyId;
		checkUpdateFacility = checkUpdateFacilityByEdit;
		bindingDataByEditFacility(facilityId, facilityName, facilitySize, facilitySizeUomId, description, facilityNameP, facilityGroupName, ownerPartyId, facilityTypeId);
	}
	
	function bindingDataByEditFacility(facilityId, facilityName, facilitySize, facilitySizeUomId, description, facilityNameP, facilityGroupName, ownerPartyId, facilityTypeId){
		listPartyByPartyId();
		loadParentIdByFacilityId();
		$('#facilityIdEdit').val(facilityId);
		$('#facilityNameEdit').val(facilityName);
		$('#descriptionEdit').jqxEditor({
	        height: "200px",
	        width: '638px',
	    });
		if(facilitySize != null){
			$('#facilitySizeEdit').val(facilitySize);
		}
		if(facilitySizeUomId != null){
			$("#facilitySizeUomIdEdit").val(facilitySizeUomId);
		}
		if(description != null){
			$("#descriptionEdit").val(description);
		}
		if(facilityNameP != null){
			$("#parentFacilityIdEdit").val(facilityNameP);
		}
		if(facilityGroupName != null){
			$("#primaryFacilityGroupIdEdit").val(facilityGroupName);
		}
		if(ownerPartyId != null){
			$("#ownerPartyIdEdit").val(ownerPartyId);
		}
		if(facilityTypeId != null){
			$("#facilityTypeIdEdit").val(facilityTypeId);
		}
		$('#alterpopupWindowEdit').jqxWindow('setTitle', "${uiLabelMap.LogFacilityUpdateByFacilityId}: "  +  facilityName);
		$('#alterpopupWindowEdit').jqxWindow('open');
	}
	
	
	$("#alterSaveEdit").click(function () {
		var facilityId = $('#facilityIdEdit').val();
		var facilityName = $('#facilityIdEdit').val();
		var facilitySize = $('#facilitySizeEdit').val();
		var facilitySizeUomId = $('#facilitySizeUomIdEdit').val();
		var description = $('#descriptionEdit').val();
		var parentFacilityId = $('#parentFacilityIdEdit').val();
		var primaryFacilityGroupId = $('#primaryFacilityGroupIdEdit').val();
		var ownerPartyId = $('#ownerPartyIdEdit').val();
		var facilityTypeId = $('#facilityTypeIdEdit').val();
		updateFacilityByFacilityId(facilityId, facilityName, facilitySize, facilitySizeUomId, description, parentFacilityId, primaryFacilityGroupId, ownerPartyId, facilityTypeId);
    });
	
	
	function updateFacilityByFacilityId(facilityId, facilityName, facilitySize, facilitySizeUomId, description, parentFacilityId, primaryFacilityGroupId, ownerPartyId, facilityTypeId){
		if(parentFacilityId == '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'){
			parentFacilityId = null;
		}
		if(ownerPartyId == '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'){
			ownerPartyId = null;
		}
		if(parentFacilityId != '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'){
			parentFacilityId = parentFacilityIdData;
		}
		if(ownerPartyId != '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'){
			ownerPartyId = ownerPartyIdEditData;
		}
		bootbox.confirm("${uiLabelMap.LogUpdateBootboxConfirmSure}", function(result) {
            if(result) {
            	$.ajax({
        			url: "updateFacilityByFacilityId",
        			type: "POST",
        			async: false,
        			data: {facilityId: facilityId, facilityName: facilityName, facilitySize: facilitySize, facilitySizeUomId: facilitySizeUomId, description: description, parentFacilityId: parentFacilityId, primaryFacilityGroupId: primaryFacilityGroupId, ownerPartyId: ownerPartyId, facilityTypeId: facilityTypeId},
        			dataType: "json",
        			success: function(data) {
        			}
        		}).done(function(data) {
        			if(checkUpdateFacility == 1){
      				  	window.location.href = "editFacilityInfo?facilityId=" + facilityId;
            			$('#alterpopupWindowEdit').jqxWindow('close');
            			$("#notificationContentUpdateSuccessByViewData").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
      				  	$("#jqxNotificationUpdateSuccessByViewData").jqxNotification('open');
        			}
        			if(checkUpdateFacility == 0){
        				$("#notificationContentUpdateSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
      				  	$("#jqxNotificationUpdateSuccess").jqxNotification('open');
            			$('#jqxgrid').jqxGrid('updatebounddata');
            			$('#alterpopupWindowEdit').jqxWindow('close');
        			}
        		});
            }
        });
	}
	
	function loadParentIdByFacilityId(){
    	var listFacilityId;
    	$.ajax({
			url: "loadParentIdByFacilityId",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listFacilityId = data["listFacilityId"];
			bindingDataToJqxGirdParentFacilityList(listFacilityId);
		});
    }
	
	function bindingDataToJqxGirdParentFacilityList(listFacilityId){
 	    var sourceP2 =
 	    {
 	        datafields:[
 	                    {name: 'facilityId', type: 'string'},
 	            		{name: 'facilityName', type: 'string'},
 	            	   ],
 	        localdata: listFacilityId,
 	        datatype: "array",
 	    };
 	    var dataAdapterP2 = new $.jqx.dataAdapter(sourceP2);
 	    $("#jqxgridParentFacility").jqxGrid({
 	        source: dataAdapterP2,
 	        filterable: true,
	        showfilterrow: true,
	        theme: theme,
	        autoheight:true,
	        pageable: true,
 	        columns: [{text: '${uiLabelMap.DAProductId}', datafield: 'facilityId'},
 	          			{text: '${uiLabelMap.DAProductName}', datafield: 'facilityName'},
 	        		]
 	    });
    }
	var parentFacilityIdData;
	$("#jqxgridParentFacility").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridParentFacility").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ getDescriptionByFacilityId(row['facilityId']) +'</div>';
        $('#parentFacilityIdEdit').jqxDropDownButton('setContent', dropDownContent);
        parentFacilityIdData = row['facilityId'];
    });
	
	function listPartyByPartyId(){
    	var listParty;
    	$.ajax({
			url: "loadListParty",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listParty = data["listParty"];
			bindingDataToJqxGirdPartyList(listParty);
		});
    }
    
    function bindingDataToJqxGirdPartyList(listParty){
 	    var sourceP2 =
 	    {
 	        datafields:[
 	                    {name: 'partyId', type: 'string'},
 	            		{name: 'groupName', type: 'string'},
 	            	   ],
 	        localdata: listParty,
 	        datatype: "array",
 	    };
 	    var dataAdapterP2 = new $.jqx.dataAdapter(sourceP2);
 	    $("#jqxgridPartyId").jqxGrid({
 	        source: dataAdapterP2,
 	        filterable: true,
	        showfilterrow: true,
	        theme: theme,
	        autoheight:true,
	        pageable: true,
 	        columns: [{text: '${uiLabelMap.DAProductId}', datafield: 'partyId'},
 	          			{text: '${uiLabelMap.DAProductName}', datafield: 'groupName'},
 	        		]
 	    });
    }
    
    var ownerPartyIdEditData;
    $("#jqxgridPartyId").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridPartyId").jqxGrid('getrowdata', args.rowindex);
        ownerPartyIdEditData = row['partyId'];
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ getDescriptionByPartyNameViewId(row['partyId']) +'</div>';
        $('#ownerPartyIdEdit').jqxDropDownButton('setContent', dropDownContent);
    });
</script>
