<style>
	.line-height {line-height: 25px;}
</style>

<#assign salesMethodChannelData = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)!/>
<#assign defaultSalesChannelList = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "ORDER_SALES_CHANNEL"), null, false)!/>
<#assign partyTypeList = delegator.findList("PartyType", null , null, orderBy, null, false)!/>
<#assign geoTypeList = delegator.findList("GeoType", null , null, orderBy, null, false)!/>
<#assign currencyUomId = Static['com.olbius.basesales.util.SalesUtil'].getCurrentCurrencyUom(delegator)!/>
<#assign storeCreditAccountEnumList = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "STR_CRDT_ACT"), null, false)>
<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator)/>
<script type="text/javascript">
	var salesMethodChannelData = [
	<#if salesMethodChannelData?exists>
	    <#list salesMethodChannelData as enumerationL>
	    {   enumId: "${enumerationL.enumId}",
	    	description: "${StringUtil.wrapString(enumerationL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	var defaultSalesChannelData = [
	<#if defaultSalesChannelList?exists>
	    <#list defaultSalesChannelList as enumerationL>
	    {   enumId: "${enumerationL.enumId}",
	    	description: "${StringUtil.wrapString(enumerationL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	
	var partyTypeList = [
	<#if partyTypeList?exists>
	    <#list partyTypeList as partyTypeL>
	    {	partyTypeId: "${partyTypeL.partyTypeId}",
	    	description: "${StringUtil.wrapString(partyTypeL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	
	var geoTypeList = [
	<#if geoTypeList?exists>
	    <#list geoTypeList as geoTypeL>
	    {	geoTypeId: "${geoTypeL.geoTypeId}",
	    	description: "${StringUtil.wrapString(geoTypeL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	
	var storeCreditAccountEnumList = [
	<#if storeCreditAccountEnumList?exists>
	    <#list storeCreditAccountEnumList as storeCreditAccountEnumL>
	    {	enumId : "${storeCreditAccountEnumL.enumId}",
	    	description: "${StringUtil.wrapString(storeCreditAccountEnumL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	
	<#assign currentOrganizationPartyId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>
	<#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)!/>
	<#assign currencyUom = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, true)!/>
	<#assign reserveOrderEnum = delegator.findByAnd("Enumeration", {"enumTypeId" : "INV_RES_ORDER"}, null, true)!/>
	var currencyUomData = [
	<#if currencyUom?exists>
		<#list currencyUom as uomItem>
		{	uomId : "${uomItem.uomId}",
			descriptionSearch : "${StringUtil.wrapString(uomItem.get("description", locale))} [${uomItem.abbreviation}]",
		},
		</#list>
	</#if>
	];
	var reserveOrderEnumData = [
	<#if reserveOrderEnum?exists>
		<#list reserveOrderEnum as item>
		{	enumId : "${item.enumId}",
			description : "${StringUtil.wrapString(item.get("description", locale))}",
		},
		</#list>
	</#if>
	];
	
	var dataYesNoChoose = [
		{id : "N", description : "${StringUtil.wrapString(uiLabelMap.BSChNo)}"},
		{id : "Y", description : "${StringUtil.wrapString(uiLabelMap.BSChYes)}"}
	];

    <#assign facilityTypeDesc = delegator.findList("FacilityType", null, null, null, null, false)!/>
    var facilityTypeDescData = [
    <#if facilityTypeDesc?exists>
        <#list facilityTypeDesc as facilityType>
            {	facilityTypeId: '${facilityType.facilityTypeId}',
                description: '${StringUtil.wrapString(facilityType.get("description", locale))}'
            },
        </#list>
    </#if>
    ];
	
	<#assign listProdStoreStatus = delegator.findByAnd("StatusItem", {"statusTypeId": "PRODSTORE_STATUS"}, null, false)!/>
	var productStoreStatusData = [
	<#if listProdStoreStatus?exists>
		<#list listProdStoreStatus as item>
		{statusId: "${item.statusId?if_exists}", description: "${item.get("description", locale)?if_exists}"},
		</#list>
	</#if>
	];
	
	var cellClassNameStore = function (row, columnfield, value, data) {
		var statusId = data.statusId;
		if (statusId == 'PRODSTORE_DISABLED') {
			return 'background-cancel';
		}
	};
</script>

<#assign dataField = "[
				{name: 'productStoreId', type: 'string'}, 
				{name: 'primaryStoreGroupId', type: 'string'}, 
				{name: 'storeName', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'groupNameLocal', type: 'string'},
				{name: 'title', type: 'string'},
				{name: 'subtitle', type: 'string'},
				{name: 'inventoryFacilityId', type: 'string'},
				{name: 'payToPartyId', type: 'string'},
				{name: 'defaultCurrencyUomId', type: 'string'},
				{name: 'defaultSalesChannelEnumId', type: 'string'},
				{name: 'salesMethodChannelEnumId', type: 'string'},
				{name: 'storeCreditAccountEnumId', type: 'string'},
				{name: 'vatTaxAuthPartyId', type: 'string'},
				{name: 'vatTaxAuthGeoId', type: 'string'},
				{name: 'reserveOrderEnumId', type: 'string'},
				{name: 'showPricesWithVatTax', type: 'string'},
				{name: 'includeOtherCustomer', type: 'string'},
				{name: 'requireInventory', type: 'string'},
				{name: 'statusId', type: 'string'},
		]"/>
		
<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSPSChannelId)}', dataField: 'productStoreId', width: 120, cellClassName: cellClassNameStore, editable: false,
					cellsrenderer: function(row, colum, value) {
				    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
				    	return \"<span><a href='showProductStore?productStoreId=\" + data.productStoreId + \"'>\" + data.productStoreId + \"</a></span>\";
				    }
				}, 
				{text: '${StringUtil.wrapString(uiLabelMap.BSPSProductStoreGroupId)}', dataField: 'primaryStoreGroupId', width: 120, cellClassName: cellClassNameStore, editable: false,
					cellsrenderer: function(row, colum, value) {
				    	return \"<span><a href='viewProductStoreGroup?productStoreGroupId=\" + value + \"'>\" + value + \"</a></span>\";
				    }
				}, 
				{text: '${StringUtil.wrapString(uiLabelMap.BSPSChannelName)}', dataField: 'storeName', minwidth: 140, cellClassName: cellClassNameStore},
				{text: '${StringUtil.wrapString(uiLabelMap.BSPayToParty)}', dataField: 'payToPartyId', width: 100, cellClassName: cellClassNameStore},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', dataField: 'fullName', width: 160, cellClassName: cellClassNameStore},
				{text: '${StringUtil.wrapString(uiLabelMap.BSDefaultCurrencyUomId)}', dataField: 'defaultCurrencyUomId', width: 80, cellClassName: cellClassNameStore},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSStatusId)}', dataField: 'statusId', width: 120, cellClassName: cellClassNameStore, filtertype: 'checkedlist',
					cellsrenderer: function(row, column, value){
						if (productStoreStatusData.length > 0) {
							for(var i = 0 ; i < productStoreStatusData.length; i++){
								if (value == productStoreStatusData[i].statusId){
									return '<span title =\"' + productStoreStatusData[i].description +'\">' + productStoreStatusData[i].description + '</span>';
								}
							}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (productStoreStatusData.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(productStoreStatusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									if (productStoreStatusData.length > 0) {
										for(var i = 0; i < productStoreStatusData.length; i++){
											if(productStoreStatusData[i].statusId == value){
												return '<span>' + productStoreStatusData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
		   			}
				}
		"/>

<#assign tmpCreate = false/>
<#assign tmpUpdate = false/>
<#if hasOlbPermission("MODULE", "SALES_PRODUCTSTOREST_NEW", "")><#assign tmpCreate = true/></#if>
<#if hasOlbPermission("MODULE", "SALES_PRODUCTSTOREST_EDIT", "")><#assign tmpUpdate = true/></#if>

<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup" addrefresh="true" 
		url="jqxGeneralServicer?sname=getListProductStore" mouseRightMenu="true" contextMenuId="contextMenu" 
		addrow="${tmpCreate?string}" createUrl="jqxGeneralServicer?sname=createProductStoreChannel&jqaction=C" addColumns="productStoreId;storeName;payToPartyId;title;subtitle;defaultCurrencyUomId;defaultSalesChannelEnumId;salesMethodChannelEnumId;reserveOrderEnumId;storeCreditAccountEnumId;vatTaxAuthGeoId;vatTaxAuthPartyId;inventoryFacilityId;showPricesWithVatTax;includeOtherCustomer;requireInventory"
	/>

<#--
	{text: '${StringUtil.wrapString(uiLabelMap.BSInternalName)}', dataField: 'groupNameLocal', width: '20%'},
	{text: '${StringUtil.wrapString(uiLabelMap.BSTitle)}', dataField: 'title', width: '10%'},
	{text: '${StringUtil.wrapString(uiLabelMap.BSSubtitle)}', dataField: 'subtitle', width: '10%'},
	{text: '${StringUtil.wrapString(uiLabelMap.BSFacilityDelivery)}', dataField: 'inventoryFacilityId', width: '15%', hidden: true},
	
	{text: '${StringUtil.wrapString(uiLabelMap.BSSalesMethodChannelEnumId)}', dataField: 'salesMethodChannelEnumId', width: '16%', filtertype: 'checkedlist', 
						cellsrenderer: function(row, column, value){
							if (salesMethodChannelData.length > 0) {
								for(var i = 0 ; i < salesMethodChannelData.length; i++){
									if (value == salesMethodChannelData[i].enumId){
										return '<span title =\"' + salesMethodChannelData[i].description +'\">' + salesMethodChannelData[i].description + '</span>';
									}
								}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
					 		if (salesMethodChannelData.length > 0) {
								var filterDataAdapter = new $.jqx.dataAdapter(salesMethodChannelData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								widget.jqxDropDownList({source: records, displayMember: 'enumId', valueMember: 'enumId',
									renderer: function(index, label, value){
										if (salesMethodChannelData.length > 0) {
											for(var i = 0; i < salesMethodChannelData.length; i++){
												if(salesMethodChannelData[i].enumId == value){
													return '<span>' + salesMethodChannelData[i].description + '</span>';
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
-->

<div id="contextMenu" style="display:none">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpUpdate>
	    <li><i class="fa-pencil open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSEdit)}</li>
		<li id="mnuDisable"><i class="fa fa-times"></i>&nbsp;${uiLabelMap.BSDisable}</li>
		<li id="mnuEnable"><i class="fa fa-check"></i>&nbsp;${uiLabelMap.BSEnable}</li>
		</#if>
	</ul>
</div>
<#include "productStoreNewPopup.ftl" />
<#include "productStoreEditPopup.ftl" />

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbProducStoreList.init();
	});
	//turn on statusId of ProductStore By Huyendt
	var contextMenu = $("#contextMenu");
	var OlbProducStoreList = (function(){
		var contextMenuRowDetail = $("#contextMenu");
		var gridMain = $("#jqxgrid");
		
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create("#contextMenu");
		};
		var initEvent = function(){
			contextMenuRowDetail.on('shown', function (event) {
				var rowIndexSelected = gridMain.jqxGrid('getSelectedRowindex');
				var rowData = gridMain.jqxGrid('getrowdata', rowIndexSelected);
				
					if ("PRODSTORE_DISABLED" == rowData.statusId) {
						contextMenuRowDetail.jqxMenu('disable', 'mnuDisable', true);
						contextMenuRowDetail.jqxMenu('disable', 'mnuEnable', false);
					} else if("PRODSTORE_ENABLED" == rowData.statusId) {
						contextMenuRowDetail.jqxMenu('disable', 'mnuDisable', false);
						contextMenuRowDetail.jqxMenu('disable', 'mnuEnable', true);
					}else{
						contextMenuRowDetail.jqxMenu('disable', 'mnuDisable', true);
						contextMenuRowDetail.jqxMenu('disable', 'mnuEnable', true);
					}
			});
			
			$("#contextMenu").on('itemclick', function (event) {
				var args = event.args;
		        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		        var tmpKey = $.trim($(args).text());
		        var itemId = $(args).attr("id");
		        var gridRowDetail = contextMenu.data("grid");
		        var productStoreId = contextMenu.data("productStoreId");
		        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
		        	$("#jqxgrid").jqxGrid('updatebounddata');
		        }<#if tmpUpdate>else if (tmpKey == '${StringUtil.wrapString(uiLabelMap.BSEdit)}') {
		    		var wtmp = window;
		    	   	var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		    	   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
		    	   	//var tmpwidth = $('#alterpopupWindowEdit').jqxWindow('width');
		    	   	OlbProductStoreEdit.openWindow(data);
		    	} else if(itemId == "mnuDisable") {
		    		var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		    	   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
		    		changeProductStoreStatus(gridRowDetail, data.productStoreId, 'PRODSTORE_DISABLED');
		    	} else if(itemId == "mnuEnable") {
		    		var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		    	   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
		    		changeProductStoreStatus(gridRowDetail, data.productStoreId, 'PRODSTORE_ENABLED');
	    		}</#if>
			});
		};
		var changeProductStoreStatus = function(gridRowDetail, productStoreId, statusId){
			$.ajax({
				type: 'POST',
				url: 'changeProductStoreStatus',
				data: {
					'productStoreId': productStoreId,
					'statusId': statusId,
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
				        	if (data.productStoreId) {
				        		$("#jqxgrid").jqxGrid('updatebounddata');
				        	}
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
		};
		return {
			init: init
		};
	}());
</script>
