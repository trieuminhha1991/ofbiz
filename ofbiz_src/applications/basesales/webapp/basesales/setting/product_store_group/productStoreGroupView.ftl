<div class="row-fluid">
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPSProductStoreGroupId}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${productStoreGroup.productStoreGroupId?if_exists}</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPSProductStoreGroupName}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${productStoreGroup.productStoreGroupName?if_exists}</i></span>
					</div>
				</div>
			</div><!--.span6-->
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSDescription}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${productStoreGroup.description?if_exists}</i></span>
					</div>
				</div>
			</div><!--.span6-->
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<#assign dataField = "[
								{name: 'productStoreGroupId', type: 'string'}, 
								{name: 'productStoreId', type: 'string'}, 
								{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
								{name: 'storeName', type: 'string'},
								{name: 'description', type: 'string'},
								{name: 'partyCode', type: 'string'},
								{name: 'partyName', type: 'string'},
							]"/>
				<#assign columnlist = "
								{text: '${StringUtil.wrapString(uiLabelMap.BSPSChannelId)}', dataField: 'productStoreId', width: 160, editable: false,
									cellsrenderer: function(row, colum, value) {
								    	return \"<span><a href='showProductStore?productStoreId=\" + value + \"'>\" + value + \"</a></span>\";
								    }
								}, 
								{text: '${StringUtil.wrapString(uiLabelMap.BSPSChannelName)}', dataField: 'storeName', minwidth: 140},
								{text: '${StringUtil.wrapString(uiLabelMap.BSPayToParty)}', dataField: 'partyCode', width: 120},
								{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', dataField: 'partyName', width: 280},
							"/>
				
				<#assign tmpCreate = false/>
				<#assign tmpDelete = false/>
				<#if hasOlbPermission("MODULE", "SALES_PRODUCTSTOREGRP_EDIT", "")>
					<#assign tmpCreate = true/>
					<#assign tmpDelete = true/>
				</#if>
				
				<#assign contextMenuItemId = "ctxmnustoregrplst"/>
				<@jqGrid id="jqxgridStoreMember" clearfilteringbutton="true" alternativeAddPopup="popupProductStoreGroupAddMember" columnlist=columnlist dataField=dataField
						viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="false" addType="popup" addrefresh="true" 
						url="jqxGeneralServicer?sname=JQGetListProductStoreGroupMember&productStoreGroupId=${productStoreGroup.productStoreGroupId?if_exists}" 
						deleterow="${tmpDelete?string}" removeUrl="jqxGeneralServicer?jqaction=D&sname=removeProductStoresInStoreGroup" deleteColumn="productStoreGroupId;productStoreId;fromDate(java.sql.Timestamp)" 
						editable="false" addrow="${tmpCreate?string}" mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" enabletooltips="true"/>
			</div>
		</div>
	</div>
</div>
<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<@jqOlbCoreLib hasGrid=true/>
<#include "productStoreGroupAddMemberPopup.ftl"/>
