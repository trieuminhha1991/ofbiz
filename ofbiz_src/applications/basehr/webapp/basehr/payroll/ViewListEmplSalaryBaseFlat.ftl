<#include "script/ViewListEmplSalaryBaseFlatScript.ftl" />
<#assign dataFields = "[{name: 'partyIdFrom', type: 'string'},
						{name: 'partyIdTo', type: 'string'},
						{name: 'roleTypeIdFrom', type: 'string'},
						{name: 'roleTypeIdTo', type: 'string'},
						{name: 'partyCode', type: 'string'},
						{name: 'firstName', type: 'string'},
						{name: 'fullName', type: 'string'},
						{name: 'groupName', type: 'string'},
						{name: 'emplPositionTypeDesc', type: 'string'},
						{name: 'amount', type: 'number'},
						{name: 'periodTypeId', type: 'string'},
						{name: 'fromDate', type: 'date', other: 'Timestamp'},
						{name: 'thruDate', type: 'date', other: 'Timestamp'},
						]"/>
<script type="text/javascript">
<#assign columnlist ="{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '13%', editable: false,
							cellsrenderer: function(row, column, value){
								return '<a href=\"javascript:void(0)\" onclick=\"contextMenuObj.showPayHistoryDetail(' + row + ')\">' + value + '</a>';
							}
					  },
					  {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'firstName', width: '15%', cellsalign: 'left', editable: false,
				    	 cellsrenderer: function(row, column, value){
				    		 var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
				    		 if(rowData && rowData.fullName){
				    			return '<span>' + rowData.fullName + '</span>';
				    		 }
				    	 }
				      },
					  {text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeDesc', width: '18%'},
				      {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '18%'},
					  {text: '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}', datafield: 'amount', width: '13%', columntype: 'numberinput', 
						  filtertype: 'number',
						  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
								}
							}
					  },
					  {text: '${StringUtil.wrapString(uiLabelMap.PeriodTypePayroll)}', datafield: 'periodTypeId', filtertype: 'checkedlist', 
						  columntype: 'dropdownlist', width:'13%',
						  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							 for(var i = 0; i < globalVar.periodTypeArr.length; i++){
								 if(value == globalVar.periodTypeArr[i].periodTypeId){
									return '<span>' + globalVar.periodTypeArr[i].description + '</span>';
								 }
							 }
							 return '<span>' + value + '</span>';
						 },
						 createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.periodTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'periodTypeId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
						 },
					  },
					  {text: '${StringUtil.wrapString(uiLabelMap.EffectiveFromDate)}', datafield: 'fromDate', width: '13%', cellsformat: 'dd/MM/yyyy',  
						 	filtertype : 'range', columntype: 'datetimeinput', editable: false,
						 	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						 		var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						 		if(rowData){
							 		var periodTypeId = rowData.periodTypeId;
							 		if(periodTypeId == 'MONTHLY'){
							 			return '<span>' + getMonth(value) + '/' + value.getFullYear() + '</span>';
							 		}else if(periodTypeId == 'DAILY'){
							 			return '<span>' + getDate(value) + '/' + getMonth(value) + '/' + value.getFullYear() + '</span>';
							 		}else if(periodTypeId == 'YEARLY'){
							 			return '<span>' + value.getFullYear() + '</span>';
							 		}
						 		}
						 	}
					  },
					  {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', width: '13%',
						  filtertype : 'range', columntype: 'datetimeinput', editable: false,
						  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						 		var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						 		if(rowData && value){
							 		var periodTypeId = rowData.periodTypeId;
							 		if(periodTypeId == 'MONTHLY'){
							 			return '<span>' + getMonth(value) + '/' + value.getFullYear() + '</span>';
							 		}else if(periodTypeId == 'DAILY'){
							 			return '<span>' + getDate(value) + '/' + getMonth(value) + '/' + value.getFullYear() + '</span>';
							 		}else if(periodTypeId == 'YEARLY'){
							 			return '<span>' + value.getFullYear() + '</span>';
							 		}
						 		}
						 	}
					  },
					 "/>
							
</script>	
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>	
	<#assign addrow="true"/>
<#else>	
	<#assign addrow="false"/>
</#if>
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_UPDATE", session)>
	<#assign customcontrol1="icon-edit open-sans@${uiLabelMap.BatchUpdates}@javascript: void(0)@updateEmplSalaryObject.openWindow()"/>
<#else>
	<#assign customcontrol1=""/>
</#if>
<div class="row-fluid">
	<#assign customcontrolAdvance = "<div id='dateTimeInput'></div>"/>
	<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" 
		 filterable="true" alternativeAddPopup="CreateSalaryBaseFlatWindow" deleterow="false" editable="false" addrow=addrow
		 url="" id="jqxgrid" customControlAdvance=customcontrolAdvance 
		 mouseRightMenu="true" contextMenuId="contextMenu"
		 customcontrol1=customcontrol1
		 removeUrl="" deleteColumn="" updateUrl="" editColumns="" selectionmode="singlerow" jqGridMinimumLibEnable="false" />	
</div>

<div id="contextMenu" class="hide">
	<ul>
		<li action="viewDetail">
			<i class="fa fa-search-plus"></i>${uiLabelMap.CommonDetail}
        </li>
	</ul>
</div>

<div id="viewEmplSalWindow" class="hide">
	<div>${uiLabelMap.EmployeeSalaryDetails}</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${StringUtil.wrapString(uiLabelMap.EmployeeId)}</label>
						</div>
						<div class="span8">
							<input type="text" id="partyCodeView">
							<!-- <button id="searchPartyCodeBtn" title="${uiLabelMap.CommonSearch}" class="btn btn-mini">
								<i class="icon-only icon-search open-sans" style="font-size: 15px; position: relative; top: -2px;"></i></button> -->
						</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${StringUtil.wrapString(uiLabelMap.EmployeeName)}</label>
						</div>
						<div class="span8">
							<input type="text" id="fullNameView">
						</div>
					</div>
				</div>
			</div>
		</div>	
		<div class="row-fluid">
			<div id="containerviewEmplSalGrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
			</div>
			<div id="jqxNotificationviewEmplSalGrid">
			    <div id="notificationContentviewEmplSalGrid">
			    </div>
			</div>
			<div id="viewEmplSalGrid"></div>
		</div>
	</div>	
</div>

<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>	
	 <#include "CreateEmplSalaryBaseFlat.ftl" /> 
</#if>
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_UPDATE", session)>
	<#include "UpdateEmplSalaryBaseFlat.ftl"/>	
</#if>
<script type="text/javascript" src="/hrresources/js/payroll/ViewListEmplSalaryBaseFlat.js"></script>