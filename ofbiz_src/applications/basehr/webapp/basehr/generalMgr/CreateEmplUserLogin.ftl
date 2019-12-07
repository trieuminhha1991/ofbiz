<div id="CreateNewUserLoginWindow" class="hide">
	<div>${uiLabelMap.HRCommonCreateNewUserLogin}</div>
	<div class='form-window-container'>
		<div class='form-window-content' >
			<div class="row-fluid" style="position: relative;">
				<div class="span12">
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="">${uiLabelMap.EmployeeId}</label>
						</div>
						<div class="span7">
							<input type="text" id="EmployeeId">
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="">${uiLabelMap.EmployeeName}</label>
						</div>
						<div class="span7">
							<input type="text" id="EmployeeName">
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="">${uiLabelMap.OrganizationalUnit}</label>
						</div>
						<div class="span7">
							<input type="text" id="OrganizationalUnit">
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="">${uiLabelMap.CommonUsername}</label>
						</div>
						<div class="span7">
							<input type="text" id="UserName">
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label id= "labelPassword" class="">${uiLabelMap.CommonPassword}</label>
						</div>
						<div class="span7">
							<input type="password" id="Password">
						</div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button id="cancelCreateUserButton" class="btn btn-danger form-action-button pull-right">
						<i class="fa-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="saveCreateUserButton" class="btn btn-primary form-action-button pull-right">
						<i class="fa fa-check"></i>${uiLabelMap.CommonSave}</button>
				<button id="editPassword" class="btn btn-primary form-action-button pull-right" style="visibility: hidden;">
						<i class="icon-edit"></i>${uiLabelMap.HRChangePassword}</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/generalMgr/CreateNewUserLogin.js"></script>