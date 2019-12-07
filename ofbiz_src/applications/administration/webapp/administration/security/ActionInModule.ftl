<script src="/administrationresources/js/security/ActionInModule.js"></script>
<#include "ActionInModule_rowDetail.ftl"/>
<div id="Action-tab" class="tab-pane<#if activeTab?exists && activeTab == "Action-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
				
			<div class="row-fluid">
			<#assign dataField = "[
								{ name: 'applicationId', type: 'string' },
								{ name: 'applicationType', type: 'string' },
								{ name: 'application', type: 'string' },
								{ name: 'name', type: 'string' },
								{ name: 'permissionId', type: 'string' },
								{ name: 'moduleId', type: 'string' }]"/>

			<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.ADActionId)}', dataField: 'applicationId', width: 250, editable: false },
									{ text: '${StringUtil.wrapString(uiLabelMap.ADAction)}', dataField: 'application', width: 250, editable: false },
									{ text: '${StringUtil.wrapString(uiLabelMap.ADActionName)}', dataField: 'name', minwidth: 250,
										validation: function (cell, value) {
											if (value) {
												return true;
											}
											return { result: false, message: multiLang.fieldRequired };
										}
									},
									{ text: '${StringUtil.wrapString(uiLabelMap.ADPermissionDefault)}', datafield: 'permissionId', columntype: 'dropdownlist', width: 200,
										validation: function (cell, value) {
											if (value) {
												return true;
											}
											return { result: false, message: multiLang.fieldRequired };
										}, createeditor: function(row, column, editor){
											editor.jqxDropDownList({ source: AdministrationConfig.SecurityPermission.array, theme: theme, placeHolder: multiLang.filterchoosestring });
										}
									}"/>

			<@jqGrid id="jqxgridActionInModule" addrow="false" clearfilteringbutton="true" editable="true" alternativeAddPopup="jqxwindowAddAction"
				columnlist=columnlist dataField=dataField customTitleProperties="ADAction"
				showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
				initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="310"
				url="jqxGeneralServicer?sname=JQGetListActionInModule&applicationId=${parameters.moduleId?if_exists}"
				updateUrl="jqxGeneralServicer?jqaction=U&sname=updateOlbiusApplication" editColumns="applicationId;name;permissionId"/>

			</div><!--.row-fluid-->
		</div>
	</div>
</div>