<script src="/crmresources/js/crmsetting/addProductToSubject.js"></script>

<div id="jqxwindowAddProductToSubject" class="hide">
	<div>${uiLabelMap.CRMAddProductToSubject}</div>
	<div style="overflow-x: hidden;" class="form-window-content-custom">
	
		<div class="row-fluid margin-top10">
			<div class="span12 no-left-margin">
				<div class="span4"><label class="text-right">${uiLabelMap.KCommSubjectId}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span8"><label id="lblProductSubjectId" style="margin-top: 5px;" class="green"></label></div>
			</div>
		</div>

		<div class="row-fluid margin-top10">
			<div class="span12 no-left-margin">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsProduct}</label></div>
				<div class="span8">
					<div id="txtProductBeAdd">
						<div style="border-color: transparent;" id="jqxgridProductBeAdd" tabindex="5"></div>
					</div>
				</div>
			</div>
		</div>

		<div class="row-fluid margin-top10">
			<div class="span12 no-left-margin">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsSequence}</label></div>
				<div class="span8"><div id="txtSequenceNum"></div></div>
			</div>
		</div>

		<div class="form-action">
			<div class="row-fluid">
				<div class="span12 margin-top10">
					<button id="cancelAddProductToSubject" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddProductToSubject" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
