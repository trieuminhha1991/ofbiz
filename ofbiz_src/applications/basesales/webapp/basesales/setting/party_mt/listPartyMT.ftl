<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtextarea.js"></script>

<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, true) />
<script>

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.BSAreYouSureActiveCustomeMT = "${StringUtil.wrapString(uiLabelMap.BSAreYouSureActiveCustomeMT)}";
	uiLabelMap.UpdateSuccess = "${StringUtil.wrapString(uiLabelMap.UpdateSuccess)}";
	uiLabelMap.BSAreYouSureDeactiveCustomeMT = "${StringUtil.wrapString(uiLabelMap.BSAreYouSureDeactiveCustomeMT)}";


var mapStatusItem = {<#if listStatus?exists><#list listStatus as item>
			"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list></#if>};
var statusData = [
	<#if listStatus?exists>
		<#list listStatus as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	
var cellClassCustomerMT = function (row, columnfield, value) {
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("PARTY_DISABLED" == data.statusId) {
 				return "background-cancel-nd";
 			} else if ("PARTY_ENABLED" == data.statusId) {
 				return "background-running";
 			} 
 		}
    }
</script>
<#assign dataField = "[
				{name: 'partyId', type: 'string'}, 
				{name: 'partyCode', type: 'string'},
				{name: 'groupName', type: 'string'},
				{name: 'description', type: 'string'},
				{name: 'statusId', type: 'string'},
			]"/>
<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSGroupId)}', dataField: 'partyCode', width: '20%', editable: false, cellClassName: cellClassCustomerMT,
					cellsrenderer: function(row, colum, value) {
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						return \"<span><a href='listPartyMemberMT?groupId=\" + data.partyId + \"'>\" + value + \"</a></span>\";
					}
				}, 
				{text: '${StringUtil.wrapString(uiLabelMap.BSGroupName)}', dataField: 'groupName', width: '30%',cellClassName: cellClassCustomerMT,},
				{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description',  width: '30%' , cellClassName: cellClassCustomerMT },
				{ text: '${uiLabelMap.BSStatus}', dataField: 'statusId', width: '20%',filtertype: 'checkedlist', cellClassName: cellClassCustomerMT, 
					 	cellsrenderer: function(row, colum, value){
							value?value=mapStatusItem[value]:value;
					        return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
					 		if (statusData.length > 0) {
								var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
									renderer: function(index, label, value){
										if (statusData.length > 0) {
											for(var i = 0; i < statusData.length; i++){
												if(statusData[i].statusId == value){
													return '<span>' + statusData[i].description + '</span>';
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
			"/>


<#assign tmpCreate = false/>
<#assign tmpUpdate = false/>
<#if hasOlbPermission("MODULE", "SALES_CUSTOMERMT_NEW", "")>
	<#assign tmpCreate = true/>
</#if>
<#if hasOlbPermission("MODULE", "SALES_CUSTOMERMT_DELETE", "")>
	<#assign tmpUpdate = true/>
</#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindowCustomerMTCreateNew" columnlist=columnlist dataField=dataField
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
		url="jqxGeneralServicer?sname=JQListCustomerMT" mouseRightMenu="true" contextMenuId="contextMenu" 
		editrefresh = "true"
		updateUrl="jqxGeneralServicer?sname=updateCustomerMT&jqaction=U" editColumns="partyId;partyCode;groupName;description"
		addrow="${tmpCreate?string}" createUrl="jqxGeneralServicer?sname=createCustomerMT&jqaction=C" addColumns="partyCode;groupName;description"/>

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

<div id="contextMenu" style="display:none">
	<ul>
		<li ><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
		<li><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li id="contextMenu"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpUpdate>	<li  id="contextMenu_edit" ><i class="fa fa-pencil-square-o" ></i>${StringUtil.wrapString(uiLabelMap.BSEditSelectedRow)}</li></#if>
	    <#if tmpUpdate>	<li  id="contextMenu_active" ><i class="fas fa-check"></i></i>${StringUtil.wrapString(uiLabelMap.BSActive)}</li></#if>
	    <#if tmpUpdate>	<li  id="contextMenu_deactive" ><i class="fas fa-times red"></i></i>${StringUtil.wrapString(uiLabelMap.BSDeactive)}</li></#if>
	</ul>
</div>

<#include "customerMTNewPopup.ftl"/>
<#include "customerMTEditPopup.ftl"/>

<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbCustomerGroupList.init();
	});
	var OlbCustomerGroupList = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu"));
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
		};
		var initEvent = function(){
			$("#contextMenu").on('shown', function (event) {
				var rowindex = $("#jqxgrid").jqxGrid('getSelectedRowindex');
	        	var dataRecord = $("#jqxgrid").jqxGrid('getRowData', rowindex);
	        	if ( dataRecord.statusId == 'PARTY_DISABLED') {
	        		$("#contextMenu").jqxMenu('disable', 'contextMenu_edit', true);
	        		$("#contextMenu").jqxMenu('disable', 'contextMenu_deactive', true);
	        		$("#contextMenu").jqxMenu('disable', 'contextMenu_active', false);
	        	}
	        	else {
	        		$("#contextMenu").jqxMenu('disable', 'contextMenu_edit', false);
	        		$("#contextMenu").jqxMenu('disable', 'contextMenu_deactive', false);
	        		$("#contextMenu").jqxMenu('disable', 'contextMenu_active', true);
	       		}
			});
		
		
			$("#contextMenu").on('itemclick', function (event) {
				var args = event.args;
		        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    	   	var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
	    	   	
		        var tmpKey = $.trim($(args).text());
		        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}") {
		    	   	if (rowData) {
						var partyId = rowData.partyId;
						var url = 'listPartyMemberMT?groupId=' + partyId;
						var win = window.open(url, '_blank');
						win.focus();
					}
		        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}") {
		        	if (rowData) {
						var partyId = rowData.partyId;
						var url = 'listPartyMemberMT?groupId=' + partyId;
						var win = window.open(url, '_self');
						win.focus();
					}
		        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
		        	$("#jqxgrid").jqxGrid('updatebounddata');
		        }
		    	<#if tmpUpdate> else if (tmpKey == '${StringUtil.wrapString(uiLabelMap.BSEditSelectedRow)}') {
		    	   	partyIdEditting = rowData.partyId;
		    	   	$('#wn_partyCodeEdit').val(rowData.partyCode);
					$('#wn_groupNameEdit').val(rowData.groupName);
					$('#wn_descriptionEdit').val(rowData.description);
					$("#alterpopupWindowCustomerMTEdit").jqxWindow("open");
		    	}
		    	else if (tmpKey == '${StringUtil.wrapString(uiLabelMap.BSActive)}') {
		    	   	changeStatusCustomerMT(rowData.partyId, 'PARTY_ENABLED')
		    	}
		    	else if (tmpKey == '${StringUtil.wrapString(uiLabelMap.BSDeactive)}') {
		    	   	changeStatusCustomerMT(rowData.partyId, 'PARTY_DISABLED')
		    	}</#if>
			});
		};
		var changeStatusCustomerMT = function( partyId, statusId){
			var notiText;
			if (statusId== 'PARTY_ENABLED')
				notiText = uiLabelMap.BSAreYouSureActiveCustomeMT;
			else 
				notiText = uiLabelMap.BSAreYouSureDeactiveCustomeMT;
			bootbox.dialog(notiText, 
						[{"label": uiLabelMap.CommonCancel, 
							"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				            "callback": function() {bootbox.hideAll();}
				        }, 
				        {"label": uiLabelMap.OK,
			            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			            "callback": function() {
							
					    	setTimeout(function(){
					    		Loading.show('loadingMacro');
					    		$.ajax({
					    			url : 'changeStatusCustomerMT',
					    			type : "POST",
					    			data : {
					    				partyId: partyId,
					    				statusId: statusId,
					    			},
					    			beforeSend: function(){
					    				
					    			},
					    			success : function(response) {
					    				$('#container').empty();
					                    $('#jqxgrid').jqxGrid('updatebounddata');
					    				$('#jqxNotification').jqxNotification({ template: 'success'});
						                $("#notificationContent").text(uiLabelMap.UpdateSuccess);
						                $("#jqxNotification").jqxNotification("open");
					    			},
					    			error: function(data){
					    				alert("Send request is error");
					    			},
					    			complete : function(jqXHR, textStatus) {
					    			}
					    		});
					            $("#jqxgrid").jqxGrid("clearSelection");
					            Loading.hide('loadingMacro');
					    	}, 500);
			            }
			        }]);
		}
		return {
			init: init
		};
	}());
</script>