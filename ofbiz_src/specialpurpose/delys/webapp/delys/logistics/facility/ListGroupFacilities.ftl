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
	<#assign facilityGroupTypeList = delegator.findList("FacilityGroupType", null, null, null, null, false) />
	var facilityGroupTypeData = new Array();
	<#list facilityGroupTypeList as facilityGroupType>
		var row = {};
		row['facilityGroupTypeId'] = "${facilityGroupType.facilityGroupTypeId}";
		row['description'] = "${StringUtil.wrapString(facilityGroupType.get('description', locale)?if_exists)}";
		facilityGroupTypeData[${facilityGroupType_index}] = row;
	</#list>
	function getFacilityGroupTypeId(facilityGroupTypeId) {
		for ( var x in facilityGroupTypeData) {
			if (facilityGroupTypeId == facilityGroupTypeData[x].facilityGroupTypeId) {
				return facilityGroupTypeData[x].description;
			}
		}
	}
	
	<#assign facilityGroupList = delegator.findList("FacilityGroup", null, null, null, null, false) />
	var facilityGroupData = new Array();
	<#list facilityGroupList as facilityGroup>
		var row = {};
		row['facilityGroupId'] = "${facilityGroup.facilityGroupId}";
		row['facilityGroupName'] = "${StringUtil.wrapString(facilityGroup.get('facilityGroupName', locale)?if_exists)}";
		facilityGroupData[${facilityGroup_index}] = row;
	</#list>
	function getFacilityGroupId(facilityGroupId) {
		for ( var x in facilityGroupData) {
			if (facilityGroupId == facilityGroupData[x].facilityGroupId) {
				return facilityGroupData[x].facilityGroupName;
			}
		}
	}
	
	
</script>

<div>
	<div id="contentNotificationCreateFacilityGroupSuccess">
	</div>
	<div id="jqxTreeGirdFacilityGroup"></div>
	
	<div id='Menu' class="hide">
	    <ul>
	    	<li>${uiLabelMap.DSEditRowGird}</li>
	    </ul>
	</div>
</div>

<div id="alterpopupWindow" class="hide">
	<div>${uiLabelMap.LogAddFaclityGroup}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid form-window-content'">
			<#--
			<div class="span6">
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						${uiLabelMap.GroupName}
					</div>
					<div class='span7'>
						<div id="facilityGroupName">
							<input id="facilityGroupName"></input>
						</div>
					</div>
				</div>
			</div>
			-->
			<div class="span12">
				<div class="form-horizontal">
					<div id="contentNotificationCreateFacilityGroupError">
					</div>
					<div class="control-group no-left-margin">
						<div class="span12">
							<div class="span6">
								<div>
									<label class="control-label required" for="facilityGroupName">${uiLabelMap.GroupName}</label>
								</div>
								<div class="controls">
									<input id="facilityGroupName"></input>
								</div> 
							</div>
							<div class="span6">
								<div>
									<label class="control-label required" for="facilityGroupTypeId">${uiLabelMap.FormFieldTitle_facilityGroupTypeId}</label>
								</div>
								<div class="controls">
										<div id="facilityGroupTypeId"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div class="span12">
							<div class="span6">
								<div>
									<label class="control-label" for="parentFacilityGroupId">${uiLabelMap.FormFieldTitle_parentFacilityGroupId}</label>
								</div>
								<div class="controls">
									<div id="parentFacilityGroupId"></div>
								</div>
							</div>
							<div class="span6">
								<div>
									<label class="control-label" for="primaryParentGroupId">${uiLabelMap.FormFieldTitle_primaryParentGroupId}</label>
								</div>
								<div class="controls">
									<div id="primaryParentGroupId" style="margin-top:8px; margin-left:-3px!important">${uiLabelMap.FormFieldTitle_parentFacilityGroupId}</div>
								</div>
							</div>
						</div>	
					</div> 
					<div class="control-group no-left-margin">
						<div class="span12">
							<div class="span6">
								<div>
									<label class="control-label" for="fromDate">${uiLabelMap.DAFromDate}</label>
								</div>
								<div class="controls">
									<div>
										<div id="fromDate"></div>
									</div>
								</div>
							</div>
							<div class="span6">
								<div>
									<label class="control-label" for="thruDate">${uiLabelMap.DAToDate}</label>
								</div>
								<div class="controls">
									<div>
										<div id="thruDate"></div>
									</div>
								</div>
							</div>
						</div>	
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label" for="sequenceNum">${uiLabelMap.CommonSequenceNum}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="sequenceNum"></input>
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="description">${uiLabelMap.CommonDescription}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<textarea  id="description"></textarea >
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
					</div>
					<div class="control-group no-left-margin">
					</div>
					<div class="control-group no-left-margin">
						<div class="controls">
							<input style="margin-left:560px; margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" />
					       	<input class="btn-danger" id="alterCancel" type="button"  value="${uiLabelMap.CommonCancel}" />  
						</div>      	
				    </div>
				</div>	
			</div>	
		</div>	
	</div>
</div>
<div id="alterpopupWindowEdit" class="hide">
	<div>${uiLabelMap.LogEditFaclityGroup}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="form-horizontal">
					<div id="contentNotificationEditFacilityGroupError">
					</div>
					<div class="control-group no-left-margin">
						<div class="span12">
							<div class="span6">
								<div>
									<label class="control-label required" for="facilityGroupTypeIdEdit">${uiLabelMap.FormFieldTitle_facilityGroupTypeId}</label>
								</div>
								<div class="controls">
									<div>
										<div id="facilityGroupTypeIdEdit"></div> 
									</div>
								</div>
							</div>
							<div class="span6">
								<div>
									<label class="control-label" for="parentFacilityGroupIdEdit">${uiLabelMap.FormFieldTitle_parentFacilityGroupId}</label>
								</div>
								<div class="controls">
									<div>
										<div id="parentFacilityGroupIdEdit"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div class="span12">
							<div class="span6">
								<div>
									<label class="control-label" for="primaryParentGroupIdEdit">${uiLabelMap.FormFieldTitle_primaryParentGroupId}</label>
								</div>
								<div class="controls">
									<div id="primaryParentGroupIdContainer">
										<div id="primaryParentGroupIdEdit">${uiLabelMap.FormFieldTitle_primaryParentGroupId}</div>
									</div>
								</div>
							</div>
							<div class="span6">	
								<div>
									<label class="control-label required" for="facilityGroupNameEdit">${uiLabelMap.GroupName}</label>
								</div>
								<div class="controls">
									<div>
										<input id="facilityGroupNameEdit"></input> 
									</div>
								</div>
							</div>
						</div>	
					</div>	
					<div class="control-group no-left-margin">
						<div class="span12">
							<div class="span6">
								<div>
									<label class="control-label" for="fromDateEdit">${uiLabelMap.DAFromDate}</label>
								</div>
								<div class="controls">
									<div>
										<div id="fromDateEdit"></div>
									</div>
								</div>
							</div>
							<div class="span6">
								<div>
									<label class="control-label" for="thruDateEdit">${uiLabelMap.DAToDate}</label>
								</div>
								<div class="controls">
									<div class="span12">
										<div id="thruDateEdit"></div>
									</div>
								</div>
							</div>
						</div>	
					</div>
					<div class="control-group no-left-margin">
						<div class="span12">
							<div class="span6">
								<div>
									<label class="control-label" for="sequenceNumEdit">${uiLabelMap.CommonSequenceNum}</label>
								</div>
								<div class="controls">
									<div>
										<input id="sequenceNumEdit"></input>
									</div>
								</div>
							</div>
							<div class="span6">
								<div class="control-group no-left-margin">
									<div class="controls">
										<div class="span12">
											<input id="facilityGroupIdEdit" type="hidden"></input>
										</div>
									</div>
								</div>
							</div>
						</div>	
					</div>
					
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="descriptionEdit">${uiLabelMap.CommonDescription}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<textarea id="descriptionEdit"></textarea> 
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
					</div>
					<div class="control-group no-left-margin">
					</div>
					<div class="control-group no-left-margin">
						<div class="controls">
							<input style="margin-left:560px; margin-right: 5px;" type="button" id="alterSaveEdit" value="${uiLabelMap.CommonSave}" />
					       	<input id="alterCancelEdit" class="btn-danger" type="button" value="${uiLabelMap.CommonCancel}" />  
						</div>      	
				    </div>
				</div>	
			</div>	
		</div>	
	</div>
</div>


<div id="jqxNotificationCreateFacilityGroupError" >
	<div id="notificationCreateFacilityGroupError">
	</div>
</div>

<div id="jqxNotificationCreateFacilityGroupSuccess" >
	<div id="notificationCreateFacilityGroupSuccess">
	</div>
</div>

<div id="jqxNotificationEditFacilityGroupError" >
	<div id="notificationEditFacilityGroupError">
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function () {
		loadDataJqxTreeGirdFacilityGroup();
	});
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$("#jqxNotificationCreateFacilityGroupError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateFacilityGroupError", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationCreateFacilityGroupSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateFacilityGroupSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#jqxNotificationEditFacilityGroupError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationEditFacilityGroupError", opacity: 0.9, autoClose: true, template: "error" });
	function loadDataJqxTreeGirdFacilityGroup() {
		var listFacilityGroupMap;
		$.ajax({
			  url: "loadDataJqxTreeGirdFacilityGroup",
			  type: "POST",
			  data: {},
			  dataType: "json",
			  success: function(data) {
				  listFacilityGroupMap = data["listFacilityGroup"];
				  for(var obj in listFacilityGroupMap){
					  listFacilityGroupMap[obj].expanded = true;
		    	  }
			  }    
		}).done(function(data) {
			bindDataToJqxTreeGirFacilityGroup(listFacilityGroupMap);
		});
	}
	
	function bindDataToJqxTreeGirFacilityGroup(dataSource) {
		for ( var i in dataSource) {
				dataSource[i].fromDate == undefined ? dataSource[i].fromDate = null : dataSource[i].fromDate = dataSource[i].fromDate['time'];
				dataSource[i].thruDate == undefined ? dataSource[i].thruDate = null	: dataSource[i].thruDate = dataSource[i].thruDate['time'];
		}
    	var source =
    	{
			dataType: "json",
		    dataFields: [
		        { name: 'facilityGroupId', type: 'string' },  
		        { name: 'parentFacilityGroupId', type: 'string' },  
		        { name: 'facilityGroupTypeId', type: 'string' },         
		    	{ name: 'primaryParentGroupId', type: 'string' },
		    	{ name: 'facilityGroupName', type: 'string' },
		    	{ name: 'fromDate', type: 'date'},
		    	{ name: 'thruDate', type: 'date'},
		    	{ name: 'sequenceNum', type: 'number' },
		    	{ name: 'description', type: 'string' },
		    	{ name: 'expanded', type: 'bool' },
		    ],
		    hierarchy:
		    {
		    	keyDataField: { name: 'facilityGroupId' },
		        parentDataField: { name: 'parentFacilityGroupId' }
		    },
		    id: 'facilityGroupId',
		    localData: dataSource
	    };
	    var dataAdapter = new $.jqx.dataAdapter(source);
	    renderDataToJqxTreeGirFacilityGroup(dataAdapter);
    }
	
	function renderDataToJqxTreeGirFacilityGroup(dataAdapter){
		$("#jqxTreeGirdFacilityGroup").jqxTreeGrid(
		{
	     	width: '100%',
	        source: dataAdapter,
            altRows: true,
            pageable: true,
            filterMode: 'simple',
            ready: function()
            {
            	$("#jqxTreeGirdFacilityGroup").jqxTreeGrid('expandRow', '2');
            },
            columns: [
                       { text: '${uiLabelMap.GroupName}',  dataField: 'facilityGroupName', editable:false, width: 200, filterable: true,
                    	   cellsrenderer : function(row, column, value, rowData){
                    		   var facilityGroupId = rowData.facilityGroupId;
                    		   return "<span><a href='editFacilityGroupRollupByFacilityGroupId?facilityGroupId="+facilityGroupId+"'>" +value+ "</a></span>"
                    	   }
                       },
                       { text: '${uiLabelMap.FormFieldTitle_parentFacilityGroupId}', dataField: 'parentFacilityGroupId', editable:false, width: 200,
                    	   cellsrenderer : function(row, column, value, rowData){
	       					   var valueParentFacilityGroupId = rowData.parentFacilityGroupId;
	       					   if(valueParentFacilityGroupId != null){
	       						   var parentFacilityGroupId = getFacilityGroupId(valueParentFacilityGroupId);
		       					   return '<span>' + parentFacilityGroupId + '</span>';
	       					   }
                    	   }
                       },
                       { text: '${uiLabelMap.FormFieldTitle_facilityGroupTypeId}', dataField: 'facilityGroupTypeId', editable:false, width: 200,
                    	   cellsrenderer : function(row, column, value, rowData){
	       					   var value = rowData.facilityGroupTypeId
	       					   if(value != null){
	       						   var facilityGroupTypeId = getFacilityGroupTypeId(value);
		       					   return '<span>' + facilityGroupTypeId + '</span>';
	       					   }
                    	   }
                       },
                       { text: '${uiLabelMap.FormFieldTitle_primaryParentGroupId}', dataField: 'primaryParentGroupId', editable:false, width: 200,
                    	   cellsrenderer : function(row, column, value, rowData){
	       					   var value = rowData.primaryParentGroupId;
	       					   if(value != null){
	       						   var primaryParentGroupId = getFacilityGroupId(value);
		       					   return '<span>' + primaryParentGroupId + '</span>';
	       					   }
                    	   }
                       },
                       { text: '${uiLabelMap.DAFromDate}', dataField: 'fromDate', editable:false, width: 200, cellsFormat: "d"},
                       { text: '${uiLabelMap.DAToDate}', dataField: 'thruDate', cellsFormat: 'd', editable:false, width: 200},
                       { text: '${uiLabelMap.CommonSequenceNum}', dataField: 'sequenceNum', editable:false, width: 200},
                       { text: '${uiLabelMap.CommonDescription}', dataField: 'description', editable:false, width: 200},
                    ],
		 });
	}
	
	function addFacilityGroup() {
		$('#alterSave').jqxButton({disabled: false });
		loadParentFacilityGroupId();
		$("#alterpopupWindow").jqxWindow('open');
//		$("#alterpopupWindowTest").jqxWindow('open');
		
	}
	
	$("#facilityGroupName").jqxInput({width: 195});
	$("#facilityGroupTypeId").jqxDropDownList({placeHolder: '${uiLabelMap.LogPleaseSelect}', autoDropDownHeight: true, source: facilityGroupTypeData, displayMember: 'description', valueMember: 'facilityGroupTypeId'});
	$('#primaryParentGroupId').jqxCheckBox({});
	$("#fromDate").jqxDateTimeInput({ 
		 showFooter:true,
	    clearString:'Clear'});
	$("#thruDate").jqxDateTimeInput({
		 showFooter:true,
	    clearString:'Clear'});
	$("#sequenceNum").jqxInput({width: 195});
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1000, minWidth: 700, height: 450 ,width:1000, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme
    });
	//create button alterSave, alterSave
	$("#alterCancel").jqxButton({height: 32, width: 70});
	$("#alterSave").jqxButton({height: 32, width: 70});
	$('#description').jqxEditor({
        height: "180px",
        width: '707px'
    });
	$('#alterpopupWindow').jqxValidator({
        rules: [
	               { input: '#facilityGroupName', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
	               { input: '#facilityGroupName', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
	               { input: '#facilityGroupTypeId', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var facilityGroupTypeId = $('#facilityGroupTypeId').val();
		            	    if(facilityGroupTypeId == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               } , 
	               { input: '#description', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged', 
	            	   rule: function () {
	            		    var description = $('#description').val();
		            	    if(description == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               }  
	           ]
    });
	
	
	$('#parentFacilityGroupId').on('select', function (event){
	    var args = event.args;
	    if (args) {
		    var index = args.index;
		    var item = args.item;
		    var value = item.value;
		    $('#fromDate').jqxDateTimeInput({disabled: false});
			$('#thruDate').jqxDateTimeInput({disabled: false});
			$('#sequenceNum').jqxInput({disabled: false });
			$('#primaryParentGroupId').jqxCheckBox({disabled: false});
	    }                        
	});
	
	$('#alterpopupWindow').on('open', function (event) { 
		$('#primaryParentGroupId').jqxCheckBox({disabled: true});
		$('#fromDate').jqxDateTimeInput({disabled: true});
		$('#thruDate').jqxDateTimeInput({disabled: true});
		$('#fromDate').val("");
		$('#thruDate').val("");
		$('#sequenceNum').jqxInput({disabled: true });
		$('#description').val("");
	}); 
	 
	$("#alterSave").click(function (){
		var facilityGroupName = $('#facilityGroupName').val();
		var facilityGroupTypeId = $('#facilityGroupTypeId').val();
		var primaryParentGroupId = $('#primaryParentGroupId').val();
		var parentFacilityGroupId = $('#parentFacilityGroupId').val();
		var description = $('#description').val();
		description = description.trim();
		var fromDate = $('#fromDate').val();
		var valueFromDate = convertDate(fromDate);
		var thruDate = $('#thruDate').val();
		var valueThruDate = convertDate(thruDate);
		var sequenceNum = $('#sequenceNum').val();
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		if(validate != false && validate != undefined){
			if(facilityGroupTypeId == "" || facilityGroupTypeId == undefined || description == "" || description == undefined){
				$("#notificationCreateFacilityGroupError").text('${StringUtil.wrapString(uiLabelMap.checkFacilityGroupTypeIdOrPrimaryParentGroupId)}');
				$("#jqxNotificationCreateFacilityGroupError").jqxNotification('open');
			}else{
				if(parentFacilityGroupId != "" && parentFacilityGroupId != null){
					if(fromDate == "" || fromDate == undefined){
						$("#notificationCreateFacilityGroupError").text('${StringUtil.wrapString(uiLabelMap.LogCheckFromDateIsNotNull)}');
						$("#jqxNotificationCreateFacilityGroupError").jqxNotification('open');
					}else{
						$('#alterSave').jqxButton({disabled: true });
						createFacilityGroupAndRollupByHungNc(facilityGroupName, facilityGroupTypeId, parentFacilityGroupId, primaryParentGroupId, valueFromDate, valueThruDate, sequenceNum, description);
					}
				}else{
					$('#alterSave').jqxButton({disabled: true });
					createFacilityGroupAndRollupByHungNc(facilityGroupName, facilityGroupTypeId, parentFacilityGroupId, primaryParentGroupId, valueFromDate, valueThruDate, sequenceNum, description);
				}
			}
		}
	});	
	
	function convertDate(date){
	     if (date == "") {
	      return "";
	     }
	     var splDate = date.split('/');
	     if (splDate[2] != null) {
	      var d = new Date(splDate[2], splDate[1] - 1, splDate[0]);
	      return d.getTime();
	     }
	     date = new Date(date);
	     return date.getTime();
	}
	
	function createFacilityGroupAndRollupByHungNc(facilityGroupName, facilityGroupTypeId, parentFacilityGroupId, primaryParentGroupId, valueFromDate, valueThruDate, sequenceNum, description){
		$.ajax({
			  url: "createFacilityGroupAndRollupByHungNc",
			  type: "POST",
			  data: {facilityGroupName:facilityGroupName, facilityGroupTypeId: facilityGroupTypeId, parentFacilityGroupId: parentFacilityGroupId, primaryParentGroupId: primaryParentGroupId, fromDate: valueFromDate, thruDate: valueThruDate, sequenceNum: sequenceNum, description: description},
			  dataType: "json"
		}).done(function(data) {
			var value = data["value"];
			if(value == "sequenceNumNotNumber"){
				$("#notificationCreateFacilityGroupError").text('${StringUtil.wrapString(uiLabelMap.LogChecksequenceNumIsNotNumber)}');
				$("#jqxNotificationCreateFacilityGroupError").jqxNotification('open');
				$('#alterSave').jqxButton({disabled: false });
	        }
	        if(value == "thruDateLongValid"){
	        	$("#notificationCreateFacilityGroupError").text('${StringUtil.wrapString(uiLabelMap.LogCheckThruDateVsFromDate)}');
				$("#jqxNotificationCreateFacilityGroupError").jqxNotification('open');
				$('#alterSave').jqxButton({disabled: false }); 
	        }
			if(value == "success"){
	        	$("#notificationCreateFacilityGroupSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiCreateSucess)}');
				$("#jqxNotificationCreateFacilityGroupSuccess").jqxNotification('open');
				loadDataJqxTreeGirdFacilityGroup();
				$('#alterpopupWindow').jqxWindow('close');
	        }
		});
	}
	
	function loadParentFacilityGroupId(){
		$.ajax({
			  url: "loadParentFacilityGroupId",
			  type: "POST",
			  data: {},
			  dataType: "json"
		}).done(function(data) {
			var listParentFacilityGroupId = data["listParentFacilityGroupId"];
			bindingDataToParentFacilityId(listParentFacilityGroupId);
		});
	}
	
	function bindingDataToParentFacilityId(listParentFacilityGroupId){
		$("#parentFacilityGroupId").jqxDropDownList({placeHolder: '${uiLabelMap.LogPleaseSelect}' , source: listParentFacilityGroupId, displayMember: 'facilityGroupName', valueMember: 'facilityGroupId'});
	}
	
	$('#alterpopupWindow').on('close', function (event) { 
		$('#alterpopupWindow').jqxValidator('hide');
		$('#alterSave').jqxButton({disabled: false});
		$('#facilityGroupName').val("");
		$('#description').val("");
		$('#fromDate').val("");
		$('#thruDate').val("");
		$('#sequenceNum').val("");
		$("#facilityGroupTypeId").jqxDropDownList('clearSelection');
		$("#parentFacilityGroupId").jqxDropDownList('clearSelection');
		$("#primaryParentGroupId").val("");
	}); 
	
	 // create context menu
    var contextMenu = $("#Menu").jqxMenu({ width: 200, height: 30, autoOpenPopup: false, mode: 'popup' });
    $("#jqxTreeGirdFacilityGroup").on('contextmenu', function () {
        return false;
    });
    $("#jqxTreeGirdFacilityGroup").on('rowClick', function (event) {
        var args = event.args;
        if (args.originalEvent.button == 2) {
            var scrollTop = $(window).scrollTop();
            var scrollLeft = $(window).scrollLeft();
            contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
            return false;
        }
    });
    var primaryParentGroupId = "";
    $("#Menu").on('itemclick', function (event) {
        var args = event.args;
        var selection = $("#jqxTreeGirdFacilityGroup").jqxTreeGrid('getSelection');
        var rowid = selection[0].uid;
        var facilityGroupName;
        var facilityGroupTypeId;
        var description;
        var parentFacilityGroupId;
        var fromDate;
        var thruDate;
        var sequenceNum;
        for(var i in selection){
        		primaryParentGroupId = selection[i].primaryParentGroupId;
            facilityGroupName = selection[i].facilityGroupName;
            facilityGroupTypeId = selection[i].facilityGroupTypeId;
            description = selection[i].description;
            parentFacilityGroupId = selection[i].parentFacilityGroupId;
            fromDate = selection[i].fromDate;
            thruDate = selection[i].thruDate;
            sequenceNum = selection[i].sequenceNum;
        }
        bindingDataToEdit(rowid, primaryParentGroupId, facilityGroupName, facilityGroupTypeId, description, parentFacilityGroupId, fromDate, thruDate, sequenceNum);	
    });
    
    // Edit infoString
	$("#facilityGroupNameEdit").jqxInput({width: 195});
	$("#facilityGroupIdEdit").jqxInput();
	$("#fromDateEdit").jqxDateTimeInput({ 
		 showFooter:true,
	    clearString:'Clear'});
	$("#thruDateEdit").jqxDateTimeInput({
		 showFooter:true,
	    clearString:'Clear'});
	$("#sequenceNumEdit").jqxInput({width: 195});
	//create button alterSaveEdit, alterSave
	$("#alterCancelEdit").jqxButton({height: 32, width: 70});
	$("#alterSaveEdit").jqxButton({height: 32, width: 70});
	$("#facilityGroupTypeIdEdit").jqxDropDownList({placeHolder: '${uiLabelMap.LogPleaseSelect}', source: facilityGroupTypeData, displayMember: 'description', valueMember: 'facilityGroupTypeId', autoDropDownHeight: true});
	$("#parentFacilityGroupIdEdit").jqxDropDownList({placeHolder: '${uiLabelMap.LogPleaseSelect}' , source: facilityGroupData, displayMember: 'facilityGroupName', valueMember: 'facilityGroupId'});
	$("#alterpopupWindowEdit").jqxWindow({
		maxWidth: 1000, minWidth: 700, height: 450 ,width:1000, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelEdit"), modalOpacity: 0.7, theme:theme           
    });
	$('#descriptionEdit').jqxEditor({
		height: "180px",
        width: '707px'
    });
	$('#alterpopupWindowEdit').jqxValidator({
        rules: [
	               { input: '#facilityGroupNameEdit', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
	               { input: '#facilityGroupNameEdit', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
	               
	               { input: '#facilityGroupTypeIdEdit', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var facilityGroupTypeId = $('#facilityGroupTypeIdEdit').val();
		            	    if(facilityGroupTypeId == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               } , 
	               { input: '#descriptionEdit', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged', 
	            	   rule: function () {
	            		    var description = $('#descriptionEdit').val();
		            	    if(description == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               } 
	           ]
    });
	var valueParentFacilityGroupIdEdit;
	var valuePrimaryParentGroupIdEdit;
    function bindingDataToEdit(rowid, primaryParentGroupId, facilityGroupName, facilityGroupTypeId, description, parentFacilityGroupId, fromDate, thruDate, sequenceNum){
    	valueParentFacilityGroupIdEdit = parentFacilityGroupId;
    	valuePrimaryParentGroupIdEdit = primaryParentGroupId;
    	if(parentFacilityGroupId == null){
    		$('#thruDateEdit').jqxDateTimeInput({disabled: true});
			$('#sequenceNumEdit').jqxInput({disabled: true });
    	}
    	$('#facilityGroupIdEdit').val(rowid);
		$('#facilityGroupNameEdit').val(facilityGroupName);
		$('#descriptionEdit').val(description);
		if(parentFacilityGroupId != null ){
			if(primaryParentGroupId != null){
				$("#primaryParentGroupIdEdit").jqxDropDownList({disabled: true});
				var valuePrimaryParentGroupId = getFacilityGroupId(primaryParentGroupId);
				$("#primaryParentGroupIdEdit").jqxDropDownList('setContent', valuePrimaryParentGroupId);
				$("#primaryParentGroupIdEdit").val(primaryParentGroupId);
			}
			if(primaryParentGroupId == null){
				document.getElementById("primaryParentGroupIdEdit").style.marginTop = "8px";
				document.getElementById("primaryParentGroupIdEdit").style.marginLeft = "-3px!important";
				$("#primaryParentGroupIdEdit").jqxCheckBox({ disabled:false });
			}
		}
		if(parentFacilityGroupId == null){
			document.getElementById("primaryParentGroupIdEdit").style.marginTop = "8px";
			document.getElementById("primaryParentGroupIdEdit").style.marginLeft = "-3px!important";
			$("#primaryParentGroupIdEdit").jqxCheckBox({ disabled:true });
		}
		
		var valueParentFacilityGroupId = getFacilityGroupId(parentFacilityGroupId);
		$("#parentFacilityGroupIdEdit").jqxDropDownList('setContent', valueParentFacilityGroupId);
		$("#parentFacilityGroupIdEdit").val(parentFacilityGroupId);
		var valueFacilityGroupTypeIdEdit = getFacilityGroupTypeId(facilityGroupTypeId);
		$("#facilityGroupTypeIdEdit").jqxDropDownList('setContent', valueFacilityGroupTypeIdEdit);
		$("#facilityGroupTypeIdEdit").val(facilityGroupTypeId);
		if(fromDate == null){
			$('#fromDateEdit ').jqxDateTimeInput('setDate', new Date(""));
		}
		if(fromDate != null){
			$('#fromDateEdit').jqxDateTimeInput('setDate', new Date(fromDate));
		}
		if(thruDate == null){
			$('#thruDateEdit ').jqxDateTimeInput('setDate', new Date(""));
		}
		if(thruDate != null){
			$('#thruDateEdit').jqxDateTimeInput('setDate', new Date(thruDate));
		}
		$('#sequenceNumEdit').val(sequenceNum);
		$('#alterpopupWindowEdit').jqxWindow('open');
    }
    
    $("#alterSaveEdit").click(function (){
    	var validate = $('#alterpopupWindowEdit').jqxValidator('validate');
		if(validate != false){
			var facilityGroupId = $('#facilityGroupIdEdit').val();
			var facilityGroupTypeIdEdit = $('#facilityGroupTypeIdEdit').val();
			var primaryParentGroupIdEdit = $('#primaryParentGroupIdEdit').val();
			var parentFacilityGroupIdEdit = $('#parentFacilityGroupIdEdit').val();
			var facilityGroupNameEdit = $('#facilityGroupNameEdit').val();
			var fromDateEdit = $('#fromDateEdit').val();
			var thruDateEdit = $('#thruDateEdit').val();
			var valueFromDateEdit = convertDate(fromDateEdit);
			var valueThruDateEdit = convertDate(thruDateEdit);
			var sequenceNumEdit = $('#sequenceNumEdit').val();
			var descriptionEdit = $('#descriptionEdit').val();
			$('#alterSaveEdit').jqxButton({disabled: true });
			editFacilityGroupByHungNc(facilityGroupId, facilityGroupTypeIdEdit, primaryParentGroupIdEdit, facilityGroupNameEdit, descriptionEdit, parentFacilityGroupIdEdit, valueFromDateEdit, valueThruDateEdit, sequenceNumEdit);
		}
	});	
    
    function editFacilityGroupByHungNc(facilityGroupId, facilityGroupTypeIdEdit, primaryParentGroupIdEdit, facilityGroupNameEdit, descriptionEdit, parentFacilityGroupIdEdit, valueFromDateEdit, valueThruDateEdit, sequenceNumEdit){
    	$.ajax({
			  url: "editFacilityGroupByHungNc",
			  type: "POST",
			  data: {facilityGroupId: facilityGroupId, facilityGroupTypeId: facilityGroupTypeIdEdit, primaryParentGroupId: primaryParentGroupIdEdit, facilityGroupName : facilityGroupNameEdit, description: descriptionEdit, parentFacilityGroupId: parentFacilityGroupIdEdit, fromDate: valueFromDateEdit, thruDate: valueThruDateEdit, sequenceNum: sequenceNumEdit},
			  dataType: "json"
		}).done(function(data) {
			var value = data["value"];
			if(value == "notEdit"){
				$('#alterpopupWindowEdit').jqxWindow('close');
				$('#alterSaveEdit').jqxButton({disabled: false });
	        }
			if(value == "sequenceNumNotNumber"){  
				$("#notificationEditFacilityGroupError").text('${StringUtil.wrapString(uiLabelMap.LogChecksequenceNumIsNotNumber)}');
				$("#jqxNotificationEditFacilityGroupError").jqxNotification('open');
				$('#alterSaveEdit').jqxButton({disabled: false });
			}
			if(value == "thruDateLongValid"){
				$("#notificationEditFacilityGroupError").text('${StringUtil.wrapString(uiLabelMap.LogCheckThruDateVsFromDate)}');
				$("#jqxNotificationEditFacilityGroupError").jqxNotification('open');
				$('#alterSaveEdit').jqxButton({disabled: false });
			}
			if(value == "success"){
				$("#notificationCreateFacilityGroupSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
				$("#jqxNotificationCreateFacilityGroupSuccess").jqxNotification('open');
				loadDataJqxTreeGirdFacilityGroup();
				$('#alterpopupWindowEdit').jqxWindow('close');
			}
		});
    }
    
    $('#alterpopupWindowEdit').on('open', function (event) { 
		$("#parentFacilityGroupIdEdit").jqxDropDownList({ disabled: true });
		$('#fromDateEdit').jqxDateTimeInput({disabled: true});
	}); 
    
    $('#alterpopupWindowEdit').on('close', function (event) { 
    	if(valueParentFacilityGroupIdEdit != null){
    		if(valuePrimaryParentGroupIdEdit != null){
    			$('#primaryParentGroupIdEdit').jqxDropDownList('destroy'); 
    		}else{
    			$('#primaryParentGroupIdEdit').jqxCheckBox('destroy'); 
    		}
    	}
    	if(valueParentFacilityGroupIdEdit == null){
    		$('#primaryParentGroupIdEdit').jqxCheckBox('destroy'); 
    	}
    	
    	$('#alterpopupWindowEdit').jqxValidator('hide');
		$('#alterSaveEdit').jqxButton({disabled: false});
		$('#facilityGroupNameEdit').val("");
		$('#descriptionEdit').val("");
		$('#fromDateEdit').val("");
		$('#thruDateEdit').val("");
		$('#fromDateEdit ').jqxDateTimeInput('setDate', new Date(""));
		$('#thruDateEdit ').jqxDateTimeInput('setDate', new Date(""));
		$("#facilityGroupTypeIdEdit").jqxDropDownList('clearSelection');
		$('#thruDateEdit').jqxDateTimeInput({disabled: false});
		$('#sequenceNumEdit').jqxInput({disabled: false });
		valueParentFacilityGroupIdEdit = "";
		valuePrimaryParentGroupIdEdit = "";
		$("#primaryParentGroupIdContainer").append("<div id='primaryParentGroupIdEdit'>${uiLabelMap.FormFieldTitle_primaryParentGroupId}</div>");
	}); 
    
    
</script>