<div id="addRecruitmentSubjectWindow" class="hide">
	<div>${uiLabelMap.AddRecruitmentSubject}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RecruitmentSubjectName}</label>
				</div>
				<div class='span8'>
					<input id="recruitmentSubjectNew" type="text">
				</div>	
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRNotes}</label>
				</div>
				<div class='span8'>
					<input id="commentSubjectNew" type="text">
				</div>	
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelCreateSubject" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveCreateSubject" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>	
</div>

<div id="addRecruitmentCostWindow" class="hide">
	<div>${uiLabelMap.AddRecruitmentCost}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RecruitmentCostItemName}</label>
				</div>
				<div class='span8'>
					<div class="row-fluid">
						<div class="span12">
							<div class="span10">
								<div id="recruitCostItemNew"></div>
							</div>
							<div class="span2">
								<button id="searchRecCostBtn" title="${uiLabelMap.CommonSearch}" class="btn btn-mini btn-primary" style="width: 82%">
									<i class="icon-only icon-list open-sans" style="font-size: 15px; position: relative; top: -2px;"></i></button>
							</div>
						</div>
					</div>
				</div>	
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RecruitmentCostCategory}</label>
				</div>
				<div class='span8'>
					<input id="recruitCostCatId" type="text">
				</div>	
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRCommonAmount}</label>
				</div>
				<div class='span8'>
					<div id="recruitCostItemAmount"></div>
				</div>	
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRNotes}</label>
				</div>
				<div class='span8'>
					<input id="recruitCostComment" type="text">
				</div>	
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelCreateCost" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveCreateCost" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
					<button id="saveAndContinueCost" type="button" class="btn btn-success form-action-button pull-right" ><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>	
</div>

<div id="RecruitCostItemTypeListWindow" class="hide">
	<div>${uiLabelMap.RecruitmentCostReason}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="containerrecruitCostItemListGrid"></div>
			<div id="jqxNotificationrecruitCostItemListGrid">
		        <div id="notificationContentrecruitCostItemListGrid">
		        </div>
		    </div>
			<div id="recruitCostItemListGrid"></div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelSelectCostItemType" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveSelectCostItemType" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSelect}</button>
				</div>
			</div>
		</div>
	</div>	
	
</div>

<div id="addRecruitCostItemTypeWindow" class="hide">
	<div>${uiLabelMap.AddRecruitmentReason}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RecruitmentCostItemName}</label>
				</div>
				<div class='span8'>
					<input id="recruitCostItemNameNew" type="text">
				</div>	
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RecruitmentCostCategory}</label>
				</div>
				<div class='span8'>
					<div class="row-fluid">
						<div class="span12">
							<div class="span10">
								<div id="recruitCostCatTypeNew"></div>
							</div>
							<div class="span2">
								<button id="searchRecCostCatBtn" title="${uiLabelMap.CommonSearch}" class="btn btn-mini btn-primary" style="width: 82%">
									<i class="icon-only icon-list open-sans" style="font-size: 15px; position: relative; top: -2px;"></i></button>
							</div>
						</div>
					</div>
				</div>	
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRNotes}</label>
				</div>
				<div class='span8'>
					<input id="recruitCostItemTypeCommentNew" type="text">
				</div>	
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelCreateCostItemType" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveCreateCostItemType" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
					<button id="saveAndContinueCostItemType" type="button" class="btn btn-success form-action-button pull-right" ><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>	
</div>

<div id="recruitCostCatTypeWindow" class="hide">
	<div>${uiLabelMap.RecruitmentCostCategoryType}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="containerrecruitCostCatTypeListGrid"></div>
			<div id="jqxNotificationrecruitCostCatTypeListGrid">
		        <div id="notificationContentrecruitCostCatTypeListGrid">
		        </div>
		    </div>
			<div id="recruitCostCatTypeListGrid"></div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelSelectCostCatType" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveSelectCostCatType" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSelect}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<div id="addRecruitCostCatTypeWindow" class="hide">
	<div>${uiLabelMap.AddRecruitmentCostCategoryType}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RecruitCostCatName}</label>
				</div>
				<div class='span8'>
					<input id="recruitCostCatNameNew" type="text">
				</div>	
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRNotes}</label>
				</div>
				<div class='span8'>
					<input id="recruitCostCatTypeCommentNew" type="text">
				</div>	
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelCreateCostCatType" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveCreateCostCatType" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
					<button id="saveAndContinueCostCatType" type="button" class="btn btn-success form-action-button pull-right" ><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentCostUtils.js"></script>