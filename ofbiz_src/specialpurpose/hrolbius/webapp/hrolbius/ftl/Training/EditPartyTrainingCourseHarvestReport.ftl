<div class="row-fluid">
	<div class="span12" >
		<div class="span6" style="text-align: right;">
			<h4>${uiLabelMap.TrainingCourseName}: </h4>
		</div>	
		<div class="span6" style="text-align: left;">
			<h4><b>${trainingCourse.trainingCourseName?if_exists}</b></h4>
		</div>
		
	</div>
	<div class="basic-form form-horizontal">
		<div class="span12">
			<div class="span6">
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.HRReporterName}</label>
					<div class="controls">
						${employee.firstName?if_exists} ${employee.middleName?if_exists} ${employee.lastName?if_exists}
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.HREmplPositionType}</label>
					<div class="controls">
						${currPositionsStr}
					</div>
				</div>
			</div>
			
			<div class="span6">
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.PartyIdWork}</label>
					<div class="controls">
						${currDept}
					</div>
				</div>	
			</div>
		</div>	
		<hr/>
		
		<div class="span12">
			<h5 style="margin-bottom: 0"><b>${uiLabelMap.HROlbiusTrainingContent}</b></h5>
			<#if trainingCoursePurpose?has_content>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.TrainingCoursePurpose}</label>
					<div class="controls">
						<#list trainingCoursePurpose as tempPurpose>
							<#assign purpose = delegator.findOne("TrainingPurposeType", Static["org.ofbiz.base.util.UtilMisc"].toMap("trainingPurposeTypeId", tempPurpose.trainingPurposeTypeId), false)>
							${purpose.description?if_exists}
							<#if tempPurpose_has_next>
								,&nbsp;
							</#if>
						</#list>
					</div>
				</div>
				
			</#if>
			<#if trainingCourseSkillType?has_content>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.SkillTypeTraining}</label>
					<div class="controls">
						<#list trainingCourseSkillType as tempSkillType>
							<#assign skillType = delegator.findOne("SkillType", Static["org.ofbiz.base.util.UtilMisc"].toMap("skillTypeId", tempSkillType.skillTypeId), false)>
							${skillType.description}
							<#if tempSkillType_has_next>
								,&nbsp;
							</#if>
						</#list>
					</div>
				</div>
			</#if>
		</div>
		<div class="span12" style="margin-top: 10px">
			<div class="span3">
				<h5><b>${uiLabelMap.HROlbiusAccompanyingDocumentation}</b></h5>
			</div>
			<div class="span9" style="text-align: left; margin-top: 10px">
				<div class="row-fluid">
					<div class="span12">
						<form class="no-margin" action="" class="row-fluid" id="upLoadFileForm"  method="post" enctype="multipart/form-data">
							<input type="hidden" name="trainingCourseId" value="${parameters.trainingCourseId}">
							<input type="hidden" name="partyId" value="${parameters.partyId}">
							<input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />
							<input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />
							<div class="span6">
					 			<input type="file" id="uploadedFile" name="uploadedFile"/>
					 		</div>
					 		<div class="span3" style="margin-top: 1px">
					 			<button type="button" class="btn btn-mini btn-primary" id="submitFile"><i class="icon-save"></i> ${uiLabelMap.CommonSave}</button>
					 		</div>
					 	</form>
					</div>
					<div id="listDocument">
						<ul class="unstyled" style="margin-left: 15px">
							<#list listTrainingCourseDocument as trainingCourseDocument>
								<#assign dataResource = delegator.findOne("DataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("dataResourceId", trainingCourseDocument.dataResourceId), false)>
								<li><i class="icon-ok green"></i>
									<a href="${dataResource.objectInfo}">${trainingCourseDocument.contentName?if_exists}</a>
								</li>
							</#list>
						</ul>
					</div>
					
			 	</div>
			 </div>
		</div>
		<div class="span12">
			<form action="<@ofbizUrl>updateTrainingCoursePartyAttend</@ofbizUrl>" method="post" id="updateTrainingCoursePartyAttend" name="updateTrainingCoursePartyAttend">
				<h5 style=""><b>${uiLabelMap.CommentAndProposal}</b></h5>
				<textarea class="note-area no-resize" id="comments" name="comment" autocomplete="off"></textarea>
				<input type="hidden" name="trainingCourseId" value="${parameters.trainingCourseId}">
				<input type="hidden" name="partyId" value="${parameters.partyId}">
			</form>
		</div>
		<div class="span12">
			<div class="control-group no-left-margin">	
				<label class="control-label">&nbsp;</label>
				<div class="controls">
					<input type="button" value="${uiLabelMap.CommonSubmit}" id='submitForm'/>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
			var ck = CKEDITOR.instances;
			function setDataEditor(key, content) {
		       	if (ck[key]) {
		       		return ck[key].setData(content);
		       	}
		    }
			
		 $(document).ready(function () {
			var editor = CKEDITOR.replace('comments', {
			    height: '120',
			    width: '96%',
			    skin: 'office2013'
			});
			<#if trainingCoursePartyAttendance.comment?has_content>
				setDataEditor('comments', "${StringUtil.wrapString(trainingCoursePartyAttendance.comment.trim())}");
			</#if>
			$("#submitForm").jqxButton({ width: '150', theme: theme});
			
			$("#submitForm").click(function(){
				jQuery("#updateTrainingCoursePartyAttend").submit();
			});
			
			
		     
			$("#submitFile").click(function(){
				var form = jQuery("#upLoadFileForm");
				var file = form.find('input[type=file]').eq(0);		
				if(!file.data('ace_input_files')) return false;
				
				var fileUpload = $('#uploadedFile')[0].files[0];
				jQuery("#_uploadedFile_fileName").val(fileUpload.name);
				jQuery("#_uploadedFile_contentType").val(fileUpload.type);
				var formData = new FormData(jQuery('#upLoadFileForm')[0]);
				
				jQuery.ajax({
			        url: 'uploadTrainingCourseDocument',
			        type: 'POST',
			        data: formData,
			        cache: false,			        
			        processData: false, // Don't process the files
			        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
			        success: function(data, textStatus, jqXHR){	        	
			        	//console.log("1111111" , data);
			        	jQuery("#listDocument").html(data);
			        },
			        error: function(jqXHR, textStatus, errorThrown)
			        {
			        	//console.log("error" , errorThrown);
			        }
				 }); 
			});
			
		  	$('#uploadedFile').ace_file_input({
		  		no_file:'No File ...',
				btn_choose:'${uiLabelMap.CommonChooseFile}',
				droppable:false,
				onchange:null,
				thumbnail:false,			
				//,icon_remove:null//set null, to hide remove/reset button
				/**,before_change:function(files, dropped) {
					//Check an example below
					//or examples/file-upload.html
					return true;
				}*/
				/**,before_remove : function() {
					return true;
				}*/
				preview_error : function(filename, error_code) {
					//name of the file that failed
					//error_code values
					//1 = 'FILE_LOAD_FAILED',
					//2 = 'IMAGE_LOAD_FAILED',
					//3 = 'THUMBNAIL_FAILED'
					//alert(error_code);
				}
		
			}).on('change', function(){
				//console.log($(this).data('ace_input_files'));
				//console.log($(this).data('ace_input_method'));
			});
		 });
	</script>
</div>