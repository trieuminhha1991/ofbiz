<script type="text/javascript">
	<#assign periodTypeList = Static["com.olbius.basesales.util.SalesUtil"].getListSalesPeriodType(delegator)!/>
	var periodTypeData = [
	<#if periodTypeList?exists>
		<#list periodTypeList as periodType>
		{	periodTypeId: "${periodType.periodTypeId}",
			description: "${StringUtil.wrapString(periodType.get("description", locale))}"
		},
		</#list>
	</#if>
	];
</script>

<style type="text/css">
	#statusbarjqxCustomTimePeriod {
		width: 0 !important;
	}
</style>

<div id="container" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<div id="jqxCustomTimePeriod"></div>

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
<#include "customTimePeriodQuickNewPopup.ftl"/>

<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSQuickCreateNew}@javascript:OlbCustomTimePeriodList.openQuickCreateNew();"/>

<@jqTreeGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbCustomTimePeriodList.init();
	});
	var OlbCustomTimePeriodList = (function(){
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
				key: 'customTimePeriodId',
				parentKeyId: 'parentPeriodId',
				localization: getLocalization(),
				datafields: [
					{name: 'customTimePeriodId', type: 'string'},
					{name: 'parentPeriodId', type: 'string'},
					{name: 'periodTypeId', type: 'string'},
					{name: 'periodName', type: 'string'},
					{name: 'fromDate', type: 'date'},
					{name: 'thruDate', type: 'date'},
					{name: 'isClosed', type: 'string'},
					{name: 'organizationPartyId', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSSalesCustomTimePeriodId)}', datafield: 'customTimePeriodId', width: '16%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSSalesPeriodName)}', datafield: 'periodName'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSPeriodType)}', datafield: 'periodTypeId', width: '16%',  
						cellsRenderer: function (row, column, value, rowData) {
							if (periodTypeData.length > 0) {
								for(var i = 0 ; i < periodTypeData.length; i++){
									if (value == periodTypeData[i].periodTypeId){
										return '<span title =\"' + periodTypeData[i].description +'\">' + periodTypeData[i].description + '</span>';
									}
								}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}
	   				},
					{text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', datafield: 'fromDate', width: '12%', cellsFormat : 'dd/MM/yyyy',
						cellsRenderer: function (row, column, value, rowData) {
							return "<span>" + jOlbUtil.dateTime.formatDate(value) + "</span>";
						},
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', datafield: 'thruDate', width: '12%', cellsFormat : 'dd/MM/yyyy',
						cellsRenderer: function (row, column, value, rowData) {
							return "<span>" + jOlbUtil.dateTime.formatDate(value) + "</span>";
						},
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSIsClosed)}', datafield: 'isClosed', width: '6%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}', datafield: 'organizationPartyId', width: '16%'},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQListCustomTimePeriodSales&pagesize=0',
				showtoolbar: true,
				rendertoolbarconfig: {
					titleProperty: "${StringUtil.wrapString(uiLabelMap[titleProperty])}",
					customcontrol1: "${StringUtil.wrapString(customcontrol1)}",
					expendButton: true,
				},
                contextMenu: "contextMenu_${contextMenuSsdvItemId}",
			};
			new OlbTreeGrid($("#jqxCustomTimePeriod"), null, configCustomTimePeriod, []);
		};
		var initEvent = function(){
	        $("#contextMenu_${contextMenuSsdvItemId}").on('itemclick', function (event) {
	            var args = event.args;
		        // var tmpKey = $.trim($(args).text());
		        var tmpId = $(args).attr('id');
		        var idGrid = "#jqxCustomTimePeriod";
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
		var openQuickCreateNew = function(){
			$("#alterpopupWindowQuick").jqxWindow('open');
		};
		return {
			init: init,
			openQuickCreateNew
		}
	}());
</script>