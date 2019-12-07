<div id="addRecruitmentRequireCondWindow" class="hide">
	<div>${uiLabelMap.HRAddCondition}</div>
	<div class='form-window-content' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RecruitmentCriteria}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span10">
								<div id="recruitReqCondTypeList"></div>
							</div>
							<div class="span2">
								<button id="addNewRecruitReqCondType" style="width: 80%" title="${uiLabelMap.AddNew}" class="btn btn-mini btn-primary">
									<i class="icon-only icon-plus open-sans" style="font-size: 15px"></i>
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.HRCondition}</label>
				</div>
				<div class="span8">
					<textarea id="conditionDescNew"></textarea>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
					<button type="button" class="btn btn-success form-action-button pull-right" id="saveAndContinue"><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>	
</div>

<div id="RecruitReqCondTypeWindow" class="hide">
	<div>${uiLabelMap.AddNewCriteria}</div>
	<div class='form-window-content' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.CriteriaName}</label>
				</div>
				<div class="span8">
					<input type="text" id="recruitReqCondTypeNameNew">
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelRecruitReqCondType" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveRecruitReqCondType" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
		<div class="row-fluid no-left-margin">
			<div id="loadingRecruitReqCondType" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
				<div class="loader-page-common-custom" id="spinnerRecruitReqCondType"></div>
			</div>
		</div>
	</div>	
</div>