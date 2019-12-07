<div id="daily_tab" class="tab-pane active">
	<#assign datafield = "[{name: 'partyId', type: 'string'},
							{name: 'partyCode', type: 'string'},
							{name: 'partyName', type: 'string'},
							{name: 'department', type: 'string'},
							{name: 'totalKpiFill', type: 'number'},
							{name: 'totalKpiConfirm', type: 'number'},
							{name: 'totalKpiNotConfirm', type: 'number'}
	]"/>
	
	<#assign columnlist = "	{datafield: 'partyId', hidden: true},
							{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '13%'},
							{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', width: '19%'},
							{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'department', width: '23%'},
							{text: '${StringUtil.wrapString(uiLabelMap.TotalKPIFill)}', datafield: 'totalKpiFill', width: '15%', columntype: 'numberinput', filterType : 'number'},
							{text: '${StringUtil.wrapString(uiLabelMap.TotalKPIConfirm)}', datafield: 'totalKpiConfirm', width: '15%', columntype: 'numberinput', filterType : 'number'},
							{text: '${StringUtil.wrapString(uiLabelMap.TotalKPINotConfirm)}', datafield: 'totalKpiNotConfirm', width: '15%', columntype: 'numberinput', filterType : 'number'},
	"/>
	<div class="widget-box transparent no-bottom-border">
		<div class="widget-body">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<h4 style="line-hight:36px;padding:0px;margin:0px;float:left;text-align:left;color:#4383b4;margin-top:4px;">${uiLabelMap.ListEmplKPIStatus}</h4>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10 '>
							<div class="span12">
								<div class="span6">
									<div id="dropDownButton" class="pull-right">
										<div style="border: none;" id="jqxTree"></div>
									</div>
								</div>
								<div class="span6">
									<div id="dateTimeInput"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<@jqGrid filtersimplemode="false" filterable="true" addrefresh="true" showtoolbar="false" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="false" addType="popup" editable="false" deleterow="false" 
				alternativeAddPopup="alterpopupWindow" addrow="false" showlist="false" sortable="false"
				url="" jqGridMinimumLibEnable="false" />
			</div>
		</div>
	</div>
</div>