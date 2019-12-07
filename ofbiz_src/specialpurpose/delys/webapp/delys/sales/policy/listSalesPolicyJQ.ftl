<#assign policyStatusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "SALES_PL_STATUS"}, null, false) />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript">
	<#if policyStatusList?exists>
		var policyStatusData = [
		<#list policyStatusList as policyStatus>
		{
			statusId: "${policyStatus.statusId}",
			description: "${StringUtil.wrapString(policyStatus.get("description", locale))}",
		},
		</#list>
		];
	<#else>
		var policyStatusData = [];
	</#if>
	
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	}
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	}
	var myTimeoutVar;
	var objectMoveVar;
	function showMore(data, objectMove) {
		objectMoveVar = objectMove;
		$(objectMove).jqxTooltip('destroy');
		data = data.trim();
		var dataPart = data.replace("<p>", "");
		dataPart = dataPart.replace("</p>", "");
	    data = "<i onmouseenter='notDestroy()' onmouseleave='destroy()'>" + dataPart + "</i>";
	    $(objectMove).jqxTooltip({ content: data, position: 'right', autoHideDelay: 3000, closeOnClick: false, autoHide: false});
	    myTimeoutVar = setTimeout(function(){ 
			$(objectMove).jqxTooltip('destroy');
	    }, 2000);
	}
	function notDestroy() {
		clearTimeout(myTimeoutVar);
	}
	function destroy() {
		clearTimeout(myTimeoutVar);
		myTimeoutVar = setTimeout(function(){
			if (objectMoveVar != undefined && objectMoveVar != null) {
				$(objectMoveVar).jqxTooltip('destroy');
			}
		}, 2000);
	}
</script>

<#assign dataField="[{name: 'salesPolicyId', type: 'string'}, 
						{name: 'policyName', type: 'string'}, 
						{name: 'policyText', type: 'string'}, 
						{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
						{name: 'thruDate', type: 'date', other: 'Timestamp'}, 
						{name: 'statusId', type: 'string'},
						{name: 'createdDate', type: 'date', other: 'Timestamp'}
						]"/>
<#assign columnlist="{text: '${uiLabelMap.DASalesPolicyId}', dataField: 'salesPolicyId', editable:false, width: '16%', 
		        			cellsrenderer: function(row, colum, value) {
	                        	var data = $('#jqxgridPolicy').jqxGrid('getrowdata', row);
	                        	return \"<span><a href='/delys/control/editSalesPolicy?salesPolicyId=\" + data.salesPolicyId + \"'>\" + data.salesPolicyId + \"</a></span>\";
	                        }
	                  	}, 
						{text: '${uiLabelMap.DASalesPolicyName}', dataField: 'policyName', 
							 cellsrenderer: function(row, colum, value){
						        return '<span onmouseenter=\"showMore(' + \"'\" + value + \"'\" + ',this)\" >' + value + '</span>';
					        }
						},
						{text: '${uiLabelMap.DACreateDate}', dataField: 'createdDate', cellsformat: 'dd/MM/yyyy - HH:mm:ss', width: '14%'}, 
						{text: '${uiLabelMap.DAStatus}', dataField: 'statusId', filtertype: 'checkedlist', width: '14%', 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgridPolicy').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < policyStatusData.length; i++){
	    							if (value == policyStatusData[i].statusId){
	    								return '<span title = ' + policyStatusData[i].description +'>' + policyStatusData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(policyStatusData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'statusId',
									renderer: function(index, label, value){
										for(var i = 0; i < policyStatusData.length; i++){
											if(policyStatusData[i].statusId == value){
												return '<span>' + policyStatusData[i].description + '</span>';
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
				   			}
					 	}, 
						{text: '${uiLabelMap.DAFromDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '14%', 
						 	cellsrenderer: function(row, colum, value) {
	                        	var newDate = new Date(value);
	                        	return \"<span>\" + formatFullDate(value) + \"</span>\";
	                        }
					 	}, 
						{text: '${uiLabelMap.DAThroughDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '14%', 
						 	cellsrenderer: function(row, colum, value) {
	                        	var newDate = new Date(value);
	                        	return \"<span>\" + formatFullDate(value) + \"</span>\";
	                        }
					 	}
              		"/>
<#-- defaultSortColumn="productId" statusbarjqxgridSO -->
<#assign tmpCreateUrl = ""/>
<#assign tmpDelete = "false"/>
<#if security.hasEntityPermission("SALES_POLICY", "_CREATE", session)>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.DACreateNew}@editSalesPolicy"/>
</#if>
<#if security.hasEntityPermission("SALES_POLICY", "_DELETE", session)>
	<#assign tmpDelete = "true"/>
</#if>
<#--removeUrl="jqxGeneralServicer?sname=deleteProductQuotation&jqaction=C" deleteColumn="salesPolicyId" deleterow=tmpDelete 
menu
<#if tmpDelete?exists && tmpDelete == "true"><li><i class="icon-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.accDeleteSelectedRow)}</li></#if>

else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.accDeleteSelectedRow)}") {
	$("#deleterowbuttonjqxgridQuotation").click();
} 
-->
<@jqGrid id="jqxgridPolicy" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" defaultSortColumn="createdDate" sortdirection="desc" filtersimplemode="true" showstatusbar="false" 
		customcontrol1=tmpCreateUrl 
		url="jqxGeneralServicer?sname=JQGetListSalesPolicy" mouseRightMenu="true" contextMenuId="contextMenu"/>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}</li>
	    <#if tmpCreateUrl != ""><li><i class="fa-plus-circle open-sans"></i>${StringUtil.wrapString(uiLabelMap.DAAddNew)}</li></#if>
	</ul>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgridPolicy").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgridPolicy").jqxGrid('updatebounddata');
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetail)}") {
        	var data = $("#jqxgridPolicy").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var salesPolicyId = data.salesPolicyId;
				var url = 'editSalesPolicy?salesPolicyId=' + salesPolicyId;
				var win = window.open(url, '_self');
				win.focus();
			}
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}") {
        	var data = $("#jqxgridPolicy").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var salesPolicyId = data.salesPolicyId;
				var url = 'editSalesPolicy?salesPolicyId=' + salesPolicyId;
				var win = window.open(url, '_blank');
				win.focus();
			}
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAAddNew)}") {
        	var win = window.open("editSalesPolicy", "_self");
        	window.focus();
        }
	});
</script>