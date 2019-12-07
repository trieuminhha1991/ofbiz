<#include "script/detailReturnScript.ftl"/>
<#include "script/exportReturnScript.ftl"/>
<div>
	<h4 class="row header smaller lighter blue" style="margin: 5px 0px 10px 0px !important;font-weight:500;line-height:20px;font-size:18px;">
		${uiLabelMap.GeneralInfo}
	</h4>
<#include "returnInfo.ftl"/>
</div>
<div class="row-fluid">
	<div id="jqxgridReturnItem"></div>
</div>
<div class="row-fluid wizard-actions margin-top5 bottom-action">
	<button class="btn btn-small btn-primary btn-next" id="exportProduct" data-last="${uiLabelMap.LogFinish}">
		<i class="fa-upload"></i>
		${uiLabelMap.ExportProduct}
	</button>
</div>
