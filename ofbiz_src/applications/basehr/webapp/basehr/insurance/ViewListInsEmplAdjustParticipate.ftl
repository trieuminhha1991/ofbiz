<#include "script/ViewListInsEmplAdjustParticipateScript.ftl" >
<#assign datafield = "[{name: 'partyId', type: 'string'},
					  {name: 'partyCode', type: 'string'},
					  {name: 'fullName', type: 'string'},
					  {name: 'gender', type: 'string'},
					  {name: 'birthDate', type: 'date'},
					  {name: 'insuranceSocialNbr', type: 'string'},
					  {name: 'insHealthCard', type: 'string'},
					  {name: 'healthCareName', type: 'string'},
					  {name: 'insHealthThruDate', type: 'date'},
					  {name: 'emplPositionTypeDesc', type: 'string'},
					  {name: 'groupName', type: 'string'},
					  {name: 'idNumber', type: 'string'},
					  {name: 'dateParticipateIns', type: 'date'},
					  {name: 'insuranceSalary', type: 'number'},
					  {name: 'insuranceRate', type: 'number'},
					  {name: 'statusId', type: 'string'}
					  ]"/>

<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', 
							width: '11%', editable: false},
						{text: '${uiLabelMap.EmployeeName}', datafield: 'fullName', width: '16%',
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
								   if(rowData){
									   return '<span>' + rowData.fullName + '</span>';
								   }
							   }
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', editable: false, 
					    	filterType : 'checkedlist', width: '14%',
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.statusArr.length; i++){
									if(globalVar.statusArr[i].statusId == value){
										return '<span>' + globalVar.statusArr[i].description + '</span>';
									}
								}			
							},
							createfilterwidget : function(column, columnElement, widget){
								var source = {
										localdata : globalVar.statusArr,
										datatype : 'array'
								};
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								var dataFilter = filterBoxAdapter.records;
								widget.jqxDropDownList({source : dataFilter, valueMember : 'statusId', displayMember : 'description'});
								if(dataFilter.length <= 8){
									widget.jqxDropDownList({autoDropDownHeight : true});
								}else{
									widget.jqxDropDownList({autoDropDownHeight : false});
								}
							}
						},
					   {text: '${uiLabelMap.PartyGender}', datafield: 'gender', width: '10%', filtertype: 'checkedlist',
						   columntype: 'dropdownlist',
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							   for(var i = 0; i < globalVar.genderArr.length; i++){
								   if(globalVar.genderArr[i].genderId == value){
									   return '<div style=\"margin-top: 4px; margin-left: 2px\">' + globalVar.genderArr[i].description + '</div>'; 
								   }
							   }
							   return '<div style=\"margin-top: 4px; margin-left: 2px\">' +  value + '</div>';
						   },
						   createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.genderArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'genderId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
							}
					   	},		
					   	{text: '${uiLabelMap.PartyBirthDate}', datafield: 'birthDate', width: '10%', cellsformat: 'dd/MM/yyyy', 
							   columntype: 'datetimeinput', filtertype: 'range'},
					    {text: '${StringUtil.wrapString(uiLabelMap.SocialInsuranceNbrIdentify)}', datafield: 'insuranceSocialNbr', width: '12%'},
					    {text: '${StringUtil.wrapString(uiLabelMap.HealthInsuranceNbr)}', datafield: 'insHealthCard', width: '12%'},
					    {text: '${StringUtil.wrapString(uiLabelMap.HealthCareName)}', datafield: 'healthCareName', width: '14%'},
					    {text: '${uiLabelMap.DateExpire}', datafield: 'insHealthThruDate', width: '11%', cellsformat: 'dd/MM/yyyy', 
							   columntype: 'datetimeinput', filtertype: 'range'},
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeDesc', width: '16%'},
					    {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', 
									cellsalign: 'left', editable: false, width: '16%'},
									
						{text: '${StringUtil.wrapString(uiLabelMap.TotalInsuranceSocialSalary)}', datafield: 'insuranceSalary', 
						   width: '14%', editable: false, filterType : 'number',
						   cellsrenderer: function (row, column, value) {
								if(typeof(value) == 'number'){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
							}
					    },
						
						"/>
</script>					  
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header" style="min-height: 25px">
		<h4>${uiLabelMap.EmplInsuranceList}</h4>
		<div class=" no-border" style="display: inline-block;">
			<div id='jqxMenu' class="hide pull-right" style="display: inline-block; margin-right: 5px; border-color:#CCC; 
															background-color: #FAFAFA; color: #333; border-radius: 0">
			</div>
			<!-- <button  id = "removeFilter" class="grid-action-button icon-filter open-sans pull-right">${StringUtil.wrapString(uiLabelMap.HRCommonRemoveFilter)}</button> -->
			
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class='row-fluid'>
				<div class="span12">
					<div class="span6">
						<div style="display: inline-block; margin-right: 5px" id="month" ></div>						
						<div style="display: inline-block;" id="year" ></div> 	
					</div>
					<div class="span6">
						<div id="dropDownButton" style="" class="pull-right">
							<div style="border: none;" id="jqxTree">
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>	
		<div class="row-fluid">
			<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" 
				 filterable="true"  deleterow="false" editable="false" addrow="false"
				 url="" id="jqxgrid"  mouseRightMenu="true" contextMenuId="contextMenu" 
				 removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />	
		</div>
	</div>	
</div> 

<div class="row-fluid">
	<div id="contextMenu" class="hide">
		<ul>
			<li action="updateInsuranceHealth">
				<i class="fa fa-ambulance"></i>${uiLabelMap.UpdateInsuranceHealthInfo}
	        </li>        
		</ul>
	</div>
</div>	

<div class="row-fluid">
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div class='form-window-container'>
			<div id="splitterEmplList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList"></div>
				</div>
				<div style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	               		<div class='form-window-content'>
		                   <div id="EmplListInOrg">
		                   </div>
	               		</div>
	               </div>
	        	</div>
			</div>
		</div>
	</div>
</div>
<#include "InsuranceAdjustParticipate.ftl"/>
<#include "InsuranceAdjustSuspendStop.ftl"/>
<#include "InsuranceAdjustSalaryAndJobTitle.ftl"/>
<#include "InsuranceHealthPartyUpdate.ftl"/>

<#assign includeJs = "false"/>
${setContextField("includeJs", "false")}
<#include "component://basehr/webapp/basehr/insurance/script/HosipitalListScript.ftl"/>
<#include "component://basehr/webapp/basehr/insurance/ViewListHosipitalList.ftl"/>
<script type="text/javascript" src="/hrresources/js/insurance/HosipitalListScript.js"></script>
<script type="text/javascript" src="/hrresources/js/insurance/ViewListInsEmplAdjustParticipate.js"></script>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId="" isDropDown="false" width="100%" height="100%" expandAll="false"/>
	
<script type="text/javascript">
<#if expandedList?has_content>
<#assign expandTreeId=expandedList[0]>
if(typeof(globalVar.expandTreeId) == 'undefined'){
	globalVar.expandTreeId = "${expandTreeId}";		
}
</#if>
function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	refreshBeforeReloadGrid($("#EmplListInOrg"));
	tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
</script>	
