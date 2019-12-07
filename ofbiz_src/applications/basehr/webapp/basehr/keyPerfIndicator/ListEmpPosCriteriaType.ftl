<#include "script/ListEmpPosCriteriaTypeScript.ftl"/>
<style>
.backgroundWhiteColor{
	background-color: #fff !important
}
</style>
<div>
<div class="row-fluid" id="containerNtf">
</div>
<div id="jqxNotificationNtf">
	<div id="content_noti"></div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.KPILIstForPositionType}</h4>
		<div class="widget-toolbar none-content" >
			<div class="row-fluid">
				<div class="span12">
					<button id="clearFilterBtn" class="grid-action-button icon-filter open-sans" style="float: right; font-size: 14px">${uiLabelMap.accRemoveFilter}</button>
					<button id="deleteBtn" class="grid-action-button icon-trash open-sans" style="float: right; font-size: 14px">${uiLabelMap.accDeleteSelectedRow}</button>
					<button id="addNewBtn" class="grid-action-button icon-plus open-sans" style="float: right; font-size: 14px">${uiLabelMap.accAddNewRow}</button>
				</div>
			</div>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div id="emplPositionTypeFilter"></div>						
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12" style="margin-right: 15px">
							<div id="statusIdFilter" class="pull-right"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div id="jqxEmplPosTypePerfCri"></div>
		</div>
	</div>
</div>
</div>

<div id="editEmplPosKPIWindow" class="hide">
	<div>${uiLabelMap.AddKPIForEmplPositionType}</div>
	<div class='form-window-container'>
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12">
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}</label>
						</div>
						<div class="span8">
							<div id="emplPositionTypeEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRCommonFields)}</label>
						</div>
						<div class="span8">
							<div id="perfCriteriaTypeEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRCommonKPIName)}</label>
						</div>
						<div class="span8">
							<div id="perfCriteriaEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRFrequency)}</label>
						</div>
						<div class="span8">
							<div id="periodTypeEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.HRTarget}</label>
						</div>
						<div class='span8'>
							<div id="targetEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.HRCommonUnit}</label>
						</div>
						<div class='span8'>
							<div id="uomIdEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="">${uiLabelMap.KPIWeigth}</label>
						</div>
						<div class='span8'>
							<div id="weightEdit"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingPositionTypeKPI" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerPositionTypeKPI"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" class='btn btn-danger form-action-button pull-right' id="alterCancel">
							<i class='icon-remove'></i>${uiLabelMap.CommonCancel}
						</button>	
						<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
						<button type="button" class="btn btn-success form-action-button pull-right" id="saveAndContinue"><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ListEmpPosCriteriaType.js"></script>
	