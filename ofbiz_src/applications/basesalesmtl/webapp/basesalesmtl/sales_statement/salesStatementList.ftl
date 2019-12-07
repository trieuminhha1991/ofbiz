<#--
<style type="text/css">
	#toolbarjqxSalesStatementList {
		height: 33px !important;
		visibility: visible !important;
		width:100% !important;
	}
	#contentjqxSalesStatementList {
		top: 33px !important;
	}
</style>
-->

<script type="text/javascript">
	<#assign statementStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "SALES_SM_STATUS"}, null, false)/>
	var statementStatusData = [
	<#if statementStatuses?exists>
		<#list statementStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
</script>

<div id="container" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<#assign datafields = "[
				{name: 'salesStatementId', type: 'string'},
				{name: 'salesStatementTypeId', type: 'string'},
				{name: 'parentSalesStatementId', type: 'string'},
				{name: 'salesStatementName', type: 'string'},
				{name: 'organizationPartyId', type: 'string'},
				{name: 'internalPartyId', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'currencyUomId', type: 'string'},
				{name: 'salesForecastId', type: 'string'},
				{name: 'customTimePeriodId', type: 'string'},
				{name: 'periodName', type: 'string'},
				{name: 'fromDate', type: 'date'},
				{name: 'thruDate', type: 'date'},
			]">
<#assign columns = "[
				{text: '${StringUtil.wrapString(uiLabelMap.BSSalesStatementId)}', datafield: 'salesStatementId', width: '10%',
					cellsrenderer: function(row, colum, value) {
						return '<span><a href=\"viewSalesStatementDetail?salesStatementId=' + value + '\">' + value + '</a></span>';
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSName)}', datafield: 'salesStatementName'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}', datafield: 'organizationPartyId', width: '8%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', datafield: 'statusId', width: '10%', filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (statementStatusData.length > 0) {
							for(var i = 0 ; i < statementStatusData.length; i++){
    							if (value == statementStatusData[i].statusId){
    								return '<span title =\"' + statementStatusData[i].description +'\">' + statementStatusData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (statementStatusData.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(statementStatusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									if (statementStatusData.length > 0) {
										for(var i = 0; i < statementStatusData.length; i++){
											if(statementStatusData[i].statusId == value){
												return '<span>' + statementStatusData[i].description + '</span>';
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
				{text: '${StringUtil.wrapString(uiLabelMap.BSSalesCustomTimePeriodId)}', datafield: 'customTimePeriodId', width: '10%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSSalesPeriodName)}', datafield: 'periodName', width: '8%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', datafield: 'fromDate', width: '10%', cellsformat: 'dd/MM/yyyy', filtertype:'range'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', datafield: 'thruDate', width: '10%', cellsformat: 'dd/MM/yyyy', filtertype:'range'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSCurrencyUomId)}', datafield: 'currencyUomId', width: '6%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSSalesForecastId)}', datafield: 'salesForecastId', width: '10%'},
			]">

<div id="jqxSalesStatementList"></div>

<#--
{text: '${StringUtil.wrapString(uiLabelMap.BSInternalPartyId)}', datafield: 'internalPartyId', width: '12%'},
{text: '${StringUtil.wrapString(uiLabelMap.BSSalesStatementType)}', datafield: 'salesStatementTypeId', width: '8%'},
-->

<#assign contextMenuItemId = "ctxmnus4c">
<div id='contextMenu' style="display:none">
	<ul>
		<li id="${contextMenuItemId}_viewdetailnewtab"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
		<li id="${contextMenuItemId}_viewdetail"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#--<li id="${contextMenuItemId}_edit"><i class="fa fa-pencil"></i>${StringUtil.wrapString(uiLabelMap.BSEdit)}</li>
	    <li id="${contextMenuItemId}_expand"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpand)}</li>
	    <li id="${contextMenuItemId}_collapse"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapse)}</li>-->
	</ul>
</div>

<#--
<#include "salesForecastEditPopup.ftl"/>
-->

<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcolorpicker.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true/>

<#include "salesStatementNewPopup.ftl"/>

<script type="text/javascript">
	$(function(){
		OlbSalesStatement.init();
	});
	var OlbSalesStatement = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
            jOlbUtil.notification.create("#container", "#jqxNotification", null, {width: 'auto', autoClose: true});
       		jOlbUtil.contextMenu.create($("#contextMenu"));
		};
		var initElementComplex = function(){
			<#-- [old code] 
			var configSalesForecast = {
				width: '100%',
				height: 560,
				filterable: false,
				pageable: true,
				showfilterrow: false,
				key: 'salesStatementId',
				parentKeyId: 'parentSalesStatementId',
				localization: getLocalization(),
				datafields: ${datafields},
				columns: ${columns},
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQListSalesStatement&pagesize=0',
				useUtilFunc: false,
				showToolbar: true,
				rendertoolbar: function(toolbar){
					<@renderToolbar id="jqxSalesStatementList" isShowTitleProperty="true" customTitleProperties="" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
						addrow="true" addType="popup" alternativeAddPopup="alterpopupWindowSalesStatementNew" 
						deleterow="false" deleteConditionFunction="" deleteConditionMessage="" 
						virtualmode="true" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1=customcontrol1 customcontrol2="" customcontrol3="" customtoolbaraction=""/>
                },
			};
			new OlbTreeGrid($("#jqxSalesStatementList"), null, configSalesForecast, []);
			-->
			var configSalesForecast = {
				width: '100%',
				filterable: true,
				pageable: true,
				showfilterrow: true,
				datafields: ${datafields},
				columns: ${columns},
				useUrl: true,
				url: 'JQListSalesStatement&tid=${parameters.tid?if_exists}',
				useUtilFunc: true,
				showtoolbar: true,
				rendertoolbar: function(toolbar){
					<#assign titleProperty1 = "BSSalesStatement${parameters.tid?if_exists}">
					<#assign titleProperty = "${uiLabelMap.BSList} ${uiLabelMap[titleProperty1]}">
					<#assign customcontrol1="icon-pencil open-sans@${uiLabelMap.wgeditonly}@javascript:OlbSalesStatement.editSalesForecast();"/>
					<@renderToolbar id="jqxSalesStatementList" isShowTitleProperty="true" customTitleProperties="${titleProperty}" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
						addrow="true" addType="popup" alternativeAddPopup="alterpopupWindowSalesStatementNew" 
						deleterow="false" deleteConditionFunction="" deleteConditionMessage="" 
						virtualmode="true" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1="" customcontrol2="" customcontrol3="" customtoolbaraction=""/>
                },
                contextMenu: "contextMenu",
                showdefaultloadelement: true,
				autoshowloadelement: true,
				enabletooltips: true,
			};
			new OlbGrid($("#jqxSalesStatementList"), null, configSalesForecast, []);
		};
		var initEvent = function(){
			var heightTreeGrid = $("#jqxSalesStatementList").css("height");
			var heightContainerTreeGrid = $("#contentjqxSalesStatementList").css("height");
			if ("25px" == heightTreeGrid) {
				$("#jqxSalesStatementList").css("height", '100px');
				if ("26px" == heightContainerTreeGrid) {
					$("#contentjqxSalesStatementList").css("height", '101px');
				}
			}
			
	        $("#jqxSalesStatementList").on('contextmenu', function () {
	            return false;
	        });
	        $("#jqxSalesStatementList").on('rowClick', function (event) {
	            var args = event.args;
	            if (args.originalEvent.button == 2) {
	                var scrollTop = $(window).scrollTop();
	                var scrollLeft = $(window).scrollLeft();
	                $("#contextMenu").jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	                return false;
	            }
	        });
	        $("#contextMenu").on('itemclick', function (event) {
	            var args = event.args;
		        // var tmpKey = $.trim($(args).text());
		        var tmpId = $(args).attr('id');
		        var idGrid = "#jqxSalesStatementList";
				
		        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
		        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
		        
		        var id;
	        	//var selection = $(idGrid).jqxTreeGrid('getSelection');
	        	//if (selection.length > 0) rowData = selection[0];
	        	if (rowData) id = rowData.salesForecastId;
	        	switch(tmpId) {
	        		case "${contextMenuItemId}_viewdetailnewtab": {
	        			if (rowData) {
							var salesStatementId = rowData.salesStatementId;
							var url = 'viewSalesStatementDetail?salesStatementId=' + salesStatementId;
							var win = window.open(url, '_blank');
							win.focus();
						}
						break;
					};
	        		case "${contextMenuItemId}_viewdetail": { 
	        			if (rowData) {
							var salesStatementId = rowData.salesStatementId;
							var url = 'viewSalesStatementDetail?salesStatementId=' + salesStatementId;
							var win = window.open(url, '_self');
							win.focus();
						}
						break;
					};
	        		case "${contextMenuItemId}_refesh": { 
	        			$(idGrid).jqxGrid('updateBoundData');
	        			break;
	        		};
	        		case "${contextMenuItemId}_edit": { 
	        			editSalesForecast();
	        			break;
        			};
	        		case "${contextMenuItemId}_expand": { 
	        			if(id) $(idGrid).jqxTreeGrid('expandRow', id);
	        			break;
	        		};
	        		case "${contextMenuItemId}_collapse": { 
	        			if(id) $(idGrid).jqxTreeGrid('collapseRow', id);
	        			break;
        			};
	        		default: break;
	        	}
	        });
		};
		var editSalesForecast = function(){
			var idGrid = "#jqxSalesStatementList";
			/* [old code] 
				var selection = $(idGrid).jqxTreeGrid('getSelection');
				rowData = selection[0];
			*/
			var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
			var rowData;
			if (rowindex > -1) {
        		rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
			}
			if (!OlbCore.isNotEmpty(rowData)) {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}!");
				return false;
			}
        	if (rowData) {
        		$("#we_salesForecastId").jqxInput("val", rowData.salesForecastId);
        		
		    	parentSalesForecastIdDBB.selectItem($("#we_parentSalesForecastId"), $("#we_parentSalesForecastGrid"), {key: 'salesForecastId', gridType: 'jqxTreeGrid', description: ['organizationPartyId', 'internalPartyId']}, [rowData.parentSalesForecastId]);
		    	organizationPartyIdDBB.selectItem($("#we_organizationPartyId"), $("#we_organizationPartyGrid"), {key: 'partyId', description: ['groupName']}, [rowData.organizationPartyId]);
		    	internalPartyIdDBB.selectItem($("#we_internalPartyId"), $("#we_internalPartyGrid"), {key: 'partyId', description: ['fullName']}, [rowData.internalPartyId]);
		    	internalPartyIdDBB.selectItem($("#we_customTimePeriodId"), $("#we_customTimePeriodGrid"), {key: 'customTimePeriodId', gridType: 'jqxTreeGrid', description: ['periodName']}, [rowData.customTimePeriodId]);
        		
        		$("#we_quotaAmount").jqxNumberInput("val", rowData.quotaAmount);
        		$("#we_forecastAmount").jqxNumberInput("val", rowData.forecastAmount);
        		$("#we_closedAmount").jqxNumberInput("val", rowData.closedAmount);
        		$("#we_percentOfQuotaForecast").jqxNumberInput("val", rowData.percentOfQuotaForecast);
        		$("#we_percentOfQuotaClosed").jqxNumberInput("val", rowData.percentOfQuotaClosed);
        		$("#we_pipelineAmount").jqxNumberInput("val", rowData.pipelineAmount);
        		
        		$("#alterpopupWindowEdit").jqxWindow("open");
        	}
		};
		return {
			init: init,
			editSalesForecast: editSalesForecast
		}
	}());
</script>