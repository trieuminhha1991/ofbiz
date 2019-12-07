<div id="addNewSkillTypeWindow" class="hide">
	<div>${uiLabelMap.HROlbiusNewPartySkill}</div>
	<div class='form-window-container'>
		<div class="form-window-content" style="position: relative;">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.SkillTypeId}</label>
				</div>
				<div class="span8">
			 		<input type="text" id="newSkillTypeId">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="asterisk">${uiLabelMap.CommonDescription}</label>
				</div>
				<div class="span8">
			 		<input type="text" id="descriptionSkillTypeNew">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="asterisk">${uiLabelMap.HRSkillTypeParent}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span10">
						 		<div id="parentNewSkillType"></div>
							</div>
							<div class="span2">
						 		<button id="searchParentSkillTypeBtn" title="${uiLabelMap.CommonSearch}" style="width: 90%" class="btn btn-mini btn-primary">
									<i class="icon-only icon-list open-sans" style="font-size: 15px; position: relative; top: -2px;"></i></button>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingCreateSkillType" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerCreateSkillType"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelNewSkillType" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveNewSkillType">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<div id="listParentSkillTypeWindow" class="hide">
	<div>${uiLabelMap.HRSkillTypeParent}</div>
	<div class='form-window-container'>
		<div id="containerparentSkillTypeGrid" style="background-color: transparent; overflow: auto; width: 100%;">
    	</div>
    	<div id="jqxNotificationparentSkillTypeGrid">
	        <div id="notificationContentparentSkillTypeGrid">
	        </div>
	    </div>
	    <div id="parentSkillTypeGrid"></div>
	</div>
</div>
<div id="AddNewParentSkillTypeWindow" class="hide">
	<div>${uiLabelMap.AddNewParentSkillType}</div>
	<div class='form-window-container'>
		<div class="form-window-content" style="position: relative;">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.ParentSkillTypeId}</label>
				</div>
				<div class="span8">
			 		<input type="text" id="newParentSkillTypeId">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="asterisk">${uiLabelMap.CommonDescription}</label>
				</div>
				<div class="span8">
			 		<input type="text" id="descriptionParentSkillTypeNew">
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelNewParentSkillType" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveNewParentSkillType">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/training/AddSkillType.js"></script>