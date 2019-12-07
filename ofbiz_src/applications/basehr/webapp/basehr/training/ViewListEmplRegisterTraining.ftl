<#include "script/ViewListEmplRegisterTrainingScript.ftl"/>
<#assign datafield = "[{name: 'dateRegisted', type: 'date'},
					   {name: 'trainingCourseId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'statusIdRegister', type: 'string'},
					   {name: 'partyCode', type: 'string'},
					   {name: 'firstName', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'groupName', type: 'string'},
					   {name: 'emplPositionType', type: 'string'},
					   ]"/>
					   
<script type="text/javascript">
var cellsrenderer = function (row, column, value, defaultHtml) {
    var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
    if(rowData.statusIdRegister != 'TCR_REGIS'){
        var element = $(defaultHtml);
        element.css('color', '#999');
        return element[0].outerHTML;
    }
    return defaultHtml;
}
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DateRegistration)}', datafield: 'dateRegisted', width: '11%', 
							filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsrenderer: cellsrenderer},
						{text: '${uiLabelMap.CommonStatus}', datafield:'statusIdRegister', editable: false, columntype: 'custom',filtertype: 'checkedlist', 
							cellsalign: 'left', width: '15%', 
							cellsrenderer: function (row, column, value, defaultHtml){
								for(var i = 0; i < globalVar.statusArr.length; i++){
									if(globalVar.statusArr[i].statusId == value){
										if(value != 'TCR_REGIS'){
											return 	'<div style=\"margin: left; color: #999 \">' + globalVar.statusArr[i].description + '</div>';		
										}else{
											return 	'<div style=\"margin: left; \">' + globalVar.statusArr[i].description + '</div>';
										}
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
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '14%', 
							cellsalign: 'left', editable: false, cellsrenderer: cellsrenderer,},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'firstName', width: '17%', 
								cellsalign: 'left', editable: false,
					    	cellsrenderer: function(row, column, value, defaultHtml){
					    		var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
					    		if(rowData && rowData.fullName){
					    			if(rowData.statusIdRegister != 'TCR_REGIS'){
						    			return '<span style=\"color: #999\">' + rowData.fullName + '</span>';
					    			}else{
					    				return '<span>' + rowData.fullName + '</span>';
					    			}
					    		}
					    	}
					    },
					    {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '20%', cellsrenderer: cellsrenderer},
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionType', width: '20%', cellsrenderer: cellsrenderer},
"/>
</script>	
<#assign customcontrol1 = "fa-thumbs-o-up open-sans@${uiLabelMap.CommonApprove}@javascript: void(0);@listEmplRegisterObj.approvalRegister('accept')">
<#assign customcontrol2 = "fa-thumbs-o-down open-sans@${uiLabelMap.CommonReject}@javascript: void(0);@listEmplRegisterObj.approvalRegister('reject')">

<@jqGrid url="jqxGeneralServicer?sname=JQGetListEmplRegisTraining&trainingCourseId=${trainingCourseId}" dataField=datafield columnlist=columnlist
		editable="true"  showlist="true" sortable="true" clearfilteringbutton="true"
		showtoolbar="true" deleterow="false" jqGridMinimumLibEnable="false" selectionmode="checkbox"
		addrow="false" customcontrol1=customcontrol1 customcontrol2=customcontrol2
		/>	
		
<script type="text/javascript" src="/hrresources/js/training/ViewListEmplRegisterTraining.js"></script>					   