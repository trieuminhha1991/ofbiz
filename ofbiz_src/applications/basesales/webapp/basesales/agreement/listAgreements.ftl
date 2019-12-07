<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#if !agreementUrl?exists>
	<#assign agreementUrl="jqxGeneralServicer?sname=JQGetListAgreementsOfPartner&partyIdFrom=${partyIdFrom?if_exists}&agreementId=${parameters.agreementId?if_exists}"/>
</#if>
<#assign dataField="[{ name: 'agreementId', type: 'string'},
				   { name: 'agreementCode', type: 'string'},
				   { name: 'partyIdFrom', type: 'string'},
				   { name: 'partyCodeFrom', type: 'string'},
				   { name: 'partyIdTo', type: 'string'},
				   { name: 'partyFromFullName', type: 'string'},
				   { name: 'agreementTypeId', type: 'string'},
				   { name: 'statusId', type: 'string'},
				   { name: 'description', type: 'string'},
				   { name: 'remainDays', type: 'number'},
				   { name: 'agrementValue', type: 'number'},
				   { name: 'paymentMethod', type: 'string'},
				   { name: 'paymentFrequen', type: 'string'},
				   { name: 'deliverDateFrequen', type: 'string'},
				   { name: 'grandTotalOrder', type: 'number'},
				   { name: 'grandTotalAgreement', type: 'number'},
				   { name: 'moneyRemain', type: 'number'},
				   { name: 'fromDate', type: 'date', other: 'Timestamp'},
				   { name: 'thruDate', type: 'date', other: 'Timestamp'}]"/>
<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', editable: false, pinned: true, groupable: false, sortable: false, filterable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsAgreementId)}', datafield: 'agreementCode', width: 120, editable: false,
						cellsrenderer: function (row, column, value) {
							var data = mainGrid.jqxGrid('getrowdata', row);
					        return '<div style=margin:4px;><a href=CreateAgreement?agreementId=' + data.agreementId + '>' + value + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyFrom)}', datafield: 'partyCodeFrom', align: 'left', width: 200, editable: false, sortable: false},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyFromFullName)}', datafield: 'partyFromFullName', align: 'left', width: 200, editable: false, sortable: false},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', align: 'left', filtertype: 'checkedlist', width: 150, editable: true,
						cellsrenderer: function(row, colum, value){
							value?value=mapStatusItem[value]:value;
					        return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapStatusItem[value];
				                }
	    		        	});
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsFromDate)}', datafield: 'fromDate', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsThruDate)}', datafield: 'thruDate', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsRemainDays)}', datafield: 'remainDays', width: 150, filtertype: 'number', sortable: false, filterable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsValue)}' , datafield: 'agrementValue', width: 150, filtertype: 'number', sortable: false, filterable: false,
						cellsrenderer: function(row, colum, value){
					        return '<span class=\"text-right\">' + value.toLocaleString(locale) + '</span>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsGrandTotalAgreement)}' , datafield: 'grandTotalAgreement', width: 150, filtertype: 'number', sortable: false, filterable: false,
						cellsrenderer: function(row, colum, value){
							return '<span class=\"text-right\">' + value.toLocaleString(locale) + '</span>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsGrandTotalOrder)}' , datafield: 'grandTotalOrder', width: 150, filtertype: 'number', sortable: false, filterable: false,
						cellsrenderer: function(row, colum, value){
					        return '<span class=\"text-right\">' + value.toLocaleString(locale) + '</span>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsMoneyRemain)}' , datafield: 'moneyRemain', width: 150, filtertype: 'number', sortable: false, filterable: false,
						cellsrenderer: function(row, colum, value){
							return '<span class=\"text-right\">' + value.toLocaleString(locale) + '</span>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPaymentFormality)}' , datafield: 'paymentMethod', width: 150, sortable: false, filterable: false},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPaymentMethod)}' , datafield: 'paymentFrequen', filtertype: 'checkedlist', width: 150, sortable: false, filterable: false,
						cellsrenderer: function(row, colum, value){
							value?value=mapPaymentMethod[value]:value;
					        return '<span >' + value + '</span>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listPaymentMethod, displayMember: 'description', valueMember: 'termDays' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapPaymentMethod[value];
				                }
	    		        	});
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsDeliveryTime)}' , datafield: 'deliverDateFrequen', width: 250, sortable: false, filterable: false}"/>

<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_NEW", "")>
	<#if partyId?exists && partyId != "">
		<#assign isShowTitleProperty="false"/>
	<#else>
		<#assign isShowTitleProperty="true"/>
	</#if>
</#if>
<#if !timeout?exists>
	<#assign timeout="0"/>
</#if>
<#if !customLoadFunction?exists>
	<#assign customLoadFunction="false"/>
</#if>
<#if !jqGridMinimumLibEnable?exists>
	<#assign jqGridMinimumLibEnable=""/>
</#if>

<@jqGrid id="listAgreementCustomer" filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" editmode="dblocalelick"
			showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
			customcontrol1="icon-plus open-sans@${uiLabelMap.CreateNewAgreement}@javascript: void(0);@quickCreateAgreement()" isShowTitleProperty=isShowTitleProperty autorowheight="true"
			url=agreementUrl timeout=timeout customLoadFunction=customLoadFunction jqGridMinimumLibEnable=jqGridMinimumLibEnable
			contextMenuId="contextMenu" mouseRightMenu="true" viewSize="10"
		/>

<#assign height=84 />
<div id='contextMenu' style="display:none;">
	<ul>
		<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_EDIT", "")>
		<#assign height=110 />
		<li id='approveAgreement'><i class="fa-check"></i>&nbsp;&nbsp;${uiLabelMap.DmsApprove}</li>
		</#if>
		<li id='createOrder'><i class="fa-cart-plus"></i>&nbsp;&nbsp;${uiLabelMap.BSCreateOrder}</li>
		<li id='saveFile'><i class="icon-camera"></i>&nbsp;&nbsp;${uiLabelMap.SaveFileScan}</li>
		<li id='viewFile'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewFileScan}</li>
	</ul>
</div>

<div id="jqxNotification">
	<div id="notificationContent">
	</div>
</div>
<#include "component://basesalesmtl/webapp/basesalesmtl/common/uploadFileScan.ftl"/>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false) />
<script>
	var mainGrid;
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	   },</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
			"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list></#if>};

	var mapPaymentMethod = {'COD': 'Sau khi nhận hàng', 0: '1 lần', 30: 'Theo tháng', 90: 'Theo quý'};
	var listPaymentMethod = [{termDays: 'COD', description: 'Sau khi nhận hàng'}, {termDays: 0, description: '1 lần'},
	                         {termDays: 30, description: 'Theo tháng'}, {termDays: 90, description: 'Theo quý'}];
	$(document).ready(function() {
		mainGrid = $("#listAgreementCustomer");
		$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		if(getCookie().checkContainValue("newContact")){
			deleteCookie("newContact");
			$('#jqxNotification').jqxNotification('closeLast');
			$("#jqxNotification").jqxNotification({ template: 'info'});
		    $("#notificationContent").text("${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}");
		    $("#jqxNotification").jqxNotification("open");
		 }
		if(getCookie().checkContainValue("updateContact")){
			deleteCookie("updateContact");
			$('#jqxNotification').jqxNotification('closeLast');
			$("#jqxNotification").jqxNotification({ template: 'info'});
			$("#notificationContent").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
			$("#jqxNotification").jqxNotification("open");
		}
		$("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: "${height}", autoOpenPopup: false, mode: 'popup'});
		mainGrid.on('contextmenu', function () {
		    return false;
		});
		$("#contextMenu").on('itemclick', function (event) {
	        var args = event.args;
	        var itemId = $(args).attr('id');
	        switch (itemId) {
			case "createOrder":
				var rowIndexSelected = mainGrid.jqxGrid('getSelectedRowindex');
				var rowData = mainGrid.jqxGrid('getrowdata', rowIndexSelected);
				window.open("newSalesOrder?partyId=" + rowData.partyIdFrom + "&agreementId=" + rowData.agreementId, '_blank');
				break;
			case "saveFile":
				var rowIndexSelected = mainGrid.jqxGrid('getSelectedRowindex');
				var rowData = mainGrid.jqxGrid('getrowdata', rowIndexSelected);
				Uploader.open(rowData.agreementId);
				break;
			case "viewFile":
				var rowIndexSelected = mainGrid.jqxGrid('getSelectedRowindex');
				var rowData = mainGrid.jqxGrid('getrowdata', rowIndexSelected);
				Viewer.open(rowData.agreementId);
				break;
			case "approveAgreement":
				var rowIndexSelected = mainGrid.jqxGrid('getSelectedRowindex');
				var rowData = mainGrid.jqxGrid('getrowdata', rowIndexSelected);
				var agreementId = rowData.agreementId;
				var result = DataAccess.execute({
						url: "approveAgreement",
						data: {agreementId: agreementId}
						});
				if (result) {
					mainGrid.jqxGrid('setcellvaluebyid', rowData.uid, "statusId", "AGREEMENT_APPROVED");
					mainGrid.jqxGrid('refreshdata');
				}
				break;
			default:
				break;
			}
		});
		$("#contextMenu").on('shown', function () {
			var rowIndexSelected = mainGrid.jqxGrid('getSelectedRowindex');
			var rowData = mainGrid.jqxGrid('getrowdata', rowIndexSelected);
			var statusId = rowData.statusId;
			if (statusId == "AGREEMENT_CREATED" || statusId == "AGREEMENT_MODIFIED") {
				$("#contextMenu").jqxMenu('disable', 'approveAgreement', false);
			}else {
				$("#contextMenu").jqxMenu('disable', 'approveAgreement', true);
			}
		});
	});
	
	function reloadAgeement() {
		mainGrid.jqxGrid('updatebounddata');
	}
	var quickCreateAgreement = function(){
		if(typeof(SCScreen) != "undefined" && SCScreen.getCurrentPartyId){
			var party = SCScreen.getCurrentPartyId();
			window.open('CreateAgreement?partyId=' + party,'_blank');
		} else {
			window.open('CreateAgreement','_blank');
		}
	};
</script>