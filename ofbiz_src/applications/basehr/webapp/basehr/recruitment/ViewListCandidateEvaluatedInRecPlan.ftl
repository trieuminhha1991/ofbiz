<#include "script/ViewListCandidateEvaluatedInRecPlanScript.ftl"/>

<#assign datafield = "[{name: 'recruitmentPlanId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'recruitmentPlanName', type: 'string'},
					   {name: 'emplPositionTypeId', type: 'string'},
					   {name: 'groupName', type: 'string'},
					   {name: 'month', type: 'number'},
					   {name: 'quantity', type: 'number'},
					   {name: 'jobTitle', type: 'string'},
					   {name: 'roleDescription', type: 'string'}
						]"/>
						
<script type="text/javascript">
<#assign columnlist = "{datafield: 'recruitmentPlanId', hidden: true},
						{datafield: 'partyId', hidden: true},
						{datafield: 'customTimePeriodId', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield: 'recruitmentPlanName', 
							width: '20%', editable: false, filtertype: 'input'},
						{text: '${StringUtil.wrapString(uiLabelMap.RecruitingPosition)}', datafield: 'emplPositionTypeId', editable: false, width: '20%',
							columntype: 'dropdownlist', filtertype: 'checkedlist',
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.emplPositionTypeArr.length; i++){
									if(value == globalVar.emplPositionTypeArr[i].emplPositionTypeId){
										return '<span>' + globalVar.emplPositionTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.emplPositionTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'emplPositionTypeId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
						},	
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', editable: false, width: '15%', filterable: true},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonQty)}', datafield: 'quantity', editable: false, width: '10%', 
							columntype: 'numberinput', filtertype: 'number', cellsalign: 'right'},
						{text: '${StringUtil.wrapString(uiLabelMap.TimeRecruitmentPlan)}', datafield: 'month', editable: false, width: '14%', filterable: false,
							cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							if(data){
								return '<span>${StringUtil.wrapString(uiLabelMap.CommonMonth)} ' + (value + 1) +'</span>';
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.JobTitleInPlanBoard)}', datafield: 'jobTitle',
							width: '20%', editable: false, filtertype: 'input'},
						{text: '${StringUtil.wrapString(uiLabelMap.RoleInPlanBoard)}', datafield: 'roleDescription', 
							width: '20%', editable: false, filtertype: 'input'},
						"/>
</script>		

<@jqGrid filtersimplemode="true" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="true" editable="false" deleterow="false" selectionmode="singlerow"
				addrow="false" showlist="false" sortable="true"  mouseRightMenu="true" customControlAdvance="<div id='yearCustomTimePeriod'></div>"
				contextMenuId="contextMenu" url="" jqGridMinimumLibEnable="false"/>	
				
<div id="recruitmentEvaluatedWindow" class="hide">
	<div>${uiLabelMap.RecruitmentCandidatesNeedEvalution}</div>
	<div class='form-window-container'>
		<div class="row-fluid" style="position: relative;">
			<div id="roundMainEvaluatedSplitter" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="listBoxRoundRecEngaged"></div>
				</div>
				<div style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	               		<div id="containerrecruitEvaluatedCandidateGrid" style="background-color: transparent; overflow: auto;">
					    </div>
					    <div id="jqxNotificationrecruitEvaluatedCandidateGrid">
					        <div id="notificationContentrecruitEvaluatedCandidateGrid">
					        </div>
					    </div>
	                   <div id="recruitEvaluatedCandidateGrid"></div>
	               </div>
	        	</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingEvaluated" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerEvaluated"></div>
				</div>
			</div>
		</div>
	</div>
</div>				
<div id="contextMenu" class="hide">
	<ul>
		<li action="viewListCandidates">
			<i class="fa-users"></i>${uiLabelMap.RecruitmentCandidatesNeedEvalution}
        </li>
	</ul>
</div>		

<div id="contextMenuEvaluation" style="z-index: 20000 !important" class="hide">
	<ul>
         <li action="evaluation"><i class="fa fa-pencil-square"></i>${uiLabelMap.EvaluatedCandidate}</li>
     </ul>
</div>		
${setContextField("editable", "true")}
<#include "RecruitEvalutedCandidateTestRound.ftl"/>
<#include "RecruitEvalutedCandidateINTVWRound.ftl"/>
<script type="text/javascript" src="/hrresources/js/recruitment/ViewListCandidateEvaluatedInRecPlan.js"></script>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitEvaluatedCandidates.js"></script>
				