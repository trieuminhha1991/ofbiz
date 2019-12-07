<#include "script/ViewListEmplSalesRecruitedScript.ftl"/>
<#assign datafield = "[{name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'partyGroupId', type: 'string'},
		                 {name: 'emplPositionTypeId', type: 'string'},
		                 {name: 'emplPositionTypeDesc', type: 'string'},
		                 {name: 'enumRecruitmentTypeId', type: 'string'},
		                 {name: 'gender', type: 'string'},
		                 {name: 'idNumber', type: 'string'},
		                 {name: 'birthDate', type: 'date'},
		                 {name: 'nativeLand', type: 'string'},
		                 {name: 'currResidenceAddr', type: 'string'},
		                 {name: 'primaryPhone', type: 'string'},
		                 {name: 'emailAddress', type: 'string'}]"/>
		                 
<script type="text/javascript">
<#assign columnlist = "{datafield: 'partyId', hidden: true},
					   {datafield: 'emplPositionTypeId', hidden: true},
					   {datafield: 'partyGroupId', hidden: true},
					   {text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '15%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: '17%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '18%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.RecruitmentPosition)}', datafield: 'emplPositionTypeDesc', width: '18%', editable: false},
		               {text: '${StringUtil.wrapString(uiLabelMap.RecruitmentEnumType)}', datafield: 'enumRecruitmentTypeId', width: '16%', 
						    columntype: 'dropdownlist', filtertype: 'checkedlist', editable: false,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.recruitmentTypeEnumArr.length; i++){
									if(globalVar.recruitmentTypeEnumArr[i].enumId == value){
										return '<span>' + globalVar.recruitmentTypeEnumArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.recruitmentTypeEnumArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'enumId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
		               },
		               {text: '${StringUtil.wrapString(uiLabelMap.Sexual)}', datafield: 'gender', width: '9%', 
		            	   columntype: 'dropdownlist', filtertype: 'checkedlist',
		            	   cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.genderList.length; i++){
									if(globalVar.genderList[i].genderId == value){
										return '<span>' + globalVar.genderList[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.genderList,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'genderId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
		               },
		               {text: '${StringUtil.wrapString(uiLabelMap.certProvisionId)}', datafield: 'idNumber', width: '15%'},
		               {text: '${StringUtil.wrapString(uiLabelMap.BirthDate)}', datafield: 'birthDate', width: '12%', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy'},
		               {text: '${StringUtil.wrapString(uiLabelMap.NativeLand)}', datafield: 'nativeLand', width: '15%',},
		               {text: '${StringUtil.wrapString(uiLabelMap.CurrentResidence)}', datafield: 'currResidenceAddr', width: '25%'},
		               {text: '${StringUtil.wrapString(uiLabelMap.PhoneNumber)}', datafield: 'primaryPhone', width: '14%'},
		               {text: '${StringUtil.wrapString(uiLabelMap.HRCommonEmail)}', datafield: 'emailAddress', width: '15%'},
						"/>
</script>

<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.ListEmplSalesRecruited}</h4>
		<div class="widget-toolbar none-content" >
			<button id="clearFilter" class="grid-action-button" style="font-size: 14px"><i class="icon-filter open-sans"></i><span>${uiLabelMap.accRemoveFilter}</span></button>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12">
							<div id='monthCustomTimePeriod' style='display: inline-block; margin-right: 5px'></div>
							<div id='yearCustomTimePeriod' style='display: inline-block;'></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10 '>
						<div class="span12" style="margin-right: 10px">
							<div id="dropDownButton" class="pull-right">
								<div style="border: none;" id="jqxTree">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>	
		<div class="row-fluid">
			<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="false" dataField=datafield columnlist=columnlist  
					clearfilteringbutton="true"  editable="false" deleterow="false" selectionmode="singlerow"
					addrow="false" showlist="false" sortable="true"  mouseRightMenu="true" 				
					contextMenuId="contextMenu" url="" jqGridMinimumLibEnable="false"/>
		</div>
	</div>
</div>		


<script type="text/javascript" src="/hrresources/js/recruitment/ViewListEmplSalesRecruited.js"></script>				                 