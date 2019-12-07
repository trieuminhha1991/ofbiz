<#if !includeJs?exists || includeJs == "true">
<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<#include "script/HosipitalListScript.ftl"/>
</#if>
<#if !defaultSuffix?exists>
	<#assign defaultSuffix = ""/>
</#if>
<div class="row-fluid">
	<div id="containerhospitalList${defaultSuffix}">
	</div>
	<div id="jqxNotificationjqxgridhospitalList${defaultSuffix}">
		<div id="notificationContentjqxgridhospitalList${defaultSuffix}"></div>
	</div>
</div>
<div id="hospitalListWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.ScreenletTitle_HealthCareProvider)}</div>
	<div class='form-window-container'>
		<div class="form-window-content">
			<div id="hospitalList${defaultSuffix}"></div>
		</div>
		<div class="form-action">
			<button id="cancelChooseHospital${defaultSuffix}" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveChooseHospital${defaultSuffix}">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSelect}</button>
		</div>
	</div>
</div>

<div id="createNewHosipitalWindow${defaultSuffix}" class="hide">
	<div>${uiLabelMap.AddNewHospital}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.MedicalPlace}</label>
				</div>
				<div class="span7">
					<input type="text" id="hospitalName${defaultSuffix}">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.InsuranceHospital}</label>
				</div>
				<div class="span7">
					<input type="text" id="hospitalId${defaultSuffix}">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.PartyState}</label>
				</div>
				<div class="span7">
					<div id="stateProvinceHospital${defaultSuffix}"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.PartyDistrictGeoId}</label>
				</div>
				<div class="span7">
					<div id="districtHospital${defaultSuffix}"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelCreateHospital" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveCreateHospital">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<#if !includeJs?exists || includeJs == "true">
<script type="text/javascript" src="/hrresources/js/insurance/HosipitalListScript.js"></script>	
</#if>

