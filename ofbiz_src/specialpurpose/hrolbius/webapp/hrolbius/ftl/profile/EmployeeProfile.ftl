<div class="row-fluid">
	<ul class="nav nav-tabs" id="recent-tab">
	<#--<ul class="nav nav-tabs padding-18">-->
		<li class="active">
			<a data-toggle="tab" href="#personal-info" aria-expanded="true">
				<i class="black fa fa-user bigger-120"></i>
				${uiLabelMap.generalInfo}
			</a>
		</li>
		<li class="">
			<a data-toggle="tab" href="#familyMemberTab" aria-expanded="false">
				<i class="black ace-icon fa fa-home bigger-120"></i>
				${uiLabelMap.familyMemberInfo}
			</a>
		</li>

		<#--
		<li class="">
			<a data-toggle="tab" href="#attrTab" aria-expanded="false">
				<i class="black ace-icon fa fa-users bigger-120"></i>
				${uiLabelMap.featureInfo}
			</a>
		</li>
		-->

		<li class="">
			<a data-toggle="tab" href="#trainingInfoTab" aria-expanded="false">
				<i class="black ace-icon fa fa-folder-open-o bigger-120"></i>
				${uiLabelMap.trainingInfo}
			</a>
		</li>

		<li class="">
			<a data-toggle="tab" href="#workProcessInfoTab" aria-expanded="false">
				<i class="ace-icon fa fa-file bigger-120"></i>
				${uiLabelMap.workProcessInfo}
			</a>
		</li>
	</ul>
	<div class="tab-content overflow-visible" style="border: none !important">
			<#include "generalInfo.ftl" >
			<#include "familyInfo.ftl" >
			<#include "trainingInfo.ftl" >
			<#include "workProcess.ftl" >
			<#--
			<#include "attrInfo.ftl" >
			-->

	</div>
</div>
<script type="text/javascript">
	jQuery(document).ready(function(){
		$('#personal-image').on('click', function(){
			var modal = 
			'<div class="modal hide fade">\
				<div class="modal-header">\
					<button type="button" class="close" data-dismiss="modal">&times;</button>\
					<h4 class="blue">Change Avatar</h4>\
				</div>\
				\
				<form class="no-margin" action="" id="upLoadImageForm"  method="post" enctype="multipart/form-data">\
				<div class="modal-body">\
					<input type="hidden" name="partyId" value="${parameters.partyId}"/>\
					<input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />\
					<input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />\
					<div class="space-4"></div>\
					<div style="width:75%;margin-left:12%;"><input type="file" name="uploadedFile" id="uploadedFile" /></div>\
				</div>\
				\
				<div class="modal-footer" style="text-align:center">\
					<button type="button" class="btn btn-small btn-success" data-dismiss="modal" id="submitImage" onclick="uploadImage();"><i class="icon-ok"></i> Submit</button>\
					<button type="button" class="btn btn-small" data-dismiss="modal"><i class="icon-remove"></i> Cancel</button>\
				</div>\
				</form>\
			</div>';
			
			
			var modal = $(modal);
			modal.modal("show").on("hidden", function(){
				modal.remove();
			});
	
			var working = false;
	
			var form = modal.find('form:eq(0)');
			var file = form.find('input[type=file]').eq(0);
			file.ace_file_input({
				style:'well',
				btn_choose:'Click to choose new avatar',
				btn_change:null,
				no_icon:'icon-picture',
				thumbnail:'small',
				before_remove: function() {
					//don't remove/reset files while being uploaded
					return !working;
				},
				before_change: function(files, dropped) {
					var file = files[0];
					if(typeof file === "string") {
						//file is just a file name here (in browsers that don't support FileReader API)
						if(! (/\.(jpe?g|png|gif)$/i).test(file) ) return false;
					}
					else {//file is a File object
						var type = $.trim(file.type);
						if( ( type.length > 0 && ! (/^image\/(jpe?g|png|gif)$/i).test(type) )
								|| ( type.length == 0 && ! (/\.(jpe?g|png|gif)$/i).test(file.name) )//for android default browser!
							) return false;
	
						if( file.size > 2048000 ) {//~2Mb
							return false;
						}
					}
	
					return true;
				}
			});
		});		
	});
	
	function uploadImage(){
		var form = jQuery("#upLoadImageForm");
		var file = form.find('input[type=file]').eq(0);		
		if(!file.data('ace_input_files')) return false;
		
		var fileUpload = $('#uploadedFile')[0].files[0];
		jQuery("#_uploadedFile_fileName").val(fileUpload.name);
		jQuery("#_uploadedFile_contentType").val(fileUpload.type);
		var formData = new FormData(jQuery('#upLoadImageForm')[0]);
		jQuery.ajax({
	        url: '<@ofbizUrl>updatePersonalImage</@ofbizUrl>',
	        type: 'POST',
	        data: formData,
	        cache: false,
	        dataType: 'json',
	        processData: false, // Don't process the files
	        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
	        success: function(data, textStatus, jqXHR){	        	
	        	if(data._EVENT_MESSAGE_){					
					var urlImg = data.contentUrl;
					//console.log(urlImg);
					if(urlImg){
						jQuery("#personal-image").attr("src", urlImg);
					}else{
						bootbox.dialog({
							message: "${uiLabelMap.ErrorWhenUpdate}",
							title: "${uiLabelMap.ResultUpdate}",
							buttons:{
								main: {
									label: "OK!",
									className: "btn-small btn-danger"
								}
							}
						});
					}
				}else{
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenUpdate}",
						title: "${uiLabelMap.ResultUpdate}",
						buttons:{
							main: {
								label: "OK!",
								className: "btn-small btn-danger"
							}
						}
					});
				}
	        },
	        error: function(jqXHR, textStatus, errorThrown)
	        {
	            // Handle errors here
	            //console.log('ERRORS: ' + textStatus);
	            // STOP LOADING SPINNER
	        }
	    });
	}
</script>