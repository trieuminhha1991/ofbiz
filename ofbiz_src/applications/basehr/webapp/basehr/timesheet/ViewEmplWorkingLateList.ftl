<#include "script/ViewEmplWorkingLateListScript.ftl"/>
<#assign datafield = "[{name: 'partyId', type: 'string'},
	                  {name: 'partyName', type: 'string'},
	                  {name: 'emplWorkingLateId', type: 'string'},
	                  {name: 'dateWorkingLate', type: 'date'},
	                  {name: 'arrivalTime', type: 'date'},
	                  {name: 'delayTime', type: 'number'},
	                  {name: 'reason', type: 'string'},
	                  {name: 'statusId', type:'string'}]"/>
<script type="text/javascript">

<#assign groups = "dateWorkingLate"/>
<#assign columnlist = "{text: '${uiLabelMap.CommonDate}', datafield: 'dateWorkingLate', width: '12%',cellsformat: 'dd/MM/yyyy', editable: false, filtertype:'range', columntype: 'template'},
						{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId', width: '12%',editable: false, hidden: false},
						{text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', width: '12%',editable: false, hidden: false},
						{text: '${uiLabelMap.ArrivalTime}', datafield: 'arrivalTime', width: '12%', editable: false, cellsformat: 'HH:mm:ss', columntype: 'datetimeinput', filterable : false},
						{text: '${uiLabelMap.HRDelayTime}', datafield: 'delayTime', editable: true, width: '13%', cellsalign: 'right', columntype: 'numberinput', filterType : 'number',
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
						{text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', editable: true, width: '15%', columntype: 'dropdownlist',filterType : 'checkedlist',
							cellsrenderer: function(row, column, value){
								for(var i = 0; i < statusWorkingLateArr.length; i++){
									if(statusWorkingLateArr[i].statusId == value){
										return '<div style=\"margin-top: 4px; margin-left: 2px\">' + statusWorkingLateArr[i].description + '</div>';		
									}
								}
							},
							createfilterwidget : function(column, columnElement, widget){
								var source = {
										localdata : statusWorkingLateArr,
										datatype : 'array'
								};
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								var dataFilter = filterBoxAdapter.records;
								//dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source : dataFilter, valueMember : 'statusId', displayMember : 'description'});
								if(dataFilter.length <= 8){
									widget.jqxDropDownList({autoDropDownHeight : true});
								}else{
									widget.jqxDropDownList({autoDropDownHeight : false});
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
					<div style="margin-left: 0px;">
						<div id="jqxDateTime" style=""></div>
					</div>
				</div>
				<div class="span5" style="text-align: right;" id="toolbarjqxgridApproval">
					<button id="removeFilter" class="grid-action-button icon-filter open-sans">${uiLabelMap.HRCommonRemoveFilter}</button> 
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
<div class="row-fluid" >
	<div id="jqxWindowAppr" class="hide">
		<div>${uiLabelMap.HREmplWorkingLateApproval}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<input type="hidden" name="emplWorkingLateId" id="emplWorkingLateIdAppr">
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.EmployeeId}</label>
					</div>
					<div class="span7">
						<span id="partyIdWorkingLateAppr"></span>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.EmployeeName}</label>
					</div>
					<div class="span7">
						<span id="partyNameWorkingLateAppr"></span>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.CommonDate}</label>
					</div>
					<div class="span7">
						<span id="dateWorkingLateAppr"></span>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.ArrivalTime}</label>
					</div>
					<div class="span7">
						<span id="arrivalTimeWorkingLateAppr"></span>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.HRDelayTime}</label>
					</div>
					<div class="span7">
						<div id="delayTimeWorkingLateAppr"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.HRCommonReason}</label>
					</div>
					<div class="span7">
						<input id="reasonWorkingLateAppr" type="text"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.CommonStatus}</label>
					</div>
					<div class="span7">
						<div id="statusWorkingLateAppr"></div>
					</div>
				</div>
			</div>
			<div class='form-action'>
				<button type="button" class="btn btn-danger form-action-button pull-right icon-remove" id="btnCancel">${uiLabelMap.CommonCancel}</button>
				<button type="button" class="btn btn-primary form-action-button pull-right icon-ok" id="btnSave">${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div id="jqxWindowAddrow" class="hide">
		<div>${uiLabelMap.HREmplCreateWorkingLate}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<form id="addNewForm">
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=" asterisk">${uiLabelMap.EmployeeName}</label>
						</div>
						<div class="span7">
							<div id="addEmployeeIdButton">
								<div id="addEmployeeId"></div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=" asterisk">${uiLabelMap.DateWorkingLate}</label>
						</div>
						<div class="span7">
							<div id="addDateWorkingLate"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.TotalWorkingLateMinutes}</label>
						</div>
						<div class="span7">
							<div id="addWorkingLateMinutes"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.HRReasonLate}</label>
						</div>
						<div class="span7">
							<input id="addWorkingLateReason" type="text"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=" asterisk">${uiLabelMap.CommonStatus}</label>
						</div>
						<div class="span7">
							<div id="addWorkingLateStatus"></div>
						</div>
					</div>
				</form>
			</div>
			<div class='form-action'>
				<button class="btn btn-danger form-action-button pull-right icon-remove" type="button" id="btnCancelAddNew">${uiLabelMap.CommonCancel}</button>
				<button class="btn btn-primary form-action-button pull-right icon-ok" type="button" id="btnSaveAddNew">${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/timesheet/ViewEmplWorkingLateList.js"></script>