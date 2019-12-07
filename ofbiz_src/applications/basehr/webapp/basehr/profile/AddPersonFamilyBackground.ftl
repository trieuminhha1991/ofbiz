<script type="text/javascript">
if(typeof(globalVar) == "undefined"){
	globalVar = {};
}
globalVar.addNewWindowId = "${addNewWindowId?if_exists}";
</script>
<div id="${addNewWindowId}" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.LastName}</label>
						</div>
						<div class="span7">
							<input type="text" id="lastName${addNewWindowId}"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.MiddleName}</label>
						</div>
						<div class="span7">
							<input type="text" id="middleName${addNewWindowId}" />
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.FirstName}</label>
						</div>
						<div class="span7">
							<input type="text" id="firstName${addNewWindowId}" />
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.BirthDate}</label>
						</div>
						<div class="span7">
			        		<div id="familyBirthDate${addNewWindowId}"></div>
			        	</div>
			        </div>
			        <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=""></label>
						</div>
						<div class="span7">
							<div style="margin-top: 3px; margin-left: 15px">
								<div id="dependentRegister${addNewWindowId}"><span style="font-size: 14px">${uiLabelMap.RegisterDependent}</span></div>
							</div>
						</div>
					</div>
			        <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.DependentDeductionStart}</label>
						</div>
						<div class="span7">
							<div id="dependentStartDate${addNewWindowId}"></div>
						</div>
					</div>
				</div>
				<div class="span6" style="margin: 0">
			        <div class='row-fluid margin-bottom10'>
			        	<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.HROccupation}</label>
						</div>
						<div class="span7">
							<input type="text" id="occupation${addNewWindowId}"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.HRPlaceWork}</label>
						</div>
						<div class="span7">
							<input type="text" id="placeWork${addNewWindowId}"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.PhoneNumber}</label>
						</div>
						<div class="span7">
							<input type="text" id="phoneNumber${addNewWindowId}">
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.HRRelationship}</label>
						</div>
						<div class="span7">
							<div id="relationship${addNewWindowId}"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=""></label>
						</div>
						<div class="span7">
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.DependentDeductionEnd}</label>
						</div>
						<div class="span7">
							<div id="dependentEndDate${addNewWindowId}"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loading${addNewWindowId}" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinner${addNewWindowId}"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button type="button" class="btn btn-danger form-action-button pull-right" id="cancel${addNewWindowId}"><i class='icon-remove'></i>${uiLabelMap.CommonClose}</button>
				<button type="button" class="btn btn-success form-action-button pull-right" id="saveAndContinue${addNewWindowId}"><i class='fa-plus'></i>${uiLabelMap.SaveAndContinue}</button>
				<button type="button" class="btn btn-primary form-action-button pull-right" id="save${addNewWindowId}"><i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>		 
<script type="text/javascript" src="/hrresources/js/profile/family.js"></script>