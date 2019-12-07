<script type="text/javascript">
	var skillTypeList = new Array();
	<#list skillTypeList as skillType>
		var row ={};
		row["skillTypeId"] = "${skillType.skillTypeId}";
		row["description"] = "${StringUtil.wrapString(skillType.description)}";
		skillTypeList[${skillType_index}] = row;
	</#list>
	
	var statusSkillList = new Array();
	<#list statusList as status>
		var row ={};
		row["statusId"] = "${status.statusId}";
		row["description"] = "${status.description}";
		statusSkillList[${status_index}] = row;
	</#list>
	
	var sourceStatusType = {
	        localdata: statusSkillList,
	        datatype: "array"
	    };		
	var filterBoxAdapter = new $.jqx.dataAdapter(sourceStatusType, {autoBind: true});
    var dataSoureList = filterBoxAdapter.records;
	
	<#assign datafield="[{name:'trainingCourseId', type:'string'},
	                     {name: 'skillTypeId', type:'string'},
	                     {name: 'requiredLevelStatusId', type: 'string'}]"/>
	<#assign columnlist ="{text: '${uiLabelMap.TrainingCourseId}', datafield: 'trainingCourseId' ,filtertype: 'input', editable: false, cellsalign: 'left', hidden: true},
						  {text: '${uiLabelMap.SkillTypeName}', datafield: 'skillTypeId', columntype: 'custom', filtertype: 'checkedlist', cellsalign: 'left',width: 150, editable: false,
								cellsrenderer: function (row, column, value){
									for(var i = 0; i < skillTypeList.length; i++){
										if(skillTypeList[i].skillTypeId == value){
											return '<div style=\"\">' + skillTypeList[i].description + '</div>';		
										}
									}
								},
								createfilterwidget: function(column, columnElement, widget){
									var sourceSkillType = {
								        localdata: skillTypeList,
								        datatype: \"array\"
								    };		
									var filterBoxAdapter = new $.jqx.dataAdapter(sourceSkillType, {autoBind: true});
								    var dataSoureList = filterBoxAdapter.records;
								    //var selectAll = {'trainingTypeId': 'selectAll', 'description': '(Select All)'};
								    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');

								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'skillTypeId', valueMember : 'skillTypeId', height: '25px',
								    	autoDropDownHeight: false, searchMode: 'containsignorecase', incrementalSearch: true, filterable:true,
										renderer: function (index, label, value) {
											for(i=0; i < skillTypeList.length; i++){
												if(skillTypeList[i].skillTypeId == value){
													return skillTypeList[i].description;
												}
											}
										    return value;
										}
									});
								    
								}	
							},
							{text: '${uiLabelMap.RequrimentLevelSkillTrainingCourse}', datafield: 'requiredLevelStatusId', columntype: 'custom', filtertype: 'checkedlist', cellsalign: 'left',
								cellsrenderer: function (row, column, value){
									for(var i = 0; i < statusSkillList.length; i++){
										if(statusSkillList[i].statusId == value){
											return '<div style=\"\">' + statusSkillList[i].description + '</div>';		
										}
									}
								},	
								createfilterwidget: function(column, columnElement, widget){
								    //var selectAll = {'trainingTypeId': 'selectAll', 'description': '(Select All)'};
								    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'statusId', valueMember : 'statusId',
										renderer: function (index, label, value) {
											for(i=0; i < statusSkillList.length; i++){
												if(statusSkillList[i].statusId == value){
													return statusSkillList[i].description;
												}
											}
										    return value;
										}
									});
								    
								}	
							}
							">
</script>

<script type="text/javascript">
	$(document).ready(function () {
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;   	
		$("#submitSkillType").jqxButton({ width: '150', theme: theme});
		$("#cancelSkillType").jqxButton({ width: '150', theme: theme});
		
		$('#alterpopupWindowSkillType').jqxWindow({
            showCollapseButton: true, maxHeight: 200, autoOpen: false, maxWidth: "50%", minHeight: 200, minWidth: "50%", height: 200, width: "50%", isModal: true, 
            theme:theme, collapsed:false,
            initContent: function () {            	
                //jQuery('#trainingCourseDetailWindow').jqxWindow('focus');
                //jQuery('#trainingCourseDetailWindow').jqxWindow('close');
            }
        });
		
		 $("#requirementLevel").jqxDropDownList({ source: dataSoureList, placeHolder:'', displayMember: 'description',dropDownHeight:'120px',  valueMember : 'statusId', height: '25px', width: 300, 
				renderer: function (index, label, value) {
					for(i=0; i < statusSkillList.length; i++){
						if(statusSkillList[i].statusId == value){
							return statusSkillList[i].description;
						}
					}
				    return value;
				}
			});
		 $("#submitSkillType").click(function(){
			 var skillType = $('#jqxTree').jqxTree('getCheckedItems');
			 var level = $("#requirementLevel").jqxDropDownList('getSelectedItem');
			 var skillArray = new Array();
			 for(var i = 0; i < skillType.length; i++){
				 skillArray.push({skillTypeId: skillType[i].id});
			 }
			 var data = {};
			 data["trainingCourseId"] = "${parameters.trainingCourseId}";
			 data["skillTypes"] = JSON.stringify(skillArray);
			 if(level){
				 data["requiredLevelStatusId"] = level.value; 
			 }
			 $("#submitSkillType").jqxButton({disabled: true });
			 jQuery.ajax({
				 url: "<@ofbizUrl>addSkillTypeToTrainingCourse</@ofbizUrl>",
				 type:'POST',
				 data: data,
				 success: function(data){
					 if(data._EVENT_MESSAGE_){
						 $("#messageNotification").jqxNotification({template: "info"});
						 $("#notificationResults").text(data._EVENT_MESSAGE_);
						// $("#messageNotification").jqxNotification("open");
						 $("#jqxGridSkillType").jqxGrid('updatebounddata');
						 $("#alterpopupWindowSkillType").jqxWindow("close");	 
					 }else{
						 $("#messageNotification").jqxNotification({template: "error"});
						 $("#notificationResults").text(data._ERROR_MESSAGE_);
						 //$("#messageNotification").jqxNotification("open");
					 }
					 $("#submitSkillType").jqxButton({disabled: false });
					 
				 }
			 });
		 });
		 
		 $("#messageNotification").jqxNotification({
             width: 250, position: "top-right", opacity: 0.9, appendContainer: "#appendContainerNotifi",
             autoOpen: false, animationOpenDelay: 800, autoClose: false, template: "info"
         });
		 
		 $("#cancelSkillType").click(function(){
			 $("#alterpopupWindowSkillType").jqxWindow("close");
		 });
		 
		var data = new Array();
		<#list listSkillTypeTree as tree>
			var row = {};
			row["id"] = "${tree.id}";
			row["text"] = "${tree.text}";
			row["parentId"] = "${tree.parentId}";
			data[${tree_index}] = row;
		</#list>
		 var source =
         {
             datatype: "json",
             datafields: [
                 { name: 'id' },
                 { name: 'parentId' },
                 { name: 'text' }                
             ],
             id: 'id',
             localdata: data
         };
		 var dataAdapter = new $.jqx.dataAdapter(source);
         // perform Data Binding.
         dataAdapter.dataBind();
         // get the tree items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents 
         // the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter 
         // specifies the mapping between the 'text' and 'label' fields.  
         var records = dataAdapter.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
         $("#dropDownButtonSkillType").jqxDropDownButton({ width: 300, height: 25, theme: theme});
		 //$("#jqxTree").jqxTree({ width: 300, height: 170, theme: 'energyblue', hasThreeStates: true, checkboxes: true});
         $('#jqxTree').jqxTree({ source: records,width: 300, height: 250, theme: 'energyblue', hasThreeStates: true, checkboxes: true});
         
         $('#jqxTree').on('checkChange', function (event) {
			 var skillTypes = $('#jqxTree').jqxTree('getCheckedItems');
			 var strDisplay = "";
			 if(skillTypes.length){
				 for(var i = 0; i < skillTypes.length - 1; i++){				 
					 strDisplay += skillTypes[i].label + ", ";
				 }
				 strDisplay += skillTypes[skillTypes.length - 1].label;
			 }
             var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + strDisplay + '</div>';
             $("#dropDownButtonSkillType").jqxDropDownButton('setContent', dropDownContent);  
         });	         
	});
</script>
<div id="alterpopupWindowSkillType">
	<div id="windowHeader">
        <span>
           ${uiLabelMap.AddTrainingSkillType}
        </span>
    </div>
	<div style="overflow: hidden;" id="windowContent">
		<form class="form-horizontal" action="" method="post">
			<div class="control-group">
				<label class="control-label">
					${uiLabelMap.TrainingSkillType}
				</label>
				<div class="controls">
					<div id="dropDownButtonSkillType">
						<div style="border: none;" id='jqxTree'>
			                
			            </div>
					</div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">
					${uiLabelMap.RequrimentLevelSkillTrainingCourse}
				</label>
				<div class="controls">
					 <div id='requirementLevel'>	
        			</div>
				</div>
			</div>
			<div class="control-group">
				<label class="label-control">&nbsp;</label>
				<div class="controls">
					<input type="button" value="${uiLabelMap.CommonSubmit}" id='submitSkillType' />
					<input type="button" value="${uiLabelMap.CommonCancel}" id='cancelSkillType' />
				</div>
			</div>
		</form>
	</div>
</div>

<div id="appendContainerNotifi" style="width: inherit; max-width: 100%; overflow: hidden; margin-left: 1%">
<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" 
		 filterable="true" alternativeAddPopup="alterpopupWindowSkillType" deleterow="true" editable="true" bindresize="false" width="'99%'" addrow="true"
		 url="jqxGeneralServicer?trainingCourseId=${parameters.trainingCourseId}&hasrequest=Y&sname=JQListTrainingCourseSkillType" id="jqxGridSkillType"
		 jqGridMinimumLibEnable="false" 
		 removeUrl="" deleteColumn=""
		 updateUrl="" 
		 editColumns=""
	/>
	 <div id="messageNotification">
        <span id="notificationResults">
            
        </span>
    </div>
</div>