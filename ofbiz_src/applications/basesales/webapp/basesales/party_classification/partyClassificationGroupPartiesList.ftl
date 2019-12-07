<script type="text/javascript">
	//prepare data
	<#assign partyType = delegator.findList("PartyType", null, null, null, null, true) />
	var partyTypeData =  [
          <#list partyType as item>
          	{
          		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>
          		'partyTypeId' : "${item.partyTypeId?if_exists}",
          		'description' : "${description?if_exists}"
  			},
		  </#list>
	];
</script>

<div id="container" class="container-noti">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<#assign dataField = "[
			{name: 'partyClassificationGroupId', type: 'string'}, 
			{name: 'partyClassificationGroupName', type: 'string'}, 
			{name: 'partyId', type: 'string'}, 
			{name: 'partyName', type: 'string'}, 
			{name: 'fromDate', type: 'date', other: 'Timestamp'},
			{name: 'thruDate', type: 'date', other: 'Timestamp'},
		]"/>
		
<#assign columnlist = "
			{text: '${uiLabelMap.BSPartyClassificationGroupId}', dataField: 'partyClassificationGroupId', width: 170, editable: false}, 
			{text: '${uiLabelMap.BSPartyClassificationGroupName}', dataField: 'partyClassificationGroupName', width: 170, editable: false}, 
			{text: '${uiLabelMap.BSPartyId}', dataField: 'partyId', editable: false, width: 170}, 
			{text: '${uiLabelMap.BSPartyName}', dataField: 'partyName', editable: false}, 
			{text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: false,
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			}, 
			{text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', columntype: 'datetimeinput', 
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				},
				validation : function (cell,value) {
					var data = $('#jqxPartyClassificationGroupParties').jqxGrid('getrowdatabyid', cell.row);
					var fromDate = data.fromDate;
				    if (value){
		        		if(value.getTime() <= fromDate.getTime()){
		        			return {result:false , message: '${uiLabelMap.BSValidateDate}'};
		        		}
		        		return true;
				    }    
				    return true;              
				}
			},
		"/>
		
<#assign tmpCreateUrl = ""/>
<#assign tmpUpdateUrl = "false"/>
<#assign tmpDelete = "false"/>
<#if hasOlbPermission("MODULE", "SALES_PTYCLASSIFI_NEW", "")>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.wgaddnew}@javascript:OlbPagePCGPNew.openCreateNew();"/>
</#if>
<#if hasOlbPermission("MODULE", "SALES_PTYCLASSIFI_EDIT", "")>
	<#assign tmpUpdateUrl = "true"/>
</#if>
<#if hasOlbPermission("MODULE", "SALES_PTYCLASSIFI_DELETE", "")>
	<#assign tmpDelete = "true"/>
</#if>
<#assign tmpPurpose = ""/>
<#if partyClassificationGroup.partyClassificationTypeId == "KEY_CUSTOMER_CLASSIF">
	<#assign tmpPurpose = "icon-plus open-sans@${uiLabelMap.BSListPurpose}@javascript:OlbPartyClassifiPurposeAdd.openWindow();"/>
</#if>

<#assign contextMenuItemId = "ctxmnuptyclassptyl"/>
<@jqGrid id="jqxPartyClassificationGroupParties" url="jqxGeneralServicer?sname=JQListPartyClassificationGroupParties&partyClassificationGroupId=${parameters.partyClassificationGroupId?if_exists}" 
		columnlist=columnlist dataField=dataField editable="false" viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" clearfilteringbutton = "true"
		editable=tmpUpdateUrl updateUrl="jqxGeneralServicer?sname=updatePartyClassificationCore&jqaction=U" 
		editColumns="partyClassificationGroupId[${partyClassificationGroup.partyClassificationGroupId?if_exists}];partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		customcontrol1=tmpCreateUrl customcontrol2=tmpPurpose mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}"/>		

<div id='contextMenu_${contextMenuItemId}' style="display:none">
	<ul>
		<li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpDelete == "true"><li id="${contextMenuItemId}_delete"><i class="fa-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li></#if>
	</ul>
</div>
<#include "partyClassificationGroupPartiesNewPopup.ftl"/>
<#if partyClassificationGroup.partyClassificationTypeId == "KEY_CUSTOMER_CLASSIF">
	<#include "partyClassificationPurposeAddPopup.ftl"/>
</#if>

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

<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbPartyClassGroupPartyList.init();
	});
	var OlbPartyClassGroupPartyList = (function(){
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
				var idGrid = "#jqxPartyClassificationGroupParties";
				
		        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
		        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
		        
		        switch(tmpId) {
		    		case "${contextMenuItemId}_refesh": { 
		    			$(idGrid).jqxGrid('updatebounddata');
		    			break;
		    		};
		    		case "${contextMenuItemId}_delete": {
		    			var rowData = $(idGrid).jqxGrid('getrowdata', rowindex);
			        	var dataMap = {
					    		partyId: rowData.partyId,
					    		partyClassificationGroupId: rowData.partyClassificationGroupId,
					    		fromDate: rowData.fromDate.getTime(),
					    	};
					    jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToDelete)}", function(){
					    	$.ajax({
								type: 'POST',
								url: 'deletePartyClassificationAjax',
								data: dataMap,
								beforeSend: function(){
									$("#loader_page_common").show();
								},
								success: function(data){
									jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
									        	$('#container').empty();
									        	$('#jqxNotification').jqxNotification({ template: 'error'});
									        	$("#jqxNotification").html(errorMessage);
									        	$("#jqxNotification").jqxNotification("open");
									        	return false;
											}, function(){
												$('#container').empty();
									        	$('#jqxNotification').jqxNotification({ template: 'info'});
									        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgdeletesuccess)}");
									        	$("#jqxNotification").jqxNotification("open");
									        	
									        	$(idGrid).jqxGrid('updatebounddata');
											}
									);
								},
								error: function(data){
									alert("Send request is error");
								},
								complete: function(data){
									$("#loader_page_common").hide();
									$("#alterpopupWindow").jqxWindow('close');
								},
							});
					    });
		    			break;
		    		};
		    		default: break;
		    	}
			});
		};
		return {
			init:init,
		};
	}());
	
</script>