<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<style>
	#alterpopupNewTraining .form-horizontal .control-group{
		padding-top: 0px !important;
	}
</style>  
<script type="text/javascript">
<#assign dataFields = "[{name:'trainingCourseId', type: 'string'},
						{name:'trainingCourseName', type: 'string'},
						{name: 'estimatedFromDate', type: 'date'},
						{name: 'estimatedThruDate', type:'date'},
						{name: 'description', type: 'string'},
						{name: 'estimatedNumber', type: 'number'},
						{name: 'actualNumber', type: 'number'},
						{name: 'trainingTypeId', type:'string'},
						{name: 'trainingFormTypeId', type:'string'},
						{name: 'statusId', type: 'string'}]">

var trainingFormTypes = new Array();
<#list trainingFormTypeList as formType>
	var row = {};
	row["trainingFormTypeId"] = "${formType.trainingFormTypeId}";
	row["description"] = "${formType.description?if_exists}";
	trainingFormTypes[${formType_index}] = row;
</#list>

var trainingTypes = new Array();

<#list trainingTypeList as type>
	var row = {};
	row["trainingTypeId"] = "${type.trainingTypeId}";
	row["description"] = "${type.description?if_exists}";
	trainingTypes[${type_index}] = row;
</#list>
var statusList = new Array();
<#list statusList as status>
	var row = {};
	row["statusId"] = "${status.statusId}";
	row["description"] = "${status.description?if_exists}";
	statusList[${status_index}] = row;
</#list>
<#assign columnlist = "{text: '${uiLabelMap.TrainingCourseId}', datafield: 'trainingCourseId' ,filtertype: 'input', editable: false, cellsalign: 'left', width: 130},
						{text: '${uiLabelMap.TrainingCourseName}', datafield: 'trainingCourseName', filtertype: 'input', editable: false, width: 200},
						{text:'${uiLabelMap.EstimatedFromDate}', datafield: 'estimatedFromDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false, width: 150},
						{text: '${uiLabelMap.EstimatedThruDate}', datafield: 'estimatedThruDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false, width: 130, hidden: true},
						{text: '${uiLabelMap.CommonDescription}', datafield: 'description', filtertype: 'input', editable: false, width: 100, hidden: true},
						{text: '${uiLabelMap.CommonEstimatedNumber}', datafield: 'estimatedNumber', filtertype: 'number', editable: false, cellsalign: 'right', width: 130},
						{text: '${uiLabelMap.CommonActualNumber}', datafield: 'actualNumber', filtertype: 'number', editable: false , cellsalign: 'right', width: 100, hidden: true},
						{text: '${uiLabelMap.TrainingTypeId}', datafield: 'trainingTypeId', editable: false, columntype: 'custom',filtertype: 'checkedlist', cellsalign: 'left',width: 150, 
							cellsrenderer: function (row, column, value){
								for(var i = 0; i < trainingTypes.length; i++){
									if(trainingTypes[i].trainingTypeId == value){
										return '<div style=\"\">' + trainingTypes[i].description + '</div>';		
									}
								}
							},
							createfilterwidget: function(column, columnElement, widget){
								var sourceTrainingType = {
							        localdata: trainingTypes,
							        datatype: \"array\"
							    };		
								var filterBoxAdapter = new $.jqx.dataAdapter(sourceTrainingType, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //var selectAll = {'trainingTypeId': 'selectAll', 'description': '(Select All)'};
							    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'trainingTypeId', valueMember : 'trainingTypeId',
									renderer: function (index, label, value) {
										for(i=0; i < trainingTypes.length; i++){
											if(trainingTypes[i].trainingTypeId == value){
												return trainingTypes[i].description;
											}
										}
									    return value;
									}
								});
							    
							}
						},
						{text: '${uiLabelMap.TrainingFormTypeId}', datafield:'trainingFormTypeId', editable: false, columntype: 'custom',filtertype: 'checkedlist',width: 150,
							cellsrenderer: function (row, column, value){
								for(var i = 0; i < trainingFormTypes.length; i++){
									if(trainingFormTypes[i].trainingFormTypeId == value){
										return 	'<div style=\"\">' + trainingFormTypes[i].description + '</div>';		
									}
								}
							},
							createfilterwidget: function(column, columnElement, widget){
								var sourceTrainingFormType = {
							        localdata: trainingFormTypes,
							        datatype: \"array\"
							    };		
								var filterBoxAdapter = new $.jqx.dataAdapter(sourceTrainingFormType, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //var selectAll = {'trainingFormTypeId': 'selectAll', 'description': '(Select All)'};
							    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'trainingFormTypeId', valueMember : 'trainingFormTypeId',
									renderer: function (index, label, value) {
										for(i=0; i < trainingFormTypes.length; i++){
											if(trainingFormTypes[i].trainingFormTypeId == value){
												return trainingFormTypes[i].description;
											}
										}
									    return value;
									}
								});
							}
						},
						{text: '${uiLabelMap.CommonStatus}', datafield:'statusId', editable: false, columntype: 'custom',filtertype: 'checkedlist', cellsalign: 'left',
							cellsrenderer: function (row, column, value){
								for(var i = 0; i < statusList.length; i++){
									if(statusList[i].statusId == value){
										return 	'<div style=\"margin: left\">' + statusList[i].description + '</div>';		
									}
								}
							},
							createfilterwidget: function(column, columnElement, widget){
								var sourceStatusItem = {
							        localdata: statusList,
							        datatype: \"array\"
							    };		
								var filterBoxAdapter = new $.jqx.dataAdapter(sourceStatusItem, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							   // var selectAll = {'trainingFormTypeId': 'selectAll', 'description': '(Select All)'};
							    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'statusId', valueMember : 'statusId',
									renderer: function (index, label, value) {
										for(i=0; i < statusList.length; i++){
											if(statusList[i].statusId == value){
												return statusList[i].description;
											}
										}
									    return value;
									}
								});
							}
						}
						">					   
</script>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"  
		 filterable="true" alternativeAddPopup="alterpopupNewTraining" deleterow="false" editable="true" addrow="false"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQListTrainingCourse" id="jqxgrid" removeUrl="" deleteColumn=""
		 updateUrl=""  jqGridMinimumLibEnable="false"
		 editColumns=""
	/>