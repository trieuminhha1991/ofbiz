<div id="createNewSkillWindow" style="display: none;">
	<div id="windowHeaderNewSkill">
		<span>
		   ${uiLabelMap.NewSkill}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<form name="createNewSkill" id="createNewSkill">	
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.skillTypeId}:</label>
						<div class="controls">
							<div id="skillTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.skillLevel}:</label>  
						<div class="controls">
							<div id="skillLevel"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp</label>
						<div class="controls">
							<button type="button" class="btn btn-mini btn-success" id="alterSaveSkill"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
							<button type="button" class="btn btn-mini btn-danger" id="alterCancelSkill">${uiLabelMap.CommonCancel}&nbsp;<i class="icon-remove"></i></button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>