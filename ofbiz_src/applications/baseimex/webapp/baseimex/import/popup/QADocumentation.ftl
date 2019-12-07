<script>
	if (multiLang == undefined) var multiLang = {};
	multiLang.fieldRequired = "${StringUtil.wrapString(uiLabelMap.fieldRequired)}";
	multiLang.dateNotValid = "${StringUtil.wrapString(uiLabelMap.dateNotValid)}";
	
</script>
<div id="popupDocQA" class="hide popup-bound">
	<div id="headerDoc"></div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" id="documentCustomsId"/>
			<input type="hidden" id="containerCustomsId"/>
			<input type="hidden" id="documentCustomsTypeId"/>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right">${uiLabelMap.documentCustomsTypeId}</div>
	 			<div class="span8 green-label"><div id="customsTypeId"></div></div>
 			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk">${uiLabelMap.registerNumber}</div>
	 			<div class="span8"><input id="registerNumber"/></div>
			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk">${uiLabelMap.registerDate}</div>
    	 		<div class="span8"><div id="registerDate"></div></div>
			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk">${uiLabelMap.sampleSendDate}</div>
	 			<div class="span8"><div id="sampleSentDate"></div></div>
			</div>
	        <div class="form-action popup-footer">
		        <button id="cancelDoc" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="saveDocAndDownload" class='btn btn-primary form-action-button pull-right'><i class='icon-download-alt'></i> ${uiLabelMap.CommonSaveAndDownload}</button>
		    	<button id="saveDoc" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
        </div>
	</div>
</div>
<script type="text/javascript" src="/imexresources/js/import/QADocumentation.js"></script>