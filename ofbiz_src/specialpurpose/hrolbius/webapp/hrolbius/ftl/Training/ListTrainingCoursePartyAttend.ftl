<#assign dataFields = "[{name: 'trainingCourseId', type: 'string'},
						{name: 'trainingCourseName', type: 'string'},
						{name: 'partyId', type: 'string'},
						{name: 'actualFromDate', type: 'date'},
						{name: 'actualThruDate', type: 'date'},
						{name: 'trainingTypeId', type: 'string'},
						{name: 'trainingFormTypeId', type: 'string'},
						{name: 'statusId', type: 'string'}]"/>
<script type="text/javascript">
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
	
	var sourceTrainingFormType = {
        localdata: trainingFormTypes,
        datatype: 'array'
	};
	
	var sourceTrainingType = {
        localdata: trainingTypes,
        datatype: "array"
    };		
	
	var sourceStatusItem = {
        localdata: statusList,
        datatype: "array"
	};	
	
	<#assign columnlist = "{text: '${uiLabelMap.TrainingCourseId}', datafield: 'trainingCourseId' ,filtertype: 'input', editable: false, cellsalign: 'left', width: 130},
						   {text: '${uiLabelMap.TrainingCourseName}', datafield: 'trainingCourseName', filtertype: 'input', editable: false, width: 200},
						   {text: '${uiLabelMap.EmployeeId}', datafield:'partyId', editable: false, filterable: false, hidden: true},
						   {text:'${uiLabelMap.HRCommonFromDate}', datafield: 'actualFromDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false, width: 130},
						   {text: '${uiLabelMap.HRCommonThruDate}', datafield: 'actualThruDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false, width: 130},
						   {text: '${uiLabelMap.TrainingTypeId}', datafield: 'trainingTypeId', editable: false, columntype: 'custom',filtertype: 'checkedlist', cellsalign: 'left',width: 150, 
								cellsrenderer: function (row, column, value){
									for(var i = 0; i < trainingTypes.length; i++){
										if(trainingTypes[i].trainingTypeId == value){
											return '<div style=\"\">' + trainingTypes[i].description + '</div>';		
										}
									}
								},
								createfilterwidget: function(column, columnElement, widget){
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
							{text: '${uiLabelMap.TrainingResult}', datafield:'statusId', editable: false, columntype: 'custom',filtertype: 'checkedlist', cellsalign: 'left',
								cellsrenderer: function (row, column, value){
									for(var i = 0; i < statusList.length; i++){
										if(statusList[i].statusId == value){
											return 	'<div style=\"margin: left\">' + statusList[i].description + '</div>';		
										}
									}
								},
								createfilterwidget: function(column, columnElement, widget){
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
		 filterable="true" deleterow="false" editable="false" addrow="false"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQListTrainingCourseParty" id="jqxgrid" removeUrl="" deleteColumn=""
		 updateUrl=""
		 editColumns=""
	/>

<div class="row-fluid">
		<div class="span12">
			<div id="HarvestReportWindow">
				<div id="windowHeader">
                    <span>
                       ${uiLabelMap.HarvestReportFor}
                    </span>
                </div>																																																																							
                <div style="overflow-y: auto; overflow-x: hidden; padding: 15px" id="windowContent">
					<div id="HarvestReport"></div>
                </div>	
			</div>
		</div>
	</div>
	
<script type="text/javascript">
$(document).ready(function () { 
	$('#HarvestReportWindow').jqxWindow({
        showCollapseButton: false, autoOpen: false, maxWidth: "75%", minWidth: 800, height: "80%", width: "75%", isModal: true, 
        theme:theme, collapsed:false,
        initContent: function () {
		}
    });
	
	jQuery("#jqxgrid").on("rowDoubleClick", function(event){
		var args = event.args;
		var data = $('#jqxgrid').jqxGrid('getrowdata', args.rowindex);
		trainingCourseId = data["trainingCourseId"];
		trainingCourseName = data["trainingCourseName"];
		partyId = data["partyId"];
		$("#HarvestReportWindow").jqxWindow('setTitle', "${uiLabelMap.HarvestReportFor} " + trainingCourseName);
		$('#HarvestReportWindow').jqxWindow("open");
		jQuery.ajax({
			url:"<@ofbizUrl>EditPartyTrainingCourseHarvestReport</@ofbizUrl>",
			type: "POST",
			data:{partyId: partyId, trainingCourseId: trainingCourseId},
			success: function(data){
				jQuery("#HarvestReport").html(data);
			}
		});
	});
});

</script>	
	
	