<script>
	//Prepare for school data
	<#assign listSchools = delegator.findList("EducationSchool", null, null, null, null, false) >
	var schoolData = new Array();
	<#list listSchools as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.schoolName?if_exists) />
		row['schoolId'] = '${item.schoolId}';
		row['description'] = '${description}';
		schoolData[${item_index}] = row;
	</#list>

	//Prepare for major data
	<#assign listMajors = delegator.findList("Major", null, null, null, null, false) >
	var majorData = new Array();
	<#list listMajors as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['majorId'] = '${item.majorId}';
		row['description'] = '${description}';
		majorData[${item_index}] = row;
	</#list>

	//Prepare for study mode data
	<#assign listStudyModes = delegator.findList("StudyModeType", null, null, null, null, false) >
	var studyModeData = new Array();
	<#list listStudyModes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['studyModeTypeId'] = '${item.studyModeTypeId}';
		row['description'] = '${description}';
		studyModeData[${item_index}] = row;
	</#list>

	//Prepare for Degree Classification Type data
	<#assign listDegree = delegator.findList("DegreeClassificationType", null, null, null, null, false) >
	var degreeData = new Array();
	<#list listDegree as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['classificationTypeId'] = '${item.classificationTypeId}';
		row['description'] = '${description}';
		degreeData[${item_index}] = row;
	</#list>

	//Prepare for education system type data
	<#assign listEducationSys = delegator.findList("EducationSystemType", null, null, null, null, false) >
	var eduSystemData = new Array();
	<#list listEducationSys as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['educationSystemTypeId'] = '${item.educationSystemTypeId}';
		row['description'] = '${description}';
		eduSystemData[${item_index}] = row;
	</#list>
</script>
<div class="tab-pane" id="trainingInfoTab">
	<#assign dataField="[{ name: 'partyId', type: 'string' },
						{ name: 'schoolId', type: 'string' },
						{ name: 'majorId', type: 'string' },
						{ name: 'studyModeTypeId', type: 'string' },
						{ name: 'classificationTypeId', type: 'string' },
						{ name: 'educationSystemTypeId', type: 'string'},
						{ name: 'fromDate', type: 'date', other: 'Timestamp'},
						{ name: 'thruDate', type: 'date', other: 'Timestamp'},
						]"/>

	<#assign columnlist="{ text: '${uiLabelMap.HRCollegeName}', datafield: 'schoolId', width: 200,
							cellsrenderer: function(column, row, value){
								for(var i = 0;  i < schoolData.length; i++){
									if(schoolData[i].schoolId == value){
										return '<span title=' + value + '>' + schoolData[i].description + '</span>'
									}
								}
								return '<span>' + value + '</span>'
							}
						 },
						 { text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', width: 200, cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									editor.jqxDateTimeInput({ });
							}
						 },
						 { text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', width: 200, cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
							 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									editor.jqxDateTimeInput({ });
							},
							validation: function (cell, value) {
								var data = $('#jqxgridTrainingInfo').jqxGrid('getrowdata', cell.row);
								if( value < data.fromDate ){
							    	return { result: false, message: '${uiLabelMap.TimeBeginAfterTimeEnd}'};
							    }
							    else 
							        return true;
							    }
						 },
						 { text: '${uiLabelMap.HRSpecialization}', datafield: 'majorId',  filtertype: 'list', columntype: 'dropdownlist', editable: true,
								cellsrenderer: function(column, row, value){
									for(var i = 0;  i < majorData.length; i++){
										if(majorData[i].majorId == value){
											return '<span title=' + value + '>' + majorData[i].description + '</span>'
										}
									}
									return '<span>' + value + '</span>'
								},
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							        editor.jqxDropDownList({source: majorList, valueMember: 'majorId', displayMember:'description' });
							    },
							    createfilterwidget: function (column, htmlElement, editor) {
					                editor.jqxDropDownList({ source: fixSelectAll(majorList), displayMember: 'description', valueMember: 'majorId' ,
					                	renderer: function (index, label, value) {
					                		if (index == 0) {
					                			return value;
					                		}
					                        for(var i = 0; i < majorList.length; i++){
					                        	if(value == majorList[i].majorId){
					                        		return majorList[i].description; 
					                        	}
					                        }
					                    }});
					                editor.jqxDropDownList('checkAll');
					            }
						 },
						 { text: '${uiLabelMap.HROlbiusTrainingType}', datafield: 'studyModeTypeId', filtertype: 'list', columntype: 'dropdownlist', editable: true,
								cellsrenderer: function(column, row, value){
									for(var i = 0;  i < studyModeData.length; i++){
										if(studyModeData[i].studyModeTypeId == value){
											return '<span title=' + value + '>' + studyModeData[i].description + '</span>'
										}
									}
									return '<span>' + value + '</span>'
								},
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							        editor.jqxDropDownList({source: studyModeType, valueMember: 'studyModeTypeId', displayMember:'description' });
							    },
							    createfilterwidget: function (column, htmlElement, editor) {
					                editor.jqxDropDownList({ source: fixSelectAll(studyModeType), displayMember: 'description', valueMember: 'studyModeTypeId' ,
					                	renderer: function (index, label, value) {
					                		if (index == 0) {
					                			return value;
					                		}
					                        for(var i = 0; i < studyModeType.length; i++){
					                        	if(value == studyModeType[i].studyModeTypeId){
					                        		return studyModeType[i].description; 
					                        	}
					                        }
					                    }});
					                editor.jqxDropDownList('checkAll');
					            }
						 },
						 { text: '${uiLabelMap.HRCommonClassification}', datafield: 'classificationTypeId',  filtertype: 'list', columntype: 'dropdownlist', editable: true,
								cellsrenderer: function(column, row, value){
									for(var i = 0;  i < degreeData.length; i++){
										if(degreeData[i].classificationTypeId == value){
											return '<span title=' + value + '>' + degreeData[i].description + '</span>'
										}
									}
									return '<span>' + value + '</span>'
								},
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							        editor.jqxDropDownList({source: degreeClassificationType, valueMember: 'classificationTypeId', displayMember:'description' });
							    },
							    createfilterwidget: function (column, htmlElement, editor) {
					                editor.jqxDropDownList({ source: fixSelectAll(degreeClassificationType), displayMember: 'description', valueMember: 'classificationTypeId' ,
					                	renderer: function (index, label, value) {
					                		if (index == 0) {
					                			return value;
					                		}
					                        for(var i = 0; i < degreeClassificationType.length; i++){
					                        	if(value == degreeClassificationType[i].classificationTypeId){
					                        		return degreeClassificationType[i].description; 
					                        	}
					                        }
					                    }});
					                editor.jqxDropDownList('checkAll');
					            }
						 },
						 { text: '${uiLabelMap.educationSystemType}', datafield: 'educationSystemTypeId', filtertype: 'list', columntype: 'dropdownlist', editable: true,
								cellsrenderer: function(column, row, value){
									for(var i = 0;  i < eduSystemData.length; i++){
										if(eduSystemData[i].educationSystemTypeId == value){
											return '<span title=' + value + '>' + eduSystemData[i].description + '</span>'
										}
									}
									return '<span>' + value + '</span>'
								},
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							        editor.jqxDropDownList({source: educationSystemType, valueMember: 'educationSystemTypeId', displayMember:'description' });
							    },
							    createfilterwidget: function (column, htmlElement, editor) {
					                editor.jqxDropDownList({ source: fixSelectAll(educationSystemType), displayMember: 'description', valueMember: 'educationSystemTypeId' ,
					                	renderer: function (index, label, value) {
					                		if (index == 0) {
					                			return value;
					                		}
					                        for(var i = 0; i < educationSystemType.length; i++){
					                        	if(value == educationSystemType[i].educationSystemTypeId){
					                        		return educationSystemType[i].description; 
					                        	}
					                        }
					                    }});
					                editor.jqxDropDownList('checkAll');
					            }
						 }
					 "/>

	<@jqGrid addrow="true" deleterow="true" addType="popup" isShowTitleProperty="false" id="jqxgridTrainingInfo" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" editable="true"
	url="jqxGeneralServicer?sname=JQGetListEmplEducation&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist  alternativeAddPopup="alterpopupWindow1"
	createUrl="jqxGeneralServicer?sname=createPersonEducation&jqaction=C" addColumns="partyId;schoolId;majorId;studyModeTypeId;classificationTypeId;educationSystemTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
	removeUrl="jqxGeneralServicer?sname=deletePersonEducation&jqaction=D" deleteColumn="partyId;schoolId;majorId;studyModeTypeId;educationSystemTypeId;fromDate(java.sql.Timestamp)"
		
		/>
</div>

<div id="alterpopupWindow1" style="display : none;">
<div>${uiLabelMap.CommonAdd}</div>
<div style="overflow: hidden;">
	<form id="PersonEducationForm" class="form-horizontal">
		<input type="hidden" value="${parameters.partyId}" id="partyId" name="partyId" />
				
		<div class="row-fluid no-left-margin">
			<label class="span4">${uiLabelMap.HRCollegeName}</label>
			<div class="span8" style="margin-bottom: 10px;">
				<div id="schoolAdd"></div>
			</div>
		</div>
				
		<div class="row-fluid no-left-margin">
			<label class="span4">${uiLabelMap.CommonFromDate}</label>
			<div class="span8" style="margin-bottom: 10px;">
        		<div id="educationFromDateAdd"></div>
        	</div>
        </div>
        
        <div class="row-fluid no-left-margin">
			<label class="span4">${uiLabelMap.CommonThruDate}</label>
			<div class="span8" style="margin-bottom: 10px;">
    			<div id="educationThruDateAdd"></div>
    		</div>
    	</div>
    	
    	<div class="row-fluid no-left-margin">
			<label class="span4">${uiLabelMap.HRSpecialization}</label>
			<div class="span8" style="margin-bottom: 10px;">
				<div id="majorAdd"></div>
			</div>
		</div>
	
		<div class="row-fluid no-left-margin">
			<label class="span4">${uiLabelMap.HROlbiusTrainingType}</label>
			<div class="span8" style="margin-bottom: 10px;">
				<div id="studyModeTypeAdd"></div>
			</div>
		</div>
		
		<div class="row-fluid no-left-margin">
			<label class="span4">${uiLabelMap.HRCommonClassification}</label>
			<div class="span8" style="margin-bottom: 10px;">
				<div id="degreeClassificationTypeAdd"></div>
			</div>
		</div>

		<div class="row-fluid no-left-margin">
			<label class="span4">${uiLabelMap.HRCommonSystemEducation}</label>
			<div class="span8" style="margin-bottom: 10px;">
				<div id="educationSystemTypeAdd"></div>
			</div>
		</div>

		<div class="control-group no-left-margin" style="float:right">
			<div class="" style="width:166px;margin:0 auto;">
				<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" style="margin-right: 5px; margin-top: 10px;" id="alterSave1"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
				<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius btn-danger" style="margin-right: 5px; margin-top: 10px;" id="alterCancel1"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
			</div>
		</div>
				
	</form>
</div>
</div>		 
 
<#assign partyRelationshipType = delegator.findByAnd("PartyRelationshipType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "FAMILY"), null, false)>
<#assign schoolList = delegator.findByAnd("EducationSchool", null, null, false)>
<#assign majorList = delegator.findList("Major", null , null, orderBy,null, false)>
<#assign studyModeType = delegator.findByAnd("StudyModeType", null ,null , false)>
<#assign degreeClassificationType = delegator.findByAnd("DegreeClassificationType", null, null, false)>
<#assign educationSystemType = delegator.findByAnd("EducationSystemType", null, null, false)>

<script type="text/javascript">
$.jqx.theme = 'olbius';
var theme = theme;

function fixSelectAll(dataList) {
	var sourceST = {
	        localdata: dataList,
	        datatype: "array"
    };
	var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
    var uniqueRecords2 = filterBoxAdapter2.records;
	uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
	return uniqueRecords2;
}

var schoolList = [
    <#list schoolList as schoolL>
    {
    	schoolId : "${schoolL.schoolId}",
    	schoolName : "${StringUtil.wrapString(schoolL.schoolName)}"
    },
    </#list>	
];

var majorList = [
                  <#list majorList as majorL>
                  {
                  	majorId : "${majorL.majorId}",
                  	description : "${StringUtil.wrapString(majorL.description)}"
                  },
                  </#list>	
              ];

var studyModeType = [
                  <#list studyModeType as studyModeT>
                  {
                	studyModeTypeId : "${studyModeT.studyModeTypeId}",
                	description : "${StringUtil.wrapString(studyModeT.description)}"
                  },
                  </#list>	
              ];

var degreeClassificationType = [
                  <#list degreeClassificationType as degreeClassificationT>
                  {
                	classificationTypeId : "${degreeClassificationT.classificationTypeId}",
                	description : "${StringUtil.wrapString(degreeClassificationT.description)}"
                  },
                  </#list>	
              ];

var educationSystemType = [
                  <#list educationSystemType as educationSystemT>
                  {
                	educationSystemTypeId : "${educationSystemT.educationSystemTypeId}",
                	description : "${StringUtil.wrapString(educationSystemT.description)}"
                  },
                  </#list>	
              ]; 

$("#relationshipAdd").jqxDropDownList({autoDropDownHeight: true});
$('#alterpopupWindow1').jqxWindow({ width: 480, height : 380,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7 });
$("#schoolAdd").jqxDropDownList({ source: schoolList, width: '248px', height: '25px', selectedIndex: 0, displayMember: "schoolName", valueMember : "schoolId"});
$("#educationFromDateAdd").jqxDateTimeInput({width: '248px', height: '25px'});
$("#educationThruDateAdd").jqxDateTimeInput({width: '248px', height: '25px'});
$("#majorAdd").jqxDropDownList({ source: majorList, width: '248px', height: '25px', selectedIndex: 0, displayMember: "description", valueMember : "majorId"});
$("#studyModeTypeAdd").jqxDropDownList({ source: studyModeType, width: '248px', height: '25px', selectedIndex: 0, displayMember: "description", valueMember : "studyModeTypeId"});
$("#degreeClassificationTypeAdd").jqxDropDownList({ source: degreeClassificationType, width: '248px', selectedIndex: 0, height: '25px', displayMember: "description", valueMember : "classificationTypeId"});
$("#educationSystemTypeAdd").jqxDropDownList({ source: educationSystemType, width: '248px', height: '25px', selectedIndex: 0, displayMember: "description", valueMember : "educationSystemTypeId"});

$("#schoolAdd").jqxDropDownList({autoDropDownHeight: true}); 
$("#majorAdd").jqxDropDownList({autoDropDownHeight: true}); 
$("#studyModeTypeAdd").jqxDropDownList({autoDropDownHeight: true}); 
$("#degreeClassificationTypeAdd").jqxDropDownList({autoDropDownHeight: true}); 
$("#educationSystemTypeAdd").jqxDropDownList({autoDropDownHeight: true}); 



$('#PersonEducationForm').jqxValidator({
	rules : [
	         {input: '#educationFromDateAdd', message: 'Your birth date must be between 1/1/1900 and 1/1/2014.', action: 'valueChanged', rule: function (input, commit) {
                     var date = $('#educationFromDateAdd').jqxDateTimeInput('value');
                     var result = date.getFullYear() >= 1900 && date.getFullYear() <= 2016;
                     // call commit with false, when you are doing server validation and you want to display a validation error on this field. 
                     return result;
                 }
             },
	]
});

$('#alterSave1').click(function(){
	$('#PersonEducationForm').jqxValidator('validate');
});

$('#PersonEducationForm').on('validationSuccess',function(){
	var row = {};
	row = {
		partyId: $('#partyId').val(),
		schoolId : $('#schoolAdd').val(),
		majorId : $('#majorAdd').val(),
		studyModeTypeId : $('#studyModeTypeAdd').val(),
		classificationTypeId : $('#degreeClassificationTypeAdd').val(),
		educationSystemTypeId : $('#educationSystemTypeAdd').val(),
		
		fromDate : $('#educationFromDateAdd').val(),
		thruDate : $('#educationThruDateAdd').val()
		
	};
	$("#jqxgridTrainingInfo").jqxGrid('addRow', null, row, "first");
// select the first row and clear the selection.
	$("#jqxgridTrainingInfo").jqxGrid('clearSelection');                        
	$("#jqxgridTrainingInfo").jqxGrid('selectRow', 0);  
	$("#alterpopupWindow1").jqxWindow('close');
});


$('#alterpopupWindow1').on('close',function(){
	$('#PersonEducationForm').jqxValidator('hide');
//	$('#PersonEducationForm').trigger('reset');
});

function convertDate(date) {
 	if (!date) {
		return null;
	}
	var dateArray = date.split("/");
	var newDate = new Date(dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0]);
	return newDate.getTime();
}
</script>