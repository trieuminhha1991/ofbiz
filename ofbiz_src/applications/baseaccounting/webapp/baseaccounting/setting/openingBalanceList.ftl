<#--<!-- <#include "script/glAccountBalData.ftl"/> -->
<#--<!-- <#include "openingBalanceGrid.ftl" /> -->
<#--<!-- <#include "openingBalanceNewPopup.ftl" /> -->

<#include "script/openingBalanceListScript.ftl" />
<style>
.disableCellEditor{
	color: rgba(58, 42, 42, 0.82) !important; 
	background-color: #843a3a !important; 
	border-color: #bbb !important; 
	background: #EEE !important
}
</style>

<div id="containerjqxgrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
</div>
<div id="jqxNotificationjqxgrid">
    <div id="notificationContentjqxgrid">
    </div>
</div>

<div id="glAccountBalanceTree"></div>

<div id="updateGlAccountBalWindow" class="hide">
	<div>${uiLabelMap.BACCUpdateOpeningBalance}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="updateGlAccountBalGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditGlAccBal">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditGlAccBal">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<script type="text/javascript" src="/accresources/js/setting/openingBalanceList.js"></script>
