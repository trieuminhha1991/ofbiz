<div id="createNewUserLoginWindow" style="display: none;">
	<div id="windowHeaderNewUserLogin">
		<span>
		   ${uiLabelMap.createUserLogin}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<form name="createNewUserLogin" id="createNewUserLogin">	
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.userLoginId}:</label>
						<div class="controls">
							<input id="userLoginId">
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.currentPassword}:</label>  
						<div class="controls">
							<input id="currentPassword" type="password">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.currentPasswordVerify}:</label>
						<div class="controls">
							<input id="currentPasswordVerify" type="password">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label ">${uiLabelMap.requirePasswordChange}:</label>  
						<div class="controls">
							<div  id="requirePasswordChange"  style="margin-left: -3px !important;"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp</label>
						<div class="controls">
							<button type="button" class="btn btn-mini btn-success" id="alterSaveUserLogin"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
							<button type="button" class="btn btn-mini btn-danger" id="alterCancelUserLogin"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}&nbsp</button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
