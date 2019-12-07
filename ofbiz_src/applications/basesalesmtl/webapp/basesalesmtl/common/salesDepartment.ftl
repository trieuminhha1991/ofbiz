<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>

<div id="treePartyGroupGrid"></div>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="listAddress"><i class="fa fa-list-alt"></i>${uiLabelMap.DmsListAddress}</li>
	</ul>
</div>

<#include "component://basesalesmtl/webapp/basesalesmtl/common/popup/listPrimaryAddress.ftl"/>
<input type="hidden" id="txtPartyId" />


<#include "script/salesDepartmentScript.ftl"/>