<#--
 # Call if customer is not customer of any product store
 # Let select product store contain it
-->
<div id="container" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>
<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>
<div id="alterpopupWindowInitEntry" style="display:none">
	<div>${uiLabelMap.BSChooseProductStore}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSPartyId}</label>
						</div>
						<div class='span7'>
							<#if parameters.partyId?exists>
								<#assign partyGV = delegator.findOne("PartyFullNameDetailSimple", {"partyId": parameters.partyId}, false)!/>
							</#if>
							<span><#if partyGV?exists>${partyGV.fullName?default("")} (${partyGV.partyCode?default(partyGV.partyId)})<#else>${parameters.partyId?exists}</#if></span>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSSalesChannel}</label>
						</div>
						<div class="span7">
							<div id="wn_productStoreId"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<#include "script/orderNewInitEntryScript.ftl"/>