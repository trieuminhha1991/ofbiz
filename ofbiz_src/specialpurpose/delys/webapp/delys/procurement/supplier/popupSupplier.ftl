<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.createSupplier}</div>
	<div class='form-window-container'>
		<div class="form-window-content">
			<div class='row-fluid'>
				<div class='span6'>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">${uiLabelMap.groupName}:</div>
						<div class="span7">
							<input id="groupNameInput"/>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right asterisk">${uiLabelMap.DARoleTypeId}:</div>
						<div class="span7">
							<div id="roleType"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 align-right">${uiLabelMap.EmailAddress}:</div>
						<div class="span7">
							<input id="emailInput"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>${uiLabelMap.TelephoneNumber}:</div>
						<div class='span7'>
							<input id="phoneNumber" />
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>${uiLabelMap.fromContactMechFax}:</div>
						<div class='span7'>
							<input id="faxNumber" />
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>${uiLabelMap.description}:</div>
						<div class='span7'>
							<textarea id="descriptionInput" style="width: 240px;margin-top:0" rows="6"></textarea>
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>${uiLabelMap.DACountry}:</div>
						<div class='span7'>
							<div id="country" class='country'></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>${uiLabelMap.DAStateProvince}:</div>
						<div class='span7'>
							<div id="province" class='province'></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>${uiLabelMap.DACountyGeoId}:</div>
						<div class='span7'>
							<div id="district" class='district'></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>${uiLabelMap.DAArea}:</div>
						<div class='span7'>
							<div id="ward" class='ward'></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>${uiLabelMap.Address}:</div>
						<div class='span7'>
							<input id="address" class='address'/>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
			<div class="span12 margin-top10">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
			</div>
		</div>
	</div>
</div>