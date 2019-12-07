<#assign dataField = "[
			{name: 'productPromoCodeId', type: 'string'}, 
			{name: 'productPromoId', type: 'string'}, 
			{name: 'userEntered', type: 'string'}, 
			{name: 'requireEmailOrParty', type: 'string'}, 
			{name: 'useLimitPerCode', type: 'number', other: 'Long'}, 
			{name: 'useLimitPerCustomer', type: 'number', other: 'Long'}, 
			{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
			{name: 'thruDate', type: 'date', other: 'Timestamp'}, 
			{name: 'createdDate', type: 'date', other: 'Timestamp'},
			{name: 'createdByUserLogin', type: 'string'}, 
			{name: 'numOrderAppl', type: 'number', other: 'Long'}, 
		]"/>
<#assign columnlist = "
			{text: '${uiLabelMap.BSVoucherCode}', dataField: 'productPromoCodeId', width: '14%'}, 
			{text: '${uiLabelMap.BSUserEntered}', dataField: 'userEntered', width: '6%'},
			{text: '${uiLabelMap.BSRequireEmailOrParty}', dataField: 'requireEmailOrParty', width: '6%'},
			{text: '${uiLabelMap.BSAbb2UseLimitPerVoucher}', dataField: 'useLimitPerCode', width: '8%', cellsalign: 'right', filtertype: 'number'},
			{text: '${uiLabelMap.BSAbb2UseLimitPerCustomer}', dataField: 'useLimitPerCustomer', width: '8%', cellsalign: 'right', filtertype: 'number'},
			{text: '${uiLabelMap.BSNumUsed}', dataField: 'numOrderAppl', width: '10%', filterable: false, filtertype: 'number'},
			{text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			}, 
			{text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			}, 
			{text: '${uiLabelMap.BSCreateDate}', dataField: 'createdDate', width: '10%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			}, 
			{text: '${uiLabelMap.BSCreatedBy}', dataField: 'createdByUserLogin', width: '10%'},
		"/>
<#assign tmpUpdate = false/>
<#if hasOlbPermission("MODULE", "PRODPROMOTION_EDIT", "")>
	<#assign tmpUpdate = true/>
</#if>

<#assign customcontrol1="fa-pencil@${uiLabelMap.wgaddnew}@javascript:void(0);@OlbPromoCodeNew.openWindowNew();"/>
<#assign customcontrol2="fa-print@${uiLabelMap.BSPrintBarcode}@javascript:void(0);@window.open('PromoVoucherBarcode.pdf?productPromoId=${productPromoId?if_exists}', '_blank');"/>
<#assign contextMenuItemId = "ctxmnupromocodel">
<@jqGrid id="jqxPromotionCode" url="jqxGeneralServicer?sname=JQListProductPromoCode&productPromoId=${productPromoId?if_exists}" columnlist=columnlist dataField=dataField 
		viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" selectionmode="multiplecellsadvanced" 
		removeUrl="jqxGeneralServicer?sname=deleteProductPromoCode&jqaction=C" deleteColumn="productPromoCodeId" deleterow="${tmpUpdate?string}" 
		mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" customcontrol1=customcontrol1 customcontrol2=customcontrol2/>

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

<div id='contextMenu_${contextMenuItemId}' style="display:none">
	<ul>
	    <li id="${contextMenuItemId}_viewdetail"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpUpdate><li id="${contextMenuItemId}_createnew"><i class="fa-plus-circle open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSAddNew)}</li></#if>
	    <#if tmpUpdate><li id="${contextMenuItemId}_delete"><i class="fa-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDeleteSelectedRow)}</li></#if>
	</ul>
</div>

<#include "promotionCodeNewPopup.ftl"/>
<#include "promotionCodeEditPopup.ftl"/>
<#include "script/promotionCodeListScript.ftl"/>