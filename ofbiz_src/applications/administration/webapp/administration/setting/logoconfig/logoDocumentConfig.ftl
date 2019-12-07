
<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>
<div class="row-fluid">
	<div class="span4">
		<div id="logoDocumentContainer">
			${screens.render("component://administration/widget/SettingScreens.xml#LogoDocumentConfigInner")}
		</div>
	</div>
	<div class="span7">
		<h4>${uiLabelMap.BSChangeImage}</h4>
		<span class="help-inline" style="color: #657ba0; font-style: italic">
			<b>${uiLabelMap.BSNoteY}</b>: ${uiLabelMap.BSImageShouldHaveHeightIs2cm}
		</span>
		<form name="attachPaymentOrder" id="attachPaymentOrder">	
			<#--<input type="file" id="uploadedFile" name="uploadedFile[]" multiple="multiple"/>-->
			<input type="file" id="uploadedFilePaymentOrder"/>
		</form>
		
		<button id="btnAttachPaymentOrder" class="btn btn-small btn-primary">
 			<i class="fa-floppy-o open-sans"></i> ${uiLabelMap.CommonSave}
 		</button>
	</div>
	<div class="span1"></div>
</div>
<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script src="/aceadmin/assets/js/jquery.inputlimiter.1.3.1.min.js"></script>
<script src="/aceadmin/assets/js/jquery.maskedinput.min.js"></script>
<@jqOlbCoreLib />
<script type="text/javascript">
	var labelwgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
	var labelwgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
	$(function(){
		jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
		
		$('#uploadedFilePaymentOrder').ace_file_input({
			//style:'well',
			btn_choose: '${StringUtil.wrapString(uiLabelMap.BSChoose)}',
			btn_change: '${StringUtil.wrapString(uiLabelMap.BSChange)}',
			no_icon: 'icon-cloud-upload',
			droppable: false,
			thumbnail: 'small',
			preview_error : function(filename, error_code) {
				//name of the file that failed
				//error_code values
				//1 = 'FILE_LOAD_FAILED',
				//2 = 'IMAGE_LOAD_FAILED',
				//3 = 'THUMBNAIL_FAILED'
				//alert(error_code);
			}
		});
		
		$("#btnAttachPaymentOrder").on("click", function(){
			var fileList = $('#uploadedFilePaymentOrder').data('ace_input_files');
			if (fileList != undefined && fileList != null && fileList.length > 0) {
				var formData = new FormData();
				formData.append("uploadedFile", fileList[0]);
				$.ajax({
					type: 'POST',
					url: 'changeDocumentLogo',
					data: formData,
					cache : false,
					contentType : false,
					processData : false,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, "default", "default", function(){
							$("#logoDocumentContainer").html(data);
							
							$('#uploadedFilePaymentOrder').ace_file_input('reset_input');
						});
						<#--
						$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
			        	$("#jqxNotification").jqxNotification("open");
			        	if (data.resourceValue != undefined && data.resourceValue != null) {
			        		$("#logoDocument").attr("src", data.resourceValue);
			        	}
						-->
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			} else {
				OlbUtil.alert.error("You haven't choose file yet!");
			}
		});
	});
</script>