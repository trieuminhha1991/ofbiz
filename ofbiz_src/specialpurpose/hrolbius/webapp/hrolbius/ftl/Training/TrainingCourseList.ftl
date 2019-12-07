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
	<div class="row-fluid">
		<div id="appendNotification">
			<div id="createNotification">
				<span id="notificationText"></span>
			</div>
		</div>
	</div>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"  
		 filterable="true" alternativeAddPopup="alterpopupNewTraining" deleterow="false" editable="true" addrow="true"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQListTrainingCourse" id="jqxgrid" removeUrl="" deleteColumn=""
		 updateUrl="" mouseRightMenu="true" contextMenuId="contextMenu" jqGridMinimumLibEnable="false"
		 editColumns=""
	/>
	<script type="text/javascript">
		function afterAddrow(){
			$("#jqxgrid").jqxGrid("updatebounddata");
		}
	</script>
	<style>
		.trainingAmount .control-group .controls{
			margin-left: 179px !important
		}
		.trainingAmount .control-group .control-label{
			width: 139px !important;
		}
	</style>
	<!-- ========================notify after create training cours====================== -->
	
	<!-- ===============./end============================= -->
	
	<!-- =====================create new training course ================= -->
	<div class="row-fluid">
		<div class="span12">
			<div id="alterpopupNewTraining">
				<div id="windowHeaderNewTraing">
                    <span>
                       ${uiLabelMap.AddTrainingCourse}
                    </span>
                </div>
                <div style="overflow: hidden; padding: 15px" id="windowContentNewTraining">
                	<div id="jqxTabsNewTraining">
                		<ul>
			                <li>${uiLabelMap.TrainingCourseInfomation}</li>
			                <li>${uiLabelMap.TrainingCourseSkillType}</li>
			                <li>${uiLabelMap.TrainingCourseTrainees}</li>			                
			            </ul>
			            <div id="NewTrainingCourseInformation">
			            	<div class="basic-form form-horizontal" style="margin-top: 10px">
		                		<form name="createNewTrainingCourse" id="createNewTrainingCourse">	
			                		<div class="row-fluid" >
				                		<div class="span12">
				                			<div class="span6">
				                				<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.TrainingCourseId}</label>
													<div class="controls">
														<input type="text" name="trainingCourseId" id="trainingCourseId">
													</div>
												</div>
												<div class="control-group no-left-margin">
													<label class="control-label asterisk">${uiLabelMap.TrainingCourseName}</label>
													<div class="controls">
														<input type="text" name="trainingCourseName" id="trainingCourseName">
													</div>
												</div>
												<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.CommonDescription}</label>
													<div class="controls">
														<input type="text" name="description">
													</div>
												</div>
												
												<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.CommonEstimatedNumber}</label>
													<div class="controls">
														<div  id="EstimatedNumber"></div>
													</div>
												</div>
				                			</div>
				                			<div class="span6">
				                				<div class="control-group no-left-margin">
				                					<label class="control-label">${uiLabelMap.TrainingTypeId}</label>
				                					<div class="controls">
				                						<select name="trainingTypeId" id="trainingTypeId">
					                						<#list trainingTypeList as tempType>
					                							<option value="${tempType.trainingTypeId}">${tempType.description?if_exists}</option>
					                						</#list>
				                						</select>
				                					</div>
				                				</div>
				                				<div class="control-group no-left-margin">
				                					<label class="control-label">${uiLabelMap.TrainingFormTypeId}</label>
				                					<div class="controls">
				                						<select name="trainingFormTypeId" id="trainingFormTypeId">
					                						<#list trainingFormTypeList as tempFormType>
					                							<option value="${tempFormType.trainingFormTypeId}">${tempFormType.description?if_exists}</option>
					                						</#list>
				                						</select>
				                					</div>
				                				</div>
				                				<div class="control-group no-left-margin">
													<label class="control-label">${uiLabelMap.TimeEstimated}</label>
													<div class="controls">
														<div id="dateTimeInput"></div>
													</div>
												</div>
				                				<div class="control-group no-left-margin">
				                					<label class="control-label">${uiLabelMap.TrainingLocation}</label>
				                					<div class="controls">
				                						<input type="text" name="trainingLocation">
				                					</div>
				                				</div>
				                				
				                			</div>
				                		</div>
			                		</div>
			                		<div class="row-fluid">
										<div class="span12 mgt20 trainingAmount">
			                				<div class="span6" style="padding-left: 9px">
			                					<div class="boder-all-profile">
				                					<span class="text-header">${uiLabelMap.CostEstimatedEmployee}</span>
				                					<div class="control-group no-left-margin">
				                						<label class="control-label" style="width: 139px !important">${uiLabelMap.AmountEstimatedEmplPaid}</label>
				                						<div class="controls" >
				                							<div id="AmountEstimatedEmplPaid"></div>
				                						</div>
				                					</div>
				                					<div class="control-group no-left-margin">
				                						<label class="control-label">${uiLabelMap.AmountCompanySupport}</label>
				                						<div class="controls">
				                							<div id="AmountCompanySupport"></div>
				                						</div>
				                					</div>
				                					<div class="control-group no-left-margin">
				                						<label class="control-label">${uiLabelMap.AmountTotal}</label>
				                						<div class="controls">
				                							<div id="AmountTotalPerEmpl"></div>
				                						</div>
				                					</div>
			                					</div>
			                				</div>
			                				<div class="span6" style="padding-right: 9px">
			                					<div class="boder-all-profile">
				                					<span class="text-header">${uiLabelMap.TotalAmountEstimated}</span>
				                					<div class="control-group no-left-margin">
				                						<label class="control-label">${uiLabelMap.AmountEstimatedEmplPaid}</label>
				                						<div class="controls">
					                						<div id="AmountTotalEmplPaid"></div>
					                					</div>
				                					</div>
				                					<div class="control-group no-left-margin">
				                						<label class="control-label">${uiLabelMap.AmountCompanySupport}</label>
				                						<div class="controls">
				                							<div id="AmountTotalCompanySupport"></div>
				                						</div>
				                					</div>
				                					<div class="control-group no-left-margin">
				                						<label class="control-label">${uiLabelMap.AmountTotal}</label>
				                						<div class="controls">
				                							<div id="AmountTotal"></div>
				                						</div>
				                					</div>
				                					
			                					</div>
			                				</div>
			                				<#--<!-- <div class="row-fluid">
			                					<div class="span12" style="margin-top: 20px">
			                						<div class="span5"></div>
				                					<div class="span7">
				                						<button type="button" id='submitTrainingCourse' class="btn btn-mini btn-primary icon-ok">${uiLabelMap.CommonSubmit}</button>
				                						<button type="button" id='cancelTrainingCourse' class="btn btn-mini btn-danger icon-remove">${uiLabelMap.CommonCancel}</button>
				                					</div>
			                					</div>
			                				</div> -->
			                			</div>	                		
			                		</div>
			                			
		                		</form>
		                	</div>
		                	<div class="row-fluid" style="margin-top: 5px">
			                	<div class="span12" style="text-align: right;">
			                		<button type="button" style="margin-right: 15px" class="btn btn-mini btn-primary next" >${uiLabelMap.CommonNext} <i class="icon-arrow-right"></i></button>
			                	</div>
		                	</div>
			            </div>
			            <div id="NewTrainingCourseSkillType">
			            	<div class="row-fluid">
			            		<div class="span12" style="padding: 5px">
				            		<div id="splitterNewTraining" style="margin-top: 10px; overflow: hidden;">
										<div>
											<div style="border: none;" id="jqxTreeSkillTypeNewTraining"></div>
										</div>
										<div>
							               <div class="" style="overflow: auto;">
							                   <div class="jqx-hideborder" id="feedListSkillType" >
							                   	
							                   </div>
							               </div>
							        	</div>
									</div>
								</div>
							</div>	
							<div class="row-fluid">
				            	<div class="span12" style="text-align: right; margin-top: 10px; padding-right: 18px">
				            		<button type="button" class="btn btn-mini btn-success back" ><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
			                		<button type="button" class="btn btn-mini btn-primary next" >${uiLabelMap.CommonNext}&nbsp;<i class="icon-arrow-right"></i></button>
			                	</div>
		                	</div>
			            </div>
			            <div id="NewTrainingCourseTrainees" style="overflow-x: hidden;">
			            	<div class="row-fluid">
			            		<div class="span12">
				            		<form class="form-horizontal">
					            		<div class="control-group">
											<label class="control-label">${uiLabelMap.EmplPositionTypeTrained}</label>
											<div class="controls">
												<div id="emplPosTypeDropDownListNewTraining">
							            		</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">${uiLabelMap.PartyGroupTrained}</label>
											<div class="controls">
												<div id='jqxTreeTraineeNewTraining'>
									            </div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">${uiLabelMap.CommonIsCompulsory}</label>
											<div class="controls">
												<div id='IsCompulsoryCheckBoxNewTrainig'></div>
											</div>
										</div>
		               				</form>
	               				</div>
	                		</div>
	                		<div class="row-fluid">
				            	<div class="span12" style="text-align: right; margin-top: 10px; padding-right: 36px">
				            		<button type="button" class="btn btn-mini btn-success back" ><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
			               			<button type="button" id='submitTrainingCourse' class="btn btn-mini btn-primary">${uiLabelMap.CommonCreate}</button>
			                	</div>
		                	</div>
			            </div>	
                	</div>
                </div>
			</div>	
		</div>
	</div>
	<!-- ===================== ./ end create new training course ================= -->
	<div class="row-fluid">
		<div class="span12">
			<div id="trainingCourseDetailWindow">
				<div id="windowHeader">
                    <span>
                       ${uiLabelMap.TrainingCourseDetail}
                    </span>
                </div>																																																																							
                <div style="overflow: hidden; padding: 15px" id="windowContent">
                	 <div id="jqxTabs">
                	 	<ul>
			                <li>${uiLabelMap.TrainingCourseInfomation}</li>
			                <li>${uiLabelMap.TrainingCourseSkillType}</li>
			                <li>${uiLabelMap.TrainingCourseTrainees}</li>			                
			            </ul>
			            <div id="TrainingCourseInformation">
			            	<!-- <img id="Loading" src="/delys/images/css/import/ajax-loader.gif" alt="Loading" width="50px" height="50px" style="text-align: center; "> -->
			            </div>
			            <div id="TrainingCourseSkillType" style="overflow: hidden;">
			            	
			            </div>
			            <div id="TrainingCourseTrainees" style="overflow-x: hidden;">
			            	
			            </div>																																																						  
                	 </div>
                </div>	
			</div>
		</div>
	</div>
	<div id='contextMenu'>
        <ul>
            <li action="attendanceList">
            	${uiLabelMap.EmplListAttendanceTrainingCourse}
            </li>
            <li action="registerList">
            	${uiLabelMap.EmplListRegisterTrainingCourse}
            </li>
       </ul>
    </div>
  
<script type="text/javascript">
	$(document).ready(function () {
		$.jqx.theme = 'olbius';  
		var trainingCourseId;
		theme = $.jqx.theme;   
		//$("#submitTrainingCourse").jqxButton({ width: '150', theme: theme});
		/* $("#cancelTrainingCourse").jqxButton({ width: '150', theme: theme}); */
		$("#dateTimeInput").jqxDateTimeInput({ width: 220, height: 30,  selectionMode: 'range' });
		
		jQuery("#EstimatedNumber").jqxNumberInput({ width: '220', height: '25px', min: 0,  spinButtons: false, digits: 6, decimalDigits:0 });
		jQuery("#AmountEstimatedEmplPaid").jqxNumberInput({ width: '200px', height: '25px', min: 0,  spinButtons: false,decimalDigits: 2, digits: 9, max: 999999999 });
		jQuery("#AmountCompanySupport").jqxNumberInput({ width: '200px', height: '25px',  min: 0,  spinButtons: false , decimalDigits: 2, digits: 9, max: 999999999});
		jQuery("#AmountTotalPerEmpl").jqxNumberInput({ width: '200px', height: '25px', min: 0,  spinButtons: false, decimalDigits: 2 , digits: 9, disabled: true , max: 999999999});
		jQuery("#AmountTotalEmplPaid").jqxNumberInput({ width: '200px', height: '25px',min: 0,  spinButtons: false, decimalDigits: 2 , digits: 9, disabled: true, max: 999999999});
		jQuery("#AmountTotalCompanySupport").jqxNumberInput({ width: '200px', height: '25px',  min: 0,  spinButtons: false, decimalDigits: 2, digits: 9 , disabled: true, max: 999999999});
		jQuery("#AmountTotal").jqxNumberInput({ width: '200px', height: '25px',  min: 0,  spinButtons: false, decimalDigits: 2, digits: 9 , disabled: true, max: 999999999});
		
		jQuery(".next").click(function(){
			
			if(!jQuery('#createNewTrainingCourse').valid()){
				return false;
			}
			jQuery("#jqxTabsNewTraining").jqxTabs('enableAt', 1);
        	jQuery("#jqxTabsNewTraining").jqxTabs('enableAt', 2);
			jQuery("#jqxTabsNewTraining").jqxTabs('next');
		});	
		jQuery(".back").click(function(){
			$('#jqxTabsNewTraining').jqxTabs('previous');
		}); 
		 jQuery("#AmountCompanySupport, #EstimatedNumber, #AmountEstimatedEmplPaid").on('valueChanged', function(event){
			var estimatedEmplPaid = jQuery("#AmountEstimatedEmplPaid").val();
			var amountCompanySupport = jQuery("#AmountCompanySupport").val();
			var estimatedNumber = jQuery("#EstimatedNumber").val(); 
			calculateAmountTraining(estimatedEmplPaid, amountCompanySupport, estimatedNumber);
		});
		var validator = $('#createNewTrainingCourse').validate({
				errorElement: 'span',
				errorClass: 'help-inline',
				focusInvalid: false,
				rules: {
					trainingCourseName:{
						required: true,
					}
				},

				messages: {
					trainingCourseName: {
						required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
					},
					
				},
				invalidHandler: function (event, validator) { //display error alert on form submit   
					$('.alert-error', $('.login-form')).show();
				},

				highlight: function (e) {
					$(e).closest('.control-group').removeClass('info').addClass('error');
				},

				success: function (e) {
					$(e).closest('.control-group').removeClass('error').addClass('info');
					$(e).remove();
				},

				submitHandler: function (form) {
					form.submit();
				},
				invalidHandler: function (form) {
				}
			});
		
		/* ================= ./end create new training course window ===================*/
		
		/* ===================== add skill type to new training course =======================*/
			var dataSkillTypeNewTraining = new Array();
			<#list listSkillTypeTree as tree>
				var row = {};
				row["id"] = "${tree.id}_newTraining";
				row["text"] = "${tree.text}";
				row["parentId"] = "${tree.parentId}_newTraining";
				row["value"] = "${tree.id}";
				dataSkillTypeNewTraining[${tree_index}] = row;
			</#list>
			 var source1 =
	         {
	             datatype: "json",
	             datafields: [
	                 { name: 'id' },
	                 { name: 'parentId' },
	                 { name: 'text' } ,
	                 {name: 'value'}
	             ],
	             id: 'id',
	             localdata: dataSkillTypeNewTraining
	         };
			 
			 var dataAdapter1 = new $.jqx.dataAdapter(source1);
	         // perform Data Binding.
	         dataAdapter1.dataBind();
	         // get the tree items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents 
	         // the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter 
	         // specifies the mapping between the 'text' and 'label' fields.  
	         var records1 = dataAdapter1.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
	         
			 //$("#jqxTree").jqxTree({ width: 300, height: 170, theme: 'energyblue', hasThreeStates: true, checkboxes: true});
			 
	         $('#jqxTreeSkillTypeNewTraining').jqxTree({ source: records1,width: 320, height: 380, theme: 'energyblue', hasThreeStates: false, checkboxes: true});
	         $("#splitterNewTraining").jqxSplitter({  width: '100%', height: '380', panels: [{ size: '320'}],theme: 'olbius' });
	         
	        $('#jqxTreeSkillTypeNewTraining').on('checkChange', function (event) {
	        	var args = event.args;
	            var element = args.element;
	            var checked = args.checked;	            
	            if(checked){
	            	var item = $('#jqxTreeSkillTypeNewTraining').jqxTree('getItem', $("#"+element.id)[0]);
	            	$("#feedListSkillType").jqxGrid('addrow', element.id, {skillTypeId: item.value,parentId: item.parentId, text: item.label});
	            }else{
	            	$("#feedListSkillType").jqxGrid('deleterow', element.id);
	            }              
	         });
	         
	         var dataListSkillType = new Array();
	         var sourceListSkillType = {
	        	localdata: dataListSkillType, 	 
	        	datatype: "array",
	        	datafields:[
	                    { name: 'skillTypeId', type: 'string' },
	                    { name: 'parentId', type: 'string' },
	                    { name: 'text', type: 'string' },	                   
	                ],
	         };
	         var dataAdapterSkillTypeList = new $.jqx.dataAdapter(sourceListSkillType);
	         $("#feedListSkillType").jqxGrid(
             {
                 width: '100%',
                 height: '50%',
                 source: dataAdapterSkillTypeList,
                 showfilterrow: false,
                 filterable: false,
                 selectionmode: 'singlerow',
                 columns: [
                   { text: '${uiLabelMap.SkillTypeId}',  datafield: 'skillTypeId', width: '40%' },
                   {text: '${uiLabelMap.SkillTypeCategory}', datafield: 'parentId', width: '35%', hidden: true},
                   { text: '${uiLabelMap.CommonDescription}', datafield: 'text', width: '60%', cellsalign: 'left'},
                 ]
             });
	         
	   /*======================./end =========================================*/
	
	   /*=====================add party to training course==========*/
		var data2 = new Array();
		<#list treePartyGroup as tree>
			var row = {};
			row["id"] = "${tree.id}_newTraining";
			row["text"] = "${tree.text}";
			row["parentId"] = "${tree.parentId}_newTraining";
			row["value"] = "${tree.id}";
			data2[${tree_index}] = row;
		</#list>
		 var source2 =
         {
             datatype: "json",
             datafields: [
                 { name: 'id' },
                 { name: 'parentId' },
                 { name: 'text' },
                 {name: 'value'}
             ],
             id: 'id',
             localdata: data2
         };
		 var dataAdapter2 = new $.jqx.dataAdapter(source2);
         // perform Data Binding.
         dataAdapter2.dataBind();
         // get the tree items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents 
         // the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter 
         // specifies the mapping between the 'text' and 'label' fields.  
         var records2 = dataAdapter2.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
         //$("#dropDownButtonpartyTrainee").jqxDropDownButton({ width: 500, height: '25px', theme: theme});
         $('#jqxTreeTraineeNewTraining').jqxTree({ source: records2, width: "95%", height: 310, theme: 'energyblue', hasThreeStates: true, checkboxes: true});
         <#if expandedList?has_content>
         	<#list expandedList as expandId>
         		$('#jqxTreeTraineeNewTraining').jqxTree('expandItem', $("#${expandId}_newTraining")[0]);
         	</#list>
         </#if>
         $('#jqxTreeTraineeNewTraining').on('select',function (event){
        	 //console.log(event.args);
        	 $("#jqxTreeTraineeNewTraining").jqxTree('checkItem', $("#" + event.args.element.id)[0], true);
         });
         $('#IsCompulsoryCheckBoxNewTrainig').jqxCheckBox({ width: '200px', height: '25px', checked: false, theme:'olbius'});
         
		
 	    /*=====================./end====================================*/
		
 	    /*==========================emplPositonType dropdown list==============================*/
		var emplPosTypeArr1 = new Array();	
		 <#list emplPosType as posType>
			var row = {};
			row["emplPositionTypeId"] = "${posType.emplPositionTypeId}";
			row["description"] = "${StringUtil.wrapString(posType.description)}";
			emplPosTypeArr1[${posType_index}] = row;
		 </#list>
		
		 var emplPosSource1 = {
		        localdata: emplPosTypeArr1,
		        datatype: "array"
		};
	    var filterBoxAdapter1 = new $.jqx.dataAdapter(emplPosSource1, {autoBind: true});
	    var dataSoureList1 = filterBoxAdapter1.records;
	    $("#emplPosTypeDropDownListNewTraining").jqxComboBox({source: dataSoureList1, placeHolder:'', displayMember: 'description',dropDownHeight:'200px',  
	    	valueMember : 'emplPositionTypeId', height: '25px', width: '94%',checkboxes: false, theme: 'olbius',multiSelect: true,  
			renderer: function (index, label, value) {
				for(i=0; i < emplPosTypeArr1.length; i++){
					if(emplPosTypeArr1[i].emplPositionTypeId == value){
						return emplPosTypeArr1[i].description;
					}
				}
			    return value;
			}
		});
	    /*==========================./end emplPositonType dropdown list==============================*/
	    
 	    /* ================= create new training course window ===================*/
 	    
		$("#alterpopupNewTraining").jqxWindow({
            showCollapseButton: false, maxHeight: 550, autoOpen: false, maxWidth: "80%", minHeight: 550, minWidth: '80%', height: 550, width: "80%", isModal: true, 
            theme:theme, collapsed:false,
            initContent: function () {
            	jQuery("#jqxTabsNewTraining").jqxTabs({ width: "100%", height: "100%"});
            	jQuery("#jqxTabsNewTraining").jqxTabs('disableAt', 1);
            	jQuery("#jqxTabsNewTraining").jqxTabs('disableAt', 2); 
            }
        });
 	    
 	    /*============================================ ./end  ========================================================*/
 	    $('#trainingCourseDetailWindow').jqxWindow({
            showCollapseButton: false, maxHeight: 500, autoOpen: false, maxWidth: "75%", minHeight: 500, minWidth: 800, height: "75%", width: "75%", isModal: true, 
            theme:theme, collapsed:false,
            initContent: function () {
            	jQuery("#jqxTabs").jqxTabs({ width: "100%", height: "100%"});
                //jQuery('#trainingCourseDetailWindow').jqxWindow('focus');
                //jQuery('#trainingCourseDetailWindow').jqxWindow('close');
            }
        });
		
		/* $('#trainingCourseDetailWindow').on("open", function(event){
			//$('#jqxTabs').jqxTabs({ selectedItem: 2 });
		}); */
		$('#jqxTabs').on('selected', function (event) {
            var pageIndex = event.args.item;
            if(trainingCourseId){
            	//console.log(trainingCourseId);
            	if(pageIndex == 0){
                	loadContent('<@ofbizUrl>getTrainingCourseInfo</@ofbizUrl>', 'TrainingCourseInformation', trainingCourseId);
                }else if(pageIndex == 1){
                	loadContent('<@ofbizUrl>getTrainingCourseSkillType</@ofbizUrl>', 'TrainingCourseSkillType', trainingCourseId);
                }else if(pageIndex == 2){
                	loadContent('<@ofbizUrl>getTrainingCourseTrainees</@ofbizUrl>', 'TrainingCourseTrainees', trainingCourseId);
                }
            }
        });
		
		jQuery("#jqxgrid").on("rowDoubleClick", function(event){
			var args = event.args;
			var data = $('#jqxgrid').jqxGrid('getrowdata', args.rowindex);
			trainingCourseId = data["trainingCourseId"];
			//console.log(trainingCourseId);
			$('#jqxTabs').jqxTabs({ selectedItem: 0});
			$('#trainingCourseDetailWindow').jqxWindow("open");	
			loadContent('<@ofbizUrl>getTrainingCourseInfo</@ofbizUrl>', 'TrainingCourseInformation', trainingCourseId);
		});	
		
		$("#contextMenu").jqxMenu({ width: 250, height: 60, autoOpenPopup: false, mode: 'popup'/* , theme:'energyblue' */});
		$("#contextMenu").on('itemclick', function (event) {
			 var args = event.args;
             var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
             var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
             var trainingCourseId = dataRecord.trainingCourseId;
             var statusId = dataRecord.statusId;
             if(statusId == 'TRAINING_PLANNED_ACC'){
            	 if($(args).attr("action") == 'attendanceList'){
                   	 window.open("<@ofbizUrl>EmplAttendanceTrainCourse?trainingCourseId="+ trainingCourseId +"</@ofbizUrl>") 
                 }else{
                	 window.open("<@ofbizUrl>EmplRegisterTrainCourse?trainingCourseId="+ trainingCourseId +"</@ofbizUrl>") 
                 }	 
             }else{
            	bootbox.alert({
           			size: 'small',
           		    message: "${uiLabelMap.EmplListExistsForTrainingCourseAccepted}", 
           		    callback: function(){ /* your callback code */ }
            	 });            	 
             }
             //
             
		});
		/* jQuery("#cancelTrainingCourse").click(function(){
			jQuery("#alterpopupNewTraining").jqxWindow("close");
		}); */
		
		jQuery("#submitTrainingCourse").click(function(){
			var formData = jQuery("#createNewTrainingCourse").serializeArray();
			formData.push({name: "estimatedEmplPaid", value: jQuery("#AmountEstimatedEmplPaid").val()});
			formData.push({name: "amountCompanySupport", value: jQuery("#AmountCompanySupport").val()}) ;
			formData.push({name: "estimatedNumber", value: jQuery("#EstimatedNumber").val()});
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			formData.push({name: "estimatedFromDateLong", value: selection.from.getTime()});
			formData.push({name: "estimatedThruDateLong", value: selection.to.getTime()});
			var skillTypeChecked= jQuery("#jqxTreeSkillTypeNewTraining").jqxTree('getCheckedItems');
			var traineeChecked = jQuery("#jqxTreeTraineeNewTraining").jqxTree('getCheckedItems');
			var emplPosTypeChoose = jQuery("#emplPosTypeDropDownListNewTraining").jqxComboBox('getSelectedItems');
			var checked = $("#IsCompulsoryCheckBoxNewTrainig").jqxCheckBox('checked');
			var skillTypeArr = new Array();
			for(var i = 0; i < skillTypeChecked.length; i++){
				skillTypeArr.push({skillTypeId: skillTypeChecked[i].value});
			}
			var traineeArr = new Array();
			for(var i = 0; i < traineeChecked.length; i++){
				traineeArr.push({partyId: traineeChecked[i].value});
			}
			
			var emplPosTypeArr = new Array();
			for(var i = 0; i < emplPosTypeChoose.length; i++){
				emplPosTypeArr.push({emplPositionTypeId: emplPosTypeChoose[i].value});
			}
			formData.push({name: "emplPositionTypes", value: JSON.stringify(emplPosTypeArr)});
			formData.push({name: "skillTypes", value: JSON.stringify(skillTypeArr)});
			formData.push({name: "partyIds", value: JSON.stringify(traineeArr)});
			if(checked){
				formData.push({name: "isCompulsory", value: "Y"});
			}
			jQuery.ajax({
				url: "<@ofbizUrl>createTrainingCourse</@ofbizUrl>",
				type: 'POST',
				data: formData,
				success: function(data){
					//console.log(data);
					if(data._EVENT_MESSAGE_){
						jQuery("#alterpopupNewTraining").jqxWindow("close");
						$('#notificationText').html(data._EVENT_MESSAGE_);
						jQuery("#createNotification").jqxNotification('open');
						jQuery("#jqxgrid").jqxGrid('updatebounddata');
					}else{
						$('#notificationText').html(data._ERROR_MESSAGE_);
						jQuery("#createNotification").jqxNotification('open');												
					}
					$("#alterpopupNewTraining").jqxWindow('close');
				}, 
				complete: function(){
					//console.log("update");
					
				}
			});
		});
		
		$("#createNotification").jqxNotification({
	         width: "100%", position: "top-left", opacity: 1, appendContainer: "#appendNotification",
	         autoOpen: false, animationOpenDelay: 800, autoClose: false, template: 'info'
	     });
	});		
	
	function calculateAmountTraining(estimatedEmplPaid, amountCompanySupport, estimatedNumber){
		
		if(!estimatedEmplPaid){
			estimatedEmplPaid = 0;
		}
		if(!amountCompanySupport){
			amountCompanySupport = 0;
		}
		if(!estimatedNumber){
			estimatedNumber = 0;
		}
		//console.log(estimatedNumber);
		var amountTotalPerEmpl = estimatedEmplPaid + amountCompanySupport;
		var amountTotalEmplPaid = estimatedNumber * estimatedEmplPaid;
		var amountTotalCompanySupport = estimatedNumber * amountCompanySupport;
		var amountTotal = amountTotalEmplPaid + amountTotalCompanySupport;
		jQuery("#AmountTotalPerEmpl").val(amountTotalPerEmpl);
		jQuery("#AmountTotalEmplPaid").val(amountTotalEmplPaid);
		jQuery("#AmountTotalCompanySupport").val(amountTotalCompanySupport);
		jQuery("#AmountTotal").val(amountTotal);
	}
	
	function loadContent(url, divId, trainingCourseId){
		//console.log(url);
		jQuery.ajax({
			url: url,
			type: "POST",
			data: {trainingCourseId: trainingCourseId},			
			success: function(data){
				//console.log(data);
				jQuery("#" + divId).html(data);
			}
		});
	}
</script>
