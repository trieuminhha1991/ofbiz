<#include "script/ViewPartyInsuranceSalListScript.ftl"/>
<#assign datafield = "[{name: 'partyId', type: 'string'},
						{name: 'partyCode', type: 'string'},
					   {name: 'partyInsSalId', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'emplPositionType', type: 'string'},
					   {name: 'department', type: 'string'},
					   {name: 'amount', type: 'number'},
					   {name: 'periodTypeId', type: 'string'},
					   {name: 'fromDate', type: 'date', other : 'Timestamp'},
					   {name: 'thruDate', type: 'date', other : 'Timestamp'},
					   {name: 'uomId', type: 'string'},
					  ]" />
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.HRSequenceNbr)}', datafield: '', editable: false,	
							groupable: false,  columntype: 'number', width: 50, filterable: false,
							cellsrenderer: function (row, column, value) {
						    	return '<span>' + (value + 1) + '</span>';
						    }
						},
						{datafield: 'partyId', hidden: true},
						{datafield: 'uomId', hidden: true},
						{datafield: 'partyInsSalId', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '12%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: '12%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionType', width: '20%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'department', width: '20%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}', datafield: 'amount', width: '15%', cellsalign: 'right', 
							columntype : 'numberinput', filterType : 'number',
							cellsrenderer: function (row, column, value) {
								if(value){
									var data = $('#jqxgrid').jqxGrid('getrowdata', row);
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value, data.uomId) + \"</div>\";
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonPeriodType)}', datafield: 'periodTypeId', width: '18%',filterType : 'checkedlist',
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < periodTypeArr.length; i++){
									if(periodTypeArr[i].periodTypeId == value){
										return '<span>' + periodTypeArr[i].description + '<span>';
									}
								}
								return '<span>' + value + '<span>';
							},
							createfilterwidget : function(column, columnfield, widget){
							   var source = {
									   localdata : periodTypeArr,
									   datatype : 'array'
							   };
							   var filterBoxAdapter = new $.jqx.dataAdapter(source , {autoBind : true});
							   dataSoureList = filterBoxAdapter.records;
							   //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							   widget.jqxDropDownList({source: dataSoureList, displayMember: 'description', valueMember : 'periodTypeId', autoDropDownHeight : true});
						   },
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', width: '15%', cellsformat: 'dd/MM/yyyy', filterType : 'range',
							columntype: 'datetimeinput'
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', width: '15%', cellsformat: 'dd/MM/yyyy',  filterType : 'range',
							columntype: 'datetimeinput'
						}
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
		<h4>${uiLabelMap.PartyInsuranceSalTitle}</h4>
		<div class="widget-toolbar none-content">
			<!-- <button id="configEmplBaseSalary" class="grid-action-button icon-cog open-sans" title="${uiLabelMap.SettingInsuranceSalaryByPosConfig}">${uiLabelMap.ConfigByEmplPositionType}</button>
			<button id="addNew" class="grid-action-button icon-plus open-sans">${uiLabelMap.CommonAddNew}</button> -->
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div id="dateTimeInput"></div>						
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12" style="margin-right: 15px">
							<div id="dropDownButton" style="margin-top: 5px;" class="pull-right">
								<div style="border: none;" id="jqxTree">
										
								</div>
							</div>
							<button id="removeFilter" class="grid-action-button icon-filter open-sans pull-right">${StringUtil.wrapString(uiLabelMap.HRCommonRemoveFilter)}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist showtoolbar="false" 
				 filterable="true" alternativeAddPopup="popupAddRow" deleterow="false" editable="false" addrow="true" addType="popup"
				 url="" id="jqxgrid" jqGridMinimumLibEnable="false"
				 removeUrl="" deleteColumn="" showlist="false"
				 updateUrl="" 
				 editColumns="" 
				 selectionmode="singlerow" 
			/>				  
		</div>	
	</div>
</div>
<div class="row-fluid">
	<div id="partyInsuranceSalaryWindow" class="hide">
		<div>${uiLabelMap.CommonAddNew}</div>
		<div class="form-window-container">
			<div class='form-window-content'>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="asterisk">${uiLabelMap.CommonEmployee}</label>
					</div>
					<div class="span7">
						<input type="text" id="partyIdNew">
						<img alt="search" id="searchBtn" width="16" height="16" src="/aceadmin/assets/images/search_lg.png" 
											style="border: #d5d5d5 1px solid;
											   padding: 4px;
											   border-bottom-right-radius: 3px;
											   border-top-right-radius: 3px;
											   margin-left: -3px;
											   background-color: #f0f0f0;
											   border-left: 0px;
											   cursor: pointer;"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class=" asterisk">${uiLabelMap.HRCommonAmount}</label>
					</div>
					<div class="span7">
						<div id="insuranceSalaryAmount"></div>
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class=" asterisk">${uiLabelMap.HRStartFrom}</label>
					</div>
					<div class="span7">
						<div id="fromDate"></div>
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="">${uiLabelMap.CommonThruDate}</label>
					</div>
					<div class="span7">
						<div id="thruDate"></div>
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
	   					<label class="asterisk">${uiLabelMap.CommonPeriodType}</label>
   					</div>
   					<div class="span7">
   						<div id="periodTypeId"></div>
   					</div>
   				</div>
			</div>
			<div class="form-action">
				<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div id="settingSalaryByPositionWindow" class="hide">
		<div>${uiLabelMap.SettingInsSalaryByPositionType}</div>
		<div class="form-window-container">
			<div class='form-window-content'>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="">${uiLabelMap.HRStartFrom}</label>
					</div>
					<div class="span7">
						<div id="configFromDate"></div>
					</div>
				</div>								
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="">${uiLabelMap.CommonThruDate}</label>
					</div>
					<div class="span7">
						<div id="configThruDate"></div>
					</div>
				</div>								
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="">${StringUtil.wrapString(uiLabelMap.PayrollParamForMorePositionFulfillment)}</label>
					</div>
					<div class="span7">
						<div id="configSettingDropdown"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button id="cancelConfig" type="button" class="btn btn-danger form-action-button pull-right icon-remove">${uiLabelMap.CommonCancel}</button>
				<button id="saveConfig" type="button" class="btn btn-primary form-action-button pull-right icon-ok">${uiLabelMap.CommonSubmit}</button>
	     	</div>
		</div>
	</div>
</div>		
<div class="row-fluid">
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div>
			<div id="splitterEmplList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList"></div>
				</div>
				<div id="ContentPanel" style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	                   <div id="EmplListInOrg">
	                   </div>
	               </div>
	        	</div>
			</div>
		</div>
	</div>
</div>			  
<script type="text/javascript" src="/hrresources/js/insurance/ViewPartyInsuranceSalList.js"></script>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>					  