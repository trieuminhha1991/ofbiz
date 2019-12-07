<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/viewListProducts.js"></script>

<div id="jqxwindowProducts" class="hide">
	<div>${uiLabelMap.DmsListProducts}</div>
	<div style="overflow: hidden;">
		<div>
			<#if security.hasEntityPermission("CATALOG", "_UPDATE", session)>
			<div id="addProductContainer" class="pull-right margin-bottom10">
				<a href="javascript:void(0)" onclick="AddProduct.open()"><i class="icon-plus open-sans"></i>${uiLabelMap.accAddNewRow}</a>
				&nbsp;&nbsp;&nbsp;<a id="btnDeleteThisProduct" href="javascript:void(0)" onclick="Products._delete()" class="red hide"><i class="icon-trash open-sans"></i>${uiLabelMap.DmsDelete}</a>
			</div>
			</#if>
			<div id="jqxgridProducts"></div>
		</div>
		<div class="form-action">
			<button id="cancelProducts" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<div id="jqxNotificationProducts">
	<div id="notificationContentProducts"></div>
</div>

<#include "addProductToCategory.ftl"/>
<#include "component://baseecommerce/webapp/baseecommerce/backend/content/listComment.ftl"/>


<div id='productMenu' style="display:none;">
	<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session)>
		<ul>
			<li id='configProduct'><i class="icon-edit"></i>&nbsp;&nbsp;${uiLabelMap.BSConfigProduct}</li>
			<li id='view'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSPreview}</li>
			<li id='viewReviews'><i class="fa-star-half-o"></i>&nbsp;&nbsp;${uiLabelMap.BSViewReviews}</li>
			<li id='viewComments'><i class="fa-comment-o"></i>&nbsp;&nbsp;${uiLabelMap.BSViewComments}</li>
			<li id='viewContent'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSViewContent}</li>
			<li id='addContent'><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.BSAddContent}</li>
		</ul>
	</#if>
</div>

<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session)>
<script>
	$(document).ready(function() {
		$("#jqxgridProducts").on('contextmenu', function () {
            return false;
        });
		$("#jqxgridProducts").on('rowclick', function (event) {
            if (event.args.rightclick) {
                $("#jqxgridProducts").jqxGrid('selectrow', event.args.rowindex);
                var scrollTop = $(window).scrollTop();
                var scrollLeft = $(window).scrollLeft();
                $("#productMenu").jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                return false;
            }
        });
	});
</script>
</#if>