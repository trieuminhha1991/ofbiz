<#--
<style type="text/css">
	#toolbar${jqxGridId} {
		height: 33px !important;
		visibility: visible !important;
		width:100% !important;
	}
	#content${jqxGridId} {
		top: 33px !important;
	}
</style>
-->

<#assign datafields = "[
				{name: 'salesForecastId', type: 'string'},
				{name: 'parentSalesForecastId', type: 'string'},
				{name: 'organizationPartyId', type: 'string'},
				{name: 'internalPartyId', type: 'string'},
				{name: 'customTimePeriodId', type: 'string'},
				{name: 'periodName', type: 'string'},
				{name: 'fromDate', type: 'date'},
				{name: 'thruDate', type: 'date'},
				
				{name: 'quotaAmount', type: 'string'},
				{name: 'forecastAmount', type: 'string'},
				{name: 'closedAmount', type: 'string'},
				{name: 'percentOfQuotaForecast', type: 'string'},
				{name: 'percentOfQuotaClosed', type: 'string'},
				{name: 'pipelineAmount', type: 'string'},
			]">
<#assign columns = "[
				{text: '${StringUtil.wrapString(uiLabelMap.BSSalesForecastId)}', datafield: 'salesForecastId', width: '14%',
					cellsrenderer: function(row, colum, value) {
						return '<span><a href=\"newSalesForecastDetailVer?salesForecastId=' + value + '\">' + value + '</a></span>';
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSSalesPeriodName)}', datafield: 'periodName'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', datafield: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype:'range', 
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatDate(value) + \"</span>\";
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', datafield: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype:'range', 
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatDate(value) + \"</span>\";
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}', datafield: 'organizationPartyId', width: '14%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSSalesCustomTimePeriodId)}', datafield: 'customTimePeriodId', width: '14%'},
			]">
<#--
{text: '${StringUtil.wrapString(uiLabelMap.BSInternalPartyId)}', datafield: 'internalPartyId', width: '16%'},
-->
<div id="container" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<#assign jqxGridId = "jqxSalesForecast"/>
<div id="${jqxGridId}"></div>
<#assign contextMenuItemId = "ctxmnus4c">
<div id='contextMenu_${contextMenuItemId}' style="display:none">
	<ul>
		<li id="${contextMenuItemId}_viewdetailnewtab"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
		<li id="${contextMenuItemId}_viewdetail"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#--<li id="${contextMenuItemId}_edit"><i class="fa fa-pencil"></i>${StringUtil.wrapString(uiLabelMap.BSEdit)}</li>
	    <li id="${contextMenuItemId}_expand"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpand)}</li>
	    <li id="${contextMenuItemId}_collapse"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapse)}</li>-->
	</ul>
</div>

<#include "salesForecastNewPopup.ftl"/>
<#include "salesForecastEditPopup.ftl"/>

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
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbPageS4CList.init();
	});
	var OlbPageS4CList = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.notification.create("#container", "#jqxNotification", null, {width: 'auto', autoClose: true});
       		jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuItemId}"));
		};
		var initElementComplex = function(){
			var configSalesForecast = {
				width: '100%',
				filterable: true,
				sortable: true,
				pageable: true,
				showfilterrow: true,
				datafields: ${datafields},
				columns: ${columns},
				useUrl: true,
				url: 'JQListSalesForecast&pagesize=0',
				useUtilFunc: true,
				showtoolbar: true,
                rendertoolbar: function(toolbar){
					<#--
					<#assign titleProperty1 = "BSSalesStatement${parameters.tid?if_exists}">
					<#assign titleProperty = "${uiLabelMap.BSList} ${uiLabelMap[titleProperty1]}">
					-->
					<#assign tmpCreate = "false"/>
					<#if hasOlbPermission("MODULE", "SALESFORECAST_NEW", "")>
						<#assign tmpCreate = "true"/>
					</#if>
					<#assign alternativeAddPopup="alterpopupWindow"/>
					<#--<#assign customcontrol1="fa-pencil@${uiLabelMap.wgeditonly}@javascript:OlbPageSalesForecastEdit.editSalesForecast();"/>-->
					<@renderToolbar id="${jqxGridId}" isShowTitleProperty="true" customTitleProperties="${titleProperty}" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="true" 
						addrow=tmpCreate addType="popup" alternativeAddPopup="${alternativeAddPopup}" 
						deleterow="false" deleteConditionFunction="" deleteConditionMessage="" 
						virtualmode="true" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1="" customcontrol2="" customcontrol3="" customtoolbaraction=""/>
                },
                contextMenu: "contextMenu_${contextMenuItemId}",
                showdefaultloadelement: true,
				autoshowloadelement: true,
			};
			new OlbGrid($("#${jqxGridId}"), null, configSalesForecast, []);
		};
		var initEvent = function(){
			<#--
			$('#jqxSalesForecast').on('bindingComplete', function(event){
				var heightTreeGrid = $("#jqxSalesForecast").css("height");
				var heightContainerTreeGrid = $("#contentjqxSalesForecast").css("height");
			});
			$("#jqxSalesForecast").on('contextmenu', function () {
	            return false;
	        });
	        $("#jqxSalesForecast").on('rowClick', function (event) {
	            var args = event.args;
	            if (args.originalEvent.button == 2) {
	                var scrollTop = $(window).scrollTop();
	                var scrollLeft = $(window).scrollLeft();
	                $("#contextMenu").jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	                return false;
	            }
	        });
			-->
			
	        $("#contextMenu_${contextMenuItemId}").on('itemclick', function (event) {
	            var args = event.args;
		        /*
		        var tmpKey = $.trim($(args).text());
		        var idGrid = "#jqxSalesForecast";
		        var rowData;
		        var id;
	        	var selection = $(idGrid).jqxTreeGrid('getSelection');
	        	if (selection.length > 0) rowData = selection[0];
	        	if (rowData) id = rowData.salesForecastId;
	        	*/
	        	var tmpId = $(args).attr('id');
		        var idGrid = "#${jqxGridId}";
				
		        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
		        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
	        	
	        	switch(tmpId) {
	        		case "${contextMenuItemId}_viewdetailnewtab": {
	        			if (rowData) {
							var salesForecastId = rowData.salesForecastId;
							var url = 'newSalesForecastDetailVer?salesForecastId=' + salesForecastId;
							var win = window.open(url, '_blank');
							win.focus();
						}
						break;
					};
	        		case "${contextMenuItemId}_viewdetail": { 
	        			if (rowData) {
							var salesForecastId = rowData.salesForecastId;
							var url = 'newSalesForecastDetailVer?salesForecastId=' + salesForecastId;
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
	        			OlbPageSalesForecastEdit.editSalesForecast();
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
		return {
			init: init,
		}
	}());
</script>