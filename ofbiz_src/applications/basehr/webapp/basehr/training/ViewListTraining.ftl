<#include "script/ViewListTrainingScript.ftl"/>
<#assign datafield = "[{name: 'trainingCourseId', type: 'string'},
						{name: 'trainingCourseCode', type: 'string'},
						{name: 'trainingCourseName', type: 'string'},
						{name: 'fromDate', type: 'date'},
						{name: 'thruDate', type: 'date'},
						{name: 'registerThruDate', type: 'date'},
						{name: 'location', type: 'string'},
						{name: 'statusId', type: 'string'},
						]"/>
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.TrainingCourseId)}', datafield: 'trainingCourseCode' ,filtertype: 'input', editable: false, cellClassName: cellClass,
							cellsalign: 'left', width: '14%',
							cellsrenderer: function (row, column, value){
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
								if(rowData){
									if (rowData.statusId == 'TRAINING_SUMMARY' || rowData.statusId == 'TRAINING_COMPLETED') {
										return '<a href=\"ViewTrainingSummary?trainingCourseId='+ rowData.trainingCourseId + '\" title=\"${StringUtil.wrapString(uiLabelMap.ViewDetails)}\">' + value + '</a>';
									}
									return '<a href=\"ViewTrainingDetail?trainingCourseId='+ rowData.trainingCourseId + '\" title=\"${StringUtil.wrapString(uiLabelMap.ViewDetails)}\">' + value + '</a>';
								}
							}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.TrainingCourseName)}', datafield: 'trainingCourseName', filtertype: 'input', editable: false, cellClassName: cellClass, width: '16%'},
						{text:'${StringUtil.wrapString(uiLabelMap.HRCommonFromDate)}', datafield: 'fromDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false, cellClassName: cellClass, width: '14%'},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonThruDate)}', datafield: 'thruDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false, cellClassName: cellClass, width: '14%'},
						{text: '${StringUtil.wrapString(uiLabelMap.HRDeadlineRegister)}', datafield: 'registerThruDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false, cellClassName: cellClass, width: '14%'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonLocation)}', datafield: 'location', editable: false, cellClassName: cellClass, width: '12%'},
						{text: '${uiLabelMap.CommonStatus}', datafield:'statusId', editable: false, columntype: 'custom',filtertype: 'checkedlist', cellClassName: cellClass,
							cellsalign: 'left', width: '15%',
							cellsrenderer: function (row, column, value){
								for(var i = 0; i < globalVar.statusArr.length; i++){
									if(globalVar.statusArr[i].statusId == value){
										return 	'<div style=\"margin: left\">' + globalVar.statusArr[i].description + '</div>';		
									}
								}
							},
							createfilterwidget: function(column, columnElement, widget){
								var sourceStatusItem = {
							        localdata: globalVar.statusArr,
							        datatype: 'array'
							    };		
								var filterBoxAdapter = new $.jqx.dataAdapter(sourceStatusItem, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
							}
						}
                       "/>
</script>	
<div class="row-fluid">
	<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"  
			 filterable="true" alternativeAddPopup="newTrainingWindow" deleterow="false" editable="true" addrow="true"
			 url="jqxGeneralServicer?sname=JQListTrainingCourse" removeUrl="" deleteColumn="" showlist="false"
			 updateUrl="" mouseRightMenu="true" contextMenuId="contextMenu" jqGridMinimumLibEnable="false"
			 editColumns=""
		/>		
</div>

	
<#assign popWindowId = "newTrainingWindow"/>	
<#include "trainingCreateNewTraining.ftl"/>	
<#--<!-- <#assign popupPartyAttTraining = "editPartyAttTrainingWindow"/>
<#include "trainingPartyAttendanceTraining.ftl"/> -->
<#include "AddSkillType.ftl"/>
<div id='contextMenu' class="hide">
    <ul>
    	<li action="trainingCourseDetailPlan"><i class="fa-list-ol"></i>${uiLabelMap.TrainingCoursePlanDetail}</li>
    	<li action="viewListEmplRegister"><i class="fa-sign-in"></i>${uiLabelMap.ListEmplRegistedTraining}</li>
    	
   </ul>
</div>	
	
<script type="text/javascript" src="/hrresources/js/training/ViewListTraining.js"></script>				