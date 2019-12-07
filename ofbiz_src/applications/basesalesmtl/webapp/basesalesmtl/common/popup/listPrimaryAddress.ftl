<script src="/salesmtlresources/js/common/listPrimaryAddress.js"></script>

<div id="jqxwindowListPrimaryAddress" style="display:none;">
	<div>${uiLabelMap.DmsListAddress}</div>
	<div>
		
		<#--DEL <#if security.hasEntityPermission("CATALOG", "_UPDATE", session)>-->
		<#if hasOlbPermission("MODULE", "PARTY_SALESORG_EDIT", "")>
			<a class="pointer pull-right" id="addAddress"><i class="icon-plus open-sans"></i>${uiLabelMap.accAddNewRow}</a>
		</#if>
		
		<div id="jqxgridPrimaryAddress"></div>
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="btnCloseWindow" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonClose}</button>
			</div>
		</div>
	</div>
</div>

<div id="jqxNotification">
	<div id="notificationContent"></div>
</div>

<div id="contextMenuAddress" style="display:none;">
	<ul>
		<li id="mnAdrEdit"><i class="fa fa-pencil-square-o"></i>${uiLabelMap.CommonEdit}</li>
		<li id="mnAdrDelete"><i class="fa fa-trash-o"></i>${uiLabelMap.CommonDelete}</li>
	</ul>
</div>

<#include "component://basesalesmtl/webapp/basesalesmtl/common/popup/addPrimaryAddress.ftl"/>

<script>
	multiLang = _.extend(multiLang, {
		BSContactMechId: "${StringUtil.wrapString(uiLabelMap.BSContactMechId)}",
		BSReceiverName: "${StringUtil.wrapString(uiLabelMap.BSReceiverName)}",
		BSOtherInfo: "${StringUtil.wrapString(uiLabelMap.BSOtherInfo)}",
		BSAddress: "${StringUtil.wrapString(uiLabelMap.BSAddress)}",
		BSWard: "${StringUtil.wrapString(uiLabelMap.BSWard)}",
		BSCounty: "${StringUtil.wrapString(uiLabelMap.BSCounty)}",
		BSStateProvince: "${StringUtil.wrapString(uiLabelMap.BSStateProvince)}",
		BSCountry: "${StringUtil.wrapString(uiLabelMap.BSCountry)}",
		DmsAddAddress: "${StringUtil.wrapString(uiLabelMap.DmsAddAddress)}",
		DmsEditAddress: "${StringUtil.wrapString(uiLabelMap.DmsEditAddress)}",
		});
</script>