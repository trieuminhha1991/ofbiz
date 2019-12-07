<#include "script/profileViewEmplAppLeaveScript.ftl"/>
<#assign datafield = "[
	{name: 'emplLeaveId', type: 'string'},
	{name: 'partyId', type: 'string'},
	{name: 'statusId', type: 'string'},
	{name: 'fromDate', type: 'date', other: 'Timestamp'},
	{name: 'thruDate', type: 'date', other : 'Timestamp'},
	{name: 'dateApplication', type: 'date', other : 'Timestamp'},	
	{name: 'nbrDayLeave', type: 'number'},	
	{name: 'emplLeaveReasonTypeId', type: 'string'},
	{name: 'commentApproval', type: 'string'},
]"/>
<script type="text/javascript">
	<#assign columnlist = "{datafield: 'partyId', hidden: true},
							{datafield: 'emplLeaveId', hidden: true},
							{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', editable: false, width: '14%',
								filtertype: 'checkedlist', columntype: 'dropdownlist', 
								cellsrenderer: function(row, column, value){
									for(var i = 0; i < globalVar.statusArr.length; i++){
										if(globalVar.statusArr[i].statusId == value){
											return '<span title=' + value + '>' + globalVar.statusArr[i].description + '</span>';		
										}
									}
									return '<span>' + value + '</span>';
								},
								createfilterwidget : function(column , columnElement , widget){
									var source = {
											localdata : globalVar.statusArr,
											datatype : 'array',
									};
									
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
									var dataSourceList = filterBoxAdapter.records;
									//dataSourceList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
									widget.jqxDropDownList({source: dataSourceList, displayMember: 'description', valueMember : 'statusId'});
								}
							},
							{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy HH:mm:ss', width: '16%',filterType : 'range'},
							{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy HH:mm:ss', width: '16%', filterType : 'range'},
							{text: '${StringUtil.wrapString(uiLabelMap.HRNumberDayLeave)}', datafield: 'nbrDayLeave', columntype: 'numberinput', width: '10%', filterType : 'number'},
							{text: '${StringUtil.wrapString(uiLabelMap.ApplicationDate)}', datafield: 'dateApplication', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: '12%', filterType : 'range'},							
							{text: '${StringUtil.wrapString(uiLabelMap.CommonReason)}', filterable : true, datafield: 'emplLeaveReasonTypeId', width: '18%',
								filtertype: 'checkedlist', columntype: 'dropdownlist', 
								cellsrenderer: function(row, column, value){
									for(var i = 0; i < globalVar.emplLeaveReasonArr.length; i++){
										if(globalVar.emplLeaveReasonArr[i].emplLeaveReasonTypeId == value){
											return '<span title=' + value + '>' + globalVar.emplLeaveReasonArr[i].description + '</span>';		
										}
									}
									return '<span>' + value + '</span>';
								},
								createfilterwidget : function(column , columnElement , widget){
									var source = {
											localdata : globalVar.emplLeaveReasonArr,
											datatype : 'array',
									};
									
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
									var dataSourceList = filterBoxAdapter.records;
									//dataSourceList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
									widget.jqxDropDownList({source: dataSourceList, displayMember: 'description', valueMember : 'emplLeaveReasonTypeId'});
									if(dataSourceList.length <= 8){
										widget.jqxDropDownList({autoDropDownHeight : true});
									}else{
										widget.jqxDropDownList({autoDropDownHeight : false});
									}
								}
							},
							{text: '${StringUtil.wrapString(uiLabelMap.NoteOfApprove)}', datafield: 'commentApproval', width: '20%',
								 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
									 return defaulthtml;
								 }
							},
	"/>
</script>
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>	
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.ApplicationLeave}</h4>
		<div class="widget-toolbar none-content" style="width: 500px; padding: 0; margin: 0; line-height: 30px">
			<div class="row-fluid">
				<div class="span2" style="float: right; margin-right: 10px; margin-top: 5px; padding: 0">
					<div id="year"></div>
				</div>
				<div class="span6" style="float: right; margin: 0; padding: 0; text-align: right;">
					<button id="addNew" class="grid-action-button icon-plus open-sans">${uiLabelMap.CommonAddNew}</button>
					<button id="cancelAppl" class="grid-action-button icon-trash open-sans">${uiLabelMap.HRCancelLeaveApplication}</button>
				</div>
			</div>
			
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid" style="margin-bottom: 15px; position: relative;">
			<div class="span12 boder-all-profile">
				<span class="text-header">${uiLabelMap.EmplLeaveInformation}</span>
				<div class="form-window-content">
					<div class="span6">
						<div class="row-fluid no-left-margin">
							<div class="span5">
								<label>${uiLabelMap.NumberDayLeaveInYear}</label>
							</div>
							<div class="span6">
								<label id="annualLeaveDayYear"></label>
							</div>
						</div>
						<div class="row-fluid no-left-margin">
							<div class="span5">
								<label>${uiLabelMap.DayCarriedOverLeave}</label>
							</div>
							<div class="span6">
								<label id="annualLastYearTransferred"></label>
							</div>
						</div>
						<div class="row-fluid no-left-margin">
							<div class="span5">
								<label>${uiLabelMap.NumberGrantedLeaveInYear}</label>
							</div>
							<div class="span6">
								<label id="annualGrantedLeaveInYear"></label>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid no-left-margin">
							<div class="span5">
								<label>${uiLabelMap.TotalDaysLeaveUsed}</label>
							</div>
							<div class="span6">
								<label id="annualLeft"></label>
							</div>
						</div>					
						<div class="row-fluid no-left-margin">
							<div class="span5">
								<label>${uiLabelMap.LeaveBalance}</label>
							</div>
							<div class="span6">
								<label id="annualLeaveRemain"></label>
							</div>
						</div>					
						<div class="row-fluid no-left-margin">
							<div class="span5">
								<label>${uiLabelMap.UnpaidDays}</label>
							</div>
							<div class="span6">
								<label id="unpaidLeave"></label>
							</div>
						</div>					
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinner-ajax"></div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid addrow="true" addType="popup" isShowTitleProperty="false" id="jqxgrid"  showtoolbar="false" clearfilteringbutton="true"
				 deleterow="true" addrefresh="true" alternativeAddPopup="alterpopupWindow" editable="false" dataField=datafield columnlist=columnlist
				 url="" filterable="true" showlist="false" autorowheight="true"
				 createUrl="" width="100%" bindresize="false"
				 addColumns="" sortable="true"
				 removeUrl="" deleteColumn=""
				 updateUrl="" jqGridMinimumLibEnable="false"
				 editColumns=""
			/>
		</div>
	</div>
</div>
<#include "profileCreateEmplApplLeave.ftl"/>
<script type="text/javascript" src="/hrresources/js/profile/profileViewEmplApplicationLeave.js"></script>
