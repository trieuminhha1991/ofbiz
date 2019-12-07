<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<!-- <script type="text/javascript" src="/jqwidgets/jqwidgets/jqxcheckbox.js"></script> -->
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<!-- <script type="text/javascript" src="/jqwidgets/jqwidgets/jqxnotification.js"></script> -->
<script type="text/javascript">
	var workingShiftArr = new Array();
	<#if workingShiftList?exists>
		<#list workingShiftList as ws>
			var row = {};
			row["workingShiftId"] = "${ws.workingShiftId}";
			row["description"] = "${StringUtil.wrapString(ws.description)}";
			workingShiftArr[${ws_index}] = row;	
		</#list>
	</#if>
	var wsSource = {
	        localdata: workingShiftArr,
	        datatype: "array"
	};
    var filterBoxAdapter = new $.jqx.dataAdapter(wsSource, {autoBind: true});
    var dataSoureList = filterBoxAdapter.records;
	
	var leaveReasonTypeArr = new Array();
	<#if leaveReasonTypes?exists>
		<#list leaveReasonTypes as reasonType>
			var row = {};
			row["emplLeaveReasonTypeId"] = "${reasonType.emplLeaveReasonTypeId}";
			row["description"] = "${StringUtil.wrapString(reasonType.description)}";
			leaveReasonTypeArr[${reasonType_index}] = row;
		</#list>
	</#if>
	
	var sourceLeaveReasonType =
    {
        localdata: leaveReasonTypeArr,
        datatype: "array"
    };
	
    var dataAdapterLeaveReasonType = new $.jqx.dataAdapter(sourceLeaveReasonType);
	$(document).ready(function () {
		$("#workingShiftList").jqxComboBox({source: dataSoureList,  displayMember: "description", valueMember: "workingShiftId",
    		height: 25, width: 250, theme: 'olbius',multiSelect: true, placeHolder:'${StringUtil.wrapString(uiLabelMap.ChooseWorkingShifLeave)}',
    		renderer: function (index, label, value) {
				for(i=0; i < workingShiftArr.length; i++){
					if(workingShiftArr[i].workingShiftId == value){
						return workingShiftArr[i].description;
					}
				}
			    return value;
			}
        });
		<#if (workingShiftList?size < 8)>
			$("#workingShiftList").jqxComboBox({autoDropDownHeight: true});	
		</#if>
		
		$("#emplLeaveReasonTypeId").jqxDropDownList({  source: dataAdapterLeaveReasonType, displayMember: "description", valueMember: "emplLeaveReasonTypeId",
    		itemHeight: 25, height: 25, width: 250, dropDownHeight: 180, theme: 'olbius', placeHolder: "${StringUtil.wrapString(uiLabelMap.ChooseReasonLeave)}",
    		renderer: function (index, label, value) {
				for(i=0; i < leaveReasonTypeArr.length; i++){
					if(leaveReasonTypeArr[i].emplLeaveReasonTypeId == value){
						return leaveReasonTypeArr[i].description;
					}
				}
			    return value;
			}
        });
		<#if (leaveReasonTypes?size < 8)>
			$("#emplLeaveReasonTypeId").jqxDropDownList({autoDropDownHeight: true});	
		</#if>
		
		$("#fromDate").jqxDateTimeInput({ width: 250, height: 25, theme: 'olbius'});
		$("#thruDate").jqxDateTimeInput({ width: 250, height: 25, theme: 'olbius'});
		$("#thruDate").val(null);
		$('#leaveUnpaid').jqxCheckBox({ width: '250px', height: '25px', checked: false, theme:'olbius'});
		$("#submitLeave").click(function(){
			 var valid = $('#leaveForm').jqxValidator('validate');
			 if(!valid){
				 return false;
			 }
			 $("#submitLeave").attr('disabled','disabled');
			 var partyId = $("#partyId").val();
			 var fromDate = $('#fromDate').jqxDateTimeInput('value').getTime();
			 var thruDate = $('#thruDate').jqxDateTimeInput('value').getTime();
			 var emplLeaveReasonTypeId = $("#emplLeaveReasonTypeId").val();
			 var selectedItems = $("#workingShiftList").jqxComboBox('getSelectedItems');
			 var workingShiftChoose = new Array();
			 for(var i = 0; i < selectedItems.length; i++){
				 workingShiftChoose.push({workingShiftId: selectedItems[i].value});
			 }
			 var checked = $("#leaveUnpaid").jqxCheckBox('checked');
			 var isLeaveUnpaid = "N";
			 if(checked){
				 isLeaveUnpaid = "Y";
			 }
			 $.ajax({
				url: "createEmplLeaveExt",
				type: "POST",
				data:{partyId: partyId, fromDate: fromDate, thruDate: thruDate, emplLeaveReasonTypeId: emplLeaveReasonTypeId, 
					leaveUnpaid: isLeaveUnpaid, workingShift: JSON.stringify(workingShiftChoose)},
				success: function(data){
					if(data._EVENT_MESSAGE_){
						$("#messageNotification").empty();
						$("#messageNotification").jqxNotification({ width: "100%", appendContainer: "#container",
								opacity: 0.9, autoClose: false, template: "info", autoOpen: false, theme: 'olbius' });
						$("#messageNotification").html(data._EVENT_MESSAGE_);
						$("#messageNotification").jqxNotification("open");
					}else{
						$("#messageNotification").empty();
						$("#messageNotification").jqxNotification({ width: "100%", appendContainer: "#container",
							opacity: 0.9, autoClose: false, template: "error", autoOpen: false,theme: 'olbius' });
						$("#messageNotification").html(data._ERROR_MESSAGE_);
						$("#messageNotification").jqxNotification("open");
					}
				},
				complete: function(jqXHR, textStatus){
					$("#submitLeave").removeAttr('disabled');
				}
			 });
		});
		$("#leaveForm").jqxValidator({
			rules:[
			       { input: '#workingShiftList', message: '${StringUtil.wrapString(uiLabelMap.CommonRequired)}', rule: function (input, commit){
			    	   		var selectedItems = input.jqxComboBox('getSelectedItems');
			    	   		if(selectedItems.length > 0){
			    	   			return true;
			    	   		}
			    	   		return false;
			       		} 
			       },
			       { input: '#emplLeaveReasonTypeId', message: '${StringUtil.wrapString(uiLabelMap.CommonRequired)}', rule: function (input, commit){
		    	   		if(!input.val()){
		    	   			return false;
		    	   		}
		    	   		return true;
		       			} 
			       },
			       { input: '#fromDate', message: '${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}', rule: function (input, commit){
			    	   		if(!input.val()){
			    	   			return false;
			    	   		}
			    	   		var date = $('#fromDate').jqxDateTimeInput('value');
			    	   		var nowDate = new Date();
			    	   		nowDate.setHours(0,0,0,0);
			    	   		if(date.getTime() < nowDate.getTime()){
			    	   			return false;
			    	   		}
			    	   		return true;
			       		} 
			       },
			       { input: '#thruDate', message: '${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}', rule: function (input, commit){
		    	   		if(!input.val()){
		    	   			return false;
		    	   		}
		    	   		var date = $('#thruDate').jqxDateTimeInput('value');
		    	   		var fromDate = $('#fromDate').jqxDateTimeInput('value');
		    	   		if(fromDate && (date.getTime() < fromDate.getTime())){
		    	   			return false;
		    	   		}
		    	   		return true;
		       		} 
		       },
	        ]
		});
	});
	
</script>
<div class="row-fluid" id="container">
	<div id="messageNotification"></div>
</div>
<div class="row-fluid">
	<div class="span12">
		<form method="post" id="leaveForm" class="basic-form form-horizontal">
			<input type="hidden" name="partyId" value="${partyId}" id="partyId">
			<div class="no-widget-header no-left-margin span6">
				<div class="control-group no-left-margin ">
					<label class="control-label">
						${uiLabelMap.HRolbiusEmployeeID}
					</label>
					<div class="controls">
						${partyId}
					</div>
				</div>
				
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.EmployeeCurrentDept}
					</label>
					<div class="controls">
						${currDept}
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.DayLeaveRegulation}
					</label>
					<div class="controls">
						${emplLeaveDayInfo.dayLeaveRegulation}
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.ActualDayLeaveInMonth}</label>
					<div class="controls">
						<#if emplLeaveDayInfo.actualLeavePaidInMonth?exists>
							${emplLeaveDayInfo.actualLeavePaidInMonth}
						<#else>
							${uiLabelMap.HRCommonNotSetting}	
						</#if>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.NbrDayLeaveInMonth}</label>
					<div class="controls">
						<#assign nbrDayLeaveInMonth = emplLeaveDayInfo.nbrDayLeavePaidInMonth + emplLeaveDayInfo.nbrDayLeavePaidInMonth> 
					    ${nbrDayLeaveInMonth}&nbsp;(${uiLabelMap.numberDayLeaveUnpaid}: ${emplLeaveDayInfo.nbrDayLeaveUnPaidInMonth})
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.WorkingShiftLeave}
					</label>
					<div class="controls">
						<div id="workingShiftList"></div>
					</div>
				</div>
				
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.emplLeaveReasonTypeId}
					</label>
					<div class="controls">
						<div id="emplLeaveReasonTypeId"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.CommonFromDate}
					</label>
					<div class="controls">
						<div id="fromDate"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.CommonThruDate}
					</label>
					<div class="controls">
						<div id="thruDate"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.LeaveUnpaid}
					</label>
					<div class="controls">
						<div id="leaveUnpaid" style="padding-left: 5px; padding-top: 2px"></div>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<button class="btn btn-small btn-primary icon-ok" id="submitLeave" type="button">
							${uiLabelMap.CommonSubmit}
						</button>
					</div>
				</div>
				
			</div>
			<div class="no-widget-header no-left-margin span6">
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.EmployeeName}
					</label>
					<div class="controls">
						<#assign employee = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId), false)>
						${employee.firstName?if_exists} ${employee.middleName?if_exists} ${employee.lastName?if_exists}
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.HREmployeePosition}
					</label>
					<div class="controls">
						${currPositionsStr}
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.DateApplication}
					</label>
					<div class="controls">
						${dateApplication?string["dd/MM/yyyy"]}
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.ActualDayLeaveInYear}</label>
					<div class="controls">
						<#if emplLeaveDayInfo.actualLeavePaidInYear?exists>
							${emplLeaveDayInfo.actualLeavePaidInYear}
						<#else>
							${uiLabelMap.HRCommonNotSetting}	
						</#if>
					</div>
				</div>
				<div class="control-group no-left-margin ">
					<label class="control-label">
						${uiLabelMap.NbrDayLeaveInYear}
					</label>
					<div class="controls">
						<#assign nbrDayLeaveInYear = emplLeaveDayInfo.nbrDayLeavePaidInYear + emplLeaveDayInfo.nbrDayLeaveUnPaidInYear> 
						${nbrDayLeaveInYear}&nbsp;(${uiLabelMap.numberDayLeaveUnpaid}: ${emplLeaveDayInfo.nbrDayLeaveUnPaidInYear})
					</div>
				</div>
			</div>
		</form>
	</div>
</div>