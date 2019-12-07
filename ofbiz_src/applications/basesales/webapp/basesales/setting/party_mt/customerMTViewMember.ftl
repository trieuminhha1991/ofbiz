<script>
var cellClass = function (row, columnfield, value) {
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("PARTY_DISABLED" == data.statusId) {
 				return "background-cancel-nd";
 			} else if ("PARTY_ENABLED" == data.status) {
 			}
 		}
    }
</script>
<#assign dataField = "[
				{name: 'partyId', type: 'string'}, 
				{name: 'partyCode', type: 'string'}, 
				{name: 'partyIdFrom', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'fromDate', type: 'date', other: 'Timestamp'},
				{name: 'thruDate', type: 'date', other: 'Timestamp'},
				{name: 'roleTypeIdFrom', type: 'string'},
				{name: 'roleTypeIdTo', type: 'string'},
			]"/>

<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSMemberId)}', dataField: 'partyCode', width: '16%', cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', dataField: 'fullName', cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', dataField: 'fromDate', width: '16%', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellClassName: cellClass,
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', dataField: 'thruDate', width: '16%', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellClassName: cellClass,
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSRoleTypeFrom)}', dataField: 'roleTypeIdFrom', width: '16%', cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSRoleTypeTo)}', dataField: 'roleTypeIdTo', width: '16%', cellClassName: cellClass},
			"/>

<#assign tmpCreateUrl = ""/>
<#if hasOlbPermission("MODULE", "SALES_CUSTOMERMT_NEW", "")>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.BSAddItem}@javascript:OlbCustomerMTViewMember.addItem();"/>
</#if>
<#assign tmpDelete = false/>
<#if hasOlbPermission("MODULE", "SALES_CUSTOMERMT_DELETE", "")>
	<#assign tmpDelete = true/>
</#if>

<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="false" columnlist=columnlist dataField=dataField
		url="jqxGeneralServicer?sname=JQListPartyMemeber&groupId=${parameters.groupId}"
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup" 
		customcontrol1=tmpCreateUrl mouseRightMenu="true" contextMenuId="contextMenu"/>

<div id="contextMenu" style="display:none">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpDelete><li><i class="fa fa-remove"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li></#if>
	</ul>
</div>

<#include "customerMTAddItemPopup.ftl"/>

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
		OlbCustomerMTViewMember.init();
	});
	var OlbCustomerMTViewMember = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu"));
		};
		var initEvent = function(){
			$("#contextMenu").on('itemclick', function (event) {
				var args = event.args;
		        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    	   	var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
	    	   	
		        var tmpKey = $.trim($(args).text());
		        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
		        	$("#jqxgrid").jqxGrid('updatebounddata');
		        }<#if tmpDelete> else if (tmpKey == '${StringUtil.wrapString(uiLabelMap.BSDelete)}') {
		        	var row = $("#jqxgrid").jqxGrid('getselectedrowindexes');
					var cMemberr = new Array();
					var data2 = $("#jqxgrid").jqxGrid('getrowdata', row);
					var map = {
						"partyIdFrom": data2.partyIdFrom,
						"partyIdTo": data2.partyId,
						"fromDate": data2.fromDate.getTime(),
						"roleTypeIdFrom": data2.roleTypeIdFrom,
						"roleTypeIdTo": data2.roleTypeIdTo,
					};
					if (!data2.thruDate) {
						map['thruDate'] = data2.fromDate.getTime();
					} else {
						map['thruDate'] = data2.thruDate.getTime();
					}
					cMemberr = map;
					
					if (cMemberr.length <= 0){
						jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}!");
						return false;
					} else {
						jOlbUtil.confirm.dialog("${uiLabelMap.BSAreYouSureYouWantToDelete}", 
							function(){
								cMemberr = JSON.stringify(cMemberr);
								$.ajax({
									type: 'POST',
									url: 'removePartyMember',
									data: {"cMemberr": cMemberr},
									beforeSend: function(){
										$("#loader_page_common").show();
									},
									success: function(data){
										jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
													$("#btnPrevWizard").removeClass("disabled");
													$("#btnNextWizard").removeClass("disabled");
													
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
										        	
										        	$("#jqxgrid").jqxGrid('updatebounddata');
										        	return true;
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
							}
						);
					}
		    	}</#if>
			});
		};
		var addItem = function(){
			$("#wn_additem_groupId").val("${partyGroup?if_exists.partyId?if_exists}");
	   		$("#wn_additem_groupCode").val("${partyGroup?if_exists.partyCode?if_exists}");
	   		$("#alterpopupWindowCustomerMTAddItem").jqxWindow("open");
		};
		return {
			init: init,
			addItem: addItem
		};
	}());
</script>