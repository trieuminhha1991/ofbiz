<div id="setupPolicyWindow" class="hide">
	<div>${uiLabelMap.KpiPolicy}</div>
	<div class='form-window-container'>
		<div class="form-window-content">
			<div class="row-fluid ">
				<div class="span12">
					<div id="containersetupPolicyGrid" style="background-color: transparent; overflow: auto; width: 100%;">
						<div id="jqxNotificationsetupPolicyGrid">
							<div id="notificationContentsetupPolicyGrid"></div>
						</div>
					</div>
					<div id="setupPolicyGrid"></div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="createKpiPolicyWindow" class="hide">
	<div>${uiLabelMap.CreatePolicy}</div>
	<div class='form-window-container' >
		<div class="form-window-content">
			<div class="row-fluid margin-bottom10">
				<div class="span4 align-right">
					<label>${uiLabelMap.KpiPolicyId}</label>
				</div>
				<div class="span8">
					<input type="text" id="policyId"/>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span4 align-right">
					<label class="asterisk">${uiLabelMap.CommonFromDate}</label>
				</div>
				<div class="span8">
					<div id="fromDateNew"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span4 align-right">
					<label>${uiLabelMap.CommonThruDate}</label>
				</div>
				<div class="span8">
					<div id="thruDateNew"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel_new" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave_new" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="editPolicyWindow" class="hide">
	<div>${uiLabelMap.SetupKPIPolicy}</div>
	<div class='form-window-container' >
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12">
					<div id="containersetupGrid_edit" style="background-color: transparent; overflow: auto; width: 100%;">
						<div id="jqxNotificationsetupGrid_edit">
							<div id="notificationContentsetupGrid_edit"></div>
						</div>
					</div>
					<div id="setupGrid_edit"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel_edit" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave_edit" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/listCriteriaDetail.js?v=0.0.2"></script>