<#include "script/receiveReturnScript.ftl"/>
<#include "script/detailReturnScript.ftl"/>
<div id="notifyUpdateSuccess" style="display: none;">
<div>
	${uiLabelMap.NotifiUpdateSucess}. 
</div>
</div>
<div id="containerNotify" style="width: 100%; overflow: auto;">
</div>
<div>
	<h4 class="row header smaller lighter blue" style="font-weight:500;line-height:20px;font-size:18px;">
		${uiLabelMap.GeneralInfo}
	</h4>
	<#include "returnInfo.ftl"/>
</div>
<div class="row-fluid">
	<h4 class="row header smaller lighter blue" style="font-weight:500;line-height:20px;font-size:18px; padding-top:110px">
	    ${uiLabelMap.ListProduct}
	    <a style="float:right;font-size:14px; margin-right: 5px" id="addRow" href="javascript:ReceiveReturnObj.addNewRow()" data-rel="tooltip" title="${uiLabelMap.AddRow}" data-placement="bottom"><i class="icon-plus-sign open-sans"></i></a>
	</h4>
	<div id="jqxgridProductReturn"></div>
</div>
<div class="row-fluid wizard-actions margin-top5 bottom-action">
	<button class="btn btn-small btn-primary btn-next" id="receiveProduct" data-last="${uiLabelMap.LogFinish}">
		<i class="fa-download"></i>
		${uiLabelMap.ProductReceiveProduct}
	</button>
</div>