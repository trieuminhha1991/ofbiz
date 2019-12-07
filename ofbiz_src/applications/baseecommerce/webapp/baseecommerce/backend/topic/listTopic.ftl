<script type="text/javascript" src="/crmresources/js/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script src="/crmresources/js/DataAccess.js"></script>

<#assign dataField="[{ name: 'contentCategoryId', type: 'string' },
					 { name: 'categoryName', type: 'string'},
					 { name: 'contentCategoryTypeId', type: 'string'},
					 { name: 'description', type: 'string'}]"/>

<#assign columnlist="{ text: '${uiLabelMap.BSTopicId}', datafield: 'contentCategoryId', width: 350, editable: false},
					 { text: '${uiLabelMap.BSTopicName}', datafield: 'categoryName'}"/>


<#assign addrow="false"/>
<#if hasOlbEntityPermission("ENTITY_ECOMMERCE_TOPIC", "CREATE")>
	<#assign addrow="true"/>
</#if>
<#assign editable="false"/>
<#if hasOlbEntityPermission("ENTITY_ECOMMERCE_TOPIC", "UPDATE")>
	<#assign editable="true"/>
</#if>

<@jqGrid id="contentCategories" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
	url="jqxGeneralServicer?sname=JQGetListContentCategories&contentCategoryId=${parameters.contentCategoryId?if_exists}"
	contextMenuId="contextMenu" mouseRightMenu="true" editable=editable alternativeAddPopup="alterpopupWindow" addrow=addrow addType="popup"
	updateUrl="jqxGeneralServicer?sname=updateContentCategory&jqaction=U"
	createUrl="jqxGeneralServicer?sname=createContentCategory&jqaction=C"
	addColumns="contentCategoryId;categoryName;"
	editColumns="contentCategoryId;categoryName;contentCategoryTypeId"/>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id='viewContent'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSViewContent}</li>
		<li id='addContent'><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.BSAddContent}</li>
	</ul>
</div>

<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.BSAddTopic}</div>
	<div style="overflow-x: hidden;">
		
		<div class="row-fluid margin-top10">
			<div class="span12">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.BSTopicId}</label></div>
				<div class="span8"><input type="text" id="txtTopicId" /></div>
			</div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.BSTopicName}</label></div>
				<div class="span8"><input type="text" id="txtTopicName"/></div>
			</div>
		</div>
	
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id='btnCancel' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button id='btnSave' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<#assign contentCategories = delegator.findList("ContentCategory", null, null, null, null, false) />
<script>
var contentCategories = [<#list contentCategories as item>
	"${item.contentCategoryId?if_exists}".toLowerCase()
	,</#list>];
	$(document).ready(function() {
		$("#alterpopupWindow").jqxWindow({
			theme: 'olbius', width: 550, maxWidth: 2000, height: 210, maxHeight: 1000, resizable: false, isModal: true, autoOpen: false,
			cancelButton: $("#btnCancel"), modalOpacity: 0.7
		});
		$("#btnSave").click(function() {
			var row = {};
			row = {
				contentCategoryId : $('#txtTopicId').val(),
				contentCategoryTypeId : "NEWS_ARTICLE",
				categoryName : $('#txtTopicName').val()
			};
			$("#contentCategories").jqxGrid('addRow', null, row, "first");
			DataAccess.execute({
				url: "fixCategory",
				data: {}});
			$("#alterpopupWindow").jqxWindow('close');
		});
		$('#alterpopupWindow').jqxValidator({
		    rules: [{ input: '#txtTopicId', message: '${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}', action: 'keyup, blur', rule: 'required' },
		            { input: '#txtTopicName', message: '${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}', action: 'keyup, blur', rule: 'required' },
		            { input: '#txtTopicId', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = input.val();
							if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtTopicId', message: '${StringUtil.wrapString(uiLabelMap.TopicIdAlreadyExists)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = input.val().toLowerCase();
							if (_.indexOf(contentCategories, value) === -1) {
								return true;
							}
							return false;
						}
					}]
		});
		
		$('#alterpopupWindow').on('close', function () {
			$('#alterpopupWindow').jqxValidator('hide');
			$("#txtTopicId").val("");
			$("#txtTopicName").val("");
		});
		
		var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: 56, autoOpenPopup: false, mode: 'popup'});

		contextMenu.on('itemclick', function (event) {
	        var args = event.args;
	        var itemId = $(args).attr('id');
	        switch (itemId) {
			case "viewContent":
				var rowIndexSelected = $('#contentCategories').jqxGrid('getSelectedRowindex');
				var rowData = $('#contentCategories').jqxGrid('getrowdata', rowIndexSelected);
				window.location.href = "ListTopicContent?contentCategoryId=" + rowData.contentCategoryId;
				break;
			case "addContent":
				var rowIndexSelected = $('#contentCategories').jqxGrid('getSelectedRowindex');
				var rowData = $('#contentCategories').jqxGrid('getrowdata', rowIndexSelected);
				window.location.href = "ContentEditorEngine?contentCategoryId=" + rowData.contentCategoryId + "&type=TOPIC";
				break;
			default:
				break;
			}
		});
	});
	function hasWhiteSpace(s) {
		return /\s/g.test(s);
	}
</script>