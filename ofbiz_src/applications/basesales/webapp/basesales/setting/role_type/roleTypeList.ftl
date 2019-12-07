<style type="text/css">
	#statusbarjqxgridRoleType {
		width: 0 !important;
	}
</style>

<div id="container" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<div class="margin-top10">
	<div id="jqxgridRoleType"></div>
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

<#assign contextMenuSsdvItemId = "ctxmnuctpl">
<div id='contextMenu_${contextMenuSsdvItemId}' style="display:none">
	<ul>
	    <li id="${contextMenuSsdvItemId}_expand"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpand)}</li>
	    <li id="${contextMenuSsdvItemId}_collapse"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapse)}</li>
		<li id="${contextMenuSsdvItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<#--
	    <li id="${contextMenuSsdvItemId}_expandAll"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpandAll)}</li>
	    <li id="${contextMenuSsdvItemId}_collapseAll"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapseAll)}</li>
	    -->
	</ul>
</div>

<#assign addType = "popup"/>
<#assign alternativeAddPopup="alterpopupWindow"/>
<#include "roleTypeNewPopup.ftl"/>

<@jqTreeGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbRoleTypeList.init();
	});
	var OlbRoleTypeList = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuSsdvItemId}"));
            jOlbUtil.notification.create("#container", "#jqxNotification", null, {width: 'auto', autoClose: true});
		};
		var initElementComplex = function(){
			var configCustomTimePeriod = {
				width: '100%',
				filterable: false,
				pageable: true,
				showfilterrow: false,
				key: 'roleTypeId',
				parentKeyId: 'parentTypeId',
				localization: getLocalization(),
				datafields: [
					{name: 'roleTypeId', type: 'string'},
					{name: 'parentTypeId', type: 'string'},
					{name: 'description', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSRoleTypeId)}', datafield: 'roleTypeId', width: '16%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description'},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQGetListRoleType&pagesize=0&hrg=Y<#if roleTypeGroupId?exists>&roleTypeGroupId=${roleTypeGroupId}</#if>',
				showtoolbar: true,
				rendertoolbarconfig: {
					<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAddNew}@javascript:OlbRoleTypeNew.openQuickCreateNew();"/>
					<#assign customcontrol2 = "icon-trash open-sans@${uiLabelMap.BSDelete}@javascript:OlbRoleTypeList.deleteMember();"/>
					titleProperty: "<#if titleProperty?exists>${StringUtil.wrapString(uiLabelMap[titleProperty])}</#if>",
					customcontrol1: "${StringUtil.wrapString(customcontrol1)}",
					customcontrol2: "${StringUtil.wrapString(customcontrol2)}",
					expendButton: true,
				},
                contextMenu: "contextMenu_${contextMenuSsdvItemId}",
			};
			new OlbTreeGrid($("#jqxgridRoleType"), null, configCustomTimePeriod, []);
		};
		var initEvent = function(){
	        $("#contextMenu_${contextMenuSsdvItemId}").on('itemclick', function (event) {
	            var args = event.args;
		        // var tmpKey = $.trim($(args).text());
		        var tmpId = $(args).attr('id');
		        var idGrid = "#jqxgridRoleType";
		        var rowData;
		        var id;
	        	var selection = $(idGrid).jqxTreeGrid('getSelection');
	        	if (selection.length > 0) rowData = selection[0];
	        	if (rowData) id = rowData.uid;
	        	switch(tmpId) {
	        		case "${contextMenuSsdvItemId}_refesh": { 
	        			$(idGrid).jqxTreeGrid('updateBoundData');
	        			break;
	        		};
	        		case "${contextMenuSsdvItemId}_expandAll": { 
	        			$(idGrid).jqxTreeGrid('expandAll', true);
	        			break;
	        		};
	        		case "${contextMenuSsdvItemId}_collapseAll": { 
	        			$(idGrid).jqxTreeGrid('collapseAll', true);
	        			break;
        			};
	        		case "${contextMenuSsdvItemId}_expand": { 
	        			if(id) $(idGrid).jqxTreeGrid('expandRow', id);
	        			break;
	        		};
	        		case "${contextMenuSsdvItemId}_collapse": { 
	        			if(id) $(idGrid).jqxTreeGrid('collapseRow', id);
	        			break;
        			};
	        		default: break;
	        	}
	        });
		};
		var deleteMember = function(){
			var idGrid = "#jqxgridRoleType";
	        var rowData;
	        var id;
        	var selection = $(idGrid).jqxTreeGrid('getSelection');
        	if (selection.length > 0) rowData = selection[0];
        	if (rowData) id = rowData.uid;
			if (rowData) {
				jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}", function(){
					$.ajax({
						type: 'POST',
						url: "removeRoleTypeGroupMember",
						data: {
							'roleTypeId': rowData.roleTypeId,
							"roleTypeGroupId": "${roleTypeGroupId?default("")}"
						},
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
							        	$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html(errorMessage);
							        	$("#jqxNotification").jqxNotification("open");
							        	return false;
									}, function(){
										$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
							        	$("#jqxNotification").jqxNotification("open");
							        	
							        	$("#jqxgridRoleType").jqxTreeGrid('updateBoundData');
									}
							);
						},
						error: function(data){
							alert("Send request is error");
						},
						complete: function(data){
							$("#loader_page_common").hide();
						},
					});
				});
			} else {
				jOlbUtil.alert.error("${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseRow)}");
			}
		};
		return {
			init: init,
			deleteMember: deleteMember,
		}
	}());
</script>