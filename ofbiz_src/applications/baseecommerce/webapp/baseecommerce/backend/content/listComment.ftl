<@jqGridMinimumLib />

<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>

<div id="jqxwindowListComment" class="hide">
	<div>${uiLabelMap.BSListComments}</div>
	<div>
		<div id="containerComment"></div>
		<div class="blue pull-right" style="cursor: pointer;" id="removeFilter"><i class="icon-filter open-sans"></i>${uiLabelMap.accRemoveFilter}</div>
		<div id="gridComment"></div>
		<div class="form-action">
			<div class="row-fluid">
				<div class="span12 margin-top10">
					<button id="alterCancelListComment" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
				</div>
			</div>
		</div>

	</div>
</div>


<div id="contextMenuComment" style="display:none;">
	<ul>
		<li id="activateComment"><i class="fa-frown-o"></i>&nbsp;&nbsp;${uiLabelMap.DmsDeactivate}</li>
	</ul>
</div>

<div id="jqxNotificationNestedComment">
	<div id="notificationContentNestedComment">
	</div>
</div>

<script type="text/javascript" src="/ecommerceresources/js/backend/content/listComment.js"></script>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "CONTENT_STATUS"), null, null, null, false) />

<script>
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item><#if item.statusId == "CTNT_PUBLISHED" || item.statusId == "CTNT_DEACTIVATED">{
		statusId: "${item.statusId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#if></#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
		"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
</script>