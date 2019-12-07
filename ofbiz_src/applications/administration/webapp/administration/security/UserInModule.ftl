<script src="/administrationresources/js/security/UserInModule.js"></script>

<#include "UserInModule_rowDetail.ftl"/>
<div id="UserInModule-tab" class="tab-pane<#if activeTab?exists && activeTab == "UserInModule-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
		
			<div class="row-fluid">
				<div class="span2">
					<label class="text-right">${uiLabelMap.ADPermissions}&nbsp;</label>
				</div>
				<div class="span10">
					<div id="txtPermissions"></div>
				</div>
			</div><!--.row-fluid-->
		
			<div class="row-fluid">
			<#assign dataField = "[
								{ name: 'applicationId', type: 'string' },
								{ name: 'userLoginId', type: 'string' },
								{ name: 'partyId', type: 'string' },
								{ name: 'partyCode', type: 'string' },
								{ name: 'partyName', type: 'string' }]"/>

			<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.ADApplicationId)}', dataField: 'applicationId', width: 250 },
									{ text: '${StringUtil.wrapString(uiLabelMap.userLoginId)}', dataField: 'userLoginId', width: 250 },
									{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', dataField: 'partyCode', width: 250 },
									{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName' }"/>

			<@jqGrid id="jqxgridUserInModule" addrow="true" clearfilteringbutton="true" editable="false" alternativeAddPopup="jqxwindowAddUser"
				columnlist=columnlist dataField=dataField customTitleProperties="ADUser"
				showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
				initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="270"
				url="jqxGeneralServicer?sname=JQGetListUserLoginInModule&applicationId=${parameters.moduleId?if_exists}"/>

			</div><!--.row-fluid-->
		</div>
	</div>
</div>

<#include "popup/addUserToModule.ftl"/>