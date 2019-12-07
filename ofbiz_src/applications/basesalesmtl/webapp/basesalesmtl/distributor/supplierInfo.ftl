<style>
	.row-fluid {
	    min-height: 40px;
	}
	.text-header {
		color: black !important;
	}
	.boder-all-profile .label {
	    font-size: 14px;
	    text-shadow: none;
	    background-color: #3a87ad !important;
		margin: 0px;
		color: white !important;
    	line-height: 14px !important;
		margin-top: -20px;
	}
</style>

<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.BSSupervisor}</span>
			<div class="row-fluid">
				<div class="span6" style="padding-left: 50px;">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.CommonDepartment}:</label>
						</div>
						<div class="div-inline-block">
							<span id="svFullName">${(supervisor.supervisor)?if_exists}</span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.PhoneNumber}:</label>
						</div>
						<div class="div-inline-block">
							<span id="svPhoneNumber">${(representative.contactNumber)?if_exists}</span>
						</div>
					</div>
				</div>
				<div class="span6" style="padding-left: 50px;">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.DmsRepresent}:</label>
						</div>
						<div class="div-inline-block">
							<span id="svRepresent">${(representative.representative)?if_exists}</span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.Email}:</label>
						</div>
						<div class="div-inline-block">
							<span id="svEmail">${(representative.emailAddress)?if_exists}</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.BSStaffSaler}</span>
			<#include "listSalers.ftl"/>
		</div>
	</div>
</div>
