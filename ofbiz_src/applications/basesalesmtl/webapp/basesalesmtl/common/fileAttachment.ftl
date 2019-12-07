<script src="/salesmtlresources/js/common/fileAttachment.js"></script>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<style>
	#contentViewerFile .img-container {
		padding: 10px;
		border: 1px solid #ccc;
	}
</style>

<div id="jqxNotificationUploader">
<div id="notificationContentUploader"></div>
</div>

<div id="jqxwindowUploadFile" style="display:none;">
<div>${uiLabelMap.UploadFileAttachment}</div>
<div>
	<div class="row-fluid">
		<div class="span12" style="overflow-y: hidden;overflow-y: auto;">
			<input multiple type="file" id="id-input-file-3" accept="${acceptFile?if_exists}"/>
		</div>
	</div>
	<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity:0.2;">
	<div class="form-action">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id='cancelUpload' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button id='btnUploadFile' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
</div>

<div id="jqxwindowViewFile" style="display:none;">
<div>${uiLabelMap.FileAttachment}</div>
<div>
	<div class="row-fluid">
		<div class="span12" style="overflow-y: auto;" id="contentViewerFile">
		</div>
	</div>
	<div class="form-action">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id='cancelViewer' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonClose}</button>
			</div>
		</div>
	</div>
</div>
</div>

<script>
var ChooseImagesToUpload = "${StringUtil.wrapString(uiLabelMap.ChooseImagesToUpload)}";
var uploadSuccessfully = "${StringUtil.wrapString(uiLabelMap.uploadSuccessfully)}";
var entityName = "${entityName?if_exists}";
</script>