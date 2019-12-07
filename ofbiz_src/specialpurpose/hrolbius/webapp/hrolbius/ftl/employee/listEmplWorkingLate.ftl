<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#assign datafield = "[{name: 'partyId', type: 'string'},
	                  {name: 'partyName', type: 'string'},
	                  {name: 'emplWorkingLateId', type: 'string'},
	                  {name: 'dateWorkingLate', type: 'date'},
	                  {name: 'arrivalTime', type: 'date'},
	                  {name: 'delayTime', type: 'number'},
	                  {name: 'reason', type: 'string'},
	                  {name: 'statusId', type:'string'}]"/>
<script type="text/javascript">
var statusWorkingLateArr = new Array();
<#if statusListWorkinglate?exists>
var row = {};
	<#list statusListWorkinglate as status>
		row = {};
		row["statusId"]= "${status.statusId}";
		row["description"] = "${StringUtil.wrapString(status.description)}";
		statusWorkingLateArr[${status_index}] = row;
	</#list>
</#if>
<#assign groups = "dateWorkingLate"/>
<#assign columnlist = "{text: '${uiLabelMap.CommonDate}', datafield: 'dateWorkingLate', width: '12%',cellsformat: 'dd/MM/yyyy', editable: false, filtertype:'range', columntype: 'template'},
						{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId', width: '12%',editable: false, hidden: false},
						{text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', width: '12%',editable: false, hidden: false},
						{text: '${uiLabelMap.ArrivalTime}', datafield: 'arrivalTime', width: '12%', editable: false, cellsformat: 'HH:mm:ss', columntype: 'datetimeinput'},
						{text: '${uiLabelMap.HRDelayTime}', datafield: 'delayTime', editable: true, width: '13%', cellsalign: 'right', columntype: 'numberinput',
							validation: function (cell, value) {
						        if (value < 0) {
						            return { result: false, message: '${uiLabelMap.ValueNotLessThanZero}' };
						        }
						        return true;
						    },
							createeditor: function (row, cellvalue, editor) {
						        editor.jqxNumberInput({ decimalDigits: 0, digits: 3 });
						    }	
						},
						{text: '${uiLabelMap.HRCommonReason}', datafield: 'reason', editable: true,
							cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
						        // return the old value, if the new value is empty.
						        if (newvalue == '') return oldvalue;
						    }	
						},
						{text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', editable: true, width: '15%', columntype: 'dropdownlist',
							cellsrenderer: function(row, column, value){
								for(var i = 0; i < statusWorkingLateArr.length; i++){
									if(statusWorkingLateArr[i].statusId == value){
										return '<div style=\"margin-top: 4px; margin-left: 2px\">' + statusWorkingLateArr[i].description + '</div>';		
									}
								}
							},
							
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								 var source =
						           {
						               localdata: statusWorkingLateArr,
						               datatype: 'array'
						           };
								var dataAdapter = new $.jqx.dataAdapter(source);
								editor.jqxDropDownList({source: dataAdapter, autoDropDownHeight: true, displayMember: 'description', valueMember : 'statusId'});							 
						    },
						   	cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
						           // return the old value, if the new value is empty.
						           if (newvalue == '') return oldvalue;
						       }
						},
						{text: '', datafield: 'emplWorkingLateId', hidden: true, editable: false}"/>
						
</script>	
<script type="text/javascript">
	<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
	<#assign nextDay = Static["org.ofbiz.base.util.UtilDateTime"].getNextDayStart(nowTimestamp)>
	<#if parameters.fromDate?exists>
		<#assign fromDate = parameters.fromDate>
	<#else>
		<#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/> 
	</#if>
	<#if parameters.thruDate?exists>
		<#assign thruDate = parameters.thruDate>
	<#else>
		<#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(nowTimestamp, timeZone, locale)/> 
	</#if>
	var fromDate = new Date(${fromDate.getTime()});
	var thruDate = new Date(${thruDate.getTime()});
		
	$(document).ready(function () {
		$('#jqxgrid').on("bindingcomplete", function (event) {
		});
		/*=====================jqxDateTimeInput defind==========================*/
		var maxDateAddNew = new Date(${nowTimestamp.getTime()});
		$("#addDateWorkingLate").jqxDateTimeInput({ width: 200, height: 25, theme: 'olbius', max: maxDateAddNew});
		$("#jqxDateTime").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
		$("#jqxDateTime").jqxDateTimeInput('setRange', fromDate, thruDate);
		/*=====================./end jqxDateTimeInput defind==========================*/
		
		$("#jqxDateTime").on('change', function(event){
			var selection = $("#jqxDateTime").jqxDateTimeInput('getRange');
			var fromDate = selection.from;
			var thruDate = selection.to;
			var source = $("#jqxgrid").jqxGrid('source');
			source._source.url = "jqxGeneralServicer?sname=getEmplWorkingLateInPeriod&fromDate="+ fromDate.getTime() +"&thruDate=" + thruDate.getTime();
			$("#jqxgrid").jqxGrid('source', source);
			/* $('#jqxgrid').jqxGrid('expandallgroups'); */
		});
		
		/*====================jqxDropdownLits, jqxCombobox defind=========================*/
		filterStatusAdapter = new $.jqx.dataAdapter(statusWorkingLateArr, {autoBind: true});
		var dataSoureStatusList = filterStatusAdapter.records;
		$("#statusWorkingLateAppr, #addWorkingLateStatus").jqxDropDownList({ width: 200, source: dataSoureStatusList, displayMember: 'description', valueMember : 'statusId', 
		 	height: '25px', theme: 'olbius',autoDropDownHeight: true,
			renderer: function (index, label, value) {
				for(i=0; i < statusWorkingLateArr.length; i++){
					if(statusWorkingLateArr[i].statusId == value){
						return statusWorkingLateArr[i].description;
					}
				}	
			    return value;
			}
		});
		
		<#assign listEmplDirectMgr = Static["com.olbius.util.PartyUtil"].getListEmplDirectMgr(delegator, userLogin.partyId)/>
		var emplDataArr = [
			<#if listEmplDirectMgr?has_content>
				<#list listEmplDirectMgr as empl>
					<#assign partyName = Static["com.olbius.util.PartyUtil"].getPersonName(delegator, empl.partyId)/>
					{
						partyId: "${empl.partyId}",
						partyName: "${StringUtil.wrapString(partyName)}"
					}
					<#if empl_has_next>
					,
					</#if>
				</#list>
			</#if>
        ];
		var emplSource = {
				localdata: emplDataArr,
                datatype: "array"
		}
		var emplJqxComboboxAdapter = new $.jqx.dataAdapter(emplSource);
		
		/*====================./end jqxDropdownLits, jqxCombobox defind=========================*/
		
		/*================================jqxWindow definded===============================*/
		$("#jqxWindowAppr").jqxWindow({
			width: 500, minWidth: 550, height: 375,  maxHeight:375, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
			initContent: function(){
			}
    	});
		$("#jqxWindowConfirmDelete").jqxWindow({
			width: 400, minWidth: 400, height: 130,  maxHeight:130, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
		});
		$("#jqxWindowAddrow").jqxWindow({
			width: 500, minWidth: 550, height: 340,  maxHeight:340, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
			initContent: function(){
				$("#addEmployeeId").jqxComboBox({source: emplJqxComboboxAdapter, displayMember: "partyName", valueMember: "partyId", theme: 'olbius',
		      		itemHeight: 25, height: 25, width: 200, multiSelect: true,
		      		renderer: function (index, label, value) {
						for(var i=0; i < emplDataArr.length; i++){
							if(emplDataArr[i].partyId == value){
								return emplDataArr[i].partyName;
							}
						}
				    	return value;
		      		}	
				});
			}
		});
		$("#jqxWindowAddrow").on('open', function (event) {
			$("#addEmployeeId").jqxComboBox('clearSelection');
			$("#addDateWorkingLate").val(new Date());
			$("#addWorkingLateMinutes").val(0);
			$("#addWorkingLateReason").val("");
			$("#addWorkingLateStatus").jqxDropDownList('clearSelection');
		});
		/*============================ jqxValidator define =============================*/
		$("#addNewForm").jqxValidator({
			rules:[
				{input: "#addEmployeeId", message: '${uiLabelMap.CommonRequired}!', action: 'blur', 
					rule: function(input, commit){
						value = input.val();
						if(!value){
							return false;
						}
						return true;
					} 
				},
				{input: "#addDateWorkingLate", message: '${uiLabelMap.CommonRequired}!', action: 'blur', rule: 'required'},
				{input: '#addWorkingLateMinutes', message: '${uiLabelMap.AmountValueGreaterThanZero}', action: 'blur',
					rule: function (input, commit){
						var value = input.val();
						if(value < 0){
							return false
						}
						return true;
					}	
				},
				{input: "#addWorkingLateStatus", message: '${uiLabelMap.CommonRequired}!', action: 'blur',
					rule: function(input, commit){
						value = input.val();
						if(!value){
							return false;
						}
						return true;
					}
				}
			]
		});	
		/*============================ ./end jqxValidator ==============================*/
		
		$("#reasonWorkingLateAppr, #addWorkingLateReason").jqxInput({height: 25, width: 197, theme:'olbius'});
		$("#delayTimeWorkingLateAppr").jqxNumberInput({ width: 200, height: '25px', spinButtons: false, digits: 3, decimalDigits: 0, theme: 'olbius'});
		$("#addWorkingLateMinutes").jqxNumberInput({ width: 200, height: '25px', spinButtons: false, digits: 3, decimalDigits: 0, theme: 'olbius'});
		/*==================jqxEvent===========================*/
		$("#jqxgrid").on("rowDoubleClick", function(event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var rowData = $(this).jqxGrid("getrowdata", boundIndex);
		    /* clearDataInWindow();
		    $("#partyIdWorkingLateAppr").text(rowData.partyId);
			$("#partyNameWorkingLateAppr").text(rowData.partyName);
			var dateWorkingLate = rowData.dateWorkingLate;
			$("#dateWorkingLateAppr").text(dateWorkingLate.getDate() + "/" + (dateWorkingLate.getMonth() + 1) + "/" + dateWorkingLate.getFullYear());
			var arrivalTime = rowData.arrivalTime;
			if(arrivalTime){
				$("#arrivalTimeWorkingLateAppr").text(arrivalTime.getHours() + ":" + arrivalTime.getMinutes() + ":" + arrivalTime.getSeconds());	
			}else{
				$("#arrivalTimeWorkingLateAppr").text("${uiLabelMap.HRCommonNotSetting}");
			}
			$("#reasonWorkingLateAppr").jqxInput("val", rowData.reason);
			$("#delayTimeWorkingLateAppr").jqxNumberInput("val", rowData.delayTime);
			$("#statusWorkingLateAppr").val(rowData.statusId);
			$("#emplWorkingLateIdAppr").val(rowData.emplWorkingLateId);
			openJqxWindow($("#jqxWindowAppr")); */
		});
		/*=======================./end==========================*/
		
		/*=================== button event =====================*/
		$("#btnSave").click(function(){
			
			var emplWorkingLateId = $("#emplWorkingLateIdAppr").val();
			if(emplWorkingLateId){
				var dataUpdate = $("#jqxgrid").jqxGrid("getrowdatabyid", emplWorkingLateId);
				var updateRow = {
						emplWorkingLateId: emplWorkingLateId,
						partyId: dataUpdate.partyId,
						partyName: dataUpdate.partyName,
						dateWorkingLate: dataUpdate.dateWorkingLate,
						reason: $("#reasonWorkingLateAppr").val(),
						statusId: $("#statusWorkingLateAppr").val(),
						arrivalTime: dataUpdate.arrivalTime,
						delayTime: $("#delayTimeWorkingLateAppr").val()
				}
				
				$("#jqxgrid").jqxGrid('updaterow', emplWorkingLateId, updateRow);
			}
			$("#jqxWindowAppr").jqxWindow('close');
		});
		$("#btnCancel").click(function(){
			$("#jqxWindowAppr").jqxWindow('close');
		});
		
		$("#approvalAll").click(function(){
			$("#jqxgrid").jqxGrid("showloadelement");
			var selectionRange = $("#jqxDateTime").jqxDateTimeInput('getRange');
			$.ajax({
				url: "approvalAllEmplWorkingLateInPeriod",
				data: {fromDate: selectionRange.from.getTime(), thruDate: selectionRange.to.getTime(), statusId: "EMPL_LATE_ACCEPTED"},
				type: "POST",
				success: function(data){
					if(data._EVENT_MESSAGE_){
						$("#jqxgrid").jqxGrid('updatebounddata');
						openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
                        		"info", data._EVENT_MESSAGE_, "jqxNotifyContainerApprAll");
					}else{
						openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
								"error", data._ERROR_MESSAGE_, "jqxNotifyContainerApprAll");
					}
				},
				complete: function(){
					$("#jqxgrid").jqxGrid("hideloadelement");
				}
			});
		});
		$("#notApprovalAll").click(function(){
			$("#jqxgrid").jqxGrid("showloadelement");
			var selectionRange = $("#jqxDateTime").jqxDateTimeInput('getRange');
			$.ajax({
				url: "approvalAllEmplWorkingLateInPeriod",
				data: {fromDate: selectionRange.from.getTime(), thruDate: selectionRange.to.getTime(), statusId: "EMPL_LATE_REJECTED"},
				type: "POST",
				success: function(data){
					if(data._EVENT_MESSAGE_){
						$("#jqxgrid").jqxGrid('updatebounddata');
                        openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
                        		"info", data._EVENT_MESSAGE_, "jqxNotifyContainerApprAll");
					}else{
                        openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
                        		"error", data._ERROR_MESSAGE_, "jqxNotifyContainerApprAll");
					}
				},
				complete: function(){
					$("#jqxgrid").jqxGrid("hideloadelement");
				}
			});
		});
		
		$("#addNewRow").click(function(event){
			openJqxWindow($("#jqxWindowAddrow"));
		});
		
		$("#updateFromRecorder").click(function(){
			$("#jqxgrid").jqxGrid({ disabled: true});
			$('#jqxgrid').jqxGrid('showloadelement');
			$(this).attr("disabled", "disabled");
			$.ajax({
				url: "updateAllEmplWorkingLateInPeriod",
				data: {fromDate: ${fromDate.getTime()}, thruDate: ${thruDate.getTime()}},
				type: "POST",
				success: function(data){
					if(data.responseMessage == "success"){
						$("#jqxgrid").jqxGrid("updatebounddata");	
						openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
                        		"info", data.successMessage, "jqxNotifyContainerApprAll");
					}else{
						openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
                        		"error", data.errorMessage, "jqxNotifyContainerApprAll");
					}
				},
				complete: function(){
					$("#jqxgrid").jqxGrid({ disabled: false});
					$('#jqxgrid').jqxGrid('hideloadelement');
					$("#updateFromRecorder").removeAttr("disabled");
				}
			});
		});
		
		$("#btnSaveAddNew").click(function(){
			var valid = $("#addNewForm").jqxValidator('validate');
			if(!valid){
				return false;
			}
			var partyIds = $("#addEmployeeId").jqxComboBox('getSelectedItems');
			var partyIdSubmit = new Array();
			for(var i = 0; i < partyIds.length; i++){
				partyIdSubmit.push({"partyId" : partyIds[i].value});
			}
			var data = {
					partyId: JSON.stringify(partyIdSubmit),
					dateWorkingLate: $("#addDateWorkingLate").jqxDateTimeInput('val', 'date').getTime(),
					delayTime: $("#addWorkingLateMinutes").val(),
					statusId: $("#addWorkingLateStatus").val(),
					reason: $("#addWorkingLateReason").val(),
			}
			$.ajax({
				url: "createEmplWorkingLateExt",
				type: "POST",
				data: data,
				success: function(data){
					$("#jqxNotificationAddNew").jqxNotification('closeLast');
					if(data._EVENT_MESSAGE_){
						$("#jqxNotificationAddNew").jqxNotification({
			                width: "100%", opacity: 0.9,appendContainer: "#jqxNotificationContainer",
			                autoOpen: false,  autoClose: false, template: "info"
			            });
						$("#jqxNotificationAddNew").text(data._EVENT_MESSAGE_);
						$("#jqxgrid").jqxGrid("updatebounddata");	
					}else{
						$("#jqxNotificationAddNew").jqxNotification({
			                width: "100%", opacity: 0.9, appendContainer: "#jqxNotificationContainer",
			                autoOpen: false,  autoClose: false, template: "error"
			            });
						$("#jqxNotificationAddNew").text(data._ERROR_MESSAGE_);
					}
					$("#jqxNotificationAddNew").jqxNotification('open');
				}, 
				error: function(){
					
				}
			});
			$("#jqxWindowAddrow").jqxWindow('close');
		});
		$("#btnCancelAddNew").click(function(){
			$("#jqxWindowAddrow").jqxWindow('close');
		});
		$("#deleteRow").click(function(){
			var selectrow = $("#jqxgrid").jqxGrid("getselectedrowindex");
			if(selectrow > -1){
				openJqxWindow($("#jqxWindowConfirmDelete"));
			}
		});
		$("#acceptDelete").click(function(){
			var selectrowIndex = $("#jqxgrid").jqxGrid("getselectedrowindex");
			if(selectrowIndex > -1){
				var rowId = $('#jqxgrid').jqxGrid('getrowid', selectrowIndex);
				$("#jqxgrid").jqxGrid('deleterow', selectrowIndex);
			}
			$("#jqxWindowConfirmDelete").jqxWindow('close');
		});
		$("#cancelDelete").click(function(){
			$("#jqxWindowConfirmDelete").jqxWindow('close');
			
		});
		/*=================== ./end button event ===============*/
		
	});
	function clearDataInWindow(){
		$("#partyIdWorkingLateAppr").text("");
		$("#partyNameWorkingLateAppr").text("");
		$("#dateWorkingLateAppr").text("");
		$("#arrivalTimeWorkingLateAppr").text("");
		$("#reasonWorkingLateAppr").val("");
		$("#delayTimeWorkingLateAppr").val(0);
		$("#emplWorkingLateIdAppr").val("");
	}
	function openJqxWindow(jqxWindowDiv){
		var wtmp = window;
		var tmpwidth = jqxWindowDiv.jqxWindow('width');
		jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
		jqxWindowDiv.jqxWindow('open');
	}
	
	function openNotification(notifyDiv, contentDiv, template, text, containerId){
		notifyDiv.jqxNotification('closeLast');
		contentDiv.text(text);
		notifyDiv.jqxNotification({
            width: "100%", opacity: 0.9, appendContainer: "#" + containerId,
            autoOpen: false,  autoClose: false, template: template});
		notifyDiv.jqxNotification("open");
	}
</script>
<div class="row-fluid">
	<div id="jqxNotificationContainer">
		<div id="jqxNotificationAddNew"></div>
	</div>
</div>   
<div class="row-fluid">
	<div id="jqxNotifyContainerApprAll">
		<div id="jqxNotificationApprovalAll">
			<div id="jqxNotificationApprovalContent"></div>
		</div>
	</div>
</div>	
<div class="row-fluid">
	<div id="jqxWindowConfirmDelete" style="display: none;">
		<div>${uiLabelMap.accDeleteSelectedRow}</div>
		<div class="row-fluid">
			<div class="row-fluid">
				<b>${uiLabelMap.wgdeleteconfirm}</b>
			</div>
			<div class="row-fluid" style="text-align: right; margin-top: 20px">
				<button class="btn btn-mini btn-primary icon-ok" id="acceptDelete">${StringUtil.wrapString(uiLabelMap.wgok)}</button>
				<button class="btn btn-mini btn-danger icon-remove" id="cancelDelete">${StringUtil.wrapString(uiLabelMap.wgcancel)}</button>
			</div>
		</div>	
	</div>
</div>               
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.HREmplWorkingLateList}</h4>
		<button id="addNewRow" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget
				jqx-widget-olbius jqx-fill-state-pressed jqx-fill-state-pressed-olbius" 
				style="margin-right: 20px; cursor: pointer;" title="${uiLabelMap.HREmplCreateWorkingLate}">
			<i class="icon-plus-sign"></i>
		</button>
		<button id="deleteRow" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget
				jqx-widget-olbius jqx-fill-state-pressed jqx-fill-state-pressed-olbius" 
				style="margin-right: 20px; cursor: pointer;" title="${uiLabelMap.accDeleteSelectedRow}">
			<i class="icon-remove"></i>
		</button>
		<button id="updateFromRecorder" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget
				jqx-widget-olbius jqx-fill-state-pressed jqx-fill-state-pressed-olbius" 
				style="margin-right: 20px; cursor: pointer;" title="${uiLabelMap.UpdateEmplWorkingLateFromTimeRecorder}">
			<i class="icon-clock-o"></i>
		</button>
	</div>
	<div class="widget-body">
		<div class="row-fluid" style="margin-bottom: 10px">
			<div class="span12">
				<div class="span7 form-horizontal">
					<div class="">
						<label class=""><b>${uiLabelMap.NotificationDateTime}:</b></label>
						<div class="controls" style="margin-left: 80px !important">
							<div id="jqxDateTime" style=""></div>
						</div>
					</div>
				</div>
				<div class="span5" style="text-align: right;" id="toolbarjqxgridApproval">
					<button id="approvalAll" id="approvalAll" 
						class="jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" 
						style="margin-right:20px;" title="${uiLabelMap.ApprovalAll}"><i class="icon-ok"></i>${uiLabelMap.ApprovalAll}</button>
					<button id="notApprovalAll" style="margin-right:20px;" title="${uiLabelMap.NotApprovalAll}"
						class="jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" >
							<i class="icon-remove"></i>${uiLabelMap.NotApprovalAll}</button>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid url="jqxGeneralServicer?sname=getEmplWorkingLateInPeriod&fromDate=${fromDate.getTime()}&thruDate=${thruDate.getTime()}" 
				dataField=datafield columnlist=columnlist
				editable="true" groupable="true" groups=groups
				editrefresh ="true" groupsexpanded="true"
				editmode="dblclick" id="jqxgrid" selectionmode="singlerow"
				showtoolbar="false" deleterow="true" jqGridMinimumLibEnable="false" 				
				createUrl="jqxGeneralServicer?jqaction=C&sname=" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
				addColumns="" sourceId="emplWorkingLateId"
				updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEmplWorkingLate" 
				removeUrl="jqxGeneralServicer?sname=deleteEmplWorkingLate&jqaction=D" deleteColumn="emplWorkingLateId"
				editColumns="emplWorkingLateId;dateWorkingLate(java.sql.Timestamp);delayTime(java.lang.Long);reason;statusId"
			/>			
		</div>
	</div>
</div>
<div class="row-fluid" style="display: none">
	<div id="jqxWindowAppr" style="display: none;">
		<div>${uiLabelMap.HREmplWorkingLateApproval}</div>
		<div class="row-fluid">
			<div class="form-horizontal row-fluid">
				<input type="hidden" name="emplWorkingLateId" id="emplWorkingLateIdAppr">
				<div class="control-group">
					<label class="control-label">${uiLabelMap.EmployeeId}</label>
					<div class="controls">
						<span id="partyIdWorkingLateAppr"></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.EmployeeName}</label>
					<div class="controls">
						<span id="partyNameWorkingLateAppr"></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.CommonDate}</label>
					<div class="controls">
						<span id="dateWorkingLateAppr"></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.ArrivalTime}</label>
					<div class="controls">
						<span id="arrivalTimeWorkingLateAppr"></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.HRDelayTime}</label>
					<div class="controls">
						<div id="delayTimeWorkingLateAppr"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.HRCommonReason}</label>
					<div class="controls">
						<input id="reasonWorkingLateAppr" type="text"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.CommonStatus}</label>
					<div class="controls">
						<div id="statusWorkingLateAppr"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">&nbsp;</label>
					<div class="controls" >
						<button class="btn btn-small btn-primary icon-ok" id="btnSave">${uiLabelMap.CommonSave}</button>
						<button class="btn btn-small btn-danger icon-remove" id="btnCancel">${uiLabelMap.CommonCancel}</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div id="jqxWindowAddrow" style="display: none;">
		<div>${uiLabelMap.HREmplCreateWorkingLate}</div>
		<div class="row-fluid form-horizontal">
			<form id="addNewForm">
				<div class="control-group">
					<label class="control-label asterisk">${uiLabelMap.EmployeeName}</label>
					<div class="controls">
						<div id="addEmployeeId"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label asterisk">${uiLabelMap.DateWorkingLate}</label>
					<div class="controls">
						<div id="addDateWorkingLate"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.TotalWorkingLateMinutes}</label>
					<div class="controls">
						<div id="addWorkingLateMinutes"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.HRReasonLate}</label>
					<div class="controls">
						<input id="addWorkingLateReason" type="text"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label asterisk">${uiLabelMap.CommonStatus}</label>
					<div class="controls">
						<div id="addWorkingLateStatus"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">&nbsp;</label>
					<div class="controls" >
						<button class="btn btn-small btn-primary icon-ok" type="button" id="btnSaveAddNew">${uiLabelMap.CommonSave}</button>
						<button class="btn btn-small btn-danger icon-remove" type="button" id="btnCancelAddNew">${uiLabelMap.CommonCancel}</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
