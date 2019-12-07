<div id="jm-main">
	<div class="inner clearfix">
		<div id="jm-current-content" class="clearfix">
			<div class="my-account account-create">
				<div class="dashboard">
				    <div class="page-title">
				        <h1 class='account-title'>${uiLabelMap.PartyChangePassword}</h1>
				    </div>
					<div class="screenlet account-form">
					  <div class="screenlet-body">
					    <form id="changepasswordform" method="post" action="<@ofbizUrl>updatePassword/${donePage}</@ofbizUrl>">
					      <fieldset>
					      	<ul class="form-list">
						        <li>
						          <label for="currentPassword" class="required"><em>*</em>${uiLabelMap.PartyOldPassword}</label>
						          <div class="input-box">
						          	<input type="password" class='input-text required-entry' name="currentPassword" id="currentPassword" maxlength="20" />
						          </div>
						        </li>
						        <li>
								<div class="field">
							          <label for="newPassword" class="required"><em>*</em>${uiLabelMap.PartyNewPassword}</label>
							          <div class="input-box">
									<input type="password" class='input-text required-entry' name="newPassword" id="newPassword" maxlength="20" />
								  </div>
							        </div>
							        <div class="field">
							          <label for="newPasswordVerify" class="required"><em>*</em>${uiLabelMap.PartyNewPasswordVerify}</label>
								  <div class="input-box">
									<input type="password" class='input-text required-entry' name="newPasswordVerify" id="newPasswordVerify" maxlength="20" />
								  </div>
							    </div>
						        </li>
						        <li>
						          <label for="passwordHint">${uiLabelMap.PartyPasswordHint}</label>
						          <div class="input-box">
								<input type="text" class='input-text' maxlength="100" name="passwordHint" id="passwordHint" value="${userLoginData.passwordHint?if_exists}" />
							  </div>
						        </li>
					        </ul>
					      </fieldset>
					      <div class="buttons-set">
					        <button type="submit" class='btn-submit'>
								<i class='fa fa-check'>&nbsp;</i>&nbsp;${uiLabelMap.BESubmit}
							</button>
					      </div>
					    </form>
					  </div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>