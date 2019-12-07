<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "component://basepo/webapp/basepo/product/listProduct_rowDetail.ftl"/>
<#include "component://basepo/webapp/basepo/product/listProduct_script.ftl"/>

<#assign dataField="[{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'numChild', type: 'number' },
					{ name: 'primaryProductCategoryId', type: 'string' },
					{ name: 'internalName', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'brandName', type: 'string' },
					{ name: 'productWeight', type: 'number' },
					{ name: 'weightUomId', type: 'string' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'longDescription', type: 'string' },
					{ name: 'isVirtual', type: 'string' },
					{ name: 'productFeatureTaste', type: 'string' },
					{ name: 'productFeatureSize', type: 'string' },
					{ name: 'productFeatureColor', type: 'string' },
					{ name: 'taxCatalogs', type: 'string' },
					{ name: 'rowDetail', type: 'string' }]"/>
<#--
{ text: '${StringUtil.wrapString(uiLabelMap.DmsInternalName)}', datafield: 'internalName', width: 150 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsDescription)}', datafield: 'longDescription', width: 200 },
-->
<#assign columnlist="{ text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productCode', width: '14%',
						cellsrenderer: function(row, colum, value){
							var productId = $('#jqxgrid').jqxGrid('getcellvalue', row, 'productId');
							var link = 'viewProduct?productId=' + productId;
							return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductName)}', datafield: 'productName'},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsProductTaxCatalogs)}', datafield: 'taxCatalogs', width: 150, sortable: false, filterable: false }"/>

<#--<#if hasOlbPermission("MODULE", "PRODUCTPO", "CREATE")>-->
<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.CommonAddNew}@newProductTax"/>
<#include "component://basepo/webapp/basepo/product/popup/addNewVariantProduct.ftl"/>
<#--</#if>-->

<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" editrefresh="true"
showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" editColumns="productId;productCode"
url="jqxGeneralServicer?sname=JQGetPOListTaxProducts" viewSize="15"
customcontrol1="${tmpCreateUrl}" selectionmode="multiplerowsextended"
initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203"
contextMenuId="contextMenu" mouseRightMenu="true"/>


<#if security.hasEntityPermission("ECOMMERCE", "_CREATE", session)>
    <#include "component://baseecommerce/webapp/baseecommerce/backend/content/listComment.ftl"/>
<div id="contextMenu" style="display:none;">
    <ul>
        <li id="view"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSPreview}</li>
        <li id="viewComments"><i class="fa-comment-o"></i>&nbsp;&nbsp;${uiLabelMap.BSViewComments}</li>
        <li id="viewContent"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSViewContent}</li>
        <li id="addContent"><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.BSAddContent}</li>
        <li id="viewProductDetails"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewProductDetails}</li>
    </ul>
</div>
<#else>
<div id="contextMenu" style="display:none;">
    <ul>
        <#if hasOlbPermission("MODULE", "PRODUCTPO", "UPDATE")>
            <li id="configProduct"><i class="icon-edit"></i>&nbsp;&nbsp;${uiLabelMap.BSConfigProduct}</li>
        </#if>
        <li id="viewProductDetails"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewProductDetails}</li>
        <#if hasOlbPermission("MODULE", "PRODUCTPO", "CREATE")>
            <li id="addSimilarProduct"><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.BSCreateASimilarProduct}</li>
            <li id="addNewProduct"><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.DmsAddNewProductVariant}</li>
        </#if>
    </ul>
</div>
</#if>

<div id="jqxNotificationNested">
    <div id="notificationContentNested">
    </div>
</div>