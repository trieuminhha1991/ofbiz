<#assign quotationStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "QUOTATION_STATUS"}, null, false)/>
<script type="text/javascript">
	var quotationStatusData = [
	<#if quotationStatuses?exists>
		<#list quotationStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	
	var cellClassjqxQuotation = function (row, columnfield, value) {
 		var data = $('#jqxgridQuotation').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			var now = new Date();
 			if ("QUOTATION_ACCEPTED" == data.statusId) {
 				if (data.fromDate <= now) {
 					if (data.thruDate == null || (data.thruDate >= now)) return "background-running";
 				} else {
 					if (data.thruDate == null || (data.thruDate >= now)) return "background-prepare";
 				}
 			} else if ("QUOTATION_CREATED" == data.statusId) {
 				if (data.thruDate == null || (data.thruDate >= now)) return "background-waiting";
 			}
 		}
    }
</script>

<#--
<#assign salesMethodChannelEnum = Static["com.olbius.basesales.util.SalesUtil"].getListSalesMethodChannelEnum(delegator)!/>
var salesMethodChannelEnumData = [
	<#if salesMethodChannelEnum?exists>
		<#list salesMethodChannelEnum as enumItem>
		{	enumId: '${enumItem.enumId}',
			description: '${StringUtil.wrapString(enumItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	
{name: 'salesMethodChannelEnumId', type: 'string'},
	
	filtertype: 'checkedlist', 
	cellsrenderer: function(row, column, value){
		if (salesMethodChannelEnumData.length > 0) {
			for(var i = 0 ; i < salesMethodChannelEnumData.length; i++){
				if (value == salesMethodChannelEnumData[i].enumId){
					return '<span title =\"' + salesMethodChannelEnumData[i].description +'\">' + salesMethodChannelEnumData[i].description + '</span>';
				}
			}
		}
		return '<span title=' + value +'>' + value + '</span>';
 	}, 
 	createfilterwidget: function (column, columnElement, widget) {
 		if (salesMethodChannelEnumData.length > 0) {
			var filterDataAdapter = new $.jqx.dataAdapter(salesMethodChannelEnumData, {
				autoBind: true
			});
			var records = filterDataAdapter.records;
			widget.jqxDropDownList({source: records, displayMember: 'enumId', valueMember: 'enumId',
				renderer: function(index, label, value){
					if (salesMethodChannelEnumData.length > 0) {
						for(var i = 0; i < salesMethodChannelEnumData.length; i++){
							if(salesMethodChannelEnumData[i].enumId == value){
								return '<span>' + salesMethodChannelEnumData[i].description + '</span>';
							}
						}
					}
					return value;
				}
			});
			widget.jqxDropDownList('checkAll');
		}
	}
 -->
<#assign dataField = "[
			{name: 'productQuotationId', type: 'string'}, 
			{name: 'quotationName', type: 'string'}, 
			{name: 'storeNames', type: 'string'}, 
			{name: 'currencyUomId', type: 'string'}, 
			{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
			{name: 'thruDate', type: 'date', other: 'Timestamp'}, 
			{name: 'statusId', type: 'string'},
			{name: 'createDate', type: 'date', other: 'Timestamp'}
		]"/>
<#assign columnlist = "
			{text: '${uiLabelMap.BSQuotationId}', dataField: 'productQuotationId', width: 140, cellClassName: cellClassjqxQuotation, 
    			cellsrenderer: function(row, colum, value) {
                	return \"<span><a href='viewQuotation?productQuotationId=\" + value + \"'>\" + value + \"</a></span>\";
                }
          	}, 
			{text: '${uiLabelMap.BSQuotationName}', dataField: 'quotationName', minwidth: 120, cellClassName: cellClassjqxQuotation},
			{text: '${uiLabelMap.BSPSSalesChannel}', dataField: 'storeNames', width: 140, cellClassName: cellClassjqxQuotation}, 
			{text: '${uiLabelMap.BSCurrencyUomId}', dataField: 'currencyUomId', width: 80, cellClassName: cellClassjqxQuotation}, 
			{text: '${uiLabelMap.BSCreateDate}', dataField: 'createDate', width: 110, cellsformat: 'dd/MM/yyyy', cellClassName: cellClassjqxQuotation, filtertype:'range'}, 
			{text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', width: 140, cellsformat: 'dd/MM/yyyy', cellClassName: cellClassjqxQuotation, filtertype:'range', 
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			}, 
			{text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', width: 140, cellsformat: 'dd/MM/yyyy', cellClassName: cellClassjqxQuotation, filtertype:'range', 
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			},
			{text: '${uiLabelMap.BSStatus}', dataField: 'statusId', width: 120, filtertype: 'checkedlist', cellClassName: cellClassjqxQuotation, 
				cellsrenderer: function(row, column, value){
					if (quotationStatusData.length > 0) {
						for(var i = 0 ; i < quotationStatusData.length; i++){
							if (value == quotationStatusData[i].statusId){
								return '<span title =\"' + quotationStatusData[i].description +'\">' + quotationStatusData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
			 	}, 
			 	createfilterwidget: function (column, columnElement, widget) {
			 		if (quotationStatusData.length > 0) {
						var filterDataAdapter = new $.jqx.dataAdapter(quotationStatusData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
							renderer: function(index, label, value){
								if (quotationStatusData.length > 0) {
									for(var i = 0; i < quotationStatusData.length; i++){
										if(quotationStatusData[i].statusId == value){
											return '<span>' + quotationStatusData[i].description + '</span>';
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

<#assign tmpCreateUrl = ""/>
<#assign tmpDelete = "false"/>
<#if hasOlbPermission("MODULE", "PRODQUOTATION_NEW", "")>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.wgaddnew}@newQuotation"/>
</#if>
<#--
<#if hasOlbPermission("MODULE", "PRODQUOTATION_DELETE", "")>
	<#assign tmpDelete = "true"/>
</#if>
-->
<#assign customcontrol2 = "fa fa-file-excel-o@@javascript: void(0);@exportExcelTrue()"/>
<#assign contextMenuItemId = "ctxmnuquotalst"/>
<@jqGrid id="jqxgridQuotation" url="jqxGeneralServicer?sname=JQListProductQuotation" columnlist=columnlist dataField=dataField
		editable="false" viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" 
		removeUrl="jqxGeneralServicer?sname=deleteProductQuotation&jqaction=C" deleteColumn="productQuotationId" deleterow=tmpDelete 
		customcontrol1=tmpCreateUrl mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" customcontrol2=customcontrol2 isSaveFormData="true" formData="filterObjData"/>

<div id='contextMenu_${contextMenuItemId}' style="display:none">
	<ul>
		<li id="${contextMenuItemId}_viewdetailnewtab"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
		<li id="${contextMenuItemId}_viewdetail"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpCreateUrl != ""><li id="${contextMenuItemId}_createnew"><i class="fa-plus-circle open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSCreateNew)}</li></#if>
	    <#if tmpDelete?exists && tmpDelete == "true"><li id="${contextMenuItemId}_delete"><i class="icon-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDeleteSelectedRow)}</li></#if>
	</ul>
</div>

<@jqOlbCoreLib />
<script type="text/javascript">
	var filterObjData = new Object();
	var exportExcelTrue = function(){
		var form = document.createElement("form");
	    form.setAttribute("method", "POST");
	    form.setAttribute("action", "exportListQuotationExcel");
	    //form.setAttribute("target", "_blank");
	    
	    if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
	    	$.each(filterObjData.data, function(key, value) {
	    		var hiddenField1 = document.createElement("input");
		        hiddenField1.setAttribute("type", "hidden");
		        hiddenField1.setAttribute("name", key);
		        hiddenField1.setAttribute("value", value);
		        form.appendChild(hiddenField1);
	    	});
	    }
        
	    document.body.appendChild(form);
	    form.submit();
	};
	
	$(function(){
		OlbQuotationList.init();
	});
	var OlbQuotationList = (function(){
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;
		var contextMenuItemId = "${contextMenuItemId}";
		
		var init = function(){
			initQuickMenu();
		};
		var initQuickMenu = function(){
			jOlbUtil.contextMenu.create($("#contextMenu_" + contextMenuItemId));
			
			$("#contextMenu_" + contextMenuItemId).on('itemclick', function (event) {
		        var args = event.args;
				// var tmpKey = $.trim($(args).text());
				var tmpId = $(args).attr('id');
				var idGrid = "#jqxgridQuotation";
				
		        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
		        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
		        
		        switch(tmpId) {
		    		case contextMenuItemId + "_viewdetailnewtab": {
		    			if (rowData) {
							var productQuotationId = rowData.productQuotationId;
							var url = 'viewQuotation?productQuotationId=' + productQuotationId;
							var win = window.open(url, '_blank');
							win.focus();
						}
						break;
					};
		    		case contextMenuItemId + "_viewdetail": { 
		    			if (rowData) {
							var productQuotationId = rowData.productQuotationId;
							var url = 'viewQuotation?productQuotationId=' + productQuotationId;
							var win = window.open(url, '_self');
							win.focus();
						}
						break;
					};
		    		case contextMenuItemId + "_refesh": { 
		    			$(idGrid).jqxGrid('updatebounddata');
		    			break;
		    		};
		    		case contextMenuItemId + "_createnew": {
			        	var win = window.open("newQuotation", "_self");
		        		window.focus();
		        		break;
		    		};
		    		case contextMenuItemId + "_delete": {
		    			$("#deleterowbuttonjqxgridQuotation").click();
		    			break;
		    		};
		    		default: break;
		    	}
			});
		};
		return {
			init:init
		};
	}());
</script>