<#assign dataField="[{ name: 'contentId', type: 'string' },
					 { name: 'contentCategoryId', type: 'string'},
					 { name: 'topicName', type: 'string'},
					 { name: 'contentName', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp'},
					 { name: 'statusId', type: 'string'},
					 { name: 'numberOfComments', type: 'number', other: 'Long'},
					 {name: 'isHot', type: 'bool'},
					 {name: 'isHotFromDate', type: 'number'}]"/>

<#assign columnlist="{ text: '${uiLabelMap.BSTopicName}', datafield: 'contentCategoryId', filtertype: 'checkedlist', editable: false, width: 250,
						cellsrenderer: function(row, colum, value){
							value?value=mapContentCategories[value]:value;
							return '<span>' + value + '</span>';
						 },createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: contentCategories, displayMember: 'contentCategoryId', valueMember: 'contentCategoryId',
				                renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
								    return mapContentCategories[value];
				                }
							});
						 }
					},
					{ text: '${uiLabelMap.BSContentId}', datafield: 'contentId', editable: false, width: 200},
					{ text: '${uiLabelMap.BSStatus}', datafield: 'statusId', filtertype: 'checkedlist', editable: false, width: 200,
						 cellsrenderer: function(row, colum, value){
								value?value=mapStatusItem[value]:value;
								return '<span>' + value + '</span>';
						 },createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'statusId', valueMember: 'statusId' ,
	                            renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
								    return mapStatusItem[value];
				                }
							});
						 }
					},
					{ text: '${uiLabelMap.BSContentName}', datafield: 'contentName', editable: false, minWidth: 200},
					{ text: '${uiLabelMap.BSNumberOfComments}', datafield: 'numberOfComments', align: 'right', cellsalign: 'right', filtertype: 'number', editable: false, width: 110},
					{text: '${uiLabelMap.BSIsHotContent}', dataField: 'isHot', columntype: 'checkbox', align: 'center', width: 60, filterable: false, sortable: false}"/>

 <#if parameters.contentTypeId?exists>
	<#assign customcontrol1 = "fa fa-plus-circle@${uiLabelMap.BSAddContent}@ContentEditorEngine?contentTypeId=${parameters.contentTypeId?if_exists}&type=TOPIC" />
 </#if>
<@jqGrid id="ListTopicContent" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
	url="jqxGeneralServicer?sname=JQGetListTopicContent&contentTypeId=${parameters.contentTypeId?if_exists}"
	customcontrol1=customcontrol1 contextMenuId="contextMenu" mouseRightMenu="true" editable="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=configContent"
	editColumns="contentId;statusId;contentCategoryId;isHot;isHotFromDate(java.lang.Long)"/>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id='viewComments'><i class="fa-comment-o"></i>&nbsp;&nbsp;${uiLabelMap.BSViewComments}</li>
		<li id='view'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSPreview}</li>
		<li id='editContent'><i class="fa-pencil-square-o"></i>&nbsp;&nbsp;${uiLabelMap.BSEditContent}</li>
		<li id='activate'><i class="fa-frown-o"></i>&nbsp;&nbsp;${uiLabelMap.DmsDeactivate}</li>
	</ul>
</div>

<#include "component://baseecommerce/webapp/baseecommerce/backend/content/listComment.ftl"/>
<#assign contentCategories = Static["com.olbius.baseecommerce.backend.TopicServices"].contentCategories(delegator, userLogin) />
<script>
var contentCategories = [<#if contentCategories?exists><#list contentCategories as item>{
	contentCategoryId: '${item.contentCategoryId?if_exists}',
	categoryName: '${StringUtil.wrapString(item.categoryName?if_exists)}'
},</#list></#if>];
var mapContentCategories = {<#if contentCategories?exists><#list contentCategories as item>
	'${item.contentCategoryId?if_exists}': '${StringUtil.wrapString(item.categoryName?if_exists)}',
</#list></#if>};
	$(document).ready(function() {
		var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 200, height: 110, autoOpenPopup: false, mode: 'popup'});
		contextMenu.on('itemclick', function (event) {
	        var args = event.args;
	        var itemId = $(args).attr('id');
	        switch (itemId) {
			case "viewComments":
				var rowIndexSelected = $('#ListTopicContent').jqxGrid('getSelectedRowindex');
			var rowData = $('#ListTopicContent').jqxGrid('getrowdata', rowIndexSelected);
			CommentTree.load(rowData.contentId);
				break;
			case "view":
				var rowIndexSelected = $('#ListTopicContent').jqxGrid('getSelectedRowindex');
				var rowData = $('#ListTopicContent').jqxGrid('getrowdata', rowIndexSelected);
				window.open("/baseecommerce/control/viewcontent?pid=" + rowData.contentId, '_blank');
				break;
			case "editContent":
				var rowIndexSelected = $('#ListTopicContent').jqxGrid('getSelectedRowindex');
				var rowData = $('#ListTopicContent').jqxGrid('getrowdata', rowIndexSelected);
				window.location.href = "ContentEditorEngine?contentId=" + rowData.contentId + "&type=TOPIC";
				break;
			case "activate":
				var rowIndexSelected = $('#ListTopicContent').jqxGrid('getSelectedRowindex');
				var rowData = $('#ListTopicContent').jqxGrid('getrowdata', rowIndexSelected);
				var contentId = rowData.contentId;
				var statusId = rowData.statusId;
			if (statusId == 'CTNT_PUBLISHED') {
				$("#ListTopicContent").jqxGrid('setcellvalue', rowIndexSelected, "statusId", "CTNT_DEACTIVATED");
				} else {
					$("#ListTopicContent").jqxGrid('setcellvalue', rowIndexSelected, "statusId", "CTNT_PUBLISHED");
				}
				break;
			default:
				break;
			}
		});
		contextMenu.on('shown', function () {
			var rowIndexSelected = $('#ListTopicContent').jqxGrid('getSelectedRowindex');
		var rowData = $('#ListTopicContent').jqxGrid('getrowdata', rowIndexSelected);
		var statusId = rowData.statusId;
		if (statusId == 'CTNT_PUBLISHED') {
			$("#activate").html("<i class='fa-frown-o'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.DmsDeactivate)}");
			} else {
				$("#activate").html("<i class='fa-smile-o'></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.DmsActive)}");
			}
		});
	});
</script>