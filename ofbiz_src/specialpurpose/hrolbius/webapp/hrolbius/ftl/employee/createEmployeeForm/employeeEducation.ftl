<div id="jqxgridEdu"></div>
<div id = "createNewEduWindow" style="display: none;">
	<div id="windowHeaderNewEducation">
		<span>
		   ${uiLabelMap.NewEducation}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<form name="createNewEducation" id="createNewEducation">	
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.HRCollegeName}:</label>
						<div class="controls">
							<div id="eduSchoolId"></div>
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.CommonFromDate}:</label>  
						<div class="controls">
							<div id="eduFromDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.CommonThruDate}:</label>
						<div class="controls">
							<div id="eduThruDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HRSpecialization}:</label>  
						<div class="controls">
							<div  id="eduMajorId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HROlbiusTrainingType}:</label>   
						<div class="controls">
							<div id="eduStudyModeTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HRCommonClassification}:</label>   
						<div class="controls">
							<div id="eduDegreeClassificationTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.HRCommonSystemEducation}:</label>   
						<div class="controls">
							<div id="eduSystemTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp</label>
						<div class="controls">
							<button type="button" class="btn btn-mini btn-primary" id="alterSaveEdu"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
							<button type="button" class="btn btn-mini btn-danger" id="alterCancelEdu"><i class="icon-remove">${uiLabelMap.CommonCancel}&nbsp;</i></button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<script type="text/javascript">
//Prepare data for school
	<#assign schoolList = delegator.findByAnd("EducationSchool", null, null, false)>
	var schoolData = new Array();
	<#list schoolList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.schoolName?if_exists) />
		row['schoolId'] = '${item.schoolId?if_exists}';
		row['description'] = '${description}';
		schoolData[${item_index}] = row;
	</#list>
	//Prepare data for Major
	<#assign majorList = delegator.findList("Major", null , null, orderBy,null, false)>
	var majorData = new Array();
	<#list majorList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['majorId'] = '${item.majorId?if_exists}';
		row['description'] = '${description}';
		majorData[${item_index}] = row;
	</#list>
	
	//Prepare StudyModeType
	<#assign studyModeTypeList = delegator.findByAnd("StudyModeType", null ,null , false)>
	var studyModeTypeData = new Array();
	<#list studyModeTypeList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['studyModeTypeId'] = '${item.studyModeTypeId?if_exists}';
		row['description'] = '${description}';
		studyModeTypeData[${item_index}] = row;
	</#list>
	
	//Prepare classificationType
	<#assign degreeClassificationTypeList = delegator.findByAnd("DegreeClassificationType", null, null, false)>
	var classificationTypeData = new Array();
	<#list degreeClassificationTypeList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['classificationTypeId'] = '${item.classificationTypeId?if_exists}';
		row['description'] = '${description}';
		classificationTypeData[${item_index}] = row;
	</#list>
	
	//Prepare EducationSystemType
	<#assign educationSystemTypeList = delegator.findByAnd("EducationSystemType", null, null, false)>
	var educationSystemTypeData = new Array();
	<#list educationSystemTypeList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['educationSystemTypeId'] = '${item.educationSystemTypeId?if_exists}';
		row['description'] = '${description}';
		educationSystemTypeData[${item_index}] = row;
	</#list>
	$(document).ready(function () {
		/******************************************************Edit Education**************************************************************************/
		var theme = "olbius";
		//Handle alterSaveFamily
		$("#createNewEducation").jqxValidator({
			rules: [
				{input: '#eduSchoolId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
	                        return true;
	                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
	                    	return false;
	                    }else {
	                    	return true;
	                    }
	                }
				},
				{input: '#eduMajorId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
	                        return true;
	                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
	                    	return false;
	                    }else {
	                    	return true;
	                    }
	                }
				},
				{input: '#eduStudyModeTypeId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
	                        return true;
	                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
	                    	return false;
	                    }else {
	                    	return true;
	                    }
	                }
				},
				{input: '#eduDegreeClassificationTypeId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
	                        return true;
	                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
	                    	return false;
	                    }else {
	                    	return true;
	                    }
	                }
				},
				{input: '#eduFromDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
	                        return true;
	                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
	                    	return false;
	                    }else {
	                    	return true;
	                    }
	                }
				},
				{input: '#eduThruDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
	                        return true;
	                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
	                    	return false;
	                    }else {
	                    	return true;
	                    }
	                }
				},
				{input: '#eduFromDate', message: '${uiLabelMap.LTDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') > $("#eduThruDate").jqxDateTimeInput('getDate') && $("#eduThruDate").jqxDateTimeInput('getDate')){
	                    	return false;
	                    }else{
	                    	return true;
	                    }
	                    	
	            	}
				},
				{input: '#eduThruDate', message: '${uiLabelMap.GTDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') < $("#eduFromDate").jqxDateTimeInput('getDate') && $("#eduFromDate").jqxDateTimeInput('getDate')){
	                    	return false;
	                    }else{
	                    	return true;
	                    }
	                    	
	            	}
				},
			]
		});
		$("#alterSaveEdu").click(function () {
			$("#createNewEducation").jqxValidator('validate');
		});
		
		$("#createNewEducation").on('validationSuccess', function (event) {
			var row;
		        row = {
		        		schoolId:$('#eduSchoolId').val(),
		        		fromDate:$("#eduFromDate").jqxDateTimeInput('getDate'),
		        		thruDate:$("#eduThruDate").jqxDateTimeInput('getDate'),
		        		majorId:$("#eduMajorId").val(),
		        		studyModeTypeId:$("#eduStudyModeTypeId").val(),
		        		classificationTypeId:$("#eduDegreeClassificationTypeId").val(),
		        		educationSystemTypeId:$("#eduSystemTypeId").val(),
				  };
		        
		        $("#jqxgridEdu").jqxGrid('addrow', null, row, "first");
		        // select the first row and clear the selection.
		        $("#jqxgridEdu").jqxGrid('clearSelection');
		        $("#jqxgridEdu").jqxGrid('selectRow', 0);
		        $("#createNewEduWindow").jqxWindow('close');
		    });
		//Create new family window
		$("#createNewEduWindow").jqxWindow({
	        showCollapseButton: false, maxHeight: 430, autoOpen: false, maxWidth: "80%", height: 430, minWidth: '40%', width: "50%", isModal: true,
	        theme:'olbius', collapsed:false
	    });
		
		//Create eduSchoolId
		$("#eduSchoolId").jqxDropDownList({source: schoolData, selectedIndex: 0, valueMember:'schoolId', displayMember:'description', theme: theme});
		<#if (schoolList?size < 8)>
			$("#eduSchoolId").jqxDropDownList({autoDropDownHeight: true});
		</#if>
		//Create eduFromDate
		$("#eduFromDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', theme: theme});
		
		//Create eduThruDate
		$("#eduThruDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy' , theme: theme});
		
		//Create eduMajorId
		$("#eduMajorId").jqxDropDownList({source: majorData, selectedIndex: 0, valueMember: 'majorId', displayMember:'description', theme: theme});
		<#if (majorList?size < 8)>
			$("#eduMajorId").jqxDropDownList({autoDropDownHeight: true});
		</#if>
		//Create eduStudyModeTypeId
		$("#eduStudyModeTypeId").jqxDropDownList({source: studyModeTypeData, selectedIndex: 0, 
			valueMember: 'studyModeTypeId', displayMember:'description', theme: theme});
		<#if (studyModeTypeList?size < 8)>
			$("#eduStudyModeTypeId").jqxDropDownList({autoDropDownHeight: true});
		</#if>
		
		//Create eduDegreeClassificationTypeId
		$("#eduDegreeClassificationTypeId").jqxDropDownList({source: classificationTypeData, selectedIndex: 0, 
			valueMember: 'classificationTypeId', displayMember:'description', theme: theme});
		<#if (degreeClassificationTypeList?size < 8)>
			$("#eduDegreeClassificationTypeId").jqxDropDownList({autoDropDownHeight: true});
		</#if>
		//Create eduSystemTypeId
		$("#eduSystemTypeId").jqxDropDownList({source: educationSystemTypeData, selectedIndex: 0, 
			valueMember: 'educationSystemTypeId', displayMember:'description', theme: theme});
		<#if (educationSystemTypeList?size < 8)>
			$("#eduSystemTypeId").jqxDropDownList({autoDropDownHeight: true});
		</#if>
		/******************************************************End Edit Family**********************************************************************/
		var eduData = new Array();
		var eduIndex = 0;
		var source =
        {
            localdata: eduData,
            datatype: "array",
            datafields:
            [
            	{ name: 'schoolId', type: 'string' },
				{ name: 'majorId', type: 'string' },
				{ name: 'studyModeTypeId', type: 'string' },
				{ name: 'classificationTypeId', type: 'string' },
				{ name: 'educationSystemTypeId', type: 'date' },
				{ name: 'fromDate', type: 'date', other:'Timestamp' },
				{ name: 'thruDate', type: 'date', other:'Timestamp' }
            ]
        };
    	var dataAdapter = new $.jqx.dataAdapter(source);
        
    	$("#jqxgridEdu").jqxGrid(
        {
            width: "99%",
            source: dataAdapter,
            columnsresize: true,
            pageable: true,
            autoheight: true,
            showtoolbar: true,
            theme: 'olbius',
            rendertoolbar: function (toolbar) {
                var container = $("<div id='toolbarcontainer' class='widget-header'>");
                toolbar.append(container);
                container.append('<h4></h4>');
                container.append('<button id="eduAddrowbutton" class="grid-action-button"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
                container.append('<button id="eduDelrowbutton" class="grid-action-button"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
             //   $("#eduAddrowbutton").jqxButton();
             //   $("#eduDelrowbutton").jqxButton();
                // create new row.
                $("#eduAddrowbutton").on('click', function () {
                	$("#createNewEduWindow").jqxWindow('open');
                });
                
                // create new row.
                $("#eduDelrowbutton").on('click', function () {
                	var selectedrowindex = $('#jqxgridEdu').jqxGrid('selectedrowindex'); 
                	eduData.splice(selectedrowindex, 1);
                	$('#jqxgridEdu').jqxGrid('updatebounddata'); 
                	
                });
            },
            columns: [
          	  { text: '${uiLabelMap.HRCollegeName}', datafield: 'schoolId', width: 150,
          		 cellsrenderer: function(row, column, value){
          			 for(var i = 0; i < schoolData.length; i++){
          				 if(value == schoolData[i].schoolId){
          					 return '<span title=' + value + '>' + schoolData[i].description + '</span>'
          				 }
          			 }
          			 return '<span>' + value + '</span>'
          		 }
          	  },
              { text: '${uiLabelMap.HRSpecialization}', datafield: 'majorId', width: 150,
          		 cellsrenderer: function(row, column, value){
          			 for(var i = 0; i < majorData.length; i++){
          				 if(value == majorData[i].majorId){
          					 return '<span title=' + value + '>' + majorData[i].description + '</span>'
          				 }
          			 }
          			 return '<span>' + value + '</span>'
          		 }  
              },
              { text: '${uiLabelMap.HROlbiusTrainingType}', datafield: 'studyModeTypeId', width: 150,
          		 cellsrenderer: function(row, column, value){
          			 for(var i = 0; i < studyModeTypeData.length; i++){
          				 if(value == studyModeTypeData[i].studyModeTypeId){
          					 return '<span title=' + value + '>' + studyModeTypeData[i].description + '</span>'
          				 }
          			 }
          			 return '<span>' + value + '</span>'
          		 }
              },
              { text: '${uiLabelMap.HRCommonClassification}', datafield: 'classificationTypeId',
            	  cellsrenderer: function(row, column, value){
          			 for(var i = 0; i < classificationTypeData.length; i++){
          				 if(value == classificationTypeData[i].classificationTypeId){
          					 return '<span title=' + value + '>' + classificationTypeData[i].description + '</span>'
          				 }
          			 }
          			 return '<span>' + value + '</span>'
          		 }
              },
              { text: '${uiLabelMap.HRCommonSystemEducation}', datafield: 'educationSystemTypeId', width: 150,
            	  cellsrenderer: function(row, column, value){
          			 for(var i = 0; i < educationSystemTypeData.length; i++){
          				 if(value == educationSystemTypeData[i].educationSystemTypeId){
          					 return '<span title=' + value + '>' + educationSystemTypeData[i].description + '</span>'
          				 }
          			 }
          			 return '<span>' + value + '</span>'
          		 }
              },
              { text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy'},
              { text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy'}
            ]
        });		
	});
	function getEmployeeEducation(){
		var rows = $("#jqxgridEdu").jqxGrid('getrows');
		return rows;
	}
</script>