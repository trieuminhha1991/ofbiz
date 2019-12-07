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

<script   src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script  type="text/javascript">
	var facilityGroupId = '${parameters.facilityGroupId}';
	
	<#assign roleTypeList = delegator.findList("RoleType", null, null, null, null, false) />
	var roleTypeData = new Array();
	<#list roleTypeList as roleType>
		var row = {};
		row['roleTypeId'] = "${roleType.roleTypeId}";
		row['description'] = "${StringUtil.wrapString(roleType.get('description', locale)?if_exists)}";
		roleTypeData[${roleType_index}] = row;
	</#list>
	function getRoleTypeId(roleTypeId) {
		for ( var x in roleTypeData) {
			if (roleTypeId == roleTypeData[x].roleTypeId) {
				return roleTypeData[x].description;
			}
		}
	}
</script>

	<div>
		<div id="contentNotificationCreateFacilityGroupRoleSuccess">
		</div>
		
		<#assign dataField="[
			{ name: 'facilityGroupId', type: 'string'},
			{ name: 'partyId', type: 'string'},
			{ name: 'roleTypeId', type: 'string'},
			{ name: 'fromDate', type: 'date', other: 'Timestamp'},
			{ name: 'thruDate', type: 'date', other: 'Timestamp'},
		]"/>
		<#assign columnlist="
			{ text: '${StringUtil.wrapString(uiLabelMap.DelysPartyId)}', datafield: 'partyId', editable:false},
			{ text: '${StringUtil.wrapString(uiLabelMap.LogfaPerm)}', datafield: 'roleTypeId', editable:false,
				cellsrenderer: function(row, colum, value){
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					var value = data.roleTypeId;
					var roleTypeId = getRoleTypeId(value);
				    return '<span>' + roleTypeId + '</span>';
			    },  
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.fromDate)}', datafield: 'fromDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable:false},
			{ text: '${StringUtil.wrapString(uiLabelMap.thruDate)}', datafield: 'thruDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable:false},
		"/>
		
		<@jqGrid filtersimplemode="true" id="jqxgrid" filterable="true" addType="popup" dataField=dataField columnlist=columnlist addrow="true"  editable="true" alternativeAddPopup="alterpopupWindow"  clearfilteringbutton="true" showtoolbar="true" editmode="click"
			url="jqxGeneralServicer?sname=JQXgetFacilityGroupRole&facilityGroupId=${parameters.facilityGroupId}"
		/>
	</div>
	<div id='Menu' class="hide">
	     <ul>
	     	<li>${StringUtil.wrapString(uiLabelMap.DSEditRowGird)}</li>
	        <li>${StringUtil.wrapString(uiLabelMap.DSDeleteRowGird)}</li>
	     </ul>
	</div>
	
	<div id="alterpopupWindow" class='hide'>
		<div>${uiLabelMap.faNewPerm}</div>
		<div>
			<div id="contentNotificationContentCreateFacilityGroupRole" class="popup-notification">
			</div>
			<div class='row-fluid margin-bottom8 padding-top8'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.DelysPartyId)}</label>
				</div>  
				<div class="span7">
					<div id="partyId">
					</div>
				</div>
		   	</div>
		   	<div class='row-fluid margin-bottom8 padding-top8'>
			   	<div class='span5 text-algin-right'>
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.roleTypeId)}</label>
				</div>  
				<div class="span7">
					<div id="roleTypeId"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom8 padding-top8'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.fromDate)}</label>
				</div>  
				<div class="span7">
					<div id="fromDate"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom8 padding-top8'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.thruDate)}</label>
				</div>  
				<div class="span7">
					<div id="thruDate"></div>
		   		</div>
		   	</div>
		   	<div class="form-action">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button> 
			</div>
		</div>
	</div>
	
	<div id="alterpopupWindowEdit" class='hide'>
		<div>${uiLabelMap.LogEditRoleFacilityGroup}</div>
		<div>
			<div id="contentNotificationContentEditFacilityGroupRoleError" class="popup-notification">
			</div>
			<div class='row-fluid margin-bottom8 padding-top8'>
		   		<div class='span5 text-algin-right'>
					<label>${StringUtil.wrapString(uiLabelMap.DelysPartyId)}</label>
				</div>  
				<div class="span7">
					<input id="partyIdEdit">
					</input>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom8 padding-top8'>
		   		<div class='span5 text-algin-right'>
					<label>${StringUtil.wrapString(uiLabelMap.roleTypeId)}</label>
				</div>  
				<div class="span7">
					<input id="roleTypeIdEdit"></input>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom8 padding-top8'>
		   		<div class='span5 text-algin-right'>
					<label>${StringUtil.wrapString(uiLabelMap.fromDate)}</label>
				</div>  
				<div class="span7">
					<div id="fromDateEdit"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom8 padding-top8'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.thruDate)}</label>
				</div>  
				<div class="span7">
					<div id="thruDatEdit"></div>
		   		</div>
		   	</div>
		   	<div class="form-action">
				<button id="alterCancelEdit" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button id="alterSaveEdit" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button> 
			</div>
		</div>
	</div>
	
	<div id="jqxNotificationCreateFacilityGroupRoleSuccess" >
		<div id="notificationCreateFacilityGroupRoleSuccess">
		</div>
	</div>
	
	<div id="jqxNotificationCreateFacilityGroupRoleError" >
		<div id="notificationCreateFacilityGroupRoleError">
		</div>
	</div>
	
	<div id="jqxNotificationUpdateFacilityGroupRoleError" >
		<div id="notificationUpdateFacilityGroupRoleError">
		</div>
	</div>
	
<script>
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$(document).ready(function () {
		loadDataToParty();
		$("#fromDate").jqxDateTimeInput({ 
			 showFooter:true,
		    clearString:'Clear'});
		$("#thruDate").jqxDateTimeInput({
			 showFooter:true,
		    clearString:'Clear'});
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 700, minWidth: 400, height: 270, width:600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		$('#alterpopupWindow').jqxValidator({
	    	rules: 
			[	{
	        		input: '#thruDate', message: '${StringUtil.wrapString(uiLabelMap.faFromDateLTThruDate)}', action: 'valueChanged', rule: function (input, commit) {
	            		if(input.jqxDateTimeInput('getDate') == null || $('#fromDate').jqxDateTimeInput('getDate') == null){
	            			return true;
	            		}
	            		if(input.jqxDateTimeInput('getDate') < $('#fromDate').jqxDateTimeInput('getDate')){
	            			return false;
	            		}
	            		return true;
	        		}
				},
				{	input: '#partyId', message: '${StringUtil.wrapString(uiLabelMap.LogCheckSelectedDropDownList)}', action: 'valueChanged', rule: function (input, commit) {
						if($('#partyId').val() == null || $('#partyId').val()==''){
						    return false;
						}
						return true;
					}
				},
				{	input: '#roleTypeId', message: '${StringUtil.wrapString(uiLabelMap.LogCheckSelectedDropDownList)}', action: 'valueChanged', 
					rule: function (input, commit) {
						if($('#roleTypeId').val() == null || $('#roleTypeId').val()==''){
						    return false;
						}
						return true;
					}
				},
	    	]
	    });
		
		
	    var contextMenu = $("#Menu").jqxMenu({ width: 200, height: 58, autoOpenPopup: false, mode: 'popup'});
	    $("#jqxgrid").on('contextmenu', function () {
	        return false;
	    });
	    $("#Menu").on('itemclick', function (event) {
	        var args = event.args;
	        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var tmpKey = $.trim($(args).text());
	        editrow = rowindex;
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', editrow);
	        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DSEditRowGird)}") {
                var partyId = dataRecord.partyId;
                var roleTypeId = dataRecord.roleTypeId;
                var fromDate = dataRecord.fromDate;
                var thruDate = dataRecord.thruDate;	
                bindingDataEditGroupFacilityRole(partyId, roleTypeId, fromDate, thruDate);
	        	$("#alterpopupWindowEdit").jqxWindow('show');
	        }
	        else {
	        	var partyId = dataRecord.partyId;
                var roleTypeId = dataRecord.roleTypeId;
                deleteFacilityGroupRoleByPartyId(partyId, roleTypeId);
	        }
	    });
	    $("#jqxgrid").on('rowClick', function (event) {
	        if (event.args.rightclick) {
	            $("#jqxgrid").jqxGrid('selectrow', event.args.rowindex);
	            var scrollTop = $(window).scrollTop();
	            var scrollLeft = $(window).scrollLeft();
	            contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	            return false;
	        }
	    });
	    
	    $("#partyIdEdit").jqxInput({width: 195});
	    $("#roleTypeIdEdit").jqxInput({width: 195});
	    $("#fromDateEdit").jqxDateTimeInput({ 
			 showFooter:true,
		    clearString:'Clear'});
		$("#thruDatEdit").jqxDateTimeInput({
			 showFooter:true,
		    clearString:'Clear'});
	    $("#alterpopupWindowEdit").jqxWindow({
			maxWidth: 600, minWidth: 400, height: 270 ,width:1110, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelEdit"), modalOpacity: 0.7, theme:theme           
		});
		
		
	})
	$("#jqxNotificationCreateFacilityGroupRoleSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateFacilityGroupRoleSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#jqxNotificationCreateFacilityGroupRoleError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateFacilityGroupRole", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationUpdateFacilityGroupRoleError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentEditFacilityGroupRoleError", opacity: 0.9, autoClose: true, template: "error" });
	function bindingDataEditGroupFacilityRole(partyId, roleTypeId, fromDate, thruDate){
		$('#partyIdEdit').val(partyId);
		$('#roleTypeIdEdit').val(roleTypeId);
		$('#fromDateEdit').jqxDateTimeInput('setDate', new Date(fromDate));
		if(thruDate == null){
        	$('#thruDatEdit').jqxDateTimeInput('setDate', new Date(""));
        }
		if(thruDate != null){
			$('#thruDatEdit').jqxDateTimeInput('setDate', new Date(thruDate));
		}
	}
	
	$('#alterpopupWindowEdit').jqxValidator({
    	rules: 
		[	{
        		input: '#thruDatEdit', message: '${StringUtil.wrapString(uiLabelMap.faFromDateLTThruDate)}', action: 'valueChanged', rule: function (input, commit) {
            		if(input.jqxDateTimeInput('getDate') == null || $('#fromDateEdit').jqxDateTimeInput('getDate') == null){
            			return true;
            		}
            		if(input.jqxDateTimeInput('getDate') < $('#fromDateEdit').jqxDateTimeInput('getDate')){
            			return false;
            		}
            		return true;
        		}
			},
    	]
    });
	
	$('#alterpopupWindowEdit').on('open', function (event) {
		document.getElementById("alterSaveEdit").disabled = false;
		$('#alterpopupWindowEdit').jqxValidator('hide');
		$('#partyIdEdit').jqxInput({disabled: true });
		$('#roleTypeIdEdit').jqxInput({disabled: true });
		$('#fromDateEdit').jqxDateTimeInput({disabled: true });
	}); 

	$('#alterpopupWindowEdit').on('close', function (event) { 
		document.getElementById("alterSaveEdit").disabled = false;
		$('#alterpopupWindowEdit').jqxValidator('hide');
		$('#partyIdEdit').val("");
		$('#roleTypeIdEdit').val("");
		$('#fromDateEdit').val("");
		$('#thruDatEdit').val("");
	});
	
	$("#alterSaveEdit").click(function () {
		var partyId = $('#partyIdEdit').val();
		var roleTypeId = $('#roleTypeIdEdit').val();
		var thruDate = $('#thruDatEdit').val();
		var fromDate = $('#fromDateEdit').val();
		var valueThruDate = convertDate(thruDate);
		var valueFromDate = convertDate(fromDate);
		var validate = $('#alterpopupWindowEdit').jqxValidator('validate');
		if(validate != false){
			document.getElementById("alterSaveEdit").disabled = true;
			editFacilityGroupRole(partyId, roleTypeId, valueFromDate ,valueThruDate);
		}
	});
	
	function deleteFacilityGroupRoleByPartyId(partyId, roleTypeId){
		bootbox.confirm("${uiLabelMap.LogNotificationBeforeDelete}", function(result) {
            if(result) {
            	$.ajax({
	      			  url: "deleteFacilityGroupRoleByPartyId",
	      			  type: "POST",
	      			  data: {facilityGroupId: facilityGroupId, partyId: partyId, roleTypeId: roleTypeId},
	      			  dataType: "json"
	      		}).done(function(data) {
	      			var value = data["value"];
	      			if(value == "success"){
	      				$("#notificationCreateFacilityGroupRoleSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiDeleteSucess)}');
	      				$("#jqxNotificationCreateFacilityGroupRoleSuccess").jqxNotification('open');
	      				$('#jqxgrid').jqxGrid('updatebounddata');
	      			}
	      		});
            }
        }); 
	}
	
	function editFacilityGroupRole(partyId, roleTypeId, valueFromDate ,valueThruDate){
		$.ajax({
			  url: "editFacilityGroupRole",
			  type: "POST",
			  data: {facilityGroupId: facilityGroupId, partyId: partyId, roleTypeId: roleTypeId, fromDate: valueFromDate ,thruDate: valueThruDate},
			  dataType: "json"
		}).done(function(data) {
			var value = data["value"];
			if(value == "notEdit"){
				$("#alterpopupWindowEdit").jqxWindow('close');
			}
			if(value == "thruDateIsNull"){ 
				$("#notificationUpdateFacilityGroupRoleError").text('${StringUtil.wrapString(uiLabelMap.LogCheckThruDate)}');
				$("#jqxNotificationUpdateFacilityGroupRoleError").jqxNotification('open');
				document.getElementById("alterSaveEdit").disabled = false;
			}
			if(value == "success"){
				$("#notificationCreateFacilityGroupRoleSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
				$("#jqxNotificationCreateFacilityGroupRoleSuccess").jqxNotification('open');
				$("#alterpopupWindowEdit").jqxWindow('close');
				$('#jqxgrid').jqxGrid('updatebounddata');
			}
		});
	}
	
	function loadDataToParty(){
		$.ajax({
			  url: "loadDataToParty",
			  type: "POST",
			  data: {},
			  dataType: "json"
		}).done(function(data) {
			var listParty = data["listParty"];
			bindingDataToParty(listParty);
		});
	}
	
	$("#alterSave").click(function () {
		var partyId = $('#partyId').val();
		var roleTypeId = $('#roleTypeId').val();
		var fromDate = $('#fromDate').val();
		var valueFromDate = convertDate(fromDate);
		var thruDate = $('#thruDate').val();
		var valueThruDate = convertDate(thruDate);
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		if(validate != false){
			document.getElementById("alterSave").disabled = true;
			createFacilityGroupRole(partyId, roleTypeId, valueFromDate, valueThruDate);
		}
	});
	
	function createFacilityGroupRole(partyId, roleTypeId, valueFromDate, valueThruDate){
		$.ajax({
			  url: "createFacilityGroupRole",
			  type: "POST",
			  data: {facilityGroupId: facilityGroupId, partyId: partyId, roleTypeId: roleTypeId, fromDate: valueFromDate, thruDate: valueThruDate},
			  dataType: "json"
		}).done(function(data) {
			var value = data["value"];
			if(value == "fromDateNull"){
				$("#notificationCreateFacilityGroupRoleError").text('${StringUtil.wrapString(uiLabelMap.LogCheckFromDate)}');
				$("#jqxNotificationCreateFacilityGroupRoleError").jqxNotification('open');
				document.getElementById("alterSave").disabled = false;
			}
			if(value == "exits"){
				$("#notificationCreateFacilityGroupRoleError").text('${StringUtil.wrapString(uiLabelMap.checkInventoryItemExits)}');
				$("#jqxNotificationCreateFacilityGroupRoleError").jqxNotification('open');
				document.getElementById("alterSave").disabled = false;
			}
			if(value == "success"){
				$("#notificationCreateFacilityGroupRoleSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiCreateSucess)}');
				$("#jqxNotificationCreateFacilityGroupRoleSuccess").jqxNotification('open');
				$("#alterpopupWindow").jqxWindow('close');
				$('#jqxgrid').jqxGrid('updatebounddata');
			}
		});
	}
	
	$('#alterpopupWindow').on('open', function (event) { 
		$("#roleTypeId").jqxDropDownList({placeHolder: '${uiLabelMap.LogPleaseSelect}', disabled: true, theme: theme});
		$('#alterpopupWindow').jqxValidator('hide');
		$('#fromDate').val("");
		$('#thruDate').val("");
		$("#roleTypeId").jqxDropDownList('clearSelection');
		$("#roleTypeId").jqxDropDownList({ disabled: true });
		$("#partyId").jqxDropDownList('clearSelection'); 
		document.getElementById("alterSave").disabled = false;
	}); 

	$('#alterpopupWindow').on('close', function (event) { 
		document.getElementById("alterSave").disabled = false;
		$('#alterpopupWindow').jqxValidator('hide');
		$('#fromDate').val("");
		$('#thruDate').val("");
		$("#roleTypeId").jqxDropDownList('clearSelection');
		$("#partyId").jqxDropDownList('clearSelection'); 
	}); 
	
	function bindingDataToParty(listParty){
		$("#partyId").jqxDropDownList({ source: listParty, searchMode: 'contains', filterPlaceHolder: '${StringUtil.wrapString(uiLabelMap.LogFiltering)}' ,filterable: true, placeHolder: '${uiLabelMap.LogPleaseSelect}', displayMember: 'firstLast', valueMember: 'partyId', theme: theme,
			renderer: function (index, label, value) 
			{
				var datarecord = listParty[index];
				if(datarecord.firstName != null && datarecord.middleName != null && datarecord.lastName != null){
					return datarecord.lastName + " " + datarecord.middleName + " " + datarecord.firstName;
				}
				if(datarecord.firstName != null && datarecord.middleName == null && datarecord.lastName != null){
					return datarecord.lastName + " " + datarecord.firstName;
				}
				if(datarecord.firstName != null && datarecord.middleName == null && datarecord.lastName == null){
					return datarecord.firstName;
				}
			}
		});
	}
	
	$("#partyId").on('select', function (event) {
		$("#roleTypeId").jqxDropDownList('clearSelection');
		var args = event.args;
	    if (args) {
		    var index = args.index;
		    var item = args.item;
		    var value = item.value;
		    $.ajax({
				  url: "loadRoleTypeIdByPartyId",
				  type: "POST",
				  data: {partyId: value},
				  dataType: "json"
			}).done(function(data) {
				var listRoleTypeId = data["listRoleTyleIdByPartyId"];
				bindingDataToRoleTypeIdDropDownList(listRoleTypeId);
			});
		    
	    }
    });
	
	function bindingDataToRoleTypeIdDropDownList(dataSource){
		$("#roleTypeId").jqxDropDownList({ disabled: false });
		$("#roleTypeId").jqxDropDownList({ source: dataSource, searchMode: 'contains', filterPlaceHolder: '${StringUtil.wrapString(uiLabelMap.LogFiltering)}' ,filterable: true, placeHolder: '${uiLabelMap.LogPleaseSelect}', displayMember: 'roleTypeId', valueMember: 'roleTypeId',
			renderer: function (index, label, value) 
			{
				var datarecord = dataSource[index];
				if(datarecord != null){
					return getRoleTypeId(datarecord.roleTypeId);
				}
			}
		});
	}
	
	
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
	
	
</script>