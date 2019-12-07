<#include "script/ViewListCustomTimePeriodScript.ftl"/>

<div id="container" style="background-color: transparent; overflow: auto;"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>
<div id="jqxCustomTimePeriod"></div>
<div id="alternativeAddPopup" class="hide">
	<div>${uiLabelMap.AddNewPayrollPeriod}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4'>
					<label class="">
						${uiLabelMap.YearPeriodPayroll}
					</label>
				</div>
				<div class="span8">
					<div id="yearCustomTimePeriod"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div id="monthCustomTimePeriodGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelCreate" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveCreate">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>


<script type="text/javascript" src="/hrresources/js/configuration/ViewListCustomTimePeriod.js"></script>
<script type="text/javascript" src="/hrresources/js/configuration/AddCustomTimePeriod.js"></script>