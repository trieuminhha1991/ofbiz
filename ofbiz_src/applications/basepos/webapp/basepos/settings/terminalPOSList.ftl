<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js?v=1.0.0"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<#--
<script>
<#assign org = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.userLoginId) !>
<#assign facilityList = delegator.findByAnd("Facility", {"facilityTypeId" : "WAREHOUSE"}, null, false) />
var facilityListData = [<#list facilityList as fa>{
	facilityId: "${fa.facilityId}", description: "${StringUtil.wrapString(fa.facilityName)}"
},</#list>];
<#assign storeList = delegator.findByAnd("ProductStore", {"salesMethodChannelEnumId":"SMCHANNEL_POS", "payToPartyId" : org}, null, false) />
var productStoreData = [<#list storeList as store>{
	productStoreId: "${store.productStoreId}", description: "${StringUtil.wrapString(store.storeName)}"
	},</#list>];
</script>
-->

<#assign dataField = "[
			{ name: 'productStoreId', type: 'string' },
			{ name: 'facilityId', type: 'string' },
			{ name: 'ownerPartyId', type: 'string' },
			{ name: 'posTerminalId', type: 'string' },
			{ name: 'terminalName', type: 'string' }]"/>
		
<#assign columnlist = "
			{ text: '${StringUtil.wrapString(uiLabelMap.BPOSTerminalId)}', dataField: 'posTerminalId', width: 150 },
			{ text: '${StringUtil.wrapString(uiLabelMap.BPOSTerminalName)}', dataField: 'terminalName', width: 350 },
			{ text: '${StringUtil.wrapString(uiLabelMap.BPOSFacilityId)}', dataField: 'facilityId', width: 150 },
			{ text: '${StringUtil.wrapString(uiLabelMap.BSPSSalesChannelId)}', dataField: 'productStoreId', width: 200,
				cellsrenderer: function (row, colum, value) {
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					return \"<span><a href='showProductStore?productStoreId=\" + data.productStoreId + \"'>\" + data.productStoreId + \"</a></span>\";
				}
			}, 
			{ text: '${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}', dataField: 'ownerPartyId', minwidth: 200 }"/>

<#if hasOlbPermission("MODULE", "TERMINALPOS_NEW", "CREATE")>
	<#assign addrow = "true" />
<#else>
	<#assign addrow = "false" />
</#if>

<@jqGrid id="jqxgrid" addrow=addrow clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
	viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListProductStorePOS" mouseRightMenu="true" contextMenuId="contextMenu" 
	createUrl="jqxGeneralServicer?sname=createTerminalPOS&jqaction=C" addColumns="posTerminalId;terminalName;facilityId"
	/>
<style>
	.line-height{
		line-height: 25px;
	} 
</style>
<div id="contextMenu" style="display:none">
	<ul>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<#if hasOlbPermission("MODULE", "TERMINALPOS_EDIT", "")>
		<li><i class="fa-pencil open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSEdit)}</li>
		</#if>
		<#if hasOlbPermission("MODULE", "TERMINALPOS_DELETE", "")>
		<li><i class="fa-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li>
		</#if>
	</ul>
</div>

<#include "terminalPOSNewPopup.ftl">
<#include "terminalPOSEditPopup.ftl">

<script type="text/javascript">
	$(function(){
		OlbTerminalList.init();
	});
	var OlbTerminalList = (function(){
		var init = function(){
			initElement();
		};
		var initElement = function(){
			$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: "popup", theme: theme });
			$("#contextMenu").on("itemclick", function (event) {
				var args = event.args;
				var rowindex = $("#jqxgrid").jqxGrid("getselectedrowindex");
				var tmpKey = $.trim($(args).text());
				if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
					$("#jqxgrid").jqxGrid("updatebounddata");
				} else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSEdit)}") {
					var wtmp = window;
					var rowindex = $("#jqxgrid").jqxGrid("getselectedrowindex");
					var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
					var tmpwidth = $("#alterpopupWindowEdit").jqxWindow("width");
					dataEdit();
				} else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSDelete)}") {
					var rowindex = $("#jqxgrid").jqxGrid("getselectedrowindex");
					var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
					bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BPOSAreYouSureDeleteThisTerminal)}", [{
						"label"   : "${StringUtil.wrapString(uiLabelMap.CommonCancel)}",
						"icon"    : "fa fa-remove",
						"class"   : "btn  btn-danger form-action-button pull-right",
						"callback": function () {
							bootbox.hideAll();
						}
					}, {
						"label"   : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
						"icon"    : "fa-check",
						"class"   : "btn btn-primary form-action-button pull-right",
						"callback": function () {
							deleteTerminalPOS(data.posTerminalId);
						}
					}]);
				}
			});
		};
		
		function dataEdit() {
			var indexSeleted = $("#jqxgrid").jqxGrid("getselectedrowindex");
			var data = $("#jqxgrid").jqxGrid("getrowdata", indexSeleted);
			if (data != null) {
				OlbTerminalPOSEdit.setValue(data);
				$("#alterpopupWindowEdit").jqxWindow("open");
			}
		}
		
		function deleteTerminalPOS(posTerminalId) {
			var success = "${StringUtil.wrapString(uiLabelMap.BPOSDeleteSucess)}";
			jQuery.ajax({
				url: "deleteTerminalPOS",
				type: "POST",
				data: { "posTerminalId" : posTerminalId },
				success: function (res) {
					var message = "";
					var template = "";
					if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
						if (res._ERROR_MESSAGE_LIST_) {
							message += res._ERROR_MESSAGE_LIST_;
						}
						if (res._ERROR_MESSAGE_) {
							message += res._ERROR_MESSAGE_;
						}
						template = "error";
					}else{
						message = success;
						template = "success";
						$("#jqxgrid").jqxGrid("updatebounddata");
						$("#jqxgrid").jqxGrid("clearselection");
					}
					updateGridMessage("jqxgrid", template, message);
				}
			});
		}
		
		return {
			init: init
		};
	}());
</script>