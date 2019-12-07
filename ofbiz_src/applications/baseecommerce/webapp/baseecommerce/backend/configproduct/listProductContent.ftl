<#assign dataField="[{ name: 'contentId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productContentTypeId', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'contentName', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'numberOfComments', type: 'number', other: 'Long' },
					{ name: 'fromDate', type: 'date', other: 'Timestamp' },
					{ name: 'statusId', type: 'string' }]"/>

<#assign columnlist="{ text: '${uiLabelMap.BSProductName}', datafield: 'productName', width: 250 },
					{ text: '${uiLabelMap.BSContentId}', datafield: 'contentId', width: 120 },
					{ text: '${uiLabelMap.BSContentType}', datafield: 'productContentTypeId', filtertype: 'checkedlist', width: 200,
						cellsrenderer: function(row, colum, value){
							value?value=mapContentType[value]:value;
							return '<span>' + value + '</span>';
						}, createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listContentType, displayMember: 'productContentTypeId', valueMember: 'productContentTypeId',
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return mapContentType[value];
								}
							});
						}
					},
					{ text: '${uiLabelMap.BSStatus}', datafield: 'statusId', filtertype: 'checkedlist', width: 150,
						 cellsrenderer: function(row, colum, value){
								value?value=mapStatusItem[value]:value;
								return '<span>' + value + '</span>';
						 }, createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return mapStatusItem[value];
								}
							});
						 }
					},
					{ text: '${uiLabelMap.BSContentName}', datafield: 'contentName' },
					{ text: '${uiLabelMap.BSNumberOfComments}', datafield: 'numberOfComments', align: 'right', cellsalign: 'right', filtertype: 'number', width: 100 }"/>

<#if parameters.productId?exists>
	<#assign customcontrol1 = "fa fa-plus-circle@${uiLabelMap.BSAddContent}@ContentEditorEngine?productId=${parameters.productId?if_exists}&type=PRODUCT" />
</#if>
<@jqGrid id="ListProductContent" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
	url="jqxGeneralServicer?sname=JQGetListProductContent&productId=${parameters.productId?if_exists}"
	customcontrol1=customcontrol1 contextMenuId="contextMenu" mouseRightMenu="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateContent" editColumns="contentId;statusId"/>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="viewComments"><i class="fa-comment-o"></i>&nbsp;&nbsp;${uiLabelMap.BSViewComments}</li>
		<li id="view"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSPreview}</li>
		<li id="editContent"><i class="fa-pencil-square-o"></i>&nbsp;&nbsp;${uiLabelMap.BSEditContent}</li>
		<li id="activate"><i class="fa-frown-o"></i>&nbsp;&nbsp;${uiLabelMap.DmsDeactivate}</li>
	</ul>
</div>


<#include "component://baseecommerce/webapp/baseecommerce/backend/content/listComment.ftl"/>

<script>
	$(document).ready(function() {
		var contextMenu = $("#contextMenu").jqxMenu({ theme: theme, width: 200, autoOpenPopup: false, mode: "popup" });

		contextMenu.on("itemclick", function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			var rowIndexSelected = $("#ListProductContent").jqxGrid("getSelectedRowindex");
			var rowData = $("#ListProductContent").jqxGrid("getrowdata", rowIndexSelected);
			if (rowData) {
				switch (itemId) {
				case "viewComments":
					CommentTree.load(rowData.contentId);
					break;
				case "view":
					if (rowData.productContentTypeId == "INTRODUCTION") {
						window.open("/baseecommerce/control/product?product_id=" + rowData.productId + "&contentId=" + rowData.contentId, "_blank");
					} else {
						window.open("/baseecommerce/control/viewcontent?pid=" + rowData.contentId, "_blank");
					}
					break;
				case "editContent":
					window.location.href = "ContentEditorEngine?contentId=" + rowData.contentId + "&productId=" + rowData.productId + "&type=PRODUCT";
					break;
				case "activate":
					var contentId = rowData.contentId;
					var statusId = rowData.statusId;
					if (statusId == "CTNT_PUBLISHED") {
						$("#ListProductContent").jqxGrid("setcellvalue", rowIndexSelected, "statusId", "CTNT_DEACTIVATED");
					} else {
						$("#ListProductContent").jqxGrid("setcellvalue", rowIndexSelected, "statusId", "CTNT_PUBLISHED");
					}
					break;
				default:
					break;
				}
			}
		});
		contextMenu.on("shown", function () {
			var rowIndexSelected = $("#ListProductContent").jqxGrid("getSelectedRowindex");
			var rowData = $("#ListProductContent").jqxGrid("getrowdata", rowIndexSelected);
			var statusId = rowData.statusId;
			if (statusId == "CTNT_PUBLISHED") {
				$("#activate").html("<i class='fa-frown-o'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.DmsDeactivate)}");
			} else {
				$("#activate").html("<i class='fa-smile-o'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.DmsActive)}");
			}
			if (rowData.productContentTypeId == "INTRODUCTION") {
				contextMenu.jqxMenu("disable", "viewComments", true);
			} else {
				contextMenu.jqxMenu("disable", "viewComments", false);
			}
		});
	});
	var listContentType =
	[
		{productContentTypeId: "RELATED_ARTICLE", description: "${StringUtil.wrapString(uiLabelMap.BSContentAboutProduct)}"},
		{productContentTypeId: "INTRODUCTION", description: "${StringUtil.wrapString(uiLabelMap.BSProductIntroduction)}"}
	];
	var mapContentType = {
		RELATED_ARTICLE: "${StringUtil.wrapString(uiLabelMap.BSContentAboutProduct)}",
		INTRODUCTION: "${StringUtil.wrapString(uiLabelMap.BSProductIntroduction)}"
	}
</script>