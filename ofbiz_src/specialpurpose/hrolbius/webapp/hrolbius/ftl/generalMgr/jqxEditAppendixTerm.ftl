<div id="wdwNewAppendixTerm" style="display: none;">
	<div id="wdwHeaderNewAppendixTerm">
		<span>
		   ${uiLabelMap.NewAppendixTerm}
		</span>
	</div>
	<div id="wdwContentNewAppendixTerm">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formNewAppendixTerm" id="formNewAppendixTerm">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.termTypeId}:</label>
							<div class="controls">
								<div id="termTypeId"></div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.fromDate}:</label>  
							<div class="controls">
								<div id="fromDate"></div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.thruDate}:</label>
							<div class="controls">
								<div id="thruDate">
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.termValue}:</label>  
							<div class="controls">
								<input id="termValue"></input>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.textValue}:</label>  
							<div class="controls">
								<input id="textValue"></input>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>