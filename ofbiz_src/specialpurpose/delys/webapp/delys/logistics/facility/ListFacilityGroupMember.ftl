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

<script  type="text/javascript">
	var facilityGroupId = '${parameters.facilityGroupId}';
	
	<#assign facilityList = delegator.findList("Facility", null, null, null, null, false) />
	var facilityData = new Array();
	<#list facilityList as facility>
		var row = {};
		row['facilityId'] = "${facility.facilityId}";
		row['facilityName'] = "${StringUtil.wrapString(facility.get('facilityName', locale)?if_exists)}";
		facilityData[${facility_index}] = row;
	</#list>
	function getFacilityId(facilityId) {
		for ( var x in facilityData) {
			if (facilityId == facilityData[x].facilityId) {
				return facilityData[x].facilityName+"["+facilityId+"]";
			}
		}
	}
</script>

<div>
		<div id="contentNotificationCreateFacilityGroupMemberSuccess">
		</div>
		
		<#assign dataField="[
			{ name: 'facilityId', type: 'string'},
			{ name: 'fromDate', type: 'date', other: 'Timestamp'},
			{ name: 'thruDate', type: 'date', other: 'Timestamp'},
			{ name: 'sequenceNum', type: 'number' },
		]"/>
		<#assign columnlist="
		    { text: '${StringUtil.wrapString(uiLabelMap.distributorInventory)}', datafield: 'facilityId', editable:false, width: 350,
				cellsrenderer: function(row, colum, value){
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					var valueFacility = data.facilityId;
					var facilityId = getFacilityId(valueFacility);
				    return '<span>' + facilityId + '</span>';
			    },  
		    },
			{ text: '${StringUtil.wrapString(uiLabelMap.fromDate)}', datafield: 'fromDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable:false},
			{ text: '${StringUtil.wrapString(uiLabelMap.thruDate)}', datafield: 'thruDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable:false},
			{ text: '${StringUtil.wrapString(uiLabelMap.CommonSequenceNum)}', datafield: 'sequenceNum', editable:false},
		"/>
		
		<@jqGrid filtersimplemode="true" id="jqxgrid" filterable="true" addType="popup" dataField=dataField columnlist=columnlist addrow="true"  editable="true" alternativeAddPopup="alterpopupWindow"  clearfilteringbutton="true" showtoolbar="true" editmode="click"
			url="jqxGeneralServicer?sname=JQXgetFacilityGroupMember&facilityGroupId=${parameters.facilityGroupId}"
		/>
</div>

<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.ProductAddFacilityGroupMember}</div>
	<div>
		<div id="contentNotificationContentCreateFacilityGroupMemberError" class="popup-notification">
		</div>
		<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.distributorInventory)}</label>
			</div>  
			<div class="span7">
				<div id="facilityId">
				</div>
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
				<label>${StringUtil.wrapString(uiLabelMap.thruDate)}</label>
			</div>  
			<div class="span7">
				<div id="thruDate"></div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.CommonSequenceNum)}</label>
			</div>  
			<div class="span7">
				<input id="sequenceNum"></input>
	   		</div>
	   	</div>
	   	<div class="form-action">
			<button id="alterCancel" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSave" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>  
		</div>
	</div>
</div>



<div id="alterpopupWindowEdit" class='hide'>
	<div>${uiLabelMap.LogEditFacilityGroupMemeber}</div>
	<div>
		<div id="contentNotificationContentCreateFacilityGroupMemberErrorEdit" class="popup-notification">
		</div>
		<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.distributorInventory)}</label>
			</div>  
			<div class="span7">
				<div id="facilityIdEdit">
				</div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.fromDate)}</label>
			</div>  
			<div class="span7">
				<div id="fromDateEdit"></div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.thruDate)}</label>
			</div>  
			<div class="span7">
				<div id="thruDateEdit"></div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.CommonSequenceNum)}</label>
			</div>  
			<div class="span7">
				<input id="sequenceNumEdit"></input>
	   		</div>
	   	</div>
	   	<div class="form-action">
			<button id="alterCancelEdit" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSaveEdit" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>  
		</div>
	</div>
</div>

<div id="jqxNotificationCreateFacilityGroupMemberError" >
	<div id="notificationCreateFacilityGroupMemberError">
	</div>
</div>

<div id="jqxNotificationEditFacilityGroupMemberSuccess" >
	<div id="notificationEditFacilityGroupMemberSuccess">
	</div>
</div>

<div id="jqxNotificationEditFacilityGroupMemberErrorEdit" >
	<div id="notificationCreateFacilityGroupMemberErrorEdit">
	</div>
</div>
<div id='Menu' class="hide">
	<ul>
		<li>${StringUtil.wrapString(uiLabelMap.DSEditRowGird)}</li>
	</ul>
</div>
<script>

	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$("#jqxNotificationCreateFacilityGroupMemberError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateFacilityGroupMemberError", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationEditFacilityGroupMemberSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateFacilityGroupMemberSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#jqxNotificationEditFacilityGroupMemberErrorEdit").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateFacilityGroupMemberErrorEdit", opacity: 0.9, autoClose: true, template: "error" });
	
	$("#facilityId").jqxDropDownList({ source: facilityData, placeHolder: '${uiLabelMap.LogPleaseSelect}', displayMember: 'facilityName', valueMember: 'facilityId'});
	$("#fromDate").jqxDateTimeInput({ 
		 showFooter:true,
	    clearString:'Clear'});
	$("#thruDate").jqxDateTimeInput({
		 showFooter:true,
	    clearString:'Clear'});
	$("#sequenceNum").jqxInput({width: 195});
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 600, minWidth: 400, height: 270 ,width:1110, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	
	$('#alterpopupWindow').jqxValidator({
    	rules: 
		[	
		 	{
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
    	]
    });
	
	function convertDate(date){
	     if (date == "" || date == undefined) {
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
	
	$('#alterpopupWindow').on('open', function (event) { 
		$('#alterpopupWindow').jqxValidator('hide');
		$('#fromDate').val("");
		$('#thruDate').val("");
	}); 
	
	$("#alterSave").click(function () {
		var facilityId = $('#facilityId').val();
		var fromDate = $('#fromDate').val();
		var valueFromDate = convertDate(fromDate);
		var thruDate = $('#thruDate').val();
		var valueThruDate = convertDate(thruDate);
		var sequenceNum = $('#sequenceNum').val();
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		if(validate != false){
			if(facilityId == "" || fromDate == ""){
				$("#notificationCreateFacilityGroupMemberError").text('${StringUtil.wrapString(uiLabelMap.LogCheckSelectFacilityId)}');
				$("#jqxNotificationCreateFacilityGroupMemberError").jqxNotification('open');
			}else{
				document.getElementById("alterSave").disabled = true;
				createFacilityGroupMember(facilityId, valueFromDate, valueThruDate, sequenceNum);
			}
		}
	});
	
	
	function createFacilityGroupMember(facilityId, valueFromDate, valueThruDate, sequenceNum){
		$.ajax({
			  url: "createFacilityGroupMember",
			  type: "POST",
			  data: {facilityId: facilityId, facilityGroupId: facilityGroupId, fromDate: valueFromDate, thruDate: valueThruDate, sequenceNum: sequenceNum},
			  dataType: "json"
		}).done(function(data) {
			var value = data["value"];
			if(value == "exits"){
				$("#notificationCreateFacilityGroupMemberError").text('${StringUtil.wrapString(uiLabelMap.checkInventoryItemExits)}');
				$("#jqxNotificationCreateFacilityGroupMemberError").jqxNotification('open');
				document.getElementById("alterSave").disabled = false;
			}
			if(value == "sequenceNumNotNumber"){
				$("#notificationCreateFacilityGroupMemberError").text('${StringUtil.wrapString(uiLabelMap.LogChecksequenceNumIsNotNumber)}');
				$("#jqxNotificationCreateFacilityGroupMemberError").jqxNotification('open');
				document.getElementById("alterSave").disabled = false;
			}
			if(value == "sequenceNumNotMaxLength"){
				$("#notificationCreateFacilityGroupMemberError").text('${StringUtil.wrapString(uiLabelMap.DSCheckCharacterValidate1To20)}');
				$("#jqxNotificationCreateFacilityGroupMemberError").jqxNotification('open');
				document.getElementById("alterSave").disabled = false;
			}
			if(value == "success"){
				$("#notificationEditFacilityGroupMemberSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiCreateSucess)}');
				$("#jqxNotificationEditFacilityGroupMemberSuccess").jqxNotification('open');
				$("#alterpopupWindow").jqxWindow('close');
				$('#jqxgrid').jqxGrid('updatebounddata');
			}
			
		});
	}
	
	$('#alterpopupWindow').on('close', function (event) { 
		$('#alterpopupWindow').jqxValidator('hide');
		$("#facilityId").jqxDropDownList('clearSelection');
		$("#sequenceNum").val("");
		document.getElementById("alterSave").disabled = false;
	});
	
	
	
	
	$("#facilityIdEdit").jqxDropDownList({ source: facilityData, placeHolder: '${uiLabelMap.LogPleaseSelect}', displayMember: 'facilityName', valueMember: 'facilityId', theme: theme});
	$("#fromDateEdit").jqxDateTimeInput({ 
		 showFooter:true,
	    clearString:'Clear'});
	$("#thruDateEdit").jqxDateTimeInput({
		 showFooter:true,
	    clearString:'Clear'});
	$("#sequenceNumEdit").jqxInput({width: 195});
	$("#alterpopupWindowEdit").jqxWindow({
		maxWidth: 600, minWidth: 400, height: 270 ,width:1110, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelEdit"), modalOpacity: 0.7, theme:theme           
	});
	$('#alterpopupWindowEdit').jqxValidator({
    	rules: 
		[	
		 	{
        		input: '#thruDateEdit', message: '${StringUtil.wrapString(uiLabelMap.faFromDateLTThruDate)}', action: 'valueChanged', rule: function (input, commit) {
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
	var contextMenu = $("#Menu").jqxMenu({ width: 200, height: 30, autoOpenPopup: false, mode: 'popup'});
    $("#jqxgrid").on('contextmenu', function () {
        return false;
    });
    $("#Menu").on('itemclick', function (event) {
        var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        editrow = rowindex;
        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', editrow);
        var facilityId = dataRecord.facilityId;
        var fromDate = dataRecord.fromDate;
        var thruDate = dataRecord.thruDate;
        var sequenceNum = dataRecord.sequenceNum;	
        bindingDataEditGroupFacilityMember(facilityId, fromDate, thruDate, sequenceNum);
    });
    
    function bindingDataEditGroupFacilityMember(facilityId, fromDate, thruDate, sequenceNum){
		$('#facilityIdEdit').val(facilityId);
		$('#sequenceNumEdit').val(sequenceNum);
		if(thruDate == null){
			$('#thruDateEdit').jqxDateTimeInput('setDate', new Date(""));
		}
		if(thruDate != null && thruDate != undefined){
			$('#thruDateEdit').jqxDateTimeInput('setDate', new Date(thruDate));
		}
		$('#fromDateEdit').jqxDateTimeInput('setDate', new Date(fromDate));
		$("#alterpopupWindowEdit").jqxWindow('open');
	}
    $("#jqxgrid").on('rowClick', function (event) {
        if (event.args.rightclick) {
            $("#jqxgrid").jqxGrid('selectrow', event.args.rowindex);
            var scrollTop = $(window).scrollTop();
            var scrollLeft = $(window).scrollLeft();
            contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
            return false;
        }
    });
    
    $("#alterSaveEdit").click(function () {
		var facilityId = $('#facilityIdEdit').val();
		var fromDate = $('#fromDateEdit').val();
		var thruDate = $('#thruDateEdit').val();
		var sequenceNum = $('#sequenceNumEdit').val();
		var valueThruDate = convertDate(thruDate);
		var valueFromDate = convertDate(fromDate);
		var validate = $('#alterpopupWindowEdit').jqxValidator('validate');
		if(validate != false){
			document.getElementById("alterSaveEdit").disabled = true;
			editFacilityGroupMember(facilityGroupId, facilityId, valueFromDate, valueThruDate, sequenceNum);
		}
	});
    
    
    function editFacilityGroupMember(facilityGroupId, facilityId, valueFromDate, valueThruDate, sequenceNum){
		$.ajax({
			  url: "editFacilityGroupMember",
			  type: "POST",
			  data: {facilityGroupId: facilityGroupId, facilityId: facilityId, fromDate: valueFromDate, thruDate: valueThruDate, sequenceNum: sequenceNum},
			  dataType: "json"
		}).done(function(data) {
			var value = data["value"];
			if(value == "notEdit"){
				$("#alterpopupWindowEdit").jqxWindow('close');
			}
			if(value == "sequenceNumNotNumber"){
				$("#notificationCreateFacilityGroupMemberErrorEdit").text('${StringUtil.wrapString(uiLabelMap.LogChecksequenceNumIsNotNumber)}');
			    $("#jqxNotificationEditFacilityGroupMemberErrorEdit").jqxNotification('open');
			    document.getElementById("alterSaveEdit").disabled = false;
			}
			if(value == "sequenceNumMaxLength"){
				$("#notificationCreateFacilityGroupMemberErrorEdit").text('${StringUtil.wrapString(uiLabelMap.DSCheckCharacterValidate1To20)}');
			    $("#jqxNotificationEditFacilityGroupMemberErrorEdit").jqxNotification('open');
			    document.getElementById("alterSaveEdit").disabled = false;
			}
			if(value == "success"){
				$("#notificationEditFacilityGroupMemberSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
				$("#jqxNotificationEditFacilityGroupMemberSuccess").jqxNotification('open');
				$("#alterpopupWindowEdit").jqxWindow('close');
				$('#jqxgrid').jqxGrid('updatebounddata');
			}
		});
	}
    
    $('#alterpopupWindowEdit').on('open', function (event) { 
		$('#alterpopupWindowEdit').jqxValidator('hide');
		$('#facilityIdEdit').jqxDropDownList({disabled: true });
		$('#fromDateEdit').jqxDateTimeInput({disabled: true });
		document.getElementById("alterSaveEdit").disabled = false;
	}); 

	$('#alterpopupWindowEdit').on('close', function (event) { 
		$('#alterpopupWindowEdit').jqxValidator('hide');
		$('#facilityIdEdit').val("");
		$('#fromDateEdit').jqxDateTimeInput('setDate', new Date(""));
		$('#thruDateEdit').jqxDateTimeInput('setDate', new Date(""));
		$('#sequenceNumEdit').val("");
		document.getElementById("alterSaveEdit").disabled = false;
	});
</script>