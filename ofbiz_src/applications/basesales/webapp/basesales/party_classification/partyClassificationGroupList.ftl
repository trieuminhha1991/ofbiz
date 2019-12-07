<script type="text/javascript">
	//prepare data
	<#assign partyClassificationType = delegator.findByAnd("PartyClassificationType", {"parentTypeId" : "CUSTOMER_CLASSIFICAT"}, null, true)! />
	var partyClassificationTypeData =  [
	<#if partyClassificationType?exists>
      	<#list partyClassificationType as item>
      	{	'partyClassificationTypeId': "${item.partyClassificationTypeId}",
      		'description': "${StringUtil.wrapString(item.get("description", locale))}"
		},
		</#list>
	</#if>
	];
</script>

<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>

<#assign dataField = "[
			{name: 'partyClassificationGroupId', type: 'string'}, 
			{name: 'partyClassificationTypeId', type: 'string'}, 
			{name: 'parentGroupId', type: 'string'}, 
			{name: 'description', type: 'string'}, 
		]"/>
		
<#assign columnlist = "
			{text: '${uiLabelMap.BSPartyClassificationGroupId}', dataField: 'partyClassificationGroupId', width: 200,
    			cellsrenderer: function(row, colum, value) {
                	return \"<span><a href='listPartyClassificationGroupParties?partyClassificationGroupId=\" + value + \"'>\" + value + \"</a></span>\";
                }
          	}, 
			{text: '${uiLabelMap.BSPartyClassificationTypeId}', dataField: 'partyClassificationTypeId', filtertype: 'checkedlist', width: 400,
				cellsrenderer: function(row, column, value){
					if (partyClassificationTypeData.length > 0) {
						for(var i = 0 ; i < partyClassificationTypeData.length; i++){
							if (value == partyClassificationTypeData[i].partyClassificationTypeId){
								return '<span title =\"' + partyClassificationTypeData[i].description +'\">' + partyClassificationTypeData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
			 	}, 
			 	createfilterwidget: function (column, columnElement, widget) {
			 		if (partyClassificationTypeData.length > 0) {
						var filterDataAdapter = new $.jqx.dataAdapter(partyClassificationTypeData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'partyClassificationTypeId',
							renderer: function(index, label, value){
								if (partyClassificationTypeData.length > 0) {
									for(var i = 0; i < partyClassificationTypeData.length; i++){
										if(partyClassificationTypeData[i].partyClassificationTypeId == value){
											return '<span>' + partyClassificationTypeData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
					}
	   			}
			},
			{text: '${uiLabelMap.BSDescription}', dataField: 'description'},
		"/>
<#--{text: '${uiLabelMap.BSParentGroupId}', dataField: 'parentGroupId'},-->		
<#assign tmpCreateUrl = ""/>
<#assign tmpUpdateUrl = "false"/>
<#assign tmpDelete = "false"/>
<#if hasOlbPermission("MODULE", "SALES_PTYCLASSIFI_NEW", "")>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.wgaddnew}@javascript:OlbPartyClassifiGroupList.openCreateNew();"/>
</#if>
<#if hasOlbPermission("MODULE", "SALES_PTYCLASSIFI_EDIT", "")>
	<#assign tmpUpdateUrl = "true"/>
</#if>
<#if hasOlbPermission("MODULE", "SALES_PTYCLASSIFI_DELETE", "")>
	<#assign tmpDelete = "true"/>
</#if>
<#assign contextMenuItemId = "ctxmnupcgn">
<@jqGrid id="jqxPartyClassificationGroup" url="jqxGeneralServicer?sname=JQListPartyClassificationGroup" columnlist=columnlist dataField=dataField
		editable="false" viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" clearfilteringbutton = "true"
		customcontrol1=tmpCreateUrl mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}"/>		

<div id='contextMenu_${contextMenuItemId}' style="display:none">
	<ul>
		<li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpUpdateUrl == "true"><li id="${contextMenuItemId}_edit"><i class="fa icon-edit open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSUpdate)}</li></#if>
		<#if tmpDelete == "true"><li id="${contextMenuItemId}_delete"><i class="fa-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li></#if>
	</ul>
</div>

<#include "partyClassificationGroupNewPopup.ftl"/>
<@jqOlbCoreLib hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbPartyClassifiGroupList.init();
	});
	var OlbPartyClassifiGroupList = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuItemId}"));
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
		};
		var initEvent = function(){
			$("#contextMenu_${contextMenuItemId}").on('itemclick', function (event) {
				var args = event.args;
				// var tmpKey = $.trim($(args).text());
				var tmpId = $(args).attr('id');
				var idGrid = "#jqxPartyClassificationGroup";
				
		        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
		        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
		        
		        switch(tmpId) {
		        	case "${contextMenuItemId}_refesh": { 
		    			$(idGrid).jqxGrid('updatebounddata');
		    			break;
		    		};
		    		case "${contextMenuItemId}_edit": { 
		    			if (rowData) {
		    				openPopupUpdate()
				        	var data = $(idGrid).jqxGrid('getrowdata', rowindex);
				        	$('#partyClassificationGroupIdEdit').val(rowData.partyClassificationGroupId);
				        	$('#descriptionEdit').val(rowData.description);
				        	<#--
				        	if (data.parentGroupId){
				        		var dropDownContent = '<div class="innerDropdownContent">' + rowData.parentGroupId + '</div>';
				        	} else {
				        		var dropDownContent = null;
				        	}
				        	$("#parentGroupIdEdit").jqxDropDownButton('setContent', dropDownContent);
				        	-->
				        	$("#partyClassificationTypeIdEdit").jqxDropDownList('val', rowData.partyClassificationTypeId);
						}
						break;
					};
		    		case contextMenuItemId + "_delete": {
		    			if (rowData) {
		    				var param = "partyClassificationGroupId=" + rowData.partyClassificationGroupId;
		    				jOlbCore.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToDelete)}", function(){
		    					$.ajax({
									type: 'POST',
									url: 'deletePartyClassificationGroupAjax',
									data: param,
									beforeSend: function(){
										$("#loader_page_common").show();
									},
									success: function(data){
										jOlbUtil.processResultDataAjax(data, "default", "default");
									},
									error: function(data){
										alert("Send request is error");
									},
									complete: function(data){
										$("#loader_page_common").hide();
										$("#alterpopupWindow").jqxWindow('close');
										$("#jqxPartyClassificationGroup").jqxGrid('updatebounddata');
									},
								});
				            	$("#jqxPartyClassificationGroup").jqxGrid('updatebounddata');
		    				}, "${StringUtil.wrapString(uiLabelMap.wgcancel)}", "${StringUtil.wrapString(uiLabelMap.wgok)}");
		    			}
		    			break;
		    		};
		    		default: break;
		    	}
				
			});
			
			$('#alterpopupWindow').on('close', function (event) { 
				$('#alterpopupWindow').jqxValidator('hide');
			}); 
			$('#alterpopupWindow').on('open', function (event) { 
				//$("#parentGroupId").jqxDropDownButton('setContent', null);
		    	$('#description').val(null);
		    	$('#partyClassificationGroupId').val(null);
		    	$('#partyClassificationTypeId').jqxDropDownList('clearSelection'); 
			}); 
		};
		var openCreateNew = function(){
			$("#alterpopupWindow").jqxWindow('open');
		};
		var openPopupUpdate = function(){
			$("#alterpopupWindowEdit").jqxWindow('open');
		};
		return {
			init:init,
			openCreateNew
		};
	}());
	
</script>