<div class="hide" id="resetPasswordWindow">
	<div>${uiLabelMap.SettingResetPassword}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class="row-fluid form-horizontal form-window-content-custom label-text-left content-description">
				<div class="span12">
					<div class='row-fluid'>
						<div class="span5 text-algin-right">
							<span style="float: right;">${uiLabelMap.HRCommonUserLogin}</span>
						</div>								
						<div class="span7">
							<div id="changePasswordUserLogin"></div>
						</div>								
					</div>	
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelResetPassword" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveResetPassword">
				<i class='fa fa-check'></i>${uiLabelMap.CommonReset}</button>
		</div>
	</div>
</div>

<script type="text/javascript" src="/hrresources/js/generalMgr/ResetPassword.js"></script>