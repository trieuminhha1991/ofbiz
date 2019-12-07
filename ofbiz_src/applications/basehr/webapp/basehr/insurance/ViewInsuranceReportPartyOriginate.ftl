<#include "script/ViewInsuranceReportPartyOriginateScript.ftl"/>
<#assign datafield = "[
					  {name: 'insuranceOriginateTypeId', type: 'string'},
					  {name: 'fromDateOriginate', type: 'date'},
					  {name: 'thruDateOriginate', type: 'date'},
					  {name: 'partyId', type: 'string'},
					  {name: 'partyCode', type: 'string'},
					  {name: 'fullName', type: 'string'},
					  {name: 'gender', type: 'string'},
					  {name: 'birthDate', type: 'date'},
					  {name: 'insuranceSocialNbr', type: 'string'},
					  {name: 'insHealthCard', type: 'string'},
					  {name: 'emplPositionTypeDesc', type: 'string'},
					  {name: 'groupName', type: 'string'},
					  {name: 'idNumber', type: 'string'},
					  {name: 'insuranceSalary', type: 'number'},
					  {name: 'insuranceRate', type: 'number'},
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.InsuranceOriginate)}', datafield: 'insuranceOriginateTypeId', editable: false, 
							filterType : 'checkedlist', width: '14%',
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.insuranceOriginateTypeArr.length; i++){
									if(globalVar.insuranceOriginateTypeArr[i].insuranceOriginateTypeId == value){
										return '<span>' + globalVar.insuranceOriginateTypeArr[i].description + '</span>';
									}
								}			
							},
							createfilterwidget : function(column, columnElement, widget){
								var source = {
										localdata : globalVar.insuranceOriginateTypeArr,
										datatype : 'array'
								};
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								var dataFilter = filterBoxAdapter.records;
								widget.jqxDropDownList({source : dataFilter, valueMember : 'insuranceOriginateTypeId', displayMember : 'description'});
								if(dataFilter.length <= 8){
									widget.jqxDropDownList({autoDropDownHeight : true});
								}else{
									widget.jqxDropDownList({autoDropDownHeight : false});
								}
							}	
						},
						{text: '${uiLabelMap.FromMonthYear}', datafield: 'fromDateOriginate', width: '10%', cellsformat: 'dd/MM/yyyy', 
							   columntype: 'datetimeinput', filtertype: 'range'},
						{text: '${uiLabelMap.ToMonthYear}', datafield: 'thruDateOriginate', width: '10%', cellsformat: 'dd/MM/yyyy', 
							   columntype: 'datetimeinput', filtertype: 'range'},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', 
							width: '11%', editable: false},
						{text: '${uiLabelMap.EmployeeName}', datafield: 'firstName', width: '16%',
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
								   if(rowData){
									   return '<span>' + rowData.fullName + '</span>';
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
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeDesc', width: '16%'},
					    {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', 
									cellsalign: 'left', editable: false, width: '16%'},
						{text: '${StringUtil.wrapString(uiLabelMap.TotalInsuranceSocialSalary)}', datafield: 'insuranceSalary', 
						   width: '14%', editable: false, filterType : 'number',
						   cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
							}
					    },
						
						"/>
</script>
<#assign customcontrol1="fa fa-refresh open-sans@${uiLabelMap.RefreshData}@javascript:void(0)@insReportPartyOrigiObj.refreshData()"/>
<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist  showtoolbar="true" columngrouplist=columngrouplist
					 filterable="true"  editable="false" clearfilteringbutton="true" 
					 url="jqxGeneralServicer?sname=JQGetInsReportPartyOriginateAndDetail&reportId=${reportId}" id="jqxgrid" jqGridMinimumLibEnable="false"
					 removeUrl="" deleteColumn="" 
					 addrow="false"  addColumns="" addrefresh="true"
					 deleterow="false" removeUrl=""
					 selectionmode="singlerow" customcontrol1=customcontrol1
					 />							  
<script type="text/javascript" src="/hrresources/js/insurance/ViewInsuranceReportPartyOriginate.js"></script>