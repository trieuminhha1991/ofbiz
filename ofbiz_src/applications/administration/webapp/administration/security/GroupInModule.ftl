<script src="/administrationresources/js/security/GroupInModule.js"></script>

<#include "GroupInModule_rowDetail.ftl"/>
<div id="GroupInModule-tab" class="tab-pane<#if activeTab?exists && activeTab == "GroupInModule-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
		
			<div class="row-fluid">
				<div class="span2">
					<label class="text-right">${uiLabelMap.ADPermissions}&nbsp;</label>
				</div>
				<div class="span10">
					<div id="txtGroupPermissions"></div>
				</div>
			</div><!--.row-fluid-->
		
			<div class="row-fluid">
			<#assign dataField = "[
								{ name: 'applicationId', type: 'string' },
								{ name: 'partyId', type: 'string' },
								{ name: 'partyCode', type: 'string' },
								{ name: 'description', type: 'string' }]"/>

			<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.ADApplicationId)}', dataField: 'applicationId', width: 250 },
								{ text: '${StringUtil.wrapString(uiLabelMap.ADUserGroupId)}', dataField: 'partyCode', width: 250 },
								{ text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description' }"/>

			<@jqGrid id="jqxgridGroupInModule" addrow="true" clearfilteringbutton="true" editable="false" alternativeAddPopup="jqxwindowAddGroup"
				columnlist=columnlist dataField=dataField customTitleProperties="ADUserGroup"
				showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
				initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="270"
				url="jqxGeneralServicer?sname=JQGetListGroupInModule&applicationId=${parameters.moduleId?if_exists}"/>

			</div><!--.row-fluid-->
		</div>
	</div>
</div>

<#include "popup/addGroupToModule.ftl"/>