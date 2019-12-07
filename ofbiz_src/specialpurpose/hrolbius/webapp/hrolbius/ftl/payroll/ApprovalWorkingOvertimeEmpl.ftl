<@jqGridMinimumLib/>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.ListEmplWorkOvertime}</h4>
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
			<div id="jqxNotifyContainer">
				<div id="jqxNotification"></div>
			</div>
		</div>	
		<div class="row-fluid">
			<@htmlTemplate.renderEmplWorkOverTime id="jqxgridEmplWorkOvertime" width="99%" updaterow="true" jqxNotifyId="jqxNotifyEmplWorkOvertime" jqxGridInWindow="false" 
				updateFuntion="getWorkOvertimeEmpl" showtoolbar="false" autoheight="true" height="" jqxNotifyId="jqxNotification" titleProperty="ListEmplWorkOvertime"/>
		</div>	
	</div>
</div>
<div class="row-fluid">
	<div id="jqxWindowWorkOvertime" style="display: none;">
		<div id="windowHeader">
			
		</div>
		<div id="windowContent">
			<div class="row-fluid">
				<div id="jqxNotifyOvertimeContainer">
					<div id="jqxNotifyWorkOvertime"></div>
				</div>
			</div>
			<div class="form-horizontal row-fluid">
				<input type="hidden" name="workOvertimeRegisId" id="workOvertimeRegisIdPopup">
				<div class="control-group">
					<label class="control-label">${uiLabelMap.EmployeeId}</label>
					<div class="controls">
						<span id="partyIdWorkOvertime"></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.EmployeeName}</label>
					<div class="controls">
						<span id="partyNameWorkOvertime"></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.DateWorkOvertime}</label>
					<div class="controls">
						<span id="dateWorkOvertime"></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.overTimeFromDate}</label>
					<div class="controls">
						<div id="regStartTimeWorkOvertime"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.overTimeThruDate}</label>
					<div class="controls">
						<div id="regEndTimeWorkOvertime"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.CommonReason}</label>
					<div class="controls">
						<span id="reasonWorkOvertime"></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.ActualStartOverTime}</label>
					<div class="controls">
						<div id="startTimeWorkOvertime"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.ActualEndOverTime}</label>
					<div class="controls">
						<div id="endTimeWorkOvertime"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.CommonStatus}</label>
					<div class="controls">
						<div id="approvalStatus"></div>
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
<script type="text/javascript">
$(document).ready(function () {
	var nowDate = new Date();
	<#if parameters.fromDate?exists>
		var fromDate = new Date(${parameters.fromDate});
	<#else>
		var fromDate = new Date(nowDate.getFullYear(), nowDate.getMonth() - 1, 1);
	</#if>
	<#if parameters.thruDate?exists>
		var thruDate = new Date(${parameters.thruDate});
	<#else>
		var thruDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), 0);
	</#if>
	getWorkOvertimeEmpl(fromDate, thruDate);
	$("#jqxDateTime").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
	$("#jqxDateTime").jqxDateTimeInput('setRange', fromDate, thruDate);
	
	$("#jqxDateTime").on('change', function(event){
		var selection = $("#jqxDateTime").jqxDateTimeInput('getRange');
		var fromDate = selection.from;
		var thruDate = selection.to;
		getWorkOvertimeEmpl(fromDate, thruDate);
		
	});
	
	/*======================== jqxDropDownlist definde ========================*/
	var statusApprPopup = new Array();
	<#if statusListWorkOvertime?exists>
		var row = {};
		<#list statusListWorkOvertime as status>
			row = {};
			row["statusId"]= "${status.statusId}";
			row["description"] = "${StringUtil.wrapString(status.description)}";
			statusApprPopup[${status_index}] = row;
		</#list>
		var filterStatusAdapter = new $.jqx.dataAdapter(statusApprPopup, {autoBind: true});
		var dataSoureStatusList = filterStatusAdapter.records;
		$("#approvalStatus").jqxDropDownList({ width: '180px', source: dataSoureStatusList, displayMember: 'description', valueMember : 'statusId', 
		 	height: '25px', theme: 'olbius',autoDropDownHeight: true,
			renderer: function (index, label, value) {
				for(i=0; i < statusApprPopup.length; i++){
					if(statusApprPopup[i].statusId == value){
						return statusApprPopup[i].description;
					}
				}
			    return value;
			}
		});
	</#if>
	/*=================== ./end jqxDropDownlist definde ========================*/
	$("#btnCancel").click(function(){
		$("#jqxWindowWorkOvertime").jqxWindow('close');
	});
	$("#btnSave").click(function(){
		//$(this).attr("disabled", "disabled");
		var rowUpdateId = $("#workOvertimeRegisIdPopup").val();
		
		if(rowUpdateId){
			$('#jqxNotification').jqxNotification('closeLast');
			var currRowData = $("#jqxgridEmplWorkOvertime").jqxGrid('getrowdatabyid', rowUpdateId);
			var row = {
					partyId: currRowData.partyId,
					partyName: currRowData.partyName,
					overTimeFromDate: currRowData.overTimeFromDate,
					overTimeThruDate: currRowData.overTimeThruDate,
					workOvertimeRegisId: rowUpdateId, 
					dateRegistration: currRowData.dateRegistration,
					actualStartTime: $("#startTimeWorkOvertime").jqxDateTimeInput('val', 'date'),
					actualEndTime: $("#endTimeWorkOvertime").jqxDateTimeInput('val', 'date'),
					statusId: $("#approvalStatus").val()
			}
			$("#jqxgridEmplWorkOvertime").jqxGrid('updaterow', rowUpdateId, row);
		}
		$("#jqxWindowWorkOvertime").jqxWindow('close');
	});
	$("#jqxNotification").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#jqxNotifyContainer"});
	$("#jqxWindowWorkOvertime").jqxWindow({
		width: 500, minWidth: 550, height: 420,  maxHeight:420, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
		initContent: function(){
			$("#jqxNotifyWorkOvertime").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#jqxNotifyOvertimeContainer"});
			$("#startTimeWorkOvertime").jqxDateTimeInput({ width: '180px', height: '25px', formatString: 'HH:mm:ss', showCalendarButton: false, theme: 'olbius'});
			$("#endTimeWorkOvertime").jqxDateTimeInput({ width: '180px', height: '25px', formatString: 'HH:mm:ss', showCalendarButton: false, theme: 'olbius'});
			/* $("#regStartTimeWorkOvertime").jqxDateTimeInput({ width: '180px', height: '25px', formatString: 'HH:mm:ss', showCalendarButton: false, theme: 'olbius'});
			$("#regEndTimeWorkOvertime").jqxDateTimeInput({ width: '180px', height: '25px', formatString: 'HH:mm:ss', showCalendarButton: false, theme: 'olbius'}); */
		}
    });
	$("#jqxgridEmplWorkOvertime").on('rowDoubleClick', function(event){
		var args = event.args;
	    var boundIndex = args.rowindex;
		setWorkOvertimeContentWindow(boundIndex);	    
	});
	$("#approvalAll").click(function(){
		$("#jqxgridEmplWorkOvertime").jqxGrid("showloadelement");
		$('#jqxNotification').jqxNotification('closeLast');
		var selectionRange = $("#jqxDateTime").jqxDateTimeInput('getRange');
		$.ajax({
			url: "approvalAllWorkOvertimeInPeriod",
			data: {fromDate: selectionRange.from.getTime(), thruDate: selectionRange.to.getTime(), statusId: "WOTR_ACCEPTED"},
			type: 'POST',
			success: function(data){
				if(data._EVENT_MESSAGE_){
					getWorkOvertimeEmpl(fromDate, thruDate);
				}else{
					$('#jqxNotification').jqxNotification({ template: 'error'});
                	$("#jqxNotification").text(data._ERROR_MESSAGE_);
                	$("#jqxNotification").jqxNotification("open");
				}
			}, 
			complete: function(){
				$("#jqxgridEmplWorkOvertime").jqxGrid("hideloadelement");
			}
		});
	});
	$("#notApprovalAll").click(function(){
		$('#jqxNotification').jqxNotification('closeLast');
		var selectionRange = $("#jqxDateTime").jqxDateTimeInput('getRange');
		$.ajax({
			url: "approvalAllWorkOvertimeInPeriod",
			data: {fromDate: selectionRange.from.getTime(), thruDate: selectionRange.to.getTime(), statusId: "WOTR_REJECTED"},
			type: 'POST',
			success: function(data){
				if(data._EVENT_MESSAGE_){
					getWorkOvertimeEmpl(fromDate, thruDate);
				}
			}	
		});
	});
});

function setWorkOvertimeContentWindow(rowIndex){
	var rowData = $("#jqxgridEmplWorkOvertime").jqxGrid('getrowdata', rowIndex);
	clearWorkOvertimeContentWindow();
	$("#jqxWindowWorkOvertime").jqxWindow("setTitle", "${uiLabelMap.ApproverWorkingOvertime} ${uiLabelMap.CommonFor} " + rowData.partyName);
	$("#partyIdWorkOvertime").text(rowData.partyId);
	$("#partyNameWorkOvertime").text(rowData.partyName);
	var dateRegister = rowData.dateRegistration;
	$("#dateWorkOvertime").text(dateRegister.getDate() + "/" + (dateRegister.getMonth() + 1) + "/" + dateRegister.getFullYear());
	$("#startTimeWorkOvertime").val(rowData.actualStartTime);
	$("#endTimeWorkOvertime").val(rowData.actualEndTime);
	$("#workOvertimeRegisIdPopup").val(rowData.workOvertimeRegisId);
	if(rowData.reasonRegister){
		$("#reasonWorkOvertime").text(rowData.reasonRegister);
	}else{
		$("#reasonWorkOvertime").text("${uiLabelMap.HRCommonNotSetting}");
	}
	var overTimeFromDate = rowData.overTimeFromDate; 
	if(overTimeFromDate){
		$("#regStartTimeWorkOvertime").text(overTimeFromDate.getHours() + ":" + overTimeFromDate.getMinutes() + ":" + overTimeFromDate.getSeconds());
	}else{
		$("#regStartTimeWorkOvertime").text("${uiLabelMap.HRCommonNotSetting}");
	}
	var overTimeThruDate = rowData.overTimeThruDate;
	if(overTimeThruDate){
		$("#regEndTimeWorkOvertime").text(overTimeThruDate.getHours() + ":" + overTimeThruDate.getMinutes() + ":" + overTimeThruDate.getSeconds());
	}else{
		$("#regEndTimeWorkOvertime").text("${uiLabelMap.HRCommonNotSetting}");
	}
	$("#approvalStatus").val(rowData.statusId);
	openJqxWindow($("#jqxWindowWorkOvertime"));
}
function openJqxWindow(jqxWindowDiv){
	var wtmp = window;
	var tmpwidth = jqxWindowDiv.jqxWindow('width');
	jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
	jqxWindowDiv.jqxWindow('open');
} 
function clearWorkOvertimeContentWindow(){
	$("#regStartTimeWorkOvertime").text("");
	$("#regEndTimeWorkOvertime").text("");
	$("#partyIdWorkOvertime").text("");
	$("#partyNameWorkOvertime").text("");
	$("#dateWorkOvertime").text("");
	$("#startTimeWorkOvertime").val(null);
	$("#endTimeWorkOvertime").val(null);
	$("#reasonWorkOvertime").text("");
	$("#workOvertimeRegisIdPopup").val("");
}
</script> 
