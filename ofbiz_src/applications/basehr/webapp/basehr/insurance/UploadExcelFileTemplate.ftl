<#include "script/UploadExcelFileTemplateScript.ftl"/>
<#assign datafield = "[
					   {name: 'insuranceContentTypeId', type: 'string'},
					   {name: 'description', type: 'string'},
					   ]"/>

<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.InsuranceTemplateType)}', datafield: 'description', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceTemplateContentName)}', datafield: 'insuranceContentTypeId', width: 230},
						
						" />
</script>
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>
<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist 
					 filterable="false" deleterow="false" editable="false" addrow="false"
					 url="jqxGeneralServicer?sname=JQGeInsuranceContentType" id="jqxgrid" jqGridMinimumLibEnable="false"
					 updateUrl="" addrefresh="true"
					 editColumns="" showlist="false"
					 selectionmode="singlerow" 
				/>
<#--<!-- <div class="row-fluid">
	<div id="popupAddRowWindow" class='hide'>
		<div id="popupWindowHeader">
			${StringUtil.wrapString(uiLabelMap.InsuranceAddNewTemplate)}
		</div>
		<div class="form-window-container">
			<div class="form-window-content" style="position: relative;">
				<form enctype="multipart/form-data" method="post" id="upLoadFileForm">
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label asterisk">${uiLabelMap.InsuranceTemplateType}</label>
						</div>
						<div class="span7">
							<div id="insuranceTemplateType"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label">${uiLabelMap.InsuranceFileTemplate}</label>
						</div>
						<div class="span7">
							<div class="row-fluid">
								<div class="span12" style="width: 96%">
									<input type="file" name="uploadedFile" id="uploadedFile">
								</div>
							</div>
						</div>
					</div>
				</form>
				<div class="row-fluid no-left-margin">
					<div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerAjax"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="uploadFileBtn">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div> -->
<#--<!-- <script type="text/javascript" src="/hrresources/js/insurance/UploadExcelFileTemplate.js"></script> -->
