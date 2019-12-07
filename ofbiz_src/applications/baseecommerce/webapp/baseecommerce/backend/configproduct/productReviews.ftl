<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField="[{ name: 'productReviewId', type: 'string' },
					{ name: 'userLoginId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'productRating', type: 'number' },
					{ name: 'productReview', type: 'string' }]"/>

<#assign columnlist="{ text: '${uiLabelMap.BSProductReviewId}', datafield: 'productReviewId', width: 120 },
					 { text: '${uiLabelMap.BEUsername}', datafield: 'userLoginId', width: 200 },
					 { text: '${uiLabelMap.BSProductName}', datafield: 'productName', width: 250 },
					 { text: '${uiLabelMap.BSStatus}', datafield: 'statusId', filtertype: 'checkedlist', width: 120,
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
					 { text: '${uiLabelMap.BSProductRating}', datafield: 'productRating', width: 120, align: 'right', cellsalign: 'right', filtertype: 'number' },
					 { text: '${uiLabelMap.BSProductReview}', datafield: 'productReview' }"/>

<@jqGrid id="productReviews" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
	url="jqxGeneralServicer?sname=JQGetListProductReviews&productId=${parameters.productId?if_exists}"
	contextMenuId="contextMenu" mouseRightMenu="true" editable="false"
	updateUrl="jqxGeneralServicer?sname=updateProductReview&jqaction=U"
	editColumns="productReviewId;statusId"/>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="activate"><i class="fa-frown-o"></i>&nbsp;&nbsp;${uiLabelMap.DmsDeactivate}</li>
	</ul>
</div>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PRODUCT_REVIEW_STTS"), null, null, null, false) />

<script>
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item><#if item.statusId == "PRR_PENDING" || item.statusId == "PRR_APPROVED">{
		statusId: "${item.statusId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#if></#list></#if>];
	
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
		"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};

	$(document).ready(function() {
		var contextMenu = $("#contextMenu").jqxMenu({ theme: theme, width: 200, autoOpenPopup: false, mode: "popup"});
		contextMenu.on("itemclick", function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			var rowIndexSelected = $("#productReviews").jqxGrid("getSelectedRowindex");
			var rowData = $("#productReviews").jqxGrid("getrowdata", rowIndexSelected);
			if (rowData) {
				switch (itemId) {
				case "activate":
					var statusId = rowData.statusId;
					if (statusId == "PRR_APPROVED") {
						$("#productReviews").jqxGrid("setcellvalue", rowIndexSelected, "statusId", "PRR_PENDING");
					} else {
						$("#productReviews").jqxGrid("setcellvalue", rowIndexSelected, "statusId", "PRR_APPROVED");
					}
					break;
				default:
					break;
				}
			}
		});
		contextMenu.on("shown", function () {
			var rowIndexSelected = $("#productReviews").jqxGrid("getSelectedRowindex");
			var rowData = $("#productReviews").jqxGrid("getrowdata", rowIndexSelected);
			var statusId = rowData.statusId;
			if (statusId == "PRR_APPROVED") {
				$("#activate").html("<i class='fa-frown-o'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.DmsDeactivate)}");
			} else {
				$("#activate").html("<i class='fa-smile-o'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.DmsActive)}");
			}
		});
		$("#productReviews").jqxGrid({ enabletooltips: true });
	});
</script>